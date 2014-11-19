/**
 * 
 */
package ch.epfl.smartmap.map;

import java.util.List;

import android.content.Context;
import ch.epfl.smartmap.cache.User;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * An interface that provides a method to set friend's markers on the map
 * 
 * @author hugo-S
 */
public interface FriendMarkerDisplayer {

    /**
     * Set markers on the map for the friends in the list listOfFriends
     * 
     * @param context
     * @param mGoogleMap
     *            the map where we want to add markers
     * @param friendsToDisplay
     *            the friends we want to display
     */
    void setMarkersToMaps(Context context, GoogleMap googleMap,
        List<User> friendsToDisplay);

    /**
     * This method updates the markers on the map with the given list of friends
     * 
     * @param context
     * @param mGoogleMap
     *            the map where we want to update markers
     * @param friendsToDisplay
     *            the updated friends
     */
    void updateMarkers(Context context, GoogleMap googleMap,
        List<User> friendsToDisplay);

    /**
     * @param friend
     * @return true if the friend is displayed
     */
    boolean isDisplayedFriend(User friend);

    /**
     * @param marker
     * @return true if the marker is displayed
     */
    boolean isDisplayedMarker(Marker marker);

    /**
     * @return the list of the markers that are displayed
     */
    List<Marker> getDisplayedMarkers();

    /**
     * @return the list of friends that are displayed
     */
    List<User> getDisplayedFriends();

    /**
     * Add a marker to the map
     * 
     * @param event
     *            the friend for which we want to add a marker
     */
    Marker addMarker(User friend, Context context, GoogleMap googleMap);

    /**
     * Remove a marker from the map
     * 
     * @param event
     *            the friend for which we want to remove a marker
     */
    Marker removeMarker(User friend);

    /**
     * @param marker
     * @return the friend that the marker represents
     */
    User getFriendForMarker(Marker marker);

    /**
     * @param event
     * @return the marker that represents the given friend
     */
    Marker getMarkerForFriend(User friend);

}
