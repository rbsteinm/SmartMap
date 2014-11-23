package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Friend;

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
	private enum State {
		MINIMIZED, EXPANDED, MAX
	}

	@SuppressWarnings("unused")
	private static final String TAG = "SEARCH_RESULT_VIEW_GROUP";

	private static final int ITEMS_PER_PAGE = 10;
	private int mCurrentItemNb;

	private final Context mContext;
	private final List<Friend> mList;
	private final Button mMoreResultsButton;
	private State mState;

	public SearchResultViewGroup(Context context, List<Friend> friendsList) {
		super(context);

		this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
		    LayoutParams.MATCH_PARENT));
		this.setOrientation(VERTICAL);
		this.setBackgroundResource(R.drawable.view_group_background);
		mContext = context;
		mList = new ArrayList<Friend>(friendsList);

		mMoreResultsButton = new MoreResultsButton(context, this);

		displayMinimized();
	}

	public SearchResultViewGroup(Context context) {
		this(context, new ArrayList<Friend>());
	}

	public void displayMinimized() {
		this.removeAllViews();
		mCurrentItemNb = Math.min(ITEMS_PER_PAGE, mList.size());

		for (int i = 0; i < mCurrentItemNb; i++) {
			this.addView(SearchResultViewFactory.getSearchResultView(mContext, mList.get(i)));
			this.addView(new Divider(mContext));
		}

		if (mCurrentItemNb == mList.size()) {
			mState = State.MAX;
		} else {
			this.addView(mMoreResultsButton);
			mState = State.MINIMIZED;
		}
	}

	public void showMoreResults() {
		if (mState != State.MAX) {
			mState = State.EXPANDED;
			int newItemsNb = Math.min(ITEMS_PER_PAGE, mList.size() - mCurrentItemNb);
			this.removeViewAt(this.getChildCount() - 1);
			for (int i = mCurrentItemNb; i < mCurrentItemNb + newItemsNb; i++) {
				this.addView(SearchResultViewFactory.getSearchResultView(mContext, mList.get(i)));
				this.addView(new Divider(mContext));
			}
			mCurrentItemNb += newItemsNb;

			if (mCurrentItemNb == mList.size()) {
				mState = State.MAX;
			} else {
				this.addView(mMoreResultsButton);
				mState = State.EXPANDED;
			}
		}
	}

	public void setResultList(List<Friend> newResultList) {
		mList.clear();
		mList.addAll(newResultList);
		displayMinimized();
	}

	/**
	 * Button showing more Search results when clicked
	 * 
	 * @author jfperren
	 */
	private static class MoreResultsButton extends Button {
		public MoreResultsButton(Context context, final SearchResultViewGroup searchResultViewGroup) {
			super(context);
			this.setText("See more");
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
