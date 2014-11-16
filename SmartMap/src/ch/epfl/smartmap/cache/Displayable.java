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
    public Bitmap getPicture(Context context);
    public String getTitle();
    public String getInfos();
}
