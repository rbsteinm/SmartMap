/**
 *
 */
package ch.epfl.smartmap.map;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import ch.epfl.smartmap.cache.Displayable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A default implementation of {@link MarkerManager}.
 *
 * @author hugo-S
 */
public class DefaultMarkerManager implements MarkerManager {

    public static final String TAG = DefaultMarkerManager.class.getSimpleName();
    public static final float MARKER_ANCHOR_X = (float) 0.5;
    public static final float MARKER_ANCHOR_Y = 1;
    public static final long HANDLER_DELAY = 16;
    public static final long ANIMATE_MARKER_DURATION = 1000;

    public static final int MIN_TIME_BETWEEN_UPDATES = 15000;

    private final GoogleMap mGoogleMap;
    private long lastUpdateInMillis = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"))
        .getTimeInMillis();

    /**
     * A map that contains the displayed markers' ids, associated with the
     * item they represent
     */
    private final Map<String, Displayable> mDisplayedItems;
    /**
     * A map that maps each marker with its id
     */
    private final Map<String, Marker> mDictionnaryMarkers;
    private static final String DISPLAYABLE_ITEM = "Displayable item";
    private static final String CONTEXT_STRING = "context";

    /**
     * Constructor
     *
     * @param googleMap
     */
    public DefaultMarkerManager(GoogleMap googleMap) {
        this.checkNonNull(googleMap, "GoogleMap");
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
        this.checkNonNull(item, DISPLAYABLE_ITEM);
        this.checkNonNull(context, CONTEXT_STRING);
        Marker marker =
            mGoogleMap.addMarker(new MarkerOptions().position(item.getLatLng()).title(item.getTitle())
                .icon(item.getMarkerIcon(context)).anchor(MARKER_ANCHOR_X, MARKER_ANCHOR_Y));
        mDisplayedItems.put(marker.getId(), item);
        mDictionnaryMarkers.put(marker.getId(), marker);
        marker.setSnippet(MarkerColor.ORANGE.toString());
        return marker;
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
        this.checkNonNull(marker, "marker");
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
        this.checkNonNull(item, DISPLAYABLE_ITEM);
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
        this.checkNonNull(item, "Displayable item");
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
        this.checkNonNull(marker, "marker");
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
        this.checkNonNull(item, DISPLAYABLE_ITEM);
        Marker marker = this.getMarkerForItem(item);
        mDisplayedItems.remove(marker.getId());
        mDictionnaryMarkers.remove(marker.getId());
        marker.remove();
        return marker;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.map.MarkerManager#resetMarkersIcon(java.lang.String)
     * The marker attribute snippet is used to store the marker's color. The marker icon will be reset only if
     * it's color was red
     */
    @Override
    public void resetMarkersIcon(Context context) {
        this.checkNonNull(context, CONTEXT_STRING);
        for (Marker marker : this.getDisplayedMarkers()) {
            if (marker.getSnippet().equals(MarkerColor.RED.toString())) {
                marker.setIcon(this.getItemForMarker(marker).getMarkerIcon(context));
                marker.setSnippet(MarkerColor.ORANGE.toString());
            }
        }

    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.MarkerManager#updateMarkers(android.content.Context,
     * java.util.List)
     */
    @Override
    public void updateMarkers(Context context, Set<Displayable> itemsToDisplay) {
        long nowInMillis = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00")).getTimeInMillis();
        if ((nowInMillis - lastUpdateInMillis) < MIN_TIME_BETWEEN_UPDATES) {
            this.checkNonNull(context, CONTEXT_STRING);
            this.checkNonNull(itemsToDisplay, "items to display");
            Log.d(TAG, "updateMarkers");
            // In the list friendsToDisplay, search if each friend s already
            // displayed
            for (Displayable item : itemsToDisplay) {
                Marker marker;
                // if the item is already displayed, get the marker for this
                // item, else add a new marker
                if (this.isDisplayedItem(item)) {
                    marker = this.getMarkerForItem(item);
                } else {
                    marker = this.addMarker(item, context);
                }

                if ((marker.getPosition().latitude != item.getLatLng().latitude)
                    || (marker.getPosition().longitude != item.getLatLng().longitude)) {

                    this.animateMarker(marker, item.getLatLng());
                }

                // set the marker's icon
                marker.setIcon(item.getMarkerIcon(context));

            }

            // remove the markers that are not longer in the list to display
            for (Displayable item : this.getDisplayedItems()) {
                if (!itemsToDisplay.contains(item)) {

                    this.removeMarker(item);

                }
            }

            // Update last time
            lastUpdateInMillis = nowInMillis;
        }
    }

    /**
     * Animate the given marker from it's position to the given one
     *
     * @param marker
     * @param toPosition
     */
    private void animateMarker(final Marker marker, final LatLng toPosition) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mGoogleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = ANIMATE_MARKER_DURATION;

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
                }
            }
        });

    }

    private void checkNonNull(Object object, String name) {
        if (object == null) {
            throw new IllegalArgumentException("Null " + name);
        }
    }

    /**
     * An enum that represents possible markers color for events
     *
     * @author hugo-S
     */
    public enum MarkerColor {
        RED,
        ORANGE
    }
}
