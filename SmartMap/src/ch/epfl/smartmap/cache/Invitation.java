package ch.epfl.smartmap.cache;

import android.content.Intent;
import android.graphics.Bitmap;

/**
 * Describes a generic invitation of the app
 * 
 * @author agpmilli
 */
public interface Invitation {
    /**
     * @return invitation's id
     */
    long getId();

    Bitmap getImage();

    /**
     * @return invitation's intent
     */
    Intent getIntent();

    /**
     * @return invitation's text
     */
    String getText();

    /**
     * @return invitation's title
     */
    String getTitle();

    /**
     * @return invitation's creator
     */
    User getUser();

    /**
     * @return True if invitation has been read
     */
    boolean isRead();

    /**
     * Set invitation's intent
     * 
     * @param intent
     *            the invitation's intent
     */
    void setIntent(Intent intent);

    /**
     * Sets whether or not the user has read the invitation
     * 
     * @param isRead
     *            True if invitation has been read
     */
    void setRead(boolean isRead);

    /**
     * Set invitation's text
     * 
     * @param text
     *            the invitation's text
     */
    void setText(String text);

    /**
     * Set invitation's title
     * 
     * @param title
     *            the invitation's title
     */
    void setTitle(String title);

    /**
     * Set invitation's creator
     * 
     * @param user
     *            the invitation's creator
     */
    void setUser(User user);

}
