/**
 * 
 */
package ch.epfl.smartmap.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.Event;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A default implementation of {@link MarkerManager}
 * 
 * @author hugo-S
 */
public class DefaultMarkerManager implements MarkerManager {

    public static final String TAG = "MARKER MANAGER";
    public static final float MARKER_ANCHOR_X = (float) 0.5;
    public static final float MARKER_ANCHOR_Y = 1;
    public static final long HANDLER_DELAY = 16;

    private final GoogleMap mGoogleMap;
    /**
     * A map that contains the displayed markers' ids, associated with the
     * item they represent
     */
    private final Map<String, Displayable> mDisplayedItems;

    /**
     * A map that maps each marker with its id
     */
    private final Map<String, Marker> mDictionnaryMarkers;

    public DefaultMarkerManager(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mDisplayedItems = new HashMap<String, Displayable>();
        mDictionnaryMarkers = new HashMap<String, Marker>();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.map.MarkerManager#addMarker(ch.epfl.smartmap.cache.
     * Displayable, android.content.Context)
     */
    @Override
    public Marker addMarker(Displayable item, Context context) {

        Marker marker =
            mGoogleMap.addMarker(new MarkerOptions().position(item.getLatLng()).title(item.getTitle())
                .icon(item.getMarkerIcon(context)).anchor(MARKER_ANCHOR_X, MARKER_ANCHOR_Y));
        mDisplayedItems.put(marker.getId(), item);
        mDictionnaryMarkers.put(marker.getId(), marker);
        return marker;
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

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.map.MarkerManager#getDisplayedItems()
     */
    @Override
    public List<Displayable> getDisplayedItems() {
        return new ArrayList<Displayable>(mDisplayedItems.values());
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
     * @see
     * ch.epfl.smartmap.map.MarkerManager#getItemForMarker(com.google.android
     * .gms.maps.model.Marker)
     */
    @Override
    public Displayable getItemForMarker(Marker marker) {
        return mDisplayedItems.get(marker.getId());
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.MarkerManager#getMarkerForItem(ch.epfl.smartmap.
     * cache.Displayable)
     */
    @Override
    public Marker getMarkerForItem(Displayable item) {
        for (Entry<String, Displayable> entry : mDisplayedItems.entrySet()) {
            if (entry.getValue().equals(item)) {
                return mDictionnaryMarkers.get(entry.getKey());
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.MarkerManager#isDisplayedItem(ch.epfl.smartmap.cache
     * .Displayable)
     */
    @Override
    public boolean isDisplayedItem(Displayable item) {
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
     * @see
     * ch.epfl.smartmap.map.MarkerManager#removeMarker(ch.epfl.smartmap.cache
     * .Displayable)
     */
    @Override
    public Marker removeMarker(Displayable item) {
        Marker marker = this.getMarkerForItem(item);
        mDisplayedItems.remove(marker.getId());
        mDictionnaryMarkers.remove(marker.getId());
        marker.remove();
        return marker;
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.MarkerManager#updateMarkers(android.content.Context,
     * java.util.List)
     */
    @Override
    public void updateMarkers(Context context, Set<Displayable> itemsToDisplay) {
        Log.d(TAG, "updateMarkers");
        // In the list friendsToDisplay, search if each friend s already
        // displayed
        for (Displayable item : itemsToDisplay) {
            Log.d("Markers", "search for markers");
            if (item instanceof Event) {
                Log.d("Markers", "Display event !");
            }
            Marker marker;
            Log.d("Markers", "search marker for " + item.getTitle());
            Log.d("Markers", "location of " + item.getTitle() + "is " + item.getLocation());
            Log.d("Markers", "latlng of " + item.getTitle() + "is " + item.getLatLng());
            // if the item is already displayed, get the marker for this
            // item, else add a new marker
            if (this.isDisplayedItem(item)) {

                marker = this.getMarkerForItem(item);
            } else {
                marker = this.addMarker(item, context);

            }

            // Log.d("markers", "marker's position for " + item.getTitle() + " is " + marker.getPosition());
            if ((marker.getPosition().latitude != item.getLatLng().latitude)
                || (marker.getPosition().longitude != item.getLatLng().longitude)) {

                this.animateMarker(marker, item.getLatLng(), false, item, context);
            }
            // Log.d("markers", "final position of marker of " + item.getTitle() + " before setIcon is "
            // + marker.getPosition());
            marker.setIcon(item.getMarkerIcon(context));
            // Log.d(
            // "markers",
            // "final position of marker of " + item.getTitle() + " after setIcon is "
            // + marker.getPosition());
        }

        // remove the markers that are not longer in the list to display
        for (Displayable item : this.getDisplayedItems()) {
            if (!itemsToDisplay.contains(item)) {

                this.removeMarker(item);

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
        final Displayable item, final Context context) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mGoogleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = (t * toPosition.longitude) + ((1 - t) * startLatLng.longitude);
                double lat = (t * toPosition.latitude) + ((1 - t) * startLatLng.latitude);
                marker.setPosition(new LatLng(lat, lng));

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
