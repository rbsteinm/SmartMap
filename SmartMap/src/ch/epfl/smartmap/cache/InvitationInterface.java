package ch.epfl.smartmap.cache;

import android.content.Intent;
import android.graphics.Bitmap;

/**
 * Describes a generic invitation of the app
 * 
 * @author agpmilli
 */
public interface InvitationInterface {

    /**
     * @return event
     */
    Event getEvent();

    /**
     * @return invitation's id
     */
    long getId();

    /**
     * @return invitation's image icon
     */
    Bitmap getImage();

    /**
     * @return immutable copy of this invitation
     */
    InvitationContainer getContainerCopy();

    /**
     * @return invitation's intent
     */
    Intent getIntent();

    /**
     * @return int representing invitation's status
     */
    int getStatus();

    /**
     * @return invitation's subtitle
     */
    String getSubtitle();

    /**
     * @return invitation's timestamp
     */
    long getTimeStamp();

    /**
     * @return invitation's title
     */
    String getTitle();

    /**
     * @return invitation's type
     */
    int getType();

    /**
     * @return user
     */
    User getUser();

    /**
     * @param invitation
     *            the invitation to update
     */
    boolean update(InvitationContainer invitation);
}
