package ch.epfl.smartmap.servercom;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.Point;
import ch.epfl.smartmap.cache.User;

import android.text.TextUtils;
import android.util.Log;

/**
 * A {@link SmartMapClient} implementation that uses a {@link NetworkProvider}
 * to communicate with a SmartMap server.
 * 
 * @author marion-S
 * 
 */

public class NetworkSmartMapClient implements SmartMapClient {

	private String mServerUrl="http://smartmap.ddns.net";
	private NetworkProvider mNetworkProvider=new DefaultNetworkProvider();
	static final String COOKIES_HEADER = "Set-Cookie";
	public static final String USER_AGENT = "Mozilla/5.0"; // latest firefox's
															// user agent
	private String mSessionId;

	private static final NetworkSmartMapClient oneInstance = new NetworkSmartMapClient();

	private NetworkSmartMapClient() {
		if (oneInstance != null) {
			throw new IllegalStateException("Already instantiated");
		}
	}

	public static NetworkSmartMapClient getInstance() {
		return oneInstance;
	}

	// TODO
	// private static CookieManager mCookieManager = new CookieManager();

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
		String textResult = sendViaPost(params, conn);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(textResult);
		} catch (JSONException e) {
			throw new SmartMapClientException(e);
		}
		checkServerErrorFromJSON(jsonObject);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#listFriendPos()
	 */
	@Override
	public List<User> listFriendPos() throws SmartMapClientException {
		// TODO Auto-generated method stub
		HttpURLConnection conn = getHttpURLConnection("/listFriendPos");
		String textResult = sendViaPost(null, conn);

		List<User> friends = new ArrayList<User>();

		try {
			JSONObject jsonObject = new JSONObject(textResult);
			checkServerErrorFromJSON(jsonObject);
			JSONArray usersArray = jsonObject.getJSONArray("positions");
			for (int i = 0; i < usersArray.length(); i++) {
				JSONObject userJSON = usersArray.getJSONObject(i);
				Friend friend = parseFriendfromJSON(userJSON);
				// x=latitude, y=longitude??
				friend.setLongitude(userJSON.getDouble("latitude"));
				friend.setLatitude(userJSON.getDouble("latitude"));
				friends.add(friend);
			}
		} catch (JSONException e) {
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
		String textResult = sendViaPost(params, conn);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(textResult);
		} catch (JSONException e) {
			throw new SmartMapClientException(e);
		}
		checkServerErrorFromJSON(jsonObject);

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
		String textResult = sendViaPost(params, conn);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(textResult);
			checkServerErrorFromJSON(jsonObject);
		} catch (JSONException e) {
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
		String textResult = sendViaPost(params, conn);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(textResult);
			checkServerErrorFromJSON(jsonObject);
		} catch (JSONException e) {
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
		String textResult = sendViaPost(params, conn);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(textResult);
			checkServerErrorFromJSON(jsonObject);
		} catch (JSONException e) {
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
	public void allowFriendList(List<Integer> ids)
			throws SmartMapClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("friend_ids", intListToString(ids));
		HttpURLConnection conn = getHttpURLConnection("/allowFriendList");
		String textResult = sendViaPost(params, conn);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(textResult);
			checkServerErrorFromJSON(jsonObject);
		} catch (JSONException e) {
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
	public void disallowFriendList(List<Integer> ids)
			throws SmartMapClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("friend_ids", intListToString(ids));
		HttpURLConnection conn = getHttpURLConnection("/disallowFriendList");
		String textResult = sendViaPost(params, conn);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(textResult);
			checkServerErrorFromJSON(jsonObject);
		} catch (JSONException e) {
			throw new SmartMapClientException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.epfl.smartmap.servercom.SmartMapClient#inviteFriend(int)
	 */
	public void inviteFriend(int id) throws SmartMapClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("friend_id", Integer.toString(id));
		HttpURLConnection conn = getHttpURLConnection("/inviteFriend");
		String textResult = sendViaPost(params, conn);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(textResult);
		} catch (JSONException e) {
			throw new SmartMapClientException(e);
		}
		checkServerErrorFromJSON(jsonObject);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.epfl.smartmap.severcom.SmartMapInvitationsClient#getInvitations()
	 */
	@Override
	public List<User> getInvitations() throws SmartMapClientException {

		HttpURLConnection conn = getHttpURLConnection("/getInvitations");
		String textResult = sendViaPost(null, conn);

		List<User> inviters = new ArrayList<User>();

		try {
			JSONObject jsonObject = new JSONObject(textResult);
			checkServerErrorFromJSON(jsonObject);
			JSONArray usersArray = jsonObject.getJSONArray("list");
			for (int i = 0; i < usersArray.length(); i++) {
				JSONObject userJSON = usersArray.getJSONObject(i);
				inviters.add(parseFriendfromJSON(userJSON));
			}
		} catch (JSONException e) {
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
	public User acceptInvitation(int id) throws SmartMapClientException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("friend_id", Integer.toString(id));
		HttpURLConnection conn = getHttpURLConnection("/acceptInvitation");
		String textResult = sendViaPost(params, conn);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(textResult);
		} catch (JSONException e) {
			throw new SmartMapClientException(e);
		}
		checkServerErrorFromJSON(jsonObject);
		User acceptedUser = parseFriendfromJSON(jsonObject);
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
		String textResult = sendViaPost(params, conn);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(textResult);
		} catch (JSONException e) {
			throw new SmartMapClientException(e);
		}
		checkServerErrorFromJSON(jsonObject);
		User acceptedUser = parseFriendfromJSON(jsonObject);
		return acceptedUser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.servercom.SmartMapClient#updatePos(ch.epfl.smartmap.
	 * cache.Point)
	 */
	public void updatePos(Point position) throws SmartMapClientException {
		Log.d("updatePos", "start");
		Map<String, String> params = new HashMap<String, String>();
		params.put("longitude", Double.toString(position.getX()));
		params.put("latitude", Double.toString(position.getY()));

		HttpURLConnection conn = getHttpURLConnection("/updatePos");
		String response = sendViaPost(params, conn);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(response);
		} catch (JSONException e) {
			throw new SmartMapClientException(e);
		}
		checkServerErrorFromJSON(jsonObject);

	}

	private String intListToString(List<Integer> list) {
		String listString = "";

		for (int n : list) {
			listString += n + ",";
		}

		return listString;
	}

	/**
	 * Creates a new User object by parsing a JSON object in the format returned
	 * by the SmartMap server.
	 * 
	 * @param jsonObject
	 *            a {@link JSONObject} encoding.
	 * @return a new User object
	 * @throws JSONException
	 *             JSONException in case of malformed JSON.
	 */
	private Friend parseFriendfromJSON(JSONObject jsonObject)
			throws SmartMapClientException {
		long userId = 0;
		String userName = null;
		try {
			userId = jsonObject.getLong("id");
			userName = jsonObject.getString("name");
		} catch (JSONException e) {
			throw new SmartMapClientException(e);
		}

		return new Friend(userId, userName);
	}

	/**
	 * Checks in the JSONObject returned by the server if the server returned
	 * ERROR, and if it is the case throws a SmartMapClientException with the
	 * server's message
	 * 
	 * @param jsonObject
	 *            a {@link JSONObject} encoding.
	 * @throws JSONException
	 * @throws SmartMapClientException
	 */
	private void checkServerErrorFromJSON(JSONObject jsonObject)
			throws SmartMapClientException {
		String status = null;
		String message = null;
		try {
			status = jsonObject.getString("status");
			message = jsonObject.getString("message");
			Log.d("serverMessage", message);
			Log.d("serverAnswer", status);

		} catch (JSONException e) {
			throw new SmartMapClientException(e);
		}

		if (status.equals("Error")) {
			throw new SmartMapClientException(message);
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
			serverURL = new URL(mServerUrl + uri);
			connection = mNetworkProvider.getConnection(serverURL);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			throw new IllegalArgumentException();
		} catch (IOException e) {
			throw new SmartMapClientException(e);
		}
		return connection;
	}

}