package ch.epfl.smartmap.gui;

import android.widget.TextView;
import ch.epfl.smartmap.cache.Event;

/**
 * <p>
 * We store our TextViews in a ViewHolder to avoid useless findViewbyId() calls.
 * </p>
 * <p>
 * Used by {@linkplain ch.epfl.smartmap.gui.EventsListItemAdapter}
 * 
 * @author SpicyCH
 */
public class EventViewHolder {
    private Event mEvent;
    private TextView mNameTextView;
    private TextView mStartTextView;
    private TextView mEndTextView;

    public void setEvent(Event e) {
        mEvent = e;
    }

    public void setNameTextView(TextView v) {
        mNameTextView = v;
    }

    public void setStarTextView(TextView v) {
        mStartTextView = v;
    }

    public void setEndTextView(TextView v) {
        mEndTextView = v;
    }

    public TextView getNameTextView() {
        return mNameTextView;
    }

    public TextView getStartTextView() {
        return mStartTextView;
    }

    public TextView getEndTextView() {
        return mEndTextView;
    }

    public Event getEvent() {
        return mEvent;
    }
}
