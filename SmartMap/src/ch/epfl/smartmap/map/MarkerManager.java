/**
 * 
 */
package ch.epfl.smartmap.map;

import java.util.List;

import android.content.Context;
import ch.epfl.smartmap.cache.Displayable;

import com.google.android.gms.maps.model.Marker;

/**
 * A generic manager for markers, that keeps track of the displayed markers
 * 
 * @param <T>
 *            the type of the items for which the manager displays markers
 * @author hugo-S
 */
public interface MarkerManager<T extends Displayable> {

	/**
	 * Add a marker to the map
	 * 
	 * @param event
	 *            the item for which we want to add a marker
	 */
	Marker addMarker(T item, Context context);

	/**
	 * @return the list of items that are displayed
	 */
	List<T> getDisplayedItems();

	/**
	 * @return the list of the markers that are displayed
	 */
	List<Marker> getDisplayedMarkers();

	/**
	 * @param marker
	 * @return the item that the marker represents
	 */
	T getItemForMarker(Marker marker);

	/**
	 * @param event
	 * @return the marker that represents the given item
	 */
	Marker getMarkerForItem(T item);

	/**
	 * @param item
	 * @return true if the item is displayed
	 */
	boolean isDisplayedItem(T item);

	/**
	 * @param marker
	 * @return true if the marker is displayed
	 */
	boolean isDisplayedMarker(Marker marker);

	/**
	 * Remove a marker from the map
	 * 
	 * @param event
	 *            the item for which we want to remove a marker
	 */
	Marker removeMarker(T item);

	/**
	 * This method updates the markers on the map with the given list of items
	 * 
	 * @param context
	 * @param mGoogleMap
	 *            the map where we want to update markers
	 * @param friendsToDisplay
	 *            the updated friends
	 */
	void updateMarkers(Context context, List<T> itemsToDisplay);

}
