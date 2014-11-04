package ch.epfl.smartmap.gui;

import android.content.Context;
import android.widget.LinearLayout;
import ch.epfl.smartmap.R;

/**
 * This class is a basic Layout that will be used to display search results in {@code SearchLayout}.
 * 
 * @author jfperren
 */
public abstract class SearchResultView extends LinearLayout {

    private static final int PADDING_BOTTOM = 20;
    private static final int PADDING_RIGHT = 20;
    private static final int PADDING_LEFT = 20;
    private static final int PADDING_TOP = 20;

    private static final int SHADOW_WIDTH = 10;
    private static final int BORDER_WIDTH = 1;
    private static final int BOTTOM_BORDER_WIDTH = 4;
    private static final float MAIN_LAYOUT_WEIGHTSUM = 10f;

    private final LinearLayout mMainLayout;
    private final LinearLayout mBorderLayout;

    /** 
     * Constructor 
     * 
     * @param context Context of the Application
     */
    public SearchResultView(Context context) {
        super(context);
        // TODO : Decide what to put here and what needs to be done in subclasses.

        // Creates shadow layout
        this.setBackgroundResource(R.color.searchResultShadow);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this
            .getLayoutParams();
        layoutParams.setMargins(0, 0, 0, SHADOW_WIDTH);
        this.setLayoutParams(layoutParams);

        // Creates border layout
        mBorderLayout = new LinearLayout(context);
        mBorderLayout.setBackgroundResource(R.color.searchResultBorder);
        mBorderLayout.setLayoutParams(new LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mBorderLayout.setPadding(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BOTTOM_BORDER_WIDTH);

        // this.setBackgroundResource(R.drawable.shape);

        this.addView(mBorderLayout);
        // Creates mMainLayout
        mMainLayout = new LinearLayout(context);
        mMainLayout.setOrientation(HORIZONTAL);
        mMainLayout.setWeightSum(MAIN_LAYOUT_WEIGHTSUM);

        // Parameters
        mMainLayout.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT,
            PADDING_BOTTOM);
        mMainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT));
        mMainLayout.setBackgroundResource(R.color.searchResultBackground);

        mBorderLayout.addView(mMainLayout);
    }

    protected LinearLayout getMainLayout() {
        return mMainLayout;
    }

    /** 
     * Called when a change is performed on the item this object displays.
     */
    public abstract void update();
}
