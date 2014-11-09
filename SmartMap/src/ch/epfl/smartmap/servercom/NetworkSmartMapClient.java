package ch.epfl.smartmap.servercom;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;
import ch.epfl.smartmap.cache.Point;
import ch.epfl.smartmap.cache.User;

/**
 * A {@link SmartMapClient} implementation that uses a {@link NetworkProvider}
 * to communicate with a SmartMap server.
 *
 * @author marion-S
 *
 */

final public class NetworkSmartMapClient implements SmartMapClient {

    private static final String SERVER_URL = "http://smartmap.ddns.net";
    private static final NetworkProvider NETWORK_PROVIDER = new DefaultNetworkProvider();
    static final String COOKIES_HEADER = "Set-Cookie";
    public static final String USER_AGENT = "Mozilla/5.0"; // latest firefox's
                                                           // user agent
    @SuppressWarnings("unused")
    private String mSessionId;

    private static final NetworkSmartMapClient ONE_INSTANCE = new NetworkSmartMapClient();

    private NetworkSmartMapClient() {
        if (ONE_INSTANCE != null) {
            throw new IllegalStateException("Already instantiated");
        }
    }

    public static NetworkSmartMapClient getInstance() {
        return ONE_INSTANCE;
    }

    // TODO
    // private static CookieManager mCookieManager = new CookieManager();

