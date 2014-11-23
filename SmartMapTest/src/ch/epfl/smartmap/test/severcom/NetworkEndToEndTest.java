package ch.epfl.smartmap.test.severcom;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import android.location.Location;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;
// import org.junit.FixMethodOrder;
// import org.junit.runners.MethodSorters;

/**
 * Tests whether we can interact with the real quiz server.
 * 
 * @author marion-S
 */

// @FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NetworkEndToEndTest extends TestCase {

    private final long facebookId = 1482245642055847L;
    private final String name = "SmartMap SwEng";
    private final String fbAccessToken =
        "CAAEWMqbRPIkBAJjvxMI0zNXLgzxYJURV5frWkDu8T60EfWup92GNEE7xDIVohfpa43Qm7FNbZCvZB7bXVTd0ZC0qLHZCju2zZBR3mc8mQH0OskEe7X5mZAWOlLZCIzsAWnfEy1ZAzz2JgYPKjaIwhIpI9OvJkQNWkJnX3rIwv4v9lL7hr9yx8LKuOegEHfZCcCNp491jewilZCz69ZA2ohryEYy";
    private Location location = new Location("SmartMapServers");
    private static final double LATITUDE = 45;
    private static final double LONGITUDE = 45;
    private static final long VALID_ID_1 = 1;
    private static final long VALID_ID_2 = 2;
    private static final long VALID_ID_3 = 3;

    protected void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testA_AuthServer() throws SmartMapClientException {

        NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();

        networkClient.authServer(name, facebookId, fbAccessToken);

    }

    @Test
    public void testB_UpdatePos() throws SmartMapClientException {
        location.setLatitude(LATITUDE);
        location.setLongitude(LONGITUDE);
        NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();

        networkClient.updatePos(location);
    }

    @Test
    public void testC_InviteFriend() throws SmartMapClientException {
        NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();
        try {
            networkClient.inviteFriend(VALID_ID_1);
        } catch (SmartMapClientException e) {
            // ok, cannot invite yourself
        }

    }

    @Test
    public void testD_FollowFriend() throws SmartMapClientException {
        NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();
        networkClient.followFriend(VALID_ID_3);
    }

    @Test
    public void testE_UnfollowFriend() throws SmartMapClientException {
        NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();
        networkClient.unfollowFriend(VALID_ID_3);
    }

    @Test
    public void ignoredtestF_AllowFriend() throws SmartMapClientException {
        NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();
        networkClient.allowFriend(VALID_ID_3);
    }

    @Test
    public void testG_DisallowFriend() throws SmartMapClientException {
        NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();
        networkClient.disallowFriend(VALID_ID_3);
    }

    @Test
    public void ignoredtestH_AllowFriendList() throws SmartMapClientException {
        NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();
        networkClient.allowFriendList(Arrays.asList((long) VALID_ID_1, (long) VALID_ID_2, (long) VALID_ID_3));
    }

    @Test
    public void testI_DisallowFriendList() throws SmartMapClientException {
        NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();
        networkClient.disallowFriendList(Arrays.asList((long) VALID_ID_1, (long) VALID_ID_2, (long) VALID_ID_3));
    }

    @Test
    public void testJ_GetUserInfo() throws SmartMapClientException {
        NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();
        User friend = networkClient.getUserInfo(VALID_ID_1);
        assertValidIdAndName(friend);

    }

    @Test
    public void testK_GetInvitations() throws SmartMapClientException {
        NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();
        List<List<User>> resultList = networkClient.getInvitations();
        assertTrue("Null list", resultList != null);

        for (List<User> list : resultList) {
            assertTrue("Null list", list != null);
            for (User user : list) {
                assertValidIdAndName(user);
            }
        }

    }

    // FIXME should simulate an invitation on server side??
    @Test
    public void testQ_AcceptInvitation() throws SmartMapClientException {
        NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();

        try {
            networkClient.acceptInvitation(VALID_ID_1);
        } catch (SmartMapClientException e) {
            // ok because not invited by anyone
        }

    }

    @Test
    public void testL_ListFriendPos() throws SmartMapClientException {
        NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();
        List<User> users = networkClient.listFriendsPos();

        assertTrue("Null map", users != null);

        for (User user : users) {
            Location location = user.getLocation();
            assertTrue("Invalid id", user.getID() > 0);
            assertTrue("Unexpected latitude", -90 <= location.getLatitude() && location.getLatitude() <= 90);
            assertTrue("Unexpected longitude", -180 <= location.getLatitude() && location.getLatitude() <= 180);
        }
    }

    @Test
    public void testM_FindUsers() throws SmartMapClientException {
        NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();
        List<User> friends = networkClient.findUsers("s");

        assertTrue("Null list", friends != null);
        for (User user : friends) {
            assertValidIdAndName(user);
        }
    }

    // FIXME normal that no error whereas no invitation to decline?
    @Test
    public void testN_declineInvitation() throws SmartMapClientException {
        NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();
        networkClient.declineInvitation(VALID_ID_3);
    }

    // FIXME normal that no error whereas no friend to remove?
    @Test
    public void testO_removeFriend() throws SmartMapClientException {
        NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();
        networkClient.removeFriend(VALID_ID_3);
    }

    // FIXME normal that no error whereas no accepted invitation to ack?
    @Test
    public void testP_AckAcceptedInvitation() throws SmartMapClientException {
        NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();
        networkClient.ackAcceptedInvitation(VALID_ID_3);
    }

    private void assertValidIdAndName(User user) {
        assertTrue("Unexpected id", user.getID() >= 0);
        assertTrue("Unexpected name", 0 < user.getName().length() && user.getName().length() <= 60);
    }
}
