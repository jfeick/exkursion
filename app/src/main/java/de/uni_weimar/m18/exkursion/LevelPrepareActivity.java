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

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import de.uni_weimar.m18.exkursion.data.level.LevelColumns;

public class LevelPrepareActivity extends FragmentActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        DownloadFragment.TaskCallbacks {

    private static final String LOG_TAG = LevelPrepareFragment.class.getSimpleName();

    private static final String TAG_DOWNLOAD_FRAGMENT = "download_fragment";
    private DownloadFragment mDownloadFragment;

    private static final int LEVEL_LOADER = 0;

    /*
    private static final String[] LEVEL_COLUMNS = {
            LevelContractOLD.LevelEntry.TABLE_NAME + "." + LevelContractOLD.LevelEntry._ID,
            LevelContractOLD.LevelEntry.COLUMN_PATH,
            LevelContractOLD.LevelEntry.COLUMN_TITLE,
            LevelContractOLD.LevelEntry.COLUMN_MD5SUM
    };

    private static final int COL_LEVEL_ID = 0;
    private static final int COL_LEVEL_PATH = 1;
    private static final int COL_LEVEL_TITLE = 2;
    private static final int COL_LEVEL_MD5SUM = 3;
    */
    private DownloadProgressFragment mDownloadProgressFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_prepare);
        if (savedInstanceState == null) {
            mDownloadProgressFragment = new DownloadProgressFragment();

        }
        //getLoaderManager().initLoader(LEVEL_LOADER, null, this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, mDownloadProgressFragment);
        ft.commit();

        getSupportLoaderManager().initLoader(LEVEL_LOADER, null, this);

        FragmentManager fm = getSupportFragmentManager();
        mDownloadFragment = (DownloadFragment) fm.findFragmentByTag(TAG_DOWNLOAD_FRAGMENT);

        if(mDownloadFragment == null) {
            mDownloadFragment = new DownloadFragment();
            fm.beginTransaction().add(mDownloadFragment, TAG_DOWNLOAD_FRAGMENT).commit();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_level, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getIntent();
        if (intent == null) {
            return null;
        }

        Log.v(LOG_TAG, "onCreateLoader received intent data: " + intent.getData());

        // create and return CursorLoader that will take care of creating a cursor for the
        // data we will need
        return new CursorLoader(
                this,
                intent.getData(),
                /* LEVEL_COLUMNS */ LevelColumns.ALL_COLUMNS,
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

        //String path = data.getString(COL_LEVEL_PATH);
        //String title = data.getString(COL_LEVEL_TITLE);
        //String md5sum = data.getString(COL_LEVEL_MD5SUM);
        String path = data.getString(data.getColumnIndex(LevelColumns.BASE_PATH));
        String title = data.getString(data.getColumnIndex(LevelColumns.TITLE));
        //String md5sum = data.getString(LevelColumns.);

        //String LevelInfo = String.format("Path: %s - Title: \"%s\" - MD5 hash: %s", path, title, md5sum);
        //Log.v(LOG_TAG, "got Loader info: " + LevelInfo);

        //TextView tv = (TextView)getView().findViewById(R.id.levelInfo_text);
        //tv.setText(mLevelInfo);

        // TODO:
        // check for md5 hash difference and only download resources we need
        //Toast.makeText(getActivity().getApplicationContext(),
        //        "Downloading Level Data", Toast.LENGTH_SHORT).show();
        //new DownloadResourcesTask().execute(path);
        mDownloadFragment.startDownload(path);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onProgressUpdate(int percent) {
        mDownloadProgressFragment.setProgress(percent);
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute() {
    }

}
