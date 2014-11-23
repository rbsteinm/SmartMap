package ch.epfl.smartmap.gui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.MainActivity;
import ch.epfl.smartmap.cache.Friend;

/**
 * Represents a {@code SearchResultView} that displays a {@code Friend}
 * 
 * @author jfperren
 */
public class FriendSearchResultView extends SearchResultView {

    private final static String TAG = "FriendSearchResultView";
    @SuppressWarnings("unused")
    private final static String AUDIT_TAG = "AuditError : " + TAG;

    private final static int NAME_VIEW_BOTTOM_PADDING = 5;
    private final static float NAME_VIEW_TEXT_SIZE = 17f;
    private static final int CLICK_DISTANCE_THRESHHOLD = 10;

    private final Friend mFriend;
    private final LinearLayout mInfoLayout;
    private final int mImageResource;

    /**
     * @param context
     * @param attrs
     */
    public FriendSearchResultView(Context context, Friend friend) {
        super(context);

        mFriend = friend;
        mImageResource = R.drawable.ic_default_user;

        // Creates mNameView
        TextView nameView = new TextView(context);
        nameView.setText(mFriend.getName());
        nameView.setTextSize(NAME_VIEW_TEXT_SIZE);
        nameView.setTypeface(null, Typeface.BOLD);
        nameView.setPadding(0, 0, 0, NAME_VIEW_BOTTOM_PADDING);

        // Creates mLastConnectionView
        TextView lastConnectionView = new TextView(context);
        lastConnectionView.setText("last seen " + friend.getLastSeen().getTime().toString() + ".");
        lastConnectionView.setTextColor(this.getResources().getColor(R.color.lastSeenConnectionTextColor));

        // Create mInfoLayout and add everything
        mInfoLayout = new LinearLayout(context);
        mInfoLayout.setOrientation(VERTICAL);
        mInfoLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mInfoLayout.addView(nameView);
        mInfoLayout.addView(lastConnectionView);

        // Add view on the parent class
        this.initViews();
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
        // TODO : Decomment when auditErrors coded for other classes
        if (depth == 0) {
            return 0;
        }

        int auditErrors = 0;
        // auditErrors += mFriend.auditErrors(depth - 1);

        return auditErrors;
    }

    @Override
    public int getImageResource() {
        return mImageResource;
    }

    @Override
    public ViewGroup getInfosLayout() {
        return mInfoLayout;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.gui.SearchResultView#getOnClickListener()
     */
    @Override
    public OnTouchListener getOnTouchListener(final SearchResultView v) {
        return new OnTouchListener() {
            private float startX;
            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d(TAG, "Action DOWN");
                    startX = ev.getAxisValue(MotionEvent.AXIS_X);
                    startY = ev.getAxisValue(MotionEvent.AXIS_Y);
                    v.setBackgroundColor(FriendSearchResultView.this.getResources().getColor(
                        R.color.searchResultOnSelect));

                } else if (ev.getAction() == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "Action UP");
                    float endX = ev.getAxisValue(MotionEvent.AXIS_X);
                    float endY = ev.getAxisValue(MotionEvent.AXIS_Y);

                    if (Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2)) < CLICK_DISTANCE_THRESHHOLD) {
                        ((MainActivity) FriendSearchResultView.this.getContext()).performQuery(mFriend);
                    }
                    v.setBackgroundColor(FriendSearchResultView.this.getResources().getColor(
                        R.color.searchResultBackground));
                } else if (ev.getAction() == MotionEvent.ACTION_CANCEL) {
                    Log.d(TAG, "Action CANCEL");
                    v.setBackgroundColor(FriendSearchResultView.this.getResources().getColor(
                        R.color.searchResultBackground));
                }
                return true;
            }
        };
    }
}
