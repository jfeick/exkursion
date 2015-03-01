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

package de.uni_weimar.m18.exkursion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadProgressFragment extends Fragment {

    private static final String LOG_TAG = DownloadProgressFragment.class.getSimpleName();

    public DownloadProgressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_download_progress, container, false);
    }

    public void setProgress(int percent) {
        //Log.v(LOG_TAG, "setting progress in DownloadProgressFragment");
        ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        progressBar.setProgress(percent * progressBar.getMax() / 100);
        TextView textPercentage = (TextView) getActivity().findViewById(R.id.textPercentage);
        textPercentage.setText(Integer.toString(percent) + "%");
    }

}
