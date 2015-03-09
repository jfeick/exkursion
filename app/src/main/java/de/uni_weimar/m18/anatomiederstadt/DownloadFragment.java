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

package de.uni_weimar.m18.anatomiederstadt;



import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import de.uni_weimar.m18.anatomiederstadt.data.level.LevelColumns;


public class DownloadFragment extends Fragment {

    private static final String LOG_TAG = DownloadFragment.class.getSimpleName();

    static interface TaskCallbacks {
        void onPreExecute();
        void onProgressUpdate(int percent);
        void onCancelled();
        void onPostExecute();
    }

    public TaskCallbacks mCallbacks;

    public DownloadFragment() {
        // Required empty public constructor
    }

    public void startFetchLevelDataTask(Cursor data) {
        Log.v(LOG_TAG, "received Signal to start download for: "
                + data.getString(data.getColumnIndex(LevelColumns.BASE_PATH)));
        FetchLevelDataTask fetchLevelDataTask = new FetchLevelDataTask(this, data);
        fetchLevelDataTask.execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (TaskCallbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

}
