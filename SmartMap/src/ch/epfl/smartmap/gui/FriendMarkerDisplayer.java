/**
 * 
 */
package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.User;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * An interface that provides a method to set friend's markers on the map
 * 
 * @author hugo-S
 * 
 */
public interface FriendMarkerDisplayer {

	/**
	 * Set markers on the map for the friends in the list listOfFriends
	 * 
	 * @param context
	 * @param mGoogleMap
	 *            the map where we want to add markers
	 * @param listOfFriends
	 *            the friends we want to display
	 */
	void setMarkersToMaps(Context context, GoogleMap mGoogleMap,
			List<Friend> listOfFriends);

	/**
	 * 
	 * @param friend
	 * @return true if the friend is displayed
	 */
	public boolean isDisplayedFriend(User friend);

	/**
	 * @param friend
	 * @return true if the marker is displayed
	 */
	public boolean isDisplayedMarker(User friend);

	/**
	 * @return the list of the markers that are displayed
	 */
	public List<Marker> getDisplayedMarkers();

	/**
	 * @param marker
	 * @return the friend that the marker represents
	 */
	public User getFriendForMarker(Marker marker);
}
