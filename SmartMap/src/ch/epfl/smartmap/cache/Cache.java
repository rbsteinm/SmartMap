package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LongSparseArray;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.callbacks.NetworkRequestCallback;
import ch.epfl.smartmap.callbacks.SearchRequestCallback;
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

    static final public String TAG = Cache.class.getSimpleName();

    // SparseArrays containing live instances
    private final LongSparseArray<User> mUserInstances;
    private final LongSparseArray<Event> mEventInstances;
    private final LongSparseArray<Filter> mFilterInstances;
    private final LongSparseArray<Invitation> mInvitationInstances;

    // These Sets are the keys for the LongSparseArrays
    private final Set<Long> mUserIds;
    private final Set<Long> mEventIds;
    private final Set<Long> mFilterIds;
    private final Set<Long> mInvitationIds;

    // This Set contains the id of all our friends
    private final Set<Long> mFriendIds;

    // These values are used when needing to assign a new local id
    private long nextFilterId;

    // Listeners
    private final List<CacheListener> mListeners;

    public Cache() {
        // Init data structures
        mUserInstances = new LongSparseArray<User>();
        mEventInstances = new LongSparseArray<Event>();
        mFilterInstances = new LongSparseArray<Filter>();
        mInvitationInstances = new LongSparseArray<Invitation>();

        mFriendIds = new HashSet<Long>();

        mUserIds = new HashSet<Long>();
        mEventIds = new HashSet<Long>();
        mFilterIds = new HashSet<Long>();
        mInvitationIds = new HashSet<Long>();

        nextFilterId = 1;

        mListeners = new ArrayList<CacheListener>();
    }

    public void acceptInvitation(final Invitation invitation, final NetworkRequestCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    switch (invitation.getType()) {
                        case Invitation.FRIEND_INVITATION:
                            // Get friends info
                            ImmutableUser newFriend =
                                ServiceContainer.getNetworkClient().acceptInvitation(invitation.getId());
                            // Add friend to cache
                            Cache.this.putFriend(newFriend);
                        case Invitation.EVENT_INVITATION:
                            long eventId = ((GenericInvitation) invitation).getEvent().getId();
                            // Join event
                            ServiceContainer.getNetworkClient().joinEvent(eventId);
                            // Reupdate event
                            Cache.this.putEvent(ServiceContainer.getNetworkClient().getEventInfo(eventId));
                        default:
                            assert false;
                    }

                    Cache.this.updateInvitation(invitation.getImmutableCopy().setStatus(Invitation.ACCEPTED));

                    callback.onSuccess();
                } catch (SmartMapClientException e) {
                    Log.e(TAG, "Error while accepting invitation: " + e.getMessage());
                    callback.onFailure();
                }
                return null;
            }

        }.execute();
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

    public void declineInvitation(final Invitation invitation, final NetworkRequestCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    switch (invitation.getType()) {
                        case Invitation.FRIEND_INVITATION:
                            // Decline online
                            ServiceContainer.getNetworkClient().declineInvitation(invitation.getId());
                        case Invitation.EVENT_INVITATION:
                            // No interaction needed here
                        default:
                            assert false;
                    }

                    Cache.this.updateInvitation(invitation.getImmutableCopy().setStatus(Invitation.DECLINED));

                    callback.onSuccess();
                } catch (SmartMapClientException e) {
                    Log.e(TAG, "Error while accepting invitation: " + e.getMessage());
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

    public Set<Filter> getAllFilters() {
        return this.getFilters(mFilterIds);
    }

    /**
     * OK
     * 
     * @return a list containing all the user's Friends.
     */
    public Set<User> getAllFriends() {
        return this.getFriends(mFriendIds);
    }

    public SortedSet<Invitation> getAllInvitations() {
        return this.getInvitations(mInvitationIds);
    }

    public Set<Event> getAllVisibleEvents() {
        Set<Event> allVisibleEvents = new HashSet<Event>();
        for (Event event : this.getAllEvents()) {
            if (event.isVisible()) {
                allVisibleEvents.add(event);
            }
        }

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

    public SortedSet<Invitation> getFriendInvitations() {
        return this.getInvitations(mInvitationIds, new InvitationFilter() {
            @Override
            public boolean filter(Invitation invitation) {
                return invitation.getType() == Invitation.FRIEND_INVITATION;
            }
        });
    }

    public SortedSet<Invitation> getFriendInvitationsByStatus(final int status) {
        return this.getInvitations(mInvitationIds, new InvitationFilter() {
            @Override
            public boolean filter(Invitation invitation) {
                return invitation.getStatus() == status;
            }
        });
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

    public Invitation getInvitation(long id) {
        return mInvitationInstances.get(id);
    }

    public SortedSet<Invitation> getInvitations(Set<Long> ids) {
        return this.getInvitations(mInvitationIds, null);
    }

    public SortedSet<Invitation> getInvitations(Set<Long> ids, InvitationFilter filter) {
        SortedSet<Invitation> invitations = new TreeSet<Invitation>();

        for (long id : ids) {
            Invitation invitation = mInvitationInstances.get(id);
            if ((filter == null) || ((invitation != null) && filter.filter(invitation))) {
                invitations.add(invitation);
            }
        }

        return invitations;
    }

    public Set<Event> getLiveEvents() {
        Set<Event> onLiveEvents = this.getAllEvents();
        for (Event event : onLiveEvents) {
            if (!event.isLive()) {
                onLiveEvents.remove(event);
            }
        }
        return onLiveEvents;
    }

    public Set<Event> getMyEvents() {
        Set<Event> myEvents = this.getAllEvents();
        for (Event event : myEvents) {
            if (!event.isOwn()) {
                myEvents.remove(event);
            }
        }
        return myEvents;
    }

    public Set<Event> getNearEvents() {
        Set<Event> nearEvents = this.getAllEvents();
        for (Event event : nearEvents) {
            if (!event.isNear()) {
                nearEvents.remove(event);
            }
        }
        return nearEvents;
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

    public SortedSet<Invitation> getUnansweredFriendInvitations() {
        SortedSet<Invitation> unansweredFriendInvitations = this.getAllInvitations();
        for (Invitation invitation : unansweredFriendInvitations) {
            if ((invitation.getStatus() == Invitation.ACCEPTED)
                || (invitation.getStatus() == Invitation.DECLINED)) {
                unansweredFriendInvitations.remove(invitation);
            }
        }
        return unansweredFriendInvitations;
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

    public void initFromDatabase(DatabaseHelper database) {
        // Clear previous values
        mEventInstances.clear();
        mUserInstances.clear();
        mFilterInstances.clear();
        mInvitationInstances.clear();

        // Clear all sets
        mFriendIds.clear();
        mEventIds.clear();
        mFilterIds.clear();

        // Initialize id Lists
        mFriendIds.addAll(database.getFriendIds());
        Log.d(TAG, "Friend ids : " + mFriendIds);
        // mPublicEventIds.addAll(DatabaseHelper.getInstance().getEventIds());
        mFilterIds.addAll(database.getFilterIds());

        // Fill with database values
        for (long id : mFriendIds) {
            Log.d(TAG, "Added friend : " + id);
            mUserInstances.put(id, new Friend(database.getFriend(id)));
        }
        for (long id : mEventIds) {
            Log.d(TAG, "Added event : " + id);
            mEventInstances.put(id, new PublicEvent(database.getEvent(id)));
        }

        for (long id : mFilterIds) {
            mFilterInstances.put(id, new DefaultFilter(database.getFilter(id)));
        }

        // Notify listeners
        for (CacheListener listener : mListeners) {
            listener.onEventListUpdate();
            listener.onFriendListUpdate();
            listener.onFilterListUpdate();
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
        for (final ImmutableEvent newEvent : newEvents) {
            // Fetch Creator
            ServiceContainer.getSearchEngine().findUserById(newEvent.getCreatorId(),
                new SearchRequestCallback<User>() {
                    @Override
                    public void onNetworkError() {
                        // Don't add
                    }

                    @Override
                    public void onNotFound() {
                        // Don't add
                    }

                    @Override
                    public void onResult(User result) {
                        mEventIds.add(newEvent.getId());
                        mEventInstances.put(newEvent.getId(), new PublicEvent(newEvent.setCreator(result)));

                        // Notify listeners
                        for (CacheListener listener : mListeners) {
                            listener.onEventListUpdate();
                        }
                    }
                });
        }
    }

    public long putFilter(ImmutableFilter newFilter) {
        Set<ImmutableFilter> singleton = new HashSet<ImmutableFilter>();
        singleton.add(newFilter);
        this.putFilters(singleton);
        return nextFilterId - 1;
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

    public void putInvitation(ImmutableInvitation newInvitation) {
        Set<ImmutableInvitation> singleton = new HashSet<ImmutableInvitation>();
        singleton.add(newInvitation);
        this.putInvitations(singleton);
    }

    public void putInvitations(Set<ImmutableInvitation> newInvitations) {
        // for (ImmutableInvitation newInvitation : newInvitations) {
        // if (mInvitationInstances.get(newInvitation.getId() == null)) {
        // // Need to add it, give it to the database to get the id
        // int id = 0; //
        // ServiceContainer.getDatabase().addInvitation(newInvitation);
        // mInvitationInstances.put(id, new Invitation(newInvitation));
        // }
        // }
        //
        // for (CacheListener listener : mListeners) {
        // listener.onFriendListUpdate();
        // }
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
            if (!mFriendIds.contains(newStranger.getId())) {
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

    public void readAllInvitations() {
        // Set<Invitation> unreadInvitations = this.getIn
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

    // TODO
    public void updateEventInvitations(List<Long> events) {
        // for (Long eventId : events) {
        // new AsyncTask<Long, Void, Void>() {
        //
        // @Override
        // protected Void doInBackground(Long... params) {
        // try {
        // ServiceContainer.getNetworkClient().ackEventInvitation(params[0]);
        //
        // mEventInvitationIds.add(params[0]);
        // Event e = Cache.this.getPublicEvent(params[0]);
        //
        // User fromCache = Cache.this.getUser(e.getCreatorId());
        // ImmutableUser creator = null;
        // if (fromCache == null) {
        // creator = ServiceContainer.getNetworkClient().getUserInfo(params[0]);
        // } else {
        // creator = fromCache.getImmutableCopy();
        // }
        //
        // EventInvitation invitation =
        // new EventInvitation(new ImmutableInvitation(creator.getId(),
        // Invitation.UNREAD),
        // e.getImmutableCopy());
        //
        // invitation.setId(ServiceContainer.getDatabase().addEventInvitation(invitation));
        //
        // mEventInvitationInstances.put(invitation.getId(), invitation);
        // } catch (SmartMapClientException e) {
        // Log.e(TAG, "Unable to ack event invitation !" + e.getMessage());
        // }
        // return null;
        // }
        // }.execute(eventId);
        //
        // }
    }

    /**
     * OK
     * 
     * @param updatedEvents
     */
    public void updateEvents(Set<ImmutableEvent> updatedEvents) {
        for (ImmutableEvent updatedEvent : updatedEvents) {
            Event cachedEvent = this.getPublicEvent(updatedEvent.getId());
            if (cachedEvent != null) {
                cachedEvent.update(updatedEvent);
            }
        }

        for (CacheListener listener : mListeners) {
            listener.onEventListUpdate();
        }
    }

    public void updateFilter(ImmutableFilter updatedFilter) {
        Set<ImmutableFilter> singleton = new HashSet<ImmutableFilter>();
        singleton.add(updatedFilter);
        this.updateFilters(singleton);
    }

    public void updateFilters(Set<ImmutableFilter> updatedFilters) {
        for (ImmutableFilter updatedFilter : updatedFilters) {
            Filter cachedFilter = this.getFilter(updatedFilter.getId());
            if (cachedFilter != null) {
                cachedFilter.update(updatedFilter);
            }
        }

        for (CacheListener listener : mListeners) {
            listener.onFilterListUpdate();
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

    // TODO
    public void updateFriendInvitations(NotificationBag notifBag, Context ctx) {

        // for (long id : this.putInvitingUsers(notifBag.getInvitingUsers())) {
        // Notifications.newFriendNotification(ctx,
        // this.getUser(id).getImmutableCopy());
        // }
        //
        // this.acceptNewFriends(notifBag.getNewFriends(), ctx);
        // this.removeRemovedFriends(notifBag.getRemovedFriendsIds());

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
                        // Fetch Image
                        Bitmap image = ServiceContainer.getNetworkClient().getProfilePicture(id);
                        onlineValues.setImage(image);

                        if (friend != null) {
                            // Simply update
                            friend.update(onlineValues.setImage(image));
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

    public void updateInvitation(ImmutableInvitation invitation) {
        // TODO
    }

    public boolean updatePublicEvent(ImmutableEvent event) {
        // Check in cache
        Event cachedEvent = mEventInstances.get(event.getId());

        if (cachedEvent == null) {
            // Not in cache
            cachedEvent = new PublicEvent(event);
            mEventInstances.put(event.getId(), cachedEvent);
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
                        Bitmap image =
                            ServiceContainer.getNetworkClient().getProfilePicture(params[0].getId());
                        params[0].setImage(image);

                        Cache.this.updateFriend(params[0]);

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
                        Log.e(TAG, "Couldn't ack removed friend: " + e);
                    }
                    return null;
                }

            }.execute(id);
        }
    }

    private interface InvitationFilter {
        boolean filter(Invitation invitation);
    }
}
