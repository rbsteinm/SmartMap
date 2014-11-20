package ch.epfl.smartmap.cache;

import java.util.GregorianCalendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Describes a generic user of the app
 * 
 * @author ritterni
 */
public interface User {

    /**
     * @return The user's ID
     */
    long getID();

    /**
     * @return The user's name
     */
    String getName();

    /**
     * @return The user's phone number
     */
    String getNumber();

    /**
     * @return The user's email address
     */
    String getEmail();

    /**
     * @return The user's position
     */
    Location getLocation();

    /**
     * @return The user's latitude and longitude
     */
    LatLng getLatLng();

    /**
     * @return The user's position as a String (e.g. 'Lausanne')
     */
    String getPositionName();

    /**
     * @return A user picture to display
     */
    Bitmap getPicture(Context context);

    /**
     * @return The date/hour at which the user was last seen
     */
    GregorianCalendar getLastSeen();

    /**
     * Deprecated. Use getLastSeen() instead.
     * 
     * @return True if the user is online
     */
    @Deprecated
    boolean isOnline();

    /**
     * @return True if the user's location is visible
     */
    boolean isVisible();

    /**
     * Sets the user position's name
     * 
     * @param posName
     *            The user's position
     */
    void setPositionName(String posName);

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
     * Sets the user's email
     * 
     * @param newEmail
     *            The new email
     */
    void setEmail(String newEmail);

    /**
     * Sets the user's longitude
     * 
     * @param x
     *            The longitude
     */
    void setLongitude(double x);

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
     * Stores a new profile picture for the user
     * 
     * @param pic
     *            The picture as a Bitmap object
     */
    void setPicture(Bitmap pic, Context context);

    /**
     *
     */
    void deletePicture(Context context);

    /**
     * @param date
     *            The date/hour at which the user was last seen
     */
    void setLastSeen(GregorianCalendar date);

    /**
     * Sets whether or not the user is online. (Deprecated, use setLastSeen()
     * instead)
     * 
     * @param isOnline
     *            True if the user is online
     */
    @Deprecated
    void setOnline(boolean isOnline);

    /**
     * @param isVisible
     *            True if the user is visible
     */
    void setVisible(boolean isVisible);
}