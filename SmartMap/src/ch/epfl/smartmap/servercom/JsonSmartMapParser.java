package ch.epfl.smartmap.servercom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.User;

/**
 * A {@link SmartMapParser} implementation that parses objects from Json format
 * 
 * @author marion-S
 * @author SpicyCH (code reviewed 02.11.2014) : changed Error to error as the
 *         server uses a lowercase e.
 */
@SuppressLint("UseSparseArrays")
public class JsonSmartMapParser implements SmartMapParser {

	private static final int UINITIALIZED_LATITUDE = -200;
	private static final int UNITIALIZED_LONGITUDE = -200;
	private static final int MIN_LATITUDE = -90;
	private static final int MAX_LATITUDE = 90;
	private static final int MIN_LONGITUDE = -180;
	private static final int MAX_LONGITUDE = 180;
	private static final int MAX_NAME_LENGTH = 60;

	private static final String TAG = "JSON_PARSER";

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
		return parseFriendFromJSON(jsonObject);
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
				User friend = parseFriendFromJSON(userJSON);
				friends.add(friend);
			}
		} catch (JSONException e) {
			throw new SmartMapParseException(e);
		}

		Log.d(TAG, Integer.toString(friends.size()));
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
			Log.d("serverMessage", message);
			Log.d("serverAnswer", status);
		} catch (JSONException e) {
			throw new SmartMapParseException(e);
		}
		if (status.equals("error")) {
			throw new SmartMapClientException(message);
		}

	}

	@Override
	public Map<Long, Location> parsePositions(String s) throws SmartMapParseException {
		Map<Long, Location> positions = new HashMap<Long, Location>();

		try {
			JSONObject jsonObject = new JSONObject(s);

			JSONArray usersArray = jsonObject.getJSONArray("positions");

			for (int i = 0; i < usersArray.length(); i++) {
				JSONObject position = usersArray.getJSONObject(i);
				long userId = position.getLong("id");
				double latitude = position.getDouble("latitude");
				double longitude = position.getDouble("longitude");

				checkId(userId);
				checkLatitude(latitude);
				checkLongitude(longitude);

				Log.d(TAG, Long.toString(userId));
				Log.d(TAG, Double.toString(latitude));
				Log.d(TAG, Double.toString(longitude));

				Location location = new Location("SmartMapServers");
				location.setLatitude(latitude);
				location.setLongitude(longitude);
				positions.put(userId, location);
			}
			Log.d(TAG + "number of positions in the list", Integer.toString(positions.size()));
		} catch (JSONException e) {
			throw new SmartMapParseException(e);
		}

		return positions;
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
		String online = null;
		double latitude = UINITIALIZED_LATITUDE;
		double longitude = UNITIALIZED_LONGITUDE;

		try {
			id = jsonObject.getLong("id");
			name = jsonObject.getString("name");
			latitude = jsonObject.optDouble("latitude", UINITIALIZED_LATITUDE);
			longitude = jsonObject.optDouble("longitude", UNITIALIZED_LONGITUDE);
			phoneNumber = jsonObject.optString("phoneNumber", null);
			email = jsonObject.optString("email", null);
			online = jsonObject.optString("online", null);
			// something else??
		} catch (JSONException e) {
			throw new SmartMapParseException(e);
		}

		checkId(id);
		checkName(name);
		Log.d(TAG, Long.toString(id));
		Log.d(TAG, name);
		Friend friend = new Friend(id, name);

		if (latitude != UINITIALIZED_LATITUDE) {
			checkLatitude(latitude);
			Log.d(TAG, Double.toString(latitude));
			friend.setLatitude(latitude);
		}
		if (longitude != UNITIALIZED_LONGITUDE) {
			checkLongitude(longitude);
			Log.d(TAG, Double.toString(longitude));
			friend.setLongitude(longitude);
		}

		if (phoneNumber != null) {
			checkPhoneNumber(phoneNumber);
			Log.d(TAG, phoneNumber);
			friend.setNumber(phoneNumber);
		}
		if (email != null) {
			checkEmail(email);
			Log.d(TAG, email);
			friend.setEmail(email);
		}
		if (online != null) {
			checkOnLine(online);
			// TODO see with Mathieu
		}

		return friend;
	}

	/**
	 * Checks if the latitude is valid
	 * 
	 * @param latitude
	 * @throws SmartMapParseException
	 *             if invalid latitude
	 */
	private void checkLatitude(double latitude) throws SmartMapParseException {
		if (!(MIN_LATITUDE <= latitude && latitude <= MAX_LATITUDE)) {
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
		if (!(MIN_LONGITUDE <= longitude && longitude <= MAX_LONGITUDE)) {
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
		if (name.length() >= MAX_NAME_LENGTH || name.length() == 0) {
			throw new SmartMapParseException("invalid name");
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
	 * Checks if the parameter "online" is valid
	 * 
	 * @param online
	 * @throws SmartMapParseException
	 *             if invalid parameter
	 */
	private void checkOnLine(String online) throws SmartMapParseException {
		// TODO
	}

}
