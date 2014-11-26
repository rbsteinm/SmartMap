package ch.epfl.smartmap.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import ch.epfl.smartmap.R;

import com.google.android.gms.maps.model.LatLng;

/**
 * Describes a generic user of the app
 * 
 * @author ritterni
 */
public interface User extends Displayable {

    String NO_NAME = "NO_NAME";
    String NO_NUMBER = "NO_NUMBER";
    String NO_EMAIL = "NO_EMAIL";
    String NO_LOCATION = "NO_LOCATION";
    int DEFAULT_PICTURE = R.drawable.ic_default_user; // placeholder
    int IMAGE_QUALITY = 100;
    String PROVIDER_NAME = "SmartMapServers";
    long ONLINE_TIMEOUT = 1000 * 60 * 3; // time in millis

    float MARKER_ANCHOR_X = (float) 0.5;
    float MARKER_ANCHOR_Y = 1;
    int PICTURE_WIDTH = 50;
    int PICTURE_HEIGHT = 50;

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
     * @return A user picture to display
     */
    @Override
    Bitmap getImage(Context context);

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
     * Stores a new profile picture for the user
     * 
     * @param newImage
     *            The picture as a Bitmap object
     */
    void setImage(Bitmap newImage, Context context) throws FileNotFoundException, IOException;

    /**
     * @param newDate
     *            The date/hour at which the user was last seen
     */
    void setLastSeen(Date newDate);

    /**
     * Sets the user's latitude
     * 
     * @param newLatitude
     *            The latitude
     */
    void setLatitude(double newLatitude);

    /**
     * Sets the user's position (x and y)
     * 
     * @param newLocation
     *            The new position
     */
    void setLocation(Location newLocation);

    /**
     * Sets the user's longitude
     * 
     * @param newLongitude
     *            The longitude
     */
    void setLongitude(double newLongitude);

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
}