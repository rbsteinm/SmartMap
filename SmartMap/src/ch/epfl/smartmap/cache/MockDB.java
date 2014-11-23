package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import android.location.Location;

/**
 * Static Database
 * 
 * @author jfperren
 */
public class MockDB {
    public static final Friend JULIEN = new Friend(0, "Julien Perrenoud", 46.2547877, 3.2142577);
    public static final Friend ALAIN = new Friend(1, "Alain Milliet", 45.2547877, 7.2142577);
    public static final Friend ROBIN = new Friend(2, "Robin Genolet", 42.2547877, 5.214577);
    public static final Friend MATTHIEU = new Friend(3, "Matthieu Girod", 39.2547877, 5.214577);
    public static final Friend NICOLAS = new Friend(4, "Nicolas Ritter", 50.2547877, 2.004577);
    public static final Friend MARION = new Friend(5, "Marion Sbai", 41.5547877, 2.4255457);
    public static final Friend RAPHAEL = new Friend(6, "Raphael Steinmann", 39.5547877, 7.4255457);
    public static final Friend GUILLAUME = new Friend(7, "Guillaume Clément", 43.5547877, 6.4255457);
    public static final Friend SELINE = new Friend(8, "Seline Eeckhout", 51.2547877, 2.2142577);
    public static final Friend CYRIL = new Friend(9, "Cyril Pulver", 70.2547877, 14.2142577);
    public static final Friend PIETRO = new Friend(10, "Pietro Ortelli", 42.2547877, -5.214577);
    public static final Friend CHRISTIE = new Friend(11, "Christie Palmer", 46.5162802, 6.6698749);
    public static final Friend MARIE = new Friend(12, "Marie Wermeille", 45.2547877, 45.004577);
    public static final Friend HUGO = new Friend(13, "Hugo Sbai", 47.5547877, 4.4255457);
    public static final ArrayList<Friend> FRIENDS_LIST = new ArrayList<Friend>(Arrays.asList(JULIEN, ALAIN,
        ROBIN, MATTHIEU, NICOLAS, MARION, RAPHAEL, HUGO, GUILLAUME, SELINE, CYRIL, PIETRO, CHRISTIE, MARIE));

    public static List<Event> getEventsList() {
        Location locationFootball = new Location("SmartMapServers");
        Location locationConference = new Location("SmartMapServers");
        Location locationBirthday = new Location("SmartMapServers");
        locationFootball.setLatitude(46.8333);
        locationFootball.setLongitude(7.3888);
        locationConference.setLatitude(47.5547877);
        locationConference.setLongitude(4.0);
        locationBirthday.setLatitude(45.8333);
        locationBirthday.setLongitude(7.38677);
        Event footballTournament =
            new UserEvent("Football Tournament", HUGO.getID(), HUGO.getName(), new GregorianCalendar(2014,
                11, 23), new GregorianCalendar(2014, 11, 27), locationFootball);
        Event conference =
            new UserEvent("Conference on cellular networks", HUGO.getID(), HUGO.getName(),
                new GregorianCalendar(2014, 11, 26), new GregorianCalendar(2014, 11, 26), locationConference);
        Event birthday =
            new UserEvent("Marion's birthday", MARION.getID(), MARION.getName(), new GregorianCalendar(2014,
                11, 20), new GregorianCalendar(2014, 11, 20), locationBirthday);
        return Arrays.asList(conference, footballTournament, birthday);
    }
}