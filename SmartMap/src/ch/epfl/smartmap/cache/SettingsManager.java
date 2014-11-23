package ch.epfl.smartmap.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

/**
 * Used to get and set settings and local info using SharedPreferences
 * 
 * @author ritterni
 * @author SpicyCH (add support for the user settings - we might want to change the design if my methods are
 *         bottlenecks)
 */
public class SettingsManager {
    public static final String PREFS_NAME = "settings";
    public static final String USER_ID = "UserID";
    public static final String USER_NAME = "UserName";
    public static final String PHONE_NUMBER = "PhoneNumber";
    public static final String EMAIL = "Email";
    public static final String TOKEN = "Token";
    public final static String FB_ID = "FacebookID";
    public static final String COOKIE = "Cookie";
    public static final String HIDDEN = "Hidden";
    public static final String LONGITUDE = "Longitude";
    public static final String LATITUDE = "Latitude";
    public static final String LOCATION_NAME = "LocName";

    public static final long DEFAULT_ID = -1;
    public static final String DEFAULT_NAME = "No name";
    public static final String DEFAULT_NUMBER = "No number";
    public static final String DEFAULT_EMAIL = "No email";
    public static final String DEFAULT_TOKEN = "No token";
    public static final long DEFAULT_FB_ID = -1;
    public static final String DEFAULT_COOKIE = "No cookie";
    public static final String DEFAULT_LOC_NAME = "";

    private final Context mContext;
    private final SharedPreferences mSharedPref;
    private final SharedPreferences.Editor mEditor;
    private static SettingsManager mInstance;

    // The following constant Strings must match the keys defined in
    // res/xml/pref_notifications.xml
    private final static String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private final static String KEY_FRIEND_REQUEST = "notifications_friend_requests";
    private final static String KEY_FRIENDSHIP_CONFIRMATIONS = "notifications_friendship_confirmations";
    private final static String KEY_EVENT_INVITATIONS = "notifications_event_invitations";
    private final static String KEY_EVENT_PROXIMITY = "notifications_event_proximity";
    private final static String KEY_VIBRATE = "notifications_vibrate";
    private static final String KEY_ALWAYS_SHARE = "general_always_share";
    private static final String KEY_REFRESH_FREQUENCY = "refresh_frequency";
    private static final String KEY_TIME_TO_WAIT_BEFORE_HIDING_FRIENDS = "last_seen_max";
    private static final String KEY_PUBLIC_EVENTS = "events_show_public";
    private static final String KEY_PRIVATE_EVENTS = "events_show_private";

    /**
     * @return The SettingsManager instance
     */
    public static SettingsManager getInstance() {
        return mInstance;
    }

    /**
     * Initializes the settings manager (should be called once when starting the
     * app)
     * 
     * @param context
     *            The app's context, needed to access the shared preferences
     * @return The SettingsManager instance
     */
    public static SettingsManager initialize(Context context) {
        if (mInstance == null) {
            mInstance = new SettingsManager(context);
        }
        return mInstance;
    }

    /**
     * <<<<<<< HEAD
     * SettingsManager constructor. Will be made private, use initialize() or getInstance() instead.
     * =======
     * SettingsManager constructor. Will be made private, use initialize() or
     * getInstance() instead.
     * >>>>>>> service-2
     * 
     * @param context
     *            The app's context, needed to access the shared preferences
     */
    @Deprecated
    public SettingsManager(Context context) {
        mContext = context;
        mSharedPref = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPref.edit();
    }

    /**
     * @return <code>true</code> if the user agreed to share his position even when the app is closed,
     *         <code>false</code> otherwise.
     * @author SpicyCH
     */
    public boolean alwaysShare() {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(KEY_ALWAYS_SHARE, true);
    }

    /**
     * Clears the settings
     * 
     * @return True if the settings were cleared successfully
     */
    public boolean clearAll() {
        mEditor.clear();
        return mEditor.commit();
    }

    /**
     * @return The session cookie if it is found, DEFAULT_COOKIE value otherwise
     */
    public String getCookie() {
        return mSharedPref.getString(COOKIE, DEFAULT_COOKIE);
    }

