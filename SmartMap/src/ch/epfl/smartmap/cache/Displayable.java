package ch.epfl.smartmap.cache;

import android.content.Context;
import android.graphics.Bitmap;
import ch.epfl.smartmap.listeners.OnDisplayableUpdateListener;

/**
 * Objects that can be displayed on the bottom menu, and as a marker on the map
 * 
 * @author ritterni
 */
public interface Displayable extends Stockable {

    // final Bitmap NO_IMAGE = Bitmap.createBitmap(R.drawable.default_event);
    final String NO_TITLE = "";
    final String NO_SUBTITLE = "";
    final Bitmap NO_IMAGE = null;

    void addOnDisplayableUpdateListener(OnDisplayableUpdateListener listener);

    /**
     * @param context
     *            The application's context, needed to access the memory
     * @return The object's picture
     */
    Bitmap getImage(Context context);

    /**
     * @return Text containing various information (description, last seen,
     *         etc.)
     */
    String getSubtitle();

    /**
     * @return A name for the panel (e.g. the username, event name, etc.)
     */
    String getTitle();

    void removeOnDisplayableUpdateListener(OnDisplayableUpdateListener listener);
}
