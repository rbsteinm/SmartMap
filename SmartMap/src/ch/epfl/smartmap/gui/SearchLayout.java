package ch.epfl.smartmap.gui;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.MockSearchEngine;
import ch.epfl.smartmap.cache.SearchEngine;

/**
 * @author jfperren
 * Provides a sliding up and down SearchPanel, containing a search bar and a layout displaying the results
 */
public class SearchLayout extends RelativeLayout {

    private final static String TAG = "SEARCH_LAYOUT";

    public enum InitState {
        NOT_INITIALIZED, INITIALIZED
    };

    private final Context mContext;
    private InitState mInitState;
    private final SearchEngine mSearchEngine;

    /**
     * Constructor
     * @param context The current context
     * @param searchEngine The searchEngine providing results for queries
     */
    public SearchLayout(Context context, AttributeSet attrs, SearchEngine searchEngine) {
        super(context, attrs);

        mContext = context;
        mInitState = InitState.NOT_INITIALIZED;
        mSearchEngine = searchEngine;

        this.setVisibility(View.GONE);
    }

    /**
     * Constructor with default mockSearchEngine
     * @param context The current context
     */
    public SearchLayout(Context context, AttributeSet attrs) {
        this(context, attrs, new MockSearchEngine());
    }

    /**
     * Returns the initialisation state of this layout
     * @return True if it is initialized
     */
    public boolean isInitialized() {
        return mInitState == InitState.INITIALIZED;
    }

    /**
     * Initializes the searchLayout, setting all listeners needed
     */
    public void initSearchLayout() {
        assert (mInitState == InitState.NOT_INITIALIZED);

        final SearchView mSearchBarEditText = (SearchView) findViewById(R.id.searchBar);

        mSearchBarEditText.setOnQueryTextListener(new OnQueryTextListener() {

            public boolean onQueryTextSubmit(String query) {
                // Do something when user his enter on keyboard
                mSearchBarEditText.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateSearchResults();
                return false;
            }
        });

        mInitState = InitState.INITIALIZED;

        assert (mInitState == InitState.INITIALIZED);
    }

    /**
     * Takes the value in the search bar and display the results of the searchEngine query in the result list.
     */
    private void updateSearchResults() {
        // Get needed Views
        final LinearLayout mSearchResultList = (LinearLayout) this
            .findViewById(R.id.search_result_list);
        final SearchView mSearchBarEditText = (SearchView) this
            .findViewById(R.id.searchBar);
        // Get search query
        String query = mSearchBarEditText.getQuery().toString();
        List<Friend> result = mSearchEngine.sendQuery(query);
        // Clean list
        clearSearchResults();
        for (Friend f : result) {
            FriendSearchResultView friendView = new FriendSearchResultView(
                mContext, f);

            mSearchResultList.addView(friendView);
        }
    }

    /**
     * Remove all search results.
     */
    private void clearSearchResults() {
        // Get needed Views
        final LinearLayout mSearchResultList = (LinearLayout) this
            .findViewById(R.id.search_result_list);
        mSearchResultList.removeAllViews();
    }

    /**
     * Opens the panel and display it full screen
     * @param query Initial search query of search bar.
     */
    public void open(String query) {
        Log.d(TAG, "method open called");
        this.setVisibility(View.VISIBLE);
        Animation bottomUp = AnimationUtils.loadAnimation(getContext(),
            R.anim.bottom_up);
        this.startAnimation(bottomUp);
        updateSearchResults();
        final SearchView mSearchView = (SearchView) findViewById(R.id.searchBar);
        mSearchView.requestFocus();
    }
    
    /**
     * Opens the panel and display it full screen
     */
    public void open(){
        open("");
    }

    /**
     * Closes the panel and hide it.
     */
    public void close() {
        Log.d(TAG, "method close called");
        Animation topDown = AnimationUtils.loadAnimation(getContext(),
            R.anim.top_down);

        this.startAnimation(topDown);
        this.setVisibility(View.GONE);
        clearSearchResults();
        clearFocus();
    }

}
