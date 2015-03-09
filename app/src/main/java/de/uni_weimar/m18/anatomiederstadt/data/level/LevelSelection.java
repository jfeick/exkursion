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
package de.uni_weimar.m18.anatomiederstadt.data.level;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import de.uni_weimar.m18.anatomiederstadt.data.base.AbstractSelection;

/**
 * Selection for the {@code level} table.
 */
public class LevelSelection extends AbstractSelection<LevelSelection> {
    @Override
    protected Uri baseUri() {
        return LevelColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code LevelCursor} object, which is positioned before the first entry, or null.
     */
    public LevelCursor query(ContentResolver contentResolver, String[] projection, String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new LevelCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null)}.
     */
    public LevelCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null)}.
     */
    public LevelCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }


    public LevelSelection id(long... value) {
        addEquals("level." + LevelColumns._ID, toObjectArray(value));
        return this;
    }

    public LevelSelection basePath(String... value) {
        addEquals(LevelColumns.BASE_PATH, value);
        return this;
    }

    public LevelSelection basePathNot(String... value) {
        addNotEquals(LevelColumns.BASE_PATH, value);
        return this;
    }

    public LevelSelection basePathLike(String... value) {
        addLike(LevelColumns.BASE_PATH, value);
        return this;
    }

    public LevelSelection basePathContains(String... value) {
        addContains(LevelColumns.BASE_PATH, value);
        return this;
    }

    public LevelSelection basePathStartsWith(String... value) {
        addStartsWith(LevelColumns.BASE_PATH, value);
        return this;
    }

    public LevelSelection basePathEndsWith(String... value) {
        addEndsWith(LevelColumns.BASE_PATH, value);
        return this;
    }

    public LevelSelection title(String... value) {
        addEquals(LevelColumns.TITLE, value);
        return this;
    }

    public LevelSelection titleNot(String... value) {
        addNotEquals(LevelColumns.TITLE, value);
        return this;
    }

    public LevelSelection titleLike(String... value) {
        addLike(LevelColumns.TITLE, value);
        return this;
    }

    public LevelSelection titleContains(String... value) {
        addContains(LevelColumns.TITLE, value);
        return this;
    }

    public LevelSelection titleStartsWith(String... value) {
        addStartsWith(LevelColumns.TITLE, value);
        return this;
    }

    public LevelSelection titleEndsWith(String... value) {
        addEndsWith(LevelColumns.TITLE, value);
        return this;
    }

    public LevelSelection description(String... value) {
        addEquals(LevelColumns.DESCRIPTION, value);
        return this;
    }

    public LevelSelection descriptionNot(String... value) {
        addNotEquals(LevelColumns.DESCRIPTION, value);
        return this;
    }

    public LevelSelection descriptionLike(String... value) {
        addLike(LevelColumns.DESCRIPTION, value);
        return this;
    }

    public LevelSelection descriptionContains(String... value) {
        addContains(LevelColumns.DESCRIPTION, value);
        return this;
    }

    public LevelSelection descriptionStartsWith(String... value) {
        addStartsWith(LevelColumns.DESCRIPTION, value);
        return this;
    }

    public LevelSelection descriptionEndsWith(String... value) {
        addEndsWith(LevelColumns.DESCRIPTION, value);
        return this;
    }

    public LevelSelection finished(boolean value) {
        addEquals(LevelColumns.FINISHED, toObjectArray(value));
        return this;
    }

    public LevelSelection lastStage(int... value) {
        addEquals(LevelColumns.LAST_STAGE, toObjectArray(value));
        return this;
    }

    public LevelSelection lastStageNot(int... value) {
        addNotEquals(LevelColumns.LAST_STAGE, toObjectArray(value));
        return this;
    }

    public LevelSelection lastStageGt(int value) {
        addGreaterThan(LevelColumns.LAST_STAGE, value);
        return this;
    }

    public LevelSelection lastStageGtEq(int value) {
        addGreaterThanOrEquals(LevelColumns.LAST_STAGE, value);
        return this;
    }

    public LevelSelection lastStageLt(int value) {
        addLessThan(LevelColumns.LAST_STAGE, value);
        return this;
    }

    public LevelSelection lastStageLtEq(int value) {
        addLessThanOrEquals(LevelColumns.LAST_STAGE, value);
        return this;
    }

    public LevelSelection score(int... value) {
        addEquals(LevelColumns.SCORE, toObjectArray(value));
        return this;
    }

    public LevelSelection scoreNot(int... value) {
        addNotEquals(LevelColumns.SCORE, toObjectArray(value));
        return this;
    }

    public LevelSelection scoreGt(int value) {
        addGreaterThan(LevelColumns.SCORE, value);
        return this;
    }

    public LevelSelection scoreGtEq(int value) {
        addGreaterThanOrEquals(LevelColumns.SCORE, value);
        return this;
    }

    public LevelSelection scoreLt(int value) {
        addLessThan(LevelColumns.SCORE, value);
        return this;
    }

    public LevelSelection scoreLtEq(int value) {
        addLessThanOrEquals(LevelColumns.SCORE, value);
        return this;
    }
}
