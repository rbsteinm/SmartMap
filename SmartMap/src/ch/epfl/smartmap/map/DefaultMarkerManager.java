/**
 * 
 */
package ch.epfl.smartmap.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.User;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * @author hugo
 */
public class DefaultMarkerManager<T extends Displayable> implements MarkerManager {

    public static final String TAG = "MARKER MANAGER";

    public static final float MARKER_ANCHOR_X = (float) 0.5;
    public static final float MARKER_ANCHOR_Y = 1;
    public static final int PICTURE_WIDTH = 50;
    public static final int PICTURE_HEIGHT = 50;
    public static final long HANDLER_DELAY = 16;

    private final GoogleMap mGoogleMap;
    /**
     * A map that contains the displayed markers' ids, associated with the
     * item they represent
     */
    private final Map<String, Displayable> displayedItems;

    /**
     * A map that maps each marker with its id
     */
    private final Map<String, Marker> dictionnaryMarkers;

    public DefaultMarkerManager(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        displayedItems = new HashMap<String, Displayable>();
        dictionnaryMarkers = new HashMap<String, Marker>();
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.MarkerManager#updateMarkers(android.content.Context,
     * java.util.List)
     */
    @Override
    public void updateMarkers(Context context, List itemsToDisplay) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.MarkerManager#isDisplayedItem(ch.epfl.smartmap.cache
     * .Displayable)
     */
    @Override
    public boolean isDisplayedItem(Displayable Item) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.MarkerManager#isDisplayedMarker(com.google.android
     * .gms.maps.model.Marker)
     */
    @Override
    public boolean isDisplayedMarker(Marker marker) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.map.MarkerManager#getDisplayedMarkers()
     */
    @Override
    public List getDisplayedMarkers() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.map.MarkerManager#getDisplayedItems()
     */
    @Override
    public List getDisplayedItems() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.map.MarkerManager#addMarker(ch.epfl.smartmap.cache.
     * Displayable, android.content.Context)
     */
    @Override
    public Marker addMarker(Displayable item, Context context) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.MarkerManager#removeMarker(ch.epfl.smartmap.cache
     * .Displayable)
     */
    @Override
    public Marker removeMarker(Displayable item) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.MarkerManager#getItemForMarker(com.google.android
     * .gms.maps.model.Marker)
     */
    @Override
    public User getItemForMarker(Marker marker) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.map.MarkerManager#getMarkerForItem(ch.epfl.smartmap.
     * cache.Displayable)
     */
    @Override
    public Marker getMarkerForItem(Displayable item) {
        // TODO Auto-generated method stub
        return null;
    }

}
