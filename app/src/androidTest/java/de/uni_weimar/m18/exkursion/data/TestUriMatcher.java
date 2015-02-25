package de.uni_weimar.m18.exkursion.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;


public class TestUriMatcher extends AndroidTestCase {
    private static final String TITLE = "Exkursionsausflug Nr. 1";
    // content://de.uni_weimar.m18.exkursion/levels"
    private static final Uri TEST_LEVEL_DIR = LevelContract.LevelEntry.CONTENT_URI;
    private static final Uri TEST_LEVEL_WITH_TITLE_DIR = LevelContract.LevelEntry.buildLevelsTitle(TITLE);


    public void testUriMatcher() {
        UriMatcher testMatcher = LevelProvider.buildUriMatcher();

        assertEquals("Error: The LEVEL URI was matched incorrectly.",
                testMatcher.match(TEST_LEVEL_DIR), LevelProvider.LEVEL);
        assertEquals("Error: The LEVEL WITH TITLE URI was matched incorrectly.",
                testMatcher.match(TEST_LEVEL_WITH_TITLE_DIR), LevelProvider.LEVEL_WITH_ID);
    }
}
