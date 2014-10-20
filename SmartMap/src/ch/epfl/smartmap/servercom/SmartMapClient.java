package ch.epfl.smartmap.servercom;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.User;

/**
 * A class that contains methods that all SmartMap clients have in common
 * 
 * @author marion-S
 * 
 */
public abstract class SmartMapClient {

	public static final String USER_AGENT = "Mozilla/5.0"; // latest firefox's
															// user agent
	protected String mServerUrl;
	protected NetworkProvider mNetworkProvider;

	/**
	 * Creates a new SmartMapClient instance that communicates with a SmartMap
	 * server at a given location, through a {@link NetworkProvider} object.
	 * 
	 * @param serverUrl
	 *            the base URL of the SmartMap server
	 * @param networkProvider
	 *            a NetworkProvider object through which to channel the server
	 *            communication.
	 */
	public SmartMapClient(String serverUrl, NetworkProvider networkProvider) {
		this.mServerUrl = serverUrl;
		this.mNetworkProvider = networkProvider;

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
	public User parseUserfromJSON(JSONObject jsonObject) throws JSONException {
		int userId = jsonObject.getInt("userId");
		String userName = jsonObject.getString("userName");

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
	public void checkServerErrorFromJSON(JSONObject jsonObject)
		throws JSONException, SmartMapClientException {
		String status = jsonObject.getString("status");
		String message = jsonObject.getString("message");

		if (status.equals("ERROR")) {
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
	public String sendViaPost(Map<String, String> params, String uri)
			throws SmartMapClientException {
		
		//Get the HttpURLConnection
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

		// Build the request
		StringBuilder postData = new StringBuilder();
		for (Map.Entry<String, String> param : params.entrySet()) {
			if (postData.length() != 0) {
				postData.append('&');
			}
			try {
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(
						String.valueOf(param.getValue()), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new SmartMapClientException(e);
			}
		}

		connection.setDoOutput(true); // To be able to send data

		// Add request header
		// TODO TO DISCUSS
		try {
			connection.setRequestMethod("POST");
			connection.setRequestProperty("User-Agent", USER_AGENT);
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		} catch (ProtocolException e) {
			throw new SmartMapClientException(e);
		}

		// Send post request
		connection.setDoOutput(true);
		DataOutputStream wr;

		try {
			wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(postData.toString());
			wr.flush();
			wr.close();
		} catch (IOException e) {
			throw new SmartMapClientException(e);
		}

		// Get response
		String inputLine;
		StringBuffer response = new StringBuffer();
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (IOException e) {
			throw new SmartMapClientException(e);
		}

		// Finally give result to caller
		return response.toString();

	}
}