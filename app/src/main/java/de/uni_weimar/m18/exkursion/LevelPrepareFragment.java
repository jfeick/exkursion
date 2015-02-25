package de.uni_weimar.m18.exkursion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import de.uni_weimar.m18.exkursion.data.LevelContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class LevelPrepareFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, DownloadFragment.TaskCallbacks {

    private static final String LOG_TAG = LevelPrepareFragment.class.getSimpleName();

    private static final String TAG_DOWNLOAD_FRAGMENT = "download_fragment";
    private DownloadFragment mDownloadFragment;


    private String mLevelInfo;

    private static final int LEVEL_LOADER = 0;

    private static final String[] LEVEL_COLUMNS = {
            LevelContract.LevelEntry.TABLE_NAME + "." + LevelContract.LevelEntry._ID,
            LevelContract.LevelEntry.COLUMN_PATH,
            LevelContract.LevelEntry.COLUMN_TITLE,
            LevelContract.LevelEntry.COLUMN_MD5SUM
    };

    private static final int COL_LEVEL_ID = 0;
    private static final int COL_LEVEL_PATH = 1;
    private static final int COL_LEVEL_TITLE = 2;
    private static final int COL_LEVEL_MD5SUM = 3;

    private ProgressDialog prgDialog;
    public static final int progress_bar_type = 0;

    public LevelPrepareFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        mDownloadFragment = (DownloadFragment) fm.findFragmentByTag(TAG_DOWNLOAD_FRAGMENT);

        if (mDownloadFragment == null) {
            mDownloadFragment = new DownloadFragment();
            fm.beginTransaction().add(mDownloadFragment, TAG_DOWNLOAD_FRAGMENT).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_level_prepare, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LEVEL_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        Log.v(LOG_TAG, "onCreateLoader received intent data: " + intent.getData());

        // create and return CursorLoader that will take care of creating a cursor for the
        // data we will need
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                LEVEL_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        Log.v(LOG_TAG, "cursor count: " + Integer.toString(data.getCount()));
        if(!data.moveToFirst()) {
            return;
        }

        String path = data.getString(COL_LEVEL_PATH);
        String title = data.getString(COL_LEVEL_TITLE);
        String md5sum = data.getString(COL_LEVEL_MD5SUM);

        mLevelInfo = String.format("Path: %s - Title: \"%s\" - MD5 hash: %s", path, title, md5sum);
        TextView tv = (TextView)getView().findViewById(R.id.levelInfo_text);
        tv.setText(mLevelInfo);

        // TODO:
        // check for md5 hash difference and only download resources we need
        Toast.makeText(getActivity().getApplicationContext(),
                "Downloading Level Data", Toast.LENGTH_SHORT).show();
        new DownloadResourcesTask().execute(path);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onProgressUpdate(int percent) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute() {

    }

    // Async Task Class
    class DownloadResourcesTask extends AsyncTask<String, String, String> {

        private String mPath;
        // Show Progress bar before downloading Music
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Shows Progress Bar Dialog and then call doInBackground method
            prgDialog = new ProgressDialog(getActivity());
            prgDialog.setMessage("Downloading...");
            prgDialog.setTitle("Wait");
            prgDialog.setIndeterminate(true);
            prgDialog.setCancelable(false);
            prgDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            prgDialog.show();
        }

        // Download Level File
        @Override
        protected String doInBackground(String... path) {
            int count;
            mPath = path[0];
            try {

                // TODO
                // move file system preparations (folder creation, ".nomedia" touches, etc)
                // to initialization activity
                // TODO
                // ask in IRC about this storage location and its _non-existing_ documentation
                URL url = new URL(getActivity().getString(R.string.GAME_BASE_URL) + "/" + path[0] + "/level.xml");
                URLConnection conection = url.openConnection();
                conection.connect();
                // Get Music file length
                int lenghtOfFile = conection.getContentLength();
                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(),10*1024);
                // Output stream to write file in SD card
                String root = Environment.getExternalStorageDirectory().toString();
                File appFolder = new File(root + "/Android/data/de.uni_weimar.m18.exkursion" + "/" + path[0]);
                appFolder.mkdirs();
                File file = new File(appFolder, "level.xml");
                if (file.exists()) {
                    file.delete();
                }
                OutputStream output = new FileOutputStream(file);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // Publish the progress which triggers onProgressUpdate method
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // Write data to file
                    output.write(data, 0, count);
                }
                // Flush output
                output.flush();
                // Close streams
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        // While Downloading Music File
        protected void onProgressUpdate(String... progress) {
            // Set progress percentage
            prgDialog.setProgress(Integer.parseInt(progress[0]));
        }

        // Once Music File is downloaded
        @Override
        protected void onPostExecute(String param) {
            // wait a bit till we dismiss
            try {
                Thread.sleep(2000);
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
            // Dismiss the dialog after the Music file was downloaded
            prgDialog.dismiss();
            Toast.makeText(getActivity().getApplicationContext(), "Download complete, starting level", Toast.LENGTH_SHORT).show();
            // Start Level
            Intent intent = new Intent(getActivity(), LevelActivity.class);
            Log.v(LOG_TAG, "Stuffing extra into intent: " + mPath);
            intent.putExtra("level_path", mPath);
            startActivity(intent);
        }
    }


}
