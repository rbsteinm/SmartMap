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
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import ch.epfl.smartmap.cache.Displayable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * A default implementation of {@link MarkerManager}
 * 
 * @param <T>
 *            the type of the items for which the class displays markers
 * @author hugo-S
 */
public class DefaultMarkerManager<T extends Displayable> implements MarkerManager<T> {

    public static final String TAG = "MARKER MANAGER";

    public static final long HANDLER_DELAY = 16;

    private final GoogleMap mGoogleMap;
    /**
     * A map that contains the displayed markers' ids, associated with the
     * item they represent
     */
    private final Map<String, T> displayedItems;

    /**
     * A map that maps each marker with its id
     */
    private final Map<String, Marker> dictionnaryMarkers;

    public DefaultMarkerManager(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        displayedItems = new HashMap<String, T>();
        dictionnaryMarkers = new HashMap<String, Marker>();
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.MarkerManager#updateMarkers(android.content.Context,
     * java.util.List)
     */
    @Override
    public void updateMarkers(Context context, List<T> itemsToDisplay) {
        // In the list friendsToDisplay, search if each friend s already
        // displayed
        for (T item : itemsToDisplay) {
            Marker marker;
            // if the item is already displayed, get the marker for this
            // item, else add a new marker
            if (this.isDisplayedItem(item)) {
                marker = this.getMarkerForItem(item);
            } else {
                marker = this.addMarker(item, context);
            }
            this.animateMarker(marker, item.getLatLng(), false);
        }

        // remove the markers that are not longer in the list to display
        for (T item : this.getDisplayedItems()) {
            if ((!itemsToDisplay.contains(item))) {
                // && (!getMarkerForItem(item).isInfoWindowShown())) {
                Marker marker = this.removeMarker(item);
                this.animateMarker(marker, item.getLatLng(), true);
            }
        }

    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.MarkerManager#isDisplayedItem(ch.epfl.smartmap.cache
     * .Displayable)
     */
    @Override
    public boolean isDisplayedItem(T item) {
        return displayedItems.containsValue(item);
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.MarkerManager#isDisplayedMarker(com.google.android
     * .gms.maps.model.Marker)
     */
    @Override
    public boolean isDisplayedMarker(Marker marker) {
        return displayedItems.containsKey(marker.getId());
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.map.MarkerManager#getDisplayedMarkers()
     */
    @Override
    public List<Marker> getDisplayedMarkers() {
        return new ArrayList<Marker>(dictionnaryMarkers.values());
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.map.MarkerManager#getDisplayedItems()
     */
    @Override
    public List<T> getDisplayedItems() {
        return new ArrayList<T>(displayedItems.values());
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.map.MarkerManager#addMarker(ch.epfl.smartmap.cache.
     * Displayable, android.content.Context)
     */
    @Override
    public Marker addMarker(T item, Context context) {
        Marker marker = mGoogleMap.addMarker(item.getMarkerOptions(context));
        displayedItems.put(marker.getId(), item);
        dictionnaryMarkers.put(marker.getId(), marker);
        return marker;
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.MarkerManager#removeMarker(ch.epfl.smartmap.cache
     * .Displayable)
     */
    @Override
    public Marker removeMarker(T item) {
        Marker marker = this.getMarkerForItem(item);
        displayedItems.remove(marker.getId());
        dictionnaryMarkers.remove(marker.getId());
        marker.remove();
        return marker;
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.MarkerManager#getItemForMarker(com.google.android
     * .gms.maps.model.Marker)
     */
    @Override
    public T getItemForMarker(Marker marker) {
        return displayedItems.get(marker.getId());
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.MarkerManager#getMarkerForItem(ch.epfl.smartmap.
     * cache.Displayable)
     */
    @Override
    public Marker getMarkerForItem(T item) {
        for (Entry<String, T> entry : displayedItems.entrySet()) {
            if (entry.getValue().equals(item)) {
                return dictionnaryMarkers.get(entry.getKey());
            }
        }
        return null;
    }

    /**
     * Animate the given marker from it's position to the given one
     * 
     * @param marker
     * @param toPosition
     * @param hideMarker
     * @param map
     */
    private void animateMarker(final Marker marker, final LatLng toPosition, final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mGoogleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = (t * toPosition.longitude) + ((1 - t) * startLatLng.longitude);
                double lat = (t * toPosition.latitude) + ((1 - t) * startLatLng.latitude);
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
