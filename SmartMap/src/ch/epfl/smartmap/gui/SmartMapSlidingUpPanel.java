/**
 * 
 */
package ch.epfl.smartmap.gui;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import ch.epfl.smartmap.R;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * @author jfperren
 *
 */
public class SmartMapSlidingUpPanel extends SlidingUpPanelLayout {

    private Layout mSearchView;
    private SearchLayout mSearchLayout;
    
    private LayoutInflater mInflater;
    
    private enum LayoutState {NONE, SEARCH, INFORMATION};
    private LayoutState mCurrentState;
    
    /**
     * @param context
     */
    public SmartMapSlidingUpPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSearchLayout = (SearchLayout) mInflater.inflate(R.layout.layout_search, (ViewGroup) this, false);
        mCurrentState = LayoutState.NONE;   
    }
    
    public void openSearchView(){
        if (mCurrentState == LayoutState.SEARCH) {
            // No need to change layout
        } else {
            // Get the layout container
            ViewGroup mSlidingLayout = (ViewGroup) this.getChildAt(1);
            // Clean it and display mSearchLayout
            mSlidingLayout.removeAllViews();
            mSlidingLayout.addView(mSearchLayout);
            // Initialize mSearchLayout if needed
            if (!mSearchLayout.isInitialized()){
                mSearchLayout.initSearchLayout();
            }
            
            // Switch state
            mCurrentState = LayoutState.SEARCH;
        }
        // When searching, panel must be fully expanded
        this.expandPanel();
    }

    public void closeSearchView(){
        
    }
}
