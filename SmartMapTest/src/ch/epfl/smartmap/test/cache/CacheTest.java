package ch.epfl.smartmap.test.cache;

import static ch.epfl.smartmap.test.database.MockContainers.ALAIN;
import static ch.epfl.smartmap.test.database.MockContainers.FAMILY;
import static ch.epfl.smartmap.test.database.MockContainers.FOOTBALL_TOURNAMENT;
import static ch.epfl.smartmap.test.database.MockContainers.JULIEN;
import static ch.epfl.smartmap.test.database.MockContainers.NULL_EVENT_VALUES;
import static ch.epfl.smartmap.test.database.MockContainers.POLYLAN;
import static ch.epfl.smartmap.test.database.MockContainers.POLYLAN_EVENT_INVITATION;
import static ch.epfl.smartmap.test.database.MockContainers.ROBIN;
import static ch.epfl.smartmap.test.database.MockContainers.ROBIN_FRIEND_INVITATION;
import static ch.epfl.smartmap.test.database.MockContainers.WRONG_USER_VALUES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;

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
import ch.epfl.smartmap.cache.Filter;
import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.cache.InvitationContainer;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserContainer;
import ch.epfl.smartmap.callbacks.NetworkRequestCallback;
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.listeners.CacheListener;
import ch.epfl.smartmap.listeners.OnCacheListener;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClient;
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
    public void testAcceptInvitation() throws SmartMapClientException, Exception {
        SmartMapClient mockNetClient = Mockito.mock(NetworkSmartMapClient.class);
        Mockito.doReturn(ROBIN).when(mockNetClient).acceptInvitation(ROBIN.getId());

        ServiceContainer.setNetworkClient(mockNetClient);

        final Cache cache = new Cache();

        cache.putUser(ROBIN);

        InvitationContainer invitRobin =
            new InvitationContainer(1, ROBIN, null, Invitation.UNREAD,
                new GregorianCalendar().getTimeInMillis(), Invitation.FRIEND_INVITATION);

        cache.putInvitation(invitRobin);

        Invitation invitation = cache.getInvitation(1);

        cache.acceptInvitation(invitation, new NetworkRequestCallback<Void>() {
            @Override
            public void onFailure(Exception e) {
                fail(); // Should not fail !
            }

            @Override
            public void onSuccess(Void result) {
                assertEquals(User.FRIEND, cache.getUser(ROBIN.getId()).getFriendship());
            }
        });

        Thread.sleep(500);
    }

    @Test
    public void testGetExistingEventsReturnSetWithOnlyValidEvents() {
        Cache cache = new Cache();
        cache.putEvent(POLYLAN);
        assertEquals(1, cache.getEvents(Sets.newHashSet(POLYLAN.getId(), User.NO_ID)).size());
    }

    @Test
    public void testGetExistingFiltersReturnSetWithOnlyValidUsers() {
        Cache cache = new Cache();
        cache.putFilter(FAMILY);
        assertEquals(1, cache.getFilters(Sets.newHashSet(FAMILY.getId(), Filter.NO_ID)).size());
        FAMILY.setId(Filter.NO_ID);
    }

    @Test
    public void testGetExistingInvitationsReturnSetWithOnlyValidInvitations() {
        Cache cache = new Cache();
        cache.putInvitation(POLYLAN_EVENT_INVITATION);
        assertEquals(
            1,
            cache.getInvitations(
                Sets.newHashSet(POLYLAN_EVENT_INVITATION.getId(), ROBIN_FRIEND_INVITATION.getId())).size());
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
    public void testGetNonExistingEventsReturnsEmptySet() {
        assertTrue(new Cache().getEvents(Sets.newHashSet((long) 567, (long) 567)).isEmpty());
    }

    @Test
    public void testGetNonExistingFilterReturnsNull() {
        assertNull(new Cache().getInvitation(6));
    }

    @Test
    public void testGetNonExistingFiltersReturnsEmptySet() {
        assertTrue(new Cache().getFilters(Sets.newHashSet((long) 287, (long) 657)).isEmpty());
    }

    @Test
    public void testGetNonExistingInvitationReturnsNull() {
        assertNull(new Cache().getInvitation(6));
    }

    @Test
    public void testGetNonExistingInvitationsReturnsEmptySet() {
        assertTrue(new Cache().getInvitations(Sets.newHashSet((long) 5672, (long) 567)).isEmpty());
    }

    @Test
    public void testGetNonExistingUserReturnsNull() {
        assertNull(new Cache().getUser(3));
    }

    @Test
    public void testGetNonExistingUsersReturnEmptySet() {
        assertTrue(new Cache().getUsers(Sets.newHashSet((long) 5672, (long) 5674)).isEmpty());
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
    public void testPutEventAlsoAddUser() {
        Cache cache = new Cache();
        cache.putEvent(POLYLAN);

        assertNotNull(cache.getUser(POLYLAN.getCreatorContainer().getId()));
    }

    @Test
    public void testPutEventCallListeners() {
        Cache cache = new Cache();
        CacheListener listener = Mockito.mock(OnCacheListener.class);
        cache.addOnCacheListener(listener);
        cache.putEvent(POLYLAN);
        Mockito.verify(listener).onEventListUpdate();
    }

    @Test
    public void testPutEventDoesntReturnNull() {
        Cache cache = new Cache();
        cache.putEvent(POLYLAN);

        assertNotNull(cache.getEvent(POLYLAN.getId()));
    }

    @Test
    public void testPutEventOnlyCallListenersWhenNeeded() {
        Cache cache = new Cache();
        CacheListener listener = Mockito.mock(OnCacheListener.class);
        cache.addOnCacheListener(listener);
        cache.putEvent(POLYLAN);
        cache.putEvent(POLYLAN);
        Mockito.verify(listener).onEventListUpdate();
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
    public void testPutFilterCallListeners() {
        Cache cache = new Cache();
        CacheListener listener = Mockito.mock(OnCacheListener.class);
        cache.addOnCacheListener(listener);
        cache.putFilter(FAMILY);
        Mockito.verify(listener).onFilterListUpdate();
    }

    @Test
    public void testPutFilterCorrectlyGivesNextId() {
        Cache cache = new Cache();
        cache.putFilter(FAMILY);
        assertEquals(FAMILY.getId(), Filter.DEFAULT_FILTER_ID + 1);
        FAMILY.setId(Filter.NO_ID);
    }

    @Test
    public void testPutFilterDoesntReturnNull() {
        Cache cache = new Cache();
        cache.putFilter(FAMILY);
        assertNotNull(cache.getFilter(FAMILY.getId()));
        FAMILY.setId(Filter.NO_ID);
    }

    @Test
    public void testPutFilterOnlyCallListenersWhenNeeded() {
        Cache cache = new Cache();
        CacheListener listener = Mockito.mock(OnCacheListener.class);
        cache.addOnCacheListener(listener);
        cache.putFilter(FAMILY);
        cache.putFilter(FAMILY);
        Mockito.verify(listener).onFilterListUpdate();
    }

    @Test
    public void testPutInvitationAlsoAddEvent() {
        Cache cache = new Cache();
        cache.putInvitation(POLYLAN_EVENT_INVITATION);

        assertNotNull(cache.getEvent(POLYLAN_EVENT_INVITATION.getEventInfos().getId()));
    }

    @Test
    public void testPutInvitationAlsoAddUser() {
        Cache cache = new Cache();
        cache.putInvitation(ROBIN_FRIEND_INVITATION);

        assertNotNull(cache.getUser(ROBIN_FRIEND_INVITATION.getUserInfos().getId()));
    }

    @Test
    public void testPutInvitationCallListeners() {
        Cache cache = new Cache();
        CacheListener listener = Mockito.mock(OnCacheListener.class);
        cache.addOnCacheListener(listener);
        cache.putInvitation(POLYLAN_EVENT_INVITATION);
        Mockito.verify(listener).onInvitationListUpdate();
    }

    @Test
    public void testPutInvitationDoesntReturnNull() {
        Cache cache = new Cache();
        cache.putInvitation(POLYLAN_EVENT_INVITATION);

        assertNotNull(cache.getInvitation(POLYLAN_EVENT_INVITATION.getId()));
    }

    @Test
    public void testPutInvitationOnlyCallListenersWhenNeeded() {
        Cache cache = new Cache();
        CacheListener listener = Mockito.mock(OnCacheListener.class);
        cache.addOnCacheListener(listener);
        cache.putInvitation(POLYLAN_EVENT_INVITATION);
        cache.putInvitation(POLYLAN_EVENT_INVITATION);
        Mockito.verify(listener).onInvitationListUpdate();
    }

    @Test
    public void testPutUserCallListeners() {
        Cache cache = new Cache();
        CacheListener listener = Mockito.mock(OnCacheListener.class);
        cache.addOnCacheListener(listener);
        cache.putUser(ROBIN);
        Mockito.verify(listener).onUserListUpdate();
    }

    @Test
    public void testPutUserDoesntReturnNull() {
        Cache cache = new Cache();
        cache.putUser(ALAIN);

        assertNotNull(cache.getUser(ALAIN.getId()));
    }

    @Test
    public void testPutUserOnlyCallListenersWhenNeeded() {
        Cache cache = new Cache();
        CacheListener listener = Mockito.mock(OnCacheListener.class);
        cache.addOnCacheListener(listener);
        cache.putUser(ROBIN);
        cache.putUser(ROBIN);
        Mockito.verify(listener).onUserListUpdate();
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
