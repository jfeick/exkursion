package de.uni_weimar.m18.exkursion;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Level;

import de.uni_weimar.m18.exkursion.data.LevelContract;

/**
 * Created by stu on 22.02.2015.
 */
public class FetchLevelsTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchLevelsTask.class.getSimpleName();

    private final Context mContext;

    private String mBaseUrl;

    public FetchLevelsTask(Context context, String baseUrl) {
        mContext = context;
        mBaseUrl = baseUrl;
    }

    private boolean DEBUG = true;

    long addLevel(String path, String title, String md5sum) {

        // TODO
        // this method stinks - we need to check the md5hash for insertion-updates and download-updates

        long levelId;

        // check if the level with this path exists in the db
        Cursor levelCursor = mContext.getContentResolver().query(
                LevelContract.LevelEntry.CONTENT_URI,
                new String[]{LevelContract.LevelEntry._ID},
                LevelContract.LevelEntry.COLUMN_PATH + " = ?",
                new String[]{path},
                null
        );

        if (levelCursor.moveToFirst()) {
            // if exits, update row

            int levelIdIndex = levelCursor.getColumnIndex(LevelContract.LevelEntry._ID);
            levelId = levelCursor.getLong(levelIdIndex);
            ContentValues updatedValues = new ContentValues();
            updatedValues.put(LevelContract.LevelEntry.COLUMN_TITLE, title);
            updatedValues.put(LevelContract.LevelEntry.COLUMN_MD5SUM, md5sum);

            int count = mContext.getContentResolver().update(
                    LevelContract.LevelEntry.CONTENT_URI, updatedValues,
                    LevelContract.LevelEntry._ID + " = ?", new String[]{Long.toString(levelId)}
            );
            //levelId = ContentUris.parseId(updatedUri);

            Log.v(LOG_TAG, "Updated " + count + " rows");
            //int levelIdIndex = levelCursor.getColumnIndex(LevelContract.LevelEntry._ID);
            //levelId = levelCursor.getLong(levelIdIndex);
        } else {
            ContentValues levelValues = new ContentValues();
            levelValues.put(LevelContract.LevelEntry.COLUMN_PATH, path);
            levelValues.put(LevelContract.LevelEntry.COLUMN_TITLE, title);
            levelValues.put(LevelContract.LevelEntry.COLUMN_MD5SUM, md5sum);

            // insert data to db
            Uri insertedUri = mContext.getContentResolver().insert(
                    LevelContract.LevelEntry.CONTENT_URI,
                    levelValues
            );
            // resulting Uri contains ID for the row. Extract the levelId from the Uri
            levelId = ContentUris.parseId(insertedUri);
        }
        levelCursor.close();
        return levelId;
    }

    private void getLevelDataFromJson(String levelJsonStr) throws JSONException {

        // TODO:
        // change bulk insert to single insert/update in regard to md5sum

        final String OWM_PATH = "path";
        final String OWM_TITLE = "title";
        final String OWM_MD5 = "md5";


        try {
            JSONObject levelsJson = new JSONObject(levelJsonStr);
            //Log.v(LOG_TAG, "JSONObject: " + levelsJson.toString());

            JSONArray levelsArray = levelsJson.getJSONArray("levels");
            Log.v(LOG_TAG, "JSONArray: " + levelsArray.toString());

            //Vector<ContentValues> cVVector = new Vector<ContentValues>(levelsArray.length());

            int inserted = 0;
            for (int i = 0; i < levelsArray.length(); i++) {

                JSONObject level = levelsArray.getJSONObject(i);

                String path;
                String title;
                String md5sum;
                path = level.getString(OWM_PATH);
                title = level.getString(OWM_TITLE);
                md5sum = level.getString(OWM_MD5);

                long _id = addLevel(path, title, md5sum);
                if( _id != 0 ) {
                    inserted++;
                }
            }

            Log.d(LOG_TAG, "FetchLevelsTask Complete. " + inserted + " inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String levelJsonStr = null;
        try {
            URL url = new URL(mBaseUrl + "game.php");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            levelJsonStr = buffer.toString();
            Log.v(LOG_TAG, "JSON answer: " + levelJsonStr);
            getLevelDataFromJson(levelJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }


        return null;
    }
}
