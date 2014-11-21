package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Displayable;

/**
 * Layout that contains different SearchResultViews that can be dynamically
 * added/removed.
 * 
 * @param <T>
 *            Type of Search results
 * @author jfperren
 */
public class SearchResultViewGroup extends LinearLayout {

    /**
     * Visual state of a ViewGroup
     * 
     * @author jfperren
     */
    private enum State {
        MINIMIZED,
        EXPANDED,
        MAX,
        EMPTY;
    }

    private static final String TAG = "SEARCH_RESULT_VIEW_GROUP";

    private static final int ITEMS_PER_PAGE = 10;

    private int mCurrentItemNb;

    private List<Displayable> mResults;
    private final Button mMoreResultsButton;
    private final TextView mEmptyListTextView;
    private State mState;

    /**
     * Constructor
     * 
     * @param context
     *            Context this View lives in
     * @param results
     *            List of results that will be displayed
     * @param name
     *            The name of this SearchResultViewGroup
     */
    public SearchResultViewGroup(Context context, List<Displayable> results) {
        super(context);
        // Layout parameters
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
        this.setOrientation(VERTICAL);
        this.setBackgroundResource(R.drawable.view_group_background);
        // Create button and TextView to display when list is empty
        mMoreResultsButton = new MoreResultsButton(context, this);
        mEmptyListTextView = new TextView(context);
        mEmptyListTextView.setText(R.string.no_search_result);
        // Sets list and displayMinimized
        this.setResultList(results);
    }

    /**
     * Constructor
     * 
     * @param context
     *            Context this View lives in
     * @param name
     *            The name of this SearchResultViewGroup
     */
    public SearchResultViewGroup(Context context) {
        this(context, new ArrayList<Displayable>());
    }

    /**
     * @return True if there is no result to display
     */
    public boolean isEmpty() {
        return mResults.isEmpty();
    }

    /**
     * Display the searchResultViewGroup in MINIMIZED mode
     */
    public void displayMinimized() {
        mCurrentItemNb = 0;
        this.removeAllViews();
        if (mResults.isEmpty()) {
            this.setBackgroundResource(0);
            this.addView(mEmptyListTextView);
            mState = State.EMPTY;
        } else {
            this.setBackgroundResource(R.drawable.view_group_background);
            this.addMoreViews();
        }
    }

    /**
     * Extend the searchResultViewGroup if there are more results to show
     */
    public void showMoreResults() {
        if (mState == State.EMPTY) {
            assert false : "Cannot expand an empty SearchResultViewGroup";
        } else if (mState == State.MAX) {
            assert false : "Cannot expand a SearchResultViewGroup that is already fully expanded";
        } else {
            this.addMoreViews();
        }
    }

    private void addMoreViews() {
        this.removeView(mMoreResultsButton);
        // Computes the number of items to add
        int newItemsNb = Math.min(ITEMS_PER_PAGE, mResults.size() - mCurrentItemNb);
        // Add SearchResultViews
        for (int i = mCurrentItemNb; i < (mCurrentItemNb + newItemsNb); i++) {
            Log.d(TAG, "add" + mResults.get(i).getName());
            this.addView(new SearchResultView(this.getContext(), mResults.get(i)));
            this.addView(new Divider(this.getContext()));
        }
        // Removes last Divider
        // this.removeViewAt(this.getChildCount() - 1);

        mCurrentItemNb += newItemsNb;
        // Sets the new visual state
        if (mCurrentItemNb == mResults.size()) {
            mState = State.MAX;
        } else {
            this.addView(mMoreResultsButton);
            mState = State.EXPANDED;
        }
    }

    /**
     * Sets a new list of results
     * 
     * @param newResultList
     *            New list of results
     */
    public void setResultList(List<Displayable> newResultList) {
        mResults = new ArrayList<Displayable>(newResultList);
        this.displayMinimized();
    }

    /**
     * Button showing more Search results when clicked
     * 
     * @author jfperren
     */
    private static class MoreResultsButton extends Button {
        public MoreResultsButton(Context context, final SearchResultViewGroup searchResultViewGroup) {
            super(context);
            this.setText(R.string.more_results);
            this.setBackgroundResource(0);
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchResultViewGroup.showMoreResults();
                }
            });
        }
    }

    /**
     * Horizontal bar separating two different search results.
     * 
     * @author jfperren
     */
    private static class Divider extends LinearLayout {
        private static final int LEFT_PADDING = 10;
        private static final int RIGHT_PADDING = 10;

        public Divider(Context context) {
            super(context);
            this.setPadding(LEFT_PADDING, 0, RIGHT_PADDING, 0);
            this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
            this.setBackgroundResource(R.color.searchResultShadow);
        }
    }
}
