/**
 * 
 */
package ch.epfl.smartmap.gui;

<<<<<<< HEAD
import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.epfl.smartmap.R;
=======
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.MainActivity;
>>>>>>> eeb2cf2d181fcb7ea3eed01dc505c27293ab6079
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.MockDB;

/**
 * @author jfperren
 *
 */
public class InformationViewExtended extends LinearLayout {

    @SuppressWarnings("unused")
    private static final String TAG = "INFORMATION VIEW EXTENDED";

    public InformationViewExtended(Context context, Displayable item,
        SlidingPanel panel) {
        super(context);
        // Layout Settings
        this.setOrientation(HORIZONTAL);
        this.setLayoutParams(new LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.setBackgroundResource(R.color.light_blue);
        this.addView(new TextView(context));
    }

    /**
     * @param context
     */
    public InformationViewExtended(Context context, SlidingPanel panel) {
        this(context, MockDB.ALAIN, panel);
    }
}
