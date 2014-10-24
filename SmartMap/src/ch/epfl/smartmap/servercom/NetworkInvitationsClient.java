package ch.epfl.smartmap.servercom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.smartmap.cache.User;

/**
 * A {@link InvitationsClient} implementation that uses a
 * {@link NetworkProvider} to communicate with a SmartMap server.
 * 
 * @author marion-S
 * 
 */
public class NetworkInvitationsClient extends SmartMapClient implements
		InvitationsClient {

	public NetworkInvitationsClient(String serverUrl,
			NetworkProvider networkProvider) {
		super(serverUrl, networkProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.severcom.SmartMapInvitationsClient#inviteFriend(java
	 * .lang.String)
	 */
	@Override
	public void inviteFriend(int id) throws SmartMapClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("friend_id", Integer.toString(id));
		String textResult = sendViaPost(params, "/inviteFriend");
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

		String textResult = sendViaPost(null, "/getInvitation");

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
		String textResult = sendViaPost(params, "/acceptInvitation");
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
		String textResult = sendViaPost(params, "/getUserInfo");
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

}
