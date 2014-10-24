package ch.epfl.smartmap.gui;

import ch.epfl.smartmap.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * @author jfperren
 *
 */
public class SearchLayout extends RelativeLayout {

    EditText mSearchBarEditText;
    
    /**
     * @param context
     * @param attrs
     */
    public SearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        mSearchBarEditText = (EditText) this.findViewById(R.id.searchBarEditText);
    }
    
    public void getSearchTextValue(){
        
    }
    
}
