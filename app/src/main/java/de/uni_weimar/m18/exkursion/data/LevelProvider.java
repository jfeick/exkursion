package de.uni_weimar.m18.exkursion.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class LevelProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private LevelDBHelper mOpenHelper;

    static final int LEVEL              = 100;
    static final int LEVEL_WITH_ID      = 101;

    //private static final SQLiteQueryBuilder sLevelByTitleQueryBuilder;

    private Cursor getLevelByTitle(Uri uri, String[] projection, String sortOrder) {
        // TODO
        // clean up

        String title = LevelContract.LevelEntry.getIdFromUri(uri);
        String[] selectionArgs = new String[]{title};
        String sLevelSelection =
                LevelContract.LevelEntry.TABLE_NAME +
                        "." + LevelContract.LevelEntry.COLUMN_TITLE + " = ? ";


        return null;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new LevelDBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        // use the Uri Matcher to determine kind of Uri
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case LEVEL:
                return LevelContract.LevelEntry.CONTENT_TYPE;
            case LEVEL_WITH_ID:
                return LevelContract.LevelEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // determine kind of request and query database accordingly
        Cursor retCursor;
        switch(sUriMatcher.match(uri)) {
            // "level/*"
            case LEVEL_WITH_ID: {
                retCursor = getLevelById(uri, projection, sortOrder);
                break;
            }
            case LEVEL: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        LevelContract.LevelEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getLevelById(Uri uri, String[] projection, String sortOrder) {
        String id = LevelContract.LevelEntry.getIdFromUri(uri);
        String[] selectionArgs;
        String selection;
        selection = LevelContract.LevelEntry.TABLE_NAME + "." + LevelContract.LevelEntry._ID + " = ?";
        selectionArgs = new String[]{id};
        return mOpenHelper.getReadableDatabase().query(
                LevelContract.LevelEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case LEVEL: {
                long _id = db.insert(LevelContract.LevelEntry.TABLE_NAME, null, values);
                if( _id > 0 )
                    returnUri = LevelContract.LevelEntry.buildLevelsUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if ( null == selection )
            selection = "1";
        switch(match) {
            case LEVEL:
                rowsDeleted = db.delete(
                        LevelContract.LevelEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // because null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case LEVEL:
                rowsUpdated = db.update(LevelContract.LevelEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LEVEL:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(LevelContract.LevelEntry.TABLE_NAME, null, value);
                        if (_id != - 1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = LevelContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, LevelContract.PATH_LEVELS, LEVEL);
        matcher.addURI(authority, LevelContract.PATH_LEVELS + "/*", LEVEL_WITH_ID);
        return matcher;
    }
}
