/**
 * 
 */
package ch.epfl.smartmap.gui;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.MockDB;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
        HISTORY, QUICK, USERS, EVENTS, TAGS, GROUPS
    }

    private static final String TAG = "SEARCH_RESULT_SWIPEABLE_CONTAINER";

    private LinkedList<ScrollView> mScrollViews;
    private LinkedList<SearchResultViewGroup> mSearchResultViewGroups;
    private LinkedList<String> mSearchResultTitles;
    private int mIndex;

    public SearchResultSwipeableContainer(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setOrientation(HORIZONTAL);
        this.setLayoutParams(new LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mSearchResultTitles = new LinkedList<String>(Arrays.asList("History",
            "Quick Search", "Users", "Events", "Tags", "Groups"));
        mScrollViews = new LinkedList<ScrollView>();
        mSearchResultViewGroups = new LinkedList<SearchResultViewGroup>();

        for (SearchPanelType type : SearchPanelType.values()) {
            createSearchResultLayout(context, type);
        }
        this.addView(mScrollViews.get(0));
    }

    private SearchResultViewGroup getCurrentViewGroup() {
        return mSearchResultViewGroups.get(mIndex);
    }

    public void setResultList(List<Friend> newSearchResults) {
        getCurrentViewGroup().setResultList(newSearchResults);
    }

    private boolean onSwipeLeft() {
        // Get current and next ScrollViews (circularily)
        ScrollView currentScrollView = mScrollViews.get(mIndex);
        mIndex = (mIndex == mScrollViews.size() - 1) ? 0 : mIndex + 1;
        ScrollView nextScrollView = mScrollViews.get(mIndex);
        
        this.addView(nextScrollView);
        nextScrollView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.swipe_left_in));
        currentScrollView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.swipe_left_out));
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
        // Parent ScrollView
        ScrollView scrollView = new ScrollView(context) {
            private GestureDetector gestureDetector = new GestureDetector(
                getContext(), new HorizontalGestureListener());

            @Override
            public boolean onTouchEvent(MotionEvent ev) {
                if (gestureDetector.onTouchEvent(ev)) {
                    return true;
                } else {
                    // If not scrolling vertically (more y than x), don't hijack the event.
                    return super.onTouchEvent(ev);
                }
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return true;
            }
        };
        scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
        scrollView.setBackgroundResource(R.color.bottomSliderBackground);
        scrollView.setScrollBarSize(0);
        scrollView.setPadding(20, 20, 20, 0);
        // Layout contained in ScrollView
        LinearLayout searchResultLayout = new LinearLayout(context);
        searchResultLayout.setLayoutParams(new LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        searchResultLayout.setOrientation(VERTICAL);
        searchResultLayout.setPadding(0, 3, 0, 20);
        // TextView displaying Name
        TextView titleView = new TextView(context);
        titleView.setText(mSearchResultTitles.get(searchPanelType.ordinal()));
        // SearchResultViewGroup
        SearchResultViewGroup searchResultViewGroup = new SearchResultViewGroup(
            context, MockDB.FRIENDS_LIST);
        // Put views together
        searchResultLayout.addView(titleView);
        searchResultLayout.addView(searchResultViewGroup);
        scrollView.addView(searchResultLayout);
        // Add in Lists
        mScrollViews.add(scrollView);
        mSearchResultViewGroups.add(searchResultViewGroup);
    }

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
