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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.TextWatcher;
import android.text.Editable;

/**
 * @author jfperren
 *
 */
public class SearchLayout extends RelativeLayout {

    final private static String TAG = "SearchLayout";
    private SearchEngine mSearchEngine = new MockSearchEngine();
    
    /**
     * @param context
     * @param attrs
     */
    public SearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void initSearchLayout(){
      final EditText  mSearchBarEditText = (EditText) this.findViewById(R.id.searchBarEditText);
      final TextView mTestTextView = (TextView) this.findViewById(R.id.testid);
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
    }
    private void updateSearchPanel(){
        // Gets search query
        final EditText  mSearchBarEditText = (EditText) this.findViewById(R.id.searchBarEditText);
        String query = mSearchBarEditText.getText().toString();
        List<Friend> result = mSearchEngine.sendQuery(query);
        
        for(Friend f : result){
            
        }
        
    }
    
}
