package ch.epfl.smartmap.test.cache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.test.AndroidTestCase;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.FriendsPagerActivity;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.cache.InvitationContainer;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserContainer;

public class InvitationTest extends AndroidTestCase {

    Bitmap ADD_PERSON_BITMAP = null;
    Intent userInvitationIntent = null;

    long userId = 3;
    String userName = "Alain";
    String phoneNumber = "0217465647";
    String email = "email@alainmilliet.ch";
    Bitmap userImage = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
    Location location = new Location(Displayable.PROVIDER_NAME);
    double latitude = 43.54574354;
    double longitude = 23.5479584;
    long lastSeen = 47587985;
    String locationString = "Bahamas";
    User.BlockStatus blockStatus = User.BlockStatus.UNBLOCKED;

    long userInvitationId = 150;
    long userInvitationTimeStamp = 2;
    long otherUserInvitationTimeStamp = 0;
    int userInvitationType = Invitation.FRIEND_INVITATION;
    int invitationStatus = Invitation.UNREAD;
    int otherInvitationStatus = Invitation.READ;

    UserContainer userContainer;
    InvitationContainer otherUserInvitationContainer;
    InvitationContainer userInvitationContainer;

    private void initContainers() {
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTime(lastSeen);

        ADD_PERSON_BITMAP =
            BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.ic_action_add_person);

        userInvitationIntent = new Intent(this.getContext(), FriendsPagerActivity.class);
        userInvitationIntent.putExtra("INVITATION", true);

        userContainer =
            new UserContainer(userId, userName, phoneNumber, email, location, locationString, userImage, blockStatus,
                User.FRIEND);
        userInvitationContainer =
            new InvitationContainer(userInvitationId, userContainer, null, invitationStatus, userInvitationTimeStamp,
                userInvitationType);
        otherUserInvitationContainer =
            new InvitationContainer(userInvitationId, userContainer, null, otherInvitationStatus,
                otherUserInvitationTimeStamp, userInvitationType);
    }

    @Before
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ServiceContainer.initSmartMapServices(this.getContext());

        this.initContainers();
    }

    @After
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // new container
        this.initContainers();
        // new cache
        ServiceContainer.setCache(new Cache());

    }

    @Test
    public void testCreateInvitationWithNullValues() {
        userInvitationContainer.setUserContainer(null);
        ServiceContainer.getCache().putInvitation(userInvitationContainer);
        Invitation invitation = ServiceContainer.getCache().getInvitation(userInvitationId);
        assertEquals(invitation, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvitationWithWrongId() {
        ServiceContainer.getCache().putInvitation(userInvitationContainer.setId(-4));
    }

    @Test
    public void testGetValues() {
        ServiceContainer.getCache().putInvitation(userInvitationContainer);
        Invitation invitation = ServiceContainer.getCache().getInvitation(userInvitationId);
        assertEquals(invitation.getId(), userInvitationId);
        assertEquals(invitation.getStatus(), invitationStatus);
        assertEquals(invitation.getTimeStamp(), userInvitationTimeStamp);
        assertEquals(invitation.getType(), userInvitationType);
        assertEquals(invitation.getEvent(), Invitation.NO_EVENT);
        assertEquals(invitation.getIntent().getExtras().get("INVITATION"),
            userInvitationIntent.getExtras().get("INVITATION"));
        assertEquals(invitation.getIntent().getAction(), userInvitationIntent.getAction());
        assertTrue(invitation.getImage().sameAs(ADD_PERSON_BITMAP));
        assertEquals(invitation.getSubtitle(),
            this.getContext().getResources().getString(R.string.invitation_click_here_to_open_your_list_of_invitations));
        assertEquals(
            invitation.getTitle(),
            userContainer.getName() + " "
                + this.getContext().getResources().getString(R.string.invitation_want_to_be_your_friend));
    }

    @Test
    public void testUpdateWithGoodParameters() {
        ServiceContainer.getCache().putInvitation(userInvitationContainer);
        Invitation invitation = ServiceContainer.getCache().getInvitation(userInvitationId);
        invitation.update(otherUserInvitationContainer);

        assertEquals(otherInvitationStatus, invitation.getStatus());
        assertEquals(otherUserInvitationTimeStamp, invitation.getTimeStamp());
    }

    @Test
    public void testUpdateWithSameParameters() {
        ServiceContainer.getCache().putInvitation(userInvitationContainer);
        Invitation invitation = ServiceContainer.getCache().getInvitation(userInvitationId);
        assertFalse(invitation.update(invitation.getContainerCopy()));

    }

    @Test
    public void testUpdateWithUnsetParameters() {
        ServiceContainer.getCache().putInvitation(userInvitationContainer);
        Invitation invitation = ServiceContainer.getCache().getInvitation(userInvitationId);

        InvitationContainer unsetParameters = Invitation.NO_INVITATION.getContainerCopy();
        unsetParameters.setId(invitation.getId());
        unsetParameters.setType(invitation.getType());
        assertFalse(invitation.update(unsetParameters));
    }

    @Test
    public void testUpdateWithWrongId() {
        ServiceContainer.getCache().putInvitation(userInvitationContainer);
        Invitation invitation = ServiceContainer.getCache().getInvitation(userInvitationId);
        try {
            invitation.update(invitation.getContainerCopy().setId(2));
            fail();
        } catch (IllegalArgumentException e) {
            // Success
        }
    }
}
