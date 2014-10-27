package ch.epfl.smartmap.test.cache;

import org.junit.Test;

import android.test.AndroidTestCase;
import ch.epfl.smartmap.cache.Friend;

public class FriendTest extends AndroidTestCase {
    private final int id = 1111;
    private final String name = "test name";
    private final String name2 = "other name";
    private final String number = "0790000000";
    private final String email = "test@test.com";
    private final double x = 1.23;
    private final double y = 3.21;

    @Test
    public void testGetID() {
        Friend friend = new Friend(id, name);
        assertTrue(friend.getID() == id);
    }

    @Test
    public void testGetName() {
        Friend friend = new Friend(id, name);
        assertTrue(friend.getName().equals(name));
    }

    @Test
    public void testSetNumber() {
        Friend friend = new Friend(id, name);
        friend.setNumber(number);
        assertTrue(friend.getNumber().equals(number));
    }

    @Test
    public void testSetX() {
        Friend friend = new Friend(id, name);
        friend.setX(x);
        assertTrue(friend.getPosition().getX() == x);
    }

    @Test
    public void testSetY() {
        Friend friend = new Friend(id, name);
        friend.setY(y);
        assertTrue(friend.getPosition().getY() == y);
    }

    @Test
    public void testSetName() {
        Friend friend = new Friend(id, name);
        friend.setName(name2);
        assertTrue(friend.getName().equals(name2));
    }

    @Test
    public void testSetEmail() {
        Friend friend = new Friend(id, name);
        friend.setEmail(email);
        assertTrue(friend.getEmail().equals(email));
    }

    //
    // @Test
    // public void testGetDefaultPic() {
    // assertTrue(friend.getPicture(getContext()).sameAs(BitmapFactory.decodeResource(getContext().getResources(),
    // R.drawable.ic_launcher)));
    // }
    //
    // @Test
    // public void testSetPic() {
    // friend.setPicture(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.searchicon),
    // getContext());
    // assertTrue(friend.getPicture(getContext()).sameAs(BitmapFactory.decodeResource(getContext().getResources(),
    // R.drawable.searchicon)));
    // }
    //
    // @Test
    // public void testDeletePic() {
    // friend.setPicture(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.searchicon),
    // getContext());
    // friend.deletePicture(getContext());
    // assertTrue(friend.getPicture(getContext()).sameAs(BitmapFactory.decodeResource(getContext().getResources(),
    // R.drawable.ic_launcher)));
    // }
}
