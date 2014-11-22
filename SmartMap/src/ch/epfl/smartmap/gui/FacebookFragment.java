package ch.epfl.smartmap.gui;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.MainActivity;
import ch.epfl.smartmap.cache.SettingsManager;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

/**
 * The fragment for the "Login with Facebook" button, that is used in scrim (1)
 * 
 * @author SpicyCH
 */
public class FacebookFragment extends Fragment {

    private static final String TAG = FacebookFragment.class.getSimpleName();

    private static final String FACEBOOK_ID_POST_NAME = "facebookId";
    private static final String FACEBOOK_TOKEN_POST_NAME = "facebookToken";
    private static final String FACEBOOK_NAME_POST_NAME = "name";

    private UiLifecycleHelper mUiHelper;

    private final List<String> mPermissions;

    public FacebookFragment() {
        // We will need to access the user's friends list
        Log.d(TAG, "Instanciating the FB Login MainFragment");
        mPermissions = Arrays.asList("user_status", "user_friends");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUiHelper = new UiLifecycleHelper(this.getActivity(), callback);
        mUiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_start, container, false);

        // Get the login button by id from the view
        LoginButton authButton = (LoginButton) view.findViewById(R.id.loginButton);

        // Set other view's component to invisible
        view.findViewById(R.id.loadingBar).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.logo).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.welcome).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.loadingTextView).setVisibility(View.INVISIBLE);

        // Start animation and set login button
        authButton.startAnimation(AnimationUtils.loadAnimation(this.getActivity().getBaseContext(),
            R.anim.face_anim));
        authButton.setFragment(this);

        // Not logged in Facebook or permission to use Facebook in SmartMap not
        // given
        if ((Session.getActiveSession() == null) || Session.getActiveSession().getPermissions().isEmpty()) {
            authButton.setReadPermissions(mPermissions);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if ((session != null) && (session.isOpened() || session.isClosed())) {
            this.onSessionStateChange(session, session.getState(), null);
        }

        mUiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUiHelper.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onPause() {
        super.onPause();
        mUiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUiHelper.onSaveInstanceState(outState);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        Log.i(TAG, "Checking FB log in status...");
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");

            // Display the loading Bar and Text
            this.getView().findViewById(R.id.loadingBar).setVisibility(View.VISIBLE);
            this.getView().findViewById(R.id.loadingTextView).setVisibility(View.VISIBLE);

            // Disable facebook log out button (CLOSE ISSUE #16)
            this.getView().findViewById(R.id.loginButton).setAlpha(0f);

            // Display the authenticated UI here
            this.makeMeRequest();

        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
            // Display the non-authenticated UI here
        }
    }

    private final Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            FacebookFragment.this.onSessionStateChange(session, state, exception);
        }
    };

    protected void makeMeRequest() {
        Request request = Request.newMeRequest(Session.getActiveSession(), new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {

                if (user != null) {

                    // This portable token is used by the server
                    String facebookToken = Session.getActiveSession().getAccessToken();

                    // Send user's infos to SmartMap server
                    Map<String, String> params = new LinkedHashMap<String, String>();
                    params.put(FACEBOOK_ID_POST_NAME, user.getId());
                    params.put(FACEBOOK_NAME_POST_NAME, user.getName());
                    params.put(FACEBOOK_TOKEN_POST_NAME, facebookToken);
                    // TODO put user's friends?

                    // Debug
                    Log.i(TAG, "user name: " + params.get("name"));
                    Log.i(TAG, "user facebookId: " + params.get(FACEBOOK_ID_POST_NAME));

                    SettingsManager.getInstance().setUserName(params.get("name"));

                    if (!FacebookFragment.this.sendDataToServer(params)) {
                        Toast.makeText(FacebookFragment.this.getActivity(),
                            "Failed to log in to the SmartMap server.", Toast.LENGTH_LONG).show();
                    } else {
                        // Create and start the next activity
                        FacebookFragment.this.startMainActivity();
                    }

                } else if (response.getError() != null) {
                    Log.e(TAG, "The user is null (authentication aborted?)");
                }
            }
        });

        request.executeAsync();
    }

    private void startMainActivity() {
        Activity currentActivity = this.getActivity();
        Intent intent = new Intent(this.getActivity(), MainActivity.class);
        this.startActivity(intent);
        currentActivity.finish();
    }

    private boolean sendDataToServer(Map<String, String> params) {

        ConnectivityManager connMgr =
            (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if ((networkInfo != null) && networkInfo.isConnected()) {
            // Send data
            SendDataTask task = new SendDataTask(params);
            task.execute();
            return true;
        } else {
            // An error occured
            Log.e(TAG, "Could not send user's data to server. Net down?");
            Toast.makeText(this.getActivity(), "Your internet connection seems down. Please try again!",
                Toast.LENGTH_LONG).show();
            return false;
        }

    }

    /**
     * An AsyncTask to send the facebook user data to the SmartMap server
     * asynchronously
     * 
     * @author SpicyCH
     */
    private class SendDataTask extends AsyncTask<Void, Void, Boolean> {

        private final static int FACEBOOK_ID_RADIX = 10;
        private final Map<String, String> mParams;

        /**
         * @param params
         */
        public SendDataTask(Map<String, String> params) {
            mParams = params;
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Boolean doInBackground(Void... params) {

            NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();

            try {
                networkClient.authServer(mParams.get(FACEBOOK_NAME_POST_NAME),
                    Long.parseLong(mParams.get(FACEBOOK_ID_POST_NAME), FACEBOOK_ID_RADIX),
                    mParams.get(FACEBOOK_TOKEN_POST_NAME));
            } catch (NumberFormatException e1) {
                Log.e(TAG, "Couldn't parse to Long: " + e1.getMessage());
                e1.printStackTrace();
                return false;
            } catch (SmartMapClientException e1) {
                Log.e(TAG, "Couldn't authenticate : " + e1.getMessage());
                e1.printStackTrace();
                return false;
            }

            Log.i(TAG, "User' infos sent to SmartMap server");
            return true;

        }
    }
}