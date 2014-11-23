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
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.User;

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

    private static final String SERVER_URL = "http://smartmap.ddns.net";
    private static final NetworkProvider NETWORK_PROVIDER = new DefaultNetworkProvider();
    private static final int SERVER_RESPONSE_OK = 200;
    private static CookieManager mCookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);;

    private static final NetworkSmartMapClient ONE_INSTANCE = new NetworkSmartMapClient();

    public static NetworkSmartMapClient getInstance() {

        return ONE_INSTANCE;
    }

    private NetworkSmartMapClient() {

        CookieHandler.setDefault(mCookieManager);
        if (ONE_INSTANCE != null) {
            throw new IllegalStateException("Already instantiated");
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.severcom.SmartMapInvitationsClient#acceptInvitation(int)
     */
    @Override
    public User acceptInvitation(long id) throws SmartMapClientException {

        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_id", Long.toString(id));
        HttpURLConnection conn = this.getHttpURLConnection("/acceptInvitation");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        User acceptedUser = null;
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
     * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#allowFriend(int)
     */
    @Override
    public void allowFriend(long id) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_id", Long.toString(id));
        HttpURLConnection conn = this.getHttpURLConnection("/allowFriend");
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
     * ch.epfl.smartmap.severcom.SmartMapFriendsClient#allowFriendList(java.
     * util.List)
     */
    @Override
    public void allowFriendList(List<Long> ids) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_ids", this.longListToString(ids));
        HttpURLConnection conn = this.getHttpURLConnection("/allowFriendList");
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

    private void checkResponseCode(HttpURLConnection connection) throws SmartMapClientException {
        try {
            if (connection.getResponseCode() != SERVER_RESPONSE_OK) {
                throw new SmartMapClientException("HTTP error with code " + connection.getResponseCode()
                    + " during communication with client.");
            }
        } catch (IOException e) {
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
    public void createPublicEvent(Event event) throws SmartMapClientException {

        Map<String, String> params = this.getParamsForEvent(event);
        HttpURLConnection conn = this.getHttpURLConnection("/createEvent");
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
     * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#disallowFriend(int)
     */
    @Override
    public void disallowFriend(long id) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_id", Long.toString(id));
        HttpURLConnection conn = this.getHttpURLConnection("/disallowFriend");
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
     * ch.epfl.smartmap.severcom.SmartMapFriendsClient#disallowFriendList(java
     * .util.List)
     */
    @Override
    public void disallowFriendList(List<Long> ids) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_ids", this.longListToString(ids));
        HttpURLConnection conn = this.getHttpURLConnection("/disallowFriendList");
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

    @Override
    public List<User> findUsers(String text) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("search_text", text);
        HttpURLConnection conn = this.getHttpURLConnection("/findUsers");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        List<User> friends = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
            friends = parser.parseFriends(response, "list");
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        return friends;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#followFriend(int)
     */
    @Override
    public void followFriend(long id) throws SmartMapClientException {

        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_id", Long.toString(id));
        HttpURLConnection conn = this.getHttpURLConnection("/followFriend");
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
            ids = parser.parseIds(response, "friends");

        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        return ids;
    }

    private HttpURLConnection getHttpURLConnection(String uri) throws SmartMapClientException {
        URL serverURL = null;
        HttpURLConnection connection = null;
        try {
            serverURL = new URL(SERVER_URL + uri);
            connection = NETWORK_PROVIDER.getConnection(serverURL);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            throw new IllegalArgumentException();
        } catch (IOException e) {
            throw new SmartMapClientException(e);
        }
        return connection;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.severcom.SmartMapInvitationsClient#getInvitations()
     */
    @Override
    public NotificationBag getInvitations() throws SmartMapClientException {

        HttpURLConnection conn = this.getHttpURLConnection("/getInvitations");
        String response = this.sendViaPost(new HashMap<String, String>(), conn);

        SmartMapParser parser = null;
        List<User> inviters = null;
        List<User> newFriends = null;
        List<Long> removedFriends = null;

        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
            inviters = parser.parseFriends(response, "invitations");
            newFriends = parser.parseFriends(response, "newFriends");
            removedFriends = parser.parseIds(response, "removedFriends");
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        return new NetworkNotificationBag(inviters, newFriends, removedFriends,
            NetworkSmartMapClient.getInstance());

    }

    private Map<String, String> getParamsForEvent(Event event) {
        Map<String, String> params = new HashMap<String, String>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String startingDate = dateFormat.format(event.getStartDate().getTime());
        String endDate = dateFormat.format(event.getEndDate().getTime());

        params.put("starting", startingDate);
        params.put("ending", endDate);
        params.put("longitude", Double.toString(event.getLocation().getLongitude()));
        params.put("latitude", Double.toString(event.getLocation().getLatitude()));
        params.put("positionName", event.getPositionName());
        params.put("name", event.getName());
        params.put("description", event.getDescription());

        return params;

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

    @Override
    public List<Event> getPublicEvents(double latitude, double longitude, double radius)
        throws SmartMapClientException {

        Map<String, String> params = new HashMap<String, String>();
        params.put("latitude", Double.toString(latitude));
        params.put("longitude", Double.toString(longitude));
        params.put("radius", Double.toString(radius));

        HttpURLConnection conn = this.getHttpURLConnection("/getPublicEvents");
        String response = this.sendViaPost(params, conn);

        List<Event> publicEvents = new ArrayList<Event>();

        try {
            SmartMapParser parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
            publicEvents = parser.parseEventList(response);

        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        for (Event event : publicEvents) {
            new EventCreatorNameRetriever(event).setEventCreatorName();
        }

        return publicEvents;
    }

    private String getRequestResponse(HttpURLConnection connection) throws SmartMapClientException {
        StringBuffer response = null;
        try {
            // Get response
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

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.severcom.SmartMapInvitationsClient#getUserInfo(int)
     */
    @Override
    public User getUserInfo(long id) throws SmartMapClientException {

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Long.toString(id));
        HttpURLConnection conn = this.getHttpURLConnection("/getUserInfo");
        String response = this.sendViaPost(params, conn);

        SmartMapParser parser = null;
        User friend = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
            friend = parser.parseFriend(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
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
     * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#listFriendPos()
     */
    @SuppressLint("UseSparseArrays")
    @Override
    public List<User> listFriendsPos() throws SmartMapClientException {

        HttpURLConnection conn = this.getHttpURLConnection("/listFriendsPos");
        String response = this.sendViaPost(new HashMap<String, String>(), conn);

        SmartMapParser parser = null;
        List<User> users = new ArrayList<User>();
        try {
            parser = SmartMapParserFactory.parserForContentType(conn.getContentType());
            parser.checkServerError(response);
            users = parser.parsePositions(response);
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }
        return users;
    }

    private String longListToString(List<Long> list) {
        String listString = "";

        for (long n : list) {
            listString += n + ",";
        }

        return listString;
    }

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

    private void sendRequestWithParams(Map<String, String> params, HttpURLConnection connection)
        throws SmartMapClientException {
        try {
            // Build the request
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

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#unfollowFriend(int)
     */
    @Override
    public void unfollowFriend(long id) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_id", Long.toString(id));
        HttpURLConnection conn = this.getHttpURLConnection("/unfollowFriend");
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

    @Override
    public void updateEvent(Event event) throws SmartMapClientException {

        Map<String, String> params = this.getParamsForEvent(event);
        params.put("eventId", Long.toString(event.getID()));
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
}