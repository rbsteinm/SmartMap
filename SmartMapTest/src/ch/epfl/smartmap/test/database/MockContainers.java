package ch.epfl.smartmap.test.database;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TimeZone;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.location.Location;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.EventContainer;
import ch.epfl.smartmap.cache.FilterContainer;
import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.cache.InvitationContainer;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserContainer;

import com.google.common.collect.Sets;

/**
 * Static Mocks
 * 
 * @author jfperren
 */
public class MockContainers {

    public static final UserContainer JULIEN;
    public static final long JULIEN_ID = 5675675;
    public static final String JULIEN_NAME = "Julien";
    public static final String JULIEN_EMAIL = "julien@epfl.ch";
    public static final String JULIEN_PHONE_NUMBER = "1234";
    public static final Bitmap JULIEN_IMAGE = Bitmap.createBitmap(1, 2, Config.ALPHA_8);
    public static final User.BlockStatus JULIEN_BLOCK_STATUS = User.BlockStatus.UNBLOCKED;
    public static final Location JULIEN_LOCATION = new Location(User.PROVIDER_NAME);
    public static final String JULIEN_LOCATION_STRING = "Bahamas";
    public static final double JULIEN_LATITUDE = 54.94890;
    public static final double JULIEN_LONGITUDE = 57.58984;
    public static final long JULIEN_LAST_SEEN = 59080598;
    public static final int JULIEN_FRIENDSHIP = User.SELF;

    static {
        JULIEN_LOCATION.setLatitude(JULIEN_LATITUDE);
        JULIEN_LOCATION.setLongitude(JULIEN_LONGITUDE);
        JULIEN_LOCATION.setTime(JULIEN_LAST_SEEN);
        JULIEN =
            new UserContainer(JULIEN_ID, JULIEN_NAME, JULIEN_PHONE_NUMBER, JULIEN_EMAIL, JULIEN_LOCATION,
                JULIEN_LOCATION_STRING, JULIEN_IMAGE, JULIEN_BLOCK_STATUS, JULIEN_FRIENDSHIP);
    }

    public static final UserContainer ALAIN;
    public static final long ALAIN_ID = 5675676;
    public static final String ALAIN_NAME = "Alain";
    public static final String ALAIN_EMAIL = "alain@epfl.ch";
    public static final String ALAIN_PHONE_NUMBER = "12345";
    public static final Bitmap ALAIN_IMAGE = Bitmap.createBitmap(2, 2, Config.ALPHA_8);
    public static final User.BlockStatus ALAIN_BLOCK_STATUS = User.BlockStatus.UNBLOCKED;
    public static final Location ALAIN_LOCATION = new Location(User.PROVIDER_NAME);
    public static final String ALAIN_LOCATION_STRING = "Pully";
    public static final double ALAIN_LATITUDE = 55.94890;
    public static final double ALAIN_LONGITUDE = 53.58984;
    public static final long ALAIN_LAST_SEEN = 59233598;
    public static final int ALAIN_FRIENDSHIP = User.FRIEND;

    static {
        ALAIN_LOCATION.setLatitude(ALAIN_LATITUDE);
        ALAIN_LOCATION.setLongitude(ALAIN_LONGITUDE);
        ALAIN_LOCATION.setTime(ALAIN_LAST_SEEN);
        ALAIN =
            new UserContainer(ALAIN_ID, ALAIN_NAME, ALAIN_PHONE_NUMBER, ALAIN_EMAIL, ALAIN_LOCATION,
                ALAIN_LOCATION_STRING, ALAIN_IMAGE, ALAIN_BLOCK_STATUS, ALAIN_FRIENDSHIP);
    }

    public static final UserContainer ROBIN;
    public static final long ROBIN_ID = 65762;
    public static final String ROBIN_NAME = "Robin";
    public static final String ROBIN_EMAIL = "robin@epfl.ch";
    public static final String ROBIN_PHONE_NUMBER = "12346";
    public static final Bitmap ROBIN_IMAGE = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
    public static final User.BlockStatus ROBIN_BLOCK_STATUS = User.BlockStatus.UNBLOCKED;
    public static final Location ROBIN_LOCATION = new Location(User.PROVIDER_NAME);
    public static final String ROBIN_LOCATION_STRING = "Ici";
    public static final double ROBIN_LATITUDE = 59.94890;
    public static final double ROBIN_LONGITUDE = 27.58984;
    public static final long ROBIN_LAST_SEEN = 59080438;
    public static final int ROBIN_FRIENDSHIP = User.STRANGER;

