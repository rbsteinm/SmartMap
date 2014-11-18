package ch.epfl.smartmap.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

/**
 * Used to get and set settings and local info using SharedPreferences
 * 
 * @author ritterni
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

	public static final long DEFAULT_ID = -1;
	public static final String DEFAULT_NAME = "No name";
	public static final String DEFAULT_NUMBER = "No number";
	public static final String DEFAULT_EMAIL = "No email";
	public static final String DEFAULT_TOKEN = "No token";
	public static final long DEFAULT_FB_ID = -1;
	public static final String DEFAULT_COOKIE = "No cookie";

	private final Context mContext;
	private final SharedPreferences mSharedPref;
	private final SharedPreferences.Editor mEditor;
	private static SettingsManager mInstance;

	/**
	 * SettingsManager constructor. Will be made private, use initialize() or
	 * getInstance() instead.
	 * 
	 * @param context
	 *            The app's context, needed to access the shared preferences
	 */
	@Deprecated
	public SettingsManager(Context context) {
		mContext = context;
		mSharedPref = mContext.getSharedPreferences(PREFS_NAME,
		    Context.MODE_PRIVATE);
		mEditor = mSharedPref.edit();
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
		mInstance = new SettingsManager(context);
		return mInstance;
	}

	/**
	 * @return The SettingsManager instance
	 */
	public static SettingsManager getInstance() {
		return mInstance;
	}

	/**
	 * @return The local user's name if it is found, DEFAULT_NAME value
	 *         otherwise
	 */
	public String getUserName() {
		return mSharedPref.getString(USER_NAME, DEFAULT_NAME);
	}

	/**
	 * @return The local user's ID if it is found, DEFAULT_ID value otherwise
	 */
	public long getUserID() {
		return mSharedPref.getLong(USER_ID, DEFAULT_ID);
	}

	/**
	 * @return The local user's Facebook ID if it is found, DEFAULT_FB_ID value
	 *         otherwise
	 */
	public long getFacebookID() {
		return mSharedPref.getLong(FB_ID, DEFAULT_FB_ID);
	}

	/**
	 * @return The local user's phone number if it is found, DEFAULT_NUMBER
	 *         value otherwise
	 */
	public String getUPhoneNumber() {
		return mSharedPref.getString(PHONE_NUMBER, DEFAULT_NUMBER);
	}

	/**
	 * @return The local user's email if it is found, DEFAULT_EMAIL value
	 *         otherwise
	 */
	public String getEmail() {
		return mSharedPref.getString(EMAIL, DEFAULT_EMAIL);
	}

	/**
	 * @return The local user's Facebook token if it is found, DEFAULT_TOKEN
	 *         value otherwise
	 */
	public String getToken() {
		return mSharedPref.getString(TOKEN, DEFAULT_TOKEN);
	}

	/**
	 * @return The session cookie if it is found, DEFAULT_COOKIE value otherwise
	 */
	public String getCookie() {
		return mSharedPref.getString(COOKIE, DEFAULT_COOKIE);
	}

	/**
	 * @return The local user's current position as a Location object
	 */
	public Location getLocation() {
		Location loc = new Location("");
		// Shared prefs can't store doubles
		loc.setLongitude(Double.longBitsToDouble(mSharedPref.getLong(LONGITUDE,
		    0)));
		loc.setLatitude(Double.longBitsToDouble(mSharedPref
		    .getLong(LATITUDE, 0)));
		return loc;
	}

	/**
	 * @return True if hidden mode is enabled
	 */
	public boolean isHidden() {
		return mSharedPref.getBoolean(HIDDEN, false);
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
	 * @return True if the location was stores successfully
	 */
	public boolean setLocation(Location loc) {
		mEditor.putLong(LONGITUDE,
		    Double.doubleToRawLongBits(loc.getLongitude()));
		mEditor
		    .putLong(LATITUDE, Double.doubleToRawLongBits(loc.getLatitude()));
		return mEditor.commit();
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
}
