package ch.epfl.smartmap.activities;

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
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.callbacks.NetworkRequestCallback;
import ch.epfl.smartmap.servercom.SmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

/**
 * <p>
 * The fragment for the "Login with Facebook" button, used by
 * {@linkplain ch.epfl.smartmap.activities.StartActivity} for screen 1.
 * </p>
 * <p>
 * On successful facebook login, we attempt to authenticate to the smartmap
 * server by sending the name, facebook id and facebook token.
 * </p>
 * 
 * @author SpicyCH
 */
public class LoginFragment extends Fragment {

    /**
     * This callback uses the retrieved facebook data to connect to our server.
     * 
     * @author SpicyCH
     */
    private class CustomGraphUserCallback implements Request.GraphUserCallback {
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

                // Displays the name, facebookId and facebookToken. When we
                // upload the app on google play,
                // we might
                // want to remove these logcat messages.
                Log.i(TAG, "user name: " + params.get(FACEBOOK_NAME_POST_NAME));
                Log.i(TAG, "user facebookId: " + params.get(FACEBOOK_ID_POST_NAME));
                Log.i(TAG, "user facebookToken: " + params.get(FACEBOOK_TOKEN_POST_NAME));

                if (!LoginFragment.this.sendDataToServer(params)) {
                    Toast.makeText(mActivity,
                        LoginFragment.this.getString(R.string.fb_fragment_toast_cannot_connect_to_smartmap_server),
                        Toast.LENGTH_LONG).show();
                } else {
                    // If all is ok, start filling Cache
                    ServiceContainer.getCache().initFromDatabase(ServiceContainer.getDatabase());
                    ServiceContainer.getCache().updateFromNetwork(ServiceContainer.getNetworkClient(),
                        new NetworkRequestCallback<Void>() {
                            @Override
                            public void onFailure(Exception e) {
                                Log.e(TAG, "Cannot update Cache from Network");
                                LoginFragment.this.startMainActivity();
                            }

                            @Override
                            public void onSuccess(Void result) {
                                LoginFragment.this.startMainActivity();
                            }
                        });
                }

            } else if (response.getError() != null) {
                Log.e(TAG, "The user is null (authentication aborted?)");
            }
        }
    }

    /**
     * An AsyncTask to send the facebook user data to the SmartMap server
     * asynchronously
     * 
     * @author SpicyCH
     */
    private class SendDataTask extends AsyncTask<Void, Void, Boolean> {

        private static final int FACEBOOK_ID_RADIX = 10;
        private final Map<String, String> mParams;

        /**
         * Constructor
         * 
         * @param params
         *            the post parameters to send (must be
         *            <code>FACEBOOK_NAME_POST_NAME</code>,
         *            <code>FACEBOOK_ID_POST_NAME</code>,
         *            <code>FACEBOOK_TOKEN_POST_NAME</code>.
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

            SmartMapClient networkClient = ServiceContainer.getNetworkClient();

            try {
                networkClient.authServer(mParams.get(FACEBOOK_NAME_POST_NAME),
                    Long.parseLong(mParams.get(FACEBOOK_ID_POST_NAME), FACEBOOK_ID_RADIX),
                    mParams.get(FACEBOOK_TOKEN_POST_NAME));
            } catch (NumberFormatException e1) {
                Log.e(TAG, "Couldn't parse to Long: " + e1);
                return false;
            } catch (SmartMapClientException e1) {
                Log.e(TAG, "Couldn't authenticate : " + e1);
                return false;
            }

            Log.i(TAG, "User' infos sent to SmartMap server");

            return true;

        }
    }

    private static final String TAG = LoginFragment.class.getSimpleName();
    private static final String FACEBOOK_ID_POST_NAME = "facebookId";

    private static final String FACEBOOK_TOKEN_POST_NAME = "facebookToken";

    private static final String FACEBOOK_NAME_POST_NAME = "name";

    private UiLifecycleHelper mUiHelper;

    private final List<String> mPermissions;

    private Activity mActivity;

    private final Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            LoginFragment.this.onSessionStateChange(state);
        }
    };

    public LoginFragment() {
        // We will need to access the user's friends list in the future.
        mPermissions = Arrays.asList("user_status", "user_friends");
        mActivity = this.getActivity();
    }

    protected void makeMeRequest() {
        Request request = Request.newMeRequest(Session.getActiveSession(), new CustomGraphUserCallback());
        request.executeAsync();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUiHelper.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this.getActivity();
        mUiHelper = new UiLifecycleHelper(mActivity, callback);
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
        authButton.startAnimation(AnimationUtils.loadAnimation(this.getActivity(), R.anim.face_anim));
        authButton.setFragment(this);

        // Not logged in Facebook or permission to use Facebook in SmartMap not
        // given
        if ((Session.getActiveSession() == null) || Session.getActiveSession().getPermissions().isEmpty()) {
            authButton.setReadPermissions(mPermissions);
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUiHelper.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mUiHelper.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.

        mUiHelper.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUiHelper.onSaveInstanceState(outState);
    }

    private void onSessionStateChange(SessionState state) {
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
        }
    }

    /**
     * Sends the params to the SmartMap server.
     * 
     * @param params
     *            a map with values for the keys name, facebookId and
     *            facebookToken
     * @return <code>true</code> if the internet connection is up and the data
     *         is beeing processed by an asynctask
     * @author SpicyCH
     */
    private boolean sendDataToServer(Map<String, String> params) {

        assert null != params.get(FACEBOOK_TOKEN_POST_NAME) : "Facebook token is null";
        assert !params.get(FACEBOOK_TOKEN_POST_NAME).isEmpty() : "Facebook token is empty";
        assert null != params.get(FACEBOOK_ID_POST_NAME) : "Facebook id is null";
        assert !params.get(FACEBOOK_ID_POST_NAME).isEmpty() : "Facebook id is empty";
        assert null != params.get(FACEBOOK_NAME_POST_NAME) : "Facebook name is null";
        assert !params.get(FACEBOOK_NAME_POST_NAME).isEmpty() : "Facebook name is empty";

        ConnectivityManager connMgr = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if ((networkInfo != null) && networkInfo.isConnected()) {
            // Send data
            SendDataTask task = new SendDataTask(params);
            task.execute();
            return true;
        } else {
            // An error occured
            Log.e(TAG, "Could not send user's data to server. Net down?");
            Toast.makeText(mActivity, this.getString(R.string.fb_fragment_toast_cannot_connect_to_internet),
                Toast.LENGTH_LONG).show();
            return false;
        }

    }

    private void startMainActivity() {
        Log.d(TAG, "START MAIN ACTIVITY");
        Intent intent = new Intent(mActivity, MainActivity.class);
        mActivity.finish();
        mActivity.startActivity(intent);
    }
}