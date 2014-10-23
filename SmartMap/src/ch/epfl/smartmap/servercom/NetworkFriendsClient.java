package ch.epfl.smartmap.servercom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#followFriend(int)
	 */
	@Override
	public void followFriend(int id) throws SmartMapClientException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("friend_id", Integer.toString(id));
		String textResult = sendViaPost(params, "/followFriend");
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
	 * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#unfollowFriend(int)
	 */
	@Override
	public void unfollowFriend(int id) throws SmartMapClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("friend_id", Integer.toString(id));
		String textResult = sendViaPost(params, "/unfollowFriend");
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
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.epfl.smartmap.severcom.SmartMapFriendsClient#disallowFriend(int)
	 */
	@Override
	public void disallowFriend(int id) throws SmartMapClientException {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

}
