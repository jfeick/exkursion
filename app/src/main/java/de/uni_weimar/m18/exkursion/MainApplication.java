package de.uni_weimar.m18.exkursion;

import android.app.Application;

import de.uni_weimar.m18.exkursion.util.LevelStateManager;

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
public class MainApplication extends Application {
    private LevelStateManager levelStateManager = new LevelStateManager();

    public LevelStateManager getStateManager() {
        return levelStateManager;
    }
}
