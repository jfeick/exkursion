package de.uni_weimar.m18.exkursion;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;


public class DownloadFragment extends Fragment {

    private static final String LOG_TAG = DownloadFragment.class.getSimpleName();

    static interface TaskCallbacks {
        void onPreExecute();
        void onProgressUpdate(int percent);
        void onCancelled();
        void onPostExecute();
    }

    private TaskCallbacks mCallbacks;

    public DownloadFragment() {
        // Required empty public constructor
    }

    public void startDownload(String path) {
        Log.v(LOG_TAG, "received Signal to start download for: " + path);
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

    private class DownloadLevelTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mCallbacks != null) {
                mCallbacks.onPreExecute();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; !isCancelled() && i < 100; ++i) {
                SystemClock.sleep(100);
                publishProgress(i);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... percent) {
            super.onProgressUpdate(percent);
            if (mCallbacks != null) {
                mCallbacks.onProgressUpdate(percent[0]);
            }
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            if (mCallbacks != null) {
                mCallbacks.onCancelled();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mCallbacks != null) {
                mCallbacks.onPostExecute();
            }
        }
    }
}
