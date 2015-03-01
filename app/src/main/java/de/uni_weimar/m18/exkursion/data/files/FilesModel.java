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

import de.uni_weimar.m18.exkursion.data.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A file, which belongs as a resource to a level
 */
public interface FilesModel extends BaseModel {

    /**
     * The name of the file
     * Cannot be {@code null}.
     */
    @NonNull
    String getFilename();

    /**
     * The path to the file
     * Cannot be {@code null}.
     */
    @NonNull
    String getPath();

    /**
     * The local file version (unix timestamp copied from remote_version when file is fetched or updated)
     */
    int getLocalVersion();

    /**
     * The remote file version (unix timestamp of the file from the backend)
     */
    int getRemoteVersion();

    /**
     * The level corresponding to this file
     */
    long getLevelId();
}
