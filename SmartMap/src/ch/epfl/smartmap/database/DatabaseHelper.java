package ch.epfl.smartmap.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.ImmutableEvent;
import ch.epfl.smartmap.cache.ImmutableFilter;
import ch.epfl.smartmap.cache.ImmutableInvitation;
import ch.epfl.smartmap.cache.ImmutableUser;
import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.listeners.OnInvitationListUpdateListener;
import ch.epfl.smartmap.listeners.OnInvitationStatusUpdateListener;

/**
 * SQLite helper
 * 
 * @author ritterni
 */
public final class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 9;
    private static final String DATABASE_NAME = "SmartMapDB";

    public static final int DEFAULT_PICTURE = R.drawable.ic_default_user; // placeholder
    public static final int IMAGE_QUALITY = 100;

    private final List<OnInvitationListUpdateListener> mOnInvitationListUpdateListeners =
        new ArrayList<OnInvitationListUpdateListener>();
    private final List<OnInvitationStatusUpdateListener> mOnInvitationStatusUpdateListeners =
        new ArrayList<OnInvitationStatusUpdateListener>();

    public static final String TABLE_USER = "users";
    public static final String TABLE_FILTER = "filters";
    public static final String TABLE_FILTER_USER = "filter_users";
    public static final String TABLE_EVENT = "events";
    public static final String TABLE_INVITATIONS = "invitations";
    public static final String TABLE_PENDING = "pending";

    private static final String KEY_USER_ID = "userID";
    private static final String KEY_NAME = "name";
    private static final String KEY_EVENT_ID = "eventID";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_POSNAME = "posName";
    private static final String KEY_COUNTRY_NAME = "countryName";
    private static final String KEY_LASTSEEN = "lastSeen";
    private static final String KEY_BLOCKED = "isBlocked";

    private static final String KEY_ID = "id";
    private static final String KEY_FILTER_ID = "filterID";
    private static final String KEY_STATUS = "status";
    private static final String KEY_TYPE = "type";

    private static final String KEY_DATE = "date";
    private static final String KEY_ENDDATE = "endDate";

    private static final String KEY_EVTDESC = "eventDescription";

    // Columns for the User table
    private static final String[] USER_COLUMNS = {KEY_USER_ID, KEY_NAME, KEY_NUMBER, KEY_EMAIL,
        KEY_LONGITUDE, KEY_LATITUDE, KEY_POSNAME, KEY_LASTSEEN, KEY_BLOCKED};

    // Columns for the Filter table
    private static final String[] FILTER_COLUMNS = {KEY_ID, KEY_NAME};

    // Columns for the Filter/User table
    private static final String[] FILTER_USER_COLUMNS = {KEY_ID, KEY_FILTER_ID, KEY_USER_ID};

    // Columns for the Event table
    private static final String[] EVENT_COLUMNS = {KEY_ID, KEY_NAME, KEY_EVTDESC, KEY_USER_ID, KEY_LONGITUDE,
        KEY_LATITUDE, KEY_DATE, KEY_ENDDATE, KEY_POSNAME, KEY_COUNTRY_NAME};

    // Columns for the Invitations table
    private static final String[] INVITATION_COLUMNS = {KEY_ID, KEY_USER_ID, KEY_EVENT_ID, KEY_STATUS,
        KEY_DATE, KEY_TYPE};

    // Columns for the pending requests table
    private static final String[] PENDING_COLUMNS = {KEY_USER_ID, KEY_NAME};

    // Table of users
    private static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "("
        + KEY_USER_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_NUMBER + " TEXT," + KEY_EMAIL
        + " TEXT," + KEY_LONGITUDE + " DOUBLE," + KEY_LATITUDE + " DOUBLE," + KEY_POSNAME + " TEXT,"
        + KEY_LASTSEEN + " INTEGER," + KEY_BLOCKED + " INTEGER" + ")";

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
        + " INTEGER," + KEY_LONGITUDE + " DOUBLE," + KEY_LATITUDE + " DOUBLE," + KEY_DATE + " INTEGER,"
        + KEY_ENDDATE + " INTEGER," + KEY_POSNAME + " TEXT," + KEY_COUNTRY_NAME + " TEXT" + ")";

    // Table of invitations
    private static final String CREATE_TABLE_INVITATIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_INVITATIONS
        + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_ID + " INTEGER," + KEY_EVENT_ID + " INTEGER, "
        + KEY_STATUS + " INTEGER," + KEY_DATE + " INTEGER, " + KEY_TYPE + " INTEGER" + ")";

    // Table of invitations
    private static final String CREATE_TABLE_PENDING = "CREATE TABLE IF NOT EXISTS " + TABLE_PENDING + "("
        + KEY_USER_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT" + ")";

    private final SQLiteDatabase mDatabase;
    private final Context mContext;

    /**
     * DatabaseHelper constructor. Will be made private, so use initialize() or
     * getInstance() instead.
     * 
     * @param context
     *            The application's context, used to access the files
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME + "_" + ServiceContainer.getSettingsManager().getUserID(), null,
            DATABASE_VERSION);
        mContext = context;
        mDatabase = this.getWritableDatabase();
        this.onCreate(mDatabase);
    }

    /**
     * Stores an event in the database. If there's already an event with the
     * same ID, updates that event instead
     * The event must have an ID (given by the server)!
     * 
     * @param event
     *            The event to store
     */
    public void addEvent(ImmutableEvent event) throws IllegalArgumentException {
        if (event.getId() < 0) {
            throw new IllegalArgumentException("Invalid event ID");
        }

        Cursor cursor =
            mDatabase.query(TABLE_EVENT, EVENT_COLUMNS, KEY_ID + " = ?",
                new String[]{String.valueOf(event.getId())}, null, null, null, null);

        // We check if the event is already there
        if (!cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, event.getId());
            values.put(KEY_NAME, event.getName());
            values.put(KEY_EVTDESC, event.getDescription());
            values.put(KEY_USER_ID, event.getCreatorId());
            values.put(KEY_LONGITUDE, event.getLocation().getLongitude());
            values.put(KEY_LATITUDE, event.getLocation().getLatitude());
            values.put(KEY_DATE, event.getStartDate().getTimeInMillis());
            values.put(KEY_ENDDATE, event.getEndDate().getTimeInMillis());

            mDatabase.insert(TABLE_EVENT, null, values);
        } else {
            this.updateEvent(event);
        }

        cursor.close();

    }

    /**
     * Adds a filter/userlist/friendlist to the database, and gives an ID to the
     * filter
     * 
     * @param filter
     *            The filter/list to add
     * @return The ID of the newly added filter in the filter database
     */
    public long addFilter(ImmutableFilter filter) {
        // First we insert the filter in the table of lists
        ContentValues filterValues = new ContentValues();
        filterValues.put(KEY_NAME, filter.getName());
        long filterID = mDatabase.insert(TABLE_FILTER, null, filterValues);

        // Then we add the filter-user pairs to another table
        ContentValues pairValues = null;
        for (long id : filter.getIds()) {
            pairValues = new ContentValues();
            pairValues.put(KEY_FILTER_ID, filterID);
            pairValues.put(KEY_USER_ID, id);
            mDatabase.insert(TABLE_FILTER_USER, null, pairValues);
        }

        return filterID;
    }

    /**
     * Adds an invitation to the database
     * 
     * @param invitation
     *            The {@code ImmutableInvitation} to add to the database
     */
    public long addInvitation(ImmutableInvitation invitation) {

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, invitation.getUserId());
        values.put(KEY_EVENT_ID, invitation.getEventId());
        values.put(KEY_STATUS, invitation.getStatus());
        values.put(KEY_DATE, invitation.getTimeStamp());
        values.put(KEY_TYPE, invitation.getType());

        this.notifyOnInvitationListUpdateListeners();

        return mDatabase.insert(TABLE_INVITATIONS, null, values);
    }

    public void addOnInvitationListUpdateListener(OnInvitationListUpdateListener listener) {
        mOnInvitationListUpdateListeners.add(listener);
    }

    public void addOnInvitationStatusUpdateListener(OnInvitationStatusUpdateListener listener) {
        mOnInvitationStatusUpdateListeners.add(listener);
    }

    /**
     * Adds a pending sent friend request to the database
     * 
     * @param user
     *            The user who was sent a request
     */
    public void addPendingFriend(long id) {
        Cursor cursor =
            mDatabase.query(TABLE_PENDING, PENDING_COLUMNS, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (!cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, id);
            values.put(KEY_NAME, id);

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
    public void addUser(ImmutableUser user) {
        Cursor cursor =
            mDatabase.query(TABLE_USER, USER_COLUMNS, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())}, null, null, null, null);

        if (!cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, user.getId());
            values.put(KEY_NAME, user.getName());
            values.put(KEY_NUMBER, user.getPhoneNumber());
            values.put(KEY_EMAIL, user.getEmail());
            values.put(KEY_LONGITUDE, user.getLocation().getLongitude());
            values.put(KEY_LATITUDE, user.getLocation().getLatitude());
            values.put(KEY_POSNAME, user.getLocationString());
            values.put(KEY_LASTSEEN, user.getLocation().getTime());

            mDatabase.insert(TABLE_USER, null, values);
        } else {
            this.updateFriend(user);
        }

        cursor.close();
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
    }

    /**
     * Deletes an event from the database
     * 
     * @param event
     *            The event to delete
     */
    public void deleteEvent(long id) {

        mDatabase.delete(TABLE_EVENT, KEY_ID + " = ?", new String[]{String.valueOf(id)});

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
     * Deletes a pending friend request from the database
     * 
     * @param id
     *            The invited user's id
     */
    public void deletePendingFriend(long id) {

        mDatabase.delete(TABLE_PENDING, KEY_USER_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Deletes a user from the database
     * 
     * @param id
     *            The user's id
     */
    public void deleteUser(long id) {

        mDatabase.delete(TABLE_USER, KEY_USER_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * @return the {@code List} of all events
     */
    public List<ImmutableEvent> getAllEvents() {
        ArrayList<ImmutableEvent> events = new ArrayList<ImmutableEvent>();

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
    public Set<ImmutableFilter> getAllFilters() {

        HashSet<ImmutableFilter> filters = new HashSet<ImmutableFilter>();

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
     * Returns all invitations (friend requests, event invitations, etc)
     * 
     * @return a {@code List} of {@code ImmutableInvitation}s, sorted by ID
     */
    public List<ImmutableInvitation> getAllInvitations() {
        List<ImmutableInvitation> invitations = new ArrayList<ImmutableInvitation>();

        String query = "SELECT  * FROM " + TABLE_INVITATIONS;

        Cursor cursor = mDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(KEY_ID));
                long userId = cursor.getLong(cursor.getColumnIndex(KEY_USER_ID));
                long eventId = cursor.getLong(cursor.getColumnIndex(KEY_EVENT_ID));
                int status = cursor.getInt(cursor.getColumnIndex(KEY_STATUS));
                long date = cursor.getLong(cursor.getColumnIndex(KEY_DATE));
                int type = cursor.getInt(cursor.getColumnIndex(KEY_TYPE));

                invitations.add(new ImmutableInvitation(id, userId, eventId, status, date, type));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return invitations;
    }

    /**
     * @param id
     *            The event's ID
     * @return The event associated to this ID
     */
    public ImmutableEvent getEvent(long id) {

        // SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
            mDatabase.query(TABLE_EVENT, EVENT_COLUMNS, KEY_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        GregorianCalendar startDate = new GregorianCalendar();
        GregorianCalendar endDate = new GregorianCalendar();
        startDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(KEY_DATE)));
        endDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(KEY_ENDDATE)));

        Location location = new Location(Displayable.PROVIDER_NAME);
        location.setLongitude(cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)));
        location.setLatitude(cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)));
        String locationString = cursor.getString(cursor.getColumnIndex(KEY_POSNAME));

        String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
        String description = cursor.getString(cursor.getColumnIndex(KEY_EVTDESC));
        long creatorId = cursor.getColumnIndex(KEY_USER_ID);

        ImmutableEvent event =
            new ImmutableEvent(id, name, creatorId, description, startDate, endDate, location,
                locationString, new ArrayList<Long>());

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
    public ImmutableFilter getFilter(long id) {

        // SQLiteDatabase db = this.getWritableDatabase();

        // First query to get the filter's name
        Cursor cursor =
            mDatabase.query(TABLE_FILTER, FILTER_COLUMNS, KEY_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null, null);

        String name = "";

        if (cursor != null) {
            cursor.moveToFirst();
            name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
        }

        // Second query to get the associated list of IDs
        Set<Long> ids = new HashSet<Long>();

        cursor =
            mDatabase.query(TABLE_FILTER_USER, FILTER_USER_COLUMNS, KEY_FILTER_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                ids.add(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)));
            } while (cursor.moveToNext());
        }

        ImmutableFilter filter = new ImmutableFilter(id, name, ids, true);

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
    public ImmutableUser getFriend(long id) {

        Cursor cursor =
            mDatabase.query(TABLE_USER, USER_COLUMNS, KEY_USER_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null, null);

        if ((cursor != null) && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(KEY_NUMBER));
            String email = cursor.getString(cursor.getColumnIndex(KEY_EMAIL));
            long lastSeen = cursor.getLong(cursor.getColumnIndex(KEY_LASTSEEN));
            double longitude = cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE));
            double latitude = cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE));
            Location location = new Location("database");
            location.setLongitude(longitude);
            location.setLatitude(latitude);
            location.setTime(lastSeen);
            String locationString = cursor.getString(cursor.getColumnIndex(KEY_POSNAME));
            Bitmap image = this.getPictureById(id);

            cursor.close();

            return new ImmutableUser(id, name, phoneNumber, email, location, locationString, image,
                User.DEFAULT_BLOCK_VALUE);
        }

        return null;
    }

    /**
     * @return the {@code List} containing all friend ids
     */
    public List<Long> getFriendIds() {
        List<Long> friendIds = new ArrayList<Long>();

        String query = "SELECT  * FROM " + TABLE_USER;

        Cursor cursor = mDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                friendIds.add(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return friendIds;
    }

    /**
     * Returns a list of all pending friends
     * 
     * @return A list of users who were sent friend requests
     */
    public List<ImmutableUser> getPendingFriends() {
        List<ImmutableUser> friends = new ArrayList<ImmutableUser>();

        String query = "SELECT  * FROM " + TABLE_PENDING;

        Cursor cursor = mDatabase.rawQuery(query, null);

        ImmutableUser friend = null;
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(KEY_USER_ID));
                friend =
                    new ImmutableUser(id, cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                        User.NO_PHONE_NUMBER, User.NO_EMAIL, new Location(""), "", this.getPictureById(id),
                        false);

                friends.add(friend);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return friends;
    }

    /**
     * Gets a user from the database
     * 
     * @param userId
     *            The user's ID
     * @return The user's profile picture if it exists, a default picture
     *         otherwise
     */
    public Bitmap getPictureById(long userId) {
        File file = new File(mContext.getFilesDir(), userId + ".png");
        Bitmap pic = null;
        if (file.exists()) {
            pic = BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        if (pic == null) {
            pic = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_default_user);
        }
        return pic;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_FILTER);
        db.execSQL(CREATE_TABLE_FILTER_USER);
        db.execSQL(CREATE_TABLE_EVENT);
        db.execSQL(CREATE_TABLE_INVITATIONS);
        db.execSQL(CREATE_TABLE_PENDING);
    }

    /**
     * Uses listFriendsPos() to update the entire friends database with updated
     * positions
     * 
     * @return The number of rows (i.e. friends) that were updated
     */
    // public int refreshFriendsPos() {
    // int updatedRows = 0;
    // try {
    // List<User> updatedUsers =
    // NetworkSmartMapClient.getInstance().listFriendsPos();
    //
    // for (User user : updatedUsers) {
    // this.updateUser(user);
    // updatedRows++;
    // }
    // } catch (SmartMapClientException e) {
    // e.printStackTrace();
    // }
    // return updatedRows;
    // }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTER_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVITATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PENDING);
        this.onCreate(db);
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
    }

    // /**
    // * Fully updates the friends database (not only positions)
    // */
    // public void refreshFriendsInfo() {
    // List<User> friends = this.getAllFriends();
    // NetworkSmartMapClient client = NetworkSmartMapClient.getInstance();
    // for (User f : friends) {
    // try {
    // this.updateUser(client.getUserInfo(f.getID()));
    // } catch (SmartMapClientException e) {
    // e.printStackTrace();
    // }
    // }
    // }

    /**
     * Stores a profile picture
     * 
     * @param picture
     *            The picture to store
     * @param userId
     *            The user's ID
     */
    public void setUserPicture(Bitmap picture, long userId) {
        File file = new File(mContext.getFilesDir(), userId + ".png");

        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream out = mContext.openFileOutput(userId + ".png", Context.MODE_PRIVATE);
            picture.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, out);
            out.close();
        } catch (FileNotFoundException e) {
            Log.e("DatabaseHelper", "Exception in setUserPicture: " + e);
        } catch (IOException e) {
            Log.e("DatabaseHelper", "Exception in setUserPicture: " + e);
        }
    }

    /**
     * Updates an event
     * 
     * @param event
     *            The event to update
     * @return The number of rows that were affected
     */
    public int updateEvent(ImmutableEvent event) {

        ContentValues values = new ContentValues();
        values.put(KEY_ID, event.getId());
        values.put(KEY_NAME, event.getName());
        values.put(KEY_EVTDESC, event.getDescription());
        values.put(KEY_USER_ID, event.getCreatorId());
        values.put(KEY_LONGITUDE, event.getLocation().getLongitude());
        values.put(KEY_LATITUDE, event.getLocation().getLatitude());
        values.put(KEY_DATE, event.getStartDate().getTimeInMillis());
        values.put(KEY_ENDDATE, event.getEndDate().getTimeInMillis());

        return mDatabase.update(TABLE_EVENT, values, KEY_ID + " = ?",
            new String[]{String.valueOf(event.getId())});
    }

    /**
     * Updates a filter
     * 
     * @param filter
     *            The updated filter
     */
    public void updateFilter(ImmutableFilter filter) {

        this.deleteFilter(filter.getId());
        this.addFilter(filter);
    }

    /**
     * Updates a user's values
     * 
     * @param friend
     *            The user to update
     * @return The number of rows that were updated
     */
    public int updateFriend(ImmutableUser friend) {
        ContentValues values = new ContentValues();

        if (friend.getId() != User.NO_ID) {
            values.put(KEY_USER_ID, friend.getId());
        }
        if (friend.getName() != null) {
            values.put(KEY_NAME, friend.getName());
        }
        if (friend.getPhoneNumber() != Friend.NO_PHONE_NUMBER) {
            values.put(KEY_NUMBER, friend.getPhoneNumber());
        }
        if (friend.getEmail() != Friend.NO_EMAIL) {
            values.put(KEY_EMAIL, friend.getEmail());
        }
        if ((friend.getLocation().getLatitude() != User.NO_LATITUDE)
            || (friend.getLocation().getLongitude() != User.NO_LONGITUDE)) {
            values.put(KEY_LONGITUDE, friend.getLocation().getLongitude());
            values.put(KEY_LATITUDE, friend.getLocation().getLatitude());
        }
        if (!friend.getLocationString().equals(Friend.NO_LOCATION_STRING)) {
            values.put(KEY_POSNAME, friend.getLocationString());
        }

        return mDatabase.update(TABLE_USER, values, KEY_USER_ID + " = ?",
            new String[]{String.valueOf(friend.getId())});
    }

    /**
     * Updates the database contents to be up-to-date with the cache
     */
    public void updateFromCache() {
        Log.d(TAG, "Update Database from Cache");
        // FIXME : Need to store only our events, filters, friends, and
        // settings.
        Set<User> friends = ServiceContainer.getCache().getAllFriends();
        Set<Event> events = ServiceContainer.getCache().getAllVisibleEvents();

        for (User user : friends) {
            this.addUser(user.getImmutableCopy());
        }
        for (Event event : events) {
            this.addEvent(event.getImmutableCopy());
        }
    }

    /**
     * Updates a {@code ImmutableInvitation} in the database
     * 
     * @param invitation
     *            The {@code ImmutableInvitation} to update
     * @return The number of rows that were updated
     */
    public int updateInvitation(ImmutableInvitation invitation) {

        ContentValues values = new ContentValues();
        values.put(KEY_ID, invitation.getId());
        values.put(KEY_USER_ID, invitation.getUser().getId());
        values.put(KEY_EVENT_ID, invitation.getEventId());
        values.put(KEY_STATUS, invitation.getStatus());
        values.put(KEY_DATE, invitation.getTimeStamp());
        values.put(KEY_TYPE, invitation.getType());

        int rows =
            mDatabase.update(TABLE_INVITATIONS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(invitation.getId())});

        if (rows > 0) {
            this.notifyOnInvitationListUpdateListeners();
        }
        if ((invitation.getStatus() == Invitation.ACCEPTED)
            || (invitation.getStatus() == Invitation.DECLINED)) {
            this.notifyOnInvitationStatusUpdateListeners(invitation.getUser().getId(), invitation.getStatus());
        }

        return rows;
    }

    private void notifyOnInvitationListUpdateListeners() {
        for (OnInvitationListUpdateListener listener : mOnInvitationListUpdateListeners) {
            listener.onInvitationListUpdate();
        }
    }

    private void notifyOnInvitationStatusUpdateListeners(long id, int status) {
        for (OnInvitationStatusUpdateListener listener : mOnInvitationStatusUpdateListeners) {
            listener.onInvitationStatusUpdate(id, status);
        }
    }
}
