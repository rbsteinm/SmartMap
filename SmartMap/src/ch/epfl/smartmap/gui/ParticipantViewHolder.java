package ch.epfl.smartmap.gui;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * <p>
 * We store our TextViews in a ViewHolder to avoid useless findViewbyId() calls.
 * </p>
 * <p>
 * Used by {@linkplain ch.epfl.smartmap.gui.EventsListItemAdapter}
 * 
 * @author agpmilli
 */
public class ParticipantViewHolder {
    private long mUserId;
    private TextView mName;
    private ImageView mImage;

    public ImageView getImageImageView() {
        return mImage;
    }

    public TextView getNameTextView() {
        return mName;
    }

    public long getUserId() {
        return mUserId;
    }

    public void setImageImageView(ImageView v) {
        mImage = v;
    }

    public void setNameTextView(TextView v) {
        mName = v;
    }

    public void setUserId(Long id) {
        mUserId = id;
    }
}
