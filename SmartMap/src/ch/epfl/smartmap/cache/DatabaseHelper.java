package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;
import ch.epfl.smartmap.gui.Utils;
import ch.epfl.smartmap.listeners.OnDisplayableInformationsChangeListener;
import ch.epfl.smartmap.listeners.OnEventListUpdateListener;
import ch.epfl.smartmap.listeners.OnFilterListUpdateListener;
import ch.epfl.smartmap.listeners.OnFriendListUpdateListener;
import ch.epfl.smartmap.listeners.OnFriendsLocationUpdateListener;
import ch.epfl.smartmap.listeners.OnInvitationListUpdateListener;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * SQLite helper
 * 
 * @author ritterni
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 6;
	private static final String DATABASE_NAME = "SmartMapDB";

	private final List<OnFriendsLocationUpdateListener> mOnFriendsLocationUpdateListeners = new ArrayList<OnFriendsLocationUpdateListener>();
	private final List<OnFriendListUpdateListener> mOnFriendListUpdateListeners = new ArrayList<OnFriendListUpdateListener>();
	private final List<OnEventListUpdateListener> mOnEventListUpdateListeners = new ArrayList<OnEventListUpdateListener>();
	private final List<OnFilterListUpdateListener> mOnFilterListUpdateListeners = new ArrayList<OnFilterListUpdateListener>();
	private final List<OnInvitationListUpdateListener> mOnInvitationListUpdateListeners = new ArrayList<OnInvitationListUpdateListener>();
	private final Map<Displayable, List<OnDisplayableInformationsChangeListener>> mOnDisplayableInformationsChangeListeners = new HashMap<Displayable, List<OnDisplayableInformationsChangeListener>>();

	public static final String TABLE_USER = "users";
	public static final String TABLE_FILTER = "filters";
	public static final String TABLE_FILTER_USER = "filter_users";
	public static final String TABLE_EVENT = "events";
	public static final String TABLE_INVITATIONS = "invitations";
	public static final String TABLE_PENDING = "pending";

	private static final String KEY_USER_ID = "userID";
	private static final String KEY_NAME = "name";
	private static final String KEY_NUMBER = "number";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_LONGITUDE = "longitude";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_POSNAME = "posName";
	private static final String KEY_LASTSEEN = "lastSeen";
	private static final String KEY_VISIBLE = "isVisible";

	private static final String KEY_ID = "id";
	private static final String KEY_FILTER_ID = "filterID";
	private static final String KEY_STATUS = "status";

	private static final String KEY_DATE = "date";
	private static final String KEY_ENDDATE = "endDate";

	private static final String KEY_CREATOR_NAME = "creatorName";
	private static final String KEY_EVTDESC = "eventDescription";

	// Columns for the User table
	private static final String[] USER_COLUMNS = {KEY_USER_ID, KEY_NAME, KEY_NUMBER, KEY_EMAIL,
	    KEY_LONGITUDE, KEY_LATITUDE, KEY_POSNAME, KEY_LASTSEEN, KEY_VISIBLE};

	// Columns for the Filter table
	private static final String[] FILTER_COLUMNS = {KEY_ID, KEY_NAME};

	// Columns for the Filter/User table
	private static final String[] FILTER_USER_COLUMNS = {KEY_ID, KEY_FILTER_ID, KEY_USER_ID};

	// Columns for the Event table
	private static final String[] EVENT_COLUMNS = {KEY_ID, KEY_NAME, KEY_EVTDESC, KEY_USER_ID,
	    KEY_CREATOR_NAME, KEY_LONGITUDE, KEY_LATITUDE, KEY_POSNAME, KEY_DATE, KEY_ENDDATE};

	// Columns for the Invitations table
	private static final String[] INVITATION_COLUMNS = {KEY_ID, KEY_USER_ID, KEY_NAME, KEY_STATUS};

	// Columns for the pending requests table
	private static final String[] PENDING_COLUMNS = {KEY_USER_ID, KEY_NAME};

	// Table of users
	private static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "("
	    + KEY_USER_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_NUMBER + " TEXT," + KEY_EMAIL
	    + " TEXT," + KEY_LONGITUDE + " DOUBLE," + KEY_LATITUDE + " DOUBLE," + KEY_POSNAME + " TEXT,"
	    + KEY_LASTSEEN + " INTEGER," + KEY_VISIBLE + " INTEGER" + ")";

	// Table of filters
	private static final String CREATE_TABLE_FILTER = "CREATE TABLE IF NOT EXISTS " + TABLE_FILTER + "("
	    + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT" + ")";

	// Table that maps filters to users
	private static final String CREATE_TABLE_FILTER_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_FILTER_USER
	    + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FILTER_ID + " INTEGER," + KEY_USER_ID + " INTEGER"
	    + ")";

	// Table of events
	private static final String CREATE_TABLE_EVENT = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENT + "("
	    + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_EVTDESC + " TEXT," + KEY_USER_ID
	    + " INTEGER," + KEY_CREATOR_NAME + " TEXT," + KEY_LONGITUDE + " DOUBLE," + KEY_LATITUDE + " DOUBLE,"
	    + KEY_POSNAME + " TEXT," + KEY_DATE + " INTEGER," + KEY_ENDDATE + " INTEGER" + ")";

	// Table of invitations
	private static final String CREATE_TABLE_INVITATIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_INVITATIONS
	    + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_ID + " INTEGER," + KEY_NAME + " TEXT,"
	    + KEY_STATUS + " INTEGER" + ")";

	// Table of invitations
	private static final String CREATE_TABLE_PENDING = "CREATE TABLE IF NOT EXISTS " + TABLE_PENDING + "("
	    + KEY_USER_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT" + ")";

	private static DatabaseHelper mInstance;
	private static SQLiteDatabase mDatabase;

	/**
	 * DatabaseHelper constructor. Will be made private, so use initialize() or
	 * getInstance() instead.
	 * 
	 * @param context
	 *            The application's context, used to access the files
	 */
	@Deprecated
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Stores an event in the database. If there's already an event with the
	 * same ID, updates that event instead
	 * The event must have an ID (given by the server)!
	 * 
	 * @param event
	 *            The event to store
	 */
	public void addEvent(Event event) throws IllegalArgumentException {
		if (event.getID() < 0) {
			throw new IllegalArgumentException("Invalid event ID");
		}

		Cursor cursor = mDatabase.query(TABLE_EVENT, EVENT_COLUMNS, KEY_ID + " = ?",
		    new String[] {String.valueOf(event.getID())}, null, null, null, null);

		// We check if the event is already there
		if (!cursor.moveToFirst()) {
			ContentValues values = new ContentValues();
			values.put(KEY_ID, event.getID());
			values.put(KEY_NAME, event.getName());
			values.put(KEY_EVTDESC, event.getDescription());
			values.put(KEY_USER_ID, event.getCreator());
			values.put(KEY_CREATOR_NAME, event.getCreatorName());
			values.put(KEY_LONGITUDE, event.getLocation().getLongitude());
			values.put(KEY_LATITUDE, event.getLocation().getLatitude());
			values.put(KEY_POSNAME, event.getPositionName());
			values.put(KEY_DATE, event.getStartDate().getTimeInMillis());
			values.put(KEY_ENDDATE, event.getEndDate().getTimeInMillis());

			mDatabase.insert(TABLE_EVENT, null, values);
		} else {
			this.updateEvent(event);
		}

		cursor.close();

		this.notifyOnEventListUpdateListeners();
	}

	/**
	 * Adds a filter/userlist/friendlist to the database, and gives an ID to the
	 * filter
	 * 
	 * @param filter
	 *            The filter/list to add
	 * @return The ID of the newly added filter in the filter database
	 */
	public long addFilter(Filter filter) {
		// First we insert the filter in the table of lists
		ContentValues filterValues = new ContentValues();
		filterValues.put(KEY_NAME, filter.getListName());
		long filterID = mDatabase.insert(TABLE_FILTER, null, filterValues);

		// Then we add the filter-user pairs to another table
		ContentValues pairValues = null;
		for (long id : filter.getList()) {
			pairValues = new ContentValues();
			pairValues.put(KEY_FILTER_ID, filterID);
			pairValues.put(KEY_USER_ID, id);
			mDatabase.insert(TABLE_FILTER_USER, null, pairValues);
		}

		filter.setID(filterID); // sets an ID so the filter can be easily
		                        // accessed

		this.notifyOnFilterListUpdateListeners();
		return filterID;
	}

	/**
	 * Adds a pending friend request to the database
	 * 
	 * @param invitation
	 *            The FriendInvitation to ad to the database
	 */
	public void addFriendInvitation(FriendInvitation invitation) {
		ContentValues values = new ContentValues();
		values.put(KEY_USER_ID, invitation.getUserId());
		values.put(KEY_NAME, invitation.getUserName());
		values.put(KEY_STATUS, invitation.getStatus());

		mDatabase.insert(TABLE_INVITATIONS, null, values);

		this.notifyOnInvitationListUpdateListeners();
	}

	public void addOnDisplayableInformationsChangeListener(Displayable displayable,
	    OnDisplayableInformationsChangeListener listener) {
		Log.d("DATABASE", "add Listener");
		List<OnDisplayableInformationsChangeListener> listenerList = mOnDisplayableInformationsChangeListeners
		    .get(displayable);
		if (listenerList != null) {
			Log.d("DATABASE", "was not null");
			listenerList.add(listener);
		} else {
			Log.d("DATABASE", "was null");
			mOnDisplayableInformationsChangeListeners.put(displayable,
			    new ArrayList<OnDisplayableInformationsChangeListener>(Arrays.asList(listener)));
		}
	}

	public void addOnEventListUpdateListener(OnEventListUpdateListener listener) {
		mOnEventListUpdateListeners.add(listener);
	}

	public void addOnFilterListUpdateListener(OnFilterListUpdateListener listener) {
		mOnFilterListUpdateListeners.add(listener);
	}

	public void addOnFriendListUpdateListener(OnFriendListUpdateListener listener) {
		mOnFriendListUpdateListeners.add(listener);
	}

	public void addOnFriendsLocationUpdateListener(OnFriendsLocationUpdateListener listener) {
		mOnFriendsLocationUpdateListeners.add(listener);
	}

	public void addOnInvitationListUpdateListener(OnInvitationListUpdateListener listener) {
		mOnInvitationListUpdateListeners.add(listener);
	}

	/**
	 * Adds a pending sent friend request to the database
	 * 
	 * @param user
	 *            The user who was sent a request
	 */
	public void addPendingFriend(User user) {
		Cursor cursor = mDatabase.query(TABLE_PENDING, PENDING_COLUMNS, KEY_USER_ID + " = ?",
		    new String[] {String.valueOf(user.getID())}, null, null, null, null);

		if (!cursor.moveToFirst()) {
			ContentValues values = new ContentValues();
			values.put(KEY_USER_ID, user.getID());
			values.put(KEY_NAME, user.getName());

			mDatabase.insert(TABLE_PENDING, null, values);

		}
		cursor.close();
	}

	/**
	 * Adds a user to the internal database. If an user with the same ID already
	 * exists, updates that user instead.
	 * 
	 * @param user
	 *            The user to add to the database
	 */
	public void addUser(User user) {
		Cursor cursor = mDatabase.query(TABLE_USER, USER_COLUMNS, KEY_USER_ID + " = ?",
		    new String[] {String.valueOf(user.getID())}, null, null, null, null);

		if (!cursor.moveToFirst()) {
			ContentValues values = new ContentValues();
			values.put(KEY_USER_ID, user.getID());
			values.put(KEY_NAME, user.getName());
			values.put(KEY_NUMBER, user.getNumber());
			values.put(KEY_EMAIL, user.getEmail());
			values.put(KEY_LONGITUDE, user.getLocation().getLongitude());
			values.put(KEY_LATITUDE, user.getLocation().getLatitude());
			values.put(KEY_POSNAME, user.getLocationString());
			values.put(KEY_LASTSEEN, user.getLastSeen().getTimeInMillis());
			values.put(KEY_VISIBLE, user.isVisible() ? 1 : 0); // boolean to int

			mDatabase.insert(TABLE_USER, null, values);

		} else {
			this.updateUser(user);
		}

		cursor.close();

		this.notifyOnFriendListUpdateListeners();
	}

	/**
	 * Clears the database. Mainly for testing purposes.
	 */
	public void clearAll() {
		mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTER);
		mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTER_USER);
		mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
		mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_INVITATIONS);
		mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PENDING);

		this.onCreate(mDatabase);

		this.notifyOnFriendListUpdateListeners();
		this.notifyOnFriendsLocationUpdateListeners();
		this.notifyOnEventListUpdateListeners();
		this.notifyOnFilterListUpdateListeners();
	}

	/**
	 * Deletes an event from the database
	 * 
	 * @param event
	 *            The event to delete
	 */
	public void deleteEvent(long id) {

		mDatabase.delete(TABLE_EVENT, KEY_ID + " = ?", new String[] {String.valueOf(id)});

		this.notifyOnEventListUpdateListeners();
	}

	/**
	 * Deletes a filter from the database
	 * 
	 * @param filter
	 *            The filter to delete
	 */
	public void deleteFilter(long id) {
		// delete the filter from the table of filters
		mDatabase.delete(TABLE_FILTER, KEY_ID + " = ?", new String[] {String.valueOf(id)});

		// then delete all the rows that reference this filter in the
		// filter-user table
		mDatabase.delete(TABLE_FILTER_USER, KEY_FILTER_ID + " = ?", new String[] {String.valueOf(id)});

		this.notifyOnFilterListUpdateListeners();
	}

	/**
	 * Deletes an invitation from the database (call this when accepting or
	 * declining an invitation)
	 * 
	 * @param id
	 *            The inviter's id
	 */
	public void deleteInvitation(long id) {

		mDatabase.delete(TABLE_INVITATIONS, KEY_USER_ID + " = ?", new String[] {String.valueOf(id)});
	}

	/**
	 * Deletes a pending friend request from the database
	 * 
	 * @param id
	 *            The invited user's id
	 */
	public void deletePendingFriend(long id) {

		mDatabase.delete(TABLE_PENDING, KEY_USER_ID + " = ?", new String[] {String.valueOf(id)});
	}

	/**
	 * Deletes a user from the database
	 * 
	 * @param id
	 *            The user's id
	 */
	public void deleteUser(long id) {

		mDatabase.delete(TABLE_USER, KEY_USER_ID + " = ?", new String[] {String.valueOf(id)});

		// TODO : There should be no need to notify LocationListeners
		this.notifyOnFriendListUpdateListeners();
		this.notifyOnFriendsLocationUpdateListeners();
	}

	/**
	 * @return the {@code List} of all events
	 */
	public List<Event> getAllEvents() {
		ArrayList<Event> events = new ArrayList<Event>();

		String query = "SELECT  * FROM " + TABLE_EVENT;

		Cursor cursor = mDatabase.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				events.add(this.getEvent(cursor.getLong(cursor.getColumnIndex(KEY_ID))));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return events;
	}

	/**
	 * @return the {@code List} of all Filters
	 */
	public List<Filter> getAllFilters() {

		ArrayList<Filter> filters = new ArrayList<Filter>();

		String query = "SELECT  * FROM " + TABLE_FILTER;

		Cursor cursor = mDatabase.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				// using getFilter to add this row's filter to the list
				filters.add(this.getFilter(cursor.getLong(cursor.getColumnIndex(KEY_ID))));
			} while (cursor.moveToNext());
		}

		cursor.close();
		return filters;
	}

	/**
	 * @return the {@code List} of all friends
	 */
	public List<User> getAllFriends() {
		ArrayList<User> friends = new ArrayList<User>();

		String query = "SELECT  * FROM " + TABLE_USER;

		Cursor cursor = mDatabase.rawQuery(query, null);

		Friend friend = null;
		if (cursor.moveToFirst()) {
			do {
				friend = new Friend(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)),
				    cursor.getString(cursor.getColumnIndex(KEY_NAME)));
				friend.setNumber(cursor.getString(cursor.getColumnIndex(KEY_NUMBER)));
				friend.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
				friend.setLongitude(cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)));
				friend.setLatitude(cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)));
				friend.setPositionName(cursor.getString(cursor.getColumnIndex(KEY_POSNAME)));
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(KEY_LASTSEEN)));
				friend.setLastSeen(cal);
				friend.setVisible(cursor.getInt(cursor.getColumnIndex(KEY_VISIBLE)) == 1); // int
				                                                                           // to
				                                                                           // boolean
				friends.add(friend);
			} while (cursor.moveToNext());
		}

		cursor.close();
		return friends;
	}

	/**
	 * @param id
	 *            The event's ID
	 * @return The event associated to this ID
	 */
	public Event getEvent(long id) {

		// SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = mDatabase.query(TABLE_EVENT, EVENT_COLUMNS, KEY_ID + " = ?",
		    new String[] {String.valueOf(id)}, null, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}

		GregorianCalendar startDate = new GregorianCalendar();
		GregorianCalendar endDate = new GregorianCalendar();

		startDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(KEY_DATE)));
		endDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(KEY_ENDDATE)));

		Location loc = new Location("");
		loc.setLongitude(cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)));
		loc.setLatitude(cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)));

		PublicEvent event = new PublicEvent(cursor.getString(cursor.getColumnIndex(KEY_NAME)),
		    cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)), cursor.getString(cursor
		        .getColumnIndex(KEY_CREATOR_NAME)), startDate, endDate, loc);

		event.setID(id);
		event.setDescription(cursor.getString(cursor.getColumnIndex(KEY_EVTDESC)));
		event.setPositionName(cursor.getString(cursor.getColumnIndex(KEY_POSNAME)));

		cursor.close();

		return event;
	}

	/**
	 * Gets a specific filter by its id
	 * 
	 * @param name
	 *            The filter's id
	 * @return The filter as a FriendList object
	 */
	public Filter getFilter(long id) {

		// SQLiteDatabase db = this.getWritableDatabase();

		// First query to get the filter's name
		Cursor cursor = mDatabase.query(TABLE_FILTER, FILTER_COLUMNS, KEY_ID + " = ?",
		    new String[] {String.valueOf(id)}, null, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}

		DefaultFilter filter = new DefaultFilter(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
		filter.setID(id);

		// Second query to get the associated list of IDs
		cursor = mDatabase.query(TABLE_FILTER_USER, FILTER_USER_COLUMNS, KEY_FILTER_ID + " = ?",
		    new String[] {String.valueOf(id)}, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				filter.addUser(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)));
			} while (cursor.moveToNext());
		}

		cursor.close();
		return filter;
	}

	/**
	 * Returns a list of all pending received invitations
	 * 
	 * @return A list of FriendInvitations
	 */
	public List<Invitation> getFriendInvitations() {
		List<Invitation> invitations = new ArrayList<Invitation>();

		String query = "SELECT  * FROM " + TABLE_INVITATIONS;

		Cursor cursor = mDatabase.rawQuery(query, null);

		FriendInvitation invitation = null;
		if (cursor.moveToFirst()) {
			do {
				invitation = new FriendInvitation(cursor.getLong(cursor.getColumnIndex(KEY_ID)),
				    cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)), cursor.getString(cursor
				        .getColumnIndex(KEY_NAME)), cursor.getInt(cursor.getColumnIndex(KEY_STATUS)));

				invitations.add(invitation);
			} while (cursor.moveToNext());
		}

		cursor.close();
		return invitations;
	}

	/**
	 * Returns a list of all unanswered received invitations
	 * 
	 * @return A list of {@code FriendInvitation}
	 */
	public List<FriendInvitation> getUnansweredFriendInvitations() {
		List<FriendInvitation> invitations = new ArrayList<FriendInvitation>();

		String query = "SELECT  * FROM " + TABLE_INVITATIONS;

		Cursor cursor = mDatabase.rawQuery(query, null);

		FriendInvitation invitation = null;
		if (cursor.moveToFirst()) {
			do {
				int status = cursor.getInt(cursor.getColumnIndex(KEY_STATUS));
				if ((status == Invitation.READ) || (status == Invitation.UNREAD)) {
					invitation = new FriendInvitation(cursor.getLong(cursor.getColumnIndex(KEY_ID)),
					    cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)), cursor.getString(cursor
					        .getColumnIndex(KEY_NAME)), status);

					invitations.add(invitation);
				}
			} while (cursor.moveToNext());
		}

		cursor.close();
		return invitations;
	}

	/**
	 * Returns a list of all friend invitations with the specified status
	 * 
	 * @param status
	 *            The invitations status
	 * @return A list of {@code FriendInvitation}
	 */
	public List<FriendInvitation> getFriendInvitationsByStatus(int status) {
		List<FriendInvitation> invitations = new ArrayList<FriendInvitation>();

		String query = "SELECT  * FROM " + TABLE_INVITATIONS;

		Cursor cursor = mDatabase.rawQuery(query, null);

		FriendInvitation invitation = null;
		if (cursor.moveToFirst()) {
			do {
				int currentStatus = cursor.getInt(cursor.getColumnIndex(KEY_STATUS));
				if (currentStatus == status) {
					invitation = new FriendInvitation(cursor.getLong(cursor.getColumnIndex(KEY_ID)),
					    cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)), cursor.getString(cursor
					        .getColumnIndex(KEY_NAME)), currentStatus);

					invitations.add(invitation);
				}
			} while (cursor.moveToNext());
		}

		cursor.close();
		return invitations;
	}

	/**
	 * Returns a list of all pending friends
	 * 
	 * @return A list of users who were sent friend requests
	 */
	public List<User> getPendingFriends() {
		List<User> friends = new ArrayList<User>();

		String query = "SELECT  * FROM " + TABLE_PENDING;

		Cursor cursor = mDatabase.rawQuery(query, null);

		Friend friend = null;
		if (cursor.moveToFirst()) {
			do {
				friend = new Friend(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)),
				    cursor.getString(cursor.getColumnIndex(KEY_NAME)));

				friends.add(friend);
			} while (cursor.moveToNext());
		}

		cursor.close();
		return friends;
	}

	/**
	 * Gets a user from the database
	 * 
	 * @param id
	 *            The user's unique ID
	 * @return The user as a Friend object
	 */
	public User getUser(long id) {

		Cursor cursor = mDatabase.query(TABLE_USER, USER_COLUMNS, KEY_USER_ID + " = ?",
		    new String[] {String.valueOf(id)}, null, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}

		Friend friend = new Friend(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)),
		    cursor.getString(cursor.getColumnIndex(KEY_NAME)));
		friend.setNumber(cursor.getString(cursor.getColumnIndex(KEY_NUMBER)));
		friend.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
		friend.setLongitude(cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)));
		friend.setLatitude(cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)));
		friend.setPositionName(cursor.getString(cursor.getColumnIndex(KEY_POSNAME)));
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(KEY_LASTSEEN)));
		friend.setLastSeen(cal);
		friend.setVisible(cursor.getInt(cursor.getColumnIndex(KEY_VISIBLE)) == 1);

		cursor.close();

		return friend;
	}

	/**
	 * Fills the friend database with server data
	 */
	public void initializeAllFriends() {
		NetworkSmartMapClient client = NetworkSmartMapClient.getInstance();
		try {
			List<Long> friends = client.getFriendsIds();
			for (long userID : friends) {
				this.addUser(client.getUserInfo(userID));
			}
		} catch (SmartMapClientException e) {
			Log.e("UpdateService", e.getMessage());
		}

		this.notifyOnFriendListUpdateListeners();
		this.notifyOnFriendsLocationUpdateListeners();
	}

	private void notifyOnDisplayableInformationListeners(Displayable d) {
		if (mOnDisplayableInformationsChangeListeners.get(d) != null) {
			for (OnDisplayableInformationsChangeListener listener : mOnDisplayableInformationsChangeListeners
			    .get(d)) {
				listener.onDisplayableInformationsChange();
			}
		}
	}

	private void notifyOnEventListUpdateListeners() {
		for (OnEventListUpdateListener listener : mOnEventListUpdateListeners) {
			listener.onEventListUpdate();
		}
	}

	private void notifyOnFilterListUpdateListeners() {
		for (OnFilterListUpdateListener listener : mOnFilterListUpdateListeners) {
			listener.onFilterListUpdate();
		}
	}

	private void notifyOnFriendListUpdateListeners() {
		for (OnFriendListUpdateListener listener : mOnFriendListUpdateListeners) {
			listener.onFriendListUpdate();
		}
	}

	private void notifyOnFriendsLocationUpdateListeners() {
		for (OnFriendsLocationUpdateListener listener : mOnFriendsLocationUpdateListeners) {
			listener.onFriendsLocationChange();
		}
	}

	private void notifyOnInvitationListUpdateListeners() {
		for (OnInvitationListUpdateListener listener : mOnInvitationListUpdateListeners) {
			listener.onInvitationListUpdate();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_USER);
		db.execSQL(CREATE_TABLE_FILTER);
		db.execSQL(CREATE_TABLE_FILTER_USER);
		db.execSQL(CREATE_TABLE_EVENT);
		db.execSQL(CREATE_TABLE_INVITATIONS);
		db.execSQL(CREATE_TABLE_PENDING);

		this.notifyOnFriendListUpdateListeners();
		this.notifyOnFriendsLocationUpdateListeners();
		this.notifyOnEventListUpdateListeners();
		this.notifyOnFilterListUpdateListeners();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTER_USER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVITATIONS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PENDING);
		this.onCreate(db);

		this.notifyOnFriendListUpdateListeners();
		this.notifyOnFriendsLocationUpdateListeners();
		this.notifyOnEventListUpdateListeners();
		this.notifyOnFilterListUpdateListeners();
	}

	/**
	 * Fully updates the friends database (not only positions)
	 */
	public void refreshFriendsInfo() {
		List<User> friends = this.getAllFriends();
		NetworkSmartMapClient client = NetworkSmartMapClient.getInstance();
		for (User f : friends) {
			try {
				this.updateUser(client.getUserInfo(f.getID()));
			} catch (SmartMapClientException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Uses listFriendsPos() to update the entire friends database with updated
	 * positions
	 * 
	 * @return The number of rows (i.e. friends) that were updated
	 */
	public int refreshFriendsPos() {
		int updatedRows = 0;
		try {
			List<User> updatedUsers = NetworkSmartMapClient.getInstance().listFriendsPos();

			for (User user : updatedUsers) {
				this.updateUser(user);
				updatedRows++;
			}
		} catch (SmartMapClientException e) {
			e.printStackTrace();
		}
		this.notifyOnFriendsLocationUpdateListeners();
		return updatedRows;
	}

	/**
	 * Updates an event
	 * 
	 * @param event
	 *            The event to update
	 * @return The number of rows that were affected
	 */
	public int updateEvent(Event event) {

		ContentValues values = new ContentValues();
		values.put(KEY_ID, event.getID());
		values.put(KEY_NAME, event.getName());
		values.put(KEY_EVTDESC, event.getDescription());
		values.put(KEY_USER_ID, event.getCreator());
		values.put(KEY_CREATOR_NAME, event.getCreatorName());
		values.put(KEY_LONGITUDE, event.getLocation().getLongitude());
		values.put(KEY_LATITUDE, event.getLocation().getLatitude());
		values.put(KEY_POSNAME, event.getPositionName());
		values.put(KEY_DATE, event.getStartDate().getTimeInMillis());
		values.put(KEY_ENDDATE, event.getEndDate().getTimeInMillis());

		int rows = mDatabase.update(TABLE_EVENT, values, KEY_ID + " = ?",
		    new String[] {String.valueOf(event.getID())});

		if (rows > 0) {
			this.notifyOnDisplayableInformationListeners(event);
		}

		return rows;
	}

	/**
	 * Updates a filter
	 * 
	 * @param filter
	 *            The updated filter
	 */
	public void updateFilter(Filter filter) {

		this.deleteFilter(filter.getID());
		this.addFilter(filter);
		// not sure if there's a more efficient way

		this.notifyOnFilterListUpdateListeners();
	}

	/**
	 * Updates a {@code FriendInvitation} in the database
	 * 
	 * @param invitation
	 *            The {@code FriendInvitation} to update
	 * @return The number of rows that were updated
	 */
	public int updateFriendInvitation(FriendInvitation invitation) {

		ContentValues values = new ContentValues();
		values.put(KEY_ID, invitation.getID());
		values.put(KEY_NAME, invitation.getUserName());
		values.put(KEY_USER_ID, invitation.getUserId());
		values.put(KEY_STATUS, invitation.getStatus());

		int rows = mDatabase.update(TABLE_INVITATIONS, values, KEY_ID + " = ?",
		    new String[] {String.valueOf(invitation.getID())});

		if (rows > 0) {
			this.notifyOnInvitationListUpdateListeners();
		}

		return rows;
	}

	/**
	 * Updates a user's values
	 * 
	 * @param user
	 *            The user to update
	 * @return The number of rows that were updated
	 */
	public int updateUser(User user) {
		ContentValues values = new ContentValues();

		// Check for default values
		values.put(KEY_USER_ID, user.getID());
		if (user.getName() != Friend.NO_NAME) {
			values.put(KEY_NAME, user.getName());
		}
		if (user.getNumber() != Friend.NO_NUMBER) {
			values.put(KEY_NUMBER, user.getNumber());
		}
		if (user.getEmail() != Friend.NO_EMAIL) {
			values.put(KEY_EMAIL, user.getEmail());
		}
		if ((user.getLocation().getLatitude() != 0.0) || (user.getLocation().getLongitude() != 0.0)) {
			values.put(KEY_LONGITUDE, user.getLocation().getLongitude());
			values.put(KEY_LATITUDE, user.getLocation().getLatitude());
		}
		if (user.getLocationString() != Utils.UNKNOWN_LOCATION) {
			values.put(KEY_POSNAME, user.getLocationString());
		}
		if (user.getLastSeen().getTimeInMillis() != 0) {
			values.put(KEY_LASTSEEN, user.getLastSeen().getTimeInMillis());
		}

		values.put(KEY_VISIBLE, user.isVisible() ? 1 : 0); // boolean to int

		int rows = mDatabase.update(TABLE_USER, values, KEY_USER_ID + " = ?",
		    new String[] {String.valueOf(user.getID())});
		if (rows > 0) {
			this.notifyOnDisplayableInformationListeners(user);
		}
		return rows;
	}

	/**
	 * Updates a user's location
	 * 
	 * @param id
	 *            The user's id
	 * @param location
	 *            The user's new location
	 */
	public void updateUserPos(long id, Location location) {
		User friend = this.getUser(id);
		friend.setLocation(location);
		this.updateUser(friend);

		this.notifyOnFriendsLocationUpdateListeners();
		this.notifyOnDisplayableInformationListeners(friend);
	}

	/**
	 * @return The instance of DatabaseHelper
	 */
	public static DatabaseHelper getInstance() {
		return mInstance;
	}

	/**
	 * Initializes the database helper (should be called once when starting the
	 * app)
	 * 
	 * @param context
	 *            The app's context, needed to access the files
	 * @return The DatabaseHelper instance
	 */
	public static DatabaseHelper initialize(Context context) {
		if (mInstance == null) {
			mInstance = new DatabaseHelper(context);
			mDatabase = mInstance.getWritableDatabase();
		}
		return mInstance;
	}
}