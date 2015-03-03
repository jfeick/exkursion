/*
 * Copyright 2015. J.F.Eick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package de.uni_weimar.m18.exkursion;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.uni_weimar.m18.exkursion.data.FileListJSON;
import de.uni_weimar.m18.exkursion.data.FileUtilities;
import de.uni_weimar.m18.exkursion.data.files.FilesColumns;
import de.uni_weimar.m18.exkursion.data.files.FilesContentValues;
import de.uni_weimar.m18.exkursion.data.files.FilesCursor;
import de.uni_weimar.m18.exkursion.data.files.FilesSelection;
import de.uni_weimar.m18.exkursion.data.level.LevelColumns;

class FetchLevelDataTask extends AsyncTask<Void, Integer, Void> {

    private static final String LOG_TAG = FetchLevelDataTask.class.getSimpleName();

    private DownloadFragment mDownloadFragment;
    private Cursor mData;
    private long mDownloadTotal = 0;
    private int mDownloadCount = 0;

    public FetchLevelDataTask(DownloadFragment downloadFragment, Cursor data) {
        //this.mContext = context;
        this.mDownloadFragment = downloadFragment;
        this.mData = data;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mDownloadFragment.mCallbacks != null) {
            mDownloadFragment.mCallbacks.onPreExecute();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        // fetch level file list (JSON)
        String fileListJSON = downloadFileListJSON(mData.getString(mData.getColumnIndex(LevelColumns.BASE_PATH)));
        // parse JSON
        FileListJSON fileData = getFileDataFromJson(fileListJSON);
        syncFiles(fileData);
        // when finished and we didnt need to download anything publish 100 percent
        publishProgress(100);
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... percent) {
        super.onProgressUpdate(percent);
        if (mDownloadFragment.mCallbacks != null) {
            mDownloadFragment.mCallbacks.onProgressUpdate(percent[0]);
        }
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        if (mDownloadFragment.mCallbacks != null) {
            mDownloadFragment.mCallbacks.onCancelled();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mDownloadFragment.mCallbacks != null) {
            mDownloadFragment.mCallbacks.onPostExecute();
        }
    }

    private void syncFiles(FileListJSON fileList) {
        ArrayList<FileListJSON.FileJSON> fileList_for_download = new ArrayList<>();
        FileListJSON.FileJSON[] files = fileList.getFiles();
        long levelId = mData.getLong(mData.getColumnIndex(LevelColumns._ID));
        String base_path = mData.getString(mData.getColumnIndex(LevelColumns.BASE_PATH));
        long downloadSize = 0;

        ContentResolver contentResolver = mDownloadFragment.getActivity().getContentResolver();

        // query existing files first and delete entries which are not in fileData
        {
            FilesSelection filesSelection = new FilesSelection();
            filesSelection.levelId(levelId);
            String[] projection = {FilesColumns._ID, FilesColumns.FILENAME, FilesColumns.PATH};
            FilesCursor filesCursor = filesSelection.query(contentResolver, projection);
            ArrayList<String> remoteFiles = new ArrayList<>();
            for (FileListJSON.FileJSON file : files) {
                remoteFiles.add(file.filepath + "/" + file.filename);
            }
            while (filesCursor.moveToNext()) {
                String local_filename = filesCursor.getPath() + "/" + filesCursor.getFilename();
                if (!remoteFiles.contains(local_filename)) {
                    // filesCursor points to a row which is not contained in the remote file list
                    // so we delete this entry.
                    FilesSelection deleteSelection = new FilesSelection();
                    deleteSelection.id(filesCursor.getId());
                    int rowsDeleted = deleteSelection.delete(contentResolver);
                    if (rowsDeleted > 0) {
                        // TODO delete leftover file in filesystem
                        Log.v(LOG_TAG, "Deleted " + Integer.toString(rowsDeleted) + " files rows ("
                                + local_filename + ")");
                    } else {
                        Log.e(LOG_TAG, "Error! Could not delete obsolote file entry!");
                    }
                }
            }
        }
        // update files
        for (FileListJSON.FileJSON file : files) {
            FilesContentValues fileData = new FilesContentValues();
            fileData.putFilename(file.filename);
            fileData.putPath(file.filepath);
            fileData.putRemoteVersion(file.filemtime);
            fileData.putLevelId(levelId);
            String selectionClause = FilesColumns.FILENAME + " = ? " +
                    " AND " + FilesColumns.PATH + " = ? " +
                    " AND " + FilesColumns.LEVEL_ID + " = ? ";
            String[] selectionArgs = {file.filename, file.filepath, Long.toString(levelId)};
            int rowsUpdated = contentResolver.update(
                    FilesColumns.CONTENT_URI,
                    fileData.values(),
                    selectionClause,
                    selectionArgs
            );
            if (rowsUpdated == 0) { // entry not in file table, so insert
                Uri uri = fileData.insert(contentResolver);
                ContentUris.parseId(uri);
            }
            FilesSelection filesSelection = new FilesSelection();
            filesSelection.levelId(levelId)
                    .and().path(file.filepath)
                    .and().filename(file.filename);
            String[] projection = {FilesColumns._ID, FilesColumns.LOCAL_VERSION};
            FilesCursor filesCursor = filesSelection.query(contentResolver);
            if (filesCursor.moveToFirst() // File is in table
                    &&
                    (filesCursor.getLocalVersion() < file.filemtime     // File is outdated
                            || !FileUtilities.fileExistsInStorage(mDownloadFragment.getActivity(),
                            base_path, file.filepath, file.filename))   // File does not exist in FS
                    ) {
                fileList_for_download.add(file);
                downloadSize += file.filesize;
            }

        }
        for (FileListJSON.FileJSON file : fileList_for_download) {
            downloadAndStoreFile(file, base_path, downloadSize, levelId);
        }
    }

    private void downloadAndStoreFile(FileListJSON.FileJSON fileJSON, String base_path,
                                      long downloadSize, long levelId) {

        if (downloadSize == 0) downloadSize = 1; // divide by zero check

        try {
            File targetFile = new File(fileJSON.filepath + "/" + fileJSON.filename);
            File rootDir = new File(mDownloadFragment.getActivity().getExternalFilesDir(null) + "/"
                    + base_path);
            File downloadFile = new File(rootDir, "/" + targetFile.toString());
            downloadFile = new File(downloadFile.getCanonicalPath());
            File downloadFolder = new File(downloadFile.getParent());
            // TODO do we need to handle .nomedia for getExternalFilesDir()?
            if (!downloadFolder.exists() || !downloadFolder.isDirectory()) {
                if (!downloadFolder.mkdirs()) {
                    Log.e(LOG_TAG, "Could not create folder " + downloadFolder.toString() + "!");
                    throw new IOException("Creation of download folder " + downloadFolder.toString()
                            + "failed");
                }
            }
            if (downloadFile.exists()) {
                if (!downloadFile.delete()) {
                    Log.e(LOG_TAG, "Could not delete file " + downloadFile.toString() + "!");
                    throw new IOException("Deletion of file " + downloadFile.toString() + "failed");
                }
            }
            OutputStream output = new FileOutputStream(downloadFile);


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            Uri builtUri = Uri.parse(mDownloadFragment.getString(R.string.GAME_BASE_URL))
                    .buildUpon()
                    .appendPath(base_path)
                    .appendPath(targetFile.toString())
                    .build();
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 10 * 1024);
            // Output stream to write file in SD card

            byte data[] = new byte[1024];
            while ((mDownloadCount = input.read(data)) != -1 && !isCancelled()) {
                mDownloadTotal += mDownloadCount;
                // Publish the progress which triggers onProgressUpdate method
                publishProgress((int) ((mDownloadTotal * 100) / downloadSize));
                // Write data to file
                output.write(data, 0, mDownloadCount);
            }
            // Flush output
            output.flush();
            // Close streams
            output.close();
            input.close();

            // TODO update local_version with remote_version
            updateLocalVersion(fileJSON, levelId);
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
    }

    private void updateLocalVersion(FileListJSON.FileJSON fileJSON, long levelId) {
        FilesContentValues fileData = new FilesContentValues();
        fileData.putLocalVersion(fileJSON.filemtime);
        String selectionClause = FilesColumns.FILENAME + " = ? " +
                " AND " + FilesColumns.PATH + " = ? " +
                " AND " + FilesColumns.LEVEL_ID + " = ? ";
        String[] selectionArgs = {fileJSON.filename, fileJSON.filepath, Long.toString(levelId)};
        int rowsUpdated = mDownloadFragment.getActivity().getContentResolver().update(
                FilesColumns.CONTENT_URI,
                fileData.values(),
                selectionClause,
                selectionArgs
        );
        if (rowsUpdated != 1) {
            Log.e(LOG_TAG, "Error! Could not update file column for local_version!");
        }
    }


    private FileListJSON getFileDataFromJson(String levelDataStr) {
        Gson gson = new GsonBuilder().create();
        FileListJSON fileData = gson.fromJson(levelDataStr, FileListJSON.class);
        return fileData;
    }

    private String downloadFileListJSON(String base_path) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String fileListStr = null;

        final String BASE_URL = mDownloadFragment.getString(R.string.GAME_BASE_URL);
        final String QUERY_FILELIST = "getfiles";
        final String PARAM_FILELIST = base_path;
        final String SCRIPT = "game.php";
        try {
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(SCRIPT)
                    .appendQueryParameter(QUERY_FILELIST, PARAM_FILELIST)
                    .build();
            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            fileListStr = buffer.toString();
            Log.v(LOG_TAG, "JSON answer: " + fileListStr);
            //LevelListJSON levelList = getLevelDataFromJson(levelJsonStr);
            //syncLevelData(levelList);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return fileListStr;
    }
}
