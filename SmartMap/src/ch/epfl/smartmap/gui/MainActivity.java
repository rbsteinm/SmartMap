package ch.epfl.smartmap.gui;


import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import ch.epfl.smartmap.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.view.KeyEvent;

/**
 * @author jfperren
 *
 */
public class MainActivity extends Activity {
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final EditText mSearchBarEditText = (EditText) findViewById(R.id.searchBarEditText);
        final SlidingUpPanelLayout mBottomSlider = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        
        mSearchBarEditText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    Log.d("searchBar", "Search Request Sent");
                    handled = true;
                }
                return handled;
            }
        });
        
        mSearchBarEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mBottomSlider.expandPanel();
                    Log.d("SearchBar", "Got focus");
                } else {
                    mBottomSlider.hidePanel();
                    Log.d("SearchBar", "Lost focus");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    

    @Override
    public void onBackPressed() {
        final SlidingUpPanelLayout mBottomSlider = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        if (mBottomSlider != null && mBottomSlider.isPanelExpanded() || mBottomSlider.isPanelAnchored()) {
            mBottomSlider.collapsePanel();
        } else {
            super.onBackPressed();
        }
    }

}
