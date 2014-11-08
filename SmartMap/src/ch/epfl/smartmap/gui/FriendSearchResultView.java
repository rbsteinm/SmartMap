package ch.epfl.smartmap.gui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Friend;

/**
 * Represents a {@code SearchResultView} that displays a {@code Friend}
 * 
 * @author jfperren
 */
public class FriendSearchResultView extends SearchResultView {

    private final static String TAG = "FriendSearchResultView";
    private final static String AUDIT_TAG = "AuditError : " + TAG;

    private final static int PHOTO_RIGHT_MARGIN = 40;
    private final static int NAME_VIEW_BOTTOM_PADDING = 5;
    private final static int PHOTO_SIZE = 150;
    private final static float NAME_VIEW_TEXT_SIZE = 17f;

    private final Friend mFriend;
    private final ImageView mPhotoView;
    private final TextView mNameView;
    private final TextView mLastConnectionView;

    private final LinearLayout mInfoLayout;

    /**
     * @param context
     * @param attrs
     */
    public FriendSearchResultView(Context context, Friend friend) {
        super(context);

        mFriend = friend;

        // Creates mPhotoView
        mPhotoView = new ImageView(context);
        mPhotoView.setAdjustViewBounds(true);
        mPhotoView.setImageResource(R.drawable.default_user_icon);
        LayoutParams mPhotoViewLayoutParams = new LayoutParams(PHOTO_SIZE,
            PHOTO_SIZE);
        mPhotoViewLayoutParams.setMargins(0, 0, PHOTO_RIGHT_MARGIN, 0);
        mPhotoView.setLayoutParams(mPhotoViewLayoutParams);
        mPhotoView.setScaleType(ScaleType.FIT_XY);

        // Creates mNameView
        mNameView = new TextView(context);
        mNameView.setText(mFriend.getName());
        mNameView.setTextSize(NAME_VIEW_TEXT_SIZE);
        mNameView.setTypeface(null, Typeface.BOLD);
        mNameView.setPadding(0, 0, 0, NAME_VIEW_BOTTOM_PADDING);

        // Creates mLastConnectionView
        mLastConnectionView = new TextView(context);
        mLastConnectionView.setText("last seen "
            + friend.getLastSeen().getTime().toString() + ".");
        mLastConnectionView.setTextColor(getResources().getColor(
            R.color.lastSeenConnectionTextColor));

        // Create mInfoLayout
        mInfoLayout = new LinearLayout(context);
        mInfoLayout.setOrientation(VERTICAL);

        // Adds all view into the ResultView
        getMainLayout().addView(mPhotoView);
        mInfoLayout.addView(mNameView);
        mInfoLayout.addView(mLastConnectionView);
        mInfoLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT));
        getMainLayout().addView(mInfoLayout);
    }

    /**
     * This method should be called when the infos on the user changes. Typically called by a FriendObserver.
     */
    @Override
    public void update() {
        // TODO
        // 1) Update last connection info
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
        // auditErrors += mFriend.auditErrors(depth - 1);

        if (mNameView.getText().equals("")) {
            auditErrors++;
            Log.e(AUDIT_TAG, "mNameView contains empty string");
        }
        if (this.isShown()
            && !(getMainLayout().isShown() && mNameView.isShown()
                && mInfoLayout.isShown() && mLastConnectionView.isShown() && mPhotoView
                    .isShown())) {
            auditErrors++;
            Log.e(AUDIT_TAG,
                "all components are not visible when they should be");
        }

        return auditErrors;
    }
}