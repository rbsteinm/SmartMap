package ch.epfl.smartmap.cache;

/**
 * Describes a generic user of the app
 * @author ritterni
 */
public interface User {
	
	/**
	 * @return The user's ID
	 */
	int getID();
	
	/**
	 * @return The user's name
	 */
	String getName();
	
	/**
	 * @return The user's phone number
	 */
	String getNumber();
	
	
	/**
	 * @return The user's email address
	 */
	String getEmail();
	
	/**
	 * @return The user's position
	 */
	Point getPosition();
	
	/**
	 * Sets the user's name
	 * @param newName The new name
	 */
	void setName(String newName);	
	
	/**
	 * Sets the user's email
	 * @param newEmail The new email
	 */
	void setEmail(String newEmail);
	
	/**
	 * Sets the user's longitude
	 * @param x The longitude
	 */
	void setX(double x);
	
	/**
	 * Sets the user's latitude
	 * @param y The latitude
	 */
	void setY(double y);
}