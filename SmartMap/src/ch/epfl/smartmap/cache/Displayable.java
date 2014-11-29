package ch.epfl.smartmap.cache;

import android.graphics.Bitmap;
import ch.epfl.smartmap.listeners.DisplayableListener;

/**
 * Objects that can be displayed with image, title and subtitle.
 * 
 * @author ritterni
 * @author jfperren
 */
public interface Displayable extends Stockable {

    // final Bitmap NO_IMAGE = Bitmap.createBitmap(R.drawable.default_event);
    String NO_TITLE = "";
    String NO_SUBTITLE = "";
    Bitmap DEFAULT_IMAGE = null;

    void addDisplayableListener(DisplayableListener newListener);

    /**
     * @param context
     *            The application's context, needed to access the memory
     * @return The object's picture
     */
    Bitmap getImage();

    /**
     * @return Text containing various information (description, last seen,
     *         etc.)
     */
    String getSubtitle();

    /**
     * @return A name for the panel (e.g. the username, event name, etc.)
     */
    String getTitle();

    void removeDisplayableListener(DisplayableListener oldListener);
}
