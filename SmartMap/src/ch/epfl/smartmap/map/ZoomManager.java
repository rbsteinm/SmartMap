package ch.epfl.smartmap.map;

import java.util.List;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import android.location.Location;

/**
 * This interface offers different ways to zomm on the map
 * 
 * @author hugo-S
 */
public interface ZoomManager {

	/**
	 * Zoom on the specified location
	 * 
	 * @param location
	 *            the location where we want to zoom
	 * @param map
	 *            the map on which we want to zoom
	 */
	void zoomOnLocation(Location location, GoogleMap map);

	/**
	 * Set bound and zoom with regards to all markers positions on the map
	 * 
	 * @param map
	 *            the map on which we want to zoom
	 * @param markers
	 *            the list of markers to take in account
	 */
	void zoomAccordingToMarkers(GoogleMap map, final List<Marker> markers);

}
