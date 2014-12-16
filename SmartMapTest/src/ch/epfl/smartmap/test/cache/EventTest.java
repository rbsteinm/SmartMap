package ch.epfl.smartmap.test.cache;

import static ch.epfl.smartmap.test.database.MockContainers.FOOTBALL_TOURNAMENT_CONTAINER;
import static ch.epfl.smartmap.test.database.MockContainers.JULIEN_CONTAINER;
import static ch.epfl.smartmap.test.database.MockContainers.NULL_EVENT_VALUES;
import static ch.epfl.smartmap.test.database.MockContainers.POLYLAN_CONTAINER;
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

public class EventTest extends AndroidTestCase {

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

        ServiceContainer.getCache().putEvent(
            NULL_EVENT_VALUES.setId(345).setCreatorContainer(JULIEN_CONTAINER));
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
        long previousId = POLYLAN_CONTAINER.getId();
        try {
            ServiceContainer.getCache().putEvent(POLYLAN_CONTAINER.setId(-5));
            fail();
        } catch (IllegalArgumentException e) {
            // Success
        } finally {
            POLYLAN_CONTAINER.setId(previousId);
        }
    }

    @Test
    public void testGetValues() {
        ServiceContainer.getCache().putEvent(POLYLAN_CONTAINER);
        PublicEvent event = (PublicEvent) ServiceContainer.getCache().getEvent(POLYLAN_CONTAINER.getId());
        User creator = ServiceContainer.getCache().getUser(POLYLAN_CONTAINER.getCreatorContainer().getId());
        assertEquals(event.getId(), POLYLAN_CONTAINER.getId());
        assertEquals(event.getName(), POLYLAN_CONTAINER.getName());
        assertEquals(event.getDescription(), POLYLAN_CONTAINER.getDescription());
        assertEquals(event.getCreator(), creator);
        assertEquals(event.getLocation().getLatitude(), POLYLAN_CONTAINER.getLocation().getLatitude());
        assertEquals(event.getLocation().getLongitude(), POLYLAN_CONTAINER.getLocation().getLongitude());
        assertEquals(event.getStartDate().getTimeInMillis(), POLYLAN_CONTAINER.getStartDate()
            .getTimeInMillis());
        assertEquals(event.getEndDate().getTimeInMillis(), POLYLAN_CONTAINER.getEndDate().getTimeInMillis());
        assertEquals(event.getLocationString(), POLYLAN_CONTAINER.getLocationString());
        assertEquals(event.getLatLng().latitude, POLYLAN_CONTAINER.getLocation().getLatitude());
        assertEquals(event.getLatLng().longitude, POLYLAN_CONTAINER.getLocation().getLongitude());
    }

    @Test
    public void testUpdateWithGoodParameters() {
        ServiceContainer.getCache().putEvent(POLYLAN_CONTAINER);
        Event polylan = ServiceContainer.getCache().getEvent(POLYLAN_CONTAINER.getId());

        long previousId = FOOTBALL_TOURNAMENT_CONTAINER.getId();

        polylan.update(FOOTBALL_TOURNAMENT_CONTAINER.setId(POLYLAN_CONTAINER.getId()));

        assertEquals(FOOTBALL_TOURNAMENT_CONTAINER.getName(), polylan.getName());
        assertEquals(FOOTBALL_TOURNAMENT_CONTAINER.getDescription(), polylan.getDescription());
        assertEquals(FOOTBALL_TOURNAMENT_CONTAINER.getCreatorContainer().getId(), polylan.getCreator()
            .getId());
        assertEquals(FOOTBALL_TOURNAMENT_CONTAINER.getLocation().getLatitude(), polylan.getLocation()
            .getLatitude());
        assertEquals(FOOTBALL_TOURNAMENT_CONTAINER.getLocation().getLongitude(), polylan.getLocation()
            .getLongitude());
        assertEquals(FOOTBALL_TOURNAMENT_CONTAINER.getStartDate().getTimeInMillis(), polylan.getStartDate()
            .getTimeInMillis());
        assertEquals(FOOTBALL_TOURNAMENT_CONTAINER.getEndDate().getTimeInMillis(), polylan.getEndDate()
            .getTimeInMillis());
        assertEquals(FOOTBALL_TOURNAMENT_CONTAINER.getLocationString(), polylan.getLocationString());

        FOOTBALL_TOURNAMENT_CONTAINER.setId(previousId);
    }

    @Test
    public void testUpdateWithUnsetParameters() {
        long previousId = UNSET_EVENT_VALUES.getId();
        ServiceContainer.getCache().putEvent(POLYLAN_CONTAINER);
        Event polylan = ServiceContainer.getCache().getEvent(POLYLAN_CONTAINER.getId());

        assertFalse(polylan.update(UNSET_EVENT_VALUES.setId(POLYLAN_CONTAINER.getId())));
        UNSET_EVENT_VALUES.setId(previousId);
    }

    @Test
    public void testUpdateWithWrongId() {
        long previousId = POLYLAN_CONTAINER.getId();

        ServiceContainer.getCache().putEvent(POLYLAN_CONTAINER);
        Event polylan = ServiceContainer.getCache().getEvent(POLYLAN_CONTAINER.getId());
        try {
            polylan.update(POLYLAN_CONTAINER.setId(6));
            fail();
        } catch (IllegalArgumentException e) {
            // Success
        } finally {
            POLYLAN_CONTAINER.setId(previousId);
        }
    }
}