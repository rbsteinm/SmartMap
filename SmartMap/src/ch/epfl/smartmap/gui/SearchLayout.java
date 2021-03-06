package ch.epfl.smartmap.gui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.search.SearchEngineInterface.Type;

/**
 * Layout that contains different SearchResult lists with different result, which you can select by sweeping
 * left or right on the screen
 * 
 * @author jfperren
 */
public class SearchLayout extends LinearLayout {

    // Margins & Paddings
    private static final int PADDING_LEFT = 20;
    private static final int PADDING_RIGHT = 20;
    private static final int PADDING_BOTTOM = 20;
    private static final int PADDING_TOP = 10;
    private static final int MARGIN_BELOW_SEARCHVIEWGROUP = 20;

    // Colors
    private static final int BACKGROUND_COLOR = R.color.background_blue;
    private static final int TITLE_NORMAL_COLOR = R.color.shadow_blue;
    private static final int TITLE_HIGHLIGHTED_COLOR = R.color.main_blue;

    // Text size
    private static final float TITLE_TEXT_SIZE = 15f;

    // Default values
    private static final String DEFAULT_SEARCH_QUERY = "";
    private static final Type DEFAULT_SEARCH_TYPE = Type.ALL;

    // Data structures
    private final Map<Type, ScrollView> mScrollViews;
    private final Map<Type, SearchResultViewGroup> mSearchResultViewGroups;
    private final Map<Type, Integer> mSearchTypeIndexes;
    private final Map<Type, TextView> mTitleTextViews;
    private final List<Type> mActiveSearchTypes;

    // Extra views
    private final LinearLayout mTitleBar;

    // Information about current state
    private Type mCurrentSearchType;
    private String mCurrentQuery;
    private Context mParentContext;

    public SearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Init to avoid NullPointers
        ServiceContainer.initSmartMapServices(context);

        // Set parent context
        mParentContext = context;

