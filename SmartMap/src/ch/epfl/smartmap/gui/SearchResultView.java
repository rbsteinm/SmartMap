/**
 * 
 */
package ch.epfl.smartmap.gui;

import android.content.Context;
import android.widget.LinearLayout;
import ch.epfl.smartmap.R;

/**
 * @author jfperren
 *
 */
public abstract class SearchResultView extends LinearLayout {

    public static final int PADDING_BOTTOM = 20;
    public static final int PADDING_RIGHT = 20;
    public static final int PADDING_LEFT = 20;
    public static final int PADDING_TOP = 20;

    public static final int BORDER_SIZE = 2;

    protected LinearLayout mMainLayout;
    private LinearLayout mBorderLayout;

    /**
     * @param context
     * @param attrs
     */
    public SearchResultView(Context context) {
        super(context);

        // Creates shadow layout
        this.setBackgroundResource(R.color.searchResultShadow);
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT));
        this.setPadding(0, 0, 0, 4);

        LinearLayout.LayoutParams layout = (LinearLayout.LayoutParams) this
            .getLayoutParams();
        layout.setMargins(0, 0, 0, 10);
        this.setLayoutParams(layout);

        // Creates border layout
        mBorderLayout = new LinearLayout(context);
        mBorderLayout.setBackgroundResource(R.color.searchResultBorder);
        mBorderLayout.setLayoutParams(new LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mBorderLayout.setPadding(1, 1, 1, 4);

        // this.setBackgroundResource(R.drawable.shape);

        this.addView(mBorderLayout);
        // Creates mMainLayout
        mMainLayout = new LinearLayout(context);
        mMainLayout.setOrientation(HORIZONTAL);
        mMainLayout.setWeightSum(10f);

        // Parameters
        mMainLayout.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT,
            PADDING_BOTTOM);
        mMainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT));
        mMainLayout.setBackgroundResource(R.color.searchResultBackground);

        mBorderLayout.addView(mMainLayout);
    }

    public abstract void update();
}
