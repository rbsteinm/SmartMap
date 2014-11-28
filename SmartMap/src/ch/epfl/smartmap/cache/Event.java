package ch.epfl.smartmap.cache;

import java.util.Calendar;

/**
 * Describes an event
 * 
 * @author ritterni
 */
public interface Event extends Displayable, Localisable {

    public static final PublicEvent NOT_FOUND = null;

    void addParticipant(Long id);

    /**
     * @return The ID of the user who created the event
     */
    long getCreatorId();

    /**
     * @return The event's description
     */
    String getDescription();

    /**
     * @return The date (year, month, day, hour, minute) at which the event ends
     */
    Calendar getEndDate();

    String getName();

    /**
     * @return The date (year, month, day, hour, minute) at which the event
     *         starts
     */
    Calendar getStartDate();

    void removeParticipant(Long id);

    /**
     * Sets the event creator's name
     * 
     * @param name
     *            The event creator's name
     */
    void setCreator(long id);

    /**
     * Sets the event's description
     * 
     * @param desc
     *            The new description
     */
    void setDescription(String description);

    /**
     * Changes the event's end date
     * 
     * @param newDate
     *            The new start date (year, month, day, hour, minute)
     */
    void setEndDate(Calendar newDate);

    /**
     * Changes the event's name
     * 
     * @param newName
     *            The new name
     */
    void setName(String newName);

    /**
     * Changes the event's start date
     * 
     * @param newDate
     *            The new start date (year, month, day, hour, minute)
     */
    void setStartDate(Calendar newDate);
}
