package ch.epfl.smartmap.test.gui;

import android.test.AndroidTestCase;
import android.widget.TextView;
import ch.epfl.smartmap.gui.EventViewHolder;

/**
 * @author SpicyCH
 */
public class EventViewHolderTest extends AndroidTestCase {

    EventViewHolder eventViewHolder;
    TextView v;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        eventViewHolder = new EventViewHolder();

        v = new TextView(this.getContext());
        v.setId(17);
    }

    public void testInit() {
        assertTrue(true);
    }

    public void testSetAndGetDates() {
        eventViewHolder.setDatesTextView(v);

        assertEquals(v, eventViewHolder.getDatesTextView());
    }

    public void testSetAndGetEventId() {
        eventViewHolder.setEventId(7L);

        assertEquals(7L, eventViewHolder.getEventId());
    }

    public void testSetAndGetEventName() {
        eventViewHolder.setEventNameTextView(v);

        assertEquals(v, eventViewHolder.getEventNameTextView());
    }

    public void testSetAndGetPlaceName() {
        eventViewHolder.setPlaceNameTextView(v);

        assertEquals(v, eventViewHolder.getPlaceNameTextView());
    }
}
