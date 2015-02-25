package de.uni_weimar.m18.exkursion.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class LevelContract {

    public static final String CONTENT_AUTHORITY = "de.uni_weimar.m18.exkursion";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_LEVELS = "levels";

    public static final class LevelEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LEVELS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LEVELS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LEVELS;

        public static final String TABLE_NAME = "levels";

        public static final String COLUMN_PATH = "path";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MD5SUM = "md5sum";

        public static Uri buildLevelsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildLevelsTitle(String title) {
            return CONTENT_URI.buildUpon().appendPath(title).build();
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
