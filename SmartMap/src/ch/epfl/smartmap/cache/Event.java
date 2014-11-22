package ch.epfl.smartmap.cache;

import java.util.GregorianCalendar;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Describes an event
 * 
 * @author ritterni
 */
public interface Event {

    /**
     * @return The event's position
     */
    Location getLocation();

    /**
     * @return The event's latitude and longitude in the LatLng format
     */
    LatLng getLatLng();

    /**
     * Sets the event's position
     * 
     * @param p
     *            The new position
     */
    void setLocation(Location p);

    /**
     * Sets the events's longitude
     * 
     * @param x
     *            The longitude
     */
    void setLongitude(double x);

    /**
     * Sets the event's latitude
     * 
     * @param y
     *            The latitude
     */
    void setLatitude(double y);

    /**
     * @return The event's name
     */
    String getName();

    /**
     * @return The event's description
     */
    String getDescription();

    /**
     * @return The ID of the user who created the event
     */
    long getCreator();

    /**
     * @return The name of the event creator
     */
    String getCreatorName();

    /**
     * @return The event's ID
     */
    long getID();

    /**
     * @return The date (year, month, day, hour, minute) at which the event
     *         starts
     */
    GregorianCalendar getStartDate();

    /**
     * @return The date (year, month, day, hour, minute) at which the event ends
     */
    GregorianCalendar getEndDate();

    /**
     * @return The event's position as a String (e.g. 'Lausanne')
     */
    String getPositionName();

    /**
     * Changes the event's name
     * 
     * @param newName
     *            The new name
     */
    void setName(String newName);

    /**
     * Sets the event's description
     * 
     * @param desc
     *            The new description
     */
    void setDescription(String desc);

    /**
     * Changes the event's start date
     * 
     * @param newDate
     *            The new start date (year, month, day, hour, minute)
     */
    void setStartDate(GregorianCalendar newDate);

    /**
     * Changes the event's end date
     * 
     * @param newDate
     *            The new start date (year, month, day, hour, minute)
     */
    void setEndDate(GregorianCalendar newDate);

    /**
     * Sets the event's ID
     * 
     * @param newID
     *            The new ID
     */
    void setID(long newID);

    /**
     * Sets the user position's name
     * 
     * @param posName
     *            The event's position
     */
    void setPositionName(String posName);

    /**
     * Sets the event creator's name
     * 
     * @param name
     *            The event creator's name
     */
    void setCreatorName(String name);
}
