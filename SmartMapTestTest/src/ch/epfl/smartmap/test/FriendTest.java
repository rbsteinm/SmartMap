package ch.epfl.smartmap.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.epfl.smartmap.cache.Friend;
import junit.framework.TestCase;

public class FriendTest extends TestCase {
    private final int id = 1111;
    private final String name = "test name";
    private final String name2 = "other name";
    private final String number = "0790000000";
    private final String email = "test@test.com";
    private final double x = 1.23;
    private final double y = 3.21;
    
    @Test
    public void testGetID() {
        Friend friend = new Friend(id, name, number);
        assertTrue(friend.getID() == id);
    }
    
    @Test
    public void testGetName() {
        Friend friend = new Friend(id, name, number);
        assertTrue(friend.getName().equals(name)); 
    }
    
    @Test
    public void testGetNumber() {
        Friend friend = new Friend(id, name, number);
        assertTrue(friend.getNumber().equals(number)); 
    }
    
    @Test
    public void testSetX() {
        Friend friend = new Friend(id, name, number);
        friend.setX(x);
        assertTrue(friend.getPosition().getX() == x); 
    }
    
    @Test
    public void testSetY() {
        Friend friend = new Friend(id, name, number);
        friend.setY(x);
        assertTrue(friend.getPosition().getY() == y); 
    }
    
    @Test
    public void testSetName() {
        Friend friend = new Friend(id, name, number);
        friend.setName(name2);
        assertTrue(friend.getName().equals(name2)); 
    }
    
    @Test
    public void testSetEmail() {
        Friend friend = new Friend(id, name, number);
        friend.setEmail(email);
        assertTrue(friend.getEmail().equals(email)); 
    }
}
