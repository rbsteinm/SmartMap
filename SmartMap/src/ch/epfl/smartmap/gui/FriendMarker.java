/**
 * 
 */
package ch.epfl.smartmap.gui;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.MockPictureProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * @author hugo
 * 
 */
public class FriendMarker {

	public FriendMarker() {
	}

	/*
	 * This function uploads images of users from cache to setup them as marker
	 */
	public void setMarkersToMaps(Context context, GoogleMap mGoogleMap,
			List<Friend> listOfFriends) {

		for (int i = 0; i < listOfFriends.size(); i++) {

			int id = listOfFriends.get(i).getID();
			String name = listOfFriends.get(i).getName();

			Location location = listOfFriends.get(i).getLocation();
			LatLng loc = new LatLng(location.getLatitude(),
					location.getLongitude());
			;
			MockPictureProvider pictureProvider = new MockPictureProvider();

			mGoogleMap.addMarker(new MarkerOptions()
					.position(loc)
					.title(name)
					.icon(BitmapDescriptorFactory.fromBitmap(pictureProvider
							.getImage(id))).anchor((float) 0.5, 1));

		}
	}
}
