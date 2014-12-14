package ch.epfl.smartmap.test.gui;

import android.test.AndroidTestCase;
import android.widget.ImageView;
import android.widget.TextView;
import ch.epfl.smartmap.gui.ParticipantViewHolder;

/**
 * @author SpicyCH
 */
public class ParticipantViewHolderTest extends AndroidTestCase {

    ParticipantViewHolder participantViewHolder;
    TextView v;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        participantViewHolder = new ParticipantViewHolder();

        v = new TextView(this.getContext());
        v.setId(17);
    }

    public void testSetAndGetImage() {
        ImageView imgView = new ImageView(this.getContext());
        imgView.setId(20);

        participantViewHolder.setImageView(imgView);

        assertEquals(imgView, participantViewHolder.getImageView());
    }

    public void testSetAndGetName() {
        participantViewHolder.setNameTextView(v);

        assertEquals(v, participantViewHolder.getNameTextView());
    }

    public void testSetAndGetUserId() {
        participantViewHolder.setUserId(22L);

        assertEquals(22L, participantViewHolder.getUserId());
    }
}
