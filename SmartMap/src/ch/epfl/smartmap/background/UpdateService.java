package ch.epfl.smartmap.background;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import ch.epfl.smartmap.cache.AcceptedFriendInvitation;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.FriendInvitation;
import ch.epfl.smartmap.cache.ImmutableUser;
import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.cache.Notifications;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.gui.Utils;
import ch.epfl.smartmap.listeners.OnInvitationListUpdateListener;
import ch.epfl.smartmap.listeners.OnInvitationStatusUpdateListener;
import ch.epfl.smartmap.search.CachedSearchEngine;
import ch.epfl.smartmap.servercom.NetworkNotificationBag;
import ch.epfl.smartmap.servercom.NotificationBag;
import ch.epfl.smartmap.servercom.SmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * A background service that updates friends' position periodically
 * 
 * @author ritterni
 */
public class UpdateService extends Service implements OnInvitationListUpdateListener,
    OnInvitationStatusUpdateListener {

    private static final int HANDLER_DELAY = 1000;
    private static final int GPS_UPDATE_DELAY = 5 * 60 * 1000;
    private static final int INVITE_UPDATE_DELAY = 30000;

    private static final float MIN_DISTANCE = 5; // minimum distance to update position
    private static final float MIN_GPS_DISTANCE = 50; // minimum distance before gps updates are requested

    private static final int RESTART_DELAY = 2000;
    public static final int IMAGE_QUALITY = 100;

    private final Handler mHandler = new Handler();
    private LocationManager mLocManager;
    private boolean mFriendsPosEnabled = true;
    private final boolean mOwnPosEnabled = true;
    private final boolean isFriendIDListRetrieved = true;
    private final boolean isDatabaseInitialized = false;
    private DatabaseHelper mHelper;
    private SettingsManager mManager;
    private Geocoder mGeocoder;
    private final SmartMapClient mClient = ServiceContainer.getNetworkClient();
    private Set<Long> mInviterIds = new HashSet<Long>();
    private Cache mCache;
    private float mCurrentAccuracy = 0;

    // Settings
    private int mPosUpdateDelay;
    private boolean mNotificationsEnabled;
    private boolean mNotificationsForEventInvitations;
    private boolean mNotificationsForFriendRequests;
    private boolean mNotificationsForFriendshipConfirmations;

    private final Runnable friendsPosUpdate = new Runnable() {
        @Override
        public void run() {
            if (!mManager.isOffline()) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... args0) {
                        try {
                            List<ImmutableUser> friendsWithNewLocations = mClient.listFriendsPos();
                            mCache.updateFriendList(friendsWithNewLocations);
                        } catch (SmartMapClientException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();
            }
            mHandler.postDelayed(this, mManager.getRefreshFrequency());
        }
    };

    private final Runnable ownPosUpdate = new Runnable() {
        @Override
        public void run() {
            if (!mManager.isOffline()) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... arg0) {
                        try {
                            mClient.updatePos(mManager.getLocation());
                        } catch (SmartMapClientException e) {
                            Log.e("UpdateService", "Position update failed!");
                        }
                        return null;
                    }
                }.execute();
            }
            mHandler.postDelayed(this, mManager.getRefreshFrequency());
        }
    };

    private final Runnable nearEventsUpdate = new Runnable() {
        @Override
        public void run() {
            new AsyncTask<Void, Void, Void>() {
                @Override
                public Void doInBackground(Void... params) {
                    CachedSearchEngine.getInstance().getAllNearEvents(
                        SettingsManager.getInstance().getLocation(), 100000, null);
                    return null;
                }
            }.execute();
            mHandler.postDelayed(this, 20000);
        }
    };

    private final Runnable updateDatabase = new Runnable() {
        @Override
        public void run() {
            new AsyncTask<Void, Void, Void>() {
                @Override
                public Void doInBackground(Void... params) {
                    DatabaseHelper.getInstance().updateFromCache();
                    return null;
                }
            }.execute();
            mHandler.postDelayed(this, 60000);
        }
    };

    private final Runnable getInvitations = new Runnable() {
        @Override
        public void run() {
            UpdateService.this.loadSettings();
            new AsyncTask<Void, Void, NotificationBag>() {
                @Override
                protected NotificationBag doInBackground(Void... arg0) {
                    NotificationBag nb = null;
                    try {
                        nb = mClient.getInvitations();
                    } catch (SmartMapClientException e) {
                        Log.e("UpdateService",
                            "Couldn't retrieve invitations due to a server error: " + e.getMessage());
                        // We set an empty notification bag as we couldn't retrieve data
                        // from the server.
                        nb =
                            new NetworkNotificationBag(new ArrayList<Long>(), new ArrayList<Long>(),
                                new ArrayList<Long>());
                        // try to re-log
                        new AsyncLogin().execute();
                    }
                    return nb;
                }

                @Override
                protected void onPostExecute(NotificationBag result) {
                    if (result != null) {
                        Set<Long> newFriends = result.getNewFriends();
                        Set<Long> removedFriends = result.getRemovedFriendsIds();

                        for (long userId : newFriends) {

                            new AsyncTask<Long, Void, Void>() {
                                @Override
                                protected Void doInBackground(Long... params) {
                                    mCache.putFriend(params[0]);
                                    User newFriend = mCache.getFriendById(params[0]);
                                    mHelper.addAcceptedRequest(new AcceptedFriendInvitation(0, newFriend
                                        .getId(), newFriend.getName(), null));
                                    if (mNotificationsEnabled && mNotificationsForFriendshipConfirmations) {
                                        Notifications.acceptedFriendNotification(Utils.sContext, newFriend);
                                    }
                                    return null;
                                }
                            }.execute(userId);
                        }

                        for (long userId : result.getInvitingUsers()) {

                            new AsyncTask<Long, Void, Void>() {

                                @Override
                                protected Void doInBackground(Long... params) {
                                    User user = mCache.getUser(params[0]);

                                    if (!mInviterIds.contains(user.getId())) {
                                        mHelper.addFriendInvitation(new FriendInvitation(0, user.getId(),
                                            user.getName(), Invitation.UNREAD, null));
                                        mInviterIds.add(user.getId());
                                        // get pictures
                                        new AsyncTask<Long, Void, Void>() {
                                            @Override
                                            protected Void doInBackground(Long... ids) {
                                                for (long id : ids) {
                                                    UpdateService.this.downloadUserPicture(id);
                                                }
                                                return null;
                                            }
                                        }.execute(user.getId());
                                        if (mNotificationsEnabled && mNotificationsForFriendRequests) {
                                            Notifications.newFriendNotification(Utils.sContext, user);
                                        }
                                    }
                                    return null;
                                }
                            }.execute(userId);
                        }

                        for (Long id : result.getRemovedFriendsIds()) {
                            mHelper.deleteUser(id);
                        }

                        if (!newFriends.isEmpty()) {
                            new AsyncTask<Long, Void, Void>() {
                                @Override
                                protected Void doInBackground(Long... users) {
                                    try {
                                        for (long user : users) {
                                            mClient.ackAcceptedInvitation(user);
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
                                            mClient.ackRemovedFriend(id);
                                        }
                                    } catch (SmartMapClientException e) {
                                        Log.e("UpdateService", "Couldn't send acks!");
                                    }
                                    return null;
                                }
                            }.execute(removedFriends.toArray(new Long[removedFriends.size()]));
                        }
                    }
                }
            }.execute();
            mHandler.postDelayed(this, INVITE_UPDATE_DELAY);
        }
    };

    private final Runnable getEventInvitations = new Runnable() {
        @Override
        public void run() {
            new AsyncTask<Void, Void, List<Long>>() {
                @Override
                protected List<Long> doInBackground(Void... arg0) {
                    List<Long> invitations = null;
                    try {
                        invitations = mClient.getEventInvitations();
                    } catch (SmartMapClientException e) {
                        Log.e("UpdateService", "Couldn't retrieve event invitations!");
                    }
                    return invitations;
                }

                @Override
                protected void onPostExecute(List<Long> result) {
                    for (long eventId : result) {
                        new AsyncTask<Long, Void, Void>() {
                            @Override
                            protected Void doInBackground(Long... params) {
                                try {
                                    mClient.ackEventInvitation(params[0]);
                                } catch (SmartMapClientException e) {
                                    Log.e("UpdateService", "Couldn't ack event invitation!");
                                }
                                if (mNotificationsEnabled && mNotificationsForEventInvitations) {
                                    // TODO Notifications.newEventNotification(this, event);
                                }
                                return null;
                            }
                        }.execute(eventId);
                    }
                }
            }.execute();
            mHandler.postDelayed(this, INVITE_UPDATE_DELAY);
        }
    };

    public void downloadUserPicture(long id) {
        try {
            mHelper.setUserPicture(mClient.getProfilePicture(id), id);
        } catch (SmartMapClientException e) {
            Log.e("UpdateService", "Couldn't download picture #" + id + "!");
        }
    }

    private void loadSettings() {
        mPosUpdateDelay = mManager.getRefreshFrequency();
        mNotificationsEnabled = mManager.notificationsEnabled();
        mNotificationsForEventInvitations = mManager.notificationsForEventInvitations();
        mNotificationsForFriendRequests = mManager.notificationsForFriendRequests();
        mNotificationsForFriendshipConfirmations = mManager.notificationsForFriendshipConfirmations();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Utils.sContext == null) {
            Utils.sContext = this;
        }
        mManager = SettingsManager.initialize(this.getApplicationContext());
        mHelper = DatabaseHelper.initialize(this.getApplicationContext());
        mCache = ServiceContainer.getCache();
        mHelper.addOnInvitationListUpdateListener(this);
        mHelper.addOnInvitationStatusUpdateListener(this);
        mGeocoder = new Geocoder(this.getBaseContext(), Locale.US);
        mLocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        this.updateInvitationSet();
    }

    @Override
    public void onInvitationListUpdate() {

    }

    @Override
    public void onInvitationStatusUpdate(long userID, int newStatus) {
        // if the updated notification is among the pending ones
        // if (mInviterIds.contains(userID)) {
        if (newStatus == Invitation.ACCEPTED) {
            new AsyncTask<Long, Void, Void>() {
                @Override
                protected Void doInBackground(Long... ids) {
                    try {
                        for (long id : ids) {
                            mClient.acceptInvitation(id);
                        }
                    } catch (SmartMapClientException e) {
                        Log.e("UpdateService", "Couldn't accept request!");
                    }
                    return null;
                }
            }.execute(userID);
        } else if (newStatus == Invitation.REFUSED) {
            new AsyncTask<Long, Void, Void>() {
                @Override
                protected Void doInBackground(Long... ids) {
                    try {
                        for (long id : ids) {
                            mClient.declineInvitation(id);
                        }
                    } catch (SmartMapClientException e) {
                        Log.e("UpdateService", "Couldn't decline request!");
                    }
                    return null;
                }
            }.execute(userID);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.loadSettings();
        new AsyncLogin().execute();
        mHandler.removeCallbacks(friendsPosUpdate);
        mHandler.postDelayed(friendsPosUpdate, HANDLER_DELAY);
        mHandler.removeCallbacks(getInvitations);
        mHandler.postDelayed(getInvitations, HANDLER_DELAY);
        mHandler.removeCallbacks(nearEventsUpdate);
        mHandler.postDelayed(nearEventsUpdate, HANDLER_DELAY);
        mHandler.removeCallbacks(updateDatabase);
        mHandler.postDelayed(updateDatabase, HANDLER_DELAY);
        mHandler.removeCallbacks(ownPosUpdate);
        mHandler.postDelayed(ownPosUpdate, HANDLER_DELAY);
        mHandler.removeCallbacks(getEventInvitations);
        mHandler.postDelayed(getEventInvitations, HANDLER_DELAY);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        mLocManager.requestSingleUpdate(criteria, new MyLocationListener(), null);
        if (mLocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mLocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mPosUpdateDelay,
                MIN_DISTANCE, new MyLocationListener());
        }

        if (mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_DELAY,
                MIN_GPS_DISTANCE, new MyLocationListener());
        }

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                List<User> friends = mCache.getAllFriends();
                for (User user : friends) {
                    UpdateService.this.downloadUserPicture(user.getId());
                    ImmutableUser immutable =
                        new ImmutableUser(user.getId(), null, null, null, null, null,
                            mHelper.getPictureById(user.getId()), false);
                    mCache.updateFriend(immutable);
                }
                return null;
            }
        }.execute();

        return START_STICKY;
    }

    // Ugly workaround because of KitKat stopping services when app gets closed
    // (Android issue #63618)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(this.getApplicationContext(), this.getClass());
        restartService.setPackage(this.getPackageName());
        PendingIntent restartServicePending =
            PendingIntent.getService(this.getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService =
            (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + RESTART_DELAY,
            restartServicePending);
    }

    /**
     * Enables/disables friends position updates
     * 
     * @param isEnabled
     *            True if updates should be enabled
     */
    public void setFriendsPosUpdateEnabled(boolean isEnabled) {
        mFriendsPosEnabled = isEnabled;
        if (isEnabled) {
            mHandler.postDelayed(friendsPosUpdate, HANDLER_DELAY);
        }
    }

    public void updateInvitationSet() {
        mInviterIds = new HashSet<Long>();
        for (FriendInvitation invitation : mHelper.getUnansweredFriendInvitations()) {
            mInviterIds.add(invitation.getUserId());
        }
    }

    /**
     * AsyncTask to log in
     * 
     * @author ritterni
     */
    private class AsyncLogin extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                mClient.authServer(mManager.getUserName(), mManager.getFacebookID(), mManager.getToken());
            } catch (SmartMapClientException e) {
                Log.e("UpdateService", "Couldn't log in!");
            }
            return null;
        }
    }

    /**
     * A location listener
     * 
     * @author ritterni
     */
    private final class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location fix) {
            // do nothing if the new location is less accurate
            if (mManager.getLocation().distanceTo(fix) >= fix.getAccuracy()
                || fix.getAccuracy() <= mCurrentAccuracy) {
                mManager.setLocation(fix);
                mCurrentAccuracy = fix.getAccuracy();

                // Sets the location name
                try {
                    List<Address> addresses =
                        mGeocoder.getFromLocation(fix.getLatitude(), fix.getLongitude(), 1);

                    String locName = SettingsManager.DEFAULT_LOC_NAME;
                    if (!addresses.isEmpty()) {
                        locName = addresses.get(0).getLocality();
                    }
                    mManager.setLocationName(locName);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }
}