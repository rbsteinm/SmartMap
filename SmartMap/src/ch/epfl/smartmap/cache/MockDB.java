package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Static Database
 * 
 * @author jfperren
 */
public class MockDB {
    public static final Friend JULIEN = new Friend(new ImmutableUser(0, "Julien Perrenoud",
        User.NO_PHONE_NUMBER, User.NO_EMAIL, User.NO_LOCATION, User.NO_LOCATION_STRING, User.NO_IMAGE));
    public static final Friend ALAIN = new Friend(new ImmutableUser(1, "Alain Milliet", User.NO_PHONE_NUMBER,
        User.NO_EMAIL, User.NO_LOCATION, User.NO_LOCATION_STRING, User.NO_IMAGE));
    public static final Friend ROBIN = new Friend(new ImmutableUser(2, "Robin Genolet", User.NO_PHONE_NUMBER,
        User.NO_EMAIL, User.NO_LOCATION, User.NO_LOCATION_STRING, User.NO_IMAGE));
    public static final Friend MATTHIEU = new Friend(new ImmutableUser(3, "Matthieu Girod",
        User.NO_PHONE_NUMBER, User.NO_EMAIL, User.NO_LOCATION, User.NO_LOCATION_STRING, User.NO_IMAGE));
    public static final Friend NICOLAS = new Friend(new ImmutableUser(4, "Nicolas Ritter",
        User.NO_PHONE_NUMBER, User.NO_EMAIL, User.NO_LOCATION, User.NO_LOCATION_STRING, User.NO_IMAGE));
    public static final Friend MARION = new Friend(new ImmutableUser(5, "Marion Sbai", User.NO_PHONE_NUMBER,
        User.NO_EMAIL, User.NO_LOCATION, User.NO_LOCATION_STRING, User.NO_IMAGE));
    public static final Friend GUILLAUME = new Friend(new ImmutableUser(6, "Guillaume Clement",
        User.NO_PHONE_NUMBER, User.NO_EMAIL, User.NO_LOCATION, User.NO_LOCATION_STRING, User.NO_IMAGE));
    public static final Friend SELINE = new Friend(new ImmutableUser(7, "Seline Eeckhout",
        User.NO_PHONE_NUMBER, User.NO_EMAIL, User.NO_LOCATION, User.NO_LOCATION_STRING, User.NO_IMAGE));
    public static final Friend CYRIL = new Friend(new ImmutableUser(8, "Cyril Pulver", User.NO_PHONE_NUMBER,
        User.NO_EMAIL, User.NO_LOCATION, User.NO_LOCATION_STRING, User.NO_IMAGE));
    public static final Friend PIETRO = new Friend(new ImmutableUser(9, "Pietro Otelli",
        User.NO_PHONE_NUMBER, User.NO_EMAIL, User.NO_LOCATION, User.NO_LOCATION_STRING, User.NO_IMAGE));
    public static final Friend CHRISTIE = new Friend(new ImmutableUser(10, "Christie Palmer",
        User.NO_PHONE_NUMBER, User.NO_EMAIL, User.NO_LOCATION, User.NO_LOCATION_STRING, User.NO_IMAGE));
    public static final Friend MARIE = new Friend(new ImmutableUser(11, "Marie Wermeille",
        User.NO_PHONE_NUMBER, User.NO_EMAIL, User.NO_LOCATION, User.NO_LOCATION_STRING, User.NO_IMAGE));
    public static final Friend HUGO = new Friend(new ImmutableUser(12, "Hugo Sbai", User.NO_PHONE_NUMBER,
        User.NO_EMAIL, User.NO_LOCATION, User.NO_LOCATION_STRING, User.NO_IMAGE));
    public static final Friend RAPHAEL = new Friend(new ImmutableUser(13, "Raphael Steinmann",
        User.NO_PHONE_NUMBER, User.NO_EMAIL, User.NO_LOCATION, User.NO_LOCATION_STRING, User.NO_IMAGE));

    // public static final PublicEvent FOOTBALL_TOURNAMENT = new PublicEvent("Football Tournament",
    // HUGO.getID(), HUGO.getName(), new GregorianCalendar(2014, 11, 23),
    // new GregorianCalendar(2014, 11, 27), new Location("SmartMapServers"));
    // public static final PublicEvent CONFERENCE = new PublicEvent("Conference on cellular networks",
    // HUGO.getID(), HUGO.getName(), new GregorianCalendar(2014, 11, 26),
    // new GregorianCalendar(2014, 11, 26), new Location("Lausanne"));
    // public static final PublicEvent MARION_BIRTHDAY = new PublicEvent("Marion's birthday", MARION.getID(),
    // MARION.getName(), new GregorianCalendar(2014, 11, 20), new GregorianCalendar(2014, 11, 20),
    // new Location("Marion's"));

    public static final ArrayList<Displayable> FRIENDS_LIST = new ArrayList<Displayable>(Arrays.asList(
        JULIEN, ALAIN, ROBIN, MATTHIEU, NICOLAS, MARION, RAPHAEL, HUGO, GUILLAUME, SELINE, CYRIL, PIETRO,
        CHRISTIE, MARIE));

    public static final Filter SWENG_TEAM = new DefaultFilter("Sweng Team");
    public static final Filter EPFL_FRIENDS = new DefaultFilter("EPFL Friends");

    public static void fillFilters() {
        SWENG_TEAM.addUser(RAPHAEL.getId());
        SWENG_TEAM.addUser(NICOLAS.getId());
        SWENG_TEAM.addUser(ALAIN.getId());
        SWENG_TEAM.addUser(JULIEN.getId());
        SWENG_TEAM.addUser(ROBIN.getId());
        SWENG_TEAM.addUser(MATTHIEU.getId());
        SWENG_TEAM.addUser(MARION.getId());
        SWENG_TEAM.addUser(HUGO.getId());

        EPFL_FRIENDS.addUser(GUILLAUME.getId());
        EPFL_FRIENDS.addUser(SELINE.getId());
        EPFL_FRIENDS.addUser(CHRISTIE.getId());
        EPFL_FRIENDS.addUser(MARIE.getId());

    }

    // public static final ArrayList<Displayable> EVENTS_LIST = new ArrayList<Displayable>(Arrays.asList(
    // FOOTBALL_TOURNAMENT, CONFERENCE, MARION_BIRTHDAY));
    //
    // public static List<Event> getEventsList() {
    // Location locationFootball = new Location("SmartMapServers");
    // Location locationConference = new Location("SmartMapServers");
    // Location locationBirthday = new Location("SmartMapServers");
    // locationFootball.setLatitude(46.8333);
    // locationFootball.setLongitude(7.3888);
    // locationConference.setLatitude(47.5547877);
    // locationConference.setLongitude(4.0);
    // locationBirthday.setLatitude(45.8333);
    // locationBirthday.setLongitude(7.38677);
    // Event footballTournament = new PublicEvent("Football Tournament", HUGO.getID(), HUGO.getName(),
    // new GregorianCalendar(2014, 11, 23), new GregorianCalendar(2014, 11, 27), locationFootball);
    // Event conference = new PublicEvent("Conference on cellular networks", HUGO.getID(), HUGO.getName(),
    // new GregorianCalendar(2014, 11, 26), new GregorianCalendar(2014, 11, 26), locationConference);
    // Event birthday = new PublicEvent("Marion's birthday", MARION.getID(), MARION.getName(),
    // new GregorianCalendar(2014, 11, 20), new GregorianCalendar(2014, 11, 20), locationBirthday);
    // return Arrays.asList(conference, footballTournament, birthday);
    // }
}