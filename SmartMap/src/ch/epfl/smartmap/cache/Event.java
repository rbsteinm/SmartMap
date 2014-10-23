package ch.epfl.smartmap.cache;

import java.util.GregorianCalendar;

/**
 * Describes an event
 * @author ritterni
 */
public interface Event {
    
    /**
     * @return The event's name
     */
    String getName();
    
    /**
     * @return The ID of the user who created the event
     */
    int getCreator();
    
    /**
     * @return The event's ID
     */
    int getID();
    
    /**
     * @return The date (year, month, day, hour, minute) at which the event starts
     */
    GregorianCalendar getStartDate();
    
    /**
     * @return The date (year, month, day, hour, minute) at which the event ends
     */
    GregorianCalendar getEndDate();
    
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
    void setID(int newID);
}
