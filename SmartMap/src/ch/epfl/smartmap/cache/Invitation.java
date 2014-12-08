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
    int DECLINED = 3;

    int FRIEND_INVITATION = 0;
    int EVENT_INVITATION = 1;
    int ACCEPTED_FRIEND_INVITATION = 2;

    /**
     * @return invitation's id
     */
    long getId();

    /**
     * @return immutable copy of this invitation
     */
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
    void update(ImmutableInvitation invitation);
}
