package ch.epfl.smartmap.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

/**
 * Objects that can be displayed in the ActionBar with image, title and subtitle, and eventually displayed on
 * the map
 * 
 * @author jfperren
 * @author ritterni
 */
public interface Displayable extends Stockable {

    // Default values
    String NO_TITLE = "";
    String NO_SUBTITLE = "";

    String PROVIDER_NAME = "SmartMapServers";
    double NO_LATITUDE = 0.0;
    double NO_LONGITUDE = 0.0;
    Location NO_LOCATION = new Location(PROVIDER_NAME);
    String NO_LOCATION_STRING = "Unknown Location";

    /**
     * @return the Bitmap image that will be displayed in the Action Bar
     */
    Bitmap getActionImage();

    /**
     * @return a LatLng containing the latitude and longitude of the displayable object
     */
    LatLng getLatLng();

    /**
     * @return the GoogleMap Location of the Displayable
     */
    Location getLocation();

    /**
     * @return a String containing infos about the location of the displayable object
     */
    String getLocationString();

    /**
     * @param context
     *            Context in which it will be displayed
     * @return a Descriptor containing informations about how to display the marker of the displayable object
     */
    BitmapDescriptor getMarkerIcon(Context context);

    /**
     * @return the Bitmap image that will be displayed in Search Layout
     */
    Bitmap getSearchImage();

    /**
     * @return a String that will be displayed as subtitle for this object in ActionBar
     */
    String getSubtitle();

    /**
     * @return a String that will be displayed as title for this object in ActionBar
     */
    String getTitle();
}
