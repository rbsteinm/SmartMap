/**
 * 
 */
package ch.epfl.smartmap.gui;

import java.util.List;

import android.content.Context;
import android.location.Location;

import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.MockPictureProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A class that creates friend's marker as their profile picture
 * 
 * @author hugo-S
 * 
 */
public class ProfilePictureFriendMarker implements FriendMarker {

	public static final float MARKER_ANCHOR_X = (float) 0.5;
	public static final float MARKER_ANCHOR_Y = 1;

	public ProfilePictureFriendMarker() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.gui.FriendMarker#setMarkersToMaps(android.content.Context
	 * , com.google.android.gms.maps.GoogleMap, java.util.List)
	 */
	public void setMarkersToMaps(Context context, GoogleMap mGoogleMap,
			List<Friend> listOfFriends) {

		for (int i = 0; i < listOfFriends.size(); i++) {

			long id = listOfFriends.get(i).getID();
			String name = listOfFriends.get(i).getName();

			Location location = listOfFriends.get(i).getLocation();
			LatLng loc = new LatLng(location.getLatitude(),
					location.getLongitude());

			MockPictureProvider pictureProvider = new MockPictureProvider();

			mGoogleMap.addMarker(new MarkerOptions()
					.position(loc)
					.title(name)
					.icon(BitmapDescriptorFactory.fromBitmap(pictureProvider
							.getImage(id)))
					.anchor((float) MARKER_ANCHOR_X, MARKER_ANCHOR_Y));

		}
	}
}
