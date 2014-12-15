package ch.epfl.smartmap.test.background;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import android.test.AndroidTestCase;
import android.util.Log;
import ch.epfl.smartmap.background.FriendsPositionsThread;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.background.SettingsManager;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.UserContainer;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClient;

public class FriendsPositionsThreadTest extends AndroidTestCase {

    private Cache cache = null;
    private SmartMapClient client = null;
    private SettingsManager settings = null;

    private final UserContainer user = new UserContainer(1337, null, null, null, null, null, null, null, 1);
    private Set<UserContainer> posSet;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        settings = Mockito.mock(SettingsManager.class);
        Mockito.when(settings.isOffline()).thenReturn(false);
        Mockito.when(settings.getRefreshFrequency()).thenReturn(10000);
        ServiceContainer.setSettingsManager(settings);

        cache = Mockito.mock(Cache.class);
        ServiceContainer.setCache(cache);

        client = Mockito.mock(NetworkSmartMapClient.class);
        List<UserContainer> posList = new ArrayList<UserContainer>();
        posList.add(user);
        posSet = new HashSet<UserContainer>(posList);
        Mockito.when(client.listFriendsPos()).thenReturn(posList);
        ServiceContainer.setNetworkClient(client);
    }

    public void testFriendsPositions() {
        FriendsPositionsThread thread = new FriendsPositionsThread();
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e(FriendsPositionsThreadTest.class.getSimpleName(), "Thread interrupted: " + e);
        }
        thread.disable();
        Mockito.verify(cache, Mockito.atLeastOnce()).putUsers(Mockito.argThat(isSame(posSet)));
    }

    public static ArgumentMatcher<Set<UserContainer>> isSame(final Set<UserContainer> expectedUser) {
        return new ArgumentMatcher<Set<UserContainer>>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("<" + expectedUser + ">");
            }

            @Override
            public boolean matches(Object actualUser) {
                return expectedUser.hashCode() == actualUser.hashCode();
            }
        };
    }
}
