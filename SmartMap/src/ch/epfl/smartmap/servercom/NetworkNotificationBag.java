/**
 * 
 */
package ch.epfl.smartmap.servercom;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import ch.epfl.smartmap.cache.User;

/**
 * @author Pamoi
 */
public class NetworkNotificationBag implements NotificationBag {

    private static final String TAG = "NetworkNotificationBag";

    private List<User> mInvitingUsers;

    private List<User> mNewFriends;
    private List<Long> mRemovedFriends;

    private SmartMapClient mClient;

    public NetworkNotificationBag(List<User> invitingUsers, List<User> newFriends,
        List<Long> removedFriendsIds, SmartMapClient client) {
        if (invitingUsers == null) {
            throw new IllegalArgumentException("invitingUsers list is null.");
        }
        if (newFriends == null) {
            throw new IllegalArgumentException("newFriends list is null.");
        }
        if (removedFriendsIds == null) {
            throw new IllegalArgumentException("removedFriendsIds list is null.");
        }
        if (client == null) {
            throw new IllegalArgumentException("Smartmap client is null.");
        }

        mInvitingUsers = new ArrayList<User>(invitingUsers);
        mNewFriends = new ArrayList<User>(newFriends);
        mRemovedFriends = new ArrayList<Long>(removedFriendsIds);

        // The client is a singleton, we cannot (and should not) create a
        // defensive copy.
        mClient = client;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.NotificationBag#ackNewFriend(int)
     */
    @Override
    public void ackNewFriend(long id) {
        AsyncAckNewFriend task = new AsyncAckNewFriend();
        task.execute(id);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.NotificationBag#ackRemovedFriend(int)
     */
    @Override
    public void ackRemovedFriend(long id) {
        AsyncAckRemovedFriend task = new AsyncAckRemovedFriend();
        task.execute(id);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.NotificationBag#getInvitingUsers()
     */
    @Override
    public List<User> getInvitingUsers() {
        return new ArrayList<User>(mInvitingUsers);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.NotificationBag#getNewFriends()
     */
    @Override
    public List<User> getNewFriends() {
        return new ArrayList<User>(mNewFriends);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.NotificationBag#getRemovedFriendsIds()
     */
    @Override
    public List<Long> getRemovedFriendsIds() {
        return new ArrayList<Long>(mRemovedFriends);
    }

    /**
     * Sends an ackAcceptedInvitation request to the server asynchronously.
     * 
     * @author Pamoi
     */
    private class AsyncAckNewFriend extends AsyncTask<Long, Void, Void> {

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Void doInBackground(Long... params) {
            // This private class is only used in this class, so we assume the
            // number of parameters to be correct
            try {
                mClient.ackAcceptedInvitation(params[0]);
            } catch (SmartMapClientException e) {
                Log.e(TAG,
                    "An error occured during communication with the server while executing ackNewFriend("
                        + params[0] + ").");
            }

            return null;
        }
    }

    /**
     * Sends an ackRemovedFriend request to the server asynchronously.
     * 
     * @author Pamoi
     */
    private class AsyncAckRemovedFriend extends AsyncTask<Long, Void, Void> {

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Void doInBackground(Long... params) {
            // This private class is only used in this class, so we assume the
            // number of parameters to be correct

            // method is ackRemovedFriend is not yet implemented.
            /*
             * try {
             * mClient.ackRemovedFriend(params[0]);
             * } catch (SmartMapClientException e) {
             * Log.e(TAG,
             * "An error occured during communication with the server while executing ackNewFriend("
             * + params[0] + ").");
             * }
             */

            return null;
        }
    }
}
