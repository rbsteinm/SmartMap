package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;

/**
 * Describes a generic user of the app
 * 
 * @author ritterni
 */

public interface User extends Displayable {

    long NO_ID = -1;
    String NO_NAME = "Unknown User";
    Bitmap NO_IMAGE = BitmapFactory.decodeResource(ServiceContainer.getSettingsManager().getContext()
        .getResources(), R.drawable.ic_default_user);

    String NO_PHONE_NUMBER = "No phone Number";
    String NO_EMAIL = "No email";
    Calendar NO_LAST_SEEN = GregorianCalendar.getInstance();

    User NOBODY = new Stranger(new ImmutableUser(NO_ID, NO_NAME, null, null, null, null, NO_IMAGE, false));

    // ???
    int IMAGE_QUALITY = 100;
    long ONLINE_TIMEOUT = 1000 * 60 * 3; // time in millis
    int PICTURE_WIDTH = 50;
    int PICTURE_HEIGHT = 50;
    double NO_LATITUDE = 0.0;
    double NO_LONGITUDE = 0.0;

    ImmutableUser getImmutableCopy();

    String getName();

    boolean isBlocked();

    boolean isFriend();

    boolean isVisible();

    void update(ImmutableUser user);
}