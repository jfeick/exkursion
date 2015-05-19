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
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baasbox.android.BaasUser;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import de.uni_weimar.m18.anatomiederstadt.user.LoginActivity;


public class LevelSelectActivity extends AppCompatActivity {

    private static final String LOG_TAG = LevelSelectActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BaasUser.current() == null) {
            startLoginScreen();
            return;
        }

        setContentView(R.layout.activity_level_select);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LevelListFragment())
                    .commit();
        }


    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {

        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onStart() {
        final String welcomeMessage = getString(R.string.welcome_message)
                + " "
                + BaasUser.current().getName();
        SnackbarManager.show(
                Snackbar.with(getApplicationContext())
                        .text(welcomeMessage)
                , this);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(sharedPref.contains(getString(R.string.user_is_playing_boolean))
                && sharedPref.getBoolean(getString(R.string.user_is_playing_boolean), false)) {
            new MaterialDialog.Builder(this)
                    .title(getString(R.string.resume_level_title))
                    .content(getString(R.string.resume_level_question))
                    .positiveText(getString(R.string.resume_positive))
                    .negativeText(getString(R.string.resume_negative))
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            resumeLevel();
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            //clearResumeState();
                        }
                    })
                    .show();
        }
        super.onStart();
    }

    private void resumeLevel() {
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Intent intent = new Intent(this, LevelPrepareActivity.class);
        intent.putExtra(getString(R.string.resume_level_intent_bool), true);
        intent.putExtra(getString(R.string.resume_base_path),
                sharedPref.getString(getString(R.string.resume_base_path), ""));
        startActivity(intent);
    }

    private void clearResumeState() {
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.user_is_playing_boolean), false);
        editor.commit();
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

    private void startLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
