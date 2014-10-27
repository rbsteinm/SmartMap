package ch.epfl.smartmap.cache;

import java.util.GregorianCalendar;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Describes a generic user of the app
 * 
 * @author ritterni
 */
public interface User {

	/**
	 * @return The user's ID
	 */
	long getID();

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
	 * @return The user's position as a String (e.g. 'Lausanne')
	 */
	String getPositionName();

	/**
	 * @return A user picture to display
	 */
	Bitmap getPicture(Context context);

	/**
	 * @return The date/hour at which the user was last seen
	 */
	GregorianCalendar getLastSeen();

	/**
	 * @return True if the user is online
	 */
	boolean isOnline();

	/**
	 * Sets the user position's name
	 * 
	 * @param posName
	 *            The user's position
	 */
	void setPositionName(String posName);

	/**
	 * Sets the user's name
	 * 
	 * @param newName
	 *            The new name
	 */
	void setName(String newName);

	/**
	 * Sets the user's phone number
	 * 
	 * @param newNumber
	 *            The new phone number
	 */
	void setNumber(String newNumber);

	/**
	 * Sets the user's email
	 * 
	 * @param newEmail
	 *            The new email
	 */
	void setEmail(String newEmail);

	/**
	 * Sets the user's longitude
	 * 
	 * @param x
	 *            The longitude
	 */
	void setX(double x);

	/**
	 * Sets the user's latitude
	 * 
	 * @param y
	 *            The latitude
	 */
	void setY(double y);

	/**
	 * Sets the user's position (x and y)
	 * 
	 * @param p
	 *            The new position
	 */
	void setPosition(Point p);

	/**
	 * Stores a new profile picture for the user
	 * 
	 * @param pic
	 *            The picture as a Bitmap object
	 */
	void setPicture(Bitmap pic, Context context);

	/**
     *
     */
	void deletePicture(Context context);

	/**
	 * 
	 * 
	 * @param date
	 *            The date/hour at which the user was last seen
	 */
	void setLastSeen(GregorianCalendar date);

	/**
	 * Sets whether or not the user is online
	 */
	void setOnline(boolean status);
}