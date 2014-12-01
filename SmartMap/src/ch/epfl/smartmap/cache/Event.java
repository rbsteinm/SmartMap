package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Describes an event
 * 
 * @author jfperren
 * @author ritterni
 */
public interface Event extends Displayable {

    final List<Long> NO_PARTICIPANTS = new ArrayList<Long>();
    final String NO_DESCRIPTION = "This event currently has no description";

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

    List<Long> getParticipants();

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
