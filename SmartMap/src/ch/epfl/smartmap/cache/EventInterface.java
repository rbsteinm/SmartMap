package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.Set;

/**
 * Describes an event
 * 
 * @author jfperren
 * @author ritterni
 */

public interface EventInterface extends Displayable {

    /**
     * @return The ID of the user who created the event
     */
    User getCreator();

    /**
     * @return The event's description
     */
    String getDescription();

    /**
     * @return The date (year, month, day, hour, minute) at which the event ends
     */
    Calendar getEndDate();

    EventContainer getImmutableCopy();

    String getName();

    Set<Long> getParticipantIds();

    /**
     * @return The date (year, month, day, hour, minute) at which the event
     *         starts
     */
    Calendar getStartDate();

    boolean isGoing();

    boolean isLive();

    boolean isNear();

    boolean isOwn();

    boolean isVisible();

    boolean update(EventContainer event);
}
