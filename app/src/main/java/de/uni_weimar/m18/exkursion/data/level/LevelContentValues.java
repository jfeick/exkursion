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
package de.uni_weimar.m18.exkursion.data.level;

import java.util.Date;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.uni_weimar.m18.exkursion.data.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code level} table.
 */
public class LevelContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return LevelColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable LevelSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * The base path at the backend location where level resources are stored
     */
    public LevelContentValues putBasePath(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("basePath must not be null");
        mContentValues.put(LevelColumns.BASE_PATH, value);
        return this;
    }


    /**
     * The title of the level
     */
    public LevelContentValues putTitle(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("title must not be null");
        mContentValues.put(LevelColumns.TITLE, value);
        return this;
    }


    /**
     * The level description
     */
    public LevelContentValues putDescription(@Nullable String value) {
        mContentValues.put(LevelColumns.DESCRIPTION, value);
        return this;
    }

    public LevelContentValues putDescriptionNull() {
        mContentValues.putNull(LevelColumns.DESCRIPTION);
        return this;
    }

    /**
     * Boolean flag to indicate if the user already finished the level
     */
    public LevelContentValues putFinished(boolean value) {
        mContentValues.put(LevelColumns.FINISHED, value);
        return this;
    }


    /**
     * The last stage/page the user reached - to resume playing from this point
     */
    public LevelContentValues putLastStage(int value) {
        mContentValues.put(LevelColumns.LAST_STAGE, value);
        return this;
    }


    /**
     * The score the user achieved so far for this level
     */
    public LevelContentValues putScore(int value) {
        mContentValues.put(LevelColumns.SCORE, value);
        return this;
    }

}
