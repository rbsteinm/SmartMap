package ch.epfl.smartmap.test.cache;

import static ch.epfl.smartmap.test.database.MockContainers.ALAIN;
import static ch.epfl.smartmap.test.database.MockContainers.JULIEN;
import static ch.epfl.smartmap.test.database.MockContainers.ROBIN;
import static ch.epfl.smartmap.test.database.MockContainers.WRONG_USER_VALUES;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import android.test.AndroidTestCase;
import ch.epfl.smartmap.background.ServiceContainer;
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

    @Override
    protected void setUp() throws Exception {
        ServiceContainer.initSmartMapServices(this.getContext());
        super.setUp();
    }

    @Test
    public void testGetExistingUsersReturnSetWithOnlyValidUsers() {
        Cache cache = new Cache();
        cache.putUser(JULIEN);
        assertEquals(1, cache.getUsers(Sets.newHashSet(JULIEN.getId(), User.NO_ID)).size());
    }

    @Test
    public void testGetNonExistingUserReturnsNull() {
        assertNull(new Cache().getUser(3));
    }

    @Test
    public void testGetNonExistingUsersReturnEmptySet() {
        assertTrue(new Cache().getUsers(Sets.newHashSet((long) 2, (long) 3)).isEmpty());
    }

    @Test
    public void testInitFromDatabaseWithCorrectUsers() {
        // create Mock correct DB
        DatabaseHelper correctDB = Mockito.mock(DatabaseHelper.class);
        Mockito.doReturn(Sets.newHashSet(JULIEN, ALAIN, ROBIN)).when(correctDB).getAllUsers();

        Cache cache = new Cache();
        ServiceContainer.setDatabaseHelper(correctDB);
        cache.initFromDatabase(correctDB);

        assertEquals(3, cache.getAllUsers().size());
        assertEquals(1, cache.getAllFriends().size());

        assertNotNull(cache.getUser(JULIEN.getId()));
        assertNotNull(cache.getUser(ALAIN.getId()));
        assertNotNull(cache.getUser(ROBIN.getId()));

        assertNotNull(cache.getSelf());
    }

    @Test
    public void testInitFromDatabaseWithIncorrectUser() {
        // create Mock correct DB
        DatabaseHelper incorrectDB = Mockito.mock(DatabaseHelper.class);
        Mockito.doReturn(Sets.newHashSet(WRONG_USER_VALUES)).when(incorrectDB).getAllUsers();

        Cache cache = new Cache();
        cache.initFromDatabase(incorrectDB);

        assertNull(cache.getUser(WRONG_USER_VALUES.getId()));
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
        cache.putUser(JULIEN);

        assertNotNull(cache.getUser(JULIEN.getId()));
    }

    @Test
    public void testUpdateFromNetworkWithCorrectUsers() throws SmartMapClientException {
        // create Mock network client
        NetworkSmartMapClient correctClient = Mockito.mock(NetworkSmartMapClient.class);
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
        Cache cache = new Cache();
        ServiceContainer.setNetworkClient(correctClient);
        cache.updateFromNetwork(correctClient);

        assertEquals(2, cache.getAllUsers().size());
        assertEquals(1, cache.getAllFriends().size());

        assertNotNull(cache.getUser(JULIEN.getId()));
        assertNotNull(cache.getUser(ALAIN.getId()));
        assertNull(cache.getUser(ROBIN.getId()));

        assertNotNull(cache.getSelf());
    }

    @Test
    public void testUpdateFromNetworkWithIncorrectUsers() throws SmartMapClientException {
        // create Mock network client
        NetworkSmartMapClient correctClient = Mockito.mock(NetworkSmartMapClient.class);
        // Return friend ids
        Mockito.doReturn(Arrays.asList(WRONG_USER_VALUES.getId())).when(correctClient).getFriendsIds();
        // Return user infos
        Mockito.doReturn(WRONG_USER_VALUES).when(correctClient).getUserInfo(WRONG_USER_VALUES.getId());

        // Return listFriendPos
        Mockito
            .doReturn(
                Arrays.asList(UserContainer.newEmptyContainer().setLocation(WRONG_USER_VALUES.getLocation())
                    .setId(WRONG_USER_VALUES.getId()).setName(WRONG_USER_VALUES.getName())
                    .setFriendship(WRONG_USER_VALUES.getFriendship()))).when(correctClient).listFriendsPos();

        Cache cache = new Cache();
        cache.updateFromNetwork(correctClient);

        assertEquals(0, cache.getAllUsers().size());
        assertEquals(0, cache.getAllFriends().size());

        assertNull(cache.getUser(WRONG_USER_VALUES.getId()));
    }
}
