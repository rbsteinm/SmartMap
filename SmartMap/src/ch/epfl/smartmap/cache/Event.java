package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;

/**
 * Describes an event
 * 
 * @author jfperren
 * @author ritterni
 */

public interface Event extends Displayable {

    long NO_ID = -1;
    Set<User> NO_PARTICIPANTS = new HashSet<User>();
    Set<Long> NO_PARTICIPANTIDS = new HashSet<Long>();
    String NO_DESCRIPTION = "This event currently has no description";
    String NO_NAME = "Anonymous Event";
    Calendar NO_START_DATE = GregorianCalendar.getInstance(TimeZone.getDefault());
    Calendar NO_END_DATE = GregorianCalendar.getInstance(TimeZone.getDefault());

    Bitmap DEFAULT_WHITE_IMAGE = BitmapFactory.decodeResource(ServiceContainer.getSettingsManager().getContext()
        .getResources(), R.drawable.ic_event_white);

    Bitmap DEFAULT_BLUE_IMAGE = BitmapFactory.decodeResource(ServiceContainer.getSettingsManager().getContext()
        .getResources(), R.drawable.ic_event_blue);

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
