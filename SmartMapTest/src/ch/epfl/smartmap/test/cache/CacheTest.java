package ch.epfl.smartmap.test.cache;

import static ch.epfl.smartmap.test.database.MockContainers.ALAIN;
import static ch.epfl.smartmap.test.database.MockContainers.JULIEN;
import static ch.epfl.smartmap.test.database.MockContainers.ROBIN;

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

        assertEquals(3, cache.getAllUsers().size());
        assertEquals(1, cache.getAllFriends().size());

        assertNotNull(cache.getUser(JULIEN.getId()));
        assertNotNull(cache.getUser(ALAIN.getId()));
        assertNotNull(cache.getUser(ROBIN.getId()));

        assertNotNull(cache.getSelf());
    }
    // // private static ImmutableInvitation ACCEPTED_FRIEND_INVITATION = new
    // // ImmutableInvitation(1, 2, Event.NO_ID,
    // // Invitation.UNREAD, 1, Invitation.ACCEPTED_FRIEND_INVITATION);
    // // private static ImmutableInvitation FRIEND_INVITATION = new
    // // ImmutableInvitation(2, 1, Event.NO_ID, Invitation.READ,
    // // 1, Invitation.FRIEND_INVITATION);
    // // private static ImmutableInvitation EVENT_INVITATION = new
    // // ImmutableInvitation(3, 2, 2, Invitation.ACCEPTED, 1,
    // // Invitation.EVENT_INVITATION);
    //
    // @Test
    // public void testPutAndGetEvent() {
    // // mCache.putEvent(POLYLAN);
    // // mCache.putEvent(FOOTBALL_TOURNAMENT);
    // // // Get event
    // // assertEquals(mCache.getEvent(POLYLAN.getId()).getId(), POLYLAN.getId());
    // // // Get all events
    // // assertEquals(mCache.getAllEvents().size(), 2);
    // // // Get all events with filter
    // // assertEquals(mCache.getEvents(new Cache.SearchFilter<Event>() {
    // // @Override
    // // public boolean filter(Event item) {
    // // return (item.getId() == 1);
    // // }
    // // }).size(), 1);
    // // // Get all events with id
    // // Set<Long> ids = new HashSet<Long>();
    // // ids.add((long) 1);
    // // assertEquals(mCache.getEvents(ids).size(), 1);
    // }
    //
    // @Test
    // public void testPutAndGetFilters() {
    // // mCache.putFilter(FAMILY);
    // // mCache.putFilter(ONLY_ME);
    // // // Get event
    // // assertEquals(mCache.getFilter(FAMILY.getId()).getId(), FAMILY.getId());
    // // // Get all friends
    // // assertEquals(mCache.getAllFilters().size(), 2);
    // // // Get all events with id
    // // Set<Long> ids = new HashSet<Long>();
    // // ids.add((long) 1);
    // // assertEquals(mCache.getFilters(ids).size(), 1);
    // }
    //
    // @Test
    // public void testPutAndGetFriend() {
    // /*
    // * mCache.putFriend(JULIEN);
    // * mCache.putFriend(ALAIN);
    // */
    // // Get friend
    // // assertEquals(mCache.getFriend(JULIEN.getId()).getId(),
    // // JULIEN.getId());
    // // Get all friends
    // // assertEquals(mCache.getAllFriends().size(), 2);
    // // Get all friends with id
    // Set<Long> ids = new HashSet<Long>();
    // ids.add((long) 1);
    // // assertEquals(mCache.getFriends(ids).size(), 1);
    // }
    //
    // private void initContainers() {
    // // julien = new UserContainer(julienId, julienName, julienEmail, julienPhoneNumber, )
    // }
    // // @Test
    // // public void testPutAndGetInvitations() {
    // // mCache.putInvitations(new
    // // HashSet<ImmutableInvitation>(Arrays.asList(ACCEPTED_FRIEND_INVITATION,
    // // FRIEND_INVITATION, EVENT_INVITATION)));
    // // // Get invitation
    // // assertEquals(mCache.getInvitation(FRIEND_INVITATION.getId()).getId(),
    // // FRIEND_INVITATION.getId());
    // // // Get unread invitations
    // // assertEquals(mCache.getUnansweredFriendInvitations().size(), 2);
    // // // Get all invitations
    // // assertEquals(mCache.getAllInvitations().size(), 3);
    // // // Get invitation with filter
    // // assertEquals(mCache.getInvitations(new Cache.SearchFilter<Invitation>() {
    // // @Override
    // // public boolean filter(Invitation item) {
    // // return item.getStatus() == Invitation.ACCEPTED;
    // // }
    // // }), 1);
    // // }
}
