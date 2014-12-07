package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LongSparseArray;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.callbacks.NetworkRequestCallback;
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.listeners.CacheListener;
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

    // These sets are for invitation managing
    private final Set<Long> mInvitedUserIds;
    private final Set<Long> mInvitingUserIds;
    private final Set<Long> mInvitingEventIds;

    // These values are used when needing to assign a new local id
    private long nextFilterId;

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

        mInvitedUserIds = new HashSet<Long>();
        mInvitingUserIds = new HashSet<Long>();
        mInvitingEventIds = new HashSet<Long>();

        nextFilterId = 1;

        mListeners = new ArrayList<CacheListener>();
    }

    public void acceptInvitation(long id, final Context ctx, final NetworkRequestCallback callback) {
        if (mInvitingUserIds.contains(id)) {
            new AsyncTask<Long, Void, Void>() {

                @Override
                protected Void doInBackground(Long... params) {
                    try {
                        ImmutableUser newFriend =
                            ServiceContainer.getNetworkClient().acceptInvitation(params[0]);
                        Cache.this.putFriend(newFriend);
                        mInvitingUserIds.remove(params[0]);
                        mFriendIds.add(params[0]);

                        callback.onSuccess();
                    } catch (SmartMapClientException e) {
                        Log.e(TAG, "Error while accepting invitation: " + e.getMessage());
                        callback.onFailure();
                    }
                    return null;
                }

            }.execute(id);
        } else {
            callback.onFailure();
        }
    }

    /**
     * OK
     */
    public void addOnCacheListener(CacheListener listener) {
        mListeners.add(listener);
    }

    /**
     * OK
     * 
     * @param createdEvent
     * @param callback
     */
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
                    Cache.this.putEvent(createdEvent);
                    callback.onSuccess();
                } catch (SmartMapClientException e) {
                    callback.onFailure();
                }
                return null;
            }
        }.execute();
    }

    /**
     * OK
     * 
     * @return
     */
    public Set<Event> getAllEvents() {
        return this.getPublicEvents(mEventIds);
    }

    /**
     * OK
     * 
     * @return a list containing all the user's Friends.
     */
    public Set<User> getAllFriends() {
        return this.getFriends(mFriendIds);
    }

    public Set<Event> getAllVisibleEvents() {

        // TODO

        return this.getPublicEvents(mEventIds);
    }

    /**
     * OK
     * 
     * @return
     */
    public Set<User> getAllVisibleFriends() {
        // Get all friends
        Set<Long> allVisibleUsersId = new HashSet<Long>(mFriendIds);

        // For each active filter, keep friends in it
        for (Long id : mFilterIds) {
            Filter filter = this.getFilter(id);
            if (filter.isActive()) {
                allVisibleUsersId.retainAll(filter.getFriendIds());
            }
        }

        // Return all friends that passed all filters
        return this.getFriends(allVisibleUsersId);
    }

    /**
     * OK
     * 
     * @param id
     * @return
     */
    public Filter getFilter(long id) {
        return mFilterInstances.get(id);
    }

    public Set<Filter> getFilters(Set<Long> ids) {
        Set<Filter> filters = new HashSet<Filter>();

        for (long id : ids) {
            Filter filter = this.getFilter(id);
            if (filter != null) {
                filters.add(filter);
            }
        }

        return filters;
    }

    /**
     * OK
     * 
     * @param id
     * @return
     */
    public User getFriend(long id) {
        if (mFriendIds.contains(id)) {
            return mUserInstances.get(id);
        } else {
            return null;
        }
    }

    /**
     * OK
     * 
     * @param ids
     * @return
     */
    public Set<User> getFriends(Set<Long> ids) {
        Set<User> friends = new HashSet<User>();
        for (long id : ids) {
            User friend = this.getFriend(id);
            if (friend != null) {
                friends.add(friend);
            }
        }
        return friends;
    }

    /**
     * OK
     * 
     * @param id
     * @return
     */
    public Event getPublicEvent(long id) {
        return mEventInstances.get(id);
    }

    /**
     * OK
     * 
     * @param ids
     * @return
     */
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

    /**
     * OK
     * 
     * @param id
     * @return
     */
    public User getStranger(long id) {
        if (!mFriendIds.contains(id)) {
            return mUserInstances.get(id);
        } else {
            return null;
        }
    }

    /**
     * OK
     * 
     * @param ids
     * @return
     */
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

    /**
     * OK
     * 
     * @param id
     * @return
     */
    public User getUser(long id) {
        User user = this.getFriend(id);
        if (user == null) {
            user = this.getStranger(id);
        }
        return user;
    }

    /**
     * OK
     * 
     * @param ids
     * @return
     */
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
        mFilterInstances.clear();
        mEventInvitationInstances.clear();
        mFriendInvitationInstances.clear();

        // Clear all sets
        mFriendIds.clear();
        mEventIds.clear();

        // Get the database
        DatabaseHelper database = ServiceContainer.getDatabase();

        // Initialize id Lists
        mFriendIds.addAll(database.getFriendIds());
        Log.d(TAG, "Friend ids : " + mFriendIds);
        // mPublicEventIds.addAll(DatabaseHelper.getInstance().getEventIds());

        // Fill with database values
        for (long id : mFriendIds) {
            Log.d(TAG, "Added friend : " + id);
            mUserInstances.put(id, new Friend(database.getFriend(id)));
        }
        for (long id : mEventIds) {
            Log.d(TAG, "Added event : " + id);
            mEventInstances.put(id, new PublicEvent(database.getEvent(id)));
        }

        // Notify listeners
        for (CacheListener listener : mListeners) {
            listener.onEventListUpdate();
            listener.onFriendListUpdate();
        }
    }

    public void inviteFriendsToEvent(final long eventId, final Set<Long> usersIds,
        final NetworkRequestCallback callback) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ServiceContainer.getNetworkClient().inviteUsersToEvent(eventId,
                        new ArrayList<Long>(usersIds));
                    callback.onSuccess();
                } catch (SmartMapClientException e) {
                    Log.e(TAG, "Couldn't invite friends to event:" + e.getMessage());
                    callback.onFailure();
                }
                return null;
            }
        }.execute();
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

    /**
     * OK
     * 
     * @param createdEvent
     * @param callback
     */
    public void modifyOwnEvent(final ImmutableEvent createdEvent, final NetworkRequestCallback callback) {
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

    /**
     * OK
     * 
     * @param id
     */
    public void putEvent(ImmutableEvent newEvent) {
        Set<ImmutableEvent> singleton = new HashSet<ImmutableEvent>();
        singleton.add(newEvent);
        this.putEvents(singleton);
    }

    public void putEvents(Set<ImmutableEvent> newEvents) {
        for (ImmutableEvent newEvent : newEvents) {
            mEventIds.add(newEvent.getID());
            mEventInstances.put(newEvent.getID(), new PublicEvent(newEvent));
        }

        // Notify listeners
        for (CacheListener listener : mListeners) {
            listener.onEventListUpdate();
        }
    }

    public void putFilters(Set<ImmutableFilter> newFilters) {
        for (ImmutableFilter newFilter : newFilters) {
            // Create new Unique Id
            newFilter.setId(nextFilterId++);
            mFilterIds.add(newFilter.getId());
            mFilterInstances.put(newFilter.getId(), new DefaultFilter(newFilter));
        }

        // Notify listeners
        for (CacheListener listener : mListeners) {
            listener.onFilterListUpdate();
        }
    }

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
            // Check that the Stranger is not a friend
            if (mFriendIds.contains(newStranger.getId())) {
                if (mUserInstances.get(newStranger.getId()) == null) {
                    // Need to add it
                    mUserInstances.put(newStranger.getId(), new Friend(newStranger));
                } else {
                    // Only update
                    mUserInstances.get(newStranger.getId()).update(newStranger);
                }
            }
        }
    }

    /**
     * OK
     * 
     * @param id
     */
    public void removeEvent(long id) {
        Set<Long> singleton = new HashSet<Long>();
        singleton.add(id);
        this.removeEvents(singleton);
    }

    /**
     * OK
     * 
     * @param ids
     */
    public void removeEvents(Set<Long> ids) {
        boolean isListModified = false;

        for (long id : ids) {
            if (mEventIds.contains(id)) {
                // Remove id from sets
                mEventIds.remove(id);

                // Remove instance from array
                mEventInstances.remove(id);

                isListModified = true;
            }
        }

        // Notify listeners if needed
        if (isListModified) {
            for (CacheListener l : mListeners) {
                l.onEventListUpdate();
            }
        }
    }

    /**
     * OK
     * 
     * @param id
     */
    public void removeFilter(long id) {
        Set<Long> singleton = new HashSet<Long>();
        singleton.add(id);
        this.removeFilters(singleton);
    }

    /**
     * OK
     * 
     * @param ids
     */
    public void removeFilters(Set<Long> ids) {
        boolean isListModified = false;

        for (long id : ids) {
            // Check that we are not trying to remove the default filter
            if (mFilterIds.contains(id) && (id != Filter.DEFAULT_FILTER_ID)) {
                // Remove id from sets
                mFilterIds.remove(id);

                // Remove instance from array
                mFilterInstances.remove(id);

                isListModified = true;
            }
        }

        // Notify listeners if needed
        if (isListModified) {
            for (CacheListener l : mListeners) {
                l.onFilterListUpdate();
            }
        }
    }

    /**
     * OK
     * 
     * @param id
     */
    public void removeFriend(long id) {
        Set<Long> singleton = new HashSet<Long>();
        singleton.add(id);
        this.removeFriends(singleton);
    }

    /**
     * OK
     * 
     * @param ids
     */
    public void removeFriends(Set<Long> ids) {
        boolean isListModified = false;

        for (long id : ids) {
            if (mFriendIds.contains(id)) {
                // Remove id from sets
                mFriendIds.remove(id);
                mUserInstances.remove(id);
                // Remove instance from array
                mUserInstances.remove(id);

                isListModified = true;
            }
        }

        // Notify listeners if needed
        if (isListModified) {
            for (CacheListener l : mListeners) {
                l.onFriendListUpdate();
            }
        }
    }

    /**
     * OK
     * 
     * @param updatedEvent
     */
    public void updateEvent(ImmutableEvent updatedEvent) {
        Set<ImmutableEvent> singleton = new HashSet<ImmutableEvent>();
        singleton.add(updatedEvent);
        this.updateEvents(singleton);
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
                            new EventInvitation(0, creator.getId(), creator.getName(), params[0],
                                e.getName(), Invitation.UNREAD);

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

    public void updateEventInvitations(List<Long> events, final Context ctx) {
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
                            new EventInvitation(0, creator.getId(), creator.getName(), params[0],
                                e.getName(), Invitation.UNREAD);

                        invitation.setId(ServiceContainer.getDatabase().addEventInvitation(invitation));

                        mEventInvitationInstances.put(invitation.getId(), invitation);

                        if (ServiceContainer.getSettingsManager().notificationsEnabled()
                            && ServiceContainer.getSettingsManager().notificationsForEventInvitations()) {
                            Notifications.newEventNotification(ctx, e);
                        }
                    } catch (SmartMapClientException e) {
                        Log.e(TAG, "Unable to ack event invitation !" + e.getMessage());
                    }
                    return null;
                }
            }.execute(eventId);
        }
    }

    /**
     * OK
     * 
     * @param updatedEvents
     */
    public void updateEvents(Set<ImmutableEvent> updatedEvents) {
        for (ImmutableEvent updatedEvent : updatedEvents) {
            Event cachedEvent = this.getPublicEvent(updatedEvent.getID());
            if (cachedEvent != null) {
                cachedEvent.update(updatedEvent);
            }
        }

        for (CacheListener listener : mListeners) {
            listener.onEventListUpdate();
        }
    }

    /**
     * OK
     * 
     * @param updatedFriend
     */
    public void updateFriend(ImmutableUser updatedFriend) {
        Set<ImmutableUser> singleton = new HashSet<ImmutableUser>();
        singleton.add(updatedFriend);
        this.updateFriends(singleton);
    }

    public void updateFriendInvitations(NotificationBag notifBag, Context ctx) {
        this.updateFriendInvitations(notifBag.getInvitingUsers(), ctx);
        this.acceptNewFriends(notifBag.getNewFriends(), ctx);
        this.removeRemovedFriends(notifBag.getRemovedFriendsIds());

    }

    /**
     * OK
     * 
     * @param updatedFriends
     */
    public void updateFriends(Set<ImmutableUser> updatedFriends) {
        for (ImmutableUser updatedFriend : updatedFriends) {
            User cachedFriend = this.getFriend(updatedFriend.getId());
            if (cachedFriend != null) {
                cachedFriend.update(updatedFriend);
            }
        }

        for (CacheListener listener : mListeners) {
            listener.onFriendListUpdate();
        }
    }

    public void updateFromNetwork(final SmartMapClient networkClient, final NetworkRequestCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // TODO : Empty useless instances from Cache
                try {
                    // Fetch friend ids
                    Set<Long> newFriendIds = new HashSet<Long>(networkClient.getFriendsIds());

                    // Remove friends that are no longer friends
                    for (long id : mFriendIds) {
                        if (!newFriendIds.contains(id)) {
                            Cache.this.removeFriend(id);
                        }
                    }

                    // Sets new friend ids
                    mFriendIds.clear();
                    mFriendIds.addAll(newFriendIds);

                    // Update each friends
                    for (long id : newFriendIds) {
                        User friend = Cache.this.getFriend(id);

                        // Get online values
                        ImmutableUser onlineValues = networkClient.getUserInfo(id);
                        if (friend != null) {
                            // Simply update
                            friend.update(onlineValues);
                        } else {
                            // Add friend
                            Cache.this.putFriend(onlineValues);
                        }
                    }

                    // TODO : Update Events
                    callback.onSuccess();
                } catch (SmartMapClientException e) {
                    Log.e(TAG, "SmartMapClientException : " + e);
                    callback.onFailure();
                }
                return null;
            }
        }.execute();
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

    private void acceptNewFriends(Set<ImmutableUser> friends, final Context ctx) {
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
                        if (ServiceContainer.getSettingsManager().notificationsEnabled()
                            && ServiceContainer.getSettingsManager()
                                .notificationsForFriendshipConfirmations()) {
                            Notifications.acceptedFriendNotification(ctx, params[0]);
                        }

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

    private void updateFriendInvitations(Set<ImmutableUser> inviters, final Context ctx) {
        for (final ImmutableUser inviter : inviters) {
            if (!mInvitingUserIds.contains(inviter.getId())) {
                mInvitingUserIds.add(inviter.getId());
                new AsyncTask<ImmutableUser, Void, Void>() {
                    @Override
                    protected Void doInBackground(ImmutableUser... params) {
                        Bitmap image = null;
                        try {
                            image = Cache.this.getUser(params[0].getId()).getImage();
                            if ((image == User.NO_IMAGE) || (image == null)) {
                                image =
                                    ServiceContainer.getNetworkClient().getProfilePicture(params[0].getId());
                            }
                        } catch (SmartMapClientException e) {
                            Log.e(TAG, "Error retrieving profile picture of inviter: " + e.getMessage());
                            image = User.NO_IMAGE;
                        } finally {
                            FriendInvitation invitation =
                                new FriendInvitation(0, params[0].getId(), params[0].getName(),
                                    Invitation.UNREAD, image);

                            invitation.setId(ServiceContainer.getDatabase().addFriendInvitation(invitation));

                            mFriendInvitationInstances.put(invitation.getId(), invitation);

                            if (ServiceContainer.getSettingsManager().notificationsEnabled()
                                && ServiceContainer.getSettingsManager().notificationsForFriendRequests()) {
                                Notifications.newFriendNotification(ctx, inviter);
                            }
                        }
                        return null;
                    }
                }.execute(inviter);
            }
        }
    }
}
