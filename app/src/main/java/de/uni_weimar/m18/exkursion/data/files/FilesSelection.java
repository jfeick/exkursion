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
package de.uni_weimar.m18.exkursion.data.files;

import java.util.Date;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import de.uni_weimar.m18.exkursion.data.base.AbstractSelection;
import de.uni_weimar.m18.exkursion.data.level.*;

/**
 * Selection for the {@code files} table.
 */
public class FilesSelection extends AbstractSelection<FilesSelection> {
    @Override
    protected Uri baseUri() {
        return FilesColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code FilesCursor} object, which is positioned before the first entry, or null.
     */
    public FilesCursor query(ContentResolver contentResolver, String[] projection, String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new FilesCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null)}.
     */
    public FilesCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null)}.
     */
    public FilesCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }


    public FilesSelection id(long... value) {
        addEquals("files." + FilesColumns._ID, toObjectArray(value));
        return this;
    }

    public FilesSelection filename(String... value) {
        addEquals(FilesColumns.FILENAME, value);
        return this;
    }

    public FilesSelection filenameNot(String... value) {
        addNotEquals(FilesColumns.FILENAME, value);
        return this;
    }

    public FilesSelection filenameLike(String... value) {
        addLike(FilesColumns.FILENAME, value);
        return this;
    }

    public FilesSelection filenameContains(String... value) {
        addContains(FilesColumns.FILENAME, value);
        return this;
    }

    public FilesSelection filenameStartsWith(String... value) {
        addStartsWith(FilesColumns.FILENAME, value);
        return this;
    }

    public FilesSelection filenameEndsWith(String... value) {
        addEndsWith(FilesColumns.FILENAME, value);
        return this;
    }

    public FilesSelection path(String... value) {
        addEquals(FilesColumns.PATH, value);
        return this;
    }

    public FilesSelection pathNot(String... value) {
        addNotEquals(FilesColumns.PATH, value);
        return this;
    }

    public FilesSelection pathLike(String... value) {
        addLike(FilesColumns.PATH, value);
        return this;
    }

    public FilesSelection pathContains(String... value) {
        addContains(FilesColumns.PATH, value);
        return this;
    }

    public FilesSelection pathStartsWith(String... value) {
        addStartsWith(FilesColumns.PATH, value);
        return this;
    }

    public FilesSelection pathEndsWith(String... value) {
        addEndsWith(FilesColumns.PATH, value);
        return this;
    }

    public FilesSelection localVersion(int... value) {
        addEquals(FilesColumns.LOCAL_VERSION, toObjectArray(value));
        return this;
    }

    public FilesSelection localVersionNot(int... value) {
        addNotEquals(FilesColumns.LOCAL_VERSION, toObjectArray(value));
        return this;
    }

    public FilesSelection localVersionGt(int value) {
        addGreaterThan(FilesColumns.LOCAL_VERSION, value);
        return this;
    }

    public FilesSelection localVersionGtEq(int value) {
        addGreaterThanOrEquals(FilesColumns.LOCAL_VERSION, value);
        return this;
    }

    public FilesSelection localVersionLt(int value) {
        addLessThan(FilesColumns.LOCAL_VERSION, value);
        return this;
    }

    public FilesSelection localVersionLtEq(int value) {
        addLessThanOrEquals(FilesColumns.LOCAL_VERSION, value);
        return this;
    }

    public FilesSelection remoteVersion(int... value) {
        addEquals(FilesColumns.REMOTE_VERSION, toObjectArray(value));
        return this;
    }

    public FilesSelection remoteVersionNot(int... value) {
        addNotEquals(FilesColumns.REMOTE_VERSION, toObjectArray(value));
        return this;
    }

    public FilesSelection remoteVersionGt(int value) {
        addGreaterThan(FilesColumns.REMOTE_VERSION, value);
        return this;
    }

    public FilesSelection remoteVersionGtEq(int value) {
        addGreaterThanOrEquals(FilesColumns.REMOTE_VERSION, value);
        return this;
    }

    public FilesSelection remoteVersionLt(int value) {
        addLessThan(FilesColumns.REMOTE_VERSION, value);
        return this;
    }

    public FilesSelection remoteVersionLtEq(int value) {
        addLessThanOrEquals(FilesColumns.REMOTE_VERSION, value);
        return this;
    }

    public FilesSelection levelId(long... value) {
        addEquals(FilesColumns.LEVEL_ID, toObjectArray(value));
        return this;
    }

    public FilesSelection levelIdNot(long... value) {
        addNotEquals(FilesColumns.LEVEL_ID, toObjectArray(value));
        return this;
    }

    public FilesSelection levelIdGt(long value) {
        addGreaterThan(FilesColumns.LEVEL_ID, value);
        return this;
    }

    public FilesSelection levelIdGtEq(long value) {
        addGreaterThanOrEquals(FilesColumns.LEVEL_ID, value);
        return this;
    }

    public FilesSelection levelIdLt(long value) {
        addLessThan(FilesColumns.LEVEL_ID, value);
        return this;
    }

    public FilesSelection levelIdLtEq(long value) {
        addLessThanOrEquals(FilesColumns.LEVEL_ID, value);
        return this;
    }

    public FilesSelection levelBasePath(String... value) {
        addEquals(LevelColumns.BASE_PATH, value);
        return this;
    }

    public FilesSelection levelBasePathNot(String... value) {
        addNotEquals(LevelColumns.BASE_PATH, value);
        return this;
    }

    public FilesSelection levelBasePathLike(String... value) {
        addLike(LevelColumns.BASE_PATH, value);
        return this;
    }

    public FilesSelection levelBasePathContains(String... value) {
        addContains(LevelColumns.BASE_PATH, value);
        return this;
    }

    public FilesSelection levelBasePathStartsWith(String... value) {
        addStartsWith(LevelColumns.BASE_PATH, value);
        return this;
    }

    public FilesSelection levelBasePathEndsWith(String... value) {
        addEndsWith(LevelColumns.BASE_PATH, value);
        return this;
    }

    public FilesSelection levelTitle(String... value) {
        addEquals(LevelColumns.TITLE, value);
        return this;
    }

    public FilesSelection levelTitleNot(String... value) {
        addNotEquals(LevelColumns.TITLE, value);
        return this;
    }

    public FilesSelection levelTitleLike(String... value) {
        addLike(LevelColumns.TITLE, value);
        return this;
    }

    public FilesSelection levelTitleContains(String... value) {
        addContains(LevelColumns.TITLE, value);
        return this;
    }

    public FilesSelection levelTitleStartsWith(String... value) {
        addStartsWith(LevelColumns.TITLE, value);
        return this;
    }

    public FilesSelection levelTitleEndsWith(String... value) {
        addEndsWith(LevelColumns.TITLE, value);
        return this;
    }

    public FilesSelection levelDescription(String... value) {
        addEquals(LevelColumns.DESCRIPTION, value);
        return this;
    }

    public FilesSelection levelDescriptionNot(String... value) {
        addNotEquals(LevelColumns.DESCRIPTION, value);
        return this;
    }

    public FilesSelection levelDescriptionLike(String... value) {
        addLike(LevelColumns.DESCRIPTION, value);
        return this;
    }

    public FilesSelection levelDescriptionContains(String... value) {
        addContains(LevelColumns.DESCRIPTION, value);
        return this;
    }

    public FilesSelection levelDescriptionStartsWith(String... value) {
        addStartsWith(LevelColumns.DESCRIPTION, value);
        return this;
    }

    public FilesSelection levelDescriptionEndsWith(String... value) {
        addEndsWith(LevelColumns.DESCRIPTION, value);
        return this;
    }

    public FilesSelection levelFinished(boolean value) {
        addEquals(LevelColumns.FINISHED, toObjectArray(value));
        return this;
    }

    public FilesSelection levelLastStage(int... value) {
        addEquals(LevelColumns.LAST_STAGE, toObjectArray(value));
        return this;
    }

    public FilesSelection levelLastStageNot(int... value) {
        addNotEquals(LevelColumns.LAST_STAGE, toObjectArray(value));
        return this;
    }

    public FilesSelection levelLastStageGt(int value) {
        addGreaterThan(LevelColumns.LAST_STAGE, value);
        return this;
    }

    public FilesSelection levelLastStageGtEq(int value) {
        addGreaterThanOrEquals(LevelColumns.LAST_STAGE, value);
        return this;
    }

    public FilesSelection levelLastStageLt(int value) {
        addLessThan(LevelColumns.LAST_STAGE, value);
        return this;
    }

    public FilesSelection levelLastStageLtEq(int value) {
        addLessThanOrEquals(LevelColumns.LAST_STAGE, value);
        return this;
    }

    public FilesSelection levelScore(int... value) {
        addEquals(LevelColumns.SCORE, toObjectArray(value));
        return this;
    }

    public FilesSelection levelScoreNot(int... value) {
        addNotEquals(LevelColumns.SCORE, toObjectArray(value));
        return this;
    }

    public FilesSelection levelScoreGt(int value) {
        addGreaterThan(LevelColumns.SCORE, value);
        return this;
    }

    public FilesSelection levelScoreGtEq(int value) {
        addGreaterThanOrEquals(LevelColumns.SCORE, value);
        return this;
    }

    public FilesSelection levelScoreLt(int value) {
        addLessThan(LevelColumns.SCORE, value);
        return this;
    }

    public FilesSelection levelScoreLtEq(int value) {
        addLessThanOrEquals(LevelColumns.SCORE, value);
        return this;
    }
}
