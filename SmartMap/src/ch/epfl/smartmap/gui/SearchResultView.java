package ch.epfl.smartmap.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.MainActivity;
import ch.epfl.smartmap.cache.Displayable;

/**
 * This class is a basic Layout that will be used to display search results in {@code SearchLayout}. It is immutable.
 *
 * @author jfperren
 */
public class SearchResultView extends RelativeLayout {

    // Margins & Paddings
    private static final int PADDING_RIGHT = 20;
    private static final int PADDING_LEFT = 20;
    private static final int PADDING_TOP = 20;
    private static final int PADDING_BOTTOM = 20;
    private static final int TITLE_BOTTOM_PADDING = 5;
    private static final int PHOTO_RIGHT_MARGIN = 40;
    private static final int IMAGE_SIZE = 150;

    // Text Sizes
    private static final float TITLE_TEXT_SIZE = 17f;

    // Distances
    private static final int CLICK_DISTANCE_THRESHHOLD = 10;

    // Children Views
    private final ImageView mImageView;
    private final TextView mTitleView;
    private final TextView mShortInfoView;

    private static final int SQUARE_POWER = 2;

    // Informations about the current state
    private final Displayable mItem;
    private final Bitmap mImage;

    /**
     * Constructor
     *
     * @param context
     *            Context of the Application
     */
    public SearchResultView(final Context context, Displayable item) {
        super(context);

        mItem = item;
        mImage = item.getSearchImage();

        // Layout Parameters
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        this.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM);

        // Create mImageView
        mImageView = new ImageView(context);
        mImageView.setId(R.id.search_result_image);
        mImageView.setAdjustViewBounds(true);

        mImageView.setImageBitmap(mImage);
        mImageView.setScaleType(ScaleType.FIT_XY);
        LayoutParams imageParams = new LayoutParams(IMAGE_SIZE, IMAGE_SIZE);
        imageParams.setMargins(0, 0, PHOTO_RIGHT_MARGIN, 0);
        imageParams.addRule(ALIGN_PARENT_LEFT);
        mImageView.setLayoutParams(imageParams);

        // Create mTitleView
        mTitleView = new TextView(context);
        mTitleView.setId(R.id.search_result_title);
        mTitleView.setText(mItem.getTitle());
        mTitleView.setTextSize(TITLE_TEXT_SIZE);
        mTitleView.setTypeface(null, Typeface.BOLD);
        mTitleView.setPadding(0, 0, 0, TITLE_BOTTOM_PADDING);
        LayoutParams titleParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        titleParams.addRule(ALIGN_PARENT_TOP);
        titleParams.addRule(RIGHT_OF, R.id.search_result_image);
        mTitleView.setLayoutParams(titleParams);

        // Create mShortInfoView
        mShortInfoView = new TextView(context);
        mShortInfoView.setId(R.id.search_result_short_info);
        mShortInfoView.setText(item.getSubtitle());
        mShortInfoView.setTextColor(this.getResources().getColor(R.color.lastSeenConnectionTextColor));
        LayoutParams shortInfoParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        shortInfoParams.addRule(RIGHT_OF, R.id.search_result_image);
        shortInfoParams.addRule(BELOW, R.id.search_result_title);
        mShortInfoView.setLayoutParams(shortInfoParams);

        // Add subViews
        this.addView(mImageView);
        this.addView(mTitleView);
        this.addView(mShortInfoView);

        // Displays the item on click
        this.setOnTouchListener(new ClickOnItemOnTouchListener());
    }

    /**
     *
     * This touch listener displays the item on click.
     *
     */
    private class ClickOnItemOnTouchListener implements OnTouchListener {
        private float startX;
        private float startY;

        @Override
        public boolean onTouch(View v, MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                startX = ev.getAxisValue(MotionEvent.AXIS_X);
                startY = ev.getAxisValue(MotionEvent.AXIS_Y);
                v.setBackgroundColor(SearchResultView.this.getResources().getColor(R.color.searchResultOnSelect));

            } else if (ev.getAction() == MotionEvent.ACTION_UP) {
                float endX = ev.getAxisValue(MotionEvent.AXIS_X);
                float endY = ev.getAxisValue(MotionEvent.AXIS_Y);

                double clickDistance = Math.sqrt(Math.pow(endX - startX, SQUARE_POWER)
                        + Math.pow(endY - startY, SQUARE_POWER));
                if (clickDistance < CLICK_DISTANCE_THRESHHOLD) {
                    ((MainActivity) SearchResultView.this.getContext()).performQuery(mItem);
                }
                v.setBackgroundResource(0);
            } else if (ev.getAction() == MotionEvent.ACTION_CANCEL) {
                v.setBackgroundResource(0);
            }
            return true;
        }
    }
}