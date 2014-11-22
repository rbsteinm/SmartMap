package ch.epfl.smartmap.cache;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Objects that can be displayed on the bottom menu
 * 
 * @author ritterni
 */
public interface Displayable {

    /**
     * @param context
     *            The application's context, needed to access the memory
     * @return The object's picture
     */
    Bitmap getPicture(Context context);

    /**
     * @return A name for the panel (e.g. the username, event name, etc.)
     */
    String getName();

    /**
     * @return Text containing various information (description, last seen,
     *         etc.)
     */
    String getShortInfos();

    /**
     * @param context
     *            , the application's context
     * @return the options to display the marker
     */
    MarkerOptions getMarkerOptions(Context context);

    /**
     * @return the position of the item, encapsulated in LatLng object
     */
    LatLng getLatLng();
}
