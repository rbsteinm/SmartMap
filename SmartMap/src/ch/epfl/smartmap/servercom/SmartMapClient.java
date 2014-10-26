package ch.epfl.smartmap.servercom;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import ch.epfl.smartmap.cache.Friend;


/**
 * A class that contains methods that all SmartMap clients have in common
 * 
 * @author marion-S
 * 
 */
public abstract class SmartMapClient {

	public static final String USER_AGENT = "Mozilla/5.0"; // latest firefox's
															// user agent
	static final String COOKIES_HEADER = "Set-Cookie";
	private String mServerUrl;
	private NetworkProvider mNetworkProvider;
	private static CookieManager mCookieManager = new CookieManager();

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
		CookieHandler.setDefault(mCookieManager);

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
	public Friend parseFriendfromJSON(JSONObject jsonObject)
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
	public void checkServerErrorFromJSON(JSONObject jsonObject) throws SmartMapClientException {
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

			// Get Cookies form cookieManager and load them to connection
			Log.d("cookies number", Integer.toString(mCookieManager.getCookieStore().getCookies().size()));
			if (mCookieManager.getCookieStore().getCookies().size() > 0) {
				Log.d("add cookies", "add stored cookies to the headers ");
				connection.setRequestProperty("Cookie", TextUtils.join(",",
						mCookieManager.getCookieStore().getCookies()));
			}

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

			List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

			if (cookiesHeader != null) {
				Log.d("store cookies", "store cookies");
				for (String cookie : cookiesHeader) {
					mCookieManager.getCookieStore().add(null,
							HttpCookie.parse(cookie).get(0));
				}
			}
			Log.d("cookies number", Integer.toString(mCookieManager.getCookieStore().getCookies().size()));
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

	public HttpURLConnection getHttpURLConnection(String uri) throws SmartMapClientException {
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