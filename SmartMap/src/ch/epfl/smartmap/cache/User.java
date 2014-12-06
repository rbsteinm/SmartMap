package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.gui.Utils;

/**
 * Describes a generic user of the app
 * 
 * @author ritterni
 */

public interface User extends Displayable {

    String NO_PHONE_NUMBER = "No phone Number";

    String NO_EMAIL = "No email";

    Calendar NO_LAST_SEEN = GregorianCalendar.getInstance(TimeZone.getDefault());

    Bitmap NO_IMAGE = BitmapFactory.decodeResource(Utils.sContext.getResources(), R.drawable.ic_default_user);

    User NOBODY = null;

    User NOT_FOUND = null;

    Boolean DEFAULT_BLOCK_VALUE = false;

    int IMAGE_QUALITY = 100;

    long ONLINE_TIMEOUT = 1000 * 60 * 3; // time in millis

    float MARKER_ANCHOR_X = (float) 0.5;

    float MARKER_ANCHOR_Y = 1;
    int PICTURE_WIDTH = 50;
    int PICTURE_HEIGHT = 50;
    double NO_LATITUDE = 0.0;
    double NO_LONGITUDE = 0.0;

    /**
     * @return The user's email address
     */
    String getEmail();

    ImmutableUser getImmutableCopy();

    /**
     * @return The user's name
     */
    /**
     * @return The date/hour at which the user was last seen
     */
    Calendar getLastSeen();

    String getName();

    String getPhoneNumber();

    boolean isBlocked();

    boolean isFriend();

    void update(ImmutableUser user);
}