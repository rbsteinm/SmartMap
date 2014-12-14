package ch.epfl.smartmap.test.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;

import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.location.Location;
import android.test.AndroidTestCase;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.background.SettingsManager;
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.util.Utils;

/**
 * @author SpicyCH
 */
public class UtilsTest extends AndroidTestCase {

    private Calendar now;
    private Calendar yesterday;
    private Calendar tomorrow;
    private Calendar inTenDays;
    private Calendar tenDaysAgo;
    private Calendar myBirthday;
    private Calendar morning;
    private Calendar afternoon;
    private Calendar firstJan2015;

    private Location epfl;

    private boolean floatArrayEquals(float[] a1, float[] a2) {
        if (a1.length != a2.length) {
            return false;
        } else {
            for (int i = 0; i < a1.length; i++) {
                if (a1[i] != a2[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ServiceContainer.setSettingsManager(new SettingsManager(this.getContext()));

        now = GregorianCalendar.getInstance(TimeZone.getTimeZone(Utils.GMT_SWITZERLAND));

        yesterday = GregorianCalendar.getInstance(TimeZone.getTimeZone(Utils.GMT_SWITZERLAND));
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        tomorrow = GregorianCalendar.getInstance(TimeZone.getTimeZone(Utils.GMT_SWITZERLAND));
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);

        inTenDays = GregorianCalendar.getInstance(TimeZone.getTimeZone(Utils.GMT_SWITZERLAND));
        inTenDays.add(Calendar.DAY_OF_YEAR, 10);

        tenDaysAgo = GregorianCalendar.getInstance(TimeZone.getTimeZone(Utils.GMT_SWITZERLAND));
        tenDaysAgo.add(Calendar.DAY_OF_YEAR, -10);

        myBirthday = new GregorianCalendar(1993, 8, 16);

        morning = GregorianCalendar.getInstance(TimeZone.getTimeZone(Utils.GMT_SWITZERLAND));
        morning.set(Calendar.HOUR_OF_DAY, 8);
        morning.set(Calendar.MINUTE, 5);

        afternoon = GregorianCalendar.getInstance(TimeZone.getTimeZone(Utils.GMT_SWITZERLAND));
        afternoon.set(Calendar.HOUR_OF_DAY, 17);
        afternoon.set(Calendar.MINUTE, 7);

        firstJan2015 = GregorianCalendar.getInstance(TimeZone.getTimeZone(Utils.GMT_SWITZERLAND));
        firstJan2015.set(Calendar.YEAR, 2015);
        firstJan2015.set(Calendar.MONTH, 0);
        firstJan2015.set(Calendar.DAY_OF_YEAR, 1);

        epfl = new Location("");
        epfl.setLatitude(46.519056);
        epfl.setLongitude(6.566758);
    }

    @Test
    public void testCityForLocation() {
        assertEquals("Ecublens", Utils.getCityFromLocation(epfl));
    }

    @Test
    public void testCountryForLocation() {
        String result = Utils.getCountryFromLocation(epfl);

        if ("Switzerland".equals(result) || "Suisse".equals(result)) {
            // good
        } else {
            fail("EPFL wasn't in switzerland");
        }
    }

    @Test
    public void testGetColorInInterval1() {
        double value = 0.0;
        double startValue = 1.0;
        double endValue = 2.0;
        int startColor = Color.RED;
        int endColor = Color.BLUE;

        assertEquals(Color.RED, Utils.getColorInInterval(value, startValue, endValue, startColor, endColor));
    }

    @Test
    public void testGetColorInInterval2() {
        double value = 3.0;
        double startValue = 1.0;
        double endValue = 2.0;
        int startColor = Color.RED;
        int endColor = Color.BLUE;

        assertEquals(Color.BLUE, Utils.getColorInInterval(value, startValue, endValue, startColor, endColor));
    }

    @Test
    public void testGetColorInInterval3() {
        double value = 0.0;
        double startValue = 2.0;
        double endValue = 1.0;
        int startColor = Color.RED;
        int endColor = Color.BLUE;

        assertEquals(Color.BLUE, Utils.getColorInInterval(value, startValue, endValue, startColor, endColor));
    }

    @Test
    public void testGetColorInInterval4() {
        double value = 1.0;
        double startValue = 0.0;
        double endValue = 2.0;
        int startColor = Color.BLACK;
        int endColor = Color.GRAY;

        assertEquals(0xff444444, Utils.getColorInInterval(value, startValue, endValue, startColor, endColor));
    }

    @Test
    public void testGetDefaultValueForUnknownPlaces() {
        Location somewhereVeryLost = new Location("");
        somewhereVeryLost.setLatitude(0);
        somewhereVeryLost.setLongitude(0);

        assertEquals(Displayable.NO_LOCATION_STRING, Utils.getCityFromLocation(somewhereVeryLost));

        assertEquals(Displayable.NO_LOCATION_STRING, Utils.getCountryFromLocation(somewhereVeryLost));

    }

    @Test
    public void testGetMatrixForColor1() {
        float[] correct = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0};

        ColorMatrix result = Utils.getMatrixForColor(Color.RED);

        assertTrue(this.floatArrayEquals(correct, result.getArray()));
    }

    @Test
    public void testGetMatrixForColor2() {
        float[] correct = {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0};

        ColorMatrix result = Utils.getMatrixForColor(Color.WHITE);

        assertTrue(this.floatArrayEquals(correct, result.getArray()));
    }

    @Test
    public void testGetMatrixForColor3() {
        float[] correct = {0.2f, 0, 0, 0, 0, 0, 0.4f, 0, 0, 0, 0, 0, 0.8f, 0, 0, 0, 0, 0, 1, 0};

        ColorMatrix result = Utils.getMatrixForColor(Color.argb(1, 51, 102, 204));

        assertTrue(this.floatArrayEquals(correct, result.getArray()));
    }

    @Test
    public void testJanuary() {
        // Regression test for #82
        assertEquals("01.01.2015", Utils.getDateString(firstJan2015));
    }

    @Test
    public void testLastSeen100Years() {
        Calendar hundredYearsAgo = GregorianCalendar.getInstance(TimeZone.getTimeZone(Utils.GMT_SWITZERLAND));
        hundredYearsAgo.add(Calendar.YEAR, -100);

        assertEquals(ServiceContainer.getSettingsManager().getContext()
            .getString(R.string.utils_never_seen_on_smartmap), Utils.getLastSeenStringFromCalendar(hundredYearsAgo));
    }

    @Test
    public void testLastSeenFiveMins() {
        Calendar fiveMinsAgo = GregorianCalendar.getInstance(TimeZone.getTimeZone(Utils.GMT_SWITZERLAND));
        fiveMinsAgo.add(Calendar.MINUTE, -5);

        assertEquals("5 " + ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_minutes_ago),
            Utils.getLastSeenStringFromCalendar(fiveMinsAgo));
    }

    @Test
    public void testLastSeenNow() {
        assertEquals(ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_now),
            Utils.getLastSeenStringFromCalendar(now));
    }

