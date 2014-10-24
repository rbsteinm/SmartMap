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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
      final EditText  mSearchBarEditText = (EditText) this.findViewById(R.id.searchBarEditText);
      
      mSearchBarEditText.addTextChangedListener(new TextWatcher(){
          @Override
          public void afterTextChanged(Editable s) {
              updateSearchPanel();
              Log.d(TAG, "afterTextChanged");
          }
          public void beforeTextChanged(CharSequence s, int start, int count, int after){
              Log.d(TAG, "beforeTextChanged");
          }
          public void onTextChanged(CharSequence s, int start, int before, int count){
              Log.d(TAG, "onTextChanged");
          }
      }); 
      
      mState = State.INITIALIZED;
    }
    private void updateSearchPanel(){
        
        // Get Views
        final LinearLayout mSearchResultList = (LinearLayout) this.findViewById(R.id.search_result_list);
        final EditText  mSearchBarEditText = (EditText) this.findViewById(R.id.searchBarEditText);
        // Get search query
        String query = mSearchBarEditText.getText().toString();
        List<Friend> result = mSearchEngine.sendQuery(query);
        // Clean list
        mSearchResultList.removeAllViews();
        for(Friend f : result){
            mSearchResultList.addView(new FriendSearchResultView(mContext, f));
        }
        
    }
    
}
