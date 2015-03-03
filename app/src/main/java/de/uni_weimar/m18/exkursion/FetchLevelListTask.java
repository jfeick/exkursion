
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

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.uni_weimar.m18.exkursion.data.LevelListJSON;
import de.uni_weimar.m18.exkursion.data.files.FilesColumns;
import de.uni_weimar.m18.exkursion.data.files.FilesContentValues;
import de.uni_weimar.m18.exkursion.data.level.LevelColumns;
import de.uni_weimar.m18.exkursion.data.level.LevelContentValues;
import de.uni_weimar.m18.exkursion.data.level.LevelCursor;
import de.uni_weimar.m18.exkursion.data.level.LevelSelection;

public class FetchLevelListTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchLevelListTask.class.getSimpleName();

    private final Context mContext;

    private String mBaseUrl;

    public FetchLevelListTask(Context context, String baseUrl) {
        mContext = context;
        mBaseUrl = baseUrl;
    }

    private boolean DEBUG = true;

    private void syncLevelData(LevelListJSON levelList) {
        LevelListJSON.LevelJSON[] levels = levelList.getLevels();

        // query every existing levels first and delete entries which are not in levelList
        {
            LevelSelection levelSelection = new LevelSelection();
            String[] projection = {LevelColumns._ID, LevelColumns.BASE_PATH};
            LevelCursor levelCursor = levelSelection.query(mContext.getContentResolver(), projection);

            ArrayList<String> remoteLevels = new ArrayList<String>();
            for(LevelListJSON.LevelJSON level : levels) {
                remoteLevels.add(level.base_path);
            }
            while (levelCursor.moveToNext()) { // iterate over all local levels in level table
                //Log.v(LOG_TAG, levelCursor.getBasePath());
                String base_path = levelCursor.getBasePath();
                if(!remoteLevels.contains(base_path)) {
                    // levelCursor points to a row which is not contained in remoteLevels
                    // so we want to delete this entry in our levels table.
                    // This deletion cascades down to the files tables
                    LevelSelection deleteSelection = new LevelSelection();
                    deleteSelection.id(levelCursor.getId());
                    int rowsDeleted = deleteSelection.delete(mContext.getContentResolver());
                    if(rowsDeleted > 0) {
                        Log.v(LOG_TAG, "Deleted " + Integer.toString(rowsDeleted) + " level rows (" + base_path + ")");
                    } else {
                        Log.e(LOG_TAG, "Error! Could not delete obsolete level entry!");
                    }
                }
            }
        }
        // update levels
        for(LevelListJSON.LevelJSON level : levels) {
            long levelId;
            { // update or insert into level table
                LevelContentValues levelData = new LevelContentValues();
                levelData.putBasePath(level.base_path);
                levelData.putTitle(level.title);
                levelData.putDescription(level.description);
                // try updating entry
                String selectionClause = LevelColumns.BASE_PATH + " = ?";
                String[] selectionArgs = {level.base_path};
                int rowsUpdated = mContext.getContentResolver().update(
                        LevelColumns.CONTENT_URI,
                        levelData.values(),
                        selectionClause,
                        selectionArgs
                );

                if (rowsUpdated == 0) { // entry not in table, so insert
                    Uri uri = levelData.insert(mContext.getContentResolver());
                    levelId = ContentUris.parseId(uri);
                } else { // query the levelId for our updated row
                    LevelSelection levelSelection = new LevelSelection();
                    levelSelection.basePath(level.base_path);
                    String[] projection = {LevelColumns._ID};
                    LevelCursor levelCursor = levelSelection.query(mContext.getContentResolver(), projection);
                    levelCursor.moveToFirst();
                    levelId = levelCursor.getId();
                }
            }
            // update or insert level.xml into files table
            FilesContentValues fileData = new FilesContentValues();
            fileData.putFilename("level.xml");
            fileData.putRemoteVersion(level.filemtime);
            fileData.putLevelId(levelId);
            String selectionClause = FilesColumns.FILENAME + " = ? " +
                    " AND " + FilesColumns.PATH + " = ? " +
                    " AND " + FilesColumns.LEVEL_ID + " = ? ";
            String[] selectionArgs = {"level.xml", ".", Long.toString(levelId)};
            int rowsUpdated = mContext.getContentResolver().update(
                    FilesColumns.CONTENT_URI,
                    fileData.values(),
                    selectionClause,
                    selectionArgs
            );
            if (rowsUpdated == 0) { // entry not in table, so insert
                Uri uri = fileData.insert(mContext.getContentResolver());
                ContentUris.parseId(uri);
            }
        }
    }

    private LevelListJSON getLevelListDataFromJson(String levelJsonStr) {
        Gson gson = new GsonBuilder().create();
        LevelListJSON levelList = gson.fromJson(levelJsonStr, LevelListJSON.class);
        return levelList;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String levelJsonStr = null;

        final String QUERY_LEVELS = "getlevels";
        final String SCRIPT = "game.php";
        try {
            Uri builtUri = Uri.parse(mBaseUrl).buildUpon()
                    .appendPath(SCRIPT)
                    .appendQueryParameter(QUERY_LEVELS, "")
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
            levelJsonStr = buffer.toString();
            Log.v(LOG_TAG, "JSON answer: " + levelJsonStr);
            LevelListJSON levelList = getLevelListDataFromJson(levelJsonStr);
            syncLevelData(levelList);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }
        finally {
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


        return null;
    }
}
