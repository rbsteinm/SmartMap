package ch.epfl.smartmap.servercom;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.smartmap.cache.Point;

/**
 * A {@link PositionClient} implementation that uses a {@link NetworkProvider}
 * to communicate with a SmartMap server.
 * 
 * @author marion-S
 * 
 */
public class NetworkPositionClient extends SmartMapClient implements
		PositionClient {

	public NetworkPositionClient(String serverUrl,
			NetworkProvider networkProvider) {
		super(serverUrl, networkProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.epfl.smartmap.severcom.SmartMapPositionClient#updatePos()
	 */
	@Override
	public void updatePos(Point position) throws SmartMapClientException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("longitude", Double.toString(position.getX()));
		params.put("latitude", Double.toString(position.getY()));

		HttpURLConnection conn=getHttpURLConnection("/updatePos");
		String response = sendViaPost(params, conn);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(response);
		} catch (JSONException e) {
			throw new SmartMapClientException(e);
		}
		checkServerErrorFromJSON(jsonObject);

	}

}
