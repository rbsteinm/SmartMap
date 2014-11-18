package ch.epfl.smartmap.cache;

import java.util.Comparator;

/**
 * A comparator to sort users by alphabetical order
 * 
 * @author ritterni
 */
public class UserComparator implements Comparator<User> {

	@Override
	public int compare(User a, User b) {
		return a.getName().compareToIgnoreCase(b.getName());
	}
}
