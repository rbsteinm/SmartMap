package ch.epfl.smartmap.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.MockSearchEngine;
import ch.epfl.smartmap.cache.SearchEngine;

/**
 * Represents a Panel that can can be opened or closed from the bottom of the activity. It contains A search bar
 * and a view that displays the queries sent using the search bar.
 * 
 * @author jfperren
 */
public class SearchLayout extends RelativeLayout {

    private final static String TAG = "SEARCH_LAYOUT";
    private final static String AUDIT_TAG = "AuditError : " + TAG;

    /** 
     * State of a SearchLayout. NOT_INITIALIZED implies that it is needed to call {@code initSearchLayout} first
     * 
     * @author jfperren
     */
    public enum InitState {
        NOT_INITIALIZED, INITIALIZED
    };

    /**
     * All possible search result lists
     * 
     * @author jfperren
     */
    public enum SearchTypes {
        QUICK, USERS, EVENTS, TAGS, GROUPS
    }

    private final Context mContext;
    private InitState mInitState;
    private final SearchEngine mSearchEngine;

    /**
     * Constructor
     * @param context The current context
     * @param searchEngine The searchEngine providing results for queries
     */
    public SearchLayout(Context context, AttributeSet attrs,
        SearchEngine searchEngine) {
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

        // Get Views
        final SearchView mSearchView = (SearchView) findViewById(R.id.searchBar);
        mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

            public boolean onQueryTextSubmit(String query) {
                // Do something when user his enter on keyboard
                mSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateSearchResults();
                return false;
            }
        });

        // mSearchResultList.setOnClickListener(new OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // mSearchView.clearFocus();
        // }
        // });

        mInitState = InitState.INITIALIZED;
    }

    /**
     * Takes the value in the search bar and display the results of the searchEngine query in the result list.
     */
    private void updateSearchResults() {
        // Get needed Views
        final SearchView mSearchBarEditText = (SearchView) this
            .findViewById(R.id.searchBar);
        final SearchResultSwipeableContainer mSearchResultSwipeableContainer = 
            (SearchResultSwipeableContainer) findViewById(R.id.swipeable_search_result_container);

        // Get search query
        String query = mSearchBarEditText.getQuery().toString();
        mSearchResultSwipeableContainer.setResultList(mSearchEngine
            .sendQuery(query));
    }

    /**
     * Remove all search results.
     */
    // private void clearSearchResults() {
    // // Get needed Views
    // final LinearLayout mSearchResultList = (LinearLayout) this
    // .findViewById(R.id.search_result_list);
    // mSearchResultList.removeAllViews();
    // }

    /**
     * Opens the panel and displays it full screen
     * @param query Initial search query of search bar.
     */
    public void open(String query) {
        Log.d(TAG, "Etape open START");
        this.setVisibility(View.VISIBLE);
        Animation bottomUp = AnimationUtils.loadAnimation(getContext(),
            R.anim.bottom_up);
        this.startAnimation(bottomUp);
        updateSearchResults();
        Log.d(TAG, "Etape open END");
    }

    /**
     * Opens the panel and displays it full screen
     */
    public void open() {
        open("");
    }

    /**
     * Closes the panel and hides it.
     */
    public void close() {
        Animation topDown = AnimationUtils.loadAnimation(getContext(),
            R.anim.top_down);

        this.startAnimation(topDown);
        this.setVisibility(View.GONE);
        // clearSearchResults();
        clearFocus();
    }

    /**
     * Checks that the Representation Invariant is not violated.
     * @param depth represents how deep the audit check is done (use 1 to check this object only)
     * @return The number of audit errors in this object
     */
    public int auditErrors(int depth) {
        // TODO : Decomment when auditErrors coded for other classes
        if (depth == 0) {
            return 0;
        }

        int auditErrors = 0;
        // auditErrors += mSearchEngine.auditErrors(depth - 1);
        if (mInitState != InitState.INITIALIZED) {
            auditErrors++;
            Log.e(AUDIT_TAG, "SearchLayout not initialized");
        }

        return auditErrors;
    }
}
