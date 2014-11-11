/**
 * 
 */
package ch.epfl.smartmap.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
public class ProfilePictureFriendMarkerDisplayer implements FriendMarkerDisplayer {

	public static final float MARKER_ANCHOR_X = (float) 0.5;
	public static final float MARKER_ANCHOR_Y = 1;

	public static final int WIDTH = 50;
	public static final int HEIGHT = 50;

	/**
	 * A map that contains the displayed markers, associated with the friend
	 * they represent
	 */
	private Map<Marker, User> displayedMarkers = new HashMap<Marker, User>();

	public ProfilePictureFriendMarkerDisplayer() {

	}

	@Override
	public void setMarkersToMaps(Context context, GoogleMap googleMap, List<Friend> listOfFriends) {

		// Add marker with profile picture for each friend
		for (User friend : listOfFriends) {
			Bitmap friendProfilePicture = Bitmap.createScaledBitmap(friend.getPicture(context), WIDTH, HEIGHT, false);
			Marker marker = googleMap.addMarker(new MarkerOptions()
							.position(friend.getLatLng())
							.title(friend.getName())
							.icon(BitmapDescriptorFactory
							.fromBitmap(friendProfilePicture))
							.anchor((float) MARKER_ANCHOR_X, MARKER_ANCHOR_Y));

			displayedMarkers.put(marker, friend);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.gui.FriendMarkerDisplayer#isDisplayedFriend(ch.epfl.
	 * smartmap.cache.User)
	 */
	public boolean isDisplayedFriend(User friend) {
		return displayedMarkers.containsValue(friend);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.gui.FriendMarkerDisplayer#isDisplayedMarker(com.google
	 * .android.gms.maps.model.Marker)
	 */
	public boolean isDisplayedMarker(Marker marker) {
		return displayedMarkers.containsKey(marker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.epfl.smartmap.gui.FriendMarkerDisplayer#getDisplayedMarkers()
	 */
	public List<Marker> getDisplayedMarkers() {
		return new ArrayList<Marker>(displayedMarkers.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.gui.FriendMarkerDisplayer#getFriendForMarker(com.google
	 * .android.gms.maps.model.Marker)
	 */
	public User getFriendForMarker(Marker marker) {
		return displayedMarkers.get(marker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.gui.FriendMarkerDisplayer#addMarker(ch.epfl.smartmap
	 * .cache.User, android.content.Context,
	 * com.google.android.gms.maps.GoogleMap)
	 */
	@Override
	public void addMarker(User friend, Context context, GoogleMap googleMap) {
		Bitmap friendProfilePicture = Bitmap.createScaledBitmap(friend.getPicture(context), WIDTH, HEIGHT, false);
		Marker marker = googleMap.addMarker(new MarkerOptions()
						.position(friend.getLatLng()).title(friend.getName())
						.icon(BitmapDescriptorFactory.fromBitmap(friendProfilePicture))
						.anchor((float) MARKER_ANCHOR_X, MARKER_ANCHOR_Y));

		displayedMarkers.put(marker, friend);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.gui.FriendMarkerDisplayer#getMarkerForFriend(ch.epfl
	 * .smartmap.cache.User)
	 */
	@Override
	public Marker getMarkerForFriend(User friend) {
		for (Entry<Marker, User> entry : displayedMarkers.entrySet()) {
			if (entry.getValue().getID() == (friend.getID())) {
				return entry.getKey();
			}
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.epfl.smartmap.gui.FriendMarkerDisplayer#getDisplayedFriends()
	 */
	@Override
	public List<User> getDisplayedFriends() {
		return new ArrayList<User>(displayedMarkers.values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.gui.FriendMarkerDisplayer#removeMarker(ch.epfl.smartmap
	 * .cache.User, android.content.Context,
	 * com.google.android.gms.maps.GoogleMap)
	 */
	@Override
	public void removeMarker(User friend) {
		Marker marker = getMarkerForFriend(friend);
		displayedMarkers.remove(marker);
		marker.remove();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.gui.FriendMarkerDisplayer#updateMarkers(android.content
	 * .Context, com.google.android.gms.maps.GoogleMap, java.util.List)
	 */
	@Override
	public void updateMarkers(Context context, GoogleMap mGoogleMap, List<Friend> listOfFriends) {
		// TODO Auto-generated method stub

	}
}
