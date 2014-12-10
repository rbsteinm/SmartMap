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
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.listeners.CacheListener;
import ch.epfl.smartmap.servercom.InvitationBag;
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

    private LongSparseArray<User> mUserInstances;
    // SparseArrays containing live instances
    private final LongSparseArray<Event> mEventInstances;
    private final LongSparseArray<Filter> mFilterInstances;
    private final LongSparseArray<Invitation> mInvitationInstances;

    private final Set<Long> mUserIds;
    // These Sets are the keys for the LongSparseArrays
    private final Set<Long> mEventIds;
    private final Set<Long> mFilterIds;

    private final Set<Long> mInvitationIds;
    private final Set<Long> mInvitingFriendsIds;

    private final Set<Long> mFriendIds;

    // This Set contains the id of all our friends
    private long nextFilterId;

    // These values are used when needing to assign a new local id
    private final List<CacheListener> mListeners;

    // Listeners
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
        mInvitingFriendsIds = new HashSet<Long>();

        nextFilterId = 1;

        mListeners = new ArrayList<CacheListener>();
    }

    /**
     * Called when the user accepts an invitation. If it's a friend, it's added
     * to the cache and if it's an
     * event the
     * user joins it and it's infos are re-updated
     * 
     * @param invitation
     * @param callback
     */
    public synchronized void acceptInvitation(final Invitation invitation, final NetworkRequestCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    switch (invitation.getType()) {
                        case Invitation.FRIEND_INVITATION:
                            ImmutableUser newFriend =
                                ServiceContainer.getNetworkClient().acceptInvitation(invitation.getUser().getId());
                            Cache.this.putUser(newFriend);
                            break;
                        case Invitation.EVENT_INVITATION:
                            long eventId = ((GenericInvitation) invitation).getEvent().getId();
                            ServiceContainer.getNetworkClient().joinEvent(eventId);
                            Cache.this.putEvent(ServiceContainer.getNetworkClient().getEventInfo(eventId));
                            break;
                        default:
                            assert false;
                            break;
                    }

                    Cache.this.updateInvitation(invitation.getImmutableCopy().setStatus(Invitation.ACCEPTED));

                    callback.onSuccess();
                } catch (SmartMapClientException e) {
                    Log.e(TAG, "Error while accepting invitation: " + e);
                    callback.onFailure();
                }
                return null;
            }

        }.execute();
    }

    /**
     * OK
     */
    public synchronized void addOnCacheListener(CacheListener listener) {
        mListeners.add(listener);
    }

    /**
     * @param ids
     * @param event
     * @param callback
     */
    public synchronized void addParticipantsToEvent(Set<Long> ids, final Event event,
        final NetworkRequestCallback callback) {
        Set<Long> newParticipantIds = event.getImmutableCopy().getParticipantIds();
        newParticipantIds.addAll(ids);

        final ImmutableEvent newImmutableEvent = event.getImmutableCopy().setParticipantIds(newParticipantIds);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ServiceContainer.getNetworkClient().joinEvent(newImmutableEvent.getId());
                    ServiceContainer.getCache().updateEvent(newImmutableEvent);
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } catch (SmartMapClientException e) {
                    if (callback != null) {
                        callback.onFailure();
                    }
                }
                return null;
            }
        }.execute();
    }

    /**
     * OK
     * 
     * @param createdEvent
     * @param callback
     */
    public synchronized void createEvent(final ImmutableEvent createdEvent, final NetworkRequestCallback callback) {
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
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } catch (SmartMapClientException e) {
                    Log.e(TAG, "Error while creating event: " + e);
                    callback.onFailure();
                    if (callback != null) {
                        callback.onFailure();
                    }
                }
                return null;
            }
        }.execute();
    }

    public synchronized void declineInvitation(final Invitation invitation, final NetworkRequestCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    switch (invitation.getType()) {
                        case Invitation.FRIEND_INVITATION:
                            // Decline online
                            ServiceContainer.getNetworkClient().declineInvitation(invitation.getId());
                            break;
                        case Invitation.EVENT_INVITATION:
                            // No interaction needed here
                            break;
                        default:
                            assert false;
                            break;
                    }

                    Cache.this.updateInvitation(invitation.getImmutableCopy().setStatus(Invitation.DECLINED));

                    callback.onSuccess();
                } catch (SmartMapClientException e) {
                    Log.e(TAG, "Error while declining invitation: " + e);
                    callback.onFailure();
                }
                return null;
            }

        }.execute();
    }

    public synchronized Set<Filter> getAllActiveFilters() {
        return this.getFilters(new SearchFilter<Filter>() {
            @Override
            public synchronized boolean filter(Filter filter) {
                return filter.isActive();
            }
        });
    }

    public synchronized Set<Event> getAllEvents() {
        return this.getEvents(mEventIds);
    }

    public synchronized Set<Filter> getAllFilters() {
        return this.getFilters(mFilterIds);
    }

    public synchronized Set<User> getAllFriends() {
        return this.getFriends(mFriendIds);
    }

    public synchronized SortedSet<Invitation> getAllInvitations() {
        return this.getInvitations(mInvitationIds);
    }

    public synchronized Set<Event> getAllVisibleEvents() {
        Set<Event> allVisibleEvents = new HashSet<Event>();
        for (Event event : this.getAllEvents()) {
            if (event.isVisible()) {
                allVisibleEvents.add(event);
            }
        }

        return this.getEvents(mEventIds);
    }

    /**
     * OK
     * 
     * @return
     */
    public synchronized Set<User> getAllVisibleFriends() {
        // Get all friends
        Set<Long> allVisibleUsersId = new HashSet<Long>(mFriendIds);

        for (long id : mFriendIds) {
            User user = this.getUser(id);
            if (user instanceof Friend) {
                Log.d(TAG, "user number " + id + " is a FRIEND");
            } else if (user instanceof Stranger) {
                Log.d(TAG, "user number " + id + " is a STRANGER");
            } else if (user instanceof Self) {
                Log.d(TAG, "user number " + id + " is a SELF");
            } else {
                Log.d(TAG, "user number " + id + " is unknown");
            }
        }

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
    public synchronized Event getEvent(long id) {
        return mEventInstances.get(id);
    }

    public synchronized Set<Event> getEvents(SearchFilter<Event> filter) {
        Set<Event> events = new HashSet<Event>();
        for (long id : mEventIds) {
            Event event = this.getEvent(id);
            if (filter.filter(event)) {
                events.add(event);
            }
        }
        return events;
    }

    /**
     * OK
     * 
     * @param ids
     * @return
     */
    public synchronized Set<Event> getEvents(Set<Long> ids) {
        Set<Event> events = new HashSet<Event>();
        for (long id : ids) {
            Event event = this.getEvent(id);
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
    public synchronized Filter getFilter(long id) {
        return mFilterInstances.get(id);
    }

    public synchronized Set<Filter> getFilters(SearchFilter<Filter> searchFilter) {
        Set<Filter> filters = new HashSet<Filter>();

        for (long id : mFilterIds) {
            Filter filter = this.getFilter(id);
            if ((filter != null) && searchFilter.filter(filter)) {
                filters.add(filter);
            }
        }

        return filters;
    }

    public synchronized Set<Filter> getFilters(Set<Long> ids) {
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
    public synchronized User getFriend(long id) {
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
    public synchronized Set<User> getFriends(Set<Long> ids) {
        Set<User> friends = new HashSet<User>();
        for (long id : ids) {
            User friend = this.getFriend(id);
            if (friend != null) {
                friends.add(friend);
            }
        }
        return friends;
    }

    public synchronized Invitation getInvitation(long id) {
        return mInvitationInstances.get(id);
    }

    public synchronized SortedSet<Invitation> getInvitations(SearchFilter<Invitation> filter) {
        SortedSet<Invitation> invitations = new TreeSet<Invitation>();

        for (long id : mInvitationIds) {
            Invitation invitation = mInvitationInstances.get(id);
            if ((filter == null) || ((invitation != null) && filter.filter(invitation))) {
                invitations.add(invitation);
            }
        }

        return invitations;
    }

    public synchronized SortedSet<Invitation> getInvitations(Set<Long> ids) {
        SortedSet<Invitation> invitations = new TreeSet<Invitation>();
        for (long id : ids) {
            Invitation invitation = this.getInvitation(id);
            if (invitation != null) {
                invitations.add(invitation);
            }
        }
        return invitations;
    }

    public synchronized Set<Event> getLiveEvents() {
        return this.getEvents(new SearchFilter<Event>() {
            @Override
            public synchronized boolean filter(Event item) {
                return item.isLive();
            }
        });
    }

    public synchronized Set<Event> getMyEvents() {
        return this.getEvents(new SearchFilter<Event>() {
            @Override
            public synchronized boolean filter(Event item) {
                return item.isOwn();
            }
        });
    }

    public synchronized Set<Event> getNearEvents() {
        return this.getEvents(new SearchFilter<Event>() {
            @Override
            public synchronized boolean filter(Event item) {
                return item.isNear();
            }
        });
    }

    /**
     * OK
     * 
     * @param id
     * @return
     */
    public synchronized User getStranger(long id) {
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
    public synchronized Set<User> getStrangers(Set<Long> ids) {
        Set<User> strangers = new HashSet<User>();
        for (long id : ids) {
            User stranger = this.getStranger(id);
            if (stranger != null) {
                strangers.add(stranger);
            }
        }
        return strangers;
    }

    public synchronized SortedSet<Invitation> getUnansweredFriendInvitations() {
        SortedSet<Invitation> unansweredFriendInvitations = this.getAllInvitations();
        for (Invitation invitation : unansweredFriendInvitations) {
            if ((invitation.getStatus() == Invitation.ACCEPTED) || (invitation.getStatus() == Invitation.DECLINED)) {
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
    public synchronized User getUser(long id) {
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
    public synchronized Set<User> getUsers(Set<Long> ids) {
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

    public synchronized void initFromDatabase(DatabaseHelper database) {
        // Clear previous values
        mEventInstances.clear();
        mUserInstances.clear();
        mFilterInstances.clear();
        mInvitationInstances.clear();

        // Clear friend ids
        mFriendIds.clear();

        // Fill with database values
        this.putUsers(database.getAllUsers());
        this.putEvents(database.getAllEvents());
        this.putFilters(database.getAllFilters());
        this.putInvitations(database.getAllInvitations());

        // Notify listeners
        for (CacheListener listener : mListeners) {
            listener.onEventListUpdate();
            listener.onFriendListUpdate();
            listener.onFilterListUpdate();
        }
    }

    public synchronized void inviteFriendsToEvent(final long eventId, final Set<Long> usersIds,
        final NetworkRequestCallback callback) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ServiceContainer.getNetworkClient().inviteUsersToEvent(eventId, new ArrayList<Long>(usersIds));
                    callback.onSuccess();
                } catch (SmartMapClientException e) {
                    Log.e(TAG, "Couldn't invite friends to event:" + e);
                    callback.onFailure();
                }
                return null;
            }
        }.execute();
    }

    public synchronized void inviteUser(long id, final NetworkRequestCallback callback) {
        new AsyncTask<Long, Void, Void>() {
            @Override
            protected Void doInBackground(Long... params) {
                try {
                    ServiceContainer.getNetworkClient().inviteFriend(params[0]);
                    callback.onSuccess();
                } catch (SmartMapClientException e) {
                    Log.e(TAG, "Error while inviting friend: " + e);
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
    public synchronized void modifyOwnEvent(final ImmutableEvent createdEvent, final NetworkRequestCallback callback) {
        new AsyncTask<ImmutableEvent, Void, Void>() {

            @Override
            protected Void doInBackground(ImmutableEvent... params) {
                try {
                    ServiceContainer.getNetworkClient().updateEvent(params[0]);
                    Cache.this.updateEvent(params[0]);
                    callback.onSuccess();
                } catch (SmartMapClientException e) {
                    callback.onFailure();
                    Log.e(TAG, "Error while modifying own event: " + e);
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
    public synchronized void putEvent(ImmutableEvent newEvent) {
        Set<ImmutableEvent> singleton = new HashSet<ImmutableEvent>();
        singleton.add(newEvent);
        this.putEvents(singleton);
    }

    public synchronized void putEvents(Set<ImmutableEvent> newEvents) {
        for (final ImmutableEvent newEvent : newEvents) {
            mEventIds.add(newEvent.getId());
            // Add user to cache & set real instance to event
            this.putUser(newEvent.getImmCreator());
            mEventInstances.put(newEvent.getId(),
                new PublicEvent(newEvent.setCreator(this.getUser(newEvent.getImmCreator().getId()))));
        }
    }

    public synchronized long putFilter(ImmutableFilter newFilter) {
        Set<ImmutableFilter> singleton = new HashSet<ImmutableFilter>();
        singleton.add(newFilter);
        this.putFilters(singleton);
        return nextFilterId - 1;
    }

    public synchronized void putFilters(Set<ImmutableFilter> newFilters) {
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

    public synchronized void putInvitation(ImmutableInvitation newInvitation) {
        Set<ImmutableInvitation> singleton = new HashSet<ImmutableInvitation>();
        singleton.add(newInvitation);
        this.putInvitations(singleton);
    }

    public synchronized void putInvitations(Set<ImmutableInvitation> newInvitations) {
        Log.d(TAG, "Put invitation : " + newInvitations.size());
        boolean isListModified = false;
        for (final ImmutableInvitation newInvitation : newInvitations) {
            Log.d(TAG, "Invitation id : " + newInvitation.getId());
            Invitation invitation = null;
            switch (newInvitation.getType()) {
                case Invitation.FRIEND_INVITATION:
                    invitation = this.processFriendInvitation(newInvitation);
                    break;

                case Invitation.ACCEPTED_FRIEND_INVITATION:
                    invitation = this.processAcceptedFriendInvitation(newInvitation);
                    break;
                case Invitation.EVENT_INVITATION:
                    invitation = this.processEventInvitation(newInvitation);
                    break;
                default:
                    break;
            }

            mInvitationIds.add(invitation.getId());
            mInvitationInstances.put(invitation.getId(), invitation);
            isListModified = true;
        }

        if (isListModified) {
            for (CacheListener listener : mListeners) {
                listener.onInvitationListUpdate();
            }
        }
    }

    public synchronized void putUser(ImmutableUser newFriend) {
        Set<ImmutableUser> singleton = new HashSet<ImmutableUser>();
        singleton.add(newFriend);
        this.putUsers(singleton);
    }

    public synchronized void putUsers(Set<ImmutableUser> newFriends) {
        boolean isListModified = false;

        for (ImmutableUser newFriend : newFriends) {
            mUserIds.add(newFriend.getId());
            if (newFriend.getFriendship() == User.FRIEND) {
                mFriendIds.add(newFriend.getId());
            }
            if (mUserInstances.get(newFriend.getId()) == null) {
                // Need to add it
                mUserInstances.put(newFriend.getId(), User.createFromContainer(newFriend));
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

    public synchronized void readAllInvitations() {
        SortedSet<Invitation> unreadInvitations = this.getInvitations(new Cache.SearchFilter<Invitation>() {
            @Override
            public boolean filter(Invitation item) {
                // Get Unread invitations
                return item.getStatus() == Invitation.UNREAD;
            }
        });
        Log.d(TAG, unreadInvitations.toString());
        for (Invitation invitation : unreadInvitations) {
            Log.d(TAG, invitation.toString());
            invitation.update(invitation.getImmutableCopy().setStatus(Invitation.READ));
        }
    }

    /**
     * OK
     * 
     * @param id
     */
    public synchronized void removeEvent(long id) {
        Set<Long> singleton = new HashSet<Long>();
        singleton.add(id);
        this.removeEvents(singleton);
    }

    /**
     * OK
     * 
     * @param ids
     */
    public synchronized void removeEvents(Set<Long> ids) {
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
    public synchronized void removeFilter(long id) {
        Set<Long> singleton = new HashSet<Long>();
        singleton.add(id);
        this.removeFilters(singleton);
    }

    /**
     * OK
     * 
     * @param ids
     */
    public synchronized void removeFilters(Set<Long> ids) {
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
    public synchronized void removeFriend(long id, final NetworkRequestCallback callback) {
        Set<Long> singleton = new HashSet<Long>();
        singleton.add(id);
        this.removeFriends(singleton, callback);
    }

    /**
     * OK
     * 
     * @param ids
     * @throws SmartMapClientException
     */
    public synchronized void removeFriends(Set<Long> ids, final NetworkRequestCallback callback) {
        boolean isListModified = false;
        for (long id : ids) {
            if (mFriendIds.contains(id)) {
                new AsyncTask<Long, Void, Void>() {
                    @Override
                    protected Void doInBackground(Long... params) {
                        try {
                            ServiceContainer.getNetworkClient().removeFriend(params[0]);
                            callback.onSuccess();
                        } catch (SmartMapClientException e) {
                            Log.e(TAG, "Error while inviting friend: " + e);
                            callback.onFailure();
                        }
                        return null;
                    }
                }.execute(id);

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
     * @param ids
     * @param event
     * @param callback
     */
    public synchronized void removeParticipantsFromEvent(Set<Long> ids, Event event,
        final NetworkRequestCallback callback) {
        Set<Long> newParticipantIds = event.getImmutableCopy().getParticipantIds();
        newParticipantIds.removeAll(ids);

        final ImmutableEvent newImmutableEvent = event.getImmutableCopy().setParticipantIds(newParticipantIds);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ServiceContainer.getNetworkClient().leaveEvent(newImmutableEvent.getId());
                    ServiceContainer.getCache().updateEvent(newImmutableEvent);
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } catch (SmartMapClientException e) {
                    if (callback != null) {
                        callback.onFailure();
                    }
                }
                return null;
            }
        }.execute();
    }

    public synchronized void retainEvents(List<Long> ids) {
        for (long id : mEventIds) {
            if (!ids.contains(id)) {
                mEventIds.remove(id);
                mEventInstances.remove(id);
            }
        }
    }

    /**
     * OK
     * 
     * @param updatedEvent
     */
    public synchronized void updateEvent(ImmutableEvent updatedEvent) {
        Set<ImmutableEvent> singleton = new HashSet<ImmutableEvent>();
        singleton.add(updatedEvent);
        this.updateEvents(singleton);
    }

    /**
     * OK
     * 
     * @param updatedEvents
     */
    public synchronized void updateEvents(Set<ImmutableEvent> updatedEvents) {
        for (ImmutableEvent updatedEvent : updatedEvents) {
            Event cachedEvent = this.getEvent(updatedEvent.getId());
            if (cachedEvent != null) {
                cachedEvent.update(updatedEvent);
            }
        }

        for (CacheListener listener : mListeners) {
            listener.onEventListUpdate();
        }
    }

    public synchronized void updateFilter(ImmutableFilter updatedFilter) {
        Set<ImmutableFilter> singleton = new HashSet<ImmutableFilter>();
        singleton.add(updatedFilter);
        this.updateFilters(singleton);
    }

    public synchronized void updateFilters(Set<ImmutableFilter> updatedFilters) {
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
    public synchronized void updateFriend(ImmutableUser updatedFriend) {
        Set<ImmutableUser> singleton = new HashSet<ImmutableUser>();
        singleton.add(updatedFriend);
        this.updateFriends(singleton);
    }

    public synchronized void updateFriendInvitations(InvitationBag notifBag, final Context ctx) {
        this.putInvitations(notifBag.getInvitations());
    }

    /**
     * OK
     * 
     * @param updatedFriends
     */
    public synchronized void updateFriends(Set<ImmutableUser> updatedFriends) {
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

    public synchronized void
        updateFromNetwork(final SmartMapClient networkClient, final NetworkRequestCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // TODO : Empty useless instances from Cache
                try {
                    // Fetch friend ids
                    Set<Long> newFriendIds = new HashSet<Long>(networkClient.getFriendsIds());

                    // Sets new friend ids
                    mFriendIds.clear();
                    mFriendIds.addAll(newFriendIds);

                    LongSparseArray<User> newUserInstances = new LongSparseArray<User>();

                    // Update each friends
                    for (long id : newFriendIds) {
                        // Get online values
                        ImmutableUser onlineValues = networkClient.getUserInfo(id);
                        // Fetch Image
                        Bitmap image = ServiceContainer.getNetworkClient().getProfilePicture(id);
                        onlineValues.setImage(image);

                        if (mUserInstances.get(id) != null) {
                            User oldFriend = mUserInstances.get(id);
                            oldFriend.update(onlineValues);
                            newUserInstances.put(id, oldFriend);
                        } else {
                            newUserInstances.put(id, User.createFromContainer(onlineValues));
                        }
                    }

                    mUserInstances = newUserInstances;

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

    public synchronized void updateInvitation(ImmutableInvitation invitation) {
        // TODO
    }

    // TODO
    public synchronized boolean updatePublicEvent(ImmutableEvent event) {
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

    private Invitation processAcceptedFriendInvitation(ImmutableInvitation newInvitation) {
        // Add User in Cache
        Cache.this.putUser(newInvitation.getUserInfos());
        newInvitation.setUser(Cache.this.getUser(newInvitation.getUserId()));
        if (newInvitation.getId() == Invitation.NO_ID) {
            // Send from server, need to add local ID
            long id = ServiceContainer.getDatabase().addInvitation(newInvitation);
            newInvitation.setId(id);
            // Phone notification
            Notifications.acceptedFriendNotification(ServiceContainer.getSettingsManager().getContext(),
                newInvitation.getUserInfos());
            // Ack to server
            new AsyncTask<Long, Void, Void>() {
                @Override
                protected Void doInBackground(Long... params) {
                    try {
                        Log.d(TAG, "Acknoledging accpeted invitation");
                        ServiceContainer.getNetworkClient().ackAcceptedInvitation(params[0]);
                    } catch (SmartMapClientException e) {
                        Log.e(TAG, "Error while acknowledging accpeted invitation : " + e);
                    }
                    return null;
                }
            }.execute(newInvitation.getUserId());
        }
        return new GenericInvitation(newInvitation);
    }

    private Invitation processEventInvitation(ImmutableInvitation newInvitation) {
        Log.d(TAG, "Event invit ID :" + newInvitation.getId());
        // Add Event in Cache
        Cache.this.putEvent(newInvitation.getEventInfos());
        newInvitation.setEvent(Cache.this.getEvent(newInvitation.getEventId()));
        if (newInvitation.getId() == Invitation.NO_ID) {
            // Send from server, need to add local ID
            long id = ServiceContainer.getDatabase().addInvitation(newInvitation);
            newInvitation.setId(id);
            Log.d(TAG, "Database gives id " + id);
            // Phone notification
            Notifications.newEventNotification(ServiceContainer.getSettingsManager().getContext(), newInvitation);
        }
        return new GenericInvitation(newInvitation);
    }

    private Invitation processFriendInvitation(final ImmutableInvitation newInvitation) {
        // Add inviter in Cache
        Cache.this.putUser(newInvitation.getUserInfos());
        newInvitation.setUser(Cache.this.getUser(newInvitation.getUserId()));
        if (newInvitation.getId() == Invitation.NO_ID) {
            // Send from server, need to add local ID
            long id = ServiceContainer.getDatabase().addInvitation(newInvitation);
            newInvitation.setId(id);
            // Phone notification
            Notifications.newFriendNotification(ServiceContainer.getSettingsManager().getContext(),
                newInvitation.getUserInfos());
        }
        return new GenericInvitation(newInvitation);
    }

    /**
     * Allows to search efficiently through the Cache, by providing a filtering
     * method
     * 
     * @param <T>
     *            Type of items searched
     * @author jfperren
     */
    public interface SearchFilter<T> {
        boolean filter(T item);
    }
}
