package ch.epfl.smartmap.gui;

import java.util.List;

import android.annotation.SuppressLint;

import android.location.Location;
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
 * 
 */
public class DefaultZoomManager extends FragmentActivity implements ZoomManager {
	public static final String TAG = "ZOOM MANAGER";
	private static final int GMAP_ZOOM_LEVEL = 10;
	private static final int PADDING = 35; // offset from edges of the map in
											// pixels
	private View mapView;

	DefaultZoomManager(SupportMapFragment fm) {

		mapView = fm.getView();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.gui.ZoomManager#zoomOnLocation(android.location.Location
	 * , com.google.android.gms.maps.GoogleMap)
	 */
	@Override
	public void zoomOnLocation(Location location, GoogleMap map) {
		Log.d(TAG, "zoomMap called");
		LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
		// Zoom in the Google Map
		map.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
		map.animateCamera(CameraUpdateFactory.zoomTo(GMAP_ZOOM_LEVEL)); // with
																		// animate

		/*
		 * Clarifications: for GMAP_ZOOM_LEVEL GMAP_ZOOM_LEVEL = 20 represents a
		 * exact address. = 11 represents a town = 6 represents a country We can
		 * make some functionality to define the asked precision.
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.gui.ZoomManager#zoomAccordingToMarkers(com.google.android
	 * .gms.maps.GoogleMap, java.util.List)
	 * 
	 * Almost all movement methods require the Map object to have passed the
	 * layout process. We can wait for this to happen using the
	 * addOnGlobalLayoutListener
	 */
	@Override
	public void zoomAccordingToMarkers(final GoogleMap map, final List<Marker> markers) {

		Log.i(TAG, "after mapview enter to zoom according");
		if (mapView.getViewTreeObserver().isAlive()) {
			mapView.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {
						@SuppressWarnings("deprecation")
						@SuppressLint("NewApi")
						// We check which build version we are using.
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
								mapView.getViewTreeObserver()
										.removeGlobalOnLayoutListener(this);
							} else {
								mapView.getViewTreeObserver()
										.removeOnGlobalLayoutListener(this);
							}

							CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(bounds, PADDING);
							// mGoogleMap.moveCamera(camUpdate);
							map.animateCamera(camUpdate);

						}
					});
		}

	}

}
