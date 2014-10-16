package ch.epfl.smartmap.cache;

/**
 * A list of users (typically a friendlist/custom group)
 * @author ritterni
 */
public interface UserList {
	
	/**
	 * @return The name of the list
	 */
	String getListName();
	
	
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
	 * Gets the nth user on the list
	 * @param n The user's index
	 * @return The user's ID
	 */
	int getID(int n);
}