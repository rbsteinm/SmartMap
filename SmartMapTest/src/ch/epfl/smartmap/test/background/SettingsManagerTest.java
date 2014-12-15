package ch.epfl.smartmap.test.background;

import java.util.GregorianCalendar;

import org.junit.Test;

import android.content.Context;
import android.location.Location;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.background.SettingsManager;
import ch.epfl.smartmap.util.Utils;

public class SettingsManagerTest extends AndroidTestCase {

    private Context mContext;
    private SettingsManager mManager;
    private final long mID = 123456;
    private final String mEmail = "abc@cde.com";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = new RenamingDelegatingContext(this.getContext(), "test_");
        mManager = new SettingsManager(mContext);
    }

    @Test
    public void testGetCookie() {
        mManager.setCookie("qwertzuiop");
        assertTrue(mManager.getCookie().equals("qwertzuiop"));
    }
    
    @Test
    public void testGetLastSeen() {
        mManager.setLastSeen(16000);
        assertEquals(mManager.getLastSeen(), 16000);
    }
    
    @Test
    public void testGetLocation() {
        Location loc = new Location("testprovider");
        loc.setLatitude(23);
        loc.setLongitude(45);
        mManager.setLocation(loc);
        assertTrue(mManager.getLocation().getLatitude() == 23
            && mManager.getLocation().getLongitude() == 45);
    }
    
    @Test
    public void testGetLocName() {
        mManager.setLocationName("Pripyat");
        assertTrue(mManager.getLocationName().equals("Pripyat"));
    }
    
    @Test
    public void testGetSubtitle() {
        ServiceContainer.setSettingsManager(mManager);
        mManager.setLastSeen(12345);
        mManager.setLocationName("Pripyat");
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(12345);
        String expectedResult = Utils.getLastSeenStringFromCalendar(calendar) + " "
            + getContext().getString(R.string.settings_manager_near) + " " + mManager.getLocationName();
        assertTrue(mManager.getSubtitle().equals(expectedResult));
    }
    
    @Test
    public void testGetPhoneNumber() {
        mManager.setUPhoneNumber("123123123");
        assertTrue(mManager.getUPhoneNumber().equals("123123123"));
    }

    @Test
    public void testGetEmail() {
        mManager.setEmail(mEmail);
        assertTrue(mManager.getEmail().equals(mEmail));
    }

    @Test
    public void testGetFacebookID() {
        mManager.setFacebookID(123456789);
        assertEquals(mManager.getFacebookID(), 123456789);
    }

    @Test
    public void testGetToken() {
        mManager.setToken("123456");
        assertTrue(mManager.getToken().equals("123456"));
    }

    @Test
    public void testGetUserID() {
        mManager.setUserID(mID);
        assertEquals(mManager.getUserId(), mID);
    }

    @Test
    public void testGetUserName() {
        mManager.setUserName("New Name");
        assertTrue(mManager.getUserName().equals("New Name"));
    }
    
    // Other fields in SettingsManager are covered by SettingsActivityTest
}