package ch.epfl.smartmap.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;

import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.User;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A default implementation of {@link EventMarkerDisplayer} that displays the
 * events with the default markers
 * 
 * @author hugo-S
 * 
 */
public class DefaultEventMarkerDisplayer implements EventMarkerDisplayer {

	public static final float MARKER_ANCHOR_X = (float) 0.5;
	public static final float MARKER_ANCHOR_Y = 1;

	public static final int WIDTH = 50;
	public static final int HEIGHT = 50;

	/**
	 * A map that contains the displayed markers, associated with the event they
	 * represent
	 */
	private Map<Marker, Event> displayedMarkers = new HashMap<Marker, Event>();

	public DefaultEventMarkerDisplayer() {

	}

	/*
	 * (non-Javadoc)
	 * 
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
	 * 
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
	 * 
	 * @see
	 * ch.epfl.smartmap.gui.EventMarkerDisplayer#isDisplayedMarker(com.google
	 * .android.gms.maps.model.Marker)
	 */
	@Override
	public boolean isDisplayedMarker(Marker marker) {

		return displayedMarkers.containsKey(marker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.epfl.smartmap.gui.EventMarkerDisplayer#getDisplayedMarkers()
	 */
	@Override
	public List<Marker> getDisplayedMarkers() {
		return new ArrayList<Marker>(displayedMarkers.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.gui.EventMarkerDisplayer#getEventForMarker(com.google
	 * .android.gms.maps.model.Marker)
	 */
	@Override
	public Event getEventForMarker(Marker marker) {
		return displayedMarkers.get(marker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.gui.EventMarkerDisplayer#getMarkerForEvent(ch.epfl.smartmap
	 * .cache.Event)
	 */
	@Override
	public Marker getMarkerForEvent(Event event) {
		for (Entry<Marker, Event> entry : displayedMarkers.entrySet()) {
			if (entry.getValue().getID() == (event.getID())) {
				return entry.getKey();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.epfl.smartmap.gui.EventMarkerDisplayer#getDisplayedEvents()
	 */
	@Override
	public List<Event> getDisplayedEvents() {

		return new ArrayList<Event>(displayedMarkers.values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.gui.EventMarkerDisplayer#addMarker(ch.epfl.smartmap.
	 * cache.Event, android.content.Context,
	 * com.google.android.gms.maps.GoogleMap)
	 */
	@Override
	public void addMarker(Event event, Context context, GoogleMap googleMap) {
		Marker marker = googleMap.addMarker(new MarkerOptions()
				.position(event.getLatLng())
				.title(event.getName())
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
				.anchor((float) MARKER_ANCHOR_X, MARKER_ANCHOR_Y));

		displayedMarkers.put(marker, event);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.gui.EventMarkerDisplayer#removeMarker(ch.epfl.smartmap
	 * .cache.Event, android.content.Context,
	 * com.google.android.gms.maps.GoogleMap)
	 */
	@Override
	public void removeMarker(Event event) {
		Marker marker = getMarkerForEvent(event);
		displayedMarkers.remove(marker);
		marker.remove();

	}

	/*
	 * (non-Javadoc)
	 * 
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

}
