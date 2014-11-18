package ch.epfl.smartmap.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import ch.epfl.smartmap.cache.Event;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A default implementation of {@link EventMarkerDisplayer} that displays the
 * events with the default markers
 * 
 * @author hugo-S
 */
public class DefaultEventMarkerDisplayer implements EventMarkerDisplayer {

	public static final float MARKER_ANCHOR_X = (float) 0.5;
	public static final float MARKER_ANCHOR_Y = 1;

	public static final int WIDTH = 50;
	public static final int HEIGHT = 50;

	/**
	 * A map that contains the displayed markers' ids, associated with the event
	 * they
	 * represent
	 */
	private final Map<String, Event> displayedMarkers = new HashMap<String, Event>();

	/**
	 * A map that maps each marker with its id
	 */
	private final Map<String, Marker> dictionnaryMarkers = new HashMap<String, Marker>();

	public DefaultEventMarkerDisplayer() {

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * ch.epfl.smartmap.gui.EventMarkerDisplayer#setMarkersToMaps(android.content
	 * .Context, com.google.android.gms.maps.GoogleMap, java.util.List)
	 */
	@Override
	public void setMarkersToMaps(Context context, GoogleMap googleMap,
	    List<Event> eventsToDisplay) {

		for (Event event : eventsToDisplay) {

			addMarker(event, context, googleMap);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * ch.epfl.smartmap.gui.EventMarkerDisplayer#isDisplayedEvent(ch.epfl.smartmap
	 * .cache.Event)
	 */
	@Override
	public boolean isDisplayedEvent(Event event) {

		return displayedMarkers.containsValue(event);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * ch.epfl.smartmap.gui.EventMarkerDisplayer#isDisplayedMarker(com.google
	 * .android.gms.maps.model.Marker)
	 */
	@Override
	public boolean isDisplayedMarker(Marker marker) {

		return displayedMarkers.containsKey(marker.getId());
	}

	/*
	 * (non-Javadoc)
	 * @see ch.epfl.smartmap.gui.EventMarkerDisplayer#getDisplayedMarkers()
	 */
	@Override
	public List<Marker> getDisplayedMarkers() {
		return new ArrayList<Marker>(dictionnaryMarkers.values());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * ch.epfl.smartmap.gui.EventMarkerDisplayer#getEventForMarker(com.google
	 * .android.gms.maps.model.Marker)
	 */
	@Override
	public Event getEventForMarker(Marker marker) {
		return displayedMarkers.get(marker.getId());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * ch.epfl.smartmap.gui.EventMarkerDisplayer#getMarkerForEvent(ch.epfl.smartmap
	 * .cache.Event)
	 */
	@Override
	public Marker getMarkerForEvent(Event event) {
		for (Entry<String, Event> entry : displayedMarkers.entrySet()) {
			if (entry.getValue().getID() == (event.getID())) {
				return dictionnaryMarkers.get(entry.getKey());
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.epfl.smartmap.gui.EventMarkerDisplayer#getDisplayedEvents()
	 */
	@Override
	public List<Event> getDisplayedEvents() {

		return new ArrayList<Event>(displayedMarkers.values());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * ch.epfl.smartmap.gui.EventMarkerDisplayer#addMarker(ch.epfl.smartmap.
	 * cache.Event, android.content.Context,
	 * com.google.android.gms.maps.GoogleMap)
	 */
	@Override
	public Marker addMarker(Event event, Context context, GoogleMap googleMap) {
		Marker marker = googleMap.addMarker(new MarkerOptions()
		    .position(event.getLatLng())
		    .title(event.getName())
		    .icon(
		        BitmapDescriptorFactory
		            .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
		    .anchor(MARKER_ANCHOR_X, MARKER_ANCHOR_Y));

		displayedMarkers.put(marker.getId(), event);
		dictionnaryMarkers.put(marker.getId(), marker);
		return marker;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * ch.epfl.smartmap.gui.EventMarkerDisplayer#removeMarker(ch.epfl.smartmap
	 * .cache.Event, android.content.Context,
	 * com.google.android.gms.maps.GoogleMap)
	 */
	@Override
	public Marker removeMarker(Event event) {
		Marker marker = getMarkerForEvent(event);
		displayedMarkers.remove(marker);
		marker.remove();

		return marker;

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * ch.epfl.smartmap.gui.EventMarkerDisplayer#updateMarkers(android.content
	 * .Context, com.google.android.gms.maps.GoogleMap, java.util.List)
	 */
	@Override
	public void updateMarkers(Context context, GoogleMap googleMap,
	    List<Event> eventsToDisplay) {

		for (Event event : eventsToDisplay) {
			if (isDisplayedEvent(event)) {
				getMarkerForEvent(event).setPosition(event.getLatLng());
			} else {
				addMarker(event, context, googleMap);
			}
		}

		for (Event event : getDisplayedEvents()) {
			if ((!eventsToDisplay.contains(event))
			    && (!getMarkerForEvent(event).isInfoWindowShown())) {
				removeMarker(event);
			}
		}

	}
}