        // Layout relative informations
        this.setOrientation(VERTICAL);
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
        this.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM);
        this.setBackgroundResource(BACKGROUND_COLOR);

        // Set default search query (needs to be done addSearchTypes)
        mCurrentQuery = DEFAULT_SEARCH_QUERY;

        // Initialize data structures
        mScrollViews = new HashMap<Type, ScrollView>();
        mSearchResultViewGroups = new HashMap<Type, SearchResultViewGroup>();
        mSearchTypeIndexes = new HashMap<Type, Integer>();
        mActiveSearchTypes = new LinkedList<Type>();
        mTitleTextViews = new HashMap<Type, TextView>();

        // Initialize Views
        mTitleBar = new LinearLayout(context);

        // Initialize search types
        this.addSearchTypes(Type.ALL, Type.FRIENDS, Type.EVENTS, Type.TAGS, Type.GROUPS);

        // Set default search type
        this.setSearchType(DEFAULT_SEARCH_TYPE);
    }

    /**
     * Show the {@code ScrollView} that needs to be displayed when opening the {@code SlidingPanel}, according
     * to the query
     * 
     * @param query
     */
    public void resetView(String query) {
        this.setSearchType(DEFAULT_SEARCH_TYPE);
        this.setSearchQuery(query);
    }

    /**
     * Updates the current panel with the new search query.
     */
    public void setSearchQuery(String query) {
        mCurrentQuery = query;
        this.updateCurrentPanel();
    }

    /**
     * Creates a new ScrollView containing the results of a certain Type
     * 
     * @param sContext
     *            Context of the Application
     * @param searchType
     *            Type of Search Results the ScrollView should display
     */
    private void addSearchResultScrollView(Type searchType) {
        if (!mActiveSearchTypes.contains(searchType)) {
            ScrollView scrollView = new SwipeableScrollView(this.getContext());
            // Create & Add Views
            SearchResultViewGroup searchResultViewGroup = new SearchResultViewGroup(this.getContext());
            LayoutParams searchParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            searchParams.setMargins(0, 0, 0, MARGIN_BELOW_SEARCHVIEWGROUP);
            searchResultViewGroup.setLayoutParams(searchParams);
            scrollView.addView(searchResultViewGroup);
            // Add ViewGroup entry
            mSearchResultViewGroups.put(searchType, searchResultViewGroup);

            // Add ScrollView entry
            mScrollViews.put(searchType, scrollView);

            // Add refenrences to the new searchType
            mActiveSearchTypes.add(searchType);
            mSearchTypeIndexes.put(searchType, mActiveSearchTypes.size() - 1);
        }
    }

    /**
     * Create all Views to handle multiple Search Types
     * 
     * @param sContext
     *            Context of the Application
     * @param searchType
     *            Search Types that need to be implemented
     */
    private void addSearchTypes(Type... searchTypes) {
        // Add all titles into mTitleBar
        for (Type searchType : searchTypes) {
            // Create titleTextView
            TextView titleTextView = new TextView(this.getContext());
            titleTextView.setText(searchType.getTitle());
            titleTextView.setTextSize(TITLE_TEXT_SIZE);
            titleTextView.setTextColor(this.getResources().getColor(TITLE_NORMAL_COLOR));
            // Create a spacer
            TextView spacer = new TextView(this.getContext());
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            spacer.setLayoutParams(params);
            // Add the titleTextView and the spacer
            mTitleTextViews.put(searchType, titleTextView);
            mTitleBar.addView(titleTextView);
            mTitleBar.addView(spacer);
        }
        // Remove last spacer
        mTitleBar.removeViewAt(mTitleBar.getChildCount() - 1);

        for (Type searchType : searchTypes) {
            this.addSearchResultScrollView(searchType);
        }
    }

    /**
     * @return Next Search Type in the Layout
     */
    private Type nextSearchType() {
        int nextSearchTypeIndex =
            (mSearchTypeIndexes.get(mCurrentSearchType).intValue() + 1) % mActiveSearchTypes.size();
        return mActiveSearchTypes.get(nextSearchTypeIndex);
    }

    /**
     * Go to next View
     */
    private void onSwipeLeft() {
        this.setSearchType(this.nextSearchType());
    }

    /**
     * Go to previous View
     */
    private void onSwipeRight() {
        this.setSearchType(this.previousSearchType());
    }

    /**
     * @return searchType that is on the left SearchResultViewGroup
     */
    private Type previousSearchType() {
        int previousSearchTypeIndex =
            ((mSearchTypeIndexes.get(mCurrentSearchType).intValue() - 1) + mActiveSearchTypes.size())
                % mActiveSearchTypes.size();
        return mActiveSearchTypes.get(previousSearchTypeIndex);
    }

    /**
     * Change to a different search type and displays it
     * 
     * @param searchType
     */
    private void setSearchType(Type searchType) {
        // Add new Views
        this.removeAllViews();
        this.addView(mTitleBar);
        this.addView(mScrollViews.get(searchType));
        // Update search type
        mCurrentSearchType = searchType;
        this.updateCurrentPanel();
        // Set title colors
        for (TextView textView : mTitleTextViews.values()) {
            textView.setTextColor(this.getResources().getColor(TITLE_NORMAL_COLOR));
        }
        mTitleTextViews.get(searchType).setTextColor(this.getResources().getColor(TITLE_HIGHLIGHTED_COLOR));
        // Scroll up
        mScrollViews.get(searchType).scrollTo(0, 0);
    }

    /**
     * Updates current {@code SearchResultViewGroup} with new values
     */
    private void updateCurrentPanel() {
        new AsyncTask<Void, Void, List<Displayable>>() {
            @Override
            public List<Displayable> doInBackground(Void... params) {
                return ServiceContainer.getSearchEngine().sendQuery(mCurrentQuery, mCurrentSearchType);
            }

            @Override
            public void onPostExecute(final List<Displayable> result) {
                ((Activity) mParentContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSearchResultViewGroups.get(mCurrentSearchType).setResultList(result);
                    }
                });
            }
        }.execute();

    }

    /**
     * GestureListener listening for horizontal swipes.
     * 
     * @author jfperren
     */
    private final class HorizontalGestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;

            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if ((Math.abs(diffX) > Math.abs(diffY)) && (Math.abs(diffX) > SWIPE_THRESHOLD)
                && (Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD)) {
                if (diffX > 0) {
                    SearchLayout.this.onSwipeRight();
                } else {
                    SearchLayout.this.onSwipeLeft();
                }
                result = true;
            }
            return result;
        }
    }

    /**
     * Provides a Vertical ScrollView that listens to Horizontal Swipes and
     * switch search panels when happening.
     * 
     * @author jfperren
     */
    private final class SwipeableScrollView extends ScrollView {

        private final GestureDetector mGestureDetector;
        private final LinearLayout mLayout;

        public SwipeableScrollView(Context context) {
            super(context);

            mLayout = new LinearLayout(context);
            mLayout.setOrientation(VERTICAL);
            this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            // Remove scrollbar and shadow when the scrollview can't be
            // scrolled.
            this.setVerticalScrollBarEnabled(false);
            super.addView(mLayout);

            mGestureDetector = new GestureDetector(this.getContext(), new HorizontalGestureListener());
        }

        @Override
        public void addView(View child) {
            mLayout.addView(child);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (!this.onTouchEvent(ev)) {
                return super.onInterceptTouchEvent(ev);
            }
            return false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            this.getChildAt(0).onTouchEvent(ev);

            if (mGestureDetector.onTouchEvent(ev)) {
                return true;
            } else {
                // If not scrolling vertically (more y than x), don't hijack the event.
                return super.onTouchEvent(ev);
            }
        }

        @Override
        public void removeAllViews() {
            mLayout.removeAllViews();
        }

        @Override
        public void removeViewAt(int index) {
            mLayout.removeViewAt(index);
        }
    }
}