package ch.epfl.smartmap.gui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import ch.epfl.smartmap.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import android.content.SharedPreferences;

/**
 * @author hugo
 * 
 */
public class LocationActivity extends Activity implements LocationListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	// A request to connect to Location Services
	private LocationRequest mLocationRequest;

	// Stores the current instantiation of the location client in this object
	private LocationClient mLocationClient;

	// Handle to SharedPreferences for this app
	SharedPreferences mPrefs;

	// Handle to a SharedPreferences editor
	SharedPreferences.Editor mEditor;

	/*
	 * Note if updates have been turned on. Starts out as "false"; is set to
	 * "true" in the method handleRequestSuccess of LocationUpdateReceiver.
	 */
	boolean mUpdatesRequested = false;

	public LocationActivity() {

		mLocationRequest = LocationRequest.create();

		mLocationRequest
				.setInterval(LocationTools.UPDATE_INTERVAL_IN_MILLISECONDS);

		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		mLocationRequest
				.setFastestInterval(LocationTools.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

		mUpdatesRequested = false;

		mPrefs = getSharedPreferences(LocationTools.SHARED_PREFERENCES,
				Context.MODE_PRIVATE);

		mEditor = mPrefs.edit();

		mLocationClient = new LocationClient(this, this, this);

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		if (connectionResult.hasResolution()) {
			try {

				connectionResult.startResolutionForResult(this,
						LocationTools.CONNECTION_FAILURE_RESOLUTION_REQUEST);

			} catch (IntentSender.SendIntentException e) {

				e.printStackTrace();
			}
		} else {
			// TODO

		}
	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, we request the current location or
	 * start periodic updates
	 */
	@Override
	public void onConnected(Bundle connectionHint) {

		Toast.makeText(this, R.string.connected, Toast.LENGTH_SHORT).show();

		if (mUpdatesRequested) {
			startPeriodicUpdates();
		}
	}

	/*
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		Toast.makeText(this, R.string.disconnected, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLocationChanged(Location location) {
		// udatePOS
	}

	/*
	 * Called when the Activity is no longer visible at all. Stop updates and
	 * disconnect.
	 */
	@Override
	public void onStop() {
		if (mLocationClient.isConnected()) {
			stopPeriodicUpdates();
		}

		mLocationClient.disconnect();

		super.onStop();
	}

	/**
	 * In response to a request to start updates, send a request to Location
	 * Services
	 */
	private void startPeriodicUpdates() {

		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		Toast.makeText(this, R.string.location_requested, Toast.LENGTH_LONG)
				.show();
	}

	/**
	 * In response to a request to stop updates, send a request to Location
	 * Services
	 */
	private void stopPeriodicUpdates() {
		mLocationClient.removeLocationUpdates(this);
		Toast.makeText(this, R.string.location_updates_stopped,
				Toast.LENGTH_SHORT).show();
	}

	/*
	 * Called when the Activity is going into the background. Parts of the UI
	 * may be visible, but the Activity is inactive.
	 */
	@Override
	public void onPause() {

		// Save the current setting for updates
		mEditor.putBoolean(LocationTools.KEY_UPDATES_REQUESTED,
				mUpdatesRequested);
		mEditor.commit();
		super.onPause();
	}

	/*
	 * Called when the Activity is restarted, even before it becomes visible.
	 */
	@Override
	public void onStart() {
		mLocationClient.connect();
	}

	/*
	 * Called when the system detects that this Activity is now visible.
	 */
	@Override
	public void onResume() {
		super.onResume();

		// If the app already has a setting for getting location updates, get it
		if (mPrefs.contains(LocationTools.KEY_UPDATES_REQUESTED)) {
			mUpdatesRequested = mPrefs.getBoolean(
					LocationTools.KEY_UPDATES_REQUESTED, false);

			// Otherwise, turn off location updates until requested
		} else {
			mEditor.putBoolean(LocationTools.KEY_UPDATES_REQUESTED, false);
			mEditor.commit();
		}

	}

	/*
	 * Handle results returned to this Activity by other Activities started with
	 * startActivityForResult(). In particular, the method onConnectionFailed()
	 * in LocationUpdateRemover and LocationUpdateRequester may call
	 * startResolutionForResult() to start an Activity that handles Google Play
	 * services problems. The result of this call returns here, to
	 * onActivityResult.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		// Choose what to do based on the request code
		switch (requestCode) {

		// If the request code matches the code sent in onConnectionFailed
		case LocationTools.CONNECTION_FAILURE_RESOLUTION_REQUEST:

			switch (resultCode) {
			// If Google Play services resolved the problem
			case Activity.RESULT_OK:

				// Log the result
				Log.d(LocationTools.APPTAG, getString(R.string.resolved));

				// Display the result
				Toast.makeText(this, R.string.connected, Toast.LENGTH_SHORT)
						.show();
				break;

			// If any other result was returned by Google Play services
			default:
				// Log the result
				Log.d(LocationTools.APPTAG, getString(R.string.no_resolution));

				// Display the result
				// mConnectionState.setText(R.string.disconnected);
				// mConnectionStatus.setText(R.string.no_resolution);
				Toast.makeText(this, R.string.disconnected, Toast.LENGTH_SHORT)
						.show();

				break;
			}

			// If any other request code was received
		default:
			// Report that this Activity received an unknown requestCode
			Log.d(LocationTools.APPTAG,
					getString(R.string.unknown_activity_request_code,
							requestCode));

			break;
		}
	}

	/**
	 * Verify that Google Play services is available before making a request.
	 * 
	 * @return true if Google Play services is available, otherwise false
	 */
	private boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d(LocationTools.APPTAG,
					getString(R.string.play_services_available));

			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Display an error dialog
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
					this, 0);
			if (dialog != null) {
				dialog.show();
			}
			return false;
		}
	}

	/**
	 * Invoked when creation login activity.
	 * 
	 * Calls getLastLocation() to get the current location
	 * 
	 */
	public void getLocation() {

		// If Google Play Services is available
		if (servicesConnected()) {

			// Get the current location
			Location currentLocation = mLocationClient.getLastLocation();

			// Display the current location in hint message

			String current_location = LocationTools.getLatLng(this,
					currentLocation);
			Toast.makeText(this, "Current location " + current_location,
					Toast.LENGTH_LONG).show();

			// Here Update User's Location to Database
			// updatePos()
		}
	}

	/**
	 * Invoked by the "Start Updates" call upon activity creation Sends a
	 * request to start location updates
	 * 
	 */
	public void startUpdates() {
		mUpdatesRequested = true;

		if (servicesConnected()) {
			startPeriodicUpdates();
		}
	}

	/**
	 * Invoked by the "Stop Updates" call upon activity close Sends a request to
	 * remove location updates request them.
	 * 
	 * @param v
	 *            The view object associated with this method, in this case a
	 *            Button (may be).
	 */
	public void stopUpdates(View v) {
		mUpdatesRequested = false;

		if (servicesConnected()) {
			stopPeriodicUpdates();
		}
	}

}
