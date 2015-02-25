package de.uni_weimar.m18.exkursion.data;

import android.net.Uri;
import android.test.AndroidTestCase;

public class TestLevelContract extends AndroidTestCase {
    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final String TEST_LEVEL_TITLE = "/Ausflug zum Brunnen";

    public void testBuildWeatherLocation() {
        Uri levelUri = LevelContract.LevelEntry.buildLevelsTitle(TEST_LEVEL_TITLE);
        assertNotNull("Error: Null Uri returned. You must fill-in buildLevelsTitle in " +
                        "LevelContract.",
                levelUri);
        assertEquals("Error: Title not properly appended to the end of the Uri",
                TEST_LEVEL_TITLE, levelUri.getLastPathSegment());
        assertEquals("Error: Level title Uri doesn't match our expected result",
                levelUri.toString(),
                "content://de.uni_weimar.m18.exkursion/levels/%2FAusflug%20zum%20Brunnen");
    }
}
