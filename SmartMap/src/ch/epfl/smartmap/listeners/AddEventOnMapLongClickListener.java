package ch.epfl.smartmap.listeners;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import ch.epfl.smartmap.activities.AddEventActivity;

import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.LatLng;

/**
 * Listener that loads AddEventActivity with the location, city name and country
 * name when the map is long clicked.
 * 
 * @author SpicyCH
 */
public class AddEventOnMapLongClickListener implements OnMapLongClickListener {

    private static final String TAG = AddEventOnMapLongClickListener.class.getSimpleName();

    private final Activity mActivity;

    public AddEventOnMapLongClickListener(Activity activity) {
        super();
        mActivity = activity;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Intent result = new Intent(mActivity, AddEventActivity.class);
        Log.d(TAG, "latLng : " + latLng);
        result.putExtra(AddEventActivity.LOCATION_EXTRA, latLng);

        if (mActivity.getIntent().getBooleanExtra("pickLocationForEvent", false)) {
            // Return the result to the calling activity (AddEventActivity)
            mActivity.setResult(Activity.RESULT_OK, result);
        } else {
            // The user was in MainActivity and long clicked to create an event
            mActivity.startActivity(result);
        }

    }
}
