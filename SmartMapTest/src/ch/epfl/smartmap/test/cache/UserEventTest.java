package ch.epfl.smartmap.test.cache;

import java.util.GregorianCalendar;

import org.junit.Test;

import ch.epfl.smartmap.cache.UserEvent;

import android.location.Location;
import android.test.AndroidTestCase;

public class UserEventTest extends AndroidTestCase {

    @Test
    public void testUserEvent() {
        Location testLoc = new Location("");
        testLoc.setLatitude(1.123);
        testLoc.setLongitude(3.321);
        UserEvent evt = new UserEvent("testname", 1234, new GregorianCalendar(), new GregorianCalendar(), testLoc);
    }
}
