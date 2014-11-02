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
		long userId = 0;
		String userName = null;
		try {
			JSONObject jsonObject = new JSONObject(s);
			userId = jsonObject.getLong("id");
			userName = jsonObject.getString("name");
		} catch (JSONException e) {
			throw new SmartMapParseException();
		}

		return new Friend(userId, userName);
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

	/* (non-Javadoc)
	 * @see ch.epfl.smartmap.servercom.SmartMapParser#parseFriends(java.lang.String)
	 */
	@Override
	public List<User> parseFriends(String s) throws SmartMapParseException {

		List<User> friends = new ArrayList<User>();

		try {
			JSONObject jsonObject = new JSONObject(s);

			JSONArray usersArray = jsonObject.getJSONArray("positions");
			for (int i = 0; i < usersArray.length(); i++) {
				JSONObject userJSON = usersArray.getJSONObject(i);
				Friend friend = new Friend(userJSON.getLong("id"),
						userJSON.getString("name"));
				friend.setLongitude(userJSON.getDouble("latitude"));
				friend.setLatitude(userJSON.getDouble("latitude"));
				friends.add(friend);
			}
		} catch (JSONException e) {
			throw new SmartMapParseException();
		}
		return friends;
	}

}
