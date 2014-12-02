package ch.epfl.smartmap.map;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * This interface offers different ways to zomm on the map
 * 
 * @author hugo-S
 */
public interface ZoomManager {

    /**
     * Center on the specified location, without changing the zoom level
     * 
     * @param latLng
     */
    void centerOnLocation(LatLng latLng);

    /**
     * Set bound and zoom with regards to all markers positions on the map
     * 
     * @param markers
     *            the list of markers to take in account
     */
    void zoomAccordingToMarkers(final List<Marker> markers);

    /**
     * Zoom on the specified location, with animation
     * 
     * @param location
     *            the location where we want to zoom
     */
    void zoomWithAnimation(LatLng latlng);

    /**
     * Zoom on the specified location, without changing the zoom level
     * 
     * @param latLng
     */
    void zoomWithoutAnimation(LatLng latLng);

}