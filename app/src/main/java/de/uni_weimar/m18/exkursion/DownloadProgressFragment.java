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
