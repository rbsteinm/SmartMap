/**
 * 
 */
package ch.epfl.smartmap.map;

import android.graphics.Bitmap;

/**
 * An interface that provides a method to create a marker icon, from a profile picture
 * 
 * @author hugo-S
 */
public interface MarkerIconMaker {

    Bitmap getMarkerIcon(Bitmap profilePicture);

}