    @Test
    public void testLastSeenTenDays() {
        assertEquals("10 " + ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_days_ago),
            Utils.getLastSeenStringFromCalendar(tenDaysAgo));
    }

    @Test
    public void testMyBirthday() {
        assertEquals("16.09.1993", Utils.getDateString(myBirthday));
    }

    @Test
    public void testPrintDistanceToMeKm() {
        Location me = new Location("");
        me.setLongitude(6.6483783);
        me.setLatitude(46.539441);

        ServiceContainer.getSettingsManager().setLocation(me);

        assertEquals("6.7 " + ServiceContainer.getSettingsManager().getContext().getString(R.string.symbol_km) + " "
            + ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_away_from_you),
            Utils.printDistanceToMe(epfl));
    }

    @Test
    public void testPrintDistanceToMeMeters() {
        Location me = new Location("");
        me.setLongitude(6.569116115570068);
        me.setLatitude(46.520293125905276);

        ServiceContainer.getSettingsManager().setLocation(me);

        assertEquals(
            "227 " + ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_meters_away_from_you),
            Utils.printDistanceToMe(epfl));
    }

    @Test
    public void testTimeAfternoon() {
        assertEquals("17:07", Utils.getTimeString(afternoon));
    }

    @Test
    public void testTimeMorning() {
        assertEquals("08:05", Utils.getTimeString(morning));
    }

    @Test
    public void testToday() {
        assertEquals(ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_today),
            Utils.getDateString(now));
    }

    @Test
    public void testTomorrow() {
        assertEquals(ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_tomorrow),
            Utils.getDateString(tomorrow));
    }

    @Test
    public void testYesterday() {
        assertEquals(ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_yesterday),
            Utils.getDateString(yesterday));
    }
}
