package ch.epfl.smartmap.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.MainActivity;
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.MockDB;

/**
 * 
 * @author jfperren
 */
public class InformationViewCollapsed extends RelativeLayout {

    public static final String TAG = "INFORMATION_VIEW_COLLAPSED";

    private static final int PADDING = 20;
    private static final int ICON_EXTRA_PADDING = 20;
    private static final int MAX_IMAGE_SIZE = 125;
    private static final int MIN_IMAGE_SIZE = 125;
    
    private SlidingPanel mPanel;
    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mInformationTextView;
    private Button mMoreInfoButton;

    private int mHeight;
    private int mWidth;
    
    /**
     * @param context
     */
    public InformationViewCollapsed(Context context, Displayable item,
        SlidingPanel panel) {
        super(context);

        mPanel = panel;

        // Layout Settings
        this.setBackgroundResource(R.drawable.view_group_background);
        this.setLayoutParams(new RelativeLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        this.setPadding(PADDING, PADDING, PADDING, PADDING);
        this.setFocusable(true);
        this.setClickable(true);
        
        // Image View
        mImageView = new ImageView(context);
        mImageView.setId(R.id.info_panel_collapsed_photo);
        Bitmap image = item.getPicture(context);
        int imageSize = Math.min(MAX_IMAGE_SIZE, Math.max(image.getHeight(), MIN_IMAGE_SIZE));
        mImageView.setImageBitmap(image);
        mImageView.setLayoutParams(new LayoutParams(imageSize, imageSize));
        ((LayoutParams) mImageView.getLayoutParams()).setMarginEnd(PADDING);
        
        

        // Title View
        mTitleTextView = new TextView(context);
        mTitleTextView.setId(R.id.info_panel_collapsed_title);
        mTitleTextView.setText(item.getTitle());
        mTitleTextView.setTextSize(20f);
        mTitleTextView.setTextColor(getResources().getColor(R.color.main_blue));
        mTitleTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mTitleTextView.setPaddingRelative(0,0,0,-10);
        

        // Information View
        mInformationTextView = new TextView(context);
        mInformationTextView.setId(R.id.info_panel_collapsed_infos);
        mInformationTextView.setText(item.getInfos());
        mInformationTextView.setTextColor(getResources().getColor(R.color.main_grey));
        mInformationTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mInformationTextView.setPaddingRelative(0,0,0,-10);
        
        
        // Button
        mMoreInfoButton = new Button(context);
        mMoreInfoButton.setLayoutParams(new LayoutParams(imageSize, imageSize));
        mMoreInfoButton.setBackgroundResource(R.drawable.ic_info_up1);
        mMoreInfoButton.setPadding(ICON_EXTRA_PADDING, ICON_EXTRA_PADDING, ICON_EXTRA_PADDING, ICON_EXTRA_PADDING);
        mMoreInfoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPanel.extend();
            }
        });

        
        ((LayoutParams) mMoreInfoButton.getLayoutParams()).addRule(ALIGN_PARENT_RIGHT);
        ((LayoutParams) mTitleTextView.getLayoutParams()).addRule(RIGHT_OF, R.id.info_panel_collapsed_photo);
        ((LayoutParams) mInformationTextView.getLayoutParams()).addRule(BELOW, R.id.info_panel_collapsed_title);
        ((LayoutParams) mInformationTextView.getLayoutParams()).addRule(RIGHT_OF, R.id.info_panel_collapsed_photo);
        ((LayoutParams) mImageView.getLayoutParams()).addRule(ALIGN_PARENT_LEFT);
        
        // Add Views into Layout
        this.addView(mImageView);
        this.addView(mTitleTextView);
        this.addView(mInformationTextView);
        this.addView(mMoreInfoButton);
    }

    /**
     * @param context
     */
    public InformationViewCollapsed(Context context, SlidingPanel panel) {
        this(context, MockDB.ALAIN, panel);
    }
    
    public void setItem(Displayable item) {
        mImageView.setImageBitmap(item.getPicture(getContext()));
        mTitleTextView.setText(item.getTitle());
        mInformationTextView.setText(item.getInfos());
    }
}