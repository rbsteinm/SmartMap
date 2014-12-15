package ch.epfl.smartmap.servercom;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;
import ch.epfl.smartmap.cache.EventContainer;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserContainer;
import ch.epfl.smartmap.util.Utils;

/**
 * A {@link SmartMapParser} implementation that parses objects from Json format
 * 
 * @author marion-S
 * @author SpicyCH (code reviewed 02.11.2014) : changed Error to error as the
 *         server uses a lower case.
 */

public class JsonSmartMapParser implements SmartMapParser {

    private static final String ERROR_STATUS = "error";
    private static final String FEEDBACK_STATUS = "feedback";

    private static final int DATETIME_FORMAT_PARTS = 2;
    private static final int DATE_FORMAT_PARTS = 3;
    private static final int TIME_FORMAT_PARTS = 3;

    private static final int MAX_MONTHS_NUMBER = 11;
    private static final int MAX_DAYS_NUMBER = 31;
    private static final int MAX_HOURS_NUMBER = 23;
    private static final int MAX_MINUTES_NUMBER = 59;
    private static final int MAX_SECONDS_NUMBER = 59;

    private static final int UNITIALIZED_LATITUDE = -200;
    private static final int UNITIALIZED_LONGITUDE = -200;
    private static final int MIN_LATITUDE = -90;
    private static final int MAX_LATITUDE = 90;
    private static final int MIN_LONGITUDE = -180;
    private static final int MAX_LONGITUDE = 180;
    private static final int MAX_NAME_LENGTH = 60;
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_EVENT_DESCRIPTION_LENGTH = 255;

