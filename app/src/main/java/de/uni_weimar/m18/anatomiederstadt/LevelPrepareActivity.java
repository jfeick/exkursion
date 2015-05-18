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

package de.uni_weimar.m18.anatomiederstadt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
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

import com.afollestad.materialdialogs.MaterialDialog;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.uni_weimar.m18.anatomiederstadt.data.level.LevelColumns;
import de.uni_weimar.m18.anatomiederstadt.util.LevelStateManager;
import de.uni_weimar.m18.anatomiederstadt.util.ParseUtilities;

public class LevelPrepareActivity extends FragmentActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        DownloadFragment.TaskCallbacks {

    private static final String LOG_TAG = LevelPrepareActivity.class.getSimpleName();

    private static final String TAG_DOWNLOAD_FRAGMENT = "download_fragment";
    private DownloadFragment mDownloadFragment;

    private static final int LEVEL_LOADER = 0;

    Cursor mCursor;

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

        Intent intent = getIntent();
        boolean resumeLevel = intent.getBooleanExtra(getString(R.string.resume_level_intent_bool), false);
        String resumeBasePath = intent.getStringExtra(getString(R.string.resume_base_path));
        if(resumeLevel) {
            resumeLevel(resumeBasePath, true);
        } else {
            getSupportLoaderManager().initLoader(LEVEL_LOADER, null, this);
        }

        FragmentManager fm = getSupportFragmentManager();
        mDownloadFragment = (DownloadFragment) fm.findFragmentByTag(TAG_DOWNLOAD_FRAGMENT);

        if(mDownloadFragment == null) {
            mDownloadFragment = new DownloadFragment();
            fm.beginTransaction().add(mDownloadFragment, TAG_DOWNLOAD_FRAGMENT).commit();
        }
    }

    @Override
    protected void onDestroy() {
        mCursor.close();
        super.onDestroy();
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

        mCursor = data;

        if(!data.moveToFirst()) {

            // TODO error handling if cursor doesn't match any data row
            return;
        }


        //mBasePath = data.getString(data.getColumnIndex(LevelColumns.BASE_PATH));


        // we finished loading the cursor (referencing a level row)
        // start our download
        mDownloadFragment.startFetchLevelDataTask(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //mCursor = null;
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

        // TODO Move to another AsyncTask?
        // The parsing of the level.xml file runs on the UI thread now (because this is the
        // onPostExecute callback

        String basePath = mCursor.getString(mCursor.getColumnIndex(LevelColumns.BASE_PATH));
        resumeLevel(basePath, false);
    }

    private void resumeLevel(String basePath, boolean resume) {
        Log.v(LOG_TAG, "resuming Level");

        try {
            ParseUtilities.parseLevelXML(basePath, this);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error - IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (SAXException e) {
            Log.e(LOG_TAG, "Error - SAXException: " + e.getMessage());
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            Log.e(LOG_TAG, "Error - ParserConfigurationException: " + e.getMessage());
            e.printStackTrace();
        }

        if(!resume)
            mCursor.close(); // when we are done, close the cursor (Important!)

        LevelStateManager stateManager =
                ((AnatomieDerStadtApplication)getApplicationContext()).getStateManager(this);
        if (stateManager.getLevelXML() != null) {
            Intent intent = new Intent(this, LevelActivity.class);
            if(resume) {
                intent.putExtra(getString(R.string.resume_level_intent_bool), true);
                intent.putExtra(getString(R.string.resume_base_path), basePath);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String pageId = sharedPref.getString(getString(R.string.resume_page_id), "start");
                intent.putExtra(getString(R.string.resume_page_id), pageId);
            }
            //intent.putExtra("level_path", "level0test");
            startActivity(intent);
        } else {
            // TODO show error message? do something!
        }

    }



}
