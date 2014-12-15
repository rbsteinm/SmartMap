package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.Set;

import android.location.Location;

/**
 * An event that can be seen on the map by anybody
 * 
 * @author jfperren
 * @author ritterni
 */

public class PublicEvent extends Event {

    /**
     * Constructor
     * 
     * @param id
     * @param name
     * @param creator
     * @param startDate
     * @param endDate
     * @param location
     * @param locationString
     * @param description
     * @param participantIds
     */
    public PublicEvent(long id, String name, User creator, Calendar startDate, Calendar endDate,
        Location location, String locationString, String description, Set<Long> participantIds) {
        super(id, name, creator, startDate, endDate, location, locationString, description, participantIds);
    }
}
