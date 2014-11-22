package ch.epfl.smartmap.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import ch.epfl.smartmap.cache.User;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A class that creates friend's marker as their profile picture
 * 
 * @author hugo-S
 */
public class ProfilePictureFriendMarkerDisplayer implements FriendMarkerDisplayer {

    public static final String TAG = "FRIEND MARKERS";

    public static final float MARKER_ANCHOR_X = (float) 0.5;
    public static final float MARKER_ANCHOR_Y = 1;
    public static final int PICTURE_WIDTH = 50;
    public static final int PICTURE_HEIGHT = 50;
    public static final long HANDLER_DELAY = 16;

    /**
     * A map that contains the displayed markers' ids, associated with the
     * friend they represent
     */
    private final Map<String, User> displayedMarkers = new HashMap<String, User>();

    /**
     * A map that maps each marker with its id
     */
    private final Map<String, Marker> dictionnaryMarkers = new HashMap<String, Marker>();

    public ProfilePictureFriendMarkerDisplayer() {

    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.FriendMarkerDisplayer#setMarkersToMaps(android.content
     * .Context, com.google.android.gms.maps.GoogleMap, java.util.List)
     */
    @Override
    public void setMarkersToMaps(Context context, GoogleMap googleMap, List<User> friendsToDisplay) {
        Log.d(TAG, "set markers to map");
        // Add marker with profile picture for each friend
        for (User friend : friendsToDisplay) {
            addMarker(friend, context, googleMap);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.gui.FriendMarkerDisplayer#isDisplayedFriend(ch.epfl.
     * smartmap.cache.User)
     */
    @Override
    public boolean isDisplayedFriend(User friend) {

        return displayedMarkers.containsValue(friend);

    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.gui.FriendMarkerDisplayer#isDisplayedMarker(com.google
     * .android.gms.maps.model.Marker)
     */
    @Override
    public boolean isDisplayedMarker(Marker marker) {
        return displayedMarkers.containsKey(marker.getId());
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.gui.FriendMarkerDisplayer#getDisplayedMarkers()
     */
    @Override
    public List<Marker> getDisplayedMarkers() {
        return new ArrayList<Marker>(dictionnaryMarkers.values());
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.gui.FriendMarkerDisplayer#getFriendForMarker(com.google
     * .android.gms.maps.model.Marker)
     */
    @Override
    public User getFriendForMarker(Marker marker) {
        return displayedMarkers.get(marker.getId());
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.gui.FriendMarkerDisplayer#addMarker(ch.epfl.smartmap
     * .cache.User, android.content.Context,
     * com.google.android.gms.maps.GoogleMap)
     */
    @Override
    public Marker addMarker(User friend, Context context, GoogleMap googleMap) {
        Log.d(TAG, "add marker for friend " + friend.getName());

        Bitmap friendProfilePicture =
            Bitmap.createScaledBitmap(friend.getPicture(context), PICTURE_WIDTH, PICTURE_HEIGHT, false);
        Marker marker =
            googleMap.addMarker(new MarkerOptions().position(friend.getLatLng()).title(friend.getName())
                .icon(BitmapDescriptorFactory.fromBitmap(friendProfilePicture))
                .anchor(MARKER_ANCHOR_X, MARKER_ANCHOR_Y));

        displayedMarkers.put(marker.getId(), friend);
        dictionnaryMarkers.put(marker.getId(), marker);
        return marker;
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.gui.FriendMarkerDisplayer#getMarkerForFriend(ch.epfl
     * .smartmap.cache.User)
     */
    @Override
    public Marker getMarkerForFriend(User friend) {
        for (Entry<String, User> entry : displayedMarkers.entrySet()) {
            if (entry.getValue().getID() == friend.getID()) {
                return dictionnaryMarkers.get(entry.getKey());
            }
        }
        return null;

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.gui.FriendMarkerDisplayer#getDisplayedFriends()
     */
    @Override
    public List<User> getDisplayedFriends() {

        return new ArrayList<User>(displayedMarkers.values());
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.gui.FriendMarkerDisplayer#removeMarker(ch.epfl.smartmap
     * .cache.User, android.content.Context,
     * com.google.android.gms.maps.GoogleMap)
     */
    @Override
    public Marker removeMarker(User friend) {
        Marker marker = getMarkerForFriend(friend);
        displayedMarkers.remove(marker.getId());
        dictionnaryMarkers.remove(marker.getId());
        marker.remove();
        return marker;

    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.gui.FriendMarkerDisplayer#updateMarkers(android.content
     * .Context, com.google.android.gms.maps.GoogleMap, java.util.List) This
     * method updates the markers on the map with the given list of friends. It
     * uses an auxiliary method animateMarkers to move the marker with an
     * animation instead of changing place roughly
     */
    @Override
    public void updateMarkers(Context context, GoogleMap googleMap, List<User> friendsToDisplay) {
        Log.d(TAG, "in updatemarkers");

        // In the list friendsToDisplay, search if each friend s already
        // displayed
        for (User friend : friendsToDisplay) {
            Log.d(TAG, friend.getName());
            Marker marker;
            // if the friend is already displayed, get the marker for this
            // friend, else add a new marker
            if (isDisplayedFriend(friend)) {
                marker = getMarkerForFriend(friend);
                Log.d(TAG, "found marker for friend " + friend.getName());
            } else {
                marker = addMarker(friend, context, googleMap);
                Log.d(TAG, "friend was not displayed");
            }
            animateMarker(marker, friend.getLatLng(), false, googleMap);
        }

        // remove the markers that are not longer in the list to display
        for (User friend : getDisplayedFriends()) {
            if ((!friendsToDisplay.contains(friend)) && (!getMarkerForFriend(friend).isInfoWindowShown())) {
                Marker marker = removeMarker(friend);
                animateMarker(marker, friend.getLatLng(), true, googleMap);
            }
        }

    }

    /**
     * Animate the given marker from it's position to the given one
     * 
     * @param marker
     * @param toPosition
     * @param hideMarker
     * @param map
     */
    private void animateMarker(final Marker marker, final LatLng toPosition, final boolean hideMarker,
        GoogleMap map) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                // Log.d(TAG, "Set marker position for friend "
                // + getFriendForMarker(marker).getName() + " "
                // + marker.getPosition().toString());

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, HANDLER_DELAY);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
}
