/**
 * 
 */
package ch.epfl.smartmap.gui;

import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.R;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author jfperren
 *
 */
public class FriendSearchResultView extends SearchResultView {

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
        mPhotoView.setImageResource(R.drawable.ic_launcher);
        
        // Creates mNameView
        mNameView = new TextView(context);
        mNameView.setText(mFriend.getName());
        
        // Creates mLastConnectionView
        mLastConnectionView = new TextView(context); 
        mLastConnectionView.setText("last seen ...");
        
        // Create mInfoLayout
        mInfoLayout = new LinearLayout(context);
        mInfoLayout.setOrientation(VERTICAL);
        
        // Creates mMoreInfoButton
        mMoreInfoButton = new Button(context);
        
        // Adds all view into the ResultView
        this.addView(mPhotoView);
        mInfoLayout.addView(mNameView);
        mInfoLayout.addView(mLastConnectionView);
        mInfoLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        
        this.addView(mInfoLayout);
        this.addView(mMoreInfoButton);
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
