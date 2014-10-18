package ch.epfl.smartmap.servercom;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author SpicyCH
 *
 */
public class NetworkSmartMapClient implements ServerLoginProtocol {

    private static final String SERVER_SUCCESS = "OK"; // the positive server's response
    private static final String SERVER_ERROR = "ERROR"; // the positive server's response
    private static final String USER_AGENT = "Mozilla/5.0"; // latest firefox's user agent
    private URL mServerUrl;
    private HttpURLConnection mHttpURLConnection;
    private final NetworkProvider mNetworkProvider;

    public NetworkSmartMapClient(final String serverUrl, final NetworkProvider networkProvider) {
        try {
            mServerUrl = new URL(serverUrl);
            mHttpURLConnection = (HttpURLConnection) mServerUrl.openConnection();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(new MalformedURLException("The serverUrl is invalid"));
        } catch (IOException e) {
            throw new IllegalArgumentException(new MalformedURLException("The serverUrl is invalid"));
        }
        mNetworkProvider = networkProvider;
    }


    public String sendViaPost(Map<String, Object> params) {

        // Build the request
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) {
                postData.append('&');
            }
            try {
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                return SERVER_ERROR;
            }
        }

        mHttpURLConnection.setDoOutput(true); // Pour pouvoir envoyer des données

        // Add request header
        try {
            mHttpURLConnection.setRequestMethod("POST");
            mHttpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
            mHttpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        } catch (ProtocolException e) {
            e.printStackTrace();
            return SERVER_ERROR;
        }

        // Send post request
        mHttpURLConnection.setDoOutput(true);
        DataOutputStream wr;

        @SuppressWarnings("unused")
        int responseCode = -1;
        try {
            wr = new DataOutputStream(mHttpURLConnection.getOutputStream());
            wr.writeBytes(postData.toString());
            wr.flush();
            wr.close();
            responseCode = mHttpURLConnection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            return SERVER_ERROR;
        }


        // Get response
        String inputLine;
        StringBuffer response = new StringBuffer();
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(mHttpURLConnection.getInputStream()));

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return SERVER_ERROR;
        }

        // Finally give result to caller
        return response.toString();

    }

    /**
     * Gets the data from a {@link Reader}
     *
     * @param reader
     * @return
     * @throws IOException
     * @author SpicyCH
     */
    public static String readAll(final Reader reader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        int charRead;
        while ((charRead = reader.read()) != -1) {
            stringBuilder.append((char) charRead);
        }
        return stringBuilder.toString();
    }

}