    static {
        ROBIN_LOCATION.setLatitude(ROBIN_LATITUDE);
        ROBIN_LOCATION.setLongitude(ROBIN_LONGITUDE);
        ROBIN_LOCATION.setTime(ROBIN_LAST_SEEN);
        ROBIN =
            new UserContainer(ROBIN_ID, ROBIN_NAME, ROBIN_PHONE_NUMBER, ROBIN_EMAIL, ROBIN_LOCATION,
                ROBIN_LOCATION_STRING, ROBIN_IMAGE, ROBIN_BLOCK_STATUS, ROBIN_FRIENDSHIP);
    }

    public static final UserContainer NULL_USER_VALUES;

    static {
        NULL_USER_VALUES =
            new UserContainer(User.NO_ID, null, null, null, null, null, null, null, User.NO_FRIENDSHIP);
    }

    public static final UserContainer UNSET_USER_VALUES;

    static {
        UNSET_USER_VALUES =
            new UserContainer(User.NO_ID, User.NO_NAME, User.NO_PHONE_NUMBER, User.NO_EMAIL,
                User.NO_LOCATION, User.NO_LOCATION_STRING, User.NO_IMAGE, User.BlockStatus.NOT_SET,
                User.NO_FRIENDSHIP);
    }

    public static final UserContainer WRONG_USER_VALUES;
    public static final long WRONG_USER_ID = -72;
    public static final String WRONG_USER_NAME = "";
    public static final Location WRONG_USER_LOCATION = new Location(User.PROVIDER_NAME);
    public static final String WRONG_USER_LOCATION_STRING = null;
    public static final double WRONG_USER_LATITUDE = -259.94890;
    public static final double WRONG_USER_LONGITUDE = -2127.58984;
    public static final long WRONG_USER_LAST_SEEN = -59080438;
    public static final int WRONG_USER_FRIENDSHIP = -5;

    static {
        WRONG_USER_LOCATION.setLatitude(WRONG_USER_LATITUDE);
        WRONG_USER_LOCATION.setLongitude(WRONG_USER_LONGITUDE);
        WRONG_USER_LOCATION.setTime(WRONG_USER_LAST_SEEN);
        WRONG_USER_VALUES =
            new UserContainer(WRONG_USER_ID, null, null, null, WRONG_USER_LOCATION, null, null, null,
                WRONG_USER_FRIENDSHIP);
    }

    public static final EventContainer POLYLAN;
    public static final long POLYLAN_ID = 673;
    public static final String POLYLAN_NAME = "Polylan";
    public static final String POLYLAN_DESCRIPTION = "This is Polylan";
    public static final long POLYLAN_START_TIME = 5809840;
    public static final Calendar POLYLAN_START_DATE = GregorianCalendar.getInstance(TimeZone
        .getTimeZone("GMT+01:00"));
    public static final long POLYLAN_END_TIME = 580985098;
    public static final Calendar POLYLAN_END_DATE = GregorianCalendar.getInstance(TimeZone
        .getTimeZone("GMT+01:00"));
    public static final Location POLYLAN_LOCATION = new Location(User.PROVIDER_NAME);
    public static final String POLYLAN_LOCATION_STRING = "Rolex";
    public static final double POLYLAN_LATITUDE = 59.94890;
    public static final double POLYLAN_LONGITUDE = 27.58984;
    public static final Set<Long> POLYLAN_PARTICIPANTS = Sets.newHashSet((long) 5, (long) 6, (long) 2);

    static {
        POLYLAN_LOCATION.setLatitude(POLYLAN_LATITUDE);
        POLYLAN_LOCATION.setLongitude(POLYLAN_LONGITUDE);
        POLYLAN_START_DATE.setTimeInMillis(POLYLAN_START_TIME);
        POLYLAN_END_DATE.setTimeInMillis(POLYLAN_END_TIME);
        POLYLAN =
            new EventContainer(POLYLAN_ID, POLYLAN_NAME, JULIEN, POLYLAN_DESCRIPTION, POLYLAN_START_DATE,
                POLYLAN_END_DATE, POLYLAN_LOCATION, POLYLAN_LOCATION_STRING, POLYLAN_PARTICIPANTS);
    }

