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
	long getID();

	/**
	 * @return user's name
	 */
	String getUserName();

	/**
	 * @return user's id
	 */
	long getUserId();

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
	 * @return int representing invitation's status
	 */
	int getStatus();

	/**
	 * Sets status of invitation
	 * 
	 * @param Status
	 *            the int representing invitation status
	 */
	void setStatus(int status);

}
