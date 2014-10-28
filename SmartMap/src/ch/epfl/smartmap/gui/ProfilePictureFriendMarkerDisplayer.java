/**
 * 
 */
package ch.epfl.smartmap.gui;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import ch.epfl.smartmap.cache.Friend;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.smartmap.gui.FriendMarker#setMarkersToMaps(android.content.Context ,
     * com.google.android.gms.maps.GoogleMap, java.util.List)
     */
    public void setMarkersToMaps(Context context, GoogleMap mGoogleMap,
        List<Friend> listOfFriends) {

        // Add marker with profile picture for each friend
        for (Friend friend : listOfFriends) {
            Bitmap friendProfilePicture = Bitmap.createScaledBitmap(
                friend.getPicture(context), WIDTH, HEIGHT, false);
            mGoogleMap.addMarker(new MarkerOptions()
                .position(friend.getLatLng()).title(friend.getName())
                .icon(BitmapDescriptorFactory.fromBitmap(friendProfilePicture))
                .anchor((float) MARKER_ANCHOR_X, MARKER_ANCHOR_Y));
        }
    }
}
