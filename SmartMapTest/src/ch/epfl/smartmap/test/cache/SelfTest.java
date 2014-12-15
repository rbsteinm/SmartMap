package ch.epfl.smartmap.test.cache;

import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.location.Location;
import android.test.AndroidTestCase;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.UserContainer;

/**
 * @author jfperren
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
    UserContainer selfValues;

    @Before
    @Override
    protected void setUp() {
        ServiceContainer.forceInitSmartMapServices(this.getContext());
    }

    @Test
    public void testReturnSettingsValues() {

    }

    private void initContainer() {
        // selfValues = new UserContainer(selfId, name, phoneNumber, email, location, email, image, null, 0, )
    }

}
