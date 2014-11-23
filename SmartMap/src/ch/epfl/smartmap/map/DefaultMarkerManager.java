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
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import ch.epfl.smartmap.cache.Displayable;

import com.google.android.gms.maps.GoogleMap;
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
    private final Map<String, T> mDisplayedItems;

    /**
     * A map that maps each marker with its id
     */
    private final Map<String, Marker> mDictionnaryMarkers;

    public DefaultMarkerManager(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mDisplayedItems = new HashMap<String, T>();
        mDictionnaryMarkers = new HashMap<String, Marker>();
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
            this.animateMarker(marker, item.getLatLng());
        }

        // remove the markers that are not longer in the list to display
        for (T item : this.getDisplayedItems()) {
            if (!itemsToDisplay.contains(item)) {
                // && (!getMarkerForItem(item).isInfoWindowShown())) {
                Marker marker = this.removeMarker(item);
                this.animateMarker(marker, item.getLatLng());
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
        return mDisplayedItems.containsValue(item);
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.MarkerManager#isDisplayedMarker(com.google.android
     * .gms.maps.model.Marker)
     */
    @Override
    public boolean isDisplayedMarker(Marker marker) {
        return mDisplayedItems.containsKey(marker.getId());
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.map.MarkerManager#getDisplayedMarkers()
     */
    @Override
    public List<Marker> getDisplayedMarkers() {
        return new ArrayList<Marker>(mDictionnaryMarkers.values());
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.map.MarkerManager#getDisplayedItems()
     */
    @Override
    public List<T> getDisplayedItems() {
        return new ArrayList<T>(mDisplayedItems.values());
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.map.MarkerManager#addMarker(ch.epfl.smartmap.cache.
     * Displayable, android.content.Context)
     */
    @Override
    public Marker addMarker(T item, Context context) {
        Marker marker = mGoogleMap.addMarker(item.getMarkerOptions(context));
        mDisplayedItems.put(marker.getId(), item);
        mDictionnaryMarkers.put(marker.getId(), marker);
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
        mDisplayedItems.remove(marker.getId());
        mDictionnaryMarkers.remove(marker.getId());
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
        return mDisplayedItems.get(marker.getId());
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.MarkerManager#getMarkerForItem(ch.epfl.smartmap.
     * cache.Displayable)
     */
    @Override
    public Marker getMarkerForItem(T item) {
        for (Entry<String, T> entry : mDisplayedItems.entrySet()) {
            if (entry.getValue().equals(item)) {
                return mDictionnaryMarkers.get(entry.getKey());
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
    private void animateMarker(final Marker marker, final LatLng finalPosition) {
        final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
        final LatLng startPosition = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;

        handler.post(new Runnable() {
            private long elapsed;
            private float t;
            private float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                marker.setPosition(latLngInterpolator.interpolate(v, startPosition, finalPosition));

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, HANDLER_DELAY);
                }
            }
        });
    }

    /**
     * Checks that the Representation Invariant is not violated.
     * 
     * @param depth
     *            represents how deep the audit check is done (use 1 to check
     *            this object only)
     * @return The number of audit errors in this object
     */
    public int auditErrors(int depth) {
        if (depth == 0) {
            return 0;
        }

        int auditErrors = 0;

        if (mGoogleMap == null) {
            auditErrors++;
        }

        if (mDictionnaryMarkers == null) {
            auditErrors++;
        }

        if (mDisplayedItems == null) {
            auditErrors++;
        }

        return auditErrors;
    }

}
