package ch.epfl.smartmap.test.cache;

import static ch.epfl.smartmap.test.database.MockContainers.FOOTBALL_TOURNAMENT;
import static ch.epfl.smartmap.test.database.MockContainers.JULIEN;
import static ch.epfl.smartmap.test.database.MockContainers.NULL_EVENT_VALUES;
import static ch.epfl.smartmap.test.database.MockContainers.POLYLAN;
import static ch.epfl.smartmap.test.database.MockContainers.UNSET_EVENT_VALUES;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.test.AndroidTestCase;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.PublicEvent;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserContainer;

public class PublicEventTest extends AndroidTestCase {

    @Before
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ServiceContainer.forceInitSmartMapServices(this.getContext());
    }

    @After
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // new cache
        ServiceContainer.setCache(new Cache());
    }

    @Test
    public void testCreateFriendWithNullValuesButGoodUser() {
        long previousId = NULL_EVENT_VALUES.getId();
        UserContainer previousCreatorContainer = NULL_EVENT_VALUES.getCreatorContainer();

        ServiceContainer.getCache().putEvent(NULL_EVENT_VALUES.setId(345).setCreatorContainer(JULIEN));
        PublicEvent event = (PublicEvent) ServiceContainer.getCache().getEvent(345);
        assertEquals(event.getName(), Event.NO_NAME);
        assertEquals(event.getSearchImage(), Event.DEFAULT_BLUE_IMAGE);
        assertEquals(event.getActionImage(), Event.DEFAULT_WHITE_IMAGE);
        assertEquals(event.getStartDate().getTimeInMillis(), Event.NO_START_DATE.getTimeInMillis());
        assertEquals(event.getEndDate().getTimeInMillis(), Event.NO_END_DATE.getTimeInMillis());
        assertEquals(event.getLocation().getLatitude(), Event.NO_LATITUDE);
        assertEquals(event.getLocation().getLongitude(), Event.NO_LONGITUDE);
        assertEquals(event.getLocationString(), Event.NO_LOCATION_STRING);

        NULL_EVENT_VALUES.setId(previousId);
        NULL_EVENT_VALUES.setCreatorContainer(previousCreatorContainer);
    }

    @Test
    public void testCreateFriendWithWrongId() {
        long previousId = POLYLAN.getId();
        try {
            ServiceContainer.getCache().putEvent(POLYLAN.setId(-5));
            fail();
        } catch (IllegalArgumentException e) {
            // Success
        } finally {
            POLYLAN.setId(previousId);
        }
    }

    @Test
    public void testGetValues() {
        ServiceContainer.getCache().putEvent(POLYLAN);
        PublicEvent event = (PublicEvent) ServiceContainer.getCache().getEvent(POLYLAN.getId());
        User creator = ServiceContainer.getCache().getUser(POLYLAN.getCreatorContainer().getId());
        assertEquals(event.getId(), POLYLAN.getId());
        assertEquals(event.getName(), POLYLAN.getName());
        assertEquals(event.getDescription(), POLYLAN.getDescription());
        assertEquals(event.getCreator(), creator);
        assertEquals(event.getLocation().getLatitude(), POLYLAN.getLocation().getLatitude());
        assertEquals(event.getLocation().getLongitude(), POLYLAN.getLocation().getLongitude());
        assertEquals(event.getStartDate().getTimeInMillis(), POLYLAN.getStartDate().getTimeInMillis());
        assertEquals(event.getEndDate().getTimeInMillis(), POLYLAN.getEndDate().getTimeInMillis());
        assertEquals(event.getLocationString(), POLYLAN.getLocationString());
        assertEquals(event.getLatLng().latitude, POLYLAN.getLocation().getLatitude());
        assertEquals(event.getLatLng().longitude, POLYLAN.getLocation().getLongitude());
    }

    @Test
    public void testUpdateWithGoodParameters() {
        ServiceContainer.getCache().putEvent(POLYLAN);
        Event polylan = ServiceContainer.getCache().getEvent(POLYLAN.getId());

        long previousId = FOOTBALL_TOURNAMENT.getId();

        polylan.update(FOOTBALL_TOURNAMENT.setId(POLYLAN.getId()));

        assertEquals(FOOTBALL_TOURNAMENT.getName(), polylan.getName());
        assertEquals(FOOTBALL_TOURNAMENT.getDescription(), polylan.getDescription());
        assertEquals(FOOTBALL_TOURNAMENT.getCreatorContainer().getId(), polylan.getCreator().getId());
        assertEquals(FOOTBALL_TOURNAMENT.getLocation().getLatitude(), polylan.getLocation().getLatitude());
        assertEquals(FOOTBALL_TOURNAMENT.getLocation().getLongitude(), polylan.getLocation().getLongitude());
        assertEquals(FOOTBALL_TOURNAMENT.getStartDate().getTimeInMillis(), polylan.getStartDate()
            .getTimeInMillis());
        assertEquals(FOOTBALL_TOURNAMENT.getEndDate().getTimeInMillis(), polylan.getEndDate()
            .getTimeInMillis());
        assertEquals(FOOTBALL_TOURNAMENT.getLocationString(), polylan.getLocationString());

        FOOTBALL_TOURNAMENT.setId(previousId);
    }

    @Test
    public void testUpdateWithSameParameters() {
        ServiceContainer.getCache().putEvent(POLYLAN);
        Event polylan = ServiceContainer.getCache().getEvent(POLYLAN.getId());
        assertFalse(polylan.update(POLYLAN));
    }

    @Test
    public void testUpdateWithUnsetParameters() {
        long previousId = UNSET_EVENT_VALUES.getId();
        ServiceContainer.getCache().putEvent(POLYLAN);
        Event polylan = ServiceContainer.getCache().getEvent(POLYLAN.getId());

        assertFalse(polylan.update(UNSET_EVENT_VALUES.setId(POLYLAN.getId())));
        UNSET_EVENT_VALUES.setId(previousId);
    }

    @Test
    public void testUpdateWithWrongId() {
        long previousId = POLYLAN.getId();

        ServiceContainer.getCache().putEvent(POLYLAN);
        Event polylan = ServiceContainer.getCache().getEvent(POLYLAN.getId());
        try {
            polylan.update(POLYLAN.setId(6));
            fail();
        } catch (IllegalArgumentException e) {
            // Success
        } finally {
            POLYLAN.setId(previousId);
        }
    }
}