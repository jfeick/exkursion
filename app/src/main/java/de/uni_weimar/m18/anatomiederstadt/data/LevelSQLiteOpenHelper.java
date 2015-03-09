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

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import de.uni_weimar.m18.anatomiederstadt.BuildConfig;
import de.uni_weimar.m18.anatomiederstadt.data.files.FilesColumns;
import de.uni_weimar.m18.anatomiederstadt.data.level.LevelColumns;

public class LevelSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = LevelSQLiteOpenHelper.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "level.db";
    private static final int DATABASE_VERSION = 3;
    private static LevelSQLiteOpenHelper sInstance;
    private final Context mContext;
    private final LevelSQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    // @formatter:off
    public static final String SQL_CREATE_TABLE_FILES = "CREATE TABLE IF NOT EXISTS "
            + FilesColumns.TABLE_NAME + " ( "
            + FilesColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FilesColumns.FILENAME + " TEXT NOT NULL, "
            + FilesColumns.PATH + " TEXT NOT NULL DEFAULT '.', "
            + FilesColumns.LOCAL_VERSION + " INTEGER NOT NULL DEFAULT 0, "
            + FilesColumns.REMOTE_VERSION + " INTEGER NOT NULL, "
            + FilesColumns.LEVEL_ID + " INTEGER NOT NULL "
            + ", CONSTRAINT fk_level_id FOREIGN KEY (" + FilesColumns.LEVEL_ID + ") REFERENCES level (_id) ON DELETE CASCADE"
            + ", CONSTRAINT unique_file UNIQUE (path, filename, level_id) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_TABLE_LEVEL = "CREATE TABLE IF NOT EXISTS "
            + LevelColumns.TABLE_NAME + " ( "
            + LevelColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + LevelColumns.BASE_PATH + " TEXT NOT NULL, "
            + LevelColumns.TITLE + " TEXT NOT NULL, "
            + LevelColumns.DESCRIPTION + " TEXT, "
            + LevelColumns.FINISHED + " INTEGER NOT NULL DEFAULT 0, "
            + LevelColumns.LAST_STAGE + " INTEGER NOT NULL DEFAULT 0, "
            + LevelColumns.SCORE + " INTEGER NOT NULL DEFAULT 0 "
            + ", CONSTRAINT unique_base_path UNIQUE (base_path) ON CONFLICT REPLACE"
            + " );";

    // @formatter:on

    public static LevelSQLiteOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static LevelSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */
    private static LevelSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new LevelSQLiteOpenHelper(context);
    }

    private LevelSQLiteOpenHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new LevelSQLiteOpenHelperCallbacks();
    }


    /*
     * Post Honeycomb.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static LevelSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new LevelSQLiteOpenHelper(context, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private LevelSQLiteOpenHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new LevelSQLiteOpenHelperCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_FILES);
        db.execSQL(SQL_CREATE_TABLE_LEVEL);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
