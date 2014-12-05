package ch.epfl.smartmap.cache;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.os.AsyncTask;
import android.util.Log;
import android.util.LongSparseArray;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.background.SettingsManager;
import ch.epfl.smartmap.gui.Utils;
import ch.epfl.smartmap.listeners.InvitationListener;
import ch.epfl.smartmap.search.CachedSearchEngine;
import ch.epfl.smartmap.servercom.NetworkRequestCallback;
import ch.epfl.smartmap.servercom.NotificationBag;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * @author agpmilli
 */
public final class InvitationManager {

    private List<Invitation> history;

    private final static InvitationManager ONE_INSTANCE = new InvitationManager();

    // List containing ids of all Friends
    private final Set<Long> mInvitingEventIds;
    private final Set<Long> mInvitingFriendIds;
    private final Set<Long> mInvitedUserIds;

    private final LongSparseArray<Invitation> mInvitationInstances;

    // Listeners
    private final List<InvitationListener> mListeners;

    public InvitationManager() {
        // Init sets
        mInvitingEventIds = new HashSet<Long>();
        mInvitingFriendIds = new HashSet<Long>();
        mInvitedUserIds = new HashSet<Long>();

        // Init sparseArray
        mInvitationInstances = new LongSparseArray<Invitation>();

        mListeners = new LinkedList<InvitationListener>();
    }

    /**
     * Return true if the invitation has been accepted (has to be called in an
     * AsyncTask<>)
     * 
     * @param id
     *            the id of user
     * @return
     *         true whether it has been accepted and false in the other case
     */
    public boolean acceptFriend(final long id) {
        ImmutableUser result;
        try {
            result = ServiceContainer.getNetworkClient().acceptInvitation(id);
            if (result != null) {
                ServiceContainer.getCache().putFriend(result);
            } else {
                return false;
            }
            return true;
        } catch (SmartMapClientException e) {
            return false;
        }
    }

    /**
     * Returns a list of all pending received invitations
     * 
     * @return A list of FriendInvitations
     */
    public List<Invitation> getFriendInvitations() {
        return null;
    }

    /**
     * @return the id of invited users
     */
    public Set<User> getInvitedUsers() {
        return ServiceContainer.getCache().getStrangers(mInvitedUserIds);
    }

    /**
     * @return number of unread invitations
     */
    public int getNumberOfUnreadInvitations() {
        return 0;
    }

    /**
     * @return the id of pending events
     */
    public Set<Event> getPendingEvents() {
        return ServiceContainer.getCache().getPublicEvents(mInvitingEventIds);
    }

    /**
     * @return the id of pending friends
     */
    public Set<User> getPendingFriends() {
        return ServiceContainer.getCache().getStrangers(mInvitingFriendIds);
    }