    private static final String LATITUDE_STRING = "latitude";
    private static final String LONGITUDE_STRING = "longitude";
    private static final String SMART_MAP_SERVER = "SmartMapServers";

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.servercom.SmartMapParser#checkServerError(java.lang.
     * String)
     */
    @Override
    public void checkServerError(String s) throws SmartMapParseException, SmartMapClientException {

        String status = null;
        String message = null;
        try {
            JSONObject jsonObject = new JSONObject(s);
            status = jsonObject.getString("status");
            message = jsonObject.getString("message");
        } catch (JSONException e) {
            throw new SmartMapParseException(e);
        }
        if (status.equals(ERROR_STATUS)) {
            throw new SmartMapClientException(message);
        }
        if (status.equals(FEEDBACK_STATUS)) {
            throw new ServerFeedbackException(message);
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapParser#parseEvent(java.lang.String)
     */
    @Override
    public EventContainer parseEvent(String s) throws SmartMapParseException {
        JSONObject jsonObject = null;
        JSONObject eventJsonObject = null;
        try {
            jsonObject = new JSONObject(s);
            eventJsonObject = jsonObject.getJSONObject("event");
        } catch (JSONException e) {
            throw new SmartMapParseException(e);
        }
        return this.parseEventFromJSON(eventJsonObject);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapParser#parseEventList(java.lang.String)
     */
    @Override
    public List<EventContainer> parseEventList(String s) throws SmartMapParseException {
        List<EventContainer> events = new ArrayList<EventContainer>();

        try {
            JSONObject jsonObject = new JSONObject(s);

            JSONArray eventsArray = jsonObject.getJSONArray("events");

            for (int i = 0; i < eventsArray.length(); i++) {
                JSONObject eventJSON = eventsArray.getJSONObject(i);
                EventContainer event = this.parseEventFromJSON(eventJSON);
                events.add(event);
                Log.d("events", event.getName());
            }
        } catch (JSONException e) {
            throw new SmartMapParseException(e);
        }

        return events;
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.servercom.SmartMapParser#parseFriend(java.lang.String)
     */
    @Override
    public UserContainer parseFriend(String s) throws SmartMapParseException {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);
        } catch (JSONException e) {
            throw new SmartMapParseException(e);
        }
        return this.parseFriendFromJSON(jsonObject);
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.servercom.SmartMapParser#parseFriends(java.lang.String)
     */
    @Override
    public List<UserContainer> parseFriendList(String s, String key) throws SmartMapParseException {

        List<UserContainer> friends = new ArrayList<UserContainer>();

        try {
            JSONObject jsonObject = new JSONObject(s);

            JSONArray usersArray = jsonObject.getJSONArray(key);

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userJSON = usersArray.getJSONObject(i);
                UserContainer friend = this.parseFriendFromJSON(userJSON);
                friends.add(friend);
            }
        } catch (JSONException e) {
            throw new SmartMapParseException(e);
        }

        return friends;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapParser#parseId(java.lang.String)
     */
    @Override
    public Long parseId(String s) throws SmartMapParseException {
        long id = -1;
        try {
            JSONObject jsonObject = new JSONObject(s);
            id = jsonObject.getLong("id");

        } catch (JSONException e) {
            throw new SmartMapParseException(e);
        }
        this.checkId(id);

        return id;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapParser#parseIds(java.lang.String)
     */
    @Override
    public List<Long> parseIdList(String s, String key) throws SmartMapParseException {
        List<Long> ids = new ArrayList<Long>();

        try {
            JSONObject jsonObject = new JSONObject(s);

            JSONArray idsArray = jsonObject.getJSONArray(key);

            for (int i = 0; i < idsArray.length(); i++) {
                long id = idsArray.getLong(i);
                this.checkId(id);
                ids.add(id);
            }

        } catch (JSONException e) {
            throw new SmartMapParseException(e);
        }
        return ids;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapParser#parsePositions(java.lang.String)
     */
    @Override
    public List<UserContainer> parsePositions(String s) throws SmartMapParseException {

        List<UserContainer> users = new ArrayList<UserContainer>();

        try {
            JSONObject jsonObject = new JSONObject(s);

            JSONArray usersArray = jsonObject.getJSONArray("positions");

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject position = usersArray.getJSONObject(i);
                long userId = position.getLong("id");
                double latitude = position.getDouble(LATITUDE_STRING);
                double longitude = position.getDouble(LONGITUDE_STRING);
                GregorianCalendar lastSeen = this.parseDate(position.getString("lastUpdate"));

                this.checkId(userId);
                this.checkLatitude(latitude);
                this.checkLongitude(longitude);

                Location location = new Location(SMART_MAP_SERVER);
                location.setTime(lastSeen.getTimeInMillis());
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                String locationString = Utils.getCityFromLocation(location);

                UserContainer user =
                    new UserContainer(userId, null, null, null, location, locationString, null,
                        User.BlockStatus.NOT_SET, -1);

                users.add(user);
            }
        } catch (JSONException e) {
            throw new SmartMapParseException(e);
        }

        return users;
    }

    /**
     * Checks if the GregorianDate parameters are valid
     * 
     * @author Pamoi
     * @param day
     * @param month
     * @param hour
     * @param minutes
     * @param seconds
     * @throws SmartMapParseException
     *             if invalid parameters
     */
    private void checkDateParams(int day, int month, int hour, int minutes, int seconds)
        throws SmartMapParseException {
        this.checkDay(day);
        this.checkMonth(month);
        this.checkHour(hour);
        this.checkMinutes(minutes);
        this.checkSeconds(seconds);
    }

    /**
     * Checks if the given day number is valid
     * 
     * @param day
     * @throws SmartMapParseException
     *             in case the day is not valid
     */
    private void checkDay(int day) throws SmartMapParseException {
        if ((day > MAX_DAYS_NUMBER) || (day < 1)) {
            throw new SmartMapParseException("Invalid day number !");
        }
    }

    /**
     * Checks if the event description is valid
     * 
     * @param description
     * @throws SmartMapParseException
     *             if invalid description
     */
    private void checkEventDescription(String description) throws SmartMapParseException {
        if (description.length() > MAX_EVENT_DESCRIPTION_LENGTH) {
            throw new SmartMapParseException("Description must not be longer than 255 characters");
        }
    }

    /**
     * Checks if the given hour number is valid
     * 
     * @param hour
     * @throws SmartMapParseException
     *             in case the hour is not valid
     */
    private void checkHour(int hour) throws SmartMapParseException {
        if ((hour > MAX_HOURS_NUMBER) || (hour < 0)) {
            throw new SmartMapParseException("Invalid hour number !");
        }
    }

    /**
     * Checks if the id is valid
     * 
     * @param id
     * @throws SmartMapParseException
     *             if invalid id
     */
    private void checkId(long id) throws SmartMapParseException {
        if (id <= 0) {
            throw new SmartMapParseException("negative id");
        }
    }

    /**
     * Checks if the latitude is valid
     * 
     * @param latitude
     * @throws SmartMapParseException
     *             if invalid latitude
     */
    private void checkLatitude(double latitude) throws SmartMapParseException {
        if (!((MIN_LATITUDE <= latitude) && (latitude <= MAX_LATITUDE)) || (latitude == Double.NaN)) {
            throw new SmartMapParseException("invalid latitude");
        }
    }

    /**
     * Checks if the longitude is valid
     * 
     * @param longitude
     * @throws SmartMapParseException
     *             if invalid longitude
     */
    private void checkLongitude(double longitude) throws SmartMapParseException {
        if (!((MIN_LONGITUDE <= longitude) && (longitude <= MAX_LONGITUDE)) || (longitude == Double.NaN)) {
            throw new SmartMapParseException("invalid longitude");
        }
    }

    /**
     * Checks if the given minutes number is valid
     * 
     * @param minutes
     * @throws SmartMapParseException
     *             in case the minutes number is not valid
     */
    private void checkMinutes(int minutes) throws SmartMapParseException {
        if ((minutes > MAX_MINUTES_NUMBER) || (minutes < 0)) {
            throw new SmartMapParseException("Invalid minute number !");
        }
    }

    /**
     * Checks if the given month number is valid
     * 
     * @param month
     * @throws SmartMapParseException
     *             in case the month is not valid
     */
    private void checkMonth(int month) throws SmartMapParseException {
        if ((month > MAX_MONTHS_NUMBER) || (month < 0)) {
            throw new SmartMapParseException("Invalid month number !");
        }
    }

    /**
     * Checks if the name is valid
     * 
     * @param name
     * @throws SmartMapParseException
     *             if invalid name
     */
    private void checkName(String name) throws SmartMapParseException {
        if ((name.length() >= MAX_NAME_LENGTH) || (name.length() < MIN_NAME_LENGTH)) {
            throw new SmartMapParseException("invalid name : must be between 2 and 60 characters");
        }
    }

    /**
     * Checks if the given seconds number is valid
     * 
     * @param seconds
     * @throws SmartMapParseException
     *             in case the seconds number is not valid
     */
    private void checkSeconds(int seconds) throws SmartMapParseException {
        if ((seconds > MAX_SECONDS_NUMBER) || (seconds < 0)) {
            throw new SmartMapParseException("Invalid second number !");
        }
    }

    /**
     * Checks if the starting date is before the ending date
     * 
     * @param startingDate
     * @param endDate
     * @throws SmartMapParseException
     *             in case the starting date is after the ending date
     */
    private void checkStartingAndEndDate(GregorianCalendar startingDate, GregorianCalendar endDate)
        throws SmartMapParseException {
        if (!startingDate.before(endDate)) {
            throw new SmartMapParseException("Starting date must be before end date");
        }
    }

    /**
     * Transforms a date in format YYYY-MM-DD hh:mm:ss into a GregorianCalendar
     * instance.
     * 
     * @author Pamoi
     * @param date
     * @return
     * @throws SmartMapParseException
     *             in case of invalid JSON format or invalid data
     */
    private GregorianCalendar parseDate(String date) throws SmartMapParseException {

        String[] dateTime = date.split(" ");

        if (dateTime.length != DATETIME_FORMAT_PARTS) {
            throw new SmartMapParseException("Invalid datetime format !");
        }

        String[] datePart = dateTime[0].split("-");

        if (datePart.length != DATE_FORMAT_PARTS) {
            throw new SmartMapParseException("Invalid date format !");
        }

        String[] timePart = dateTime[1].split(":");

        if (timePart.length != TIME_FORMAT_PARTS) {
            throw new SmartMapParseException("Invalid time format !");
        }

        int year = Integer.parseInt(datePart[0]);
        // GregorianCalendar counts months from 0.
        int month = Integer.parseInt(datePart[1]) - 1;
        int day = Integer.parseInt(datePart[2]);

        int hour = Integer.parseInt(timePart[0]);
        int minutes = Integer.parseInt(timePart[1]);
        int seconds = Integer.parseInt(timePart[2]);

        // As GregorianCalendar does not check arguments, we need to to it.
        this.checkDateParams(day, month, hour, minutes, seconds);

        // Server time is in GMT+01:00
        GregorianCalendar g = new GregorianCalendar(TimeZone.getTimeZone("GMT+01:00"));

        g.set(year, month, day, hour, minutes, seconds);

        return g;
    }

    /**
     * Parse an Event from a JSONObject
     * 
     * @param jsonObject
     * @return the parsed event, encapsulated in an {@link EventContainer} Object
     * @throws SmartMapParseException
     *             in case of invalid JSON format or invalid data
     */
    private EventContainer parseEventFromJSON(JSONObject jsonObject) throws SmartMapParseException {
        long id = -1;
        UserContainer creator = null;
        GregorianCalendar startingDate = null;
        GregorianCalendar endDate = null;
        double latitude = UNITIALIZED_LATITUDE;
        double longitude = UNITIALIZED_LONGITUDE;
        String positionName = null;
        String name = null;
        String description = "";
        List<Long> participants;

        try {
            id = jsonObject.getLong("id");
            creator = this.parseFriendFromJSON(jsonObject.getJSONObject("creator"));
            startingDate = this.parseDate(jsonObject.getString("startingDate"));
            endDate = this.parseDate(jsonObject.getString("endingDate"));
            latitude = jsonObject.getDouble(LATITUDE_STRING);
            longitude = jsonObject.getDouble(LONGITUDE_STRING);
            positionName = jsonObject.getString("positionName");
            name = jsonObject.getString("name");
            description = jsonObject.getString("description");
            participants = this.parseIdList(jsonObject.toString(), "participants");
        } catch (JSONException e) {
            throw new SmartMapParseException(e);
        }

        this.checkId(id);
        this.checkStartingAndEndDate(startingDate, endDate);
        this.checkLatitude(latitude);
        this.checkLongitude(longitude);
        this.checkName(positionName);
        this.checkName(name);
        this.checkEventDescription(description);
        for (long participantId : participants) {
            this.checkId(participantId);
        }
        Location location = new Location(SMART_MAP_SERVER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        return new EventContainer(id, name, creator, description, startingDate, endDate, location,
            positionName, new HashSet<Long>(participants));

    }

    /**
     * Return the friend parsed from a jsonObject
     * 
     * @param jsonObject
     * @return a friend
     * @throws SmartMapParseException
     *             in case of invalid JSON format or invalid data
     */
    private UserContainer parseFriendFromJSON(JSONObject jsonObject) throws SmartMapParseException {
        long id = 0;
        String name = null;
        double latitude = UNITIALIZED_LATITUDE;
        double longitude = UNITIALIZED_LONGITUDE;
        String lastSeenString = null;
        int friendship = User.NO_FRIENDSHIP;

        try {
            id = jsonObject.getLong("id");
            name = jsonObject.getString("name");
            latitude = jsonObject.optDouble(LATITUDE_STRING, UNITIALIZED_LATITUDE);
            longitude = jsonObject.optDouble(LONGITUDE_STRING, UNITIALIZED_LONGITUDE);
            lastSeenString = jsonObject.optString("lastUpdate", null);
            friendship = jsonObject.optInt("isFriend");
        } catch (JSONException e) {
            throw new SmartMapParseException(e);
        }

        this.checkId(id);
        this.checkName(name);

        Location location = null;

        if (latitude != UNITIALIZED_LATITUDE) {
            this.checkLatitude(latitude);
        }

        if (longitude != UNITIALIZED_LONGITUDE) {
            this.checkLongitude(longitude);
        }

        // We do not want a location if it has not
        // last seen date.
        if (lastSeenString != null) {
            location = new Location(SMART_MAP_SERVER);
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            location.setTime(this.parseDate(lastSeenString).getTimeInMillis());
        }

        return new UserContainer(id, name, null, null, location, null, null, User.BlockStatus.NOT_SET,
            friendship);
    }
}