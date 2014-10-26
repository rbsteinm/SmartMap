package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.MockSearchEngine;
import ch.epfl.smartmap.cache.SearchEngine;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.text.TextWatcher;
import android.text.Editable;

/**
 * @author jfperren
 *
 */
public class SearchLayout extends RelativeLayout {

    private final static String TAG = "SearchLayout";
    private final Context mContext;
    public enum State {NOT_INITIALIZED, INITIALIZED};
    private State mState;
    private SearchEngine mSearchEngine = new MockSearchEngine();
    
    /**
     * @param context
     * @param attrs
     */
    public SearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mState = State.NOT_INITIALIZED;
    }
    
    public boolean isInitialized(){
        return mState == State.INITIALIZED;
    }
    
    public void initSearchLayout(){
      final SearchView  mSearchBarEditText = (SearchView) findViewById(R.id.searchBar);
      final SmartMapSlidingUpPanel mBottomSlider = (SmartMapSlidingUpPanel) findViewById(R.id.sliding_layout);
      
      mSearchBarEditText.setOnQueryTextListener(new OnQueryTextListener(){

          public boolean onQueryTextSubmit(String query) {
              // Do something when user his enter on keyboard
              mSearchBarEditText.clearFocus();
              return false;
          }
          
          @Override
          public boolean onQueryTextChange(String newText) {
              updateSearchPanel();
              return false;
          }
      });
      
      mSearchBarEditText.setOnClickListener(new OnClickListener(){
        @Override
        public void onClick(View v) {
              mBottomSlider.expandPanel();
        }
      });
      
      mState = State.INITIALIZED;
    }
    
    private void updateSearchPanel(){
        
        // Get Views
        final LinearLayout mSearchResultList = (LinearLayout) this.findViewById(R.id.search_result_list);
        final SearchView  mSearchBarEditText = (SearchView) this.findViewById(R.id.searchBar);
        // Get search query
        String query = mSearchBarEditText.getQuery().toString();
        List<Friend> result = mSearchEngine.sendQuery(query);
        // Clean list
        mSearchResultList.removeAllViews();
        for(Friend f : result){
            FriendSearchResultView friendView = new FriendSearchResultView(mContext, f);

            mSearchResultList.addView(friendView);
        }
    }
    
}
