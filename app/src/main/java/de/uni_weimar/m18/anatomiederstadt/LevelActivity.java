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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import org.w3c.dom.NodeList;

import de.uni_weimar.m18.anatomiederstadt.element.ButtonFragment;
import de.uni_weimar.m18.anatomiederstadt.element.InputCheckFragment;
import de.uni_weimar.m18.anatomiederstadt.element.InputFragment;
import de.uni_weimar.m18.anatomiederstadt.element.LocationFragment;
import de.uni_weimar.m18.anatomiederstadt.element.QuizMulti;
import de.uni_weimar.m18.anatomiederstadt.element.Quiz4Buttons;
import de.uni_weimar.m18.anatomiederstadt.util.LevelStateManager;

public class LevelActivity extends FragmentActivity
        implements LevelPageFragment.OnFragmentInteractionListener,
                   Quiz4Buttons.OnFragmentInteractionListener,
                   LocationFragment.OnFragmentInteractionListener,
                   ButtonFragment.OnFragmentInteractionListener,
                   InputFragment.OnFragmentInteractionListener,
                   QuizMulti.OnFragmentInteractionListener,
                   InputCheckFragment.OnFragmentInteractionListener {

    private ProgressBar mProgressBar;

    private static final String LOG_TAG = LevelSelectActivity.class.getSimpleName();
    ViewPager viewPager = null;
    String mBasePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mProgressBar = (ProgressBar) findViewById(R.id.levelProgress);
        viewPager = (ViewPager) findViewById(R.id.pager);
        LevelStateManager stateManager =
                ((AnatomieDerStadtApplication)getApplicationContext()).getStateManager(this);
        mBasePath = stateManager.getBasePath();

        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //boolean resumeLevel = sharedPref.getBoolean(getString(R.string.resume_level_intent_bool), false);
        Intent intent = getIntent();

        boolean resumeLevel = intent.getBooleanExtra(getString(R.string.resume_level_intent_bool), false);
        if(resumeLevel) {
            try {
                String basePath = intent.getStringExtra(getString(R.string.resume_base_path));
                String pageId = intent.getStringExtra(getString(R.string.resume_page_id));
                mBasePath = basePath;
                viewPager.setAdapter(new LevelViewPagerAdapter(fragmentManager, mBasePath, pageId));
            }
            catch (Exception e) {
                // TODO c'mon, at least try to care
            }
        } else {
            try {
                NodeList pageList = stateManager.getLevelXML().getDocumentElement().getElementsByTagName("page");
                viewPager.setAdapter(new LevelViewPagerAdapter(fragmentManager, mBasePath, "start"));
            } catch (Exception e) {
                // TODO c'mon, at least try to care
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void switchToTarget(String pageId) {
        Log.v(LOG_TAG, "page switch triggered");
        LevelViewPagerAdapter adapter = (LevelViewPagerAdapter) viewPager.getAdapter();
        int id = adapter.getItemById(pageId);
        viewPager.setCurrentItem(id, true);
        incrementProgress();
    }

    @Override
    public void correctAnswerAction(String pageId) {
        //LevelViewPagerAdapter adapter = (LevelViewPagerAdapter) viewPager.getAdapter();
        //adapter.addPage(pageId);
        //switchToNextPage();
        switchToTarget(pageId);
    }


    private void incrementProgress() {
        LevelStateManager stateManager =
                ((AnatomieDerStadtApplication)getApplicationContext()).getStateManager(this);
        int numPages = stateManager.getLevelXML().getDocumentElement().getElementsByTagName("page").getLength();
        int step = 100 / numPages;
        mProgressBar.setProgress(mProgressBar.getProgress() + step);
    }

    @Override
    public void inProximityAction(String pageId) {
        switchToTarget(pageId);
    }

    @Override
    public void buttonAction(String pageId) {
        switchToTarget(pageId);
    }

    @Override
    public void onInputClick(String pageId) {
        switchToTarget(pageId);
    }

    @Override
    public void onMultiClick(String pageId) {
        switchToTarget(pageId);
    }

    @Override
    public void onInputCheckClick(String pageId) {
        switchToTarget(pageId);
    }

    @Override
    public void onBackPressed() {
        Log.v(LOG_TAG, "page switch triggered");
        LevelViewPagerAdapter adapter = (LevelViewPagerAdapter) viewPager.getAdapter();
        int current_id = viewPager.getCurrentItem();
        if(current_id > 0)
            viewPager.setCurrentItem(current_id - 1, true);
        else
            super.onBackPressed();


    }
}