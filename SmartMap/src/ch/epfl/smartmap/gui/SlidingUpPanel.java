package ch.epfl.smartmap.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import ch.epfl.smartmap.R;

/**
 * Represents a Panel that can can be opened or closed from the bottom of the activity. It contains A search bar
 * and a view that displays the queries sent using the search bar.
 * 
 * @author jfperren
 */
public class SlidingUpPanel extends RelativeLayout {

    private final static String TAG = "SLIDING_UP_PANEL";
    @SuppressWarnings("unused")
    private final static String AUDIT_TAG = "AuditError : " + TAG;

    /**
     * Constructor
     * 
     * @param context The current context
     */
    public SlidingUpPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setVisibility(View.GONE);
    }

    /**
     * Opens the panel and displays it full screen
     */
    public void open() {
        this.setVisibility(View.VISIBLE);
        Animation bottomUp = AnimationUtils.loadAnimation(getContext(),
            R.anim.bottom_up);
        this.startAnimation(bottomUp);
    }

    /**
     * Closes the panel and hides it.
     */
    public void close() {
        Animation topDown = AnimationUtils.loadAnimation(getContext(),
            R.anim.top_down);

        this.startAnimation(topDown);
        this.setVisibility(View.GONE);
        clearFocus();
    }

    /**
     * Checks that the Representation Invariant is not violated.
     * @param depth represents how deep the audit check is done (use 1 to check this object only)
     * @return The number of audit errors in this object
     */
    public int auditErrors(int depth) {
        // TODO : Decomment when auditErrors coded for other classes
        if (depth == 0) {
            return 0;
        }

        int auditErrors = 0;

        return auditErrors;
    }
}
