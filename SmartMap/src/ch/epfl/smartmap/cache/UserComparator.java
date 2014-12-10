package ch.epfl.smartmap.cache;

import java.util.Comparator;

/**
 * A comparator to sort users by alphabetical order
 * 
 * @author ritterni
 */
public class UserComparator implements Comparator<UserInterface> {

    @Override
    public int compare(UserInterface a, UserInterface b) {
        return a.getName().compareToIgnoreCase(b.getName());
    }
}
