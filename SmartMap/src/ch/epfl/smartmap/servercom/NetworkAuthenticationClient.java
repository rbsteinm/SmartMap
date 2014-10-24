package ch.epfl.smartmap.servercom;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.smartmap.cache.User;

/**
 * A {@link AuthenticationClient} implementation that uses a
 * {@link NetworkProvider} to communicate with a SmartMap server.
 * 
 * @author marion-S
 */

public class NetworkAuthenticationClient extends SmartMapClient implements
		AuthenticationClient {

	public NetworkAuthenticationClient(String serverUrl,
			NetworkProvider networkProvider) {
		super(serverUrl, networkProvider);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.servercom.AuthenticationClient#authServer(ch.epfl.smartmap
	 * .cache.User, java.lang.String)
	 */
	@Override
	public void authServer(User user, String fbAccessToken)
			throws SmartMapClientException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", Integer.toString(user.getID()));
		params.put("user_name", user.getName());
		params.put("fb_access_token", fbAccessToken);
		String textResult = sendViaPost(params, "/index.php/auth");
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(textResult);
		} catch (JSONException e) {
			throw new SmartMapClientException(e);
		}
		checkServerErrorFromJSON(jsonObject);

	}

}
