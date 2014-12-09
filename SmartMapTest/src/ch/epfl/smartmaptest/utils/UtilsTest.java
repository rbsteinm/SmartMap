package ch.epfl.smartmaptest.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import junit.framework.TestCase;
import ch.epfl.smartmap.util.Utils;

/**
 * @author SpicyCH
 */
public class UtilsTest extends TestCase {

    private Calendar now;
    private Calendar yesterday;
    private Calendar tomorrow;
    private Calendar inTenDays;
    private Calendar tenDaysAgo;

    @Override
    protected void setUp() throws Exception {
        now = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));

        yesterday = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        tomorrow = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);

        inTenDays = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
        inTenDays.add(Calendar.DAY_OF_YEAR, 10);

        tenDaysAgo = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));;
        tenDaysAgo.add(Calendar.DAY_OF_YEAR, -10);
        super.setUp();
    }

    public void test1() {
        assertEquals("Today", Utils.getDateString(now));
    }
}
