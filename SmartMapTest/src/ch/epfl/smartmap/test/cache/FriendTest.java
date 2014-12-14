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
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserContainer;

public class FriendTest extends AndroidTestCase {

    long id = 30;
    String name = "Alain";
    String otherName = "Julien";
    String phoneNumber = "0217465647";
    String otherPhoneNumber = "7508758798";
    String email = "email@alainmilliet.ch";
    String otherEmail = "julien@perrenoud.ch";
    Bitmap image = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
    Bitmap otherImage = Bitmap.createBitmap(2, 1, Config.ALPHA_8);
    Location location = new Location(Displayable.PROVIDER_NAME);
    Location otherLocation = new Location(Displayable.PROVIDER_NAME);
    double latitude = 43.54574354;
    double longitude = 23.5479584;
    double otherLatitude = 67.48908490;
    double otherLongitude = 12.489048;
    long lastSeen = 47587985;
    long otherLastSeen = 44987984;
    String locationString = "Bahamas";
    String otherLocationString = "Lausanne";
    User.BlockStatus blockStatus = User.BlockStatus.UNBLOCKED;
    User.BlockStatus otherBlockStatus = User.BlockStatus.BLOCKED;

    UserContainer container;
    UserContainer otherContainer;

    @Before
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ServiceContainer.forceInitSmartMapServices(this.getContext());

        this.initContainers();
    }

    @After
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // new container
        this.initContainers();
        // new cache
        ServiceContainer.setCache(new Cache());
    }

    @Test
    public void testCreateFriendWithNullValues() {
        container.setName(null);
        container.setImage(null);
        container.setEmail(null);
        container.setPhoneNumber(null);
        container.setBlocked(null);
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
        ServiceContainer.getCache().putUser(container);
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

    @Test
    public void testUpdateWithGoodParameters() {
        ServiceContainer.getCache().putUser(container);
        Friend friend = (Friend) ServiceContainer.getCache().getUser(id);
        friend.update(otherContainer);

        assertEquals(otherName, friend.getName());
        assertEquals(otherEmail, friend.getEmail());
        assertEquals(otherPhoneNumber, friend.getPhoneNumber());
        assertEquals(otherLatitude, friend.getLocation().getLatitude());
        assertEquals(otherLongitude, friend.getLocation().getLongitude());
        assertEquals(otherLastSeen, friend.getLastSeen().getTimeInMillis());
        assertTrue(otherImage.sameAs(friend.getActionImage()));
        assertEquals(otherLocationString, friend.getLocationString());
        assertEquals(otherBlockStatus, friend.getBlockStatus());
    }

    @Test
    public void testUpdateWithSameParameters() {
        ServiceContainer.getCache().putUser(container);
        Friend friend = (Friend) ServiceContainer.getCache().getUser(id);
        assertFalse(friend.update(friend.getContainerCopy()));
    }

    @Test
    public void testUpdateWithUnsetParameters() {
        ServiceContainer.getCache().putUser(container);
        Friend friend = (Friend) ServiceContainer.getCache().getUser(id);

        UserContainer unsetParameters = User.NOBODY.getContainerCopy();
        unsetParameters.setFriendship(User.NO_FRIENDSHIP);
        unsetParameters.setId(friend.getId());

        assertFalse(friend.update(unsetParameters));
    }

    @Test
    public void testUpdateWithWrongId() {
        ServiceContainer.getCache().putUser(container);
        Friend friend = (Friend) ServiceContainer.getCache().getUser(id);
        try {
            friend.update(User.NOBODY.getContainerCopy());
            fail();
        } catch (IllegalArgumentException e) {
            // Success
        }
    }

    @Test
    public void testUpdateWithWrongType() {
        ServiceContainer.getCache().putUser(container);
        Friend friend = (Friend) ServiceContainer.getCache().getUser(id);
        try {
            friend.update(friend.getContainerCopy().setFriendship(User.STRANGER));
            fail();
        } catch (IllegalArgumentException e) {
            // Success
        }
    }

    private void initContainers() {
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTime(lastSeen);

        otherLocation.setLatitude(otherLatitude);
        otherLocation.setLongitude(otherLongitude);
        otherLocation.setTime(otherLastSeen);

        container =
            new UserContainer(id, name, phoneNumber, email, location, locationString, image, blockStatus,
                User.FRIEND);
        otherContainer =
            new UserContainer(id, otherName, otherPhoneNumber, otherEmail, otherLocation,
                otherLocationString, otherImage, otherBlockStatus, User.FRIEND);
    }
}