package ch.epfl.smartmap.map;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
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
    private static final int GMAP_ZOOM_LEVEL = 14;
    private static final int PADDING = 35; // offset from edges of the map in
                                           // pixels
    private final View mMapView;
    private final GoogleMap mGoogleMap;

    public DefaultZoomManager(SupportMapFragment fm) {
        mMapView = fm.getView();
        mGoogleMap = fm.getMap();
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

        // TODO

        return auditErrors;
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

            if (mMapView.getViewTreeObserver().isAlive()) {
                mMapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation")
                    @SuppressLint("NewApi")
                    @Override
                    public void onGlobalLayout() {

                        // LatLng centre = new LatLng(CENTER_LATTITUDE,
                        // CENTER_LONGITUDE);
                        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                        for (Marker marker : markers) {
                            boundsBuilder.include(marker.getPosition());
                        }
                        LatLngBounds bounds = boundsBuilder.build();
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mMapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mMapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }

                        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(bounds, PADDING);
                        // mGoogleMap.moveCamera(camUpdate);
                        mGoogleMap.animateCamera(camUpdate);

                    }
                });
            }

        }
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.gui.ZoomManager#zoomOnLocation(android.location.Location
     * , com.google.android.gms.maps.GoogleMap)
     */
    @Override
    public void zoomOnLocation(LatLng latLng) {

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, GMAP_ZOOM_LEVEL));
        /*
         * Clarifications: for GMAP_ZOOM_LEVEL GMAP_ZOOM_LEVEL = 20 represents a
         * exact address. = 11 represents a town = 6 represents a country We can
         * make some functionality to define the asked precision.
         */
    }
}