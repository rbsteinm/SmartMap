/**
 * 
 */
package ch.epfl.smartmap.map;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * An interface that provides a method to create a marker icon
 * 
 * @author hugo-S
 */
public interface MarkerIconMaker {

    /**
     * Returns the created marker icon
     * 
     * @param context
     * @return the created marker icon
     */
    Bitmap getMarkerIcon(Context context);

}