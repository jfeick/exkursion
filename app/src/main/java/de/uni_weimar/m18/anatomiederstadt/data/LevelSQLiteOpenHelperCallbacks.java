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
package de.uni_weimar.m18.anatomiederstadt.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import de.uni_weimar.m18.anatomiederstadt.BuildConfig;
import de.uni_weimar.m18.anatomiederstadt.data.files.FilesColumns;
import de.uni_weimar.m18.anatomiederstadt.data.level.LevelColumns;

/**
 * Implement your custom database creation or upgrade code here.
 *
 * This file will not be overwritten if you re-run the content provider generator.
 */
public class LevelSQLiteOpenHelperCallbacks {
    private static final String TAG = LevelSQLiteOpenHelperCallbacks.class.getSimpleName();

    public void onOpen(final Context context, final SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onOpen");
        // Insert your db open code here.
    }

    public void onPreCreate(final Context context, final SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onPreCreate");
        // Insert your db creation code here. This is called before your tables are created.
    }

    public void onPostCreate(final Context context, final SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onPostCreate");
        // Insert your db creation code here. This is called after your tables are created.
    }

    public void onUpgrade(final Context context, final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        if (BuildConfig.DEBUG) Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        // TODO - change for production upgrade scenario
        // start with a fresh DB so far
        db.execSQL("DROP TABLE IF EXISTS " + LevelColumns.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FilesColumns.TABLE_NAME);
        LevelSQLiteOpenHelper.getInstance(context).onCreate(db);
    }
}
