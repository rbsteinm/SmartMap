package ch.epfl.smartmap.test.cache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.location.Location;
import android.test.AndroidTestCase;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserContainer;

public class FriendTest extends AndroidTestCase {

    long id = 3;
    String name = "Alain";
    String phoneNumber = "0217465647";
    String email = "email@alainmilliet.ch";
    Bitmap image = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
    Location location = new Location("SmartMapServers");
    double latitude = 43.54574354;
    double longitude = 23.5479584;
    long lastSeen = 47587985;
    String locationString = "Bahamas";
    User.BlockStatus blockStatus = User.BlockStatus.UNBLOCKED;

    UserContainer container;

    @Before
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ServiceContainer.initSmartMapServices(this.getContext());
        container =
            new UserContainer(id, name, phoneNumber, email, location, locationString, image, blockStatus,
                User.FRIEND);
        container.getLocation().setLatitude(latitude);
        container.getLocation().setLongitude(longitude);
        container.getLocation().setTime(lastSeen);
    }

    @After
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // new container
        ServiceContainer.setCache(new Cache());
        UserContainer container =
            new UserContainer(id, name, phoneNumber, email, location, locationString, image, blockStatus,
                User.FRIEND);
        container.getLocation().setLatitude(latitude);
        container.getLocation().setLongitude(longitude);
        container.getLocation().setTime(lastSeen);
        // new cache
        ServiceContainer.setCache(new Cache());
    }

    @Test
    public void testCreateFriendWithNullValues() {
        container.setName(null);
        container.setImage(null);
        container.setEmail(null);
        container.setPhoneNumber(null);
        container.setBlocked(User.BlockStatus.NOT_SET);
        container.setLocation(null);
        container.setLocationString(null);
        ServiceContainer.getCache().putUser(container);
        Friend friend = (Friend) ServiceContainer.getCache().getUser(id);
        assertEquals(friend.getName(), User.NO_NAME);
        assertEquals(friend.getSearchImage(), User.NO_IMAGE);
        assertEquals(friend.getActionImage(), User.NO_IMAGE);
        assertEquals(friend.getEmail(), User.NO_EMAIL);
        assertEquals(friend.getPhoneNumber(), User.NO_PHONE_NUMBER);
        assertEquals(friend.getLocation().getLatitude(), User.NO_LATITUDE);
        assertEquals(friend.getLocation().getLongitude(), User.NO_LONGITUDE);
        assertEquals(friend.getLastSeen(), User.NO_LAST_SEEN);
        assertEquals(friend.getLocationString(), User.NO_LOCATION_STRING);
        assertEquals(friend.getBlockStatus(), User.NO_BLOCK_STATUS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFriendWithWrongId() {
        ServiceContainer.getCache().putUser(container.setId(-4));
    }

    @Test
    public void testGetValues() {
        Friend friend = (Friend) ServiceContainer.getCache().getUser(id);
        assertEquals(friend.getId(), id);
        assertEquals(friend.getName(), name);
        assertEquals(friend.getPhoneNumber(), phoneNumber);
        assertEquals(friend.getEmail(), email);
        assertEquals(friend.getLocation().getLatitude(), location.getLatitude());
        assertEquals(friend.getLocation().getLongitude(), location.getLongitude());
        assertEquals(friend.getLastSeen().getTimeInMillis(), lastSeen);
        assertEquals(friend.getLocationString(), locationString);
        assertEquals(friend.getLatLng().latitude, location.getLatitude());
        assertEquals(friend.getLatLng().longitude, location.getLongitude());
        assertEquals(friend.getFriendship(), User.FRIEND);
        assertEquals(friend.getBlockStatus(), blockStatus);
    }
}