/**
 * 
 */
package ch.epfl.smartmap.gui;

import java.util.List;

import android.content.Context;
import ch.epfl.smartmap.cache.Friend;

import com.google.android.gms.maps.GoogleMap;

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
}
