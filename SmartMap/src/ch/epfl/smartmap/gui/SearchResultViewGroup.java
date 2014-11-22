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
 * @author jfperren
 */
public class SearchResultViewGroup extends LinearLayout {

    /**
     * Visual state of a ViewGroup
     * 
     * @author jfperren
     */
    private enum VisualState {
        MINIMIZED,
        EXPANDED,
        MAX,
        EMPTY;
    }

    private static final String TAG = "SEARCH_RESULT_VIEW_GROUP";
    // Margins & Paddings
    private static final int NO_RESULT_VIEW_VERTICAL_PADDING = 150;
    private static final int SEPARATOR_LEFT_PADDING = 10;
    private static final int SEPARATOR_RIGHT_PADDING = 10;
    // Colors
    private static final int SEPARATOR_BACKGROUND_COLOR = R.color.bottomSliderBackground;
    // Text Sizes
    private static final float NO_RESULT_VIEW_TEXT_SIZE = 25f;
    // Others
    private static final int ITEMS_PER_PAGE = 10;

    // Children Views
    private final Button mMoreResultsButton;
    private final TextView mEmptyListTextView;
    // Informations about current state
    private int mCurrentItemNb;
    private List<Displayable> mCurrentResultList;
    private VisualState mCurrentVisualState;

    /**
     * Constructor
     * 
     * @param context
     *            Context this View lives in
     * @param results
     *            List of results that will be displayed
     */
    public SearchResultViewGroup(Context context, List<Displayable> results) {
        super(context);
        // Layout parameters
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
        this.setOrientation(VERTICAL);
        this.setBackgroundResource(R.drawable.div_background);

        // Create button
        mMoreResultsButton = new MoreResultsButton(context, this);
        // Create TextView that needs to be displayed when no result is found
        mEmptyListTextView = new TextView(context);
        mEmptyListTextView.setText(R.string.no_search_result);
        mEmptyListTextView.setTextColor(this.getResources().getColor(R.color.main_blue));
        mEmptyListTextView.setTextSize(NO_RESULT_VIEW_TEXT_SIZE);
        mEmptyListTextView.setPadding(0, NO_RESULT_VIEW_VERTICAL_PADDING, 0, NO_RESULT_VIEW_VERTICAL_PADDING);
        mEmptyListTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT));
        mEmptyListTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);

        // Create all SearchresultViews
        this.setResultList(results);
    }

    /**
     * Constructor with empty list
     * 
     * @param context
     *            Context this View lives in
     */
    public SearchResultViewGroup(Context context) {
        this(context, new ArrayList<Displayable>());
    }

    /**
     * Sets a new list of results
     * 
     * @param newResultList
     *            New list of results
     */
    public void setResultList(List<Displayable> newResultList) {
        mCurrentResultList = new ArrayList<Displayable>(newResultList);
        this.displayMinimized();
    }

    /**
     * @return True if there is no result to display
     */
    public boolean isEmpty() {
        return mCurrentResultList.isEmpty();
    }

    /**
     * Display the searchResultViewGroup in MINIMIZED mode
     */
    public void displayMinimized() {
        mCurrentItemNb = 0;
        this.removeAllViews();
        if (mCurrentResultList.isEmpty()) {
            this.setBackgroundResource(0);
            this.addView(mEmptyListTextView);
            mCurrentVisualState = VisualState.EMPTY;
        } else {
            this.setBackgroundResource(R.drawable.div_background);
            this.addMoreViews();
        }
    }

    /**
     * Extend the searchResultViewGroup if there are more results to show
     */
    private void showMoreResults() {
        if (mCurrentVisualState == VisualState.EMPTY) {
            assert false : "Cannot expand an empty SearchResultViewGroup";
        } else if (mCurrentVisualState == VisualState.MAX) {
            assert false : "Cannot expand a SearchResultViewGroup that is already fully expanded";
        } else {
            this.addMoreViews();
        }
    }

    /**
     * If possible, add {@code ITEMS_PER_PAGE} more {@code SearchResultView}s
     */
    private void addMoreViews() {
        this.removeView(mMoreResultsButton);
        // Computes the number of items to add
        int newItemsNb = Math.min(ITEMS_PER_PAGE, mCurrentResultList.size() - mCurrentItemNb);
        // Add SearchResultViews
        for (int i = mCurrentItemNb; i < (mCurrentItemNb + newItemsNb); i++) {
            Log.d(TAG, "add" + mCurrentResultList.get(i).getName());
            this.addView(new SearchResultView(this.getContext(), mCurrentResultList.get(i)));
            this.addView(new Divider(this.getContext()));
        }

        mCurrentItemNb += newItemsNb;
        // Sets the new visual state
        if (mCurrentItemNb == mCurrentResultList.size()) {
            mCurrentVisualState = VisualState.MAX;
        } else {
            this.addView(mMoreResultsButton);
            mCurrentVisualState = VisualState.EXPANDED;
        }
    }

    /**
     * Checks that the Representation Invariant is not violated.
     * 
     * @param depth
     *            represents how deep the audit check is done (use 1 to check
     *            this object only)
     * @return The number of audit errors in this object
     */
    public int auditErrors(int depth) {
        if (depth == 0) {
            return 0;
        }

        int auditErrors = 0;
        if ((mCurrentVisualState == VisualState.EMPTY) && !this.isEmpty()) {
            Log.e(TAG, "State is Empty but result list isn't");
            auditErrors++;
        }
        if ((mCurrentVisualState != VisualState.EMPTY) && this.isEmpty()) {
            Log.e(TAG, "State is not Empty but result list is");
            auditErrors++;
        }
        if ((this.getChildAt(this.getChildCount() - 1) instanceof Button)
            && (mCurrentVisualState == VisualState.MAX)) {
            Log.e(TAG, "Button is displayed in MAX mode");
            auditErrors++;
        }
        if ((this.getChildAt(this.getChildCount() - 1) instanceof Button)
            && (mCurrentVisualState == VisualState.EMPTY)) {
            Log.e(TAG, "Button is displayed in EMPTY mode");
            auditErrors++;
        }
        if (this.getChildCount() == 0) {
            Log.e(TAG, "A SearchResultViewGroup should always have at least one child");
            auditErrors++;
        }

        return auditErrors;
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
        public Divider(Context context) {
            super(context);
            this.setPadding(SEPARATOR_LEFT_PADDING, 0, SEPARATOR_RIGHT_PADDING, 0);
            this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
            this.setBackgroundResource(SEPARATOR_BACKGROUND_COLOR);
        }
    }
}
