package ch.epfl.smartmap.test.cache;

import static ch.epfl.smartmap.test.database.MockContainers.ALAIN;
import static ch.epfl.smartmap.test.database.MockContainers.FOOTBALL_TOURNAMENT;
import static ch.epfl.smartmap.test.database.MockContainers.JULIEN;
import static ch.epfl.smartmap.test.database.MockContainers.NULL_EVENT_VALUES;
import static ch.epfl.smartmap.test.database.MockContainers.POLYLAN;
import static ch.epfl.smartmap.test.database.MockContainers.ROBIN;
import static ch.epfl.smartmap.test.database.MockContainers.WRONG_USER_VALUES;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.test.AndroidTestCase;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.background.SettingsManager;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserContainer;
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

import com.google.common.collect.Sets;

/**
 * @author jfperren
 */
@RunWith(MockitoJUnitRunner.class)
public class CacheTest extends AndroidTestCase {

    private DatabaseHelper correctDB;
    private DatabaseHelper incorrectDB;
    private NetworkSmartMapClient correctClient;
    private NetworkSmartMapClient incorrectClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ServiceContainer.forceInitSmartMapServices(this.getContext());

        // Create mock Settings
        SettingsManager mockSettings = Mockito.mock(SettingsManager.class);
        // Act as if julien is self
        Mockito.doReturn(JULIEN.getId()).when(mockSettings).getUserId();
        Mockito.doReturn(JULIEN.getName()).when(mockSettings).getUserName();
        Mockito.doReturn(JULIEN.getLocation()).when(mockSettings).getLocation();
        Mockito.doReturn(this.getContext()).when(mockSettings).getContext();
        // Add as Service
        ServiceContainer.setSettingsManager(mockSettings);

        // create Mock network client
        correctClient = Mockito.mock(NetworkSmartMapClient.class);
        // Return friend ids
        Mockito.doReturn(Arrays.asList(ALAIN.getId())).when(correctClient).getFriendsIds();
        // Return user infos
        Mockito.doReturn(ALAIN).when(correctClient).getUserInfo(ALAIN.getId());
        Mockito.doReturn(JULIEN).when(correctClient).getUserInfo(JULIEN.getId());
        Mockito.doReturn(ROBIN).when(correctClient).getUserInfo(ROBIN.getId());
        // Return listFriendPos
        Mockito
            .doReturn(
                Arrays.asList(UserContainer.newEmptyContainer().setLocation(ALAIN.getLocation())
                    .setId(ALAIN.getId()).setName(ALAIN.getName()).setFriendship(ALAIN.getFriendship())))
            .when(correctClient).listFriendsPos();
        // Return image
        Mockito.doReturn(Bitmap.createBitmap(1, 2, Config.ALPHA_8)).when(correctClient)
            .getProfilePicture(JULIEN.getId());
        Mockito.doReturn(new ArrayList<Long>()).when(correctClient)
            .getPublicEvents(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong());

        // create Mock incorrect network client
        incorrectClient = Mockito.mock(NetworkSmartMapClient.class);
        // Return friend ids
        Mockito.doReturn(Arrays.asList(WRONG_USER_VALUES.getId())).when(correctClient).getFriendsIds();
        // Return user infos
        Mockito.doReturn(WRONG_USER_VALUES).when(correctClient).getUserInfo(WRONG_USER_VALUES.getId());

        // create Mock correct DB
        correctDB = Mockito.mock(DatabaseHelper.class);
        Mockito.doReturn(Sets.newHashSet(JULIEN, ALAIN, ROBIN)).when(correctDB).getAllUsers();
        Mockito.doReturn(Sets.newHashSet(POLYLAN, FOOTBALL_TOURNAMENT)).when(correctDB).getAllEvents();