    public static final EventContainer FOOTBALL_TOURNAMENT;
    public static final long FOOTBALL_TOURNAMENT_ID = 673;
    public static final String FOOTBALL_TOURNAMENT_NAME = "Polylan";
    public static final String FOOTBALL_TOURNAMENT_DESCRIPTION = "This is Polylan";
    public static final long FOOTBALL_TOURNAMENT_START_TIME = 5809840;
    public static final Calendar FOOTBALL_TOURNAMENT_START_DATE = GregorianCalendar.getInstance(TimeZone
        .getTimeZone("GMT+01:00"));
    public static final long FOOTBALL_TOURNAMENT_END_TIME = 580985098;
    public static final Calendar FOOTBALL_TOURNAMENT_END_DATE = GregorianCalendar.getInstance(TimeZone
        .getTimeZone("GMT+01:00"));
    public static final Location FOOTBALL_TOURNAMENT_LOCATION = new Location(User.PROVIDER_NAME);
    public static final String FOOTBALL_TOURNAMENT_LOCATION_STRING = "Rolex";
    public static final double FOOTBALL_TOURNAMENT_LATITUDE = 59.94890;
    public static final double FOOTBALL_TOURNAMENT_LONGITUDE = 27.58984;
    public static final Set<Long> FOOTBALL_TOURNAMENT_PARTICIPANTS = Sets.newHashSet((long) 5, (long) 6,
        (long) 2);

    static {
        FOOTBALL_TOURNAMENT_LOCATION.setLatitude(FOOTBALL_TOURNAMENT_LATITUDE);
        FOOTBALL_TOURNAMENT_LOCATION.setLongitude(FOOTBALL_TOURNAMENT_LONGITUDE);
        FOOTBALL_TOURNAMENT_START_DATE.setTimeInMillis(FOOTBALL_TOURNAMENT_START_TIME);
        FOOTBALL_TOURNAMENT_END_DATE.setTimeInMillis(FOOTBALL_TOURNAMENT_END_TIME);
        FOOTBALL_TOURNAMENT =
            new EventContainer(FOOTBALL_TOURNAMENT_ID, FOOTBALL_TOURNAMENT_NAME, ALAIN,
                FOOTBALL_TOURNAMENT_DESCRIPTION, FOOTBALL_TOURNAMENT_START_DATE,
                FOOTBALL_TOURNAMENT_END_DATE, FOOTBALL_TOURNAMENT_LOCATION,
                FOOTBALL_TOURNAMENT_LOCATION_STRING, FOOTBALL_TOURNAMENT_PARTICIPANTS);
    }

    public static final EventContainer NULL_EVENT_VALUES;

    static {
        NULL_EVENT_VALUES = new EventContainer(Event.NO_ID, null, null, null, null, null, null, null, null);
    }

    public static final EventContainer UNSET_EVENT_VALUES;

    static {
        UNSET_EVENT_VALUES =
            new EventContainer(Event.NO_ID, Event.NO_NAME, User.NOBODY.getContainerCopy(),
                Event.NO_DESCRIPTION, Event.NO_START_DATE, Event.NO_END_DATE, Event.NO_LOCATION,
                Event.NO_LOCATION_STRING, Event.NO_PARTICIPANTIDS);
    }

    public static final FilterContainer FAMILY;
    public static final long FAMILY_ID = 1;
    public static final String FAMILY_NAME = "Family";
    public static final Set<Long> FAMILY_IDS = Sets.newHashSet((long) 1, (long) 2);
    public static final boolean FAMILY_IS_VISIBLE = true;

    static {
        FAMILY = new FilterContainer(FAMILY_ID, FAMILY_NAME, FAMILY_IDS, FAMILY_IS_VISIBLE);
    }

    public static final InvitationContainer ROBIN_FRIEND_INVITATION;
    public static final long ROBIN_FRIEND_INVITATION_ID = 776;
    public static final int ROBIN_FRIEND_INVITATION_STATUS = Invitation.UNREAD;
    public static final long ROBIN_FRIEND_INVITATION_TIMESTAMP = 679086708;
    public static final int ROBIN_FRIEND_INVITATION_TYPE = Invitation.FRIEND_INVITATION;

    static {
        ROBIN_FRIEND_INVITATION =
            new InvitationContainer(ROBIN_FRIEND_INVITATION_ID, ROBIN, null, ROBIN_FRIEND_INVITATION_STATUS,
                ROBIN_FRIEND_INVITATION_TIMESTAMP, ROBIN_FRIEND_INVITATION_TYPE);
    }

    public static final InvitationContainer POLYLAN_EVENT_INVITATION;
    public static final long POLYLAN_EVENT_INVITATION_ID = 765;
    public static final int POLYLAN_EVENT_INVITATION_STATUS = Invitation.READ;
    public static final long POLYLAN_EVENT_INVITATION_TIMESTAMP = 6790867;
    public static final int POLYLAN_EVENT_INVITATION_TYPE = Invitation.EVENT_INVITATION;

    static {
        POLYLAN_EVENT_INVITATION =
            new InvitationContainer(POLYLAN_EVENT_INVITATION_ID, null, POLYLAN,
                POLYLAN_EVENT_INVITATION_STATUS, POLYLAN_EVENT_INVITATION_TIMESTAMP,
                POLYLAN_EVENT_INVITATION_TYPE);
    }
}