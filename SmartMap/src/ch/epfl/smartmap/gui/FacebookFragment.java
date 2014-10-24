package ch.epfl.smartmap.gui;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
import ch.epfl.smartmap.servercom.DefaultNetworkProvider;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

/**
 * The fragment for the "Login with Facebook" button, that is used in scrim (1)
 * - Welcome
 * 
 * @author SpicyCH
 * 
 */
public class FacebookFragment extends Fragment {

	private static final String TAG = FacebookFragment.class.getSimpleName();

	protected static final String CONNECT_USER_URL = "http://swissgen.net/smartmap/connectUser.php";

	protected static final String SERVER_CONFIRMATION = "OK";

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
		mUiHelper = new UiLifecycleHelper(getActivity(), callback);
		mUiHelper.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_start, container, false);

		// Get the login button by id from the view
		LoginButton authButton = (LoginButton) view.findViewById(R.id.loginButton);
		
		// Set other view's component to invisible
		view.findViewById(R.id.loadingBar).setVisibility(View.INVISIBLE);
		view.findViewById(R.id.logo).setVisibility(View.INVISIBLE);
		view.findViewById(R.id.welcome).setVisibility(View.INVISIBLE);
		view.findViewById(R.id.loadingTextView).setVisibility(View.INVISIBLE);
		
		// Start animation and set login button
		authButton.startAnimation(AnimationUtils.loadAnimation(this
				.getActivity().getBaseContext(), R.anim.face_anim));
		authButton.setFragment(this);
		authButton.setReadPermissions(mPermissions);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
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

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		Log.i(TAG, "Checking FB log in status...");
		if (state.isOpened()) {
			Log.i(TAG, "Logged in...");
			
			// Display the loading Bar and Text
			getView().findViewById(R.id.loadingBar).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.loadingTextView).setVisibility(View.VISIBLE);

			// TODO Don't display LogOut Button
			
			// Display the authenticated UI here
			makeMeRequest();
			

		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
			// Display the non-authenticated UI here
		}
	}

	private final Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private void makeMeRequest() {
		final JSONObject userProfile = new JSONObject();
		Request request = Request.newMeRequest(Session.getActiveSession(),
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
												
						if (user != null) {
							
							String userName = user.getName();
							// This portable token can be used by the server
							String facebookToken = Session.getActiveSession()
									.getAccessToken();

							// Create a JSON object to hold the profile info
							try {

								// Populate the JSON object
								userProfile.put("facebookId", user.getId());
								userProfile.put("name", userName);
								userProfile.put("facebookToken", facebookToken);
								// userProfile.put("friends",
								// user.getProperty("friends"));

								Log.i(TAG, "user name in json (async): "
										+ userProfile.getString("name"));
								Log.i(TAG, "user ID in json (async): "
										+ userProfile.getString("facebookId"));

							} catch (JSONException e) {
								Log.e(TAG, "Error parsing returned user data.");
							}

							// TODO store userProfile locally (see with
							// Nicolas?)

							// Send user's infos to SmartMap server
							Map<String, Object> params = new LinkedHashMap<String, Object>();
							params.put("facebookId", user.getId());
							params.put("name", userName);
							params.put("facebookToken", facebookToken);
							// params.put("friends",
							// user.getProperty("friends"));
							sendDataToServer(params);

							// Create and start the next activity
							Toast.makeText(getActivity(),
									"You logged in successfully, " + userName,
									Toast.LENGTH_LONG).show();
							Intent intent = new Intent(getActivity(),
									MainActivity.class);
							startActivity(intent);

						} else if (response.getError() != null) {
							Log.e(TAG, "The user is null");
						}
					}
				});

		request.executeAsync();
	}

	private boolean sendDataToServer(Map<String, Object> params) {

		ConnectivityManager connMgr = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			// Send data
			SendDataTask task = new SendDataTask(params);
			task.execute();
			return true;
		} else {
			// An error occured
			Log.e(TAG, "Could not send user's data to server. Net down?");
			return false;
		}

	}

	/**
	 * An AsyncTask to send the facebook user data to the SmartMap server
	 * asynchronously
	 * 
	 * @author SpicyCH
	 * 
	 */
	private class SendDataTask extends AsyncTask<Void, Void, Boolean> {

		private final Map<String, Object> mParams;

		/**
		 * @param params
		 */
		public SendDataTask(Map<String, Object> params) {
			mParams = params;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Boolean doInBackground(Void... params) {
			DefaultNetworkProvider provider = new DefaultNetworkProvider();
			NetworkSmartMapClient networkClient = new NetworkSmartMapClient(
					CONNECT_USER_URL, provider);

			String serverAnswer = networkClient.sendViaPost(mParams);

			if (SERVER_CONFIRMATION.equals(serverAnswer)) {
				Log.i(TAG, "User's data sent to SmartMap server");
				return true;
			} else {
				Log.e(TAG, "Couldn't send user's data to server");
				return false;
			}
		}

	}
}
