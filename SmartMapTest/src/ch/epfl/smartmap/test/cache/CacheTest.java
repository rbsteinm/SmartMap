package ch.epfl.smartmap.test.cache;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import android.test.AndroidTestCase;

/**
 * @author jfperren
 */
public class CacheTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    // private static ImmutableInvitation ACCEPTED_FRIEND_INVITATION = new
    // ImmutableInvitation(1, 2, Event.NO_ID,
    // Invitation.UNREAD, 1, Invitation.ACCEPTED_FRIEND_INVITATION);
    // private static ImmutableInvitation FRIEND_INVITATION = new
    // ImmutableInvitation(2, 1, Event.NO_ID, Invitation.READ,
    // 1, Invitation.FRIEND_INVITATION);
    // private static ImmutableInvitation EVENT_INVITATION = new
    // ImmutableInvitation(3, 2, 2, Invitation.ACCEPTED, 1,
    // Invitation.EVENT_INVITATION);

    @Test
    public void testCreateEventGivesId() {
        // mCache.createEvent(EVENT_WITH_NO_ID, null);
    }

    @Test
    public void testPutAndGetEvent() {
        // mCache.putEvent(POLYLAN);
        // mCache.putEvent(FOOTBALL_TOURNAMENT);
        // // Get event
        // assertEquals(mCache.getEvent(POLYLAN.getId()).getId(), POLYLAN.getId());
        // // Get all events
        // assertEquals(mCache.getAllEvents().size(), 2);
        // // Get all events with filter
        // assertEquals(mCache.getEvents(new Cache.SearchFilter<Event>() {
        // @Override
        // public boolean filter(Event item) {
        // return (item.getId() == 1);
        // }
        // }).size(), 1);
        // // Get all events with id
        // Set<Long> ids = new HashSet<Long>();
        // ids.add((long) 1);
        // assertEquals(mCache.getEvents(ids).size(), 1);
    }

    @Test
    public void testPutAndGetFilters() {
        // mCache.putFilter(FAMILY);
        // mCache.putFilter(ONLY_ME);
        // // Get event
        // assertEquals(mCache.getFilter(FAMILY.getId()).getId(), FAMILY.getId());
        // // Get all friends
        // assertEquals(mCache.getAllFilters().size(), 2);
        // // Get all events with id
        // Set<Long> ids = new HashSet<Long>();
        // ids.add((long) 1);
        // assertEquals(mCache.getFilters(ids).size(), 1);
    }

    @Test
    public void testPutAndGetFriend() {
        /*
         * mCache.putFriend(JULIEN);
         * mCache.putFriend(ALAIN);
         */
        // Get friend
        // assertEquals(mCache.getFriend(JULIEN.getId()).getId(),
        // JULIEN.getId());
        // Get all friends
        // assertEquals(mCache.getAllFriends().size(), 2);
        // Get all friends with id
        Set<Long> ids = new HashSet<Long>();
        ids.add((long) 1);
        // assertEquals(mCache.getFriends(ids).size(), 1);
    }

    private void initContainers() {
        // julien = new UserContainer(julienId, julienName, julienEmail, julienPhoneNumber, )
    }
    // @Test
    // public void testPutAndGetInvitations() {
    // mCache.putInvitations(new
    // HashSet<ImmutableInvitation>(Arrays.asList(ACCEPTED_FRIEND_INVITATION,
    // FRIEND_INVITATION, EVENT_INVITATION)));
    // // Get invitation
    // assertEquals(mCache.getInvitation(FRIEND_INVITATION.getId()).getId(),
    // FRIEND_INVITATION.getId());
    // // Get unread invitations
    // assertEquals(mCache.getUnansweredFriendInvitations().size(), 2);
    // // Get all invitations
    // assertEquals(mCache.getAllInvitations().size(), 3);
    // // Get invitation with filter
    // assertEquals(mCache.getInvitations(new Cache.SearchFilter<Invitation>() {
    // @Override
    // public boolean filter(Invitation item) {
    // return item.getStatus() == Invitation.ACCEPTED;
    // }
    // }), 1);
    // }
}
