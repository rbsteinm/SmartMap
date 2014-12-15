package ch.epfl.smartmap.gui;

import android.widget.TextView;

/**
 * <p>
 * We store our TextViews in a ViewHolder to avoid useless findViewbyId() calls.
 * </p>
 * <p>
 * Used by {@linkplain ch.epfl.smartmap.gui.EventsListItemAdapter}
 * </p>
 * 
 * @author SpicyCH
 */
public class EventViewHolder {
    private long mEvent;
    private TextView mEventName;
    private TextView mPlaceName;
    private TextView mDates;

    public TextView getDatesTextView() {
        return mDates;
    }

    public long getEventId() {
        return mEvent;
    }

    public TextView getEventNameTextView() {
        return mEventName;
    }

    public TextView getPlaceNameTextView() {
        return mPlaceName;
    }

    public void setDatesTextView(TextView v) {
        mDates = v;
    }

    public void setEventId(Long id) {
        mEvent = id;
    }

    public void setEventNameTextView(TextView v) {
        mEventName = v;
    }

    public void setPlaceNameTextView(TextView v) {
        mPlaceName = v;
    }
}
