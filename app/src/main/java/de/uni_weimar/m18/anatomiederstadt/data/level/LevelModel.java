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

import de.uni_weimar.m18.anatomiederstadt.data.base.BaseModel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A level resource stored on the backend
 */
public interface LevelModel extends BaseModel {

    /**
     * The base path at the backend location where level resources are stored
     * Cannot be {@code null}.
     */
    @NonNull
    String getBasePath();

    /**
     * The title of the level
     * Cannot be {@code null}.
     */
    @NonNull
    String getTitle();

    /**
     * The level description
     * Can be {@code null}.
     */
    @Nullable
    String getDescription();

    /**
     * Boolean flag to indicate if the user already finished the level
     */
    boolean getFinished();

    /**
     * The last stage/page the user reached - to resume playing from this point
     */
    int getLastStage();

    /**
     * The score the user achieved so far for this level
     */
    int getScore();
}
