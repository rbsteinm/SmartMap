package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import ch.epfl.smartmap.listeners.EventListener;

/**
 * Describes an event
 * 
 * @author jfperren
 * @author ritterni
 */
public interface Event extends Displayable, Localisable {

    final List<Long> NO_PARTICIPANTS = new ArrayList<Long>();
    final String NO_DESCRIPTION = "NO_DESCRIPTION";
    final long NO_CREATOR_ID = NO_ID;
    final String NO_NAME = "NO_NAME";
    final Calendar NOT_A_DATE = GregorianCalendar.getInstance();

    void addEventListener(EventListener newListener);

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

    Type getType();

    void update(ImmutableEvent event);

    enum Type {
        PUBLIC,
        PRIVATE;
    }
}
