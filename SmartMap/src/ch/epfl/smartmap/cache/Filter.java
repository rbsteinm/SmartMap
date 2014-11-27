package ch.epfl.smartmap.cache;

import java.util.List;

import android.util.LongSparseArray;

/**
 * A list of user IDs
 * 
 * @author ritterni
 */
public interface Filter extends Displayable {

	/**
	 * Adds a user to the list
	 * 
	 * @param id
	 *            The user's ID
	 */
	void addUser(long id);

	/**
	 * Returns the list's ID for storing/loading purposes. Only gets a value
	 * when the UserList is stored.
	 * 
	 * @return The ID
	 */
	@Override
	long getID();

	/**
	 * @return The whole list of IDs
	 */
	List<Long> getList();

	/**
	 * @return The name of the list
	 */
	String getListName();

	/**
	 * Given a SparseArray of Users (mapped to their ID), returns a filtered
	 * list of Users
	 * 
	 * @param friends
	 *            The array of Users
	 * @return The list of users sorted alphabetically
	 */
	List<User> getUserList(LongSparseArray<User> friends);

	/**
	 * Removes a user from the list
	 * 
	 * @param id
	 *            The user's ID
	 */
	void removeUser(long id);

	/**
	 * Sets the list's ID
	 * 
	 * @param id
	 *            The ID
	 */
	void setID(long id);

	/**
	 * Renames the list
	 * 
	 * @param newName
	 *            The new name of the list
	 */
	void setListName(String newName);
}