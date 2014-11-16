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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;
import ch.epfl.smartmap.cache.User;

/**
 * A {@link SmartMapClient} implementation that uses a {@link NetworkProvider}
 * to communicate with a SmartMap server.
 * 
 * @author marion-S
 * 
 * @author Pamoi (code reviewed : 9.11.2014) : - I don't think user-agent and
 *         accept-language request headers are necessary, and they provide wrong
 *         information to the server (we are not firefox !).
 * 
 *         - Should COOKIES_HEADER not be private ?
 * 
 *         - At line 120 the HashMap could be initialize to null as it is
 *         reassigned later and not used if an exception is thrown.
 * 
 *         - There is a typo: it is listFriendsPos instead of listFriendPos.
 * 
 *         - We should check for the server response code in sendViaPost (for
 *         example if it returns 404 not found, there will be a json error that
 *         is not the real error source).
 * 
 *         - It would be better to give sendViaPost an empty map when there are
 *         no post arguments instead of null, and throw an exception if params
 *         is null.
 * 
 *         - Sould not getHttpUrlConnection and sendViaPost methods be private ?
 *         Server should not be accessed outside of this class ?
 * 
 *         - More general remark (you can discuss it with Nicolas and me): how
 *         should we handle partially initialized users (for example from
 *         getUserInfo) ? Is there a way to update the only partially in the
 *         database ?
 * 
 *         - I think you should replace the @author SpicyCH by your name in this
 *         package files as it is you who implemented it.
 */

final public class NetworkSmartMapClient implements SmartMapClient {

	private static final String SERVER_URL = "http://smartmap.ddns.net";
	private static final NetworkProvider NETWORK_PROVIDER = new DefaultNetworkProvider();
	private static final int SERVER_RESPONSE_OK = 200;
	private static CookieManager mCookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);;

	private static final NetworkSmartMapClient ONE_INSTANCE = new NetworkSmartMapClient();

	private NetworkSmartMapClient() {

		CookieHandler.setDefault(mCookieManager);
		if (ONE_INSTANCE != null) {
			throw new IllegalStateException("Already instantiated");
		}
	}

	public static NetworkSmartMapClient getInstance() {

		return ONE_INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.servercom.SmartMapClient#authServer(java.lang.String,
	 * long, java.lang.String)
	 */
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
	@SuppressLint("UseSparseArrays")
	@Override
	public Map<Long, Location> listFriendsPos() throws SmartMapClientException {

		HttpURLConnection conn = getHttpURLConnection("/listFriendsPos");
		String response = sendViaPost(new HashMap<String, String>(), conn);
		Map<Long, Location> positions = null;

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

		positions = new HashMap<Long, Location>();
		try {
			positions = parser.parsePositions(response);
		} catch (SmartMapParseException e) {
			throw new SmartMapClientException(e);
		}

		return positions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#followFriend(int)
	 */
	@Override
	public void followFriend(long id) throws SmartMapClientException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("friend_id", Long.toString(id));
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
	public void unfollowFriend(long id) throws SmartMapClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("friend_id", Long.toString(id));
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
	public void allowFriend(long id) throws SmartMapClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("friend_id", Long.toString(id));
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
	public void disallowFriend(long id) throws SmartMapClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("friend_id", Long.toString(id));
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
	 * @see
	 * ch.epfl.smartmap.severcom.SmartMapFriendsClient#allowFriendList(java.
	 * util.List)
	 */
	@Override
	public void allowFriendList(List<Long> ids) throws SmartMapClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("friend_ids", longListToString(ids));
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
	 * @see
	 * ch.epfl.smartmap.severcom.SmartMapFriendsClient#disallowFriendList(java
	 * .util.List)
	 */
	@Override
	public void disallowFriendList(List<Long> ids)
			throws SmartMapClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("friend_ids", longListToString(ids));
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
	public void inviteFriend(long id) throws SmartMapClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("friend_id", Long.toString(id));
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
		String response = sendViaPost(new HashMap<String, String>(), conn);

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
	 * @see
	 * ch.epfl.smartmap.severcom.SmartMapInvitationsClient#acceptInvitation(int)
	 */
	@Override
	public User acceptInvitation(long id) throws SmartMapClientException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("friend_id", Long.toString(id));
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
	public User getUserInfo(long id) throws SmartMapClientException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", Long.toString(id));
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
	 * @see
	 * ch.epfl.smartmap.servercom.SmartMapClient#updatePos(ch.epfl.smartmap.
	 * cache.Point)
	 */
	public void updatePos(Location location) throws SmartMapClientException {
		Log.d("updatePos", "start");
		Map<String, String> params = new HashMap<String, String>();
		params.put("longitude", Double.toString(location.getLongitude()));
		params.put("latitude", Double.toString(location.getLatitude()));

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
	
	
	@Override
	public List<User> findUsers(String text) throws SmartMapClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("search_text", text);
		HttpURLConnection conn = getHttpURLConnection("/findUsers");
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
		
		List<User> friends = null;
		try {
			friends = parser.parseFriends(response);
		} catch (SmartMapParseException e) {
			throw new SmartMapClientException(e);
		}
		
		return friends;
	}

	private String longListToString(List<Long> list) {
		String listString = "";

		for (long n : list) {
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
	private String sendViaPost(Map<String, String> params, HttpURLConnection connection) 
			throws SmartMapClientException {
		StringBuffer response = null;
		Log.d("sendViaPost", "start");

		try {

			// Add request header
			connection.setRequestMethod("POST");

			if (params.size() != 0) {

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

			}

			if (connection.getResponseCode() != SERVER_RESPONSE_OK) {
				throw new SmartMapClientException("HTTP error with code " + connection.getResponseCode()
				        + " during communication with client.");
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

	private HttpURLConnection getHttpURLConnection(String uri)
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