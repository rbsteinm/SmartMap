package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.History;
import ch.epfl.smartmap.cache.MockDB;
import ch.epfl.smartmap.cache.SearchEngine;

/**
 * Layout that contains different SearchResultLists, that can be swapped with a horizontal swipe.
 * 
 * @author jfperren
 */
public class SearchLayout extends LinearLayout {

    /**
     * Type of Search Filter
     * 
     * @author jfperren
     */
    private enum SearchPanelType {
        HISTORY("History"), QUICK("All"), USERS("Users"), EVENTS("Events"), TAGS(
            "Tags"), GROUPS("Groups");

        private String mTitle;

        private SearchPanelType(String title) {
            mTitle = title;
        }

        public String title() {
            return mTitle;
        }
    }

    private static final String TAG = "SEARCH_RESULT_SWIPEABLE_CONTAINER";

    private static final SearchPanelType FIRST_SEARCH_PANEL_TYPE = SearchPanelType.HISTORY;
    private static final int SCROLLVIEW_SIDE_PADDING = 20;
    private static final int SCROLLVIEW_LAYOUT_BOTTOM_PADDING = 20;
    private static final int SCROLLVIEW_LAYOUT_TOP_PADDING = 3;
    private static final float TITLE_TEXT_SIZE = 15f;

    private HashMap<SearchPanelType, ScrollView> mScrollViews;
    private HashMap<SearchPanelType, SearchResultViewGroup> mSearchResultViewGroups;

    private List<Friend> mCurrentSearchResults;
    private SearchPanelType mCurrentSearchType;
    private SearchEngine mSearchEngine;

    public SearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Layout relative informations
        this.setOrientation(HORIZONTAL);
        this.setLayoutParams(new LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.setBackgroundResource(R.color.background_blue);
        mCurrentSearchType = FIRST_SEARCH_PANEL_TYPE;
        mScrollViews = new HashMap<SearchPanelType, ScrollView>();
        mSearchResultViewGroups = new HashMap<SearchPanelType, SearchResultViewGroup>();
        mCurrentSearchResults = new ArrayList<Friend>();

        // Create different Search Panels
        for (SearchPanelType type : SearchPanelType.values()) {
            Log.d(TAG, "Create SearchPanel : " + type.ordinal());
            createSearchResultLayout(context, type);
        }

        this.addView(mScrollViews.get(mCurrentSearchType));
    }

    /**
     * Sets a new {@code SearchEngine} to this SearchLayout
     * 
     * @param searchEngine
     */
    public void setSearchEngine(SearchEngine searchEngine) {
        mSearchEngine = searchEngine;
    }

    /**
     * Go to next View
     */
    private void onSwipeLeft() {
        // Get current and next ScrollViews (circularily)
        Log.d(TAG, mCurrentSearchType.title());
        ScrollView currentScrollView = mScrollViews.get(mCurrentSearchType);
        mCurrentSearchType = SearchPanelType.values()[(mCurrentSearchType
            .ordinal() + 1) % SearchPanelType.values().length];
        Log.d(TAG, mCurrentSearchType.title());
        ScrollView nextScrollView = mScrollViews.get(mCurrentSearchType);
        nextScrollView.scrollTo(0, 0);
        if (mCurrentSearchType != SearchPanelType.HISTORY) {
            SearchResultViewGroup nextSearchResultViewGroup = mSearchResultViewGroups
                .get(mCurrentSearchType);

            nextSearchResultViewGroup.displayMinimized();
            nextSearchResultViewGroup.setResultList(mCurrentSearchResults);
        }

        this.addView(nextScrollView);
        nextScrollView.startAnimation(AnimationUtils.loadAnimation(
            getContext(), R.anim.swipe_left_in));
        currentScrollView.startAnimation(AnimationUtils.loadAnimation(
            getContext(), R.anim.swipe_left_out));
        this.removeViewAt(0);
    }

    /**
     * Go to previous View
     */
    private void onSwipeRight() {
//        // Get current and next ScrollViews (circularily)
//        ScrollView currentScrollView = mScrollViews.get(mCurrentSearchType);
//        mCurrentSearchType = SearchPanelType.values()[(2 * mCurrentSearchType
//            .ordinal() - 1) % SearchPanelType.values().length];
//        Log.d(TAG, mCurrentSearchType.title());
//        ScrollView previousScrollView = mScrollViews.get(mCurrentSearchType);
//        previousScrollView.scrollTo(0, 0);
//        if (mCurrentSearchType != SearchPanelType.HISTORY) {
//            SearchResultViewGroup nextSearchResultViewGroup = mSearchResultViewGroups
//                .get(mCurrentSearchType);
//
//            nextSearchResultViewGroup.displayMinimized();
//            nextSearchResultViewGroup.setResultList(mCurrentSearchResults);
//        }
//
//        this.addView(previousScrollView, 0);
//        previousScrollView.startAnimation(AnimationUtils.loadAnimation(
//            getContext(), R.anim.swipe_right_in));
//        currentScrollView.startAnimation(AnimationUtils.loadAnimation(
//            getContext(), R.anim.swipe_right_out));
//        this.removeViewAt(1);
    }

