package ch.epfl.smartmap.test.severcom;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.junit.Test;

import ch.epfl.smartmap.cache.EventContainer;
import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.cache.InvitationContainer;
import ch.epfl.smartmap.servercom.NetworkEventInvitationBag;
import ch.epfl.smartmap.util.Utils;

/**
 * The class <code>NetworkEventInvitationBagTest</code> contains tests for the class <code>{@link NetworkEventInvitationBag}</code>.
 *
 * @generatedBy CodePro at 12/13/14 3:48 PM
 * @author matthieu
 * @version $Revision: 1.0 $
 */
public class NetworkEventInvitationBagTest extends TestCase {
    /**
     * Run the NetworkEventInvitationBag(HashSet<EventContainer>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/13/14 3:48 PM
     */
    @Test
    public void testNetworkEventInvitationBag_1()
        throws Exception {
        HashSet<EventContainer> hashSet = new HashSet<EventContainer>();

        NetworkEventInvitationBag result = new NetworkEventInvitationBag(hashSet);

        // add additional test code here
        assertNotNull(result);
    }

    /**
     * Run the NetworkEventInvitationBag(HashSet<EventContainer>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/13/14 3:48 PM
     */
    @Test
    public void testNetworkEventInvitationBag_2()
        throws Exception {
        HashSet<EventContainer> hashSet = new HashSet<EventContainer>();

        NetworkEventInvitationBag result = new NetworkEventInvitationBag(hashSet);

        // add additional test code here
        assertNotNull(result);
    }

    /**
     * Run the NetworkEventInvitationBag(HashSet<EventContainer>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/13/14 3:48 PM
     */
    @Test
    public void testNetworkEventInvitationBag_3()
        throws Exception {
        HashSet<EventContainer> hashSet = null;

        try {
            @SuppressWarnings("unused")
            NetworkEventInvitationBag result = new NetworkEventInvitationBag(hashSet);
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
     * @generatedBy CodePro at 12/13/14 3:48 PM
     */
    @Test
    public void testGetInvitations_1()
        throws Exception {
        NetworkEventInvitationBag fixture = new NetworkEventInvitationBag(new HashSet<EventContainer>());

        Set<InvitationContainer> result = fixture.getInvitations();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
    
    /**
     * Tests that the constructor creates the right invitations.
     */
    @Test
    public void testGetInvitations_2() {
        long now = GregorianCalendar.getInstance(TimeZone.getTimeZone(Utils.GMT_SWITZERLAND)).getTimeInMillis();
        
        HashSet<EventContainer> set = new HashSet<EventContainer>();
        set.add(new EventContainer(1, "Test event", null, null, null, null, null, null, null));
        
        NetworkEventInvitationBag fixture = new NetworkEventInvitationBag(set);
        
        Set<InvitationContainer> result = fixture.getInvitations();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        
        InvitationContainer invit = result.iterator().next();
        assertEquals(Invitation.NO_ID, invit.getId());
        assertEquals(1, invit.getEventId());
        assertEquals("Test event", invit.getEventInfos().getName());
        assertEquals(Invitation.UNREAD, invit.getStatus());
        assertEquals(Invitation.EVENT_INVITATION, invit.getType());
        // Checking timestamp
        assertTrue(Math.abs(invit.getTimeStamp() - now) < 5000);
    }
    
    /**
     * Test that getInvitations returns a defensive copy.
     */
    @Test
    public void testGetInvitationsDefensiveCopy() {
        NetworkEventInvitationBag fixture = new NetworkEventInvitationBag(new HashSet<EventContainer>());

        Set<InvitationContainer> result = fixture.getInvitations();
        
        assertNotNull(result);
        assertEquals(0, result.size());
        
        result.add(new InvitationContainer(0, null, null, 0, 0, 0));
        
        Set<InvitationContainer> result2 = fixture.getInvitations();
        
        assertNotNull(result2);
        assertEquals(0, result2.size());
    }
}