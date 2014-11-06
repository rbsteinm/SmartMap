package ch.epfl.smartmap.cache;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Used to get and set settings and local info using SharedPreferences
 * @author ritterni
 */
public class SettingsManager {
    public static final String PREFS_NAME = "SettingsFile";
    public static final String USER_ID = "UserID";
    public static final String USER_NAME = "UserName";
    public static final String PHONE_NUMBER = "PhoneNumber";
    public static final String EMAIL = "Email";
    public static final String TOKEN = "Token";
    public static final String COOKIE = "Cookie";
    public static final String HIDDEN = "Hidden";
    
    public static final long DEFAULT_ID = -1;
    public static final String DEFAULT_NAME = "No name";
    public static final String DEFAULT_NUMBER = "No number";
    public static final String DEFAULT_EMAIL = "No email";
    public static final long DEFAULT_TOKEN = -1;
    public static final String DEFAULT_COOKIE = "No cookie";
    
    private Context mContext;
    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mEditor;
    
	/**
	 * SettingsManager constructor
	 * @param context The app's context, needed to access the shared preferences
	 */
	public SettingsManager(Context context) {
	    mContext = context;
	    mSharedPref = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	    mEditor = mSharedPref.edit();
	}
	
	/**
	 * @return The local user's name if it is found, DEFAULT_NAME value otherwise
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
	 * @return The local user's phone number if it is found, DEFAULT_NUMBER value otherwise
	 */
	public String getUPhoneNumber() {
        return mSharedPref.getString(PHONE_NUMBER, DEFAULT_NUMBER);
    }
	
	/**
	 * @return The local user's email if it is found, DEFAULT_EMAIL value otherwise
	 */
	public String getEmail() {
        return mSharedPref.getString(EMAIL, DEFAULT_EMAIL);
    }
	
	/**
	 * @return The local user's Facebook token if it is found, DEFAULT_TOKEN value otherwise
	 */
	public long getToken() {
        return mSharedPref.getLong(TOKEN, DEFAULT_TOKEN);
    }
	
	/**
	 * @return The session cookie if it is found, DEFAULT_COOKIE value otherwise
	 */
	public String getCookie() {
        return mSharedPref.getString(COOKIE, DEFAULT_COOKIE);
    }
	
	/**
	 * @return True if hidden mode is enabled
	 */
	public boolean isHidden() {
        return mSharedPref.getBoolean(HIDDEN, false);
    }
	
	/**
	 * Stores the local user's name
	 * @param newName The user's new name
	 * @return True if the new value was successfully saved
	 */
	public boolean setUserName(String newName) {
        mEditor.putString(USER_NAME, newName);
        return mEditor.commit();
    }
    
    /**
     * Stores the user's ID
     * @param newID The user's new ID
     * @return True if the new value was successfully saved
     */
    public boolean setUserID(long newID) {
        mEditor.putLong(USER_ID, newID);
        return mEditor.commit();
    }
    
    /**
     * Stores the user's phone number
     * @param newNumber The user's new number
     * @return True if the new value was successfully saved
     */
    public boolean setUPhoneNumber(String newNumber) {
        mEditor.putString(PHONE_NUMBER, newNumber);
        return mEditor.commit();
    }
    
    /**
     * Stores the user's email
     * @param newEmail The new email address
     * @return True if the new value was successfully saved
     */
    public boolean setEmail(String newEmail) {
        mEditor.putString(EMAIL, newEmail);
        return mEditor.commit();
    }
    
    /**
     * Stores the Facebook token
     * @param newToken The new token
     * @return True if the new value was successfully saved
     */
    public boolean setToken(long newToken) {
        mEditor.putLong(TOKEN, newToken);
        return mEditor.commit();
    }
    
    /**
     * Stores the session cookie
     * @param newCookie The new cookie
     * @return True if the new value was successfully saved
     */
    public boolean setCookie(String newCookie) {
        mEditor.putString(COOKIE, newCookie);
        return mEditor.commit();
    }
    
    /**
     * Sets whether or not the local user is in hidden mode
     * @param isHidden True to enable hidden mode
     * @return True if the new value was successfully saved
     */
    public boolean setHidden(boolean isHidden) {
    	mEditor.putBoolean(HIDDEN, isHidden);
    	return mEditor.commit();
    }
}
