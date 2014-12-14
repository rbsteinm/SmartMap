package ch.epfl.smartmap.test.background;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import android.content.Intent;
import android.location.Location;
import android.test.ServiceTestCase;
import android.util.Log;
import ch.epfl.smartmap.background.InvitationsService;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.background.SettingsManager;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.EventContainer;
import ch.epfl.smartmap.cache.InvitationContainer;
import ch.epfl.smartmap.cache.UserContainer;
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.servercom.InvitationBag;
import ch.epfl.smartmap.servercom.NetworkEventInvitationBag;
import ch.epfl.smartmap.servercom.NetworkFriendInvitationBag;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

public class InvitationsServiceTest extends ServiceTestCase<InvitationsService> {
    private Intent testIntent;
    private final Location mLocation = new Location("gps");
    private InvitationBag invitations = null;
    private InvitationBag eventInvitations = null;

    private Cache cache = null;
    private SmartMapClient client = null;

    public InvitationsServiceTest() {
        super(InvitationsService.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mLocation.setLatitude(50);
        mLocation.setLongitude(50);

        List<Long> removed = new ArrayList<Long>();
        removed.add((long) 1337);

        invitations =
            new NetworkFriendInvitationBag(new ArrayList<UserContainer>(), new ArrayList<UserContainer>(),
                removed);
        eventInvitations = new NetworkEventInvitationBag(new HashSet<EventContainer>());

        SettingsManager manager = Mockito.mock(SettingsManager.class);
        Mockito.when(manager.getUserId()).thenReturn((long) 0);
        ServiceContainer.setSettingsManager(manager);

        DatabaseHelper dbh = Mockito.mock(DatabaseHelper.class);
        ServiceContainer.setDatabaseHelper(dbh);

        client = Mockito.mock(NetworkSmartMapClient.class);
        Mockito.when(client.getFriendInvitations()).thenReturn(invitations);
        Mockito.when(client.getEventInvitations()).thenReturn(eventInvitations);
        ServiceContainer.setNetworkClient(client);

        cache = Mockito.mock(Cache.class);
        ServiceContainer.setCache(cache);
    }

    @Test
    public void testBackGroundNotifications() {
        ServiceContainer.setCache(null);

        testIntent = new Intent(this.getContext(), InvitationsService.class);
        this.startService(testIntent);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e(InvitationsServiceTest.class.getSimpleName(), "Thread interrupted: " + e);
        }

    }

    @Test
    public void testInvitRetrieval() throws SmartMapClientException {
        testIntent = new Intent(this.getContext(), InvitationsService.class);
        this.getContext().startService(testIntent);
        // Just to make sure the service has enough time to call the method since it's in an AsyncTask
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e(InvitationsServiceTest.class.getSimpleName(), "Thread interrupted: " + e);
        }
        Mockito.verify(client, Mockito.atLeastOnce()).ackRemovedFriend(1337);
        Mockito.verify(cache, Mockito.never()).putInvitations(Mockito.anySetOf(InvitationContainer.class));
    }

    @Test
    public void testOnTaskRemoved() {
        testIntent = new Intent(this.getContext(), InvitationsService.class);

        // Not the same as Context.startService() !
        this.startService(testIntent);

        getService().onTaskRemoved(testIntent);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Log.e(InvitationsServiceTest.class.getSimpleName(), "Thread interrupted: " + e);
        }
        // Check if the service is running
        assertTrue(this.getContext().stopService(testIntent));
    }

    @Test
    public void testStartStop() {
        testIntent = new Intent(this.getContext(), InvitationsService.class);
        this.getContext().startService(testIntent);
        assertTrue(this.getContext().stopService(testIntent));
    }
}