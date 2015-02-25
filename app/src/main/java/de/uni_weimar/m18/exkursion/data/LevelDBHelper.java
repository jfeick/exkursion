package de.uni_weimar.m18.exkursion.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.uni_weimar.m18.exkursion.data.LevelContract.LevelEntry;

public class LevelDBHelper extends SQLiteOpenHelper {
    // version of database schema (increment on change)
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "level.db";

    public LevelDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_CREATE_LEVEL_TABLE = "CREATE TABLE " + LevelEntry.TABLE_NAME + " (" +
                LevelEntry._ID + " INTEGER PRIMARY KEY, " +
                LevelEntry.COLUMN_PATH + " TEXT UNIQUE NOT NULL, " +
                LevelEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                LevelEntry.COLUMN_MD5SUM + " CHAR(32) " +
                ");";
        db.execSQL(SQL_CREATE_CREATE_LEVEL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO
        // need to change for a production upgrade scenario - right now, just discard everything and
        // start fresh with a new db
        db.execSQL("DROP TABLE IF EXISTS " + LevelEntry.TABLE_NAME);
        onCreate(db);
    }
}
