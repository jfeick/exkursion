package de.uni_weimar.m18.exkursion.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.logging.Level;

import de.uni_weimar.m18.exkursion.data.LevelContract.LevelEntry;

public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
    This helper function deletes all records from both database tables using the ContentProvider.
    It also queries the ContentProvider to make sure that the database has been successfully
    deleted, so it cannot be used until the Query and Delete functions have been written
    in the ContentProvider.
    Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
    the delete functionality in the ContentProvider.
    */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                LevelEntry.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                LevelEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Level table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
    This helper function deletes all records from both database tables using the database
    functions only. This is designed to be used to reset the state of the database until the
    delete functionality is available in the ContentProvider.
    */
    public void deleteAllRecordsFromDB() {
        LevelDBHelper dbHelper = new LevelDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(LevelEntry.TABLE_NAME, null, null);
        db.close();
    }

    /*
    Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
    you have implemented delete functionality there.
    */
    public void deleteAllRecords() {
        deleteAllRecordsFromDB();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
    This test checks to make sure that the content provider is registered correctly.
    Students: Uncomment this test to make sure you've correctly registered the WeatherProvider.
    */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                LevelProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: LevelProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + LevelContract.CONTENT_AUTHORITY,
                    providerInfo.authority, LevelContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: LevelProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }
    /*
    This test doesn't touch the database. It verifies that the ContentProvider returns
    the correct type for each type of URI that it can handle.
    Students: Uncomment this test to verify that your implementation of GetType is
    functioning correctly.
    */
    public void testGetType() {
        // content://de.uni_weimar.m18.exkursion/levels/
        String type = mContext.getContentResolver().getType(LevelEntry.CONTENT_URI);
        // vnd.android.cursor.dir/de.uni_weimar.m18.exkursion/levels
        assertEquals("Error: the LevelEntry CONTENT_URI should return LevelEntry.CONTENT_TYPE",
                LevelEntry.CONTENT_TYPE, type);

        String title = "Ausflug zum Brunnen";
        // content://de.uni_weimar.m18.exkursion/levels/Ausflug%20zum%20Brunnen
        type = mContext.getContentResolver().getType(
                LevelEntry.buildLevelsTitle(title));
        // vnd.android.cursor.dir/de.uni_weimar.m18.exkursion/levels/???
        assertEquals("Error: the LevelEntry CONTENT_URI with title should return LevelEntry.CONTENT_ITEM_TYPE",
                LevelEntry.CONTENT_ITEM_TYPE, type);

    }
    /*
    This test uses the database directly to insert and then uses the ContentProvider to
    read out the data. Uncomment this test to see if the basic weather query functionality
    given in the ContentProvider is working correctly.
    */
    public void testBasicLevelQuery() {
        // insert our test records into the database
        LevelDBHelper dbHelper = new LevelDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createLevelValues();
        //long locationRowId = TestUtilities.insertLevelValues(mContext);

        long levelRowId = db.insert(LevelEntry.TABLE_NAME, null, testValues);
        assertTrue("Unable to Insert LevelEntry into the Database", levelRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor weatherCursor = mContext.getContentResolver().query(
                LevelEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicLevelQuery", weatherCursor, testValues);
    }
    /*
    This test uses the database directly to insert and then uses the ContentProvider to
    read out the data. Uncomment this test to see if your location queries are
    performing correctly.
    */
    public void testBasicLevelQueries() {
    // insert our test records into the database
        LevelDBHelper dbHelper = new LevelDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createLevelValues();
        long locationRowId = TestUtilities.insertLevelValues(mContext);

    // Test the basic content provider query
        Cursor levelCursor = mContext.getContentResolver().query(
                LevelEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

    // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicLevelQueries, level query", levelCursor, testValues);

    // Has the NotificationUri been set correctly? --- we can only test this easily against API
    // level 19 or greater because getNotificationUri was added in API level 19.
        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Level Query did not properly set NotificationUri",
                    levelCursor.getNotificationUri(), LevelEntry.CONTENT_URI);
        }
    }
/*
This test uses the provider to insert and then update the data. Uncomment this test to
see if your update location is functioning correctly.
*/
    public void testUpdateLevel() {
    // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createLevelValues();

        Uri locationUri = mContext.getContentResolver().
                insert(LevelEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

    // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(LevelEntry._ID, locationRowId);
        updatedValues.put(LevelEntry.COLUMN_MD5SUM, "bf0ec3ad0c8a38074639906b5a164d4d");

    // Create a cursor with observer to make sure that the content provider is notifying
    // the observers as expected
        Cursor locationCursor = mContext.getContentResolver().query(LevelEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                LevelEntry.CONTENT_URI, updatedValues, LevelEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});
        assertEquals(count, 1);

    // Test to make sure our observer is called. If not, we throw an assertion.
    //
    // Students: If your code is failing here, it means that your content provider
    // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

    // A cursor is your primary interface to the query results.
            Cursor cursor = mContext.getContentResolver().query(
                    LevelEntry.CONTENT_URI,
                    null, // projection
                    LevelEntry._ID + " = " + locationRowId,
                    null, // Values for the "where" clause
                    null // sort order
            );

            TestUtilities.validateCursor("testUpdateLocation. Error validating location entry update.",
                    cursor, updatedValues);

            cursor.close();
        }
// Make sure we can still delete after adding/updating stuff
//
// Student: Uncomment this test after you have completed writing the insert functionality
// in your provider. It relies on insertions with testInsertReadProvider, so insert and
// query functionality must also be complete before this test can be used.
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createLevelValues();

        // Register a content observer for our insert. This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(LevelEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(LevelEntry.CONTENT_URI, testValues);

        // Did our content observer get called? Students: If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted. IN THEORY. Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LevelEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating LocationEntry.",
                cursor, testValues);

        // Fantastic. Now that we have a location, add some weather!
        //ContentValues weatherValues = TestUtilities.createWeatherValues(locationRowId);
        ContentValues weatherValues = TestUtilities.createLevelValues();
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(LevelEntry.CONTENT_URI, true, tco);

        Uri weatherInsertUri = mContext.getContentResolver()
                .insert(LevelEntry.CONTENT_URI, testValues);
        assertTrue(weatherInsertUri != null);

        // Did our content observer get called? Students: If this fails, your insert weather
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = mContext.getContentResolver().query(
                LevelEntry.CONTENT_URI, // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating WeatherEntry insert.",
                weatherCursor, testValues);

        // Add the location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        weatherValues.putAll(testValues);

        // Get the joined Weather and Location data
        weatherCursor = mContext.getContentResolver().query(
                LevelEntry.buildLevelsTitle("Spaziergang mit Goethe"),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider. Error validating joined Weather and Location Data.",
                weatherCursor, weatherValues);
    }

