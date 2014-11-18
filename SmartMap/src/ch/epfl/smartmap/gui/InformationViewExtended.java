package ch.epfl.smartmap.gui;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.MockDB;

/**
 * @author jfperren
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
