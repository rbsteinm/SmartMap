package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.util.Utils;

/**
 * Describes an event
 * 
 * @author jfperren
 * @author ritterni
 */

public interface Event extends Displayable {

    long NO_ID = -1;
    Set<User> NO_PARTICIPANTS = new HashSet<User>();
    String NO_DESCRIPTION = "This event currently has no description";
    String NO_NAME = "Anonymous Event";
    Calendar NO_START_DATE = GregorianCalendar.getInstance(TimeZone.getDefault());
    Calendar NO_END_DATE = GregorianCalendar.getInstance(TimeZone.getDefault());

    Bitmap DEFAULT_IMAGE = BitmapFactory.decodeResource(Utils.sContext.getResources(),
        R.drawable.default_event);

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

    ImmutableEvent getImmutableCopy();

    String getName();

    List<User> getParticipants();

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

    void update(ImmutableEvent event);
}
