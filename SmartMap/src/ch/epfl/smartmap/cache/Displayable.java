/**
 * 
 */
package ch.epfl.smartmap.cache;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * @author jfperren
 *
 */
public interface Displayable {
    /**
     * Image to be displayed
     * 
     * @param context Current Context
     * @return Bitmap Image
     */
    Bitmap getPicture(Context context);
    /**
     * @return Title to be displayed
     */
    String getTitle();
    /**
     * @return Short Infos to be displayed
     */
    String getShortInfos();
}
