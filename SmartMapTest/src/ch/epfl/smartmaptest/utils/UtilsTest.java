package ch.epfl.smartmaptest.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.test.AndroidTestCase;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.background.SettingsManager;
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

        tenDaysAgo = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));;
        tenDaysAgo.add(Calendar.DAY_OF_YEAR, -10);
    }

    public void testInTenDays() {

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
