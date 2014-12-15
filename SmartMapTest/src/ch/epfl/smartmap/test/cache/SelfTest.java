package ch.epfl.smartmap.test.cache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.location.Location;
import android.test.AndroidTestCase;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.background.SettingsManager;
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.Self;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserContainer;

/**
 * @author jfperren
 * @author ritterni
 */
public class SelfTest extends AndroidTestCase {

    long selfId = 5;
    String name = "Julien";
    String phoneNumber = "0217465647";
    String email = "julien@epfl.ch";
    Bitmap image = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
    Location location = new Location(Displayable.PROVIDER_NAME);
    double latitude = 43.54574354;
    double longitude = 23.5479584;
    long lastSeen = 47587985;
    String locationName = "Ecublens";
    UserContainer selfValues;

    @Before
    @Override
    protected void setUp() {
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        SettingsManager settings = Mockito.mock(SettingsManager.class);
        Mockito.when(settings.getUserId()).thenReturn(selfId);
        Mockito.when(settings.getUserName()).thenReturn(name);
        Mockito.when(settings.getUPhoneNumber()).thenReturn(phoneNumber);
        Mockito.when(settings.getEmail()).thenReturn(email);
        Mockito.when(settings.getLocation()).thenReturn(location);
        Mockito.when(settings.getLastSeen()).thenReturn(lastSeen);
        Mockito.when(settings.getContext()).thenReturn(this.getContext());
        Mockito.when(settings.getLocationName()).thenReturn(locationName);
        ServiceContainer.setSettingsManager(settings);
    }

    @Test
    public void testReturnSettingsValues() {
        Self self = new Self(image);
        assertTrue(self.getLatLng().latitude == latitude);
        assertTrue(self.getLatLng().longitude == longitude);
        assertTrue(self.getLocation().getLongitude() == longitude);
        assertTrue(self.getLocation().getLatitude() == latitude);
        assertTrue(self.getLocationString().equals(locationName));
        assertTrue(self.getSubtitle().equals("You are near " + locationName));
    }

    @Test
    public void testSelfConstruction() {
        Self self = new Self(image);
        assertTrue(self.getId() == selfId);
        assertTrue(self.getName().equals(name));
        assertTrue(self.getActionImage().sameAs(image));
        assertTrue(self.getBlockStatus().equals(User.BlockStatus.UNBLOCKED));
        assertTrue(self.getFriendship() == User.SELF);
    }
}
