/**
 * 
 */
package ch.epfl.smartmap.gui;

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
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.MockDB;

/**
 * @author jfperren
 *
 */
public class InformationViewExtended extends LinearLayout {
    
    private static final String TAG = "INFORMATION VIEW EXTENDED";
    
    public InformationViewExtended(Context context, Displayable item,
        InformationPanel panel) {
        super(context);
        // Layout Settings
        this.setOrientation(HORIZONTAL);
        this.setLayoutParams(new LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.setBackgroundResource(R.color.searchResultBackground);
    }

    /**
     * @param context
     */
    public InformationViewExtended(Context context, InformationPanel panel) {
        this(context, MockDB.ALAIN, panel);
    }
}
