package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LongSparseArray;
import ch.epfl.smartmap.background.Notifications;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.background.SettingsManager;
import ch.epfl.smartmap.callbacks.NetworkRequestCallback;
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.listeners.CacheListener;
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

    static final public String TAG = Cache.class.getSimpleName();
    private final LongSparseArray<User> mUserInstances;
    // SparseArrays containing live instances
    private final LongSparseArray<Event> mEventInstances;
    private final LongSparseArray<Filter> mFilterInstances;

    private final LongSparseArray<Invitation> mInvitationInstances;
    private final Set<Long> mUserIds;
    // These Sets are the keys for the LongSparseArrays
    private final Set<Long> mEventIds;

    private final Set<Long> mFilterIds;

    private final Set<Long> mInvitationIds;

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

        nextFilterId = Filter.DEFAULT_FILTER_ID + 1;

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
                            ServiceContainer.getDatabase().deletePendingFriend(invitation.getUser().getId());
                            newFriend.setFriendship(User.FRIEND);
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
                            ServiceContainer.getNetworkClient().declineInvitation(invitation.getUser().getId());
                            ServiceContainer.getDatabase().deletePendingFriend(invitation.getUser().getId());
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

    public synchronized Set<Filter> getAllCustomFilters() {
        Set<Long> customFilterIds = new HashSet<Long>(mFilterIds);
        customFilterIds.remove(Filter.DEFAULT_FILTER_ID);
        Log.d(TAG, "custom filters : " + customFilterIds);
        return this.getFilters(customFilterIds);
    }

    public synchronized Set<Event> getAllEvents() {
        return this.getEvents(mEventIds);
    }

    public synchronized Set<Filter> getAllFilters() {
        return this.getFilters(mFilterIds);
    }

    public synchronized Set<User> getAllFriends() {
        return this.getUsers(mFriendIds);
    }

    public synchronized SortedSet<Invitation> getAllInvitations() {
        return this.getInvitations(mInvitationIds);
    }

    public synchronized Set<User> getAllUsers() {
        return this.getUsers(mUserIds);
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
        Set<Long> allVisibleUsersId = new HashSet<Long>();
        if (this.getDefaultFilter() != null) {
            // Get all friends
            allVisibleUsersId.addAll(this.getDefaultFilter().getFriendIds());
        } else {
            allVisibleUsersId.addAll(mFriendIds);
        }

        // For each active filter, keep friends in it
        for (Long id : mFilterIds) {
            Filter filter = this.getFilter(id);
            if (filter.isActive()) {
                allVisibleUsersId.retainAll(filter.getFriendIds());
            }
        }

        // Return all friends that passed all filters
        return this.getUsers(allVisibleUsersId);
    }

    public synchronized Filter getDefaultFilter() {
        return this.getFilter(Filter.DEFAULT_FILTER_ID);
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

    public synchronized Set<Long> getFriendIds() {
        return mFriendIds;
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

    public synchronized Set<Event> getParticipatingEvents() {
        return this.getEvents(new SearchFilter<Event>() {
            @Override
            public synchronized boolean filter(Event item) {
                return item.isGoing();
            }
        });
    }

    public synchronized User getSelf() {
        return mUserInstances.get(ServiceContainer.getSettingsManager().getUserId());
    }

    public synchronized SortedSet<Invitation> getUnansweredFriendInvitations() {
        return this.getInvitations(new SearchFilter<Invitation>() {
            @Override
            public boolean filter(Invitation item) {
                int type = item.getType();
                int status = item.getStatus();
                return (type == Invitation.FRIEND_INVITATION)
                    && ((status == Invitation.READ) || (status == Invitation.UNREAD));
            }
        });
    }

    /**
     * OK
     * 
     * @param id
     * @return
     */
    public synchronized User getUser(long id) {
        return mUserInstances.get(id);
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
            User user = this.getUser(id);
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

        // Clear ids
        mUserIds.clear();
        mEventIds.clear();
        mFilterIds.clear();
        mInvitationIds.clear();

        // Clear friend ids
        mFriendIds.clear();

        // Fill with database values
        Log.d(TAG, "Init, put users : " + database.getAllUsers());
        this.putUsers(database.getAllUsers());

        Log.d(TAG, "Init, put events : " + database.getAllEvents());
        this.putEvents(database.getAllEvents());
        Log.d(TAG, "Init, put filters : " + database.getAllFilters());
        this.putFilters(database.getAllFilters());
        Log.d(TAG, "Init, put invitations : " + database.getAllInvitations());
        this.putInvitations(database.getAllInvitations());

        // Notify listeners
        for (CacheListener listener : mListeners) {
            listener.onEventListUpdate();
            listener.onUserListUpdate();
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

    private synchronized void keepOnlyTheseEvents(Set<ImmutableEvent> events) {
        mEventIds.clear();
        mEventInstances.clear();
        this.putEvents(events);
    }

    private synchronized void keepOnlyTheseUsers(Set<ImmutableUser> users) {
        mFriendIds.clear();
        mUserIds.clear();
        mUserInstances.clear();
        this.putUsers(users);
    }

    public void logState() {
        Log.d(TAG, "CACHE STATE : Users : " + mUserIds);
        Log.d(TAG, "CACHE STATE : Friends : " + mFriendIds);
        Log.d(TAG, "CACHE STATE : Events : " + mEventIds);
        Set<String> filters = new HashSet<String>();
        for (long id : mFilterIds) {
            filters.add("" + this.getFilter(id).getName() + "(" + this.getFilter(id).getFriendIds() + ")"
                + this.getFilter(id).getId() + this.getFilter(id).isActive());
        }
        Log.d(TAG, "CACHE STATE : Filters : " + filters);
        Set<Long> invitingUsers = new HashSet<Long>();
        for (long id : mInvitationIds) {
            if (this.getInvitation(id).getUser() != null) {
                invitingUsers.add(this.getInvitation(id).getUser().getId());
            }
        }
        Log.d(TAG, "CACHE STATE : Invits : " + invitingUsers);
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
        boolean needToCallListeners = false;

        Set<ImmutableUser> usersToAdd = new HashSet<ImmutableUser>();
        Set<ImmutableEvent> eventsToUpdate = new HashSet<ImmutableEvent>();
        Set<ImmutableEvent> eventsToAdd = new HashSet<ImmutableEvent>();

        for (final ImmutableEvent newEvent : newEvents) {
            Log.d(TAG, "putEvents, process event #" + newEvent.getId() + "with creator " + newEvent.getImmCreator());
            // Get id
            long eventId = newEvent.getId();

            if (this.getEvent(eventId) != null) {
                // Put in the update list
                eventsToUpdate.add(newEvent);
            } else {
                // Need to add to Cache, check if contains all informations
                if (newEvent.getImmCreator() != null) {
                    eventsToAdd.add(newEvent);
                    usersToAdd.add(newEvent.getImmCreator());
                }
            }

            // Add users that need to be added
            this.putUsers(usersToAdd);

            // Add user to Container for new Events & Add to SparseArray
            for (ImmutableEvent eventInfo : eventsToAdd) {
                needToCallListeners = true;
                eventInfo.setCreator(this.getUser(eventInfo.getCreatorId()));
                mEventIds.add(newEvent.getId());
                mEventInstances.put(newEvent.getId(), new PublicEvent(eventInfo));
            }

            // Update Events to need to be updated & put true if update didnt
            // call listeners
            needToCallListeners = !this.updateEvents(eventsToUpdate) && needToCallListeners;

            // Update Listeners if needed
            if (needToCallListeners) {
                for (CacheListener listener : mListeners) {
                    Log.d(TAG, "Called listeners on Events");
                    listener.onEventListUpdate();
                }
            }
            this.logState();
        }
    }

    public synchronized long putFilter(ImmutableFilter newFilter) {
        Set<ImmutableFilter> singleton = new HashSet<ImmutableFilter>();
        singleton.add(newFilter);
        this.putFilters(singleton);
        return nextFilterId - 1;
    }

    public synchronized void putFilters(Set<ImmutableFilter> newFilters) {
        boolean needToCallListeners = false;

        Set<ImmutableFilter> filtersToUpdate = new HashSet<ImmutableFilter>();

        for (ImmutableFilter newFilter : newFilters) {
            Log.d(TAG, "Put filter " + newFilter.getId() + " in Cache");
            if (!mFilterIds.contains(newFilter.getId())) {
                long filterId = newFilter.getId();
                // if not default
                if (filterId != Filter.DEFAULT_FILTER_ID) {
                    // Need to set an id
                    filterId = nextFilterId++;
                    newFilter.setId(filterId);
                }

                mFilterIds.add(filterId);
                mFilterInstances.put(filterId, Filter.createFromContainer(newFilter));
                needToCallListeners = true;
            } else {
                // Put in update set
                filtersToUpdate.add(newFilter);
            }
        }

        // Update filters that need to be added
        needToCallListeners = !this.updateFilters(filtersToUpdate) && needToCallListeners;

        // Notify listeners
        if (needToCallListeners) {
            Log.d(TAG, "Called listeners on Filters");
            for (CacheListener listener : mListeners) {
                listener.onFilterListUpdate();
            }
        }
        this.logState();
    }

    public synchronized void putInvitation(ImmutableInvitation invitationInfo) {
        Set<ImmutableInvitation> singleton = new HashSet<ImmutableInvitation>();
        singleton.add(invitationInfo);
        this.putInvitations(singleton);
    }

    public synchronized void putInvitations(Set<ImmutableInvitation> invitationInfos) {
        boolean needToCallListeners = false;

        // Contains values to add later all at once
        Set<ImmutableUser> usersToAdd = new HashSet<ImmutableUser>();
        Set<ImmutableEvent> eventsToAdd = new HashSet<ImmutableEvent>();
        Set<ImmutableInvitation> invitationsToAdd = new HashSet<ImmutableInvitation>();

        for (final ImmutableInvitation invitationInfo : invitationInfos) {
            // Get Id
            if (invitationInfo.getId() == Invitation.NO_ID) {
                // Get Id from database
                long id = ServiceContainer.getDatabase().addInvitation(invitationInfo);
                invitationInfo.setId(id);
            }

            if ((invitationInfo.getId() != Invitation.ALREADY_RECEIVED)
                && (this.getInvitation(invitationInfo.getId()) == null)) {
                switch (invitationInfo.getType()) {
                    case Invitation.FRIEND_INVITATION:
                        // Check that it contains all informations
                        if (invitationInfo.getUserInfos() != null) {
                            invitationsToAdd.add(invitationInfo);
                            usersToAdd.add(invitationInfo.getUserInfos());
                        }
                        break;
                    case Invitation.ACCEPTED_FRIEND_INVITATION:
                        // Check that it contains all informations
                        ImmutableUser newFriend = invitationInfo.getUserInfos();
                        if (newFriend != null) {
                            newFriend.setFriendship(User.FRIEND);
                            usersToAdd.add(newFriend);
                            invitationsToAdd.add(invitationInfo);
                        }
                        // Acknowledge new friend
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
                        }.execute(invitationInfo.getUserId());
                        break;
                    case Invitation.EVENT_INVITATION:
                        // Check that it contains all informations
                        if (invitationInfo.getEventInfos() != null) {
                            invitationsToAdd.add(invitationInfo);
                            eventsToAdd.add(invitationInfo.getEventInfos());
                        }
                        // Acknowledge event invitation
                        new AsyncTask<Long, Void, Void>() {
                            @Override
                            protected Void doInBackground(Long... params) {
                                try {
                                    Log.d(TAG, "Acknoledging event invitation");
                                    ServiceContainer.getNetworkClient().ackEventInvitation(params[0]);
                                } catch (SmartMapClientException e) {
                                    Log.e(TAG, "Error while acknowledging event invitation : " + e);
                                }
                                return null;
                            }
                        }.execute(invitationInfo.getEventId());
                        break;
                    default:
                        assert false;
                        break;
                }
            }
        }

        // Add all users
        this.putUsers(usersToAdd);

        // Add all events
        this.putEvents(eventsToAdd);

        // Create and add live instances of Invitations
        for (ImmutableInvitation invitationInfo : invitationsToAdd) {
            boolean isSetCorrectly = false;

            switch (invitationInfo.getType()) {
                case Invitation.FRIEND_INVITATION:
                case Invitation.ACCEPTED_FRIEND_INVITATION:
                    invitationInfo.setUser(this.getUser(invitationInfo.getUserInfos().getId()));
                    isSetCorrectly = invitationInfo.getUser() != null;
                    break;
                case Invitation.EVENT_INVITATION:
                    Log.d(TAG, "Invitation tries to put event " + invitationInfo.getEventInfos().getId());
                    invitationInfo.setEvent(this.getEvent(invitationInfo.getEventInfos().getId()));
                    isSetCorrectly = invitationInfo.getEvent() != null;
                    break;
                default:
                    assert false;
                    break;
            }
            Log.d(TAG, "Adding instance of invitation type " + invitationInfo.getType() + " with User "
                + invitationInfo.getUser() + " or Event " + invitationInfo.getEvent());

            if (isSetCorrectly) {
                mInvitationIds.add(invitationInfo.getId());
                long invitationId = invitationInfo.getId();
                GenericInvitation invitation = new GenericInvitation(invitationInfo);
                mInvitationInstances.put(invitationInfo.getId(), invitation);
                if (invitationId != Invitation.ALREADY_RECEIVED) {
                    Notifications.createNotification(invitation, ServiceContainer.getSettingsManager().getContext());
                }
            }

            needToCallListeners = true;
        }

        if (needToCallListeners) {
            Log.d(TAG, "Called listeners on Invitations");
            for (CacheListener listener : mListeners) {
                listener.onInvitationListUpdate();
            }
        }

        this.logState();
    }

    public synchronized void putUser(ImmutableUser newFriend) {
        Set<ImmutableUser> singleton = new HashSet<ImmutableUser>();
        singleton.add(newFriend);
        this.putUsers(singleton);
    }

    public synchronized void putUsers(Set<ImmutableUser> newUsers) {
        boolean needToCallListeners = false;

        Set<ImmutableUser> usersToUpdate = new HashSet<ImmutableUser>();

        for (ImmutableUser newUser : newUsers) {
            mUserIds.add(newUser.getId());

            if (newUser.getFriendship() == User.FRIEND) {
                mFriendIds.add(newUser.getId());
            }
            if (mUserInstances.get(newUser.getId()) == null) {
                if ((newUser.getFriendship() == User.FRIEND) || (newUser.getFriendship() == User.STRANGER)
                    || (newUser.getFriendship() == User.SELF)) {
                    mUserInstances.put(newUser.getId(), User.createFromContainer(newUser));
                    needToCallListeners = true;
                }
            } else {
                // Put in set for update
                usersToUpdate.add(newUser);
            }
        }

        // Update users that need to be updated
        needToCallListeners = !this.updateUsers(usersToUpdate) && needToCallListeners;

        // Notify listeners if needed
        if (needToCallListeners) {
            Log.d(TAG, "Called listeners on User");
            for (CacheListener listener : mListeners) {
                listener.onUserListUpdate();
            }
        }
        this.logState();
    }

    public synchronized void readAllInvitations() {
        SortedSet<Invitation> unreadInvitations = this.getInvitations(new Cache.SearchFilter<Invitation>() {
            @Override
            public boolean filter(Invitation item) {
                // Get Unread invitations
                return item.getStatus() == Invitation.UNREAD;
            }
        });

        Set<ImmutableInvitation> readInvitations = new HashSet<ImmutableInvitation>();

        for (Invitation invitation : unreadInvitations) {
            readInvitations.add(invitation.getImmutableCopy().setStatus(Invitation.READ));
        }

        this.updateInvitations(readInvitations);
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
                // TODO remove event in networkClient
                // ServiceContainer.getNetworkClient().

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
                            Log.e(TAG, "Error while removing friend: " + e);
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
                l.onUserListUpdate();
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

    public synchronized boolean removeUsers(Set<Long> userIds) {
        boolean isListModified = false;

        for (long id : userIds) {
            if (this.getUser(id) != null) {
                mUserInstances.remove(id);
                mUserIds.remove(id);
                mFriendIds.remove(id);
                isListModified = true;
            }
        }

        if (isListModified) {
            for (CacheListener listener : mListeners) {
                listener.onUserListUpdate();
            }
        }

        return isListModified;
    }

    /**
     * @param user
     *            the user we are trying to (un)block
     * @param newBlockedStatus
     *            true if we're blocking the user, false otherwise
     * @param callback
     * @author rbsteinm
     */
    public synchronized void setBlockedStatus(final ImmutableUser user, final boolean newBlockedStatus,
        final NetworkRequestCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (user.isBlocked() != newBlockedStatus) {
                    try {
                        if (user.isBlocked()) {
                            ServiceContainer.getNetworkClient().unblockFriend(user.getId());
                        } else {
                            ServiceContainer.getNetworkClient().blockFriend(user.getId());
                        }
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    } catch (SmartMapClientException e) {
                        Log.e("TAG", "Error while (un)blocking friend: " + e);
                        if (callback != null) {
                            callback.onFailure();
                        }
                    }
                }
                return null;
            }
        }.execute();
    }

    private synchronized boolean updateEvent(ImmutableEvent eventInfo) {
        Set<ImmutableEvent> singleton = new HashSet<ImmutableEvent>();
        singleton.add(eventInfo);
        return this.updateEvents(singleton);
    }

    private synchronized boolean updateEvents(Set<ImmutableEvent> eventInfos) {
        Log.d(TAG, "updateEvents(" + eventInfos + ")");
        boolean isListModified = false;
        for (ImmutableEvent eventInfo : eventInfos) {
            Event event = this.getEvent(eventInfo.getId());
            if ((event != null) && event.update(eventInfo)) {
                Log.d(
                    TAG,
                    "updateEvents successfully updated event " + event.getId() + " with participants "
                        + event.getParticipantIds());
                isListModified = true;
            }
        }

        if (isListModified) {
            for (CacheListener listener : mListeners) {
                listener.onEventListUpdate();
            }
        }

        return isListModified;
    }

    private synchronized boolean updateFilter(ImmutableFilter filterInfo) {
        Set<ImmutableFilter> singleton = new HashSet<ImmutableFilter>();
        singleton.add(filterInfo);
        return this.updateFilters(singleton);
    }

    private synchronized boolean updateFilters(Set<ImmutableFilter> filterInfos) {
        boolean isListModified = false;

        for (ImmutableFilter filterInfo : filterInfos) {
            Filter filter = this.getFilter(filterInfo.getId());
            if ((filter != null) && filter.update(filterInfo)) {
                isListModified = true;
            }
        }

        if (isListModified) {
            for (CacheListener listener : mListeners) {
                listener.onFilterListUpdate();
            }
        }

        return isListModified;
    }

    public synchronized void
        updateFromNetwork(final SmartMapClient networkClient, final NetworkRequestCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // Get settings
                    SettingsManager settingsManager = ServiceContainer.getSettingsManager();

                    // Sets with new values (avoid calling multiple times the
                    // listeners)
                    Set<ImmutableUser> updatedUsers = new HashSet<ImmutableUser>();
                    Set<ImmutableEvent> updatedEvents = new HashSet<ImmutableEvent>();

                    // Update self informations
                    long myId = settingsManager.getUserId();
                    ImmutableUser self = networkClient.getUserInfo(myId);
                    self.setImage(networkClient.getProfilePicture(myId));
                    updatedUsers.add(self);

                    // Fetch friends via listFriendPos
                    Set<ImmutableUser> listFriendPos = new HashSet<ImmutableUser>(networkClient.listFriendsPos());

                    for (ImmutableUser positionInfos : listFriendPos) {
                        // get id
                        long id = positionInfos.getId();
                        // Get other online info
                        ImmutableUser onlineInfos = networkClient.getUserInfo(id);
                        Log.d(TAG, "onlineInfos has name " + onlineInfos.getName());
                        // Get picture
                        Bitmap image = networkClient.getProfilePicture(id);
                        // Put all inside container
                        onlineInfos.setLocation(positionInfos.getLocation());
                        onlineInfos.setLocationString(positionInfos.getLocationString());
                        onlineInfos.setImage(image);

                        Log.d(
                            TAG,
                            "Update(" + onlineInfos.getId() + ") : " + onlineInfos.getName() + ", "
                                + self.getLocationString());
                        Log.d(TAG, "Has " + ((onlineInfos.getImage() == User.NO_IMAGE) ? "no " : "") + "image");

                        // Put friend in Set
                        updatedUsers.add(onlineInfos);
                    }

                    // Get near Events
                    Set<Long> nearEventIds =
                        new HashSet<Long>(networkClient.getPublicEvents(settingsManager.getLocation().getLatitude(),
                            settingsManager.getLocation().getLongitude(), settingsManager.getNearEventsMaxDistance()));

                    // Update all cached event if needed
                    for (long id : mEventIds) {
                        // Get event infos
                        ImmutableEvent onlineInfos = networkClient.getEventInfo(id);
                        // Check if event needs to be kept
                        if (nearEventIds.contains(id) || (onlineInfos.getCreatorId() == myId)
                            || onlineInfos.getParticipantIds().contains(myId)) {
                            // if so, put it in Set
                            updatedEvents.add(onlineInfos);
                            updatedUsers.add(onlineInfos.getImmCreator());
                        }
                    }

                    // Update users from invitations
                    for (Invitation invitation : Cache.this.getAllInvitations()) {
                        if (invitation.getType() == Invitation.FRIEND_INVITATION) {
                            // get id
                            long id = invitation.getUser().getId();
                            // Get online info
                            ImmutableUser onlineInfos = networkClient.getUserInfo(id);
                            // Get picture
                            Bitmap image = networkClient.getProfilePicture(id);
                            // Put all inside container
                            onlineInfos.setImage(image);

                            // Put friend in Set
                            updatedUsers.add(onlineInfos);
                        }
                    }

                    // Put new values in cache
                    Cache.this.keepOnlyTheseUsers(updatedUsers);
                    Cache.this.keepOnlyTheseEvents(updatedEvents);

                    callback.onSuccess();
                } catch (SmartMapClientException e) {
                    Log.e(TAG, "SmartMapClientException : " + e);
                    callback.onFailure();
                }
                return null;
            }
        }.execute();
    }

    private boolean updateInvitation(ImmutableInvitation invitation) {
        Set<ImmutableInvitation> singleton = new HashSet<ImmutableInvitation>();
        singleton.add(invitation);
        return this.updateInvitations(singleton);
    }

    private boolean updateInvitations(Set<ImmutableInvitation> invitations) {
        boolean isListModified = false;
        for (ImmutableInvitation invitation : invitations) {
            isListModified = isListModified || this.getInvitation(invitation.getId()).update(invitation);
        }

        if (isListModified) {
            for (CacheListener listener : mListeners) {
                listener.onInvitationListUpdate();
            }
        }

        return isListModified;
    }

    /**
     * OK
     * 
     * @param userInfo
     */
    private synchronized boolean updateUser(ImmutableUser userInfo) {
        Set<ImmutableUser> singleton = new HashSet<ImmutableUser>();
        singleton.add(userInfo);
        return this.updateUsers(singleton);
    }

    public synchronized void updateUserInfos(long id) {
        new AsyncTask<Long, Void, Void>() {
            @Override
            protected Void doInBackground(Long... params) {
                try {
                    Log.d(TAG, "need to update user " + params[0]);
                    ImmutableUser userInfos = ServiceContainer.getNetworkClient().getUserInfo(params[0]);
                    userInfos.setImage(ServiceContainer.getNetworkClient().getProfilePicture(params[0]));
                    Cache.this.updateUser(userInfos);
                } catch (SmartMapClientException e) {
                    Log.e(TAG, "SmartMapClientException : " + e);
                }
                return null;
            }
        }.execute(id);
    }

    /**
     * OK
     * 
     * @param userInfos
     */
    private synchronized boolean updateUsers(Set<ImmutableUser> userInfos) {
        boolean isListModified = false;

        Set<Long> usersWithNewTypeIds = new HashSet<Long>();
        Set<ImmutableUser> usersWithNewType = new HashSet<ImmutableUser>();

        for (ImmutableUser userInfo : userInfos) {
            User user = this.getUser(userInfo.getId());
            if (user != null) {
                // Check if friendship has changed
                if (user.getFriendship() == userInfo.getFriendship()) {
                    isListModified = user.update(userInfo) || isListModified;
                } else {
                    // Need to remove and add user again to change the instance
                    // type
                    usersWithNewTypeIds.add(userInfo.getId());
                    usersWithNewType.add(userInfo);
                }
            }
        }

        // Remove and add again users with new type
        if (!usersWithNewType.isEmpty()) {
            this.removeUsers(usersWithNewTypeIds);
            this.putUsers(usersWithNewType);
        }

        if (isListModified) {
            for (CacheListener listener : mListeners) {
                listener.onUserListUpdate();
            }
        }

        return isListModified;
    }
}