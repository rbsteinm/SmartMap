package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Describes an event
 * 
 * @author ritterni
 */
public interface Event extends Displayable, Localisable {

    static final List<Long> NO_PARTICIPANTS = new ArrayList<Long>();
    static final String NO_DESCRIPTION = "NO_DESCRIPTION";
    static final long NO_CREATOR_ID = NO_ID;
    static final String NO_NAME = "NO_NAME";
    static final Calendar NOT_A_DATE = GregorianCalendar.getInstance();

    void addEventListener(EventListener newListener);

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

    boolean isPublic();

    void removeEventListener(EventListener oldListener);

    void removeParticipant(Long id);

    /**
     * Sets the event creator's name
     * 
     * @param name
     *            The event creator's name
     */
    void setCreatorId(long id);

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