// Make sure we can still delete after adding/updating stuff
//
// Student: Uncomment this test after you have completed writing the delete functionality
// in your provider. It relies on insertions with testInsertReadProvider, so insert and
// query functionality must also be complete before this test can be used.
    public void testDeleteRecords() {
        testInsertReadProvider();

    // Register a content observer for our location delete.
        TestUtilities.TestContentObserver levelObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(LevelEntry.CONTENT_URI, true, levelObserver);

        deleteAllRecordsFromProvider();

    // Students: If either of these fail, you most-likely are not calling the
    // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
    // delete. (only if the insertReadProvider is succeeding)
        levelObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(levelObserver);
    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertLevelValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++ ) {
            ContentValues testValues = new ContentValues();
            testValues.put(LevelEntry.COLUMN_PATH, "path" + Integer.toString(i));
            testValues.put(LevelEntry.COLUMN_TITLE, "title" + Integer.toString(i));
            testValues.put(LevelEntry.COLUMN_MD5SUM, "d1e01057deffd459f8fbc2c54132ba0" + Integer.toString(i));
            returnContentValues[i] = testValues;
        }
        return returnContentValues;
    }


// Student: Uncomment this test after you have completed writing the BulkInsert functionality
// in your provider. Note that this test will work with the built-in (default) provider
// implementation, which just inserts records one-at-a-time, so really do implement the
// BulkInsert ContentProvider function.
    public void testBulkInsert() {
        ContentValues[] bulkInsertContentValues = createBulkInsertLevelValues();

    // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver levelObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(LevelEntry.CONTENT_URI, true, levelObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(LevelEntry.CONTENT_URI, bulkInsertContentValues);

    // Students: If this fails, it means that you most-likely are not calling the
    // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
    // ContentProvider method.
        levelObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(levelObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

    // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LevelEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                LevelEntry.COLUMN_MD5SUM + " ASC" // sort order == by DATE ASCENDING
        );

    // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

    // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert. Error validating LevelEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}
