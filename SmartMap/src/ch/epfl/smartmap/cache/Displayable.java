package ch.epfl.smartmap.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import ch.epfl.smartmap.listeners.OnDisplayableUpdateListener;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Objects that can be displayed on the bottom menu, and as a marker on the map
 * 
 * @author ritterni
 */
public interface Displayable {

    int MIN_LATITUDE = -90;
    int MAX_LATITUDE = 90;
    int MIN_LONGITUDE = -180;
    int MAX_LONGITUDE = 180;

    int NO_LONGITUDE = 0;
    int NO_LATITUDE = 0;

    void addOnDisplayableUpdateListener(OnDisplayableUpdateListener listener);

    /**
     * @return The user's ID
     */
    long getID();

    /**
     * @param context
     *            The application's context, needed to access the memory
     * @return The object's picture
     */
    Bitmap getImage(Context context);

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
     * @return Text containing various information (description, last seen,
     *         etc.)
     */
    String getShortInfos();

    void removeOnDisplayableUpdateListener(OnDisplayableUpdateListener listener);
}
