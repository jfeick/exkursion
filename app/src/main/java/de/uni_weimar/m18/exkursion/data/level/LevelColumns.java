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
package de.uni_weimar.m18.exkursion.data.level;

import android.net.Uri;
import android.provider.BaseColumns;

import de.uni_weimar.m18.exkursion.data.LevelProvider;
import de.uni_weimar.m18.exkursion.data.files.FilesColumns;
import de.uni_weimar.m18.exkursion.data.level.LevelColumns;

/**
 * A level resource stored on the backend
 */
public class LevelColumns implements BaseColumns {
    public static final String TABLE_NAME = "level";
    public static final Uri CONTENT_URI = Uri.parse(LevelProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * The base path at the backend location where level resources are stored
     */
    public static final String BASE_PATH = "base_path";

    /**
     * The title of the level
     */
    public static final String TITLE = "title";

    /**
     * The level description
     */
    public static final String DESCRIPTION = "description";

    /**
     * Boolean flag to indicate if the user already finished the level
     */
    public static final String FINISHED = "finished";

    /**
     * The last stage/page the user reached - to resume playing from this point
     */
    public static final String LAST_STAGE = "last_stage";

    /**
     * The score the user achieved so far for this level
     */
    public static final String SCORE = "score";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            BASE_PATH,
            TITLE,
            DESCRIPTION,
            FINISHED,
            LAST_STAGE,
            SCORE
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c == BASE_PATH || c.contains("." + BASE_PATH)) return true;
            if (c == TITLE || c.contains("." + TITLE)) return true;
            if (c == DESCRIPTION || c.contains("." + DESCRIPTION)) return true;
            if (c == FINISHED || c.contains("." + FINISHED)) return true;
            if (c == LAST_STAGE || c.contains("." + LAST_STAGE)) return true;
            if (c == SCORE || c.contains("." + SCORE)) return true;
        }
        return false;
    }

}
