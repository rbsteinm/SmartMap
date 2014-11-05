package ch.epfl.smartmap.cache;

import java.util.GregorianCalendar;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;

/**
 * Describes an event
 * @author ritterni
 */
public interface Event {
    
    /**
     * @return The event's position
     */
    Location getLocation();
    
    /**
	 * @return The user's latitude and longitude
	 */
	LatLng getLatLng();
    
    /**
     * Sets the event's position
     * @param p The new position
     */
    void setLocation(Location p);
    
    /**
     * Sets the events's longitude
     * @param x The longitude
     */
    void setLongitude(double x);
    
    /**
     * Sets the event's latitude
     * @param y The latitude
     */
    void setLatitude(double y);
    
    /**
     * @return The event's name
     */
    String getName();
    
    /**
     * @return The ID of the user who created the event
     */
    long getCreator();
    
    /**
     * @return The event's ID
     */
    long getID();
    
    /**
     * @return The date (year, month, day, hour, minute) at which the event starts
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
     * @param newName The new name
     */
    void setName(String newName);
    
    /**
     * Changes the event's start date
     * @param newDate The new start date (year, month, day, hour, minute)
     */
    void setStartDate(GregorianCalendar newDate);
    
    /**
     * Changes the event's end date
     * @param newDate The new start date (year, month, day, hour, minute)
     */
    void setEndDate(GregorianCalendar newDate);
    
    
    /**
     * Sets the event's ID
     * @param newID The new ID
     */
    void setID(long newID);
    
    /**
     * Sets the user position's name
     * 
     * @param posName
     *            The event's position
     */
    void setPositionName(String posName);
}
