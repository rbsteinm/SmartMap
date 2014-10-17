package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.SparseArray;

/**
 * Describes a clientside, custom friend list (e.g. friends, family, etc.)
 * @author ritterni
 */
public class FriendList implements UserList {

    private List<Integer> idList; //list of the friends' IDs
    private SparseArray<User> friends; //contains every friend, mapped to their ID
    private String listName;
    
    /**
     * @param name The name of the friend list
     * @param friendsDatabase Whole database of friends referenced by the friendlist
     */
    public FriendList(String name, SparseArray<User> friendsDatabase) {
        //The friendlist needs a reference to the whole friend database in order to find users from their ID
        friends = friendsDatabase;
        listName = name;
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
    public void addUser(int id) {
        if (!idList.contains(id)) {
            idList.add(id);
        }
    }

    @Override
    public void removeUser(int id) {
        idList.remove(id);
    }

    @Override
    public List<Integer> getList() {
        return new ArrayList<Integer>(idList);
    }

    @Override
    public List<User> getUserList() {
        List<User> uList = new ArrayList<User>();
        for (int i = 0; i < idList.size(); i++) {
            uList.add(friends.get(idList.get(i)));
        }
        Collections.sort(uList, new UserComparator());
        return uList;
    }
}
