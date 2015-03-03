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

package de.uni_weimar.m18.exkursion.data;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileUtilities {

    private static final String LOG_TAG = FileUtilities.class.getSimpleName();

    static public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    static public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    static public boolean fileExistsInStorage(Context context, String base_path, String path, String filename) {
        File file = new File(context.getExternalFilesDir(null)
                + "/" + base_path + "/" + path + "/" + filename);
        //Log.v(LOG_TAG, "File location: " + file.getAbsolutePath());
        if(file != null) {
            return file.exists();
        }
        return false;
    }


}
