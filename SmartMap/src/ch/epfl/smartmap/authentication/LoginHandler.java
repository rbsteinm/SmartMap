/**
 *
 */
package ch.epfl.smartmap.authentication;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.View;

/**
 * Handles the login
 *
 * @author SpicyCH
 *
 */
public class LoginHandler {

    /**
     * The different connection status. Only CONNECTION_SUCCESS is a success,
     * all other status are different erros.
     *
     * @author SpicyCH
     */
    public enum ConnectionStatus {
        CONNECTION_SUCCESS, CONNECTION_FAILED, MALFORMED_URL
    }

    private final View mView;
    private final String mServerUrl;
    private String mPhoneNumber;
    private final TelephonyManager mTelephonyManager;
    private final static String FILE_STORING_HASH = "connection_hash.sha256";

    /**
     * Creates a LoginHandler using the application's view
     *
     * @param view
     */
    public LoginHandler(View view, String serverUrl) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(serverUrl).openConnection();
            httpURLConnection.disconnect();
        } catch (IOException e) {
            throw new IllegalArgumentException("The connection to the server url has failed: " + e.toString());
        }

        mView = view;
        mServerUrl = serverUrl;
        mTelephonyManager = (TelephonyManager) mView.getContext().getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     *
     * @return a String containing the phone number if it could be retrieved,
     *         and the empty String "" otherwise
     * @author SpicyCH
     */
    public String getPhoneNumber() {

        if (mTelephonyManager.getLine1Number() == null) {
            return "";
        } else {
            return mTelephonyManager.getLine1Number();
        }
    }

    public void setPhoneNumber(String number) {
        mPhoneNumber = number;
    }

    /**
     * 3 cases:
     * <p>
     * -The hash exists in a file locally on the phone. We check if it is the same as the
     * one stored in the server-side database. User is then logged in or out accordingly
     * in.
     * </p>
     * <p>
     * -This is the scenario where the app is launched for the first time. The
     * hash doesn't exist locally. We thus need to query the server for a new
     * one.
     * </p>
     *
     * @return A ConnectionStatus constant specifying if the connection was
     *         successful or not.
     * @author SpicyCH
     */
    public ConnectionStatus connect() {
        HttpURLConnection httpURLConnection = null;

        // Opens the connection
        try {
            httpURLConnection = (HttpURLConnection) new URL(mServerUrl).openConnection();

            try {
                // Case 1
                String hash = getLocalHash();

                Map<String, Object> params = new LinkedHashMap<String, Object>();

                params.put("localHash", hash);
                params.put("phoneNumber", mPhoneNumber);

                ClientServerInteractions.sendViaPost(params, httpURLConnection);
                //TODO read reply...


            } catch (IOException e) {
                // The file could not be retrieved, so we try and ask the server
                // for
                // a new hash
                // TODO query server and configure nexmo
            }

        } catch (MalformedURLException e1) {
            return ConnectionStatus.MALFORMED_URL;
        } catch (IOException e1) {
            return ConnectionStatus.CONNECTION_FAILED;
        } finally {
            httpURLConnection.disconnect();
        }

        return ConnectionStatus.CONNECTION_FAILED;
    }

    private void createLocalHash(String hash) throws IOException {

        FileOutputStream fos = mView.getContext().openFileOutput(FILE_STORING_HASH, Context.MODE_PRIVATE);
        fos.write(hash.getBytes());
        fos.close();
    }

    private String getLocalHash() throws IOException {
        FileInputStream fis = mView.getContext().openFileInput(FILE_STORING_HASH);
        return ClientServerInteractions.readAll(new BufferedReader(new InputStreamReader(fis, Charset
                .forName("UTF-8"))));
    }

}