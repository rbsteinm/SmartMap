package ch.epfl.smartmap.gui;

import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.MockDB;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author jfperren
 */
public class InformationViewCollapsed extends LinearLayout {

    public static final String TAG = "INFORMATION_VIEW_COLLAPSED";
    
    private InformationPanel mPanel;
    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mInformationTextView;
    private Button mMoreInfoButton;
    
    /**
     * @param context
     */
    public InformationViewCollapsed(Context context, Displayable item, InformationPanel panel) {
        super(context);
        
        mPanel = panel;
        
        // Image View
        mImageView = new ImageView(context);
        mImageView.setImageBitmap(item.getPicture(context));
        mImageView.setScaleType(ScaleType.FIT_XY);
        
        // Title View
        mTitleTextView = new TextView(context);
        mTitleTextView.setText(item.getTitle());
        
        // Information View
        mInformationTextView = new TextView(context);
        mInformationTextView.setText(item.getInfos());
        
        // Button
        mMoreInfoButton = new Button(context);
        mMoreInfoButton.setText("Infos");
        mMoreInfoButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                mPanel.extend();
            }
        });
        
        // Add Views into Layout
        this.addView(mImageView);
        this.addView(mTitleTextView);
        this.addView(mMoreInfoButton);
        
        // Layout Settings
        this.setOrientation(HORIZONTAL);
        this.setBackgroundResource(R.color.bottomSliderBackground);
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        this.setFocusable(true);
        this.setClickable(true);
    }
    
    /**
     * @param context
     */
    public InformationViewCollapsed(Context context, InformationPanel panel) {
        this(context, MockDB.ALAIN, panel);
    }
}