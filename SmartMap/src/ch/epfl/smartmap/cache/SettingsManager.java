package ch.epfl.smartmap.cache;

import android.content.Context;

/**
 * Used to get and set settings and local info
 * @author ritterni
 */
public class SettingsManager {
	private long mId;
	private String mName;
	private String mPhoneNumber;
	private String mEmail;
	private long mToken;
	private String mCookie;
	
	private DatabaseHelper mHelper;
	
	/**
	 * @param The app's context (used to access the database)
	 * @return A SettingsManager containing info from the database
	 */
	public SettingsManager getManager(Context context) {
		DatabaseHelper dbh = new DatabaseHelper(context);
		SettingsManager settings = dbh.getSettings();
		settings.setHelper(dbh);
		return settings;
	}
	
	/**
	 * Used by the database helper to return settings data
	 * @param id The local user's id
	 * @param name The local user's name
	 * @param number The local user's phone number
	 * @param email The local user's email address
	 * @param token The local user's Facebook token
	 * @param cookie The current session cookie
	 */
	protected SettingsManager(long id, String name, String number, String email, long token, String cookie) {
		mId = id;
		mName = name;
		mPhoneNumber = number;
		mEmail = email;
		mToken = token;
		mCookie = cookie;
	}
	
	public long getID() {
		return mId;
	}

	public String getName() {
		return mName;
	}

	public String getNumber() {
		return mPhoneNumber;
	}

	public String getEmail() {
		return mEmail;
	}
	
	public long getToken() {
		return mToken;
	}
	
	public String getCookie() {
		return mCookie;
	}
	
	public void setID() {
		
	}
	
	protected void setHelper(DatabaseHelper dbh) {
		mHelper = dbh;
	}
}
