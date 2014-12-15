package ch.epfl.smartmap.test.background;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.Description;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import android.location.Location;
import android.test.AndroidTestCase;
import android.util.Log;
import ch.epfl.smartmap.background.NearEventsThread;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.background.SettingsManager;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.EventContainer;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClient;

public class NearEventsThreadTest extends AndroidTestCase {

    private Cache cache = null;
    private SmartMapClient client = null;
    private SettingsManager settings = null;
    private EventContainer event = new EventContainer(0, null, null, null, null, null, null, null, null);
    private Set<EventContainer> evtSet = new HashSet<EventContainer>();
    private Location loc = new Location("testprovicer");

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        cache = Mockito.mock(Cache.class);
        ServiceContainer.setCache(cache);

        client = Mockito.mock(NetworkSmartMapClient.class);
        List<Long> events = new ArrayList<Long>();
        events.add((long) 123);
        evtSet.add(event);
        Mockito.when(client.getPublicEvents(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble()))
            .thenReturn(events);
        Mockito.when(client.getEventInfo(123)).thenReturn(event);
        ServiceContainer.setNetworkClient(client);

        settings = Mockito.mock(SettingsManager.class);
        Mockito.when(settings.getLocation()).thenReturn(loc);
        ServiceContainer.setSettingsManager(settings);
    }

    @Test
    public void testGetEvents() {
        NearEventsThread thread = new NearEventsThread();
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e(FriendsPositionsThreadTest.class.getSimpleName(), "Thread interrupted: " + e);
        }
        thread.interrupt();
        Mockito.verify(cache, Mockito.atLeastOnce()).putEvents(Mockito.argThat(isSame(evtSet)));
    }

    public static ArgumentMatcher<Set<EventContainer>> isSame(final Set<EventContainer> expectedEvent) {
        return new ArgumentMatcher<Set<EventContainer>>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("<" + expectedEvent + ">");
            }

            @Override
            public boolean matches(Object actualEvent) {
                return expectedEvent.hashCode() == actualEvent.hashCode();
            }
        };
    }
}
