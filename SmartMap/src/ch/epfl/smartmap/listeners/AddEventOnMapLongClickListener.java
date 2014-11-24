package ch.epfl.smartmap.listeners;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import ch.epfl.smartmap.activities.AddEventActivity;

import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.LatLng;

/**
 * Listener that add an event to the map when long click
 * 
 * @author SpicyCH
 */
public class AddEventOnMapLongClickListener implements OnMapLongClickListener {

    private static final String CITY_NAME = "CITY_NAME";

    private Activity mActivity;

    public AddEventOnMapLongClickListener(Activity activity) {
        super();
        mActivity = activity;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Intent result = new Intent(mActivity, AddEventActivity.class);
        Bundle extras = new Bundle();
        Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
        String cityName = "";
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                // Makes sure that an address is associated to the coordinates, the user could have long
                // clicked in the middle of the sea after all :)
                cityName = addresses.get(0).getLocality();
            }
        } catch (IOException e) {
        }
        if (cityName == null) {
            // If google couldn't retrieve the city name, we use the
            // country name instead
            try {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addresses.size() > 0) {
                    cityName = addresses.get(0).getCountryName();
                }
            } catch (IOException e) {
            }
        }
        extras.putString(CITY_NAME, cityName);
        extras.putParcelable(Activity.LOCATION_SERVICE, latLng);
        result.putExtras(extras);
        if (mActivity.getIntent().getBooleanExtra("pickLocationForEvent", false)) {
            // Return the result to the calling activity (AddEventActivity)
            mActivity.setResult(Activity.RESULT_OK, result);
        } else {
            // The user was in MainActivity and long clicked to create an event
            mActivity.startActivity(result);
        }
    }

}
