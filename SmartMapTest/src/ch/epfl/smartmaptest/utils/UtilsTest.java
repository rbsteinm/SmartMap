package ch.epfl.smartmaptest.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

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

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ServiceContainer.setSettingsManager(new SettingsManager(this.getContext()));

        now = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));

        yesterday = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        tomorrow = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);

        inTenDays = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
        inTenDays.add(Calendar.DAY_OF_YEAR, 10);

        tenDaysAgo = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
        tenDaysAgo.add(Calendar.DAY_OF_YEAR, -10);

        myBirthday = new GregorianCalendar(1993, 8, 16);

        morning = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
        morning.set(Calendar.HOUR_OF_DAY, 8);
        morning.set(Calendar.MINUTE, 5);

        afternoon = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
        afternoon.set(Calendar.HOUR_OF_DAY, 17);
        afternoon.set(Calendar.MINUTE, 7);

        firstJan2015 = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
        firstJan2015.set(Calendar.YEAR, 2015);
        firstJan2015.set(Calendar.MONTH, 0);
        firstJan2015.set(Calendar.DAY_OF_YEAR, 1);

        epfl = new Location("");
        epfl.setLatitude(46.519056);
        epfl.setLongitude(6.566758);
    }

    public void testCitiyForLocation() {
        assertEquals("Ecublens", Utils.getCityFromLocation(epfl));
    }

    public void testCountryForLocation() {
        String result = Utils.getCountryFromLocation(epfl);

        if ("Switzerland".equals(result) || "Suisse".equals(result)) {
            // good
        } else {
            fail("EPFL wasn't in switzerland");
        }
    }

    public void testGetDefaultValueForUnknownPlaces() {
        Location somewhereVeryLost = new Location("");
        somewhereVeryLost.setLatitude(0);
        somewhereVeryLost.setLongitude(0);

        assertEquals(Displayable.NO_LOCATION_STRING, Utils.getCityFromLocation(somewhereVeryLost));

        assertEquals(Displayable.NO_LOCATION_STRING, Utils.getCountryFromLocation(somewhereVeryLost));

    }

    public void testJanuary() {
        // Regression test for #82
        assertEquals("01.01.2015", Utils.getDateString(firstJan2015));
    }

    public void testLastSeen100Years() {
        Calendar hundredYearsAgo = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
        hundredYearsAgo.add(Calendar.YEAR, -100);

        assertEquals(
            ServiceContainer.getSettingsManager().getContext()
                .getString(R.string.utils_never_seen_on_smartmap),
            Utils.getLastSeenStringFromCalendar(hundredYearsAgo));
    }

    public void testLastSeenFiveMins() {
        Calendar fiveMinsAgo = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
        fiveMinsAgo.add(Calendar.MINUTE, -5);

        assertEquals(
            "5 " + ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_minutes_ago),
            Utils.getLastSeenStringFromCalendar(fiveMinsAgo));
    }

    public void testLastSeenNow() {
        assertEquals(ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_now),
            Utils.getLastSeenStringFromCalendar(now));
    }

    public void testLastSeenTenDays() {
        assertEquals(
            "10 " + ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_days_ago),
            Utils.getLastSeenStringFromCalendar(tenDaysAgo));
    }

    public void testMyBirthday() {
        assertEquals("16.09.1993", Utils.getDateString(myBirthday));
    }

    public void testTimeAfternoon() {
        assertEquals("17:07", Utils.getTimeString(afternoon));
    }

    public void testTimeMorning() {
        assertEquals("08:05", Utils.getTimeString(morning));
    }

    public void testToday() {
        assertEquals(ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_today),
            Utils.getDateString(now));
    }

    public void testTomorrow() {
        assertEquals(ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_tomorrow),
            Utils.getDateString(tomorrow));
    }

    public void testYesterday() {
        assertEquals(ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_yesterday),
            Utils.getDateString(yesterday));
    }
}
