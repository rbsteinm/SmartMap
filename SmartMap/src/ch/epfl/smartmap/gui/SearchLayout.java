package ch.epfl.smartmap.gui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.History;
import ch.epfl.smartmap.cache.MockSearchEngine;
import ch.epfl.smartmap.cache.SearchEngine;

/**
 * Layout that contains different SearchResultLists, that can be swapped with a
 * horizontal swipe.
 * 
 * @author jfperren
 */
public class SearchLayout extends LinearLayout {

    private static final String TAG = "SEARCH_RESULT_SWIPEABLE_CONTAINER";

    private static final int PADDING_LEFT = 20;
    private static final int PADDING_RIGHT = 20;
    private static final int PADDING_BOTTOM = 20;
    private static final int PADDING_TOP = 10;
    private static final int MARGIN_BELOW_TITLE = 5;
    private static final int MARGIN_BELOW_SEARCHVIEWGROUP = 20;

    private static final float TITLE_TEXT_SIZE = 15f;

    private final HashMap<SearchEngine.Type, ScrollView> mScrollViews;
    private final HashMap<SearchEngine.Type, SearchResultViewGroup> mSearchResultViewGroups;
    private final HashMap<SearchEngine.Type, Integer> mSearchTypeIndexes;

    private SearchEngine.Type mCurrentSearchType;

    private SearchEngine mSearchEngine;
    private LinearLayout mTitleBar;
    private List<SearchEngine.Type> mActiveSearchTypes;

    private String mCurrentQuery;

    public SearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Layout relative informations
        this.setOrientation(VERTICAL);
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
        this.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM);
        this.setBackgroundResource(R.color.background_blue);
        // Initialize data structures
        mCurrentQuery = "";
        mCurrentSearchType = SearchEngine.Type.FRIENDS;
        mScrollViews = new HashMap<SearchEngine.Type, ScrollView>();
        mSearchResultViewGroups = new HashMap<SearchEngine.Type, SearchResultViewGroup>();
        mSearchTypeIndexes = new HashMap<SearchEngine.Type, Integer>();
        mActiveSearchTypes = new LinkedList<SearchEngine.Type>();

        this.setSearchEngine(new MockSearchEngine());

        mTitleBar = new LinearLayout(context);

        this.addSearchType(context, SearchEngine.Type.ALL);
        this.addSearchType(context, SearchEngine.Type.FRIENDS);
        this.addSearchType(context, SearchEngine.Type.EVENTS);
        this.setSearchType(SearchEngine.Type.ALL);
    }

    /**
     * Sets a new {@code SearchEngine} to this SearchLayout
     * 
     * @param searchEngine
     */
    public void setSearchEngine(SearchEngine searchEngine) {
        mSearchEngine = searchEngine;
    }

    public void setSearchType(SearchEngine.Type searchType) {
        // Add new Views
        this.removeAllViews();
        this.addView(mTitleBar);
        this.addView(mScrollViews.get(searchType));
        // Update search type
        mCurrentSearchType = searchType;
        this.updateCurrentPanel();
        // Scroll up
        mScrollViews.get(searchType).scrollTo(0, 0);
    }

    private void updateCurrentPanel() {
        mSearchResultViewGroups.get(mCurrentSearchType).setResultList(
            mSearchEngine.sendQuery(mCurrentQuery, mCurrentSearchType));
    }

    /**
     * Go to next View
     */
    private void onSwipeLeft() {
        int nextSearchTypeIndex =
            (mSearchTypeIndexes.get(mCurrentSearchType).intValue() + 1) % mActiveSearchTypes.size();
        SearchEngine.Type nextSearchType = mActiveSearchTypes.get(nextSearchTypeIndex);
        this.setSearchType(nextSearchType);

        // ScrollView currentScrollView = mScrollViews.get(mCurrentSearchType);
        // mCurrentSearchType =
        // SearchType.values()[(mCurrentSearchType.ordinal() + 1) %
        // SearchType.values().length];
        // Log.d(TAG, mCurrentSearchType.mTitle);
        // ScrollView nextScrollView = mScrollViews.get(mCurrentSearchType);
        // nextScrollView.scrollTo(0, 0);
        // if (mCurrentSearchType != SearchType.HISTORY) {
        // SearchResultViewGroup<Friend> nextSearchResultViewGroup =
        // mSearchResultViewGroups.get(mCurrentSearchType);
        // nextSearchResultViewGroup.displayMinimized();
        // nextSearchResultViewGroup.setResultList(mCurrentSearchResults);
        // }
        //
        // this.addView(nextScrollView);
        // nextScrollView.startAnimation(AnimationUtils.loadAnimation(this.getContext(),
        // R.anim.swipe_left_in));
        // currentScrollView.startAnimation(AnimationUtils.loadAnimation(this.getContext(),
        // R.anim.swipe_left_out));
        // this.removeViewAt(0);
    }

    /**
     * Go to previous View
     */
    private void onSwipeRight() {
        // // Get current and next ScrollViews (circularily)
        // ScrollView currentScrollView = mScrollViews.get(mCurrentSearchType);
        // mCurrentSearchType = SearchPanelType.values()[(2 * mCurrentSearchType
        // .ordinal() - 1) % SearchPanelType.values().length];
        // Log.d(TAG, mCurrentSearchType.title());
        // ScrollView previousScrollView = mScrollViews.get(mCurrentSearchType);
        // previousScrollView.scrollTo(0, 0);
        // if (mCurrentSearchType != SearchPanelType.HISTORY) {
        // SearchResultViewGroup nextSearchResultViewGroup =
        // mSearchResultViewGroups
        // .get(mCurrentSearchType);
        //
        // nextSearchResultViewGroup.displayMinimized();
        // nextSearchResultViewGroup.setResultList(mCurrentSearchResults);
        // }
        //
        // this.addView(previousScrollView, 0);
        // previousScrollView.startAnimation(AnimationUtils.loadAnimation(
        // getContext(), R.anim.swipe_right_in));
        // currentScrollView.startAnimation(AnimationUtils.loadAnimation(
        // getContext(), R.anim.swipe_right_out));
        // this.removeViewAt(1);
    }

    /**
     * Updates the current panel with the new search query.
     */
    public void setSearchQuery(String query) {
        mCurrentQuery = query;
        this.updateCurrentPanel();
    }

    /**
     * Show the View that needs to be displayed when opening the
     * {@code SearchPanel}, according to the query
     * 
     * @param query
     */
    public void resetView(String query) {
        if (mSearchEngine.getHistory().isEmpty() || !query.equals("")) {
            this.setSearchType(SearchEngine.Type.FRIENDS);
            this.setSearchQuery(query);
        } else {
            this.showHistoryPanel();
            this.updateHistoryPanel();
        }
    }

    /**
     * Replace current View by HISTORY View
     */
    private void showHistoryPanel() {
        this.removeAllViews();
        this.addView(mScrollViews.get(SearchEngine.Type.HISTORY));
        mCurrentSearchType = SearchEngine.Type.HISTORY;
    }

    /**
     * Updates the HISTORY Panel according to the {@code SearchEngine}
     */
    private void updateHistoryPanel() {
        // History Panel
        History history = mSearchEngine.getHistory();
        LinearLayout searchResultLayout =
            (LinearLayout) mScrollViews.get(SearchEngine.Type.HISTORY).getChildAt(0);
        searchResultLayout.removeAllViews();

        for (int i = 0; i < history.nbOfDates(); i++) {
            // TextView displaying Date
            TextView titleView = new TextView(this.getContext());
            titleView.setTextSize(TITLE_TEXT_SIZE);
            titleView.setTextColor(this.getResources().getColor(R.color.searchResultTitle));
            titleView.setText(history.getDateForIndex(i).toString());
            // SearchResultViewGroup grouping all queries of this date
            SearchResultViewGroup searchResultViewGroup =
                new SearchResultViewGroup(this.getContext(), history.getEntriesForIndex(i));
            // Put views together
            searchResultLayout.addView(titleView);
            searchResultLayout.addView(searchResultViewGroup);
        }
    }

    /**
     * Create a new SearchResultViewGroup for a given {@code SearchPanelType}
     * 
     * @param context
     * @param searchType
     */
    private void addSearchType(Context context, SearchEngine.Type searchType) {
        if (!mActiveSearchTypes.contains(searchType)) {
            // Add name in Title Bar
            TextView titleView = new TextView(context);
            titleView.setText(searchType.getTitle());
            titleView.setTextSize(TITLE_TEXT_SIZE);
            LayoutParams titleParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            titleParams.setMarginEnd(MARGIN_BELOW_TITLE);
            titleView.setLayoutParams(titleParams);
            mTitleBar.addView(titleView);

            ScrollView scrollView = new SwipeableScrollView(context);

            // Create & Add Views
            SearchResultViewGroup searchResultViewGroup = new SearchResultViewGroup(context);
            LayoutParams searchParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            searchParams.setMargins(0, 0, 0, MARGIN_BELOW_SEARCHVIEWGROUP);
            searchResultViewGroup.setLayoutParams(searchParams);
            SearchOnlineButton searchOnlineButton = new SearchOnlineButton(context);
            scrollView.addView(searchResultViewGroup);
            scrollView.addView(searchOnlineButton);
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
     * GestureListener listening for horizontal swipes.
     * 
     * @author jfperren
     */
    private final class HorizontalGestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "Analysing Gesture");
            boolean result = false;

            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if ((Math.abs(diffX) > SWIPE_THRESHOLD) && (Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD)) {
                    if (diffX > 0) {
                        SearchLayout.this.onSwipeRight();
                    } else {
                        SearchLayout.this.onSwipeLeft();
                    }
                    result = true;
                }
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
            this.setVerticalScrollBarEnabled(false);
            super.addView(mLayout);

            mGestureDetector = new GestureDetector(this.getContext(), new HorizontalGestureListener());
        }

        @Override
        public void addView(View child) {
            mLayout.addView(child);
        }

        @Override
        public void removeViewAt(int index) {
            mLayout.removeViewAt(index);
        }

        @Override
        public void removeAllViews() {
            mLayout.removeAllViews();
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            this.getChildAt(0).onTouchEvent(ev);

            if (mGestureDetector.onTouchEvent(ev)) {
                return true;
            } else {
                // If not scrolling vertically (more y than x), don't hijack
                // the event.
                return super.onTouchEvent(ev);
            }
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (!this.onTouchEvent(ev)) {
                return super.onInterceptTouchEvent(ev);
            }
            return false;
        }
    };

    private final class SearchOnlineButton extends Button {
        public SearchOnlineButton(Context context) {
            super(context);

            this.setBackgroundResource(R.drawable.view_group_background);
            this.setText("Search on SmartMap");
            this.setTextSize(20f);
            this.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // open friendsactivity

                }
            });
        }
    }
}
