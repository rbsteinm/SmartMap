package ch.epfl.smartmap.map;

import java.util.List;

import android.content.Context;
import ch.epfl.smartmap.cache.Event;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * An interface that provides a method to display event's markers on the map
 * 
 * @author hugo-S
 */
public interface EventMarkerDisplayer {

    /**
     * Set markers on the map for the events in the list listOfEvents
     * 
     * @param context
     * @param mGoogleMap
     *            the map where we want to add markers
     * @param eventsToDisplay
     *            the events we want to display
     */
    void setMarkersToMaps(Context context, GoogleMap googleMap,
        List<Event> eventsToDisplay);

    /**
     * This method updates the markers on the map with the given list of events
     * 
     * @param context
     * @param mGoogleMap
     *            the map where we want to update markers
     * @param eventsToDisplay
     *            the updated events
     */
    void updateMarkers(Context context, GoogleMap googleMap,
        List<Event> eventsToDisplay);

    /**
     * @param event
     * @return true if the event is displayed
     */
    boolean isDisplayedEvent(Event event);

    /**
     * @param marker
     * @return true if the marker is displayed
     */
    boolean isDisplayedMarker(Marker marker);

    /**
     * @return the list of the markers that are displayed
     */
    List<Marker> getDisplayedMarkers();

    /**
     * @return the list of the events that are displayed
     */
    List<Event> getDisplayedEvents();

    /**
     * Add a marker to the map
     * 
     * @param event
     *            the event for which we want to add a marker
     */
    Marker addMarker(Event event, Context context, GoogleMap googleMap);

    /**
     * Remove a marker from the map
     * 
     * @param event
     *            the event for which we want to remove the marker
     */
    Marker removeMarker(Event event);

    /**
     * @param event
     * @return the marker that represents the given event
     */
    Marker getMarkerForEvent(Event event);

    /**
     * @param marker
     * @return the event that the given marker represents
     */
    Event getEventForMarker(Marker marker);
}
