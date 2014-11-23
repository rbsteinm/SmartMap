package ch.epfl.smartmap.servercom;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserEvent;

/**
 * A {@link SmartMapParser} implementation that parses objects from Json format
 * 
 * @author marion-S
 * @author SpicyCH (code reviewed 02.11.2014) : changed Error to error as the
 *         server uses a lower case.
 */
@SuppressLint("UseSparseArrays")
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
    private static final int MAX_EVENT_DESCRIPTION_LENGTH = 255;

    // private static final String TAG = "JSON_PARSER";

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.servercom.SmartMapParser#parseFriend(java.lang.String)
     */
    @Override
    public User parseFriend(String s) throws SmartMapParseException {
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
    public List<User> parseFriends(String s, String key) throws SmartMapParseException {

        List<User> friends = new ArrayList<User>();

        try {
            JSONObject jsonObject = new JSONObject(s);

            JSONArray usersArray = jsonObject.getJSONArray(key);

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userJSON = usersArray.getJSONObject(i);
                User friend = this.parseFriendFromJSON(userJSON);
                friends.add(friend);
            }
        } catch (JSONException e) {
            throw new SmartMapParseException(e);
        }

        return friends;
    }

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
            Log.d("serverStatus", status);
            Log.d("serverMessage", message);
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

    @Override
    public List<User> parsePositions(String s) throws SmartMapParseException {

        List<User> users = new ArrayList<User>();

        try {
            JSONObject jsonObject = new JSONObject(s);

            JSONArray usersArray = jsonObject.getJSONArray("positions");

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject position = usersArray.getJSONObject(i);
                long userId = position.getLong("id");
                double latitude = position.getDouble("latitude");
                double longitude = position.getDouble("longitude");
                GregorianCalendar lastSeen = this.parseDate(position.getString("lastUpdate"));

                this.checkId(userId);
                this.checkLatitude(latitude);
                this.checkLongitude(longitude);
                this.checkLastSeen(lastSeen);

                Location location = new Location("SmartMapServers");
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                User user = null;

                user = new Friend(userId, Friend.NO_NAME);

                user.setLocation(location);
                user.setLastSeen(lastSeen);

                users.add(user);
            }

        } catch (JSONException e) {
            throw new SmartMapParseException(e);
        }

        return users;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapParser#parseIds(java.lang.String)
     */
    @Override
    public List<Long> parseIds(String s, String key) throws SmartMapParseException {
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

    @Override
    public Event parseEvent(String s) throws SmartMapParseException {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);
        } catch (JSONException e) {
            throw new SmartMapParseException(e);
        }
        return this.parseEventFromJSON(jsonObject);
    }

    @Override
    public List<Event> parseEventList(String s) throws SmartMapParseException {
        List<Event> events = new ArrayList<Event>();

        try {
            JSONObject jsonObject = new JSONObject(s);

            JSONArray eventsArray = jsonObject.getJSONArray("events");

            for (int i = 0; i < eventsArray.length(); i++) {
                JSONObject eventJSON = eventsArray.getJSONObject(i);
                Event event = this.parseEventFromJSON(eventJSON);
                events.add(event);
                // Log.d("events", event.getName());
            }
        } catch (JSONException e) {
            throw new SmartMapParseException(e);
        }

        return events;
    }

    /**
     * Return the friend parsed from a jsonObject
     * 
     * @param jsonObject
     * @return a friend
     * @throws SmartMapParseException
     */
    private User parseFriendFromJSON(JSONObject jsonObject) throws SmartMapParseException {
        long id = 0;
        String name = null;
        String phoneNumber = null;
        String email = null;
        double latitude = UNITIALIZED_LATITUDE;
        double longitude = UNITIALIZED_LONGITUDE;

        try {
            id = jsonObject.getLong("id");
            name = jsonObject.getString("name");
            latitude = jsonObject.optDouble("latitude", UNITIALIZED_LATITUDE);
            longitude = jsonObject.optDouble("longitude", UNITIALIZED_LONGITUDE);
            phoneNumber = jsonObject.optString("phoneNumber", null);
            email = jsonObject.optString("email", null);
        } catch (JSONException e) {
            throw new SmartMapParseException(e);
        }

        this.checkId(id);
        this.checkName(name);

        Friend friend = new Friend(id, name);

        if (latitude != UNITIALIZED_LATITUDE) {
            this.checkLatitude(latitude);
            friend.setLatitude(latitude);
        }
        if (longitude != UNITIALIZED_LONGITUDE) {
            this.checkLongitude(longitude);
            friend.setLongitude(longitude);
        }

        if (phoneNumber != null) {
            this.checkPhoneNumber(phoneNumber);
            friend.setNumber(phoneNumber);
        }
        if (email != null) {
            this.checkEmail(email);
            friend.setEmail(email);
        }

        return friend;
    }

    private Event parseEventFromJSON(JSONObject jsonObject) throws SmartMapParseException {
        long id = -1;
        long creatorId = -1;
        GregorianCalendar startingDate = null;
        GregorianCalendar endDate = null;
        double latitude = UNITIALIZED_LATITUDE;
        double longitude = UNITIALIZED_LONGITUDE;
        String positionName = null;
        String name = null;
        String description = "";

        try {
            id = jsonObject.getLong("id");
            creatorId = jsonObject.getLong("creatorId");
            startingDate = this.parseDate(jsonObject.getString("startingDate"));
            endDate = this.parseDate(jsonObject.getString("endingDate"));
            latitude = jsonObject.getDouble("latitude");
            longitude = jsonObject.getDouble("longitude");
            positionName = jsonObject.getString("positionName");
            name = jsonObject.getString("name");
            description = jsonObject.getString("description");
        } catch (JSONException e) {
            throw new SmartMapParseException(e);
        }

        this.checkId(id);
        this.checkId(creatorId);
        this.checkStartingAndEndDate(startingDate, endDate);
        this.checkLatitude(latitude);
        this.checkLongitude(longitude);
        this.checkName(positionName);
        this.checkName(name);
        this.checkEventDescription(description);
        Event event =
            new UserEvent(name, creatorId, Friend.NO_NAME, startingDate, endDate, new Location(
                "SmartMapServer"));
        event.setID(id);
        event.setPositionName(positionName);
        event.setDescription(description);
        event.getLocation().setLatitude(latitude);
        event.getLocation().setLongitude(longitude);

        return event;
    }

    /**
     * Checks if the latitude is valid
     * 
     * @param latitude
     * @throws SmartMapParseException
     *             if invalid latitude
     */
    private void checkLatitude(double latitude) throws SmartMapParseException {
        if (!((MIN_LATITUDE <= latitude) && (latitude <= MAX_LATITUDE))) {
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
        if (!((MIN_LONGITUDE <= longitude) && (longitude <= MAX_LONGITUDE))) {
            throw new SmartMapParseException("invalid longitude");
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
            throw new SmartMapParseException("invalid id");
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
        if ((name.length() >= MAX_NAME_LENGTH) || (name.length() < 2)) {
            throw new SmartMapParseException("invalid name : must be between 2 and 60 characters");
        }
    }

    /**
     * Checks if the email address is valid
     * 
     * @param email
     * @throws SmartMapParseException
     *             if invalid email address
     */
    private void checkEmail(String email) throws SmartMapParseException {
        // TODO
    }

    /**
     * Checks if the phone number is valid
     * 
     * @param phoneNumber
     * @throws SmartMapParseException
     *             if invalid phone number
     */
    private void checkPhoneNumber(String phoneNumber) throws SmartMapParseException {
        // TODO
    }

    /**
     * Checks if the parameter lastSeen is valid
     * 
     * @param lastSeen
     * @throws SmartMapParseException
     */
    private void checkLastSeen(GregorianCalendar lastSeen) throws SmartMapParseException {
        GregorianCalendar now = new GregorianCalendar(TimeZone.getTimeZone("GMT+01:00"));
        if (now.compareTo(lastSeen) < 0) {
            throw new SmartMapParseException("Invalid last seen date: " + lastSeen.toString()
                + " compared to " + now.toString());
        }
    }

    private void checkStartingAndEndDate(GregorianCalendar startingDate, GregorianCalendar endDate)
        throws SmartMapParseException {
        if (!startingDate.before(endDate)) {
            throw new SmartMapParseException("Starting date must be before end date");
        }
    }

    private void checkEventDescription(String description) throws SmartMapParseException {
        if (description.length() > MAX_EVENT_DESCRIPTION_LENGTH) {
            throw new SmartMapParseException("Description must not be longer than 255 characters");
        }
    }

    /**
     * Transforms a date in format YYYY-MM-DD hh:mm:ss into a GregorianCalendar
     * instance.
     * 
     * @author Pamoi
     * @param date
     * @return
     * @throws SmartMapClientException
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
        if ((month > MAX_MONTHS_NUMBER) || (month < 0)) {
            throw new SmartMapParseException("Invalid month number !");
        }

        if ((day > MAX_DAYS_NUMBER) || (day < 1)) {
            throw new SmartMapParseException("Invalid day number !");
        }

        if ((hour > MAX_HOURS_NUMBER) || (hour < 0)) {
            throw new SmartMapParseException("Invalid hour number !");
        }

        if ((minutes > MAX_MINUTES_NUMBER) || (hour < 0)) {
            throw new SmartMapParseException("Invalid minute number !");
        }

        if ((seconds > MAX_SECONDS_NUMBER) || (seconds < 0)) {
            throw new SmartMapParseException("Invalid second number !");
        }

        // Server time is in GMT+01:00
        GregorianCalendar g = new GregorianCalendar(TimeZone.getTimeZone("GMT+01:00"));

        g.set(year, month, day, hour, minutes, seconds);

        return g;
    }

}