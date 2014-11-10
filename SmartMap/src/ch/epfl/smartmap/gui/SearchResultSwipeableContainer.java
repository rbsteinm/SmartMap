/**
 * 
 */
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
import ch.epfl.smartmap.cache.MockDB;
import ch.epfl.smartmap.cache.SearchEngine;

/**
 * @author jfperren
 *
 */
public class SearchResultSwipeableContainer extends LinearLayout {

    /**
     * Type of Search Filter
     * 
     * @author jfperren
     */
    private enum SearchPanelType {
        HISTORY("History"), QUICK("All"), USERS("Users"), EVENTS("Events"), TAGS(
            "Tags"), GROUPS("groups");

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

    private HashMap<SearchPanelType, ScrollView> mScrollViews;
    private HashMap<SearchPanelType, SearchResultViewGroup> mSearchResultViewGroups;

    private List<Friend> mCurrentSearchResults;
    private SearchPanelType mCurrentSearchType;
    private SearchEngine mSearchEngine;

    public SearchResultSwipeableContainer(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setOrientation(HORIZONTAL);
        this.setLayoutParams(new LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mScrollViews = new HashMap<SearchPanelType, ScrollView>();
        mSearchResultViewGroups = new HashMap<SearchPanelType, SearchResultViewGroup>();
        mCurrentSearchResults = new ArrayList<Friend>();

        for (SearchPanelType type : SearchPanelType.values()) {
            Log.d(TAG, "Create SearchPanel : " + type.ordinal());
            createSearchResultLayout(context, type);
        }

        mCurrentSearchType = FIRST_SEARCH_PANEL_TYPE;
        this.addView(mScrollViews.get(mCurrentSearchType));
    }

    public void setSearchEngine(SearchEngine searchEngine) {
        mSearchEngine = searchEngine;
    }

    private SearchResultViewGroup getCurrentViewGroup() {
        return mSearchResultViewGroups.get(mCurrentSearchType);
    }

    public void setResultList(List<Friend> newSearchResults) {
        mCurrentSearchResults.clear();
        mCurrentSearchResults.addAll(newSearchResults);
        if (getCurrentViewGroup() != null) {
            getCurrentViewGroup().setResultList(mCurrentSearchResults);
        }
    }

    private boolean onSwipeLeft() {
        // Get current and next ScrollViews (circularily)
        ScrollView currentScrollView = mScrollViews.get(mCurrentSearchType);
        mCurrentSearchType = (mCurrentSearchType.ordinal() == mScrollViews
            .size() - 1) ? SearchPanelType.values()[0] : SearchPanelType
            .values()[mCurrentSearchType.ordinal() + 1];
        ScrollView nextScrollView = mScrollViews.get(mCurrentSearchType);
        nextScrollView.scrollTo(0, 0);
        SearchResultViewGroup nextSearchResultViewGroup = mSearchResultViewGroups
            .get(mCurrentSearchType);

        nextSearchResultViewGroup.displayMinimized();
        nextSearchResultViewGroup.setResultList(mCurrentSearchResults);

        this.addView(nextScrollView);
        nextScrollView.startAnimation(AnimationUtils.loadAnimation(
            getContext(), R.anim.swipe_left_in));
        currentScrollView.startAnimation(AnimationUtils.loadAnimation(
            getContext(), R.anim.swipe_left_out));
        this.removeViewAt(0);

        // Notify that the MotionEvent has been handled
        return true;
    }

    private boolean onSwipeRight() {
        // Notify that the MotionEvent has been handled
        return true;
    }

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
        scrollView.setBackgroundResource(R.color.bottomSliderBackground);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setPadding(20, 0, 20, 0);

        // Layout contained in ScrollView
        LinearLayout searchResultLayout = new LinearLayout(context);
        searchResultLayout.setLayoutParams(new LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        searchResultLayout.setOrientation(VERTICAL);
        searchResultLayout.setPadding(0, 3, 0, 20);

        if (searchPanelType == SearchPanelType.HISTORY) {

            // // History Panel
            // History history = mSearchEngine.getHistory();
            //
            // for (int i = 0; i < history.nbOfDates(); i++) {
            // // TextView displaying Date
            // TextView titleView = new TextView(context);
            // titleView.setTextSize(15f);
            // titleView.setTextColor(getResources().getColor(
            // R.color.searchResultTitle));
            // titleView.setText(history.getDateForIndex(i).toString());
            // // SearchResultViewGroup grouping all queries of this date
            // SearchResultViewGroup searchResultViewGroup = new SearchResultViewGroup(
            // context, history.getEntriesForIndex(i));// Put views together
            // searchResultLayout.addView(titleView);
            // searchResultLayout.addView(searchResultViewGroup);
            // }

        } else {
            // Normal Search Panel
            // TextView displaying Name
            TextView titleView = new TextView(context);
            titleView.setTextSize(15f);
            titleView.setTextColor(getResources().getColor(
                R.color.searchResultTitle));
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
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD
                        && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            result = onSwipeRight();
                        } else {
                            result = onSwipeLeft();
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

}
