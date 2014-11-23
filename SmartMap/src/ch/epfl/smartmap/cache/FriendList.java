package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.LongSparseArray;
import ch.epfl.smartmap.R;

/**
 * Describes a clientside, custom friend list (e.g. friends, family, etc.)
 * 
 * @author ritterni
 */
public class FriendList implements UserList {

    private final List<Long> idList; // list of the friends' IDs
    private String listName;
    private long databaseID;

    /**
     * @param name
     *            The name of the friend list
     * @param friendsDatabase
     *            Whole database of friends referenced by the friendlist
     */
    public FriendList(String name) {
        // The friendlist needs a reference to the whole friend database in
        // order to find users from their ID
        listName = name;
        idList = new ArrayList<Long>();
    }

    @Override
    public String getListName() {
        return listName;
    }

    @Override
    public void setListName(String newName) {
        listName = newName;
    }

    @Override
    public void addUser(long id) {
        if (!idList.contains(id)) {
            idList.add(id);
        }
    }

    @Override
    public void removeUser(long id) {
        idList.remove(id);
    }

    @Override
    public List<Long> getList() {
        return new ArrayList<Long>(idList);
    }

    @Override
    public List<User> getUserList(LongSparseArray<User> friends) {
        List<User> uList = new ArrayList<User>();
        for (long id : idList) {
            uList.add(friends.get(id));
        }

        Collections.sort(uList, new UserComparator());

        return uList;
    }

    @Override
    public long getID() {
        return databaseID;
    }

    @Override
    public void setID(long newID) {
        databaseID = newID;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getPicture(android.content.Context)
     */
    @Override
    public Bitmap getPicture(Context context) {

        Bitmap pic = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_default_user);

        return pic;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getName()
     */
    @Override
    public String getName() {
        return listName;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getShortInfos()
     */
    @Override
    public String getShortInfos() {
        return "This is a Family Filter";
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getLocation()
     */
    @Override
    public Location getLocation() {
        return new Location("Lausanne");
    }
}
