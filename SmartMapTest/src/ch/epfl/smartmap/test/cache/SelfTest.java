package ch.epfl.smartmap.test.cache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import android.location.Location;
import android.test.AndroidTestCase;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.background.SettingsManager;
import ch.epfl.smartmap.cache.Self;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserContainer;
import ch.epfl.smartmap.test.database.MockContainers;

/**
 * @author jfperren
 * @author ritterni
 */
public class SelfTest extends AndroidTestCase {
    Location location = new Location("");
    String locationName = "Ecublens";
    UserContainer selfValues;

    @Before
    @Override
    protected void setUp() {

        location.setLatitude(MockContainers.JULIEN_LATITUDE);
        location.setLongitude(MockContainers.JULIEN_LONGITUDE);

        SettingsManager settings = Mockito.mock(SettingsManager.class);
        Mockito.when(settings.getUserId()).thenReturn(MockContainers.JULIEN_ID);
        Mockito.when(settings.getUserName()).thenReturn(MockContainers.JULIEN_NAME);
        Mockito.when(settings.getUPhoneNumber()).thenReturn(MockContainers.JULIEN_PHONE_NUMBER);
        Mockito.when(settings.getEmail()).thenReturn(MockContainers.JULIEN_EMAIL);
        Mockito.when(settings.getLocation()).thenReturn(MockContainers.JULIEN_LOCATION);
        Mockito.when(settings.getLastSeen()).thenReturn(MockContainers.JULIEN_LAST_SEEN);
        Mockito.when(settings.getContext()).thenReturn(this.getContext());
        Mockito.when(settings.getLocationName()).thenReturn(MockContainers.JULIEN_LOCATION_STRING);
        ServiceContainer.setSettingsManager(settings);
        ServiceContainer.getCache().putUser(MockContainers.JULIEN_CONTAINER);
    }

    @Test
    public void testReturnSettingsValues() {
        Self self =
            (Self) ServiceContainer.getCache().getUser(ServiceContainer.getSettingsManager().getUserId());
        assertTrue(self.getLatLng().latitude == MockContainers.JULIEN_LATITUDE);
        assertTrue(self.getLatLng().longitude == MockContainers.JULIEN_LONGITUDE);
        assertTrue(self.getLocation().getLongitude() == MockContainers.JULIEN_LATITUDE);
        assertTrue(self.getLocation().getLatitude() == MockContainers.JULIEN_LONGITUDE);
        assertTrue(self.getLocationString().equals(locationName));
        assertTrue(self.getSubtitle().equals("You are near " + locationName));
    }

    @Test
    public void testSelfConstruction() {
        Self self =
            (Self) ServiceContainer.getCache().getUser(ServiceContainer.getSettingsManager().getUserId());
        assertTrue(self.getId() == MockContainers.JULIEN_ID);
        assertTrue(self.getName().equals(MockContainers.JULIEN_NAME));
        assertTrue(self.getActionImage().sameAs(MockContainers.JULIEN_IMAGE));
        assertTrue(self.getBlockStatus().equals(User.BlockStatus.UNBLOCKED));
        assertTrue(self.getFriendship() == User.SELF);
    }
}
