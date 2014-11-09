/**
 * 
 */
package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Friend;

/**
 * @author jfperren
 *
 */
public class SearchResultViewGroup extends LinearLayout {

    private enum State {
        MINIMIZED, EXPANDED, MAX
    }
    
    private static final String TAG = "SEARCH_RESULT_ITEM_ADAPTER";
    
    private static final int ITEMS_PER_PAGE = 10;
    private int mCurrentItemNb;
    
    private final Context mContext;
    private final List<Friend> mList;
    private Button mMoreResultsButton;
    private State mState;
    

    public SearchResultViewGroup(Context context, List<Friend> friendsList) {
        super(context);
        
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.setOrientation(VERTICAL);
        this.setBackgroundResource(R.drawable.shape);
        mContext = context;
        mList = new ArrayList<Friend>(friendsList);
        
        mMoreResultsButton = new MoreResultsButton(context, this);
        
        displayMinimized();
    }
    
    public void displayMinimized() {
        this.removeAllViews();
        mCurrentItemNb = Math.min(ITEMS_PER_PAGE, mList.size());
        
        for (int i=0; i < mCurrentItemNb; i++) {
            this.addView(SearchResultViewFactory.getSearchResultView(mContext, mList.get(i)));
            this.addView(new Divider(mContext));
        }
        
        //this.addView(mMoreResultsButton);
        
        mState = State.MINIMIZED;
    }
    
    public void showMoreResults() {
        if (mState != State.MAX) {
            mState = State.EXPANDED;
            mCurrentItemNb += Math.min(ITEMS_PER_PAGE, mList.size() - mCurrentItemNb);
            if (mCurrentItemNb == mList.size()) {
                mState = State.MAX;
            }
        }
    }
    
    public void setResultList(List<Friend> newResultList) {
        Log.d(TAG, "set result list called");
        mList.clear();
        mList.addAll(newResultList);
        displayMinimized();
    }
    
    public int getSize() {
        return mCurrentItemNb + 1;
    }
    
    private static class MoreResultsButton extends Button {
        public MoreResultsButton(Context context, final SearchResultViewGroup adapter) {
            super(context);
            this.setText("See more");
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.showMoreResults();
                }
            });
        }
    }
    
    private static class Divider extends LinearLayout {
        private static final int LEFT_PADDING = 10;
        private static final int RIGHT_PADDING = 10; 
        
        public Divider(Context context) {
            super(context);
            this.setPadding(LEFT_PADDING, 0, RIGHT_PADDING, 0);
            this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
            this.setBackgroundResource(R.color.searchResultShadow);
        }
    }
}