    /**
     * Sends accepted friend invitation to server
     * 
     * @param friendId
     *            the id of friend that accepted the invitation
     * @param callback
     *            callback that says if it fails or it successes
     */
    public void sendAcceptedFriendInvitation(final long friendId, final NetworkRequestCallback callback) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ServiceContainer.getNetworkClient().acceptInvitation(friendId);
                    callback.onSuccess();
                } catch (SmartMapClientException e) {
                    callback.onFailure();
                }
                return null;
            }
        }.execute();
    }

    /**
     * Sends event invitations to several users to server
     * 
     * @param eventId
     *            the event id
     * @param usersIds
     *            the list of user we want to invite
     * @param callback
     *            callback that says if it fails or it successes
     */
    public void
        sendEventInvitation(final long eventId, final List<Long> usersIds, final NetworkRequestCallback callback) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ServiceContainer.getNetworkClient().inviteUsersToEvent(eventId, usersIds);
                    callback.onSuccess();
                } catch (SmartMapClientException e) {
                    callback.onFailure();
                }
                return null;
            }
        }.execute();
    }

    /**
     * Sends friend invitation to server
     * 
     * @param friendId
     *            the invited friend
     * @param callback
     *            callback that says if it fails or it successes
     */
    public void sendFriendInvitation(final long friendId, final NetworkRequestCallback callback) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ServiceContainer.getNetworkClient().inviteFriend(friendId);
                    callback.onSuccess();
                } catch (SmartMapClientException e) {
                    callback.onFailure();
                }
                return null;
            }
        }.execute();
    }

    /**
     * @param notifBag
     */
    public void update(NotificationBag notifBag) {

        // Delete previous lists
        mInvitingFriendIds.clear();
        mInvitingEventIds.clear();
        mInvitedUserIds.clear();

        for (long userId : notifBag.getInvitingUsers()) {
            mInvitingFriendIds.add(userId);
        }

        Set<Long> newFriends = notifBag.getNewFriends();
        Set<Long> removedFriends = notifBag.getRemovedFriendsIds();

        for (long userId : newFriends) {

            new AsyncTask<Long, Void, Void>() {
                @Override
                protected Void doInBackground(Long... params) {
                    User newFriend = ServiceContainer.getCache().putFriend(params[0]);
                    Notifications.acceptedFriendNotification(Utils.sContext, newFriend);
                    return null;
                }
            }.execute(userId);
        }

        for (long userId : notifBag.getInvitingUsers()) {

            new AsyncTask<Long, Void, Void>() {

                @Override
                protected Void doInBackground(Long... params) {
                    User user = ServiceContainer.getCache().putStranger(params[0]);

                    if (!mInvitedUserIds.contains(user.getId())) {
                        ServiceContainer.getCache().addFriendInvitation(
                            new FriendInvitation(0, user.getId(), user.getName(), Invitation.UNREAD, user.getImage()));
                        mInvitedUserIds.add(user.getId());
                        if (SettingsManager.getInstance().notificationsEnabled()
                            && SettingsManager.getInstance().notificationsForFriendRequests()) {
                            Notifications.newFriendNotification(Utils.sContext, user);
                        }
                    }
                    return null;
                }
            }.execute(userId);
        }

        for (Long id : notifBag.getRemovedFriendsIds()) {
            Cache.getInstance().removeFriend(id);
        }

        if (!newFriends.isEmpty()) {
            new AsyncTask<Long, Void, Void>() {
                @Override
                protected Void doInBackground(Long... users) {
                    try {
                        for (long user : users) {
                            ServiceContainer.getCache().ackAcceptedInvitation(user);
                        }
                    } catch (SmartMapClientException e) {
                        Log.e("UpdateService", "Couldn't send acks!");
                    }
                    return null;
                }
            }.execute(newFriends.toArray(new Long[newFriends.size()]));
        }

        if (!removedFriends.isEmpty()) {
            new AsyncTask<Long, Void, Void>() {
                @Override
                protected Void doInBackground(Long... ids) {
                    try {
                        for (long id : ids) {
                            ServiceContainer.getNetworkClient().ackRemovedFriend(id);
                        }
                    } catch (SmartMapClientException e) {
                        Log.e("UpdateService", "Couldn't send acks!");
                    }
                    return null;
                }
            }.execute(removedFriends.toArray(new Long[removedFriends.size()]));
        }

        // Notify listeners
        for (InvitationListener l : mListeners) {
            l.onInvitationListUpdate();
        }
    }

    /**
     * Add a pending Event, and fill the cache with its information
     * 
     * @param id
     *            the id of event
     */
    private void addInvitingEvents(final long id) {
        mInvitingEventIds.add(id);

        (new AsyncTask<Void, Void, Void>() {
            @Override
            public Void doInBackground(Void... params) {
                // This method adds the event in the cache
                CachedSearchEngine.getInstance().findPublicEventById(id);

                return null;
            }
        }).execute();

        // Notify listeners
        for (InvitationListener listener : mListeners) {
            listener.onInvitationListUpdate();
        }
    }

    /**
     * Add a pending Friend, and fill the cache with its informations.
     * 
     * @param id
     *            the id of user
     */

    private void addInvitingFriends(Set<Long> ids) {
        for (final long id : ids) {
            (new AsyncTask<Void, Void, Boolean>() {
                @Override
                public Boolean doInBackground(Void... params) {
                    // This method adds the user in the cache
                    User user = CachedSearchEngine.getInstance().findStrangerById(id);
                    return user != null;
                }

                @Override
                public void onPostExecute(Boolean result) {
                    if (result) {
                        // Add if user exists
                        mInvitingFriendIds.add(id);
                    }
                }
            }).execute();
        }

        // Notify listeners
        for (InvitationListener listener : mListeners) {
            listener.onInvitationListUpdate();
        }
    }

    public static InvitationManager getInstance() {
        return ONE_INSTANCE;
    }
}
