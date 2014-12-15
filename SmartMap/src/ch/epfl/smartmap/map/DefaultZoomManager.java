package ch.epfl.smartmap.map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.util.Utils;

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
    public static final String TAG = DefaultZoomManager.class.getSimpleName();

    private static final int GMAP_ZOOM_LEVEL = 14;
    private static final int PADDING = 35;

    private final View mMapView;
    private final GoogleMap mGoogleMap;

    /**
     * Constructor
     * 
     * @param fm
     *            the map fragment if the map for which we need zooming methods
     */
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

        if (mGoogleMap == null) {
            auditErrors++;
        }
        if (mMapView == null) {
            auditErrors++;
        }

        return auditErrors;
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.ZoomManager#centerOnLocation(com.google.android.
     * gms.maps.model.LatLng)
     */
    @Override
    public void centerOnLocation(LatLng latLng) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
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
        if (!markers.isEmpty() && mMapView.getViewTreeObserver().isAlive()) {
            mMapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnMapGlobalLayoutListener(markers));

        }
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.gui.ZoomManager#zoomOnLocation(android.location.Location
     * , com.google.android.gms.maps.GoogleMap)
     */
    @Override
    public void zoomWithAnimation(LatLng latLng) {

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, GMAP_ZOOM_LEVEL));

    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.ZoomManager#zoomWithoutAnimation(com.google.android
     * .gms.maps.model.LatLng)
     */
    @Override
    public void zoomWithoutAnimation(LatLng latLng) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, GMAP_ZOOM_LEVEL));
    }

    /**
     * @author hugo-S
     */
    private class OnMapGlobalLayoutListener implements OnGlobalLayoutListener {
        List<Marker> mMarkerList;

        public OnMapGlobalLayoutListener(List<Marker> markers) {
            mMarkerList = new ArrayList<Marker>(markers);
        }

        @SuppressWarnings("deprecation")
        @SuppressLint("NewApi")
        @Override
        public void onGlobalLayout() {

            /*
             * @author jfperren
             * Did this quick fix to remove extreme values and have
             * a more accurate zoom
             */
            double average = 0;
            for (Marker marker : mMarkerList) {
                average += Utils.distanceToMe(marker.getPosition());
            }
            average /= mMarkerList.size();
            Set<Marker> importantMarkers = new HashSet<Marker>();
            for (Marker marker : mMarkerList) {
                if (Utils.distanceToMe(marker.getPosition()) < (average * 2)) {
                    importantMarkers.add(marker);
                }
            }

            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            for (Marker marker : importantMarkers) {
                boundsBuilder.include(marker.getPosition());
            }
            // Include own pos in case there is no marker
            boundsBuilder.include(new LatLng(ServiceContainer.getSettingsManager().getLocation()
                .getLatitude(), ServiceContainer.getSettingsManager().getLocation().getLongitude()));
            LatLngBounds bounds = boundsBuilder.build();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                mMapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
                mMapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, PADDING));

        }
    }

}