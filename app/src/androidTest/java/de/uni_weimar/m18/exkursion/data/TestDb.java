package de.uni_weimar.m18.exkursion.data;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;
import de.uni_weimar.m18.exkursion.data.LevelContract.LevelEntry;

public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(LevelDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new LevelDBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {
        // Test data we're going to insert into the DB to see if it works.
        String testPath = "level0";
        String testTitle = "Brunnenquiz mit Ümläüten";
        String testMD5sum = "d1e01057deffd459f8fbc2c54132ba01";
        // If there's an error in those massive SQL table creation Strings,
        //errors will be thrown here when you try to get a writable database.
                LevelDBHelper dbHelper = new LevelDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LevelEntry.COLUMN_PATH, testPath);
        values.put(LevelEntry.COLUMN_TITLE, testTitle);
        values.put(LevelEntry.COLUMN_MD5SUM, testMD5sum);
        long locationRowId;
        locationRowId = db.insert(LevelEntry.TABLE_NAME, null, values);
        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);
        // Data's inserted. IN THEORY. Now pull some out to stare at it and verify it made
        // the round trip.
        // Specify which columns you want.
        String[] columns = {
                LevelEntry._ID,
                LevelEntry.COLUMN_PATH,
                LevelEntry.COLUMN_TITLE,
                LevelEntry.COLUMN_MD5SUM
        };
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                LevelEntry.TABLE_NAME, // Table to Query
                columns,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );
        // If possible, move to the first row of the query results.
        if (cursor.moveToFirst()) {
            // Get the value in each column by finding the appropriate column index.
            int pathIndex = cursor.getColumnIndex(LevelEntry.COLUMN_PATH);
            String path = cursor.getString(pathIndex);
            int titleIndex = cursor.getColumnIndex(LevelEntry.COLUMN_TITLE);
            String title = cursor.getString(titleIndex);
            int md5sumIndex = cursor.getColumnIndex(LevelEntry.COLUMN_MD5SUM);
            String md5sum = cursor.getString(md5sumIndex);
            // Hooray, data was returned! Assert that it's the right data, and that the database
            // creation code is working as intended.
            // Then take a break. We both know that wasn't easy.
            assertEquals(testPath, path);
            assertEquals(testTitle, title);
            assertEquals(testMD5sum, md5sum);
            // Fantastic. Now that we have a location, add some weather!
        } else {
            // That's weird, it works on MY machine...
            fail("No values returned :(");
        }

        dbHelper.close();
    }
}