package ch.epfl.smartmap.servercom;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.User;

/**
 * A {@link FriendsClient} implementation that uses a {@link NetworkProvider} to
 * communicate with a SmartMap server.
 * 
 * @author marion-S
 */
public class NetworkFriendsClient extends SmartMapClient implements
		FriendsClient {

	public NetworkFriendsClient(String serverUrl,
			NetworkProvider networkProvider) {
		super(serverUrl, networkProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#listFriendPos()
	 */
	@Override
	public List<User> listFriendPos() throws SmartMapClientException {
		// TODO Auto-generated method stub
		HttpURLConnection conn=getHttpURLConnection("/listFriendPos");
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
		HttpURLConnection conn=getHttpURLConnection("/followFriend");
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
		HttpURLConnection conn=getHttpURLConnection("/unfollowFriend");
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
		HttpURLConnection conn=getHttpURLConnection("/allowFriend");
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
		HttpURLConnection conn=getHttpURLConnection("/disallowFriend");
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
	public void allowFriendList(List<Integer> ids) throws SmartMapClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("friend_ids", intListToString(ids));
		HttpURLConnection conn=getHttpURLConnection("/allowFriendList");
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
	public void disallowFriendList(List<Integer> ids) throws SmartMapClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("friend_ids", intListToString(ids));
		HttpURLConnection conn=getHttpURLConnection("/disallowFriendList");
		String textResult = sendViaPost(params, conn);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(textResult);
			checkServerErrorFromJSON(jsonObject);
		} catch (JSONException e) {
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

}
