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
package de.uni_weimar.m18.exkursion.data.files;

import android.net.Uri;
import android.provider.BaseColumns;

import de.uni_weimar.m18.exkursion.data.LevelProvider;
import de.uni_weimar.m18.exkursion.data.files.FilesColumns;
import de.uni_weimar.m18.exkursion.data.level.LevelColumns;

/**
 * A file, which belongs as a resource to a level
 */
public class FilesColumns implements BaseColumns {
    public static final String TABLE_NAME = "files";
    public static final Uri CONTENT_URI = Uri.parse(LevelProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * The name of the file
     */
    public static final String FILENAME = "filename";

    /**
     * The path to the file
     */
    public static final String PATH = "path";

    /**
     * The local file version (unix timestamp copied from remote_version when file is fetched or updated)
     */
    public static final String LOCAL_VERSION = "local_version";

    /**
     * The remote file version (unix timestamp of the file from the backend)
     */
    public static final String REMOTE_VERSION = "remote_version";

    /**
     * The level corresponding to this file
     */
    public static final String LEVEL_ID = "level_id";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            FILENAME,
            PATH,
            LOCAL_VERSION,
            REMOTE_VERSION,
            LEVEL_ID
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c == FILENAME || c.contains("." + FILENAME)) return true;
            if (c == PATH || c.contains("." + PATH)) return true;
            if (c == LOCAL_VERSION || c.contains("." + LOCAL_VERSION)) return true;
            if (c == REMOTE_VERSION || c.contains("." + REMOTE_VERSION)) return true;
            if (c == LEVEL_ID || c.contains("." + LEVEL_ID)) return true;
        }
        return false;
    }

    public static final String PREFIX_LEVEL = TABLE_NAME + "__" + LevelColumns.TABLE_NAME;
}