    /*
     * (non-Javadoc)
     *
     * @see ch.epfl.smartmap.servercom.SmartMapClient#authServer(java.lang.String, long, java.lang.String)
     */
    @Override
    public void authServer(String name, long facebookId, String fbAccessToken)
        throws SmartMapClientException {
        Log.d("authServer", "begin");
        Map<String, String> params = new HashMap<String, String>();

        params.put("name", name);
        params.put("facebookId", Long.toString(facebookId));
        params.put("facebookToken", fbAccessToken);
        HttpURLConnection conn = getHttpURLConnection("/auth");
        String response = sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn
                .getContentType());
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        }

        try {
            parser.checkServerError(response);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#listFriendPos()
     */
    @Override
    public List<User> listFriendPos() throws SmartMapClientException {

        HttpURLConnection conn = getHttpURLConnection("/listFriendPos");
        String response = sendViaPost(null, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn
                .getContentType());
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        }

        try {
            parser.checkServerError(response);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        List<User> friends = null;
        try {
            friends = parser.parseFriends(response);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        return friends;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#followFriend(int)
     */
    @Override
    public void followFriend(int id) throws SmartMapClientException {

        Map<String, String> params = new HashMap<String, String>();
        params.put("id_friend", Integer.toString(id));
        HttpURLConnection conn = getHttpURLConnection("/followFriend");
        String response = sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn
                .getContentType());
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        }

        try {
            parser.checkServerError(response);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#unfollowFriend(int)
     */
    @Override
    public void unfollowFriend(int id) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id_friend", Integer.toString(id));
        HttpURLConnection conn = getHttpURLConnection("/unfollowFriend");
        String response = sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn
                .getContentType());
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        }

        try {
            parser.checkServerError(response);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#allowFriend(int)
     */
    @Override
    public void allowFriend(int id) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id_friend", Integer.toString(id));
        HttpURLConnection conn = getHttpURLConnection("/allowFriend");
        String response = sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn
                .getContentType());
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        }

        try {
            parser.checkServerError(response);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#disallowFriend(int)
     */
    @Override
    public void disallowFriend(int id) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id_friend", Integer.toString(id));
        HttpURLConnection conn = getHttpURLConnection("/disallowFriend");
        String response = sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn
                .getContentType());
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        }

        try {
            parser.checkServerError(response);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#allowFriendList(java. util.List)
     */
    @Override
    public void allowFriendList(List<Integer> ids)
        throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_ids", intListToString(ids));
        HttpURLConnection conn = getHttpURLConnection("/allowFriendList");
        String response = sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn
                .getContentType());
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        }

        try {
            parser.checkServerError(response);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#disallowFriendList(java .util.List)
     */
    @Override
    public void disallowFriendList(List<Integer> ids)
        throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_ids", intListToString(ids));
        HttpURLConnection conn = getHttpURLConnection("/disallowFriendList");
        String response = sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn
                .getContentType());
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        }

        try {
            parser.checkServerError(response);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see ch.epfl.smartmap.servercom.SmartMapClient#inviteFriend(int)
     */
    @Override
    public void inviteFriend(int id) throws SmartMapClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_id", Integer.toString(id));
        HttpURLConnection conn = getHttpURLConnection("/inviteFriend");
        String response = sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn
                .getContentType());
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        }

        try {
            parser.checkServerError(response);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see ch.epfl.smartmap.severcom.SmartMapInvitationsClient#getInvitations()
     */
    @Override
    public List<User> getInvitations() throws SmartMapClientException {

        HttpURLConnection conn = getHttpURLConnection("/getInvitations");
        String response = sendViaPost(null, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn
                .getContentType());
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        }

        try {
            parser.checkServerError(response);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        List<User> inviters = null;
        try {
            inviters = parser.parseFriends(response);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        return inviters;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.epfl.smartmap.severcom.SmartMapInvitationsClient#acceptInvitation(int)
     */
    @Override
    public User acceptInvitation(int id) throws SmartMapClientException {

        Map<String, String> params = new HashMap<String, String>();
        params.put("friend_id", Integer.toString(id));
        HttpURLConnection conn = getHttpURLConnection("/acceptInvitation");
        String response = sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn
                .getContentType());
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        }

        try {
            parser.checkServerError(response);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        User acceptedUser = null;
        try {
            acceptedUser = parser.parseFriend(response);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        return acceptedUser;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.epfl.smartmap.severcom.SmartMapInvitationsClient#getUserInfo(int)
     */
    @Override
    public User getUserInfo(int id) throws SmartMapClientException {

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(id));
        HttpURLConnection conn = getHttpURLConnection("/getUserInfo");
        String response = sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn
                .getContentType());
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        }

        try {
            parser.checkServerError(response);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        User friend = null;
        try {
            friend = parser.parseFriend(response);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

        return friend;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.epfl.smartmap.servercom.SmartMapClient#updatePos(ch.epfl.smartmap. cache.Point)
     */
    @Override
    public void updatePos(Point position) throws SmartMapClientException {
        Log.d("updatePos", "start");
        Map<String, String> params = new HashMap<String, String>();
        params.put("longitude", Double.toString(position.getX()));
        params.put("latitude", Double.toString(position.getY()));

        HttpURLConnection conn = getHttpURLConnection("/updatePos");
        String response = sendViaPost(params, conn);

        SmartMapParser parser = null;
        try {
            parser = SmartMapParserFactory.parserForContentType(conn
                .getContentType());
        } catch (NoSuchFormatException e) {
            throw new SmartMapClientException(e);
        }

        try {
            parser.checkServerError(response);
        } catch (SmartMapParseException e) {
            throw new SmartMapClientException(e);
        }

    }

    private String intListToString(List<Integer> list) {
        String listString = "";

        for (int n : list) {
            listString += n + ",";
        }

        return listString;
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
    public String sendViaPost(Map<String, String> params,
        HttpURLConnection connection) throws SmartMapClientException {
        StringBuffer response = null;
        Log.d("sendViaPost", "start");

        try {

            // Add request header

            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            // TODO
            // Get Cookies form cookieManager and load them to connection
            // Log.d("cookies number",
            // Integer.toString(mCookieManager.getCookieStore()
            // .getCookies().size()));
            // if (mCookieManager.getCookieStore().getCookies().size() > 0) {
            // Log.d("add cookies", "add stored cookies to the headers ");
            // connection.setRequestProperty("Cookie", TextUtils.join(",",
            // mCookieManager.getCookieStore().getCookies()));
            // }

            if (params != null) {

                // Build the request
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, String> param : params.entrySet()) {
                    if (postData.length() != 0) {
                        postData.append('&');
                    }

                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(
                        String.valueOf(param.getValue()), "UTF-8"));

                }

                connection.setDoOutput(true); // To be able to send data

                // Send post request

                DataOutputStream wr;

                wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(postData.toString());
                wr.flush();
                wr.close();

            }

            // Get response
            String inputLine;
            response = new StringBuffer();
            BufferedReader in;

            in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            // Get Cookies form response header and load them to cookieManager

            @SuppressWarnings("unused")
            Map<String, List<String>> headerFields = connection
                .getHeaderFields();

            // TODO
            // List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
            //
            // if (cookiesHeader != null) {
            // Log.d("store cookies", "store cookies");
            // for (String cookie : cookiesHeader) {
            // mCookieManager.getCookieStore().add(null,
            // HttpCookie.parse(cookie).get(0));
            // }
            // }
            // Log.d("cookies number",
            // Integer.toString(mCookieManager.getCookieStore()
            // .getCookies().size()));
            in.close();
        } catch (ProtocolException e) {
            throw new SmartMapClientException(e);
        } catch (IOException e) {
            throw new SmartMapClientException(e);
        } finally {
            connection.disconnect();
        }
        // Finally give result to caller
        return response.toString();
    }

    public HttpURLConnection getHttpURLConnection(String uri)
        throws SmartMapClientException {
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

}