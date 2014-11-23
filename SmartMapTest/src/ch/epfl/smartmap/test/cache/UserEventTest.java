package ch.epfl.smartmap.test.cache;

import java.util.GregorianCalendar;

import org.junit.Test;

import android.location.Location;
import android.test.AndroidTestCase;
import ch.epfl.smartmap.cache.UserEvent;

public class UserEventTest extends AndroidTestCase {

    @Test
    public void testUserEvent() {
        Location testLoc = new Location("");
        testLoc.setLatitude(45.67);
        UserEvent evt =
            new UserEvent("testname", 1234, "testUserName", new GregorianCalendar(), new GregorianCalendar(), testLoc);
        assertTrue(evt.getLocation().getLatitude() == 45.67);
    }
}