        // create Mock incorrect DB
        incorrectDB = Mockito.mock(DatabaseHelper.class);
        Mockito.doReturn(Sets.newHashSet(WRONG_USER_VALUES)).when(incorrectDB).getAllUsers();
        Mockito.doReturn(Sets.newHashSet(NULL_EVENT_VALUES)).when(incorrectDB).getAllEvents();
    }

    @Test
    public void testGetExistingEventsReturnSetWithOnlyValidUsers() {
        Cache cache = new Cache();
        cache.putEvent(POLYLAN);
        assertEquals(1, cache.getEvents(Sets.newHashSet(POLYLAN.getId(), User.NO_ID)).size());
    }

    @Test
    public void testGetExistingUsersReturnSetWithOnlyValidUsers() {
        Cache cache = new Cache();
        cache.putUser(JULIEN);
        assertEquals(1, cache.getUsers(Sets.newHashSet(JULIEN.getId(), User.NO_ID)).size());
    }

    @Test
    public void testGetNonExistingEventReturnsNull() {
        assertNull(new Cache().getEvent(3));
    }

    @Test
    public void testGetNonExistingUserReturnsNull() {
        assertNull(new Cache().getUser(3));
    }

    @Test
    public void testGetNonExistingUsersReturnEmptySet() {
        assertTrue(new Cache().getEvents(Sets.newHashSet((long) 2, (long) 3)).isEmpty());
    }

    @Test
    public void testInitFromCorrectDatabase() {
        Cache cache = new Cache();
        cache.initFromDatabase(correctDB);

        assertEquals(3, cache.getAllUsers().size());
        assertEquals(1, cache.getAllFriends().size());

        assertNotNull(cache.getUser(JULIEN.getId()));
        assertNotNull(cache.getUser(ALAIN.getId()));
        assertNotNull(cache.getUser(ROBIN.getId()));

        assertNotNull(cache.getEvent(POLYLAN.getId()));
        assertNotNull(cache.getEvent(FOOTBALL_TOURNAMENT.getId()));

        assertNotNull(cache.getUser(POLYLAN.getCreatorContainer().getId()));
        assertNotNull(cache.getUser(FOOTBALL_TOURNAMENT.getCreatorContainer().getId()));

        assertNotNull(cache.getSelf());
    }

    @Test
    public void testInitFromDatabaseWithIncorrectEvent() {
        Cache cache = new Cache();
        cache.initFromDatabase(incorrectDB);

        assertNull(cache.getEvent(NULL_EVENT_VALUES.getId()));
    }

    @Test
    public void testInitFromDatabaseWithIncorrectUser() {
        Cache cache = new Cache();
        cache.initFromDatabase(incorrectDB);

        assertNull(cache.getUser(WRONG_USER_VALUES.getId()));
    }

    @Test
    public void testPutEventWithExistingEventCallsUpdate() {
        Cache cache = new Cache();
        cache.putEvent(POLYLAN);
        assertEquals(cache.getEvent(POLYLAN.getId()).getName(), POLYLAN.getName());
        cache.putEvent(cache.getEvent(POLYLAN.getId()).getContainerCopy().setName("Foot"));
        assertEquals(cache.getEvent(POLYLAN.getId()).getName(), "Foot");
    }

    @Test
    public void testPutEventWithNewEventDoesntReturnNull() {
        Cache cache = new Cache();
        cache.putEvent(POLYLAN);

        assertNotNull(cache.getEvent(POLYLAN.getId()));
    }

    @Test
    public void testPutUserWithExistingUserCallsUpdate() {
        Cache cache = new Cache();
        cache.putUser(ALAIN);
        assertEquals(cache.getUser(ALAIN.getId()).getName(), ALAIN.getName());
        cache.putUser(cache.getUser(ALAIN.getId()).getContainerCopy().setName("Robert"));
        assertEquals(cache.getUser(ALAIN.getId()).getName(), "Robert");
    }

    @Test
    public void testPutUserWithNewUserDoesntReturnNull() {
        Cache cache = new Cache();
        cache.putUser(ALAIN);

        assertNotNull(cache.getUser(ALAIN.getId()));
    }

    @Test
    public void testUpdateFromNetworkWithCorrectUsers() throws SmartMapClientException {
        Cache cache = new Cache();
        ServiceContainer.setNetworkClient(correctClient);
        cache.updateFromNetwork(correctClient);

        assertEquals(2, cache.getAllUsers().size());
        assertEquals(1, cache.getAllFriends().size());

        assertNotNull(cache.getUser(JULIEN.getId()));
        assertNotNull(cache.getUser(ALAIN.getId()));
        assertNull(cache.getUser(ROBIN.getId()));

        assertFalse(cache.getSelf().getActionImage().sameAs(User.NO_IMAGE));

        assertNotNull(cache.getSelf());
    }

    @Test
    public void testUpdateFromNetworkWithIncorrectUsers() throws SmartMapClientException {
        Cache cache = new Cache();
        cache.updateFromNetwork(incorrectClient);

        assertEquals(0, cache.getAllUsers().size());
        assertEquals(0, cache.getAllFriends().size());

        assertNull(cache.getUser(WRONG_USER_VALUES.getId()));
    }
}