    /**
     * Takes the value in the search bar and display the results of the searchEngine query in the result list.
     */
    public void updateSearchResults(String query) {
        mCurrentSearchResults = mSearchEngine.sendQuery(query);
        Log.d(TAG, mCurrentSearchType.title());
        Log.d(TAG, "query : " + query);
        if (mCurrentSearchType == SearchPanelType.HISTORY && query.equals("")) {
            return;
        } else if (mCurrentSearchType == SearchPanelType.HISTORY) {
            onSwipeLeft();
        }
        mSearchResultViewGroups.get(mCurrentSearchType).setResultList(
            mCurrentSearchResults);
    }

    /**
     * Show the View that needs to be displayed when opening the {@code SearchPanel}, according to the query
     * 
     * @param query
     */
    public void showMainPanel(String query) {
        if (mSearchEngine.getHistory().isEmpty() || !query.equals("")) {
            showQuickPanel();
            updateSearchResults(query);
        } else {
            showHistoryPanel();
            updateHistoryPanel();
        }
    }

    /**
     * Replace current View by QUICK View
     */
    private void showQuickPanel() {
        this.removeAllViews();
        this.addView(mScrollViews.get(SearchPanelType.QUICK));
        mCurrentSearchType = SearchPanelType.QUICK;
    }

    /**
     * Replace current View by HISTORY View
     */
    private void showHistoryPanel() {
        this.removeAllViews();
        this.addView(mScrollViews.get(SearchPanelType.HISTORY));
        mCurrentSearchType = SearchPanelType.HISTORY;
    }

    /**
     * Updates the HISTORY Panel according to the {@code SearchEngine}
     */
    private void updateHistoryPanel() {
        // History Panel
        History history = mSearchEngine.getHistory();
        LinearLayout searchResultLayout = (LinearLayout) mScrollViews.get(
            SearchPanelType.HISTORY).getChildAt(0);
        searchResultLayout.removeAllViews();

        for (int i = 0; i < history.nbOfDates(); i++) {
            // TextView displaying Date
            TextView titleView = new TextView(getContext());
            titleView.setTextSize(TITLE_TEXT_SIZE);
            titleView.setTextColor(getResources().getColor(
                R.color.searchResultTitle));
            titleView.setText(history.getDateForIndex(i).toString());
            // SearchResultViewGroup grouping all queries of this date
            SearchResultViewGroup searchResultViewGroup = new SearchResultViewGroup(
                getContext(), history.getEntriesForIndex(i));
            // Put views together
            searchResultLayout.addView(titleView);
            searchResultLayout.addView(searchResultViewGroup);
        }
    }

    /**
     * Create a new SearchResultViewGroup for a given {@code SearchPanelType}
     * 
     * @param context
     * @param searchPanelType
     */
    private void createSearchResultLayout(Context context,
        SearchPanelType searchPanelType) {

        // ScrollView with TouchEvent passing handling
        ScrollView scrollView = new ScrollView(context) {
            private GestureDetector gestureDetector = new GestureDetector(
                getContext(), new HorizontalGestureListener());

            @Override
            public boolean onTouchEvent(MotionEvent ev) {
                this.getChildAt(0).onTouchEvent(ev);

                if (gestureDetector.onTouchEvent(ev)) {
                    return true;
                } else {
                    // If not scrolling vertically (more y than x), don't hijack the event.
                    return super.onTouchEvent(ev);
                }
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (!onTouchEvent(ev)) {
                    return super.onInterceptTouchEvent(ev);
                }
                return false;
            }
        };
        scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setPadding(SCROLLVIEW_SIDE_PADDING, 0,
            SCROLLVIEW_SIDE_PADDING, 0);

        // Layout contained in ScrollView
        LinearLayout searchResultLayout = new LinearLayout(context);
        searchResultLayout.setLayoutParams(new LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        searchResultLayout.setOrientation(VERTICAL);
        searchResultLayout.setPadding(0, SCROLLVIEW_LAYOUT_TOP_PADDING, 0,
            SCROLLVIEW_LAYOUT_BOTTOM_PADDING);

        if (searchPanelType != SearchPanelType.HISTORY) {
            // Normal Search Panel
            // TextView displaying Name
            TextView titleView = new TextView(context);
            titleView.setTextSize(TITLE_TEXT_SIZE);
            titleView.setTextColor(getResources().getColor(
                R.color.main_blue));
            titleView.setText(searchPanelType.title());
            // SearchResultViewGroup
            SearchResultViewGroup searchResultViewGroup = new SearchResultViewGroup(
                context, MockDB.FRIENDS_LIST);
            // Put views together
            searchResultLayout.addView(titleView);
            searchResultLayout.addView(searchResultViewGroup);
            // Add ViewGroup entry
            mSearchResultViewGroups.put(searchPanelType, searchResultViewGroup);
        }

        scrollView.addView(searchResultLayout);
        // Add in Lists
        mScrollViews.put(searchPanelType, scrollView);
    }

    /**
     * GestureListener listening for horizontal swipes.
     * 
     * @author jfperren
     */
    private final class HorizontalGestureListener extends
        SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
            Log.d(TAG, "Analysing Gesture");
            boolean result = false;

            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD
                    && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                    result = true;
                }
            }
            return result;
        }
    }

}