    /**
     * @return The local user's email if it is found, DEFAULT_EMAIL value otherwise
     */
    public String getEmail() {
        return mSharedPref.getString(EMAIL, DEFAULT_EMAIL);
    }

    /**
     * @return The local user's Facebook ID if it is found, DEFAULT_FB_ID value otherwise
     */
    public long getFacebookID() {
        return mSharedPref.getLong(FB_ID, DEFAULT_FB_ID);
    }

    /**
     * @return The local user's current position as a Location object
     */
    public Location getLocation() {
        Location loc = new Location("");
        // Shared prefs can't store doubles
        loc.setLongitude(Double.longBitsToDouble(mSharedPref.getLong(LONGITUDE, 0)));
        loc.setLatitude(Double.longBitsToDouble(mSharedPref.getLong(LATITUDE, 0)));
        return loc;
    }

    /**
     * @return the frequence in seconds at which we fetch and upload the datas. Used by the service.
     * @author SpicyCH
     */
    public int getRefreshFrequency() {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(mContext).getString(
            KEY_REFRESH_FREQUENCY, "10"));
    }

    /**
     * @return the time to wait in minutes before hiding inactive friends from the map.
     * @author SpicyCH
     */
    public int getTimeToWaitBeforeHidingFriends() {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(mContext).getString(
            KEY_TIME_TO_WAIT_BEFORE_HIDING_FRIENDS, "30"));
    }

    /**
     * @return The local user's Facebook token if it is found, DEFAULT_TOKEN value otherwise
     */
    public String getToken() {
        return mSharedPref.getString(TOKEN, DEFAULT_TOKEN);
    }

    /**
     * @return The local user's phone number if it is found, DEFAULT_NUMBER value otherwise
     */
    public String getUPhoneNumber() {
        return mSharedPref.getString(PHONE_NUMBER, DEFAULT_NUMBER);
    }

    /**
     * @return The local user's ID if it is found, DEFAULT_ID value otherwise
     */
    public long getUserID() {
        return mSharedPref.getLong(USER_ID, DEFAULT_ID);
    }

    /**
     * @return The local user's name if it is found, DEFAULT_NAME value otherwise
     */
    public String getUserName() {
        return mSharedPref.getString(USER_NAME, DEFAULT_NAME);
    }

    /**
     * @return True if hidden mode is enabled
     */
    public boolean isHidden() {
        return mSharedPref.getBoolean(HIDDEN, false);
    }

    /**
     * @return <code>true</code> if the user enabled the notifications, <code>false</code> otherwise.
     * @author SpicyCH
     */
    public boolean notificationsEnabled() {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(KEY_NOTIFICATIONS_ENABLED,
            true);
    }

    /**
     * @return <code>true</code> if the user enabled the notifications for event invitations and the user
     *         activated the notifications in general, <code>false</code> otherwise.
     * @author SpicyCH
     */
    public boolean notificationsForEventInvitations() {
        return this.notificationsEnabled() ? PreferenceManager.getDefaultSharedPreferences(mContext)
            .getBoolean(KEY_EVENT_INVITATIONS, true) : false;
    }

    /**
     * @return <code>true</code> if the user enabled the notifications for event proximity and the user
     *         activated the notifications in general, <code>false</code> otherwise.
     * @author SpicyCH
     */
    public boolean notificationsForEventProximity() {
        return this.notificationsEnabled() ? PreferenceManager.getDefaultSharedPreferences(mContext)
            .getBoolean(KEY_EVENT_PROXIMITY, true) : false;
    }

    /**
     * @return <code>true</code> if the user enabled the notifications for friend requests and the user
     *         activated the notifications in general, <code>false</code> otherwise.
     * @author SpicyCH
     */
    public boolean notificationsForFriendRequests() {
        return this.notificationsEnabled() ? PreferenceManager.getDefaultSharedPreferences(mContext)
            .getBoolean(KEY_FRIEND_REQUEST, true) : false;
    }

    /**
     * A friendship confirmation happens when another user accepts your friend request.
     * 
     * @return <code>true</code> if the user enabled the notifications for friendship confirmations and the
     *         user activated the notifications in general, <code>false</code> otherwise.
     * @author SpicyCH
     */
    public boolean notificationsForFriendshipConfirmations() {
        return this.notificationsEnabled() ? PreferenceManager.getDefaultSharedPreferences(mContext)
            .getBoolean(KEY_FRIENDSHIP_CONFIRMATIONS, true) : false;
    }

    /**
     * @return <code>true</code> if the user enabled the notifications vibrations and the user activated the
     *         notifications in general, <code>false</code> otherwise.
     * @author SpicyCH
     */
    public boolean notificationsVibrate() {
        return this.notificationsEnabled() ? PreferenceManager.getDefaultSharedPreferences(mContext)
            .getBoolean(KEY_VIBRATE, true) : false;
    }

    /**
     * Stores the session cookie
     * 
     * @param newCookie
     *            The new cookie
     * @return True if the new value was successfully saved
     */
    public boolean setCookie(String newCookie) {
        mEditor.putString(COOKIE, newCookie);
        return mEditor.commit();
    }

    /**
     * Stores the user's email
     * 
     * @param newEmail
     *            The new email address
     * @return True if the new value was successfully saved
     */
    public boolean setEmail(String newEmail) {
        mEditor.putString(EMAIL, newEmail);
        return mEditor.commit();
    }

    /**
     * Stores the user's Facebook ID
     * 
     * @param newID
     *            The user's new Facebook ID
     * @return True if the new value was successfully saved
     */
    public boolean setFacebookID(long newID) {
        mEditor.putLong(FB_ID, newID);
        return mEditor.commit();
    }

    /**
     * Sets whether or not the local user is in hidden mode
     * 
     * @param isHidden
     *            True to enable hidden mode
     * @return True if the new value was successfully saved
     */
    public boolean setHidden(boolean isHidden) {
        mEditor.putBoolean(HIDDEN, isHidden);
        return mEditor.commit();
    }

    /**
     * Stores the local users location
     * 
     * @param loc
     *            The location to be stored
     * @return True if the location was stored successfully
     */
    public boolean setLocation(Location loc) {
        mEditor.putLong(LONGITUDE, Double.doubleToRawLongBits(loc.getLongitude()));
        mEditor.putLong(LATITUDE, Double.doubleToRawLongBits(loc.getLatitude()));
        return mEditor.commit();
    }

    /**
     * Change the location name of the local user
     * 
     * @param locName
     *            The new location name (e.g. city)
     * @return If the name was stored successfully
     */
    public boolean setLocationName(String locName) {
        mEditor.putString(LOCATION_NAME, locName);
        return mEditor.commit();
    }

    /**
     * Stores the Facebook token
     * 
     * @param newToken
     *            The new token
     * @return True if the new value was successfully saved
     */
    public boolean setToken(String newToken) {
        mEditor.putString(TOKEN, newToken);
        return mEditor.commit();
    }

    /**
     * Stores the user's phone number
     * 
     * @param newNumber
     *            The user's new number
     * @return True if the new value was successfully saved
     */
    public boolean setUPhoneNumber(String newNumber) {
        mEditor.putString(PHONE_NUMBER, newNumber);
        return mEditor.commit();
    }

    /**
     * Stores the user's ID
     * 
     * @param newID
     *            The user's new ID
     * @return True if the new value was successfully saved
     */
    public boolean setUserID(long newID) {
        mEditor.putLong(USER_ID, newID);
        return mEditor.commit();
    }

    /**
     * Stores the local user's name
     * 
     * @param newName
     *            The user's new name
     * @return True if the new value was successfully saved
     */
    public boolean setUserName(String newName) {
        mEditor.putString(USER_NAME, newName);
        return mEditor.commit();
    }

    /**
     * @return <code>true</code> if the user wants to see his private events on his map, <code>false</code>
     *         otherwise.
     * @author SpicyCH
     */
    public boolean showPrivateEvents() {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(KEY_PRIVATE_EVENTS, true);
    }

    /**
     * @return <code>true</code> if the user wants to see public events on his map, <code>false</code>
     *         otherwise.
     * @author SpicyCH
     */
    public boolean showPublicEvents() {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(KEY_PUBLIC_EVENTS, true);
    }
}
