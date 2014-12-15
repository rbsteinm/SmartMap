package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.Set;

/**
 * Describes an Event that was created by an {@code User}, has a position on the Map, a start and end date, a
 * list of participants and other informations such as a name, a description, ...
 * 
 * @author jfperren
 * @author ritterni
 */

public interface EventInterface extends Displayable {

    /**
     * @return an {@code EventContainer} with all informations about this Event.
     */
    EventContainer getContainerCopy();

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

    /**
     * @return The name of the Event
     */
    String getName();

    /**
     * @return a {@code Set} containing the ids of the people participating
     */
    Set<Long> getParticipantIds();

    /**
     * @return The date (year, month, day, hour, minute) at which the event
     *         starts
     */
    Calendar getStartDate();

    /**
     * @return {@code True} if {@code Self} is participating to this event
     */
    boolean isGoing();

    /**
     * @return {@code True} if this Event is currently happening
     */
    boolean isLive();

    /**
     * @return {@code True} if the Event is near {@code Self} (According to the near radius of
     *         {@code SettingsManager}
     */
    boolean isNear();

    /**
     * @return {@code True} if {@code Self} created this event
     */
    boolean isOwn();

    /**
     * @return {@code True} if this Event should be displayed on the Map.
     */
    boolean isVisible();

    /**
     * Updates all informations with the ones given in the {@code EventContainer}. If you don't want to update
     * some field, just put {@code null} or a default value ({@code NO_NAME, NOBODY,} ...). The
     * {@code EventContainer} MUST have the same Id otherwise it will throw an
     * {@code IllegalArgumentException}.
     * 
     * @param event
     *            the {@code EventContainer} with new informations
     * @return {@code True} if any of the values changed.
     */
    boolean update(EventContainer event);
}
