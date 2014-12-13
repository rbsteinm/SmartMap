package ch.epfl.smartmap.test.severcom;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.junit.Test;

import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.cache.InvitationContainer;
import ch.epfl.smartmap.cache.UserContainer;
import ch.epfl.smartmap.servercom.NetworkFriendInvitationBag;
import ch.epfl.smartmap.util.Utils;

/**
 * The class <code>NetworkFriendInvitationBagTest</code> contains tests for the class <code>{@link NetworkFriendInvitationBag}</code>.
 *
 * @generatedBy CodePro at 12/13/14 5:12 PM
 * @author matthieu
 * @version $Revision: 1.0 $
 */
public class NetworkFriendInvitationBagTest extends TestCase {
    /**
     * Run the NetworkFriendInvitationBag(List<UserContainer>,List<UserContainer>,List<Long>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/13/14 5:12 PM
     */
    @Test
    public void testNetworkFriendInvitationBag_1()
        throws Exception {
        List<UserContainer> invitingUsers = new ArrayList<UserContainer>();
        List<UserContainer> newFriends = new ArrayList<UserContainer>();
        List<Long> removedFriendsIds = new ArrayList<Long>();

        NetworkFriendInvitationBag result = new NetworkFriendInvitationBag(invitingUsers, newFriends, removedFriendsIds);

        // add additional test code here
        assertNotNull(result);
    }

    /**
     * Run the NetworkFriendInvitationBag(List<UserContainer>,List<UserContainer>,List<Long>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/13/14 5:12 PM
     */
    public void testNetworkFriendInvitationBag_5()
        throws Exception {
        List<UserContainer> invitingUsers = null;
        List<UserContainer> newFriends = new ArrayList<UserContainer>();
        List<Long> removedFriendsIds = new ArrayList<Long>();

        try {
            @SuppressWarnings("unused")
            NetworkFriendInvitationBag result = 
                new NetworkFriendInvitationBag(invitingUsers, newFriends, removedFriendsIds);
            fail();
        } catch (IllegalArgumentException e) {
            // Ok
        }
    }

    /**
     * Run the NetworkFriendInvitationBag(List<UserContainer>,List<UserContainer>,List<Long>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/13/14 5:12 PM
     */
    public void testNetworkFriendInvitationBag_6()
        throws Exception {
        List<UserContainer> invitingUsers = new ArrayList<UserContainer>();
        List<UserContainer> newFriends = null;
        List<Long> removedFriendsIds = new ArrayList<Long>();

        try {
            @SuppressWarnings("unused")
            NetworkFriendInvitationBag result = 
                new NetworkFriendInvitationBag(invitingUsers, newFriends, removedFriendsIds);
            fail();
        } catch (IllegalArgumentException e) {
            // Ok
        }
    }

    /**
     * Run the NetworkFriendInvitationBag(List<UserContainer>,List<UserContainer>,List<Long>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/13/14 5:12 PM
     */
    public void testNetworkFriendInvitationBag_7()
        throws Exception {
        List<UserContainer> invitingUsers = new ArrayList<UserContainer>();
        List<UserContainer> newFriends = new ArrayList<UserContainer>();
        List<Long> removedFriendsIds = null;
        
        try {
            @SuppressWarnings("unused")
            NetworkFriendInvitationBag result = 
                new NetworkFriendInvitationBag(invitingUsers, newFriends, removedFriendsIds);
            fail();
        } catch (IllegalArgumentException e) {
            // Ok
        }
    }

    /**
     * Run the Set<InvitationContainer> getInvitations() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/13/14 5:12 PM
     */
    @Test
    public void testGetInvitations_1()
        throws Exception {
        NetworkFriendInvitationBag fixture = new NetworkFriendInvitationBag(new ArrayList<UserContainer>(),
            new ArrayList<UserContainer>(), new ArrayList<Long>());

        Set<InvitationContainer> result = fixture.getInvitations();

        // add additional test code here
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the Set<Long> getRemovedFriendsIds() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/13/14 5:12 PM
     */
    @Test
    public void testGetRemovedFriendsIds_1()
        throws Exception {
        NetworkFriendInvitationBag fixture = new NetworkFriendInvitationBag(new ArrayList<UserContainer>(),
            new ArrayList<UserContainer>(), new ArrayList<Long>());

        Set<Long> result = fixture.getRemovedFriendsIds();

        // add additional test code here
        assertNotNull(result);
        assertEquals(0, result.size());
    }
    
    /**
     * Test defensive copies of arguments and returns.
     */
    @Test
    public void testGetInvitations_2() {
        long now = GregorianCalendar.getInstance(TimeZone.getTimeZone(Utils.GMT_SWITZERLAND)).getTimeInMillis();
        
        List<UserContainer> invitingUsers = new ArrayList<UserContainer>();
        invitingUsers.add(new UserContainer(0, "Toto", null, null, null, null, null, null, 0));
        List<UserContainer> newFriends = new ArrayList<UserContainer>();
        newFriends.add(new UserContainer(1, "Titi", null, null, null, null, null, null, 1));
        List<Long> removedFriendsIds = new ArrayList<Long>();
        removedFriendsIds.add(1234L);
        
        NetworkFriendInvitationBag fixture = new NetworkFriendInvitationBag(invitingUsers,
            newFriends, removedFriendsIds);
        
        Set<InvitationContainer> result = fixture.getInvitations();
        
        assertNotNull(result);
        assertEquals(2, result.size());
        
        InvitationContainer invit = result.iterator().next();
        assertEquals(Invitation.NO_ID, invit.getId());
        // Checking timestamp
        assertTrue(Math.abs(invit.getTimeStamp() - now) < 5000);
        
        // Checking removed ids
        Set<Long> ids = fixture.getRemovedFriendsIds();
        
        assertNotNull(ids);
        assertEquals(1, ids.size());
        
        // Checking defensive copies
        result.add(new InvitationContainer(0, null, null, 0, 0, 0));
        
        Set<InvitationContainer> result2 = fixture.getInvitations();
        assertNotNull(result2);
        assertEquals(2, result2.size());
        
        invitingUsers.add(new UserContainer(4, "Toto", null, null, null, null, null, null, 0));
        newFriends.add(new UserContainer(5, "Titi", null, null, null, null, null, null, 1));
        removedFriendsIds.add(1276845L);
        
        Set<InvitationContainer> result3 = fixture.getInvitations();
        assertNotNull(result3);
        assertEquals(2, result3.size());
        
        Set<Long> ids2 = fixture.getRemovedFriendsIds();
        assertNotNull(ids2);
        assertEquals(1, ids2.size());
    }
}