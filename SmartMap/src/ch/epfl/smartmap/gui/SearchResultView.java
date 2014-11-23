package ch.epfl.smartmap.gui;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

/**
 * This class is a basic Layout that will be used to display search results in {@code SearchLayout}.
 * 
 * @author jfperren
 */
public abstract class SearchResultView extends LinearLayout {

    /**
     * 
     */
    public enum ChildrenState {
        EMPTY,
        ADDED
    }

    // FIXME : Should define
    private static final int PADDING_BOTTOM = 20;
    private static final int PADDING_RIGHT = 20;
    private static final int PADDING_LEFT = 20;
    private static final int PADDING_TOP = 20;
    private static final float MAIN_LAYOUT_WEIGHTSUM = 10f;
    private final static int PHOTO_RIGHT_MARGIN = 40;

    private final static int PHOTO_SIZE = 150;

    private final ImageView mImageView;

    @SuppressWarnings("unused")
    private ChildrenState mChildrenState;

    /**
     * Constructor
     * 
     * @param context
     *            Context of the Application
     */
    public SearchResultView(Context context) {
        super(context);
        mChildrenState = ChildrenState.EMPTY;

        // Layout Parameters
        this.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT));
        this.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM);
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setWeightSum(MAIN_LAYOUT_WEIGHTSUM);

        // ImageView Parameters
        mImageView = new ImageView(context);
        mImageView.setAdjustViewBounds(true);
        mImageView.setImageResource(this.getImageResource());
        LayoutParams mPhotoViewLayoutParams = new LayoutParams(PHOTO_SIZE, PHOTO_SIZE);
        mPhotoViewLayoutParams.setMargins(0, 0, PHOTO_RIGHT_MARGIN, 0);
        mImageView.setLayoutParams(mPhotoViewLayoutParams);
        mImageView.setScaleType(ScaleType.FIT_XY);

        this.setOnTouchListener(this.getOnTouchListener(this));
    }

    public abstract int getImageResource();

    public abstract ViewGroup getInfosLayout();

    /**
     * Define the OnClickListener that will be called when touching this View.
     * Needs to be overriden
     * 
     * @return
     */
    public abstract OnTouchListener getOnTouchListener(final SearchResultView v);

    public void initViews() {
        mImageView.setImageResource(this.getImageResource());
        this.addView(mImageView);
        this.addView(this.getInfosLayout());
        mChildrenState = ChildrenState.ADDED;
    }
}