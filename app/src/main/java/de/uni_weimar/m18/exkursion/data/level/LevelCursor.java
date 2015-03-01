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

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.uni_weimar.m18.exkursion.data.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code level} table.
 */
public class LevelCursor extends AbstractCursor implements LevelModel {
    public LevelCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(LevelColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The base path at the backend location where level resources are stored
     * Cannot be {@code null}.
     */
    @NonNull
    public String getBasePath() {
        String res = getStringOrNull(LevelColumns.BASE_PATH);
        if (res == null)
            throw new NullPointerException("The value of 'base_path' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The title of the level
     * Cannot be {@code null}.
     */
    @NonNull
    public String getTitle() {
        String res = getStringOrNull(LevelColumns.TITLE);
        if (res == null)
            throw new NullPointerException("The value of 'title' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The level description
     * Can be {@code null}.
     */
    @Nullable
    public String getDescription() {
        String res = getStringOrNull(LevelColumns.DESCRIPTION);
        return res;
    }

    /**
     * Boolean flag to indicate if the user already finished the level
     */
    public boolean getFinished() {
        Boolean res = getBooleanOrNull(LevelColumns.FINISHED);
        if (res == null)
            throw new NullPointerException("The value of 'finished' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The last stage/page the user reached - to resume playing from this point
     */
    public int getLastStage() {
        Integer res = getIntegerOrNull(LevelColumns.LAST_STAGE);
        if (res == null)
            throw new NullPointerException("The value of 'last_stage' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The score the user achieved so far for this level
     */
    public int getScore() {
        Integer res = getIntegerOrNull(LevelColumns.SCORE);
        if (res == null)
            throw new NullPointerException("The value of 'score' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
