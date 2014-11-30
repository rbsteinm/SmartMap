package ch.epfl.smartmap.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.graphics.Bitmap;
import ch.epfl.smartmap.listeners.UserListener;

/**
 * Describes a generic user of the app
 * 
 * @author ritterni
 */
public interface User extends Displayable, Markable {

    String NO_NUMBER = "No phone Number";
    String NO_EMAIL = "No email";
    String NO_NAME = "No name";
    Calendar NO_LAST_SEEN = GregorianCalendar.getInstance(TimeZone.getDefault());
    Bitmap NO_IMAGE = null; // R.drawable.ic_default_user; // placeholder

    User NOBODY = null;

    User NOT_FOUND = null;
    int IMAGE_QUALITY = 100;

    long ONLINE_TIMEOUT = 1000 * 60 * 3; // time in millis

    float MARKER_ANCHOR_X = (float) 0.5;

    float MARKER_ANCHOR_Y = 1;
    int PICTURE_WIDTH = 50;
    int PICTURE_HEIGHT = 50;
    double NO_LATITUDE = 0.0;
    double NO_LONGITUDE = 0.0;

    void addUserListener(UserListener newListener);

    /**
     * @return The user's email address
     */
    String getEmail();

    /**
     * @return The user's name
     */
    /**
     * @return The date/hour at which the user was last seen
     */
    Calendar getLastSeen();

    String getName();

    String getPhoneNumber();

    Type getType();

    void removeUserListener(UserListener oldListener);

    /**
     * Sets the user's email
     * 
     * @param newEmail
     *            The new email
     */
    void setEmail(String newEmail);

    /**
     * Stores a new profile picture for the user
     * 
     * @param newImage
     *            The picture as a Bitmap object
     */
    void setImage(Bitmap newImage) throws FileNotFoundException, IOException;

    /**
     * Sets the user's name
     * 
     * @param newName
     *            The new name
     */
    void setName(String newName);

    /**
     * Sets the user's phone number
     * 
     * @param newNumber
     *            The new phone number
     */
    void setPhoneNumber(String newNumber);

    public enum Type {
        FRIEND,
        STRANGER;
    }
}