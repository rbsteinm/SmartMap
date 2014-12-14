package ch.epfl.smartmap.servercom;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.EventContainer;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserContainer;

/**
 * A {@link SmartMapClient} implementation that uses a {@link NetworkProvider} to communicate with a SmartMap
 * server.
 * 
 * @author marion-S
 * @author Pamoi (code reviewed : 9.11.2014) : - I don't think user-agent and
 *         accept-language request headers are necessary, and they provide wrong
 *         information to the server (we are not firefox !). - Should
 *         COOKIES_HEADER not be private ? - At line 120 the HashMap could be
 *         initialize to null as it is reassigned later and not used if an
 *         exception is thrown. - There is a typo: it is listFriendsPos instead
 *         of listFriendPos. - We should check for the server response code in
 *         sendViaPost (for example if it returns 404 not found, there will be a
 *         json error that is not the real error source). - It would be better
 *         to give sendViaPost an empty map when there are no post arguments
 *         instead of null, and throw an exception if params is null. - Sould
 *         not getHttpUrlConnection and sendViaPost methods be private ? Server
 *         should not be accessed outside of this class ? - More general remark
 *         (you can discuss it with Nicolas and me): how should we handle
 *         partially initialized users (for example from getUserInfo) ? Is there
 *         a way to update the only partially in the database ? - I think you
 *         should replace the @author SpicyCH by your name in this package files
 *         as it is you who implemented it.
 */

final public class NetworkSmartMapClient implements SmartMapClient {

    private static final String TAG = NetworkSmartMapClient.class.getSimpleName();

    private static final String SERVER_URL = "http://smartmap.ddns.net";
    private static final NetworkProvider NETWORK_PROVIDER = new DefaultNetworkProvider();
    private final static int HTTP_SUCCESS_START = 200;
    private final static int HTTP_SUCCESS_END = 299;
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static CookieManager mCookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);

    public NetworkSmartMapClient() {

        CookieHandler.setDefault(mCookieManager);
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.severcom.SmartMapInvitationsClient#acceptInvitation(int)
     */
    @Override
    public UserContainer acceptInvitation(long id) throws SmartMapClientException {

        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_id", Long.toString(id));
        HttpURLConnection conn = this.getHttpURLConnection("/acceptInvitation");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        UserContainer acceptedUser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
            acceptedUser = parser.parseFriend(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        return acceptedUser;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#ackAcceptedInvitation(long)
     */
    @Override
    public void ackAcceptedInvitation(long id) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_id", Long.toString(id));
        HttpURLConnection conn = this.getHttpURLConnection("/ackAcceptedInvitation");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#ackEventInvitation(long)
     */
    @Override
    public void ackEventInvitation(long eventId) throws SmartMapClientException {

        Map<String, String> params = new HashMap<String, String>();
        params.put("event_id", Long.toString(eventId));
        HttpURLConnection conn = this.getHttpURLConnection("/ackEventInvitation");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#ackRemovedFriend(long)
     */
    @Override
    public void ackRemovedFriend(long id) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_id", Long.toString(id));
        HttpURLConnection conn = this.getHttpURLConnection("/ackRemovedFriend");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.servercom.SmartMapClient#authServer(java.lang.String,
     * long, java.lang.String)
     */
    @Override
    public void authServer(String name, long facebookId, String fbAccessToken) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();

        params.put("name", name);
        params.put("facebookId", Long.toString(facebookId));
        params.put("facebookToken", fbAccessToken);
        HttpURLConnection conn = this.getHttpURLConnection("/auth");
        String response = this.sendViaPost(params, conn);

        ServiceContainer.getSettingsManager().setUserName(name);
        ServiceContainer.getSettingsManager().setFacebookID(facebookId);
        ServiceContainer.getSettingsManager().setToken(fbAccessToken);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
            long id = parser.parseId(response);
            ServiceContainer.getSettingsManager().setUserID(id);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#disallowFriend(int)
     */
    @Override
    public void blockFriend(long id) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_id", Long.toString(id));
        HttpURLConnection conn = this.getHttpURLConnection("/blockFriend");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.servercom.SmartMapClient#createEvent(ch.epfl.smartmap
     * .cache.Event)
     */
    @Override
    public long createPublicEvent(EventContainer event) throws SmartMapClientException {

        Map<String, String> params = this.getParamsForEvent(event);
        HttpURLConnection conn = this.getHttpURLConnection("/createEvent");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        long id = -1;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
            id = parser.parseId(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);

        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        return id;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#declineInvitation(long)
     */
    @Override
    public void declineInvitation(long id) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_id", Long.toString(id));
        HttpURLConnection conn = this.getHttpURLConnection("/declineInvitation");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#findUsers(java.lang.String)
     */
    @Override
    public List<UserContainer> findUsers(String text) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("search_text", text);
        HttpURLConnection conn = this.getHttpURLConnection("/findUsers");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        List<UserContainer> friends = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
            friends = parser.parseFriendList(response, "list");
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        return friends;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.severcom.SmartMapInvitationsClient#getUserInfo(int)
     */
    @Override
    public EventContainer getEventInfo(long eventId) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("event_id", Long.toString(eventId));
        HttpURLConnection conn = this.getHttpURLConnection("/getEventInfo");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        EventContainer event = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
            event = parser.parseEvent(response);

        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }
        return event;

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#getEventInvitations()
     */
    @Override
    public InvitationBag getEventInvitations() throws SmartMapClientException {

        HttpURLConnection conn = this.getHttpURLConnection("/getEventInvitations");
        String response = this.sendViaPost(new HashMap<String, String>(), conn);

        List<EventContainer> eventInvitations = new ArrayList<EventContainer>();

        try {
            SmartMapParser parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
            eventInvitations = parser.parseEventList(response);
            Log.d(TAG, "event size :" + eventInvitations.size());

        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        return new NetworkEventInvitationBag(new HashSet<EventContainer>(eventInvitations));
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#getFriendsIds()
     */
    @Override
    public List<Long> getFriendsIds() throws SmartMapClientException {

        List<Long> ids = new ArrayList<Long>();
        HttpURLConnection conn = this.getHttpURLConnection("/getFriendsIds");
        String response = this.sendViaPost(new HashMap<String, String>(), conn);

        try {
            SmartMapParser parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
            ids = parser.parseIdList(response, "friends");

        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        return ids;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.severcom.SmartMapInvitationsClient#getInvitations()
     */
    @Override
    public InvitationBag getFriendInvitations() throws SmartMapClientException {

        HttpURLConnection conn = this.getHttpURLConnection("/getInvitations");
        String response = this.sendViaPost(new HashMap<String, String>(), conn);

        SmartMapParser parser = null;
        List<UserContainer> inviters = null;
        List<UserContainer> newFriends = null;
        List<Long> removedFriends = null;

        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
            inviters = parser.parseFriendList(response, "invitations");
            newFriends = parser.parseFriendList(response, "newFriends");
            removedFriends = parser.parseIdList(response, "removedFriends");
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        return new NetworkFriendInvitationBag(inviters, newFriends, removedFriends);

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#getProfilePicture(long)
     */
    @Override
    public Bitmap getProfilePicture(long id) throws SmartMapClientException {
        Bitmap profilePicture = null;
        HttpURLConnection conn = null;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", Long.toString(id));
            conn = this.getHttpURLConnection("/getProfilePicture");
            this.sendRequestWithParams(params, conn);
            this.checkResponseCode(conn);
            profilePicture = BitmapFactory.decodeStream(conn.getInputStream());
        } catch (IOException e) {
            throw new SmartMapClientException(e);
        } finally {
            conn.disconnect();
        }
        if (profilePicture == null) {
            throw new SmartMapClientException("Error : the image data could not be decoded");
        }
        return profilePicture;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#getPublicEvents(double, double, double)
     */
    @Override
    public List<Long> getPublicEvents(double latitude, double longitude, double radius)
        throws SmartMapClientException {

        Map<String, String> params = new HashMap<String, String>();
        params.put("latitude", Double.toString(latitude));
        params.put("longitude", Double.toString(longitude));
        params.put("radius", Double.toString(radius));

        HttpURLConnection conn = this.getHttpURLConnection("/getPublicEvents");
        String response = this.sendViaPost(params, conn);

        List<Long> publicEvents = new ArrayList<Long>();

        try {
            SmartMapParser parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
            publicEvents = parser.parseIdList(response, "events");

        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        return publicEvents;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#getEventInvitations()
     */
    @Override
    public UserContainer getUserInfo(long id) throws SmartMapClientException {

        Map<String, String> params = new HashMap<String, String>();
        HttpURLConnection conn = this.getHttpURLConnection("/getUserInfo");
        params.put("user_id", Long.toString(id));
        String response = this.sendViaPost(params, conn);
        SmartMapParser parser = null;
        UserContainer friend = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
            friend = parser.parseFriend(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException();
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException();
        }

        return friend;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#inviteFriend(int)
     */
    @Override
    public void inviteFriend(long id) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_id", Long.toString(id));
        HttpURLConnection conn = this.getHttpURLConnection("/inviteFriend");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#inviteUsersToEvent(long,
     * java.util.List)
     */
    @Override
    public void inviteUsersToEvent(long eventId, List<Long> usersIds) throws SmartMapClientException {

        Map<String, String> params = new HashMap<String, String>();
        params.put("event_id", Long.toString(eventId));
        params.put("users_ids", this.longListToString(usersIds));
        HttpURLConnection conn = this.getHttpURLConnection("/inviteUsersToEvent");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#joinEvent(long)
     */
    @Override
    public void joinEvent(long eventId) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("event_id", Long.toString(eventId));
        HttpURLConnection conn = this.getHttpURLConnection("/joinEvent");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#leaveEvent(long)
     */
    @Override
    public void leaveEvent(long eventId) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("event_id", Long.toString(eventId));
        HttpURLConnection conn = this.getHttpURLConnection("/leaveEvent");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#listFriendPos()
     */
    @SuppressLint("UseSparseArrays")
    @Override
    public List<UserContainer> listFriendsPos() throws SmartMapClientException {

        HttpURLConnection conn = this.getHttpURLConnection("/listFriendsPos");
        String response = this.sendViaPost(new HashMap<String, String>(), conn);

        SmartMapParser parser = null;
        List<UserContainer> users = new ArrayList<UserContainer>();
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
            users = parser.parsePositions(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        for (UserContainer user : users) {
            user.setFriendship(User.FRIEND);
        }

        return users;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#removeFriend(long)
     */
    @Override
    public void removeFriend(long id) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_id", Long.toString(id));
        HttpURLConnection conn = this.getHttpURLConnection("/removeFriend");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#disallowFriend(int)
     */
    @Override
    public void unblockFriend(long id) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_id", Long.toString(id));
        HttpURLConnection conn = this.getHttpURLConnection("/unblockFriend");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#updateEvent(ch.epfl.smartmap.cache.EventContainer)
     */
    @Override
    public void updateEvent(EventContainer event) throws SmartMapClientException {

        Map<String, String> params = this.getParamsForEvent(event);
        params.put("eventId", Long.toString(event.getId()));
        HttpURLConnection conn = this.getHttpURLConnection("/updateEvent");

        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());

            parser.checkServerError(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);

        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.servercom.SmartMapClient#updatePos(ch.epfl.smartmap.
     * cache.Point)
     */
    @Override
    public void updatePos(Location location) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("longitude", Double.toString(location.getLongitude()));
        params.put("latitude", Double.toString(location.getLatitude()));

        HttpURLConnection conn = this.getHttpURLConnection("/updatePos");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /**
     * Checks the HTTP response code
     * 
     * @param connection
     *            : the connection whose the response code is checked
     * @throws SmartMapClientException
     *             if error code
     */
    private void checkResponseCode(HttpURLConnection connection) throws SmartMapClientException {
        try {
            int responseCode = connection.getResponseCode();
            if ((responseCode < HTTP_SUCCESS_START) || (responseCode > HTTP_SUCCESS_END)) {
                throw new SmartMapClientException("HTTP error with code " + connection.getResponseCode()
                    + " during communication with client.");
            }
        } catch (IOException e) {
            throw new SmartMapClientException(e);
        }
    }

    /**
     * Return a {@link HttpURLConnection} object for the given uri. The connection is obtained by a
     * {@link NetworkProvider} object
     * 
     * @param uri
     *            the uri to append to the base url of the SmartMap server
     * @return an HttpURLConnection for the given uri
     * @throws SmartMapClientException
     *             in case the connection could not be retrieved for any reason
     *             external to the application (network failure etc.)
     */
    private HttpURLConnection getHttpURLConnection(String uri) throws SmartMapClientException {
        URL serverURL = null;
        HttpURLConnection connection = null;
        try {
            serverURL = new URL(SERVER_URL + uri);
            connection = NETWORK_PROVIDER.getConnection(serverURL);
        } catch (MalformedURLException e1) {
            Log.e(NetworkSmartMapClient.class.getSimpleName(), e1.getMessage());
            throw new IllegalArgumentException();
        } catch (IOException e) {
            throw new SmartMapClientException(e);
        }
        return connection;
    }

    /**
     * Prepare the parameters to send to the server for the given {@link EventContainer} object
     * 
     * @param event
     *            the event for which parameters need to be prepared
     * @return a map of strings to strings which represents the parameters to send
     */
    private Map<String, String> getParamsForEvent(EventContainer event) {
        Map<String, String> params = new HashMap<String, String>();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String startingDate = dateFormat.format(event.getStartDate().getTime());
        String endDate = dateFormat.format(event.getEndDate().getTime());

        params.put("starting", startingDate);
        params.put("ending", endDate);
        params.put("longitude", Double.toString(event.getLocation().getLongitude()));
        params.put("latitude", Double.toString(event.getLocation().getLatitude()));
        params.put("positionName", event.getLocationString());
        params.put("name", event.getName());
        params.put("description", event.getDescription());

        return params;

    }

    /**
     * Gets the server's response for the given {@link HttpURLConnection} object
     * 
     * @param connection
     *            the connection from which the response is given
     * @return the request response in String format
     * @throws SmartMapClientException
     *             in case the response code could not be retrieved for any reason
     *             external to the application (network failure etc.)
     */
    private String getRequestResponse(HttpURLConnection connection) throws SmartMapClientException {
        StringBuffer response = null;
        try {

            String inputLine;
            response = new StringBuffer();
            BufferedReader in;

            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
        } catch (IOException e) {
            throw new SmartMapClientException(e);
        }
        return response.toString();
    }

    /**
     * Converts a list of long to a string to match with the parameters format required by the SmartMap server
     * 
     * @param list
     *            the list of long to convert to a string
     * @return the String in the required format
     */
    private String longListToString(List<Long> list) {
        String listString = "";

        for (long n : list) {
            listString += n + ",";
        }

        return listString;
    }

    /**
     * Sends a request to the server with the given parameters, and via the gven {@link HttpURLConnection}
     * object
     * 
     * @param params
     *            a map of String to String representing the parameters to send with the request
     * @param connection
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    private void sendRequestWithParams(Map<String, String> params, HttpURLConnection connection)
        throws SmartMapClientException {
        try {

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (postData.length() != 0) {
                    postData.append('&');
                }

                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));

            }

            connection.setDoOutput(true); // To be able to send data

            // Send post request

            DataOutputStream wr;

            wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(postData.toString());
            wr.flush();
            wr.close();
        } catch (IOException e) {
            throw new SmartMapClientException(e);
        }
    }

    /**
     * Sends a POST request to the server and returns the server's response
     * 
     * @param params
     *            the parameters to send to the server
     * @param uri
     *            to append to the base url of the SmartMap server
     * @return the server's response in String format
     * @throws SmartMapClientException
     *             in case the response could not be retrieved for any reason
     *             external to the application (network failure etc.)
     */
    private String sendViaPost(Map<String, String> params, HttpURLConnection connection)
        throws SmartMapClientException {
        String response = null;
        try {
            connection.setRequestMethod("POST");

            if (params.size() != 0) {
                this.sendRequestWithParams(params, connection);
            }

            this.checkResponseCode(connection);

            response = this.getRequestResponse(connection);

        } catch (ProtocolException e) {
            throw new SmartMapClientException(e);

        } finally {
            connection.disconnect();
        }
        return response;
    }
}