package ch.epfl.smartmap.gui;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * <p>
 * We store our Views in a ViewHolder to avoid useless findViewbyId() calls.
 * </p>
 * <p>
 *
 * @author agpmilli
 */
public class ParticipantViewHolder {
    private long mUserId;
    private TextView mName;
    private ImageView mImage;

    /**
     * @return the stored <code>ImageView</code>.
     *
     */
    public ImageView getImageView() {
        return mImage;
    }

    /**
     * @return the stored <code>TextView</code> for the name.
     */
    public TextView getNameTextView() {
        return mName;
    }

    /**
     *
     * @return the user id-
     *
     * @author SpicyCH
     */
    public long getUserId() {
        return mUserId;
    }

    /**
     *
     * @param v
     *            the <code>ImageView</code> to set.
     *
     */
    public void setImageView(ImageView v) {
        mImage = v;
    }

    /**
     * @param v
     *            the <code>TextView</code> to set.
     *
     */
    public void setNameTextView(TextView v) {
        mName = v;
    }

    /**
     *
     * @param id
     *            the id to set.
     *
     * @author SpicyCH
     */
    public void setUserId(Long id) {
        mUserId = id;
    }
}
