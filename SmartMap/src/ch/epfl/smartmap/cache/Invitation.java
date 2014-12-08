package ch.epfl.smartmap.cache;

import android.content.Intent;

/**
 * Describes a generic invitation of the app
 * 
 * @author agpmilli
 */
public interface Invitation {

    /**
     * int representing invitation status
     */
    int UNREAD = 0;
    int READ = 1;
    int ACCEPTED = 2;
    int REFUSED = 3;

    /**
     * @return invitation's id
     */
    long getId();

    ImmutableInvitation getImmutableCopy();

    /**
     * @return invitation's intent
     */
    Intent getIntent();

    /**
     * @return int representing invitation's status
     */
    int getStatus();

    /**
     * @return invitation's text
     */
    String getText();

    /**
     * @return invitation's title
     */
    String getTitle();

    /**
     * @return user
     */
    User getUser();

    void update(ImmutableInvitation invitation);
}
