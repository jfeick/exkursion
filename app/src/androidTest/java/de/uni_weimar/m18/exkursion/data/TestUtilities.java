package de.uni_weimar.m18.exkursion.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;
//import de.uni_weimar.m18.exkursion.utils.PollingCheck;
import java.util.Map;
import java.util.Set;

public class TestUtilities extends AndroidTestCase {
    static final String TEST_TITLE = "Ausflug zum Brunnen";

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
    Students: You can uncomment this helper function once you have finished creating the
    LocationEntry part of the WeatherContract.
    */
    static ContentValues createLevelValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(LevelContract.LevelEntry.COLUMN_PATH, "/random_level_directory");
        testValues.put(LevelContract.LevelEntry.COLUMN_TITLE, TEST_TITLE);
        testValues.put(LevelContract.LevelEntry.COLUMN_MD5SUM, "d1e01057deffd459f8fbc2c54132ba01");
        return testValues;
    }

    static long insertLevelValues(Context context) {
        // insert our test records into the database
        LevelDBHelper dbHelper = new LevelDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createLevelValues();
        long locationRowId;
        locationRowId = db.insert(LevelContract.LevelEntry.TABLE_NAME, null, testValues);
        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);
        return locationRowId;
    }

    /*
    Students: The functions we provide inside of TestProvider use this utility class to test
    the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
    CTS tests.
    Note that this only tests that the onChange function is called; it does not test that the
    correct Uri is returned.
    */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications. The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            /*
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
            */
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}