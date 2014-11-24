package ch.epfl.smartmap.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Objects that can be displayed on the bottom menu, and as a marker on the map
 * 
 * @author ritterni
 */
public interface Displayable {

    /**
     * @return The user's ID
     */
    long getID();

    /**
     * @return the position of the displayable, encapsulated in a LatLng object
     */
    LatLng getLatLng();

    /**
     * @return GoogleMap Location of the Displayable
     */
    Location getLocation();

    /**
     * @param context
     *            , the application's context
     * @return the options to display the marker
     */
    MarkerOptions getMarkerOptions(Context context);

    /**
     * @return A name for the panel (e.g. the username, event name, etc.)
     */
    String getName();

    /**
     * @param context
     *            The application's context, needed to access the memory
     * @return The object's picture
     */
    Bitmap getPicture(Context context);

    /**
     * @return Text containing various information (description, last seen,
     *         etc.)
     */
    String getShortInfos();
}
