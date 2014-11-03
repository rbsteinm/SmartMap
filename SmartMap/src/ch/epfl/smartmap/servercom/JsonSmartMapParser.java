package ch.epfl.smartmap.servercom;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.User;


/**
 * A {@link SmartMapParser} implementation that parses objects from Json format
 * 
 * @author marion-S
 * 
 */
public class JsonSmartMapParser implements SmartMapParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.servercom.SmartMapParser#parseFriend(java.lang.String)
	 */
	@Override
	public User parseFriend(String s) throws SmartMapParseException {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(s);
		} catch (JSONException e) {
			throw new SmartMapParseException();
		}
		return parseFriendFromJSON(jsonObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.servercom.SmartMapParser#parseFriends(java.lang.String)
	 */
	@Override
	public List<User> parseFriends(String s) throws SmartMapParseException {

		List<User> friends = new ArrayList<User>();

		try {
			JSONObject jsonObject = new JSONObject(s);

			JSONArray usersArray = jsonObject.getJSONArray("positions"); // to
																			// discuss

			for (int i = 0; i < usersArray.length(); i++) {
				JSONObject userJSON = usersArray.getJSONObject(i);
				User friend = parseFriendFromJSON(userJSON);
				friends.add(friend);
			}
		} catch (JSONException e) {
			throw new SmartMapParseException();
		}
		return friends;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.servercom.SmartMapParser#checkServerError(java.lang.
	 * String)
	 */
	@Override
	public void checkServerError(String s) throws SmartMapParseException,
			SmartMapClientException {

		String status = null;
		String message = null;
		try {
			JSONObject jsonObject = new JSONObject(s);
			status = jsonObject.getString("status");
			message = jsonObject.getString("message");
			Log.d("serverMessage", message);
			Log.d("serverAnswer", status);
		} catch (JSONException e) {
			throw new SmartMapParseException();
		}
		if (status.equals("Error")) {
			throw new SmartMapClientException(message);
		}

	}

	/**
	 * Return the friend parsed from a jsonObject
	 * 
	 * @param jsonObject
	 * @return a friend
	 * @throws SmartMapParseException
	 */
	private User parseFriendFromJSON(JSONObject jsonObject)
			throws SmartMapParseException {
		long id = 0;
		String name = null;
		String phoneNumber = null;
		String email = null;
		int online = 0;
		double latitude = -200;
		double longitude = -200;

		try {
			id = jsonObject.getLong("id");
			name = jsonObject.getString("name");
			latitude = jsonObject.optDouble("latitude", -200);
			longitude = jsonObject.optDouble("latitude", -200);
			phoneNumber = jsonObject.optString("phoneNumber", null);
			email = jsonObject.optString("email", null);
			online = jsonObject.optInt("online", -1);
			// something else??
		} catch (JSONException e) {
			throw new SmartMapParseException();
		}

		Friend friend = new Friend(id, name);

		if (latitude != -200) {
			if (!(-90 <= latitude && latitude <= 90)) {
				throw new SmartMapParseException("invalid latitude");
			}
			friend.setLatitude(latitude);
		}
		if (longitude != -200) {
			if (!(-180 <= latitude && latitude <= 180)) {
				throw new SmartMapParseException("invalid longitude");
			}
			friend.setLongitude(longitude);
		}

		if (phoneNumber != null) {
			// TODO some verifications, don't accept invalid phoneNumber
			friend.setNumber(phoneNumber);
		}
		if (email != null) {
			// TODO some verifications, don't accept invalid email
			friend.setEmail(email);
		}
		if (online != -1) {
			if (online == 0) {
				friend.setOnline(false);
			} else if (online == 1) {
				friend.setOnline(true);
			} else {
				throw new SmartMapParseException(
						"the value online must be either 0 or 1");
			}
		}

		return friend;
	}

}
