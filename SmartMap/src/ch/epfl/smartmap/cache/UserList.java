package ch.epfl.smartmap.cache;

import java.util.List;

/**
 * A list of user IDs
 * @author ritterni
 */
public interface UserList {
	
	/**
	 * @return The name of the list
	 */
	String getListName();
	
	/**
	 * Renames the list
     * @param newName The new name of the list
     */
    void setListName(String newName);	
	
	/**
	 * Adds a user to the list
	 * @param id The user's ID
	 */
	void addUser(int id);
	
	/**
	 * Removes a user from the list
	 * @param id The user's ID
	 */
	void removeUser(int id);
	
	/**
	 * @return The whole list of IDs
	 */
	List<Integer> getList();
	
	/**
	 * @return The whole list of users sorted alphabetically
	 */
	List<User> getUserList();
}