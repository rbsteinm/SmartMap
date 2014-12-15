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
import ch.epfl.smartmap.cache.Stranger;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserContainer;

/**
 * @author jfperren
 */
public class StrangerTest extends AndroidTestCase {

    long id = 3;
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
    public void testCreateStrangerWithNullValues() {
        container.setName(null);
        container.setImage(null);
        container.setEmail(null);
        ServiceContainer.getCache().putUser(container);
        Stranger stranger = (Stranger) ServiceContainer.getCache().getUser(id);
        assertEquals(stranger.getName(), User.NO_NAME);
        assertEquals(stranger.getSearchImage(), User.NO_IMAGE);
        assertEquals(stranger.getActionImage(), User.NO_IMAGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateStrangerWithWrongId() {
        ServiceContainer.getCache().putUser(container.setId(-4));
    }

    @Test
    public void testGetValues() {
        ServiceContainer.getCache().putUser(container);
        Stranger stranger = (Stranger) ServiceContainer.getCache().getUser(id);
        assertEquals(stranger.getId(), id);
        assertEquals(stranger.getName(), name);
        assertTrue(stranger.getActionImage().sameAs(image));
        assertEquals(stranger.getFriendship(), User.STRANGER);
    }

    @Test
    public void testUnsupportedOperationMarkerExceptions() {
        ServiceContainer.getCache().putUser(container);
        Stranger stranger = (Stranger) ServiceContainer.getCache().getUser(id);

        try {
            stranger.getMarkerIcon(this.getContext());
            fail();
        } catch (UnsupportedOperationException e) {
            // Success
        }
    }

    @Test
    public void testUpdateWithGoodParameters() {
        ServiceContainer.getCache().putUser(container);
        Stranger stranger = (Stranger) ServiceContainer.getCache().getUser(id);
        stranger.update(otherContainer);

        assertEquals(otherName, stranger.getName());
        assertEquals(User.NO_LATITUDE, stranger.getLocation().getLatitude());
        assertEquals(User.NO_LONGITUDE, stranger.getLocation().getLongitude());
        assertTrue(otherImage.sameAs(stranger.getActionImage()));
        assertEquals(User.NO_LOCATION_STRING, stranger.getLocationString());
        assertEquals(User.NO_BLOCK_STATUS, stranger.getBlockStatus());
    }

    @Test
    public void testUpdateWithSameParameters() {
        ServiceContainer.getCache().putUser(container);
        Stranger stranger = (Stranger) ServiceContainer.getCache().getUser(id);
        assertFalse(stranger.update(stranger.getContainerCopy()));
    }

    @Test
    public void testUpdateWithUnsetParameters() {
        ServiceContainer.getCache().putUser(container);
        Stranger stranger = (Stranger) ServiceContainer.getCache().getUser(id);

        UserContainer unsetParameters = User.NOBODY.getContainerCopy();
        unsetParameters.setFriendship(User.NO_FRIENDSHIP);
        unsetParameters.setId(stranger.getId());

        assertFalse(stranger.update(unsetParameters));
    }

    @Test
    public void testUpdateWithWrongFriendship() {
        ServiceContainer.getCache().putUser(container);
        Stranger stranger = (Stranger) ServiceContainer.getCache().getUser(id);
        try {
            stranger.update(stranger.getContainerCopy().setFriendship(User.FRIEND));
            fail();
        } catch (IllegalArgumentException e) {
            // Success
        }
    }

    @Test
    public void testUpdateWithWrongId() {
        ServiceContainer.getCache().putUser(container);
        Stranger stranger = (Stranger) ServiceContainer.getCache().getUser(id);
        try {
            stranger.update(User.NOBODY.getContainerCopy());
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
                User.STRANGER);
        otherContainer =
            new UserContainer(id, otherName, otherPhoneNumber, otherEmail, otherLocation,
                otherLocationString, otherImage, otherBlockStatus, User.STRANGER);
    }
}
