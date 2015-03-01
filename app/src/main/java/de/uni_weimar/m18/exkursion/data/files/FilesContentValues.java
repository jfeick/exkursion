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

import java.util.Date;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.uni_weimar.m18.exkursion.data.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code files} table.
 */
public class FilesContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return FilesColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable FilesSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * The name of the file
     */
    public FilesContentValues putFilename(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("filename must not be null");
        mContentValues.put(FilesColumns.FILENAME, value);
        return this;
    }


    /**
     * The path to the file
     */
    public FilesContentValues putPath(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("path must not be null");
        mContentValues.put(FilesColumns.PATH, value);
        return this;
    }


    /**
     * The local file version (unix timestamp copied from remote_version when file is fetched or updated)
     */
    public FilesContentValues putLocalVersion(int value) {
        mContentValues.put(FilesColumns.LOCAL_VERSION, value);
        return this;
    }


    /**
     * The remote file version (unix timestamp of the file from the backend)
     */
    public FilesContentValues putRemoteVersion(int value) {
        mContentValues.put(FilesColumns.REMOTE_VERSION, value);
        return this;
    }


    /**
     * The level corresponding to this file
     */
    public FilesContentValues putLevelId(long value) {
        mContentValues.put(FilesColumns.LEVEL_ID, value);
        return this;
    }

}
