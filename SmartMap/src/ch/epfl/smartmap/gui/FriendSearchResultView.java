/**
 * 
 */
package ch.epfl.smartmap.gui;

import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.R;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

/**
 * @author jfperren
 *
 */
public class FriendSearchResultView extends SearchResultView {

    private static int PHOTO_RIGHT_MARGIN = 40;
    private static int NAME_VIEW_BOTTOM_PADDING = 5;
    
    private Friend mFriend;
    private ImageView mPhotoView;
    private TextView mNameView;
    private TextView mLastConnectionView;
    private Button mMoreInfoButton;
    
    private LinearLayout mInfoLayout;
    
    /**
     * @param context
     * @param attrs
     */
    public FriendSearchResultView(Context context, Friend friend) {
        super(context);
        
        mFriend = friend;
        
        // Creates mPhotoView
        mPhotoView = new ImageView(context);
        mPhotoView.setAdjustViewBounds(true);
        mPhotoView.setImageResource(R.drawable.default_user_icon);
        LayoutParams mPhotoViewLayoutParams = new LayoutParams(150, 150);
        mPhotoViewLayoutParams.setMargins(0, 0, PHOTO_RIGHT_MARGIN, 0);
        mPhotoView.setLayoutParams(mPhotoViewLayoutParams);
        mPhotoView.setScaleType(ScaleType.FIT_XY);
        
        // Creates mNameView
        mNameView = new TextView(context);
        mNameView.setText(mFriend.getName());
        mNameView.setTextSize(17f);
        mNameView.setTypeface(null, Typeface.BOLD);
        mNameView.setPadding(0, 0, 0, NAME_VIEW_BOTTOM_PADDING);
        
        // Creates mLastConnectionView
        mLastConnectionView = new TextView(context); 
        mLastConnectionView.setText("last seen " + friend.getLastConnection()+ ".");
        mLastConnectionView.setTextColor(getResources().getColor(R.color.lastSeenConnectionTextColor));
        
        // Create mInfoLayout
        mInfoLayout = new LinearLayout(context);
        mInfoLayout.setOrientation(VERTICAL);
        
        // Creates mMoreInfoButton
//        mMoreInfoButton = new Button(context);
//        mMoreInfoButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 9f));
//        mMoreInfoButton.setText("Infos");
//        mMoreInfoButton.setBackground(null);
//        mMainLayout.addView(mMoreInfoButton, 1);
        
        // Adds all view into the ResultView
        mMainLayout.addView(mPhotoView);
        mInfoLayout.addView(mNameView);
        mInfoLayout.addView(mLastConnectionView);
        mInfoLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        
        mMainLayout.addView(mInfoLayout);
    }
    
    /**
     * This method should be called when the infos on the user changes. Typically called by a FriendObserver.
     */
    @Override
    public void update(){
        // TODO
        // 1) Update last connection info
    }
}
