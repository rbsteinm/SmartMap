/**
 *
 */
package ch.epfl.smartmap.authentication;

import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author SpicyCH
 *
 */
public class ClientServerInteractions {

    /**
     * Sends the given parameters to the {@link HttpURLConnection}
     * @param params A map of the parameters and their values
     * @param httpURLConnection
     * @author SpicyCH
     * @throws IOException if we weren't able to send all the data
     */
    protected static void sendViaPost(Map<String, Object> params, HttpURLConnection httpURLConnection) throws
    IOException {

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) {
                postData.append('&');
            }
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }

        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        httpURLConnection.setDoOutput(true);
        httpURLConnection.getOutputStream().write(postDataBytes);
    }

    protected static String readAll(final Reader reader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        int charRead;
        while ((charRead = reader.read()) != -1) {
            stringBuilder.append((char) charRead);
        }
        return stringBuilder.toString();
    }
}
