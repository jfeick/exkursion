/*
 * Copyright 2015 J.F.Eick
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
 *
 */
package de.uni_weimar.m18.exkursion.data;

import java.util.Arrays;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import de.uni_weimar.m18.exkursion.BuildConfig;
import de.uni_weimar.m18.exkursion.data.base.BaseContentProvider;
import de.uni_weimar.m18.exkursion.data.files.FilesColumns;
import de.uni_weimar.m18.exkursion.data.level.LevelColumns;

public class LevelProvider extends BaseContentProvider {
    private static final String TAG = LevelProvider.class.getSimpleName();

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";
    private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";

    public static final String AUTHORITY = "de.uni_weimar.m18.exkursion";
    public static final String CONTENT_URI_BASE = "content://" + AUTHORITY;

    private static final int URI_TYPE_FILES = 0;
    private static final int URI_TYPE_FILES_ID = 1;

    private static final int URI_TYPE_LEVEL = 2;
    private static final int URI_TYPE_LEVEL_ID = 3;



    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, FilesColumns.TABLE_NAME, URI_TYPE_FILES);
        URI_MATCHER.addURI(AUTHORITY, FilesColumns.TABLE_NAME + "/#", URI_TYPE_FILES_ID);
        URI_MATCHER.addURI(AUTHORITY, LevelColumns.TABLE_NAME, URI_TYPE_LEVEL);
        URI_MATCHER.addURI(AUTHORITY, LevelColumns.TABLE_NAME + "/#", URI_TYPE_LEVEL_ID);
    }

    @Override
    protected SQLiteOpenHelper createSqLiteOpenHelper() {
        return LevelSQLiteOpenHelper.getInstance(getContext());
    }

    @Override
    protected boolean hasDebug() {
        return DEBUG;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_TYPE_FILES:
                return TYPE_CURSOR_DIR + FilesColumns.TABLE_NAME;
            case URI_TYPE_FILES_ID:
                return TYPE_CURSOR_ITEM + FilesColumns.TABLE_NAME;

            case URI_TYPE_LEVEL:
                return TYPE_CURSOR_DIR + LevelColumns.TABLE_NAME;
            case URI_TYPE_LEVEL_ID:
                return TYPE_CURSOR_ITEM + LevelColumns.TABLE_NAME;

        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (DEBUG) Log.d(TAG, "insert uri=" + uri + " values=" + values);
        return super.insert(uri, values);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (DEBUG) Log.d(TAG, "bulkInsert uri=" + uri + " values.length=" + values.length);
        return super.bulkInsert(uri, values);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "update uri=" + uri + " values=" + values + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.update(uri, values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "delete uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.delete(uri, selection, selectionArgs);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (DEBUG)
            Log.d(TAG, "query uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs) + " sortOrder=" + sortOrder
                    + " groupBy=" + uri.getQueryParameter(QUERY_GROUP_BY) + " having=" + uri.getQueryParameter(QUERY_HAVING) + " limit=" + uri.getQueryParameter(QUERY_LIMIT));
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected QueryParams getQueryParams(Uri uri, String selection, String[] projection) {
        QueryParams res = new QueryParams();
        String id = null;
        int matchedId = URI_MATCHER.match(uri);
        switch (matchedId) {
            case URI_TYPE_FILES:
            case URI_TYPE_FILES_ID:
                res.table = FilesColumns.TABLE_NAME;
                res.idColumn = FilesColumns._ID;
                res.tablesWithJoins = FilesColumns.TABLE_NAME;
                if (LevelColumns.hasColumns(projection)) {
                    res.tablesWithJoins += " LEFT OUTER JOIN " + LevelColumns.TABLE_NAME + " AS " + FilesColumns.PREFIX_LEVEL + " ON " + FilesColumns.TABLE_NAME + "." + FilesColumns.LEVEL_ID + "=" + FilesColumns.PREFIX_LEVEL + "." + LevelColumns._ID;
                }
                res.orderBy = FilesColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_LEVEL:
            case URI_TYPE_LEVEL_ID:
                res.table = LevelColumns.TABLE_NAME;
                res.idColumn = LevelColumns._ID;
                res.tablesWithJoins = LevelColumns.TABLE_NAME;
                res.orderBy = LevelColumns.DEFAULT_ORDER;
                break;

            default:
                throw new IllegalArgumentException("The uri '" + uri + "' is not supported by this ContentProvider");
        }

        switch (matchedId) {
            case URI_TYPE_FILES_ID:
            case URI_TYPE_LEVEL_ID:
                id = uri.getLastPathSegment();
        }
        if (id != null) {
            if (selection != null) {
                res.selection = res.table + "." + res.idColumn + "=" + id + " and (" + selection + ")";
            } else {
                res.selection = res.table + "." + res.idColumn + "=" + id;
            }
        } else {
            res.selection = selection;
        }
        return res;
    }
}
