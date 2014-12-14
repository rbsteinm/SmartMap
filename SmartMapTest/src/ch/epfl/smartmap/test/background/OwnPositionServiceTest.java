package ch.epfl.smartmap.test.background;

import org.junit.Test;
import org.mockito.Mockito;

import android.content.Intent;
import android.location.Location;
import android.test.ServiceTestCase;
import android.util.Log;
import ch.epfl.smartmap.background.OwnPositionService;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.background.SettingsManager;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClient;

public class OwnPositionServiceTest extends ServiceTestCase<OwnPositionService> {
    private Intent testIntent;
    private final Location mLocation = new Location("gps");

    public OwnPositionServiceTest() {
        super(OwnPositionService.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mLocation.setLatitude(50);
        mLocation.setLongitude(50);

        SettingsManager manager = Mockito.mock(SettingsManager.class);
        ServiceContainer.setSettingsManager(manager);

        SmartMapClient client = Mockito.mock(NetworkSmartMapClient.class);
        ServiceContainer.setNetworkClient(client);
    }

    @Test
    public void testOnTaskRemoved() {
        testIntent = new Intent(this.getContext(), OwnPositionService.class);

        // Not the same as Context.startService() !
        this.startService(testIntent);

        getService().onTaskRemoved(testIntent);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Log.e(OwnPositionServiceTest.class.getSimpleName(), "Thread interrupted: " + e);
        }
        // Check if the service is running
        assertTrue(this.getContext().stopService(testIntent));
    }

    @Test
    public void testStartStop() {
        testIntent = new Intent(this.getContext(), OwnPositionService.class);
        this.getContext().startService(testIntent);
        assertTrue(this.getContext().stopService(testIntent));
    }

}