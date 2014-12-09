package ch.epfl.smartmap.test.cache;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import android.location.Location;
import android.test.AndroidTestCase;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.ImmutableEvent;
import ch.epfl.smartmap.cache.ImmutableFilter;
import ch.epfl.smartmap.cache.ImmutableInvitation;
import ch.epfl.smartmap.cache.ImmutableUser;
import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.cache.User;

/**
 * @author jfperren
 */
public class CacheTest extends AndroidTestCase {

    Cache mCache;

    private static Calendar DATE_ONE;
    private static Calendar DATE_TWO;

    private static Location LOCATION_ONE;

    private static ImmutableEvent FOOTBALL_TOURNAMENT;
    private static ImmutableEvent POLYLAN;

    private static ImmutableUser JULIEN;
    private static ImmutableUser ALAIN;

    private static ImmutableFilter FAMILY;
    private static ImmutableFilter ONLY_ME;

    private static ImmutableEvent EVENT_WITH_NO_ID;

    private static ImmutableInvitation ACCEPTED_FRIEND_INVITATION = new ImmutableInvitation(1, 2,
        Event.NO_ID, Invitation.UNREAD, 1, Invitation.ACCEPTED_FRIEND_INVITATION);
    private static ImmutableInvitation FRIEND_INVITATION = new ImmutableInvitation(2, 1, Event.NO_ID,
        Invitation.READ, 1, Invitation.FRIEND_INVITATION);
    private static ImmutableInvitation EVENT_INVITATION = new ImmutableInvitation(3, 2, 2,
        Invitation.ACCEPTED, 1, Invitation.EVENT_INVITATION);

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Add services
        ServiceContainer.initSmartMapServices(this.getContext());
        mCache = ServiceContainer.getCache();

        DATE_ONE = GregorianCalendar.getInstance();
        DATE_TWO = GregorianCalendar.getInstance();
        DATE_ONE.setTimeInMillis(785759875);
        DATE_TWO.setTimeInMillis(453453453);

        LOCATION_ONE = new Location("SmartMapServers");

        EVENT_WITH_NO_ID =
            new ImmutableEvent(Event.NO_ID, "This is an Event name", 5, "This is a description", DATE_ONE,
                DATE_TWO, LOCATION_ONE, "This is a location", new HashSet<Long>());

        FOOTBALL_TOURNAMENT =
            new ImmutableEvent(1, "Football Tournament", 2, "Foot is great", DATE_ONE, DATE_TWO,
                LOCATION_ONE, "Morges", new HashSet<Long>(1, 2));

        POLYLAN =
            new ImmutableEvent(2, "Polylan", 1, "Video games are better", DATE_ONE, DATE_TWO, LOCATION_ONE,
                "EPFL", new HashSet<Long>(1));

        JULIEN =
            new ImmutableUser(1, "Julien", "123456789", "julien@epfl.ch", LOCATION_ONE, "Grandvaux",
                User.NO_IMAGE, false);

        ALAIN =
            new ImmutableUser(2, "Alain", "123456789", "alain@epfl.ch", LOCATION_ONE, "Pully", User.NO_IMAGE,
                false);

        FAMILY = new ImmutableFilter(1, "Family", new HashSet<Long>(Arrays.asList((long) 1, (long) 2)), true);
        ONLY_ME = new ImmutableFilter(2, "Myself", new HashSet<Long>(Arrays.asList((long) 1)), true);
    }

    @Test
    public void testCreateEventGivesId() {
        mCache.createEvent(EVENT_WITH_NO_ID, null);
    }

    @Test
    public void testPutAndGetEvent() {
        mCache.putEvent(POLYLAN);
        mCache.putEvent(FOOTBALL_TOURNAMENT);
        // Get event
        assertEquals(mCache.getEvent(POLYLAN.getId()).getId(), POLYLAN.getId());
        // Get all events
        assertEquals(mCache.getAllEvents().size(), 2);
        // Get all events with filter
        assertEquals(mCache.getEvents(new Cache.SearchFilter<Event>() {
            @Override
            public boolean filter(Event item) {
                return (item.getId() == 1);
            }
        }).size(), 1);
        // Get all events with id
        Set<Long> ids = new HashSet<Long>();
        ids.add((long) 1);
        assertEquals(mCache.getEvents(ids).size(), 1);
    }

    @Test
    public void testPutAndGetFilters() {
        mCache.putFilter(FAMILY);
        mCache.putFilter(ONLY_ME);
        // Get event
        assertEquals(mCache.getFilter(FAMILY.getId()).getId(), FAMILY.getId());
        // Get all friends
        assertEquals(mCache.getAllFilters().size(), 2);
        // Get all events with id
        Set<Long> ids = new HashSet<Long>();
        ids.add((long) 1);
        assertEquals(mCache.getFilters(ids).size(), 1);
    }

    @Test
    public void testPutAndGetFriend() {
        mCache.putFriend(JULIEN);
        mCache.putFriend(ALAIN);
        // Get friend
        assertEquals(mCache.getFriend(JULIEN.getId()).getId(), JULIEN.getId());
        // Get all friends
        assertEquals(mCache.getAllFriends().size(), 2);
        // Get all friends with id
        Set<Long> ids = new HashSet<Long>();
        ids.add((long) 1);
        assertEquals(mCache.getFriends(ids).size(), 1);
    }

    @Test
    public void testPutAndGetInvitations() {
        mCache.putInvitations(new HashSet<ImmutableInvitation>(Arrays.asList(ACCEPTED_FRIEND_INVITATION,
            FRIEND_INVITATION, EVENT_INVITATION)));
        // Get invitation
        assertEquals(mCache.getInvitation(FRIEND_INVITATION.getId()).getId(), FRIEND_INVITATION.getId());
        // Get unread invitations
        assertEquals(mCache.getUnansweredFriendInvitations().size(), 2);
        // Get all invitations
        assertEquals(mCache.getAllInvitations().size(), 3);
        // Get invitation with filter
        assertEquals(mCache.getInvitations(new Cache.SearchFilter<Invitation>() {
            @Override
            public boolean filter(Invitation item) {
                return item.getStatus() == Invitation.ACCEPTED;
            }
        }), 1);
    }
}
