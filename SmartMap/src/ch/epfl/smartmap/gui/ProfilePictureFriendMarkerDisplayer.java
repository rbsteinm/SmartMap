/**
 * 
 */
package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.User;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A class that creates friend's marker as their profile picture
 * 
 * @author hugo-S
 * 
 */
public class ProfilePictureFriendMarkerDisplayer implements
		FriendMarkerDisplayer {

	public static final float MARKER_ANCHOR_X = (float) 0.5;
	public static final float MARKER_ANCHOR_Y = 1;

	public static final int WIDTH = 50;
	public static final int HEIGHT = 50;

	/**
	 * A map that contains the displayed markers, associated with the friend
	 * they represent
	 */
	private Map<Marker, User> displayedMarkers = new HashMap<Marker, User>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.gui.FriendMarker#setMarkersToMaps(android.content.Context
	 * , com.google.android.gms.maps.GoogleMap, java.util.List)
	 */
	public void setMarkersToMaps(Context context, GoogleMap googleMap,
			List<Friend> listOfFriends) {

		// Add marker with profile picture for each friend
		for (Friend friend : listOfFriends) {
			Bitmap friendProfilePicture = Bitmap.createScaledBitmap(
					friend.getPicture(context), WIDTH, HEIGHT, false);
			Marker marker = googleMap.addMarker(new MarkerOptions()
					.position(friend.getLatLng())
					.title(friend.getName())
					.icon(BitmapDescriptorFactory
							.fromBitmap(friendProfilePicture))
					.anchor((float) MARKER_ANCHOR_X, MARKER_ANCHOR_Y));

			displayedMarkers.put(marker, friend);
		}
	}

	public boolean isDisplayedFriend(User friend) {
		return displayedMarkers.containsValue(friend);
	}

	public boolean isDisplayedMarker(User friend) {
		return displayedMarkers.containsValue(friend);
	}

	public List<Marker> getDisplayedMarkers() {
		return new ArrayList<Marker>(displayedMarkers.keySet());
	}

	public User getFriendForMarker(Marker marker) {
		return displayedMarkers.get(marker);
	}
}
