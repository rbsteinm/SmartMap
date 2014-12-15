//package ch.epfl.smartmap.test.cache;
//
//import static ch.epfl.smartmap.test.database.MockContainers.ALAIN_CONTAINER;
//import static ch.epfl.smartmap.test.database.MockContainers.FAMILY;
//import static ch.epfl.smartmap.test.database.MockContainers.FOOTBALL_TOURNAMENT_CONTAINER;
//import static ch.epfl.smartmap.test.database.MockContainers.JULIEN_CONTAINER;
//import static ch.epfl.smartmap.test.database.MockContainers.POLYLAN_CONTAINER;
//import static ch.epfl.smartmap.test.database.MockContainers.POLYLAN_EVENT_INVITATION_CONTAINER;
//import static ch.epfl.smartmap.test.database.MockContainers.ROBIN;
//import static ch.epfl.smartmap.test.database.MockContainers.ROBIN_FRIEND_INVITATION_CONTANER;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.GregorianCalendar;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.mockito.runners.MockitoJUnitRunner;
//
//import android.graphics.Bitmap;
//import android.graphics.Bitmap.Config;
//import android.test.AndroidTestCase;
//import ch.epfl.smartmap.background.ServiceContainer;
//import ch.epfl.smartmap.background.SettingsManager;
//import ch.epfl.smartmap.cache.Cache;
//import ch.epfl.smartmap.cache.Filter;
//import ch.epfl.smartmap.cache.Invitation;
//import ch.epfl.smartmap.cache.InvitationContainer;
//import ch.epfl.smartmap.cache.User;
//import ch.epfl.smartmap.cache.UserContainer;
//import ch.epfl.smartmap.callbacks.NetworkRequestCallback;
//import ch.epfl.smartmap.database.DatabaseHelper;
//import ch.epfl.smartmap.listeners.CacheListener;
//import ch.epfl.smartmap.listeners.OnCacheListener;
//import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
//import ch.epfl.smartmap.servercom.SmartMapClient;
//import ch.epfl.smartmap.servercom.SmartMapClientException;
//
//import com.google.common.collect.Sets;
//
///**
// * @author jfperren
// */
//@RunWith(MockitoJUnitRunner.class)
//public class CacheTest extends AndroidTestCase {
//
//    private DatabaseHelper databaseForUsers;
//    private DatabaseHelper databaseForEvents;
//    private NetworkSmartMapClient clientForUsers;
//    private NetworkSmartMapClient clientForEvents;
//    private SettingsManager mockSettings;
//
//    private Cache cache;
//
//    @Before
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//        ServiceContainer.forceInitSmartMapServices(this.getContext());
//
//        cache = new Cache();
//        ServiceContainer.setCache(cache);
//        // Create mock Settings
//        mockSettings = Mockito.mock(SettingsManager.class);
//        // Act as if julien is self
//        Mockito.doReturn(JULIEN_CONTAINER.getId()).when(mockSettings).getUserId();
//        Mockito.doReturn(JULIEN_CONTAINER.getName()).when(mockSettings).getUserName();
//        Mockito.doReturn(JULIEN_CONTAINER.getLocation()).when(mockSettings).getLocation();
//        Mockito.doReturn(this.getContext()).when(mockSettings).getContext();
//        // Add as Service
//        ServiceContainer.setSettingsManager(mockSettings);
//
//        // CLIENT FOR USERS ONLY
//        // create Mock network client
//        clientForUsers = Mockito.mock(NetworkSmartMapClient.class);
//        // Return friends/users
//        Mockito.doReturn(Arrays.asList(ALAIN_CONTAINER.getId())).when(clientForUsers).getFriendsIds();
//        Mockito.doReturn(ALAIN_CONTAINER).when(clientForUsers).getUserInfo(ALAIN_CONTAINER.getId());
//        Mockito.doReturn(JULIEN_CONTAINER).when(clientForUsers).getUserInfo(JULIEN_CONTAINER.getId());
//        Mockito.doReturn(ROBIN).when(clientForUsers).getUserInfo(ROBIN.getId());
//        Mockito
//            .doReturn(
//                Arrays.asList(UserContainer.newEmptyContainer().setLocation(ALAIN_CONTAINER.getLocation())
//                    .setId(ALAIN_CONTAINER.getId()).setName(ALAIN_CONTAINER.getName()).setFriendship(ALAIN_CONTAINER.getFriendship())))
//            .when(clientForUsers).listFriendsPos();
//        // Return own profile picture (otherwise NullPointers)
//        Mockito.doReturn(Bitmap.createBitmap(1, 2, Config.ALPHA_8)).when(clientForUsers)
//            .getProfilePicture(JULIEN_CONTAINER.getId());
//        // Return no events
//        Mockito.doReturn(new ArrayList<Long>()).when(clientForUsers)
//            .getPublicEvents(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong());
//
//        // CLIENT FOR EVENTS ONLY
//        // Return no friends
//        clientForEvents = Mockito.mock(NetworkSmartMapClient.class);
//        Mockito.doReturn(Arrays.asList()).when(clientForEvents).getFriendsIds();
//        Mockito.doReturn(Arrays.asList()).when(clientForEvents).listFriendsPos();
//        // Return our own profile picture (otherwise NullPointers)
//        Mockito.doReturn(Bitmap.createBitmap(1, 2, Config.ALPHA_8)).when(clientForEvents)
//            .getProfilePicture(JULIEN_CONTAINER.getId());
//        // Return events
//        Mockito.doReturn(Arrays.asList(POLYLAN_CONTAINER.getId(), FOOTBALL_TOURNAMENT_CONTAINER.getId())).when(clientForEvents)
//            .getPublicEvents(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong());
//        Mockito.doReturn(POLYLAN_CONTAINER).when(clientForEvents).getEventInfo(POLYLAN_CONTAINER.getId());
//        Mockito.doReturn(FOOTBALL_TOURNAMENT_CONTAINER).when(clientForEvents).getEventInfo(FOOTBALL_TOURNAMENT_CONTAINER.getId());
//
//        // DATABASE FOR USERS ONLY
//        databaseForUsers = Mockito.mock(DatabaseHelper.class);
//        Mockito.doReturn(Sets.newHashSet(JULIEN_CONTAINER, ALAIN_CONTAINER, ROBIN)).when(databaseForUsers).getAllUsers();
//        Mockito.doReturn(Sets.newHashSet()).when(databaseForUsers).getAllEvents();
//        Mockito.doReturn(Sets.newHashSet()).when(databaseForUsers).getAllInvitations();
//        Mockito.doReturn(Sets.newHashSet()).when(databaseForUsers).getAllFilters();
//
//        // DATABASE FOR EVENTS ONLY
//        databaseForEvents = Mockito.mock(DatabaseHelper.class);
//        Mockito.doReturn(Sets.newHashSet()).when(databaseForEvents).getAllUsers();
//        Mockito.doReturn(Sets.newHashSet(POLYLAN_CONTAINER, FOOTBALL_TOURNAMENT_CONTAINER)).when(databaseForEvents)
//            .getAllEvents();
//        Mockito.doReturn(Sets.newHashSet()).when(databaseForEvents).getAllInvitations();
//        Mockito.doReturn(Sets.newHashSet()).when(databaseForEvents).getAllFilters();
//
//    }
//
//    @After
//    @Override
//    protected void tearDown() throws Exception {
//        super.tearDown();
//        // Clear cache
//        cache = new Cache();
//
//        // Reset values in containers
//        FAMILY.setId(Filter.NO_ID);
//    }
//
//    @Test
//    public void testAcceptInvitation() throws SmartMapClientException, Exception {
//        SmartMapClient mockNetClient = Mockito.mock(NetworkSmartMapClient.class);
//        Mockito.doReturn(ROBIN).when(mockNetClient).acceptInvitation(ROBIN.getId());
//
//        ServiceContainer.setNetworkClient(mockNetClient);
//
//        cache.putUser(ROBIN);
//
//        InvitationContainer invitRobin =
//            new InvitationContainer(1, ROBIN, null, Invitation.UNREAD,
//                new GregorianCalendar().getTimeInMillis(), Invitation.FRIEND_INVITATION);
//
//        cache.putInvitation(invitRobin);
//
//        Invitation invitation = cache.getInvitation(1);
//
//        cache.acceptInvitation(invitation, new NetworkRequestCallback<Void>() {
//            @Override
//            public void onFailure(Exception e) {
//                fail(); // Should not fail !
//            }
//
//            @Override
//            public void onSuccess(Void result) {
//                assertEquals(User.FRIEND, cache.getUser(ROBIN.getId()).getFriendship());
//            }
//        });
//
//        Thread.sleep(500);
//    }
//
//    @Test
//    public void testGetExistingEventsReturnSetWithOnlyValidEvents() {
//        cache.putEvent(POLYLAN_CONTAINER);
//        assertEquals(1, cache.getEvents(Sets.newHashSet(POLYLAN_CONTAINER.getId(), User.NO_ID)).size());
//    }
//
//    @Test
//    public void testGetExistingFiltersReturnSetWithOnlyValidUsers() {
//        cache.putFilter(FAMILY);
//        assertEquals(1, cache.getFilters(Sets.newHashSet(FAMILY.getId(), Filter.NO_ID)).size());
//    }
//
//    @Test
//    public void testGetExistingInvitationsReturnSetWithOnlyValidInvitations() {
//        cache.putInvitation(POLYLAN_EVENT_INVITATION_CONTAINER);
//        assertEquals(
//            1,
//            cache.getInvitations(
//                Sets.newHashSet(POLYLAN_EVENT_INVITATION_CONTAINER.getId(), ROBIN_FRIEND_INVITATION_CONTANER.getId())).size());
//    }
//
//    @Test
//    public void testGetExistingUsersReturnSetWithOnlyValidUsers() {
//        cache.putUser(JULIEN_CONTAINER);
//        assertEquals(1, cache.getUsers(Sets.newHashSet(JULIEN_CONTAINER.getId(), User.NO_ID)).size());
//    }
//
//    @Test
//    public void testGetNonExistingEventReturnsNull() {
//        assertNull(cache.getEvent(3));
//    }
//
//    @Test
//    public void testGetNonExistingEventsReturnsEmptySet() {
//        assertTrue(cache.getEvents(Sets.newHashSet((long) 567, (long) 567)).isEmpty());
//    }
//
//    @Test
//    public void testGetNonExistingFilterReturnsNull() {
//        assertNull(cache.getInvitation(6));
//    }
//
//    @Test
//    public void testGetNonExistingFiltersReturnsEmptySet() {
//        assertTrue(cache.getFilters(Sets.newHashSet((long) 287, (long) 657)).isEmpty());
//    }
//
//    @Test
//    public void testGetNonExistingInvitationReturnsNull() {
//        assertNull(cache.getInvitation(6));
//    }
//
//    @Test
//    public void testGetNonExistingInvitationsReturnsEmptySet() {
//        assertTrue(cache.getInvitations(Sets.newHashSet((long) 5672, (long) 567)).isEmpty());
//    }
//
//    @Test
//    public void testGetNonExistingUserReturnsNull() {
//        assertNull(cache.getUser(3));
//    }
//
//    @Test
//    public void testGetNonExistingUsersReturnEmptySet() {
//        assertTrue(cache.getUsers(Sets.newHashSet((long) 5672, (long) 5674)).isEmpty());
//    }
//
//    @Test
//    public void testInitFromDatabaseWithCorrectEvents() {
//        cache.initFromDatabase(databaseForEvents);
//
//        assertEquals(2, cache.getAllEvents().size());
//
//        assertNotNull(cache.getEvent(POLYLAN_CONTAINER.getId()));
//        assertNotNull(cache.getEvent(FOOTBALL_TOURNAMENT_CONTAINER.getId()));
//
//        assertNotNull(cache.getSelf());
//    }
//
//    @Test
//    public void testInitFromDatabaseWithCorrectUsers() {
//        cache.initFromDatabase(databaseForUsers);
//        ServiceContainer.setDatabaseHelper(databaseForUsers);
//        ServiceContainer.setNetworkClient(clientForUsers);
//        assertEquals(3, cache.getAllUsers().size());
//
//        assertNotNull(cache.getUser(JULIEN_CONTAINER.getId()));
//        assertNotNull(cache.getUser(ALAIN_CONTAINER.getId()));
//        assertNotNull(cache.getUser(ROBIN.getId()));
//
//        assertNotNull(cache.getSelf());
//    }
//
//    @Test
//    public void testPutEventAlsoAddUser() {
//        cache.putEvent(POLYLAN_CONTAINER);
//
//        assertNotNull(cache.getUser(POLYLAN_CONTAINER.getCreatorContainer().getId()));
//    }
//
//    @Test
//    public void testPutEventCallListeners() {
//        CacheListener listener = Mockito.mock(OnCacheListener.class);
//        cache.addOnCacheListener(listener);
//        cache.putEvent(POLYLAN_CONTAINER);
//        Mockito.verify(listener).onEventListUpdate();
//    }
//
//    @Test
//    public void testPutEventDoesntReturnNull() {
//        cache.putEvent(POLYLAN_CONTAINER);
//
//        assertNotNull(cache.getEvent(POLYLAN_CONTAINER.getId()));
//    }
//
//    @Test
//    public void testPutEventOnlyCallListenersWhenNeeded() {
//        CacheListener listener = Mockito.mock(OnCacheListener.class);
//        cache.addOnCacheListener(listener);
//        cache.putEvent(POLYLAN_CONTAINER);
//        cache.putEvent(POLYLAN_CONTAINER);
//        Mockito.verify(listener).onEventListUpdate();
//    }
//
//    @Test
//    public void testPutEventWithExistingEventCallsUpdate() {
//        cache.putEvent(POLYLAN_CONTAINER);
//        assertEquals(cache.getEvent(POLYLAN_CONTAINER.getId()).getName(), POLYLAN_CONTAINER.getName());
//        cache.putEvent(cache.getEvent(POLYLAN_CONTAINER.getId()).getContainerCopy().setName("Foot"));
//        assertEquals(cache.getEvent(POLYLAN_CONTAINER.getId()).getName(), "Foot");
//    }
//
//    @Test
//    public void testPutFilterCallListeners() {
//        CacheListener listener = Mockito.mock(OnCacheListener.class);
//        cache.addOnCacheListener(listener);
//        cache.putFilter(FAMILY);
//        Mockito.verify(listener).onFilterListUpdate();
//    }
//
//    @Test
//    public void testPutFilterCorrectlyGivesNextId() {
//        cache.putFilter(FAMILY);
//        assertEquals(FAMILY.getId(), Filter.DEFAULT_FILTER_ID + 1);
//    }
//
//    @Test
//    public void testPutFilterDoesntReturnNull() {
//        cache.putFilter(FAMILY);
//        assertNotNull(cache.getFilter(FAMILY.getId()));
//    }
//
//    @Test
//    public void testPutFilterOnlyCallListenersWhenNeeded() {
//        CacheListener listener = Mockito.mock(OnCacheListener.class);
//        cache.addOnCacheListener(listener);
//        cache.putFilter(FAMILY);
//        cache.putFilter(FAMILY);
//        Mockito.verify(listener).onFilterListUpdate();
//    }
//
//    @Test
//    public void testPutInvitationAlsoAddEvent() {
//        cache.putInvitation(POLYLAN_EVENT_INVITATION_CONTAINER);
//
//        assertNotNull(cache.getEvent(POLYLAN_EVENT_INVITATION_CONTAINER.getEventInfos().getId()));
//    }
//
//    @Test
//    public void testPutInvitationAlsoAddUser() {
//        cache.putInvitation(ROBIN_FRIEND_INVITATION_CONTANER);
//
//        assertNotNull(cache.getUser(ROBIN_FRIEND_INVITATION_CONTANER.getUserInfos().getId()));
//    }
//
//    @Test
//    public void testPutInvitationCallListeners() {
//        CacheListener listener = Mockito.mock(OnCacheListener.class);
//        cache.addOnCacheListener(listener);
//        cache.putInvitation(POLYLAN_EVENT_INVITATION_CONTAINER);
//        Mockito.verify(listener).onInvitationListUpdate();
//    }
//
//    @Test
//    public void testPutInvitationDoesntReturnNull() {
//        cache.putInvitation(POLYLAN_EVENT_INVITATION_CONTAINER);
//
//        assertNotNull(cache.getInvitation(POLYLAN_EVENT_INVITATION_CONTAINER.getId()));
//    }
//
//    @Test
//    public void testPutInvitationOnlyCallListenersWhenNeeded() {
//        CacheListener listener = Mockito.mock(OnCacheListener.class);
//        cache.addOnCacheListener(listener);
//        cache.putInvitation(POLYLAN_EVENT_INVITATION_CONTAINER);
//        cache.putInvitation(POLYLAN_EVENT_INVITATION_CONTAINER);
//        Mockito.verify(listener).onInvitationListUpdate();
//    }
//
//    @Test
//    public void testPutUserCallListeners() {
//        CacheListener listener = Mockito.mock(OnCacheListener.class);
//        cache.addOnCacheListener(listener);
//        cache.putUser(ROBIN);
//        Mockito.verify(listener).onUserListUpdate();
//    }
//
//    @Test
//    public void testPutUserDoesntReturnNull() {
//        cache.putUser(ALAIN_CONTAINER);
//
//        assertNotNull(cache.getUser(ALAIN_CONTAINER.getId()));
//    }
//
//    @Test
//    public void testPutUserOnlyCallListenersWhenNeeded() {
//        CacheListener listener = Mockito.mock(OnCacheListener.class);
//        cache.addOnCacheListener(listener);
//        cache.putUser(ROBIN);
//        cache.putUser(ROBIN);
//        Mockito.verify(listener).onUserListUpdate();
//    }
//
//    @Test
//    public void testPutUserWithExistingUserCallsUpdate() {
//        cache.putUser(ALAIN_CONTAINER);
//        assertEquals(cache.getUser(ALAIN_CONTAINER.getId()).getName(), ALAIN_CONTAINER.getName());
//        cache.putUser(cache.getUser(ALAIN_CONTAINER.getId()).getContainerCopy().setName("Robert"));
//        assertEquals(cache.getUser(ALAIN_CONTAINER.getId()).getName(), "Robert");
//    }
//
//    @Test
//    public void testUpdateFromNetworkWithCorrectUsers() throws SmartMapClientException {
//        ServiceContainer.setNetworkClient(clientForUsers);
//        cache.updateFromNetwork(clientForUsers);
//
//        assertEquals(2, cache.getAllUsers().size());
//        assertEquals(1, cache.getAllFriends().size());
//
//        assertNotNull(cache.getUser(JULIEN_CONTAINER.getId()));
//        assertNotNull(cache.getUser(ALAIN_CONTAINER.getId()));
//        assertNull(cache.getUser(ROBIN.getId()));
//
//        assertFalse(cache.getSelf().getActionImage().sameAs(User.NO_IMAGE));
//
//        assertNotNull(cache.getSelf());
//    }
//}
