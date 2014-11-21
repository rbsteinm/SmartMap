package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;
import android.util.LongSparseArray;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * SQLite helper
 * 
 * @author ritterni
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "SmartMapDB";

    private final List<FriendsLocationListener> mLocationsListeners = new ArrayList<FriendsLocationListener>();
    private final List<FriendsListener> mFriendsListeners = new ArrayList<FriendsListener>();
    private final List<EventsListener> mEventsListeners = new ArrayList<EventsListener>();
    private final List<FiltersListener> mFiltersListeners = new ArrayList<FiltersListener>();

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

    private static final String KEY_DATE = "date";
    private static final String KEY_ENDDATE = "endDate";

    private static final String KEY_CREATOR_NAME = "creatorName";
    private static final String KEY_EVTDESC = "eventDescription";

    // Columns for the User table
    private static final String[] USER_COLUMNS = {KEY_USER_ID, KEY_NAME, KEY_NUMBER, KEY_EMAIL, KEY_LONGITUDE,
        KEY_LATITUDE, KEY_POSNAME, KEY_LASTSEEN, KEY_VISIBLE};

    // Columns for the Filter table
    private static final String[] FILTER_COLUMNS = {KEY_ID, KEY_NAME};

    // Columns for the Filter/User table
    private static final String[] FILTER_USER_COLUMNS = {KEY_ID, KEY_FILTER_ID, KEY_USER_ID};

    // Columns for the Event table
    private static final String[] EVENT_COLUMNS = {KEY_ID, KEY_NAME, KEY_EVTDESC, KEY_USER_ID, KEY_CREATOR_NAME,
        KEY_LONGITUDE, KEY_LATITUDE, KEY_POSNAME, KEY_DATE, KEY_ENDDATE};

    // Columns for the Invitations table
    private static final String[] INVITATION_COLUMNS = {KEY_ID, KEY_NAME};

    // Columns for the Invitations table
    private static final String[] PENDING_COLUMNS = {KEY_ID, KEY_NAME};

    // Table of users
    private static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "(" + KEY_USER_ID
        + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_NUMBER + " TEXT," + KEY_EMAIL + " TEXT," + KEY_LONGITUDE
        + " DOUBLE," + KEY_LATITUDE + " DOUBLE," + KEY_POSNAME + " TEXT," + KEY_LASTSEEN + " INTEGER," + KEY_VISIBLE
        + " INTEGER" + ")";

    // Table of filters
    private static final String CREATE_TABLE_FILTER = "CREATE TABLE IF NOT EXISTS " + TABLE_FILTER + "(" + KEY_ID
        + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT" + ")";

    // Table that maps filters to users
    private static final String CREATE_TABLE_FILTER_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_FILTER_USER + "("
        + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FILTER_ID + " INTEGER," + KEY_USER_ID + " INTEGER" + ")";

    // Table of events
    private static final String CREATE_TABLE_EVENT = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENT + "(" + KEY_ID
        + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_EVTDESC + " TEXT," + KEY_USER_ID + " INTEGER,"
        + KEY_CREATOR_NAME + " TEXT," + KEY_LONGITUDE + " DOUBLE," + KEY_LATITUDE + " DOUBLE," + KEY_POSNAME + " TEXT,"
        + KEY_DATE + " INTEGER," + KEY_ENDDATE + " INTEGER" + ")";

    // Table of invitations
    private static final String CREATE_TABLE_INVITATIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_INVITATIONS + "("
        + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT" + ")";

    // Table of invitations
    private static final String CREATE_TABLE_PENDING = "CREATE TABLE IF NOT EXISTS " + TABLE_PENDING + "(" + KEY_ID
        + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT" + ")";

    private static DatabaseHelper mInstance;
    private static SQLiteDatabase mDatabase;

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
        mInstance = new DatabaseHelper(context);
        mDatabase = mInstance.getWritableDatabase();
        return mInstance;
    }

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

        Cursor cursor =
            mDatabase.query(TABLE_EVENT, EVENT_COLUMNS, KEY_ID + " = ?", new String[]{String.valueOf(event.getID())},
                null, null, null, null);

        // We check if the even is already there
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

        notifyEventListeners();
    }

    /**
     * Adds a filter/userlist/friendlist to the database, and gives an ID to the
     * filter
     * 
     * @param filter
     *            The filter/list to add
     * @return The ID of the newly added filter in the filter database
     */
    public long addFilter(UserList filter) {
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

        notifyFilterListeners();
        return filterID;
    }

    /**
     * Adds a user to the internal database. If an user with the same ID already
     * exists, updates that user instead.
     * 
     * @param user
     *            The user to add to the database
     */
    public void addUser(User user) {
        Cursor cursor =
            mDatabase.query(TABLE_USER, USER_COLUMNS, KEY_USER_ID + " = ?", new String[]{String.valueOf(user.getID())},
                null, null, null, null);

        if (!cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, user.getID());
            values.put(KEY_NAME, user.getName());
            values.put(KEY_NUMBER, user.getNumber());
            values.put(KEY_EMAIL, user.getEmail());
            values.put(KEY_LONGITUDE, user.getLocation().getLongitude());
            values.put(KEY_LATITUDE, user.getLocation().getLatitude());
            values.put(KEY_POSNAME, user.getPositionName());
            values.put(KEY_LASTSEEN, user.getLastSeen().getTimeInMillis());
            values.put(KEY_VISIBLE, user.isVisible() ? 1 : 0); // boolean to int

            mDatabase.insert(TABLE_USER, null, values);

        } else {
            this.updateUser(user);
        }

        cursor.close();

        notifyFriendListeners();
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

        notifyFriendListeners();
        notifyLocationsListeners();
        notifyEventListeners();
        notifyFilterListeners();
    }

    /**
     * Deletes an event from the database
     * 
     * @param event
     *            The event to delete
     */
    public void deleteEvent(long id) {

        mDatabase.delete(TABLE_EVENT, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        notifyEventListeners();
    }

    /**
     * Deletes a filter from the database
     * 
     * @param filter
     *            The filter to delete
     */
    public void deleteFilter(long id) {
        // delete the filter from the table of filters
        mDatabase.delete(TABLE_FILTER, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        // then delete all the rows that reference this filter in the
        // filter-user table
        mDatabase.delete(TABLE_FILTER_USER, KEY_FILTER_ID + " = ?", new String[]{String.valueOf(id)});

        notifyFilterListeners();
    }

    /**
     * Deletes a user from the database
     * 
     * @param id
     *            The user's id
     */
    public void deleteUser(long id) {

        mDatabase.delete(TABLE_USER, KEY_USER_ID + " = ?", new String[]{String.valueOf(id)});

        notifyFriendListeners();
        notifyLocationsListeners();
    }

    /**
     * @return The list of all events
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
     * Gets all the filters from the database
     * 
     * @return A list of FriendLists
     */
    public List<UserList> getAllFilters() {

        ArrayList<UserList> filters = new ArrayList<UserList>();

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
     * Creates a LongSparseArray containing all users in the database, mapped to
     * their ID
     * 
     * @return A LongSparseArray of all users
     */
    public LongSparseArray<User> getAllUsers() {
        LongSparseArray<User> friends = new LongSparseArray<User>();

        String query = "SELECT  * FROM " + TABLE_USER;

        Cursor cursor = mDatabase.rawQuery(query, null);

        Friend friend = null;
        if (cursor.moveToFirst()) {
            do {
                friend =
                    new Friend(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)), cursor.getString(cursor
                        .getColumnIndex(KEY_NAME)));
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

                friends.put(friend.getID(), friend);
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

        Cursor cursor =
            mDatabase.query(TABLE_EVENT, EVENT_COLUMNS, KEY_ID + " = ?", new String[]{String.valueOf(id)}, null, null,
                null, null);

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

        UserEvent event =
            new UserEvent(cursor.getString(cursor.getColumnIndex(KEY_NAME)), cursor.getLong(cursor
                .getColumnIndex(KEY_USER_ID)), cursor.getString(cursor.getColumnIndex(KEY_CREATOR_NAME)), startDate,
                endDate, loc);

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
    public UserList getFilter(long id) {

        // SQLiteDatabase db = this.getWritableDatabase();

        // First query to get the filter's name
        Cursor cursor =
            mDatabase.query(TABLE_FILTER, FILTER_COLUMNS, KEY_ID + " = ?", new String[]{String.valueOf(id)}, null,
                null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        FriendList filter = new FriendList(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        filter.setID(id);

        // Second query to get the associated list of IDs
        cursor =
            mDatabase.query(TABLE_FILTER_USER, FILTER_USER_COLUMNS, KEY_FILTER_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                filter.addUser(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return filter;
    }

    /**
     * Gets a user from the database
     * 
     * @param id
     *            The user's unique ID
     * @return The user as a Friend object
     */
    public User getUser(long id) {

        Cursor cursor =
            mDatabase.query(TABLE_USER, USER_COLUMNS, KEY_USER_ID + " = ?", new String[]{String.valueOf(id)}, null,
                null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Friend friend =
            new Friend(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)), cursor.getString(cursor
                .getColumnIndex(KEY_NAME)));
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

        cursor.close();

        return friend;
    }

    /**
     * Fills the friend database with server data
     */
    public void initializeAllFriends() {
        NetworkSmartMapClient client = NetworkSmartMapClient.getInstance();
        try {
            Map<Long, Location> friends = client.listFriendsPos();
            for (long i : friends.keySet()) {
                this.addUser(client.getUserInfo(i));
                Log.d("Database", i + " initialized");
            }
        } catch (SmartMapClientException e) {
            Log.e("UpdateService", e.getMessage());
        }

        notifyFriendListeners();
        notifyLocationsListeners();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_FILTER);
        db.execSQL(CREATE_TABLE_FILTER_USER);
        db.execSQL(CREATE_TABLE_EVENT);
        db.execSQL(CREATE_TABLE_INVITATIONS);
        db.execSQL(CREATE_TABLE_PENDING);

        notifyFriendListeners();
        notifyLocationsListeners();
        notifyEventListeners();
        notifyFilterListeners();
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

        notifyFriendListeners();
        notifyLocationsListeners();
        notifyEventListeners();
        notifyFilterListeners();
    }

    /**
     * Fully updates the friends database (not only positions)
     */
    public void refreshFriendsInfo() {
        LongSparseArray<User> friends = this.getAllUsers();
        NetworkSmartMapClient client = NetworkSmartMapClient.getInstance();
        for (int i = 0; i < friends.size(); i++) {
            try {
                // The keys are IDs, so we call getUserInfo for each key and
                // update the db with the result
                this.updateUser(client.getUserInfo(friends.keyAt(i)));
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
            Map<Long, Location> locations = NetworkSmartMapClient.getInstance().listFriendsPos();
            LongSparseArray<User> friends = this.getAllUsers();
            long id;
            for (int i = 0; i < friends.size(); i++) {
                id = friends.keyAt(i);
                if (locations.containsKey(id)) {
                    this.updateUserPos(id, locations.get(id));
                    updatedRows++;
                }
            }
        } catch (SmartMapClientException e) {
            e.printStackTrace();
        }
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

        int rows = mDatabase.update(TABLE_EVENT, values, KEY_ID + " = ?", new String[]{String.valueOf(event.getID())});

        if (rows > 0) {
            notifyEventListeners();
        }

        return rows;
    }

    /**
     * Updates a filter
     * 
     * @param filter
     *            The updated filter
     */
    public void updateFilter(UserList filter) {

        this.deleteFilter(filter.getID());
        this.addFilter(filter);
        // not sure if there's a more efficient way

        notifyFilterListeners();
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
        values.put(KEY_USER_ID, user.getID());
        values.put(KEY_NAME, user.getName());
        values.put(KEY_NUMBER, user.getNumber());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_LONGITUDE, user.getLocation().getLongitude());
        values.put(KEY_LATITUDE, user.getLocation().getLatitude());
        values.put(KEY_POSNAME, user.getPositionName());
        values.put(KEY_LASTSEEN, user.getLastSeen().getTimeInMillis());
        values.put(KEY_VISIBLE, user.isVisible() ? 1 : 0); // boolean to int

        int rows =
            mDatabase.update(TABLE_USER, values, KEY_USER_ID + " = ?", new String[]{String.valueOf(user.getID())});

        if (rows > 0) {
            notifyFriendListeners();
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

        User friend = getUser(id);
        friend.setLocation(location);
        updateUser(friend);

        notifyLocationsListeners();
    }

    /**
     * Adds a pending friend request to the database
     * 
     * @param user
     *            The user who made the request (only need name and ID)
     */
    public void addInvitation(User user) {
        Cursor cursor =
            mDatabase.query(TABLE_INVITATIONS, INVITATION_COLUMNS, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(user.getID())}, null, null, null, null);

        if (!cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, user.getID());
            values.put(KEY_NAME, user.getName());

            mDatabase.insert(TABLE_INVITATIONS, null, values);

        }
        cursor.close();
    }

    /**
     * Returns a list of all pending received invitations
     * 
     * @return A list of users who sent requests
     */
    public List<User> getInvitations() {
        List<User> invitations = new ArrayList<User>();

        String query = "SELECT  * FROM " + TABLE_INVITATIONS;

        Cursor cursor = mDatabase.rawQuery(query, null);

        Friend friend = null;
        if (cursor.moveToFirst()) {
            do {
                friend =
                    new Friend(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)), cursor.getString(cursor
                        .getColumnIndex(KEY_NAME)));

                invitations.add(friend);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return invitations;
    }

    /**
     * Deletes an invitation from the database (call this when accepting or
     * declining an invitation)
     * 
     * @param id
     *            The inviter's id
     */
    public void deleteInvitation(long id) {

        mDatabase.delete(TABLE_INVITATIONS, KEY_USER_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Adds a pending sent friend request to the database
     * 
     * @param user
     *            The user who was sent a request
     */
    public void addPendingFriend(User user) {
        Cursor cursor =
            mDatabase.query(TABLE_PENDING, PENDING_COLUMNS, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(user.getID())}, null, null, null, null);

        if (!cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, user.getID());
            values.put(KEY_NAME, user.getName());

            mDatabase.insert(TABLE_PENDING, null, values);

        }
        cursor.close();
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
                friend =
                    new Friend(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)), cursor.getString(cursor
                        .getColumnIndex(KEY_NAME)));

                friends.add(friend);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return friends;
    }

    /**
     * Deletes a pending friend request from the database
     * 
     * @param id
     *            The invited user's id
     */
    public void deletePendingFriend(long id) {

        mDatabase.delete(TABLE_PENDING, KEY_USER_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void addFriendsLocationListener(FriendsLocationListener listener) {
        mLocationsListeners.add(listener);
    }

    public void addFriendsListener(FriendsListener listener) {
        mFriendsListeners.add(listener);
    }

    public void addEventsListener(EventsListener listener) {
        mEventsListeners.add(listener);
    }

    public void addFiltersListener(FiltersListener listener) {
        mFiltersListeners.add(listener);
    }

    public void notifyLocationsListeners() {
        for (FriendsLocationListener listener : mLocationsListeners) {
            listener.onChange();
        }
    }

    public void notifyFriendListeners() {
        for (FriendsLocationListener listener : mLocationsListeners) {
            listener.onChange();
        }
    }

    public void notifyEventListeners() {
        for (FriendsLocationListener listener : mLocationsListeners) {
            listener.onChange();
        }
    }

    public void notifyFilterListeners() {
        for (FriendsLocationListener listener : mLocationsListeners) {
            listener.onChange();
        }
    }
}