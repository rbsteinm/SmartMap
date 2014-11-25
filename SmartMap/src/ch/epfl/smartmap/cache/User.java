package ch.epfl.smartmap.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Parcelable;
import ch.epfl.smartmap.R;

import com.google.android.gms.maps.model.LatLng;

/**
 * Describes a generic user of the app
 * 
 * @author ritterni
 */
public interface User extends Parcelable, Displayable {

    public static final String NO_NAME = "NO_NAME";
    public static final String NO_NUMBER = "NO_NUMBER";
    public static final String NO_EMAIL = "NO_EMAIL";
    public static final String NO_LOCATION = "NO_LOCATION";
    public static final int DEFAULT_PICTURE = R.drawable.ic_default_user; // placeholder
    public static final int IMAGE_QUALITY = 100;
    public static final String PROVIDER_NAME = "SmartMapServers";
    public static final long ONLINE_TIMEOUT = 1000 * 60 * 3; // time in millis

    public static final float MARKER_ANCHOR_X = (float) 0.5;
    public static final float MARKER_ANCHOR_Y = 1;
    public static final int PICTURE_WIDTH = 50;
    public static final int PICTURE_HEIGHT = 50;

    /**
     *
     */
    void deletePicture(Context context);

    /**
     * @return The user's email address
     */
    String getEmail();

    /**
     * @return The user's ID
     */
    @Override
    long getID();

    /**
     * @return The date/hour at which the user was last seen
     */
    Calendar getLastSeen();

    /**
     * @return The user's latitude and longitude
     */

    @Override
    LatLng getLatLng();

    /**
     * @return The user's position
     */
    @Override
    Location getLocation();

    /**
     * @return The user's position as a String (e.g. 'Lausanne')
     */
    String getLocationString();

    /**
     * @return The user's name
     */

    @Override
    String getName();

    /**
     * @return The user's phone number
     */
    String getNumber();

    /**
     * @return A user picture to display
     */
    @Override
    Bitmap getPicture(Context context);

    /**
     * @return True if the user's location is visible
     */
    boolean isVisibleOnMap();

    /**
     * Sets the user's email
     * 
     * @param newEmail
     *            The new email
     */
    void setEmail(String newEmail);

    /**
     * @param date
     *            The date/hour at which the user was last seen
     */
    void setLastSeen(Date date);

    /**
     * Sets the user's latitude
     * 
     * @param y
     *            The latitude
     */
    void setLatitude(double y);

    /**
     * Sets the user's position (x and y)
     * 
     * @param p
     *            The new position
     */
    void setLocation(Location p);

    /**
     * Sets the user's longitude
     * 
     * @param x
     *            The longitude
     */
    void setLongitude(double x);

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
    void setNumber(String newNumber);

    /**
     * Stores a new profile picture for the user
     * 
     * @param pic
     *            The picture as a Bitmap object
     */
    void setPicture(Bitmap pic, Context context) throws FileNotFoundException, IOException;
}