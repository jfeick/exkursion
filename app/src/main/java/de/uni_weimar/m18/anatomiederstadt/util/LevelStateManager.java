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

package de.uni_weimar.m18.anatomiederstadt.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.w3c.dom.Document;

import java.util.Map;

import de.uni_weimar.m18.anatomiederstadt.R;

public class LevelStateManager {

    private Activity activity;

    private Document levelXML;
    private String basePath;

    private Map<String, Float> varsFloat;
    private Map<String, Integer> varsInteger;

    public LevelStateManager() {
    }

    public void updateActivity(Activity activity) {
        this.activity = activity;
    }

    public Document getLevelXML() {
        return levelXML;
    }

    public void setLevelXML(Document levelXML) {
        this.levelXML = levelXML;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getBasePath() {
        return basePath;
    }

    public void saveFloat(String identifier, float value) {
        String key = basePath + "_" + identifier;
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public float getFloat(String identifier) {
        String key = basePath + "_" + identifier;
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getFloat(key, 1.0f);
    }

    public void saveInt(String identifier, int value) {
        String key = basePath + "_" + identifier;
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String identifier) {
        String key = basePath + "_" + identifier;
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt(key, 1);
    }

}
