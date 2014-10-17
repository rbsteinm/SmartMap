package ch.epfl.smartmap.cache;

import java.util.Comparator;

/**
 * Compares two users based on their name
 * @author ritterni
 */
public class UserComparator implements Comparator<User> {
    @Override
    public int compare(User a, User b) {
        return a.getName().compareToIgnoreCase(b.getName());
    } 
}