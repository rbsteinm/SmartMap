package ch.epfl.smartmap.cache;

import java.util.List;

import android.util.LongSparseArray;

/**
 * A list of user IDs
 * 
 * @author ritterni
 */
public interface UserList {

	/**
	 * @return The name of the list
	 */
	String getListName();

	/**
	 * Renames the list
	 * 
	 * @param newName
	 *            The new name of the list
	 */
	void setListName(String newName);

	/**
	 * Returns the list's ID for storing/loading purposes. Only gets a value
	 * when the UserList is stored.
	 * 
	 * @return The ID
	 */
	long getID();

	/**
	 * Sets the list's ID
	 * 
	 * @param id
	 *            The ID
	 */
	void setID(long id);

	/**
	 * Adds a user to the list
	 * 
	 * @param id
	 *            The user's ID
	 */
	void addUser(long id);

	/**
	 * Removes a user from the list
	 * 
	 * @param id
	 *            The user's ID
	 */
	void removeUser(long id);

	/**
	 * @return The whole list of IDs
	 */
	List<Long> getList();

	/**
	 * Given a SparseArray of Users (mapped to their ID), returns a filtered
	 * list of Users
	 * 
	 * @param friends
	 *            The array of Users
	 * @return The list of users sorted alphabetically
	 */
	List<User> getUserList(LongSparseArray<User> friends);
}