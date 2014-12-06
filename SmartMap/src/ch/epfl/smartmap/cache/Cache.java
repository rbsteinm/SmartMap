package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LongSparseArray;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.background.SettingsManager;
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.gui.Utils;
import ch.epfl.smartmap.listeners.CacheListener;
import ch.epfl.smartmap.servercom.NetworkRequestCallback;
import ch.epfl.smartmap.servercom.NotificationBag;
import ch.epfl.smartmap.servercom.SmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * The Cache contains all instances of network objects that are used by the GUI.
 * Therefore, every request to
 * find an
 * user or an event should go through it. It will automatically fill itself with
 * the database on creation, and
 * then
 * updates the database as changes are made.
 * 
 * @author jfperren
 */
public class Cache {

    static final public String TAG = "Cache";

    // SparseArrays containing live instances
    private final LongSparseArray<User> mUserInstances;
    private final LongSparseArray<Event> mEventInstances;
    private final LongSparseArray<Filter> mFilterInstances;
    private final LongSparseArray<EventInvitation> mEventInvitationInstances;
    private final LongSparseArray<FriendInvitation> mFriendInvitationInstances;

    // These Sets are the keys for the LongSparseArrays
    private final Set<Long> mUserIds;
    private final Set<Long> mEventIds;
    private final Set<Long> mFilterIds;
    private final Set<Long> mEventInvitationIds;
    private final Set<Long> mFriendInvitationIds;

    // This Set contains the id of all our friends
    private final Set<Long> mFriendIds;

    // These sets contains different kind of Events
    private final Set<Long> mOwnEventIds;
    private final Set<Long> mNearEventIds;
    private final Set<Long> mPinnedEventIds;

    // These sets are for invitation managing
    private final Set<Long> mInvitedUserIds;
    private final Set<Long> mInvitingUserIds;
    private final Set<Long> mInvitingEventIds;

    // Listeners
    private final List<CacheListener> mListeners;

    public Cache() {
        // Init data structures
        mUserInstances = new LongSparseArray<User>();
        mEventInstances = new LongSparseArray<Event>();
        mFilterInstances = new LongSparseArray<Filter>();
        mEventInvitationInstances = new LongSparseArray<EventInvitation>();
        mFriendInvitationInstances = new LongSparseArray<FriendInvitation>();

        mFriendIds = new HashSet<Long>();

        mUserIds = new HashSet<Long>();
        mEventIds = new HashSet<Long>();
        mFilterIds = new HashSet<Long>();
        mEventInvitationIds = new HashSet<Long>();
        mFriendInvitationIds = new HashSet<Long>();

        mOwnEventIds = new HashSet<Long>();
        mNearEventIds = new HashSet<Long>();
        mPinnedEventIds = new HashSet<Long>();

        mInvitedUserIds = new HashSet<Long>();
        mInvitingUserIds = new HashSet<Long>();
        mInvitingEventIds = new HashSet<Long>();

        mListeners = new ArrayList<CacheListener>();
    }

    /**
     * @param listener
     *            Listener to be added
     */
    public void addOnCacheListener(CacheListener listener) {
        mListeners.add(listener);
    }

    public void clearOldInstances() {

    }

    public void createEvent(final ImmutableEvent createdEvent, final NetworkRequestCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    long eventId;
                    eventId = ServiceContainer.getNetworkClient().createPublicEvent(createdEvent);
                    // Add ID to event
                    createdEvent.setId(eventId);
                    // Puts event in Cache
                    Cache.this.putPublicEvent(createdEvent);
                    callback.onSuccess();
                } catch (SmartMapClientException e) {
                    callback.onFailure();
                }
                return null;
            }
        }.execute();
    }

    public List<Event> getAllEvents() {
        List<Event> result = new ArrayList<Event>();
        for (long id : mEventIds) {
            Event event = mEventInstances.get(id);

            if (event != null) {
                result.add(event);
            } else {
                assert false;
            }
        }

        return result;
    }

    /**
     * @return a list containing all the user's Friends.
     */
    public List<User> getAllFriends() {
        List<User> allFriends = new ArrayList<User>();
        for (Long id : mFriendIds) {
            User friend = mUserInstances.get(id);

            if (friend != null) {
                allFriends.add(friend);
            } else {
                assert false;
            }
        }
        return allFriends;
    }

    /**
     * @return a list containing all the user's Going Events.
     */
    public List<Event> getAllGoingEvents() {
        List<Event> allGoingEvents = new ArrayList<Event>();
        long myId = SettingsManager.getInstance().getUserID();
        for (Long id : mEventIds) {
            Event event = mEventInstances.get(id);

            if ((event != null) && event.getParticipants().contains(myId)) {
                allGoingEvents.add(event);
            } else {
                assert false;
            }
        }
        return allGoingEvents;
    }

    public List<Displayable> getAllVisibleEvents() {
        List<Displayable> allVisibleEvents = new ArrayList<Displayable>();
        for (Long id : mEventIds) {
            Event event = mEventInstances.get(id);
            if ((event != null) && event.isVisible()) {
                allVisibleEvents.add(event);
            }
        }
        return allVisibleEvents;
    }

    public List<Displayable> getAllVisibleFriends() {
        List<Displayable> allVisibleUsers = new ArrayList<Displayable>();
        for (Long id : mFriendIds) {
            User user = mUserInstances.get(id);
            if ((user != null) && user.isVisible()) {
                allVisibleUsers.add(user);
            }
        }
        return allVisibleUsers;
    }

    public Filter getFilter(long id) {
        return mFilterInstances.get(id);
    }

    public User getFriend(long id) {
        return mUserInstances.get(id);
    }

    public Set<User> getFriends(Set<Long> ids) {
        Set<User> friends = new HashSet<User>();
        for (long id : ids) {
            User friend = this.getStranger(id);
            if (friend != null) {
                friends.add(friend);
            }
        }
        return friends;
    }

    public Event getPublicEvent(long id) {
        return mEventInstances.get(id);
    }

    public Set<Event> getPublicEvents(Set<Long> ids) {
        Set<Event> events = new HashSet<Event>();
        for (long id : ids) {
            Event event = this.getPublicEvent(id);
            if (event != null) {
                events.add(event);
            }
        }
        return events;
    }

    public User getStranger(long id) {
        return mUserInstances.get(id);
    }

    public Set<User> getStrangers(Set<Long> ids) {
        Set<User> strangers = new HashSet<User>();
        for (long id : ids) {
            User stranger = this.getStranger(id);
            if (stranger != null) {
                strangers.add(stranger);
            }
        }
        return strangers;
    }

    public User getUser(long id) {
        User user = this.getFriend(id);
        if (user == null) {
            user = this.getStranger(id);
        }
        return user;
    }

    public Set<User> getUsers(Set<Long> ids) {
        Set<User> users = new HashSet<User>();
        for (long id : ids) {
            User user = this.getFriend(id);
            if (user == null) {
                user = this.getStranger(id);
            }
            if (user != null) {
                users.add(user);
            }
        }

        return users;
    }

    public void initFromDatabase() {
        // Clear previous values
        mEventInstances.clear();
        mUserInstances.clear();
        mUserInstances.clear();

        // Clear lists
        mFriendIds.clear();
        mEventIds.clear();

        // Get the database
        DatabaseHelper database = ServiceContainer.getDatabase();

        // Initialize id Lists
        mFriendIds.addAll(database.getFriendIds());
        // mPublicEventIds.addAll(DatabaseHelper.getInstance().getEventIds());

        // Fill with database values
        for (long id : mFriendIds) {
            mUserInstances.put(id, new Friend(database.getFriend(id)));
        }
        for (long id : mEventIds) {
            mEventInstances.put(id, new PublicEvent(database.getEvent(id)));
        }

        // Notify listeners
        for (CacheListener listener : mListeners) {
            listener.onEventListUpdate();
            listener.onFriendListUpdate();
        }
    }

    public void putFilters(Set<ImmutableFilter> newFilters) {
        boolean isListModified = false;

        for (ImmutableFilter newFilter : newFilters) {
            Filter filter = this.getFilter(newFilter.getId());
            if (filter == null) {
                // Need to add it
                mFilterIds.add(newFilter.getId());
                mFilterInstances.put(newFilter.getId(), new DefaultFilter(newFilter));
                isListModified = true;
            } else {
                // Only update
                filter.update(newFilter);
            }
        }

        // Notify listeners if needed
        if (isListModified) {
            for (CacheListener listener : mListeners) {
                listener.onFilterListUpdate();
            }
        }
    }

    /**
     * Add a Friend, and fill the cache with its informations.
     * 
     * @param id
     */
    public void putFriend(ImmutableUser newFriend) {
        Set<ImmutableUser> singleton = new HashSet<ImmutableUser>();
        singleton.add(newFriend);
        this.putFriends(singleton);
    }

    public void putFriends(Set<ImmutableUser> newFriends) {
        boolean isListModified = false;

        for (ImmutableUser newFriend : newFriends) {
            if (mUserInstances.get(newFriend.getId()) == null) {
                // Need to add it
                mFriendIds.add(newFriend.getId());
                mUserInstances.put(newFriend.getId(), new Friend(newFriend));
                isListModified = true;
            } else {
                // Only update
                this.updateFriend(newFriend);
            }
        }

        // Notify listeners if needed
        if (isListModified) {
            for (CacheListener listener : mListeners) {
                listener.onFriendListUpdate();
            }
        }
    }

    /**
     * Mark an Event as Going and fill the cache with its informations.
     * 
     * @param id
     */
    public void putPublicEvent(ImmutableEvent newEvent) {
        Set<ImmutableEvent> singleton = new HashSet<ImmutableEvent>();
        singleton.add(newEvent);
        this.putPublicEvents(singleton);
    }

    public void putPublicEvents(Set<ImmutableEvent> newEvents) {
        boolean isListModified = false;

        for (ImmutableEvent newEvent : newEvents) {
            if (mEventInstances.get(newEvent.getID()) == null) {
                // Need to add it
                Event event = new PublicEvent(newEvent);
                mEventInstances.put(newEvent.getID(), event);

                isListModified = true;
            } else {
                // Only update
                this.updateEvent(newEvent);
            }
        }

        // Notify listeners if needed
        if (isListModified) {
            for (CacheListener listener : mListeners) {
                listener.onEventListUpdate();
            }
        }
    }

    /**
     * Fill Cache with an unknown User's informations.
     * 
     * @param user
     */
    public void putStranger(ImmutableUser newStranger) {
        Set<ImmutableUser> singleton = new HashSet<ImmutableUser>();
        singleton.add(newStranger);
        this.putStrangers(singleton);
    }

    /**
     * Put Strangers in Cache to be reused later.
     * 
     * @param newStrangers
     */
    public void putStrangers(Set<ImmutableUser> newStrangers) {
        for (ImmutableUser newStranger : newStrangers) {
            if (mUserInstances.get(newStranger.getId()) == null) {
                // Need to add it
                mUserInstances.put(newStranger.getId(), new Friend(newStranger));
            } else {
                // Only update
                mUserInstances.get(newStranger.getId()).update(newStranger);
            }
        }
    }

    public void removeFriend(long id) {
        mFriendIds.remove(id);

        // Notify listeners
        for (CacheListener l : mListeners) {
            l.onFriendListUpdate();
        }
    }

    public void updateEvent(ImmutableEvent event) {
        // Check in cache
        Event cachedEvent = mEventInstances.get(event.getID());
        if (cachedEvent != null) {
            // In cache
            cachedEvent.update(event);
        }
    }

    public void updateEvents(Set<ImmutableUser> users) {
        for (ImmutableUser user : users) {
            this.updateFriend(user);
        }
    }

    public void updateFriend(ImmutableUser user) {
        // Check in cache
        User cachedFriend = mUserInstances.get(user.getId());
        if (cachedFriend != null) {
            // In cache
            cachedFriend.update(user);
        }
    }

    public void updateFromNetwork(SmartMapClient networkClient) throws SmartMapClientException {
        // TODO : Empty useless instances from Cache

        // Fetch friend ids
        Set<Long> newFriendIds = new HashSet<Long>(networkClient.getFriendsIds());

        // Remove friends that are no longer friends
        for (long id : mFriendIds) {
            if (!newFriendIds.contains(id)) {
                this.removeFriend(id);
            }
        }

        // Sets new friend ids
        mFriendIds.clear();
        mFriendIds.addAll(newFriendIds);

        // Update each friends
        for (long id : newFriendIds) {
            User friend = this.getFriend(id);

            // Get online values
            ImmutableUser onlineValues = networkClient.getUserInfo(id);
            if (friend != null) {
                // Simply update
                friend.update(onlineValues);
            } else {
                // Add friend
                this.putFriend(onlineValues);
            }
        }

        // TODO : Update Events
    }

    public void updateOwnEvent(final ImmutableEvent createdEvent, final NetworkRequestCallback callback) {
        new AsyncTask<ImmutableEvent, Void, Void>() {

            @Override
            protected Void doInBackground(ImmutableEvent... params) {
                try {
                    ServiceContainer.getNetworkClient().updateEvent(params[0]);
                    Cache.this.updateEvent(params[0]);
                    callback.onSuccess();
                } catch (SmartMapClientException e) {
                    callback.onFailure();
                }
                return null;
            }

        }.execute(createdEvent);
    }

    public boolean updatePublicEvent(ImmutableEvent event) {
        // Check in cache
        Event cachedEvent = mEventInstances.get(event.getID());

        if (cachedEvent == null) {
            // Not in cache
            cachedEvent = new PublicEvent(event);
            mEventInstances.put(event.getID(), cachedEvent);
            return true;
        } else {
            // In cache
            cachedEvent.update(event);
            return false;
        }
    }

    public void updatePositions(Set<ImmutableUser> friends) {
        for (ImmutableUser friend : friends) {
            this.updateFriend(friend);
        }

        for (CacheListener listener : mListeners) {
            listener.onFriendListUpdate();
        }
    }

    public void updateEventInvitations(List<Long> events) {
        for (Long eventId : events) {
            new AsyncTask<Long, Void, Void>() {

                @Override
                protected Void doInBackground(Long... params) {
                    try {
                        ServiceContainer.getNetworkClient().ackEventInvitation(params[0]);

                        mEventInvitationIds.add(params[0]);
                        Event e = Cache.this.getPublicEvent(params[0]);

                        User fromCache = Cache.this.getUser(e.getCreatorId());
                        ImmutableUser creator = null;
                        if (fromCache == null) {
                            creator = ServiceContainer.getNetworkClient().getUserInfo(params[0]);
                        } else {
                            creator = fromCache.getImmutableCopy();
                        }

                        EventInvitation invitation =
                            new EventInvitation(0, creator.getId(), creator.getName(), params[0], e.getName(),
                                Invitation.UNREAD);

                        invitation.setId(ServiceContainer.getDatabase().addEventInvitation(invitation));

                        mEventInvitationInstances.put(invitation.getId(), invitation);
                    } catch (SmartMapClientException e) {
                        Log.e(TAG, "Unable to ack event invitation !" + e.getMessage());
                    }
                    return null;
                }
            }.execute(eventId);

        }
    }

    public void updateFriendInvitations(NotificationBag notifBag) {
        this.updateFriendInvitations(notifBag.getInvitingUsers());
        this.acceptNewFriends(notifBag.getNewFriends());
        this.removeRemovedFriends(notifBag.getRemovedFriendsIds());

    }

    public void inviteUser(long id, final NetworkRequestCallback callback) {
        new AsyncTask<Long, Void, Void>() {

            @Override
            protected Void doInBackground(Long... params) {
                try {
                    ServiceContainer.getNetworkClient().inviteFriend(params[0]);
                    mInvitedUserIds.add(params[0]);
                    // Better to store only id in db ?
                    // ServiceContainer.getDatabase().addPendingFriend(params[0]);
                    callback.onSuccess();
                } catch (SmartMapClientException e) {
                    Log.e(TAG, "Error while inviting friend: " + e.getMessage());
                    callback.onFailure();
                }
                return null;
            }

        }.execute(id);
    }

    public void acceptInvitation(long id) { // Callback ?

    }

    private void updateFriendInvitations(Set<ImmutableUser> inviters) {
        for (ImmutableUser inviter : inviters) {
            if (!mInvitingUserIds.contains(inviter.getId())) {
                mInvitingUserIds.add(inviter.getId());
                new AsyncTask<ImmutableUser, Void, Void>() {
                    @Override
                    protected Void doInBackground(ImmutableUser... params) {
                        try {
                            Bitmap image = Cache.this.getUser(params[0].getId()).getImage();
                            if ((image == User.NO_IMAGE) || (image == null)) {
                                image = ServiceContainer.getNetworkClient().getProfilePicture(params[0].getId());
                            }
                            // Do we have to store received invitation or sent
                            // invitations ?
                            FriendInvitation invitation =
                                new FriendInvitation(0, params[0].getId(), params[0].getName(), Invitation.UNREAD,
                                    image);

                            invitation.setId(ServiceContainer.getDatabase().addFriendInvitation(invitation));

                            mFriendInvitationInstances.put(invitation.getId(), invitation);

                        } catch (SmartMapClientException e) {
                            Log.e(TAG, "Error retrieving profile picture of inviter: " + e.getMessage());
                        }
                        return null;
                    }
                }.execute(inviter);
            }
        }
    }

    private void acceptNewFriends(Set<ImmutableUser> friends) {
        for (ImmutableUser friend : friends) {
            this.putFriend(friend);

            new AsyncTask<ImmutableUser, Void, Void>() {

                @Override
                protected Void doInBackground(ImmutableUser... params) {
                    try {
                        ServiceContainer.getNetworkClient().ackAcceptedInvitation(params[0].getId());
                        // Bitmap image =
                        // ServiceContainer.getNetworkClient().getProfilePicture(params[0].getId());

                        // Set new friend profile picture ?

                        Notifications.acceptedFriendNotification(Utils.sContext, params[0]);
                    } catch (SmartMapClientException e) {
                        Log.e(TAG, "Couldn't ack new friend: " + e.getMessage());
                    }
                    return null;
                }

            }.execute(friend);
        }

        for (CacheListener listener : mListeners) {
            listener.onFriendListUpdate();
        }
    }

    private void removeRemovedFriends(Set<Long> removedFriendsIds) {
        for (Long id : removedFriendsIds) {
            this.removeFriend(id);

            new AsyncTask<Long, Void, Void>() {

                @Override
                protected Void doInBackground(Long... params) {
                    try {
                        ServiceContainer.getNetworkClient().ackRemovedFriend(params[0]);
                    } catch (SmartMapClientException e) {
                        Log.e(TAG, "Couldn't ack removed friend: " + e.getMessage());
                    }
                    return null;
                }

            }.execute(id);
        }
    }
}
