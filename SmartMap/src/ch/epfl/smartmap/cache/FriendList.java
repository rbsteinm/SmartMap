package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.LongSparseArray;

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
    public void addUser(long id) {
        if (!idList.contains(id)) {
            idList.add(id);
        }
    }

    @Override
    public long getID() {
        return databaseID;
    }

    @Override
    public List<Long> getList() {
        return new ArrayList<Long>(idList);
    }

    @Override
    public String getListName() {
        return listName;
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
    public void removeUser(long id) {
        idList.remove(id);
    }

    @Override
    public void setID(long newID) {
        databaseID = newID;
    }

    @Override
    public void setListName(String newName) {
        listName = newName;
    }
}
