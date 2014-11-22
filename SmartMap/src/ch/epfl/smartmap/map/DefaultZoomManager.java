package ch.epfl.smartmap.map;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

/**
 * A default implementation of ZoomManager
 * 
 * @author hugo-S
 */
public class DefaultZoomManager extends FragmentActivity implements ZoomManager {
    public static final String TAG = "ZOOM MANAGER";
    private static final int GMAP_ZOOM_LEVEL = 17;
    private static final int PADDING = 35; // offset from edges of the map in
                                           // pixels
    private final View mapView;
    private final GoogleMap mGoogleMap;

    public DefaultZoomManager(SupportMapFragment fm) {
        mapView = fm.getView();
        mGoogleMap = fm.getMap();
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.gui.ZoomManager#zoomOnLocation(android.location.Location
     * , com.google.android.gms.maps.GoogleMap)
     */
    @Override
    public void zoomOnLocation(LatLng latLng) {
        Log.d(TAG, "zoomMap called");
        LatLng latLng1 = new LatLng(latLng.latitude, latLng.longitude);
        // Zoom in the Google Map
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(GMAP_ZOOM_LEVEL)); // with
        // animate

        /*
         * Clarifications: for GMAP_ZOOM_LEVEL GMAP_ZOOM_LEVEL = 20 represents a
         * exact address. = 11 represents a town = 6 represents a country We can
         * make some functionality to define the asked precision.
         */
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.gui.ZoomManager#zoomAccordingToMarkers(com.google.android
     * .gms.maps.GoogleMap, java.util.List)
     * Almost all movement methods require the Map object to have passed the
     * layout process. We can wait for this to happen using the
     * addOnGlobalLayoutListener
     */
    @Override
    public void zoomAccordingToMarkers(final List<Marker> markers) {
        if (!markers.isEmpty()) {
            Log.i(TAG, "after mapview enter to zoom according");
            if (mapView.getViewTreeObserver().isAlive()) {
                mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation")
                    @SuppressLint("NewApi")
                    @Override
                    public void onGlobalLayout() {
                        Log.d(TAG, "enter to zoom according on glpbal");
                        // LatLng centre = new LatLng(CENTER_LATTITUDE,
                        // CENTER_LONGITUDE);
                        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                        for (Marker marker : markers) {
                            boundsBuilder.include(marker.getPosition());
                        }
                        LatLngBounds bounds = boundsBuilder.build();
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }

                        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(bounds, PADDING);
                        // mGoogleMap.moveCamera(camUpdate);
                        mGoogleMap.animateCamera(camUpdate);

                    }
                });
            }

        }
    }
}