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
import ch.epfl.smartmap.cache.EventContainer;
import ch.epfl.smartmap.cache.Filter;
import ch.epfl.smartmap.cache.FilterContainer;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.cache.InvitationContainer;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserContainer;

/**
 * SQLite helper
 * 
 * @author ritterni
 */
public class DatabaseHelper extends SQLiteOpenHelper implements DatabaseHelperInterface {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 12;
    private static final String DATABASE_NAME = "SmartMapDB";

    public static final int DEFAULT_PICTURE = R.drawable.ic_default_user; // placeholder
    public static final int IMAGE_QUALITY = 100;

    public static final String TABLE_USER = "users";
    public static final String TABLE_FILTER = "filters";
    public static final String TABLE_FILTER_USER = "filter_users";
    public static final String TABLE_EVENT = "events";
    public static final String TABLE_EVENT_USER = "event_users";
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
    private static final String KEY_FRIENDSHIP = "friendStatus";

    private static final String KEY_ID = "id";
    private static final String KEY_FILTER_ID = "filterID";
    private static final String KEY_ACTIVE = "isActive";
    private static final String KEY_STATUS = "status";
    private static final String KEY_TYPE = "type";

    private static final String KEY_DATE = "date";
    private static final String KEY_ENDDATE = "endDate";

    private static final String KEY_EVTDESC = "eventDescription";

    // Columns for the User table
    private static final String[] USER_COLUMNS = {KEY_USER_ID, KEY_NAME, KEY_NUMBER, KEY_EMAIL,
        KEY_LONGITUDE, KEY_LATITUDE, KEY_POSNAME, KEY_LASTSEEN, KEY_BLOCKED, KEY_FRIENDSHIP};

    // Columns for the Filter table
    private static final String[] FILTER_COLUMNS = {KEY_ID, KEY_NAME, KEY_ACTIVE};

    // Columns for the Filter/User table
    private static final String[] FILTER_USER_COLUMNS = {KEY_ID, KEY_FILTER_ID, KEY_USER_ID};

    // Columns for the Event table
    private static final String[] EVENT_COLUMNS = {KEY_ID, KEY_NAME, KEY_EVTDESC, KEY_USER_ID, KEY_LONGITUDE,
        KEY_LATITUDE, KEY_DATE, KEY_ENDDATE, KEY_POSNAME, KEY_COUNTRY_NAME};

    // Columns for the Event-User table
    private static final String[] EVENT_USER_COLUMNS = {KEY_ID, KEY_EVENT_ID, KEY_USER_ID};

    // Columns for the pending requests table
    private static final String[] PENDING_COLUMNS = {KEY_USER_ID, KEY_NAME};

    // Table of users
    private static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "("
        + KEY_USER_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_NUMBER + " TEXT," + KEY_EMAIL
        + " TEXT," + KEY_LONGITUDE + " DOUBLE," + KEY_LATITUDE + " DOUBLE," + KEY_POSNAME + " TEXT,"
        + KEY_LASTSEEN + " INTEGER," + KEY_BLOCKED + " INTEGER," + KEY_FRIENDSHIP + " INTEGER" + ")";

    // Table of filters
    private static final String CREATE_TABLE_FILTER = "CREATE TABLE IF NOT EXISTS " + TABLE_FILTER + "("
        + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_ACTIVE + " INTEGER" + ")";

    // Table that maps filters to users
    private static final String CREATE_TABLE_FILTER_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_FILTER_USER
        + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FILTER_ID + " INTEGER," + KEY_USER_ID + " INTEGER"
        + ")";

    // Table of events
    private static final String CREATE_TABLE_EVENT = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENT + "("
        + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_EVTDESC + " TEXT," + KEY_USER_ID
        + " INTEGER," + KEY_LONGITUDE + " DOUBLE," + KEY_LATITUDE + " DOUBLE," + KEY_DATE + " INTEGER,"
        + KEY_ENDDATE + " INTEGER," + KEY_POSNAME + " TEXT," + KEY_COUNTRY_NAME + " TEXT" + ")";

    // Table that maps events to attending users
    private static final String CREATE_TABLE_EVENT_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENT_USER
        + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_EVENT_ID + " INTEGER," + KEY_USER_ID + " INTEGER"
        + ")";

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
        super(context, DATABASE_NAME + "_" + ServiceContainer.getSettingsManager().getUserId(), null,
            DATABASE_VERSION);
        mContext = context;
        mDatabase = this.getWritableDatabase();
        this.onCreate(mDatabase);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#addEvent(ch.epfl.smartmap.cache.EventContainer)
     */
    @Override
    public void addEvent(EventContainer event) throws IllegalArgumentException {
        if (event.getId() < 0) {
            throw new IllegalArgumentException("Invalid event ID");
        }

        Cursor cursor =
            mDatabase.query(TABLE_EVENT, EVENT_COLUMNS, KEY_ID + " = ?",
                new String[]{String.valueOf(event.getId())}, null, null, null, null);

        // We check if the event is already there
        if (!cursor.moveToFirst()) {
            this.addUser(event.getImmCreator());

            // Doesn't add event if we can't store the user
            if (this.getUser(event.getImmCreator().getId()) != null) {
                ContentValues values = new ContentValues();
                values.put(KEY_ID, event.getId());
                values.put(KEY_NAME, event.getName());
                values.put(KEY_EVTDESC, event.getDescription());
                values.put(KEY_USER_ID, event.getCreatorId());
                if (event.getLocation() != null) {
                    values.put(KEY_LONGITUDE, event.getLocation().getLongitude());
                    values.put(KEY_LATITUDE, event.getLocation().getLatitude());
                }
                if ((event.getStartDate() != null) && (event.getEndDate() != null)) {
                    values.put(KEY_DATE, event.getStartDate().getTimeInMillis());
                    values.put(KEY_ENDDATE, event.getEndDate().getTimeInMillis());
                }
                values.put(KEY_POSNAME, event.getLocationString());

                mDatabase.insert(TABLE_EVENT, null, values);

                // Then we add the event-user pairs to another table
                ContentValues pairValues = null;
                for (long id : event.getParticipantIds()) {
                    pairValues = new ContentValues();
                    pairValues.put(KEY_EVENT_ID, event.getId());
                    pairValues.put(KEY_USER_ID, id);
                    mDatabase.insert(TABLE_EVENT_USER, null, pairValues);
                }
            }
        } else {
            this.updateEvent(event);
        }

        cursor.close();

    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.database.DatabaseHelperInterface#addFilter(ch.epfl.smartmap.cache.FilterContainer)
     */
    @Override
    public long addFilter(FilterContainer filter) {
        // First we insert the filter in the table of lists
        ContentValues filterValues = new ContentValues();
        filterValues.put(KEY_ID, filter.getId());
        filterValues.put(KEY_NAME, filter.getName());
        filterValues.put(KEY_ACTIVE, filter.isActive() ? 1 : 0);
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

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.database.DatabaseHelperInterface#addInvitation(ch.epfl.smartmap.cache.InvitationContainer
     * )
     */
    @Override
    public long addInvitation(InvitationContainer invitation) {

        // Add invitation related infos in database
        if (invitation.getUserInfos() != null) {
            this.addUser(invitation.getUserInfos());
        }
        if (invitation.getEventInfos() != null) {
            this.addEvent(invitation.getEventInfos());
        }

        // Doesn't store invitation if we can't store the values
        if ((invitation.getUserInfos() != null) || (invitation.getEventInfos() != null)) {

            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, invitation.getUserId());
            values.put(KEY_EVENT_ID, invitation.getEventId());
            values.put(KEY_STATUS, invitation.getStatus());
            values.put(KEY_DATE, invitation.getTimeStamp());
            values.put(KEY_TYPE, invitation.getType());

            if (invitation.getType() == Invitation.FRIEND_INVITATION) {

                // Get user infos
                UserContainer userInfo = invitation.getUserInfos();

                // Get pending ids
                Set<Long> pendingIds = this.getPendingFriends();

                Log.d(TAG, "Pending ids before : " + pendingIds);

                if (pendingIds.contains(userInfo.getId())) {
                    // Already stored
                    return Invitation.ALREADY_RECEIVED;
                } else if (invitation.getStatus() == Invitation.UNREAD) {
                    this.addPendingFriend(userInfo.getId());
                }

                Log.d(TAG, "Pending ids after : " + this.getPendingFriends());
            }

            return mDatabase.insert(TABLE_INVITATIONS, null, values);
        } else {
            return Invitation.ALREADY_RECEIVED;
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#addPendingFriend(long)
     */
    @Override
    public void addPendingFriend(long id) {
        Cursor cursor =
            mDatabase.query(TABLE_PENDING, PENDING_COLUMNS, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (!cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, id);

            mDatabase.insert(TABLE_PENDING, null, values);
        }
        cursor.close();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#addUser(ch.epfl.smartmap.cache.UserContainer)
     */
    @Override
    public void addUser(UserContainer user) {
        Cursor cursor =
            mDatabase.query(TABLE_USER, USER_COLUMNS, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())}, null, null, null, null);

        if (!cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, user.getId());
            values.put(KEY_NAME, user.getName());
            values.put(KEY_NUMBER, user.getPhoneNumber());
            values.put(KEY_EMAIL, user.getEmail());
            if (user.getLocation() != null) {
                values.put(KEY_LONGITUDE, user.getLocation().getLongitude());
                values.put(KEY_LATITUDE, user.getLocation().getLatitude());
                values.put(KEY_LASTSEEN, user.getLocation().getTime());
            } else {
                values.put(KEY_LONGITUDE, User.NO_LONGITUDE);
                values.put(KEY_LATITUDE, User.NO_LATITUDE);
                values.put(KEY_LASTSEEN, 0);
            }

            values.put(KEY_POSNAME, user.getLocationString());
            int blocked = 0;
            if (user.isBlocked() == User.BlockStatus.BLOCKED) {
                blocked = 1;
            }
            values.put(KEY_BLOCKED, blocked);
            values.put(KEY_FRIENDSHIP, user.getFriendship());

            mDatabase.insert(TABLE_USER, null, values);
        } else {
            this.updateFriend(user);
        }

        cursor.close();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#clearAll()
     */
    @Override
    public void clearAll() {
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTER);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTER_USER);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT_USER);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_INVITATIONS);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PENDING);

        this.onCreate(mDatabase);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#deleteEvent(long)
     */
    @Override
    public void deleteEvent(long id) {

        mDatabase.delete(TABLE_EVENT, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        mDatabase.delete(TABLE_EVENT_USER, KEY_EVENT_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#deleteFilter(long)
     */
    @Override
    public void deleteFilter(long id) {
        // delete the filter from the table of filters
        mDatabase.delete(TABLE_FILTER, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        // then delete all the rows that reference this filter in the
        // filter-user table
        mDatabase.delete(TABLE_FILTER_USER, KEY_FILTER_ID + " = ?", new String[]{String.valueOf(id)});

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#deleteInvitation(long)
     */
    @Override
    public void deleteInvitation(long id) {
        mDatabase.delete(TABLE_INVITATIONS, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#deletePendingFriend(long)
     */
    @Override
    public void deletePendingFriend(long id) {
        mDatabase.delete(TABLE_PENDING, KEY_USER_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#deleteUser(long)
     */
    @Override
    public void deleteUser(long id) {
        mDatabase.delete(TABLE_USER, KEY_USER_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#getAllEvents()
     */
    @Override
    public Set<EventContainer> getAllEvents() {
        Set<EventContainer> events = new HashSet<EventContainer>();

        String query = "SELECT  * FROM " + TABLE_EVENT;

        Cursor cursor = mDatabase.rawQuery(query, null);

        if ((cursor != null) && cursor.moveToFirst()) {
            do {
                events.add(this.getEvent(cursor.getLong(cursor.getColumnIndex(KEY_ID))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return events;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#getAllFilters()
     */
    @Override
    public Set<FilterContainer> getAllFilters() {

        Set<FilterContainer> filters = new HashSet<FilterContainer>();
        Set<Long> filterIds = new HashSet<Long>();

        String query = "SELECT  * FROM " + TABLE_FILTER;

        Cursor cursor = mDatabase.rawQuery(query, null);

        if ((cursor != null) && cursor.moveToFirst()) {
            do {
                // using getFilter to add this row's filter to the list
                filters.add(this.getFilter(cursor.getLong(cursor.getColumnIndex(KEY_ID))));
            } while (cursor.moveToNext());
        }

        for (FilterContainer filter : filters) {
            filterIds.add(filter.getId());
        }

        Log.d(TAG, "Database contains filter ids : " + filterIds);

        if (!filterIds.contains(Filter.DEFAULT_FILTER_ID)) {
            Log.d(TAG, "database contains no default filter");
            filters.add(new FilterContainer(Filter.DEFAULT_FILTER_ID, "", new HashSet<Long>(), true));
        }

        cursor.close();

        return filters;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#getAllInvitations()
     */
    @Override
    public Set<InvitationContainer> getAllInvitations() {
        Set<InvitationContainer> invitations = new HashSet<InvitationContainer>();

        String query = "SELECT  * FROM " + TABLE_INVITATIONS;

        Cursor cursor = mDatabase.rawQuery(query, null);

        if ((cursor != null) && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(KEY_ID));
                long userId = cursor.getLong(cursor.getColumnIndex(KEY_USER_ID));
                long eventId = cursor.getLong(cursor.getColumnIndex(KEY_EVENT_ID));
                int status = cursor.getInt(cursor.getColumnIndex(KEY_STATUS));
                long date = cursor.getLong(cursor.getColumnIndex(KEY_DATE));
                int type = cursor.getInt(cursor.getColumnIndex(KEY_TYPE));

                UserContainer user = this.getUser(userId);
                EventContainer event = this.getEvent(eventId);

                invitations.add(new InvitationContainer(id, user, event, status, date, type));

            } while (cursor.moveToNext());
        }

        cursor.close();
        return invitations;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#getAllUsers()
     */
    @Override
    public Set<UserContainer> getAllUsers() {
        Set<UserContainer> users = new HashSet<UserContainer>();

        String query = "SELECT  * FROM " + TABLE_USER;

        Cursor cursor = mDatabase.rawQuery(query, null);

        if ((cursor != null) && (cursor.getCount() > 0) && cursor.moveToFirst()) {
            do {
                Log.d(TAG, "" + cursor);
                users.add(this.getUser(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#getEvent(long)
     */
    @Override
    public EventContainer getEvent(long id) {

        Cursor cursor =
            mDatabase.query(TABLE_EVENT, EVENT_COLUMNS, KEY_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null, null);

        EventContainer event = null;
        if ((cursor != null) && cursor.moveToFirst()) {

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

            event =
                new EventContainer(id, name, this.getUser(creatorId), description, startDate, endDate,
                    location, locationString, new HashSet<Long>());

            // Second query to get the associated list of IDs
            Set<Long> ids = new HashSet<Long>();

            cursor =
                mDatabase.query(TABLE_EVENT_USER, EVENT_USER_COLUMNS, KEY_EVENT_ID + " = ?",
                    new String[]{String.valueOf(id)}, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    ids.add(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)));
                } while (cursor.moveToNext());
            }

            event.setParticipantIds(ids);
        }

        cursor.close();
        return event;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#getFilter(long)
     */
    @Override
    public FilterContainer getFilter(long id) {
        // First query to get the filter's name
        Cursor cursor =
            mDatabase.query(TABLE_FILTER, FILTER_COLUMNS, KEY_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null, null);

        String name = "";
        boolean isActive = false;

        if (cursor != null) {
            cursor.moveToFirst();
            name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            isActive = cursor.getInt(cursor.getColumnIndex(KEY_ACTIVE)) == 1;
        }

        // Second query to get the associated list of IDs
        Set<Long> ids = new HashSet<Long>();

        cursor =
            mDatabase.query(TABLE_FILTER_USER, FILTER_USER_COLUMNS, KEY_FILTER_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if ((cursor != null) && cursor.moveToFirst()) {
            do {
                ids.add(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)));
            } while (cursor.moveToNext());
        }

        FilterContainer filter = new FilterContainer(id, name, ids, isActive);

        cursor.close();

        return filter;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#getFilterIds()
     */
    @Override
    public List<Long> getFilterIds() {
        List<Long> filterIds = new ArrayList<Long>();
        String query = "SELECT  * FROM " + TABLE_FILTER;

        Cursor cursor = mDatabase.rawQuery(query, null);

        if ((cursor != null) && cursor.moveToFirst()) {
            do {
                filterIds.add(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return filterIds;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#getFriendIds()
     */
    @Override
    public List<Long> getFriendIds() {
        List<Long> friendIds = new ArrayList<Long>();

        String query = "SELECT  * FROM " + TABLE_USER;

        Cursor cursor = mDatabase.rawQuery(query, null);

        if ((cursor != null) && cursor.moveToFirst()) {
            do {
                if (this.getUser(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID))).getFriendship() == User.FRIEND) {
                    friendIds.add(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return friendIds;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#getPendingFriends()
     */
    @Override
    public Set<Long> getPendingFriends() {
        Set<Long> friends = new HashSet<Long>();

        String query = "SELECT  * FROM " + TABLE_PENDING;

        Cursor cursor = mDatabase.rawQuery(query, null);

        if ((cursor != null) && cursor.moveToFirst()) {
            do {
                friends.add(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return friends;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#getPictureById(long)
     */
    @Override
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

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#getUser(long)
     */
    @Override
    public UserContainer getUser(long id) {

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
            boolean isBlocked = cursor.getInt(cursor.getColumnIndex(KEY_BLOCKED)) == 1;
            int friendship = cursor.getInt(cursor.getColumnIndex(KEY_FRIENDSHIP));

            cursor.close();

            User.BlockStatus status = isBlocked ? User.BlockStatus.BLOCKED : User.BlockStatus.UNBLOCKED;
            return new UserContainer(id, name, phoneNumber, email, location, locationString, image, status,
                friendship);
        }

        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_FILTER);
        db.execSQL(CREATE_TABLE_FILTER_USER);
        db.execSQL(CREATE_TABLE_EVENT);
        db.execSQL(CREATE_TABLE_EVENT_USER);
        db.execSQL(CREATE_TABLE_INVITATIONS);
        db.execSQL(CREATE_TABLE_PENDING);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTER_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT_USER);
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVITATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PENDING);
        this.onCreate(db);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#setUserPicture(android.graphics.Bitmap, long)
     */
    @Override
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

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.database.DatabaseHelperInterface#updateEvent(ch.epfl.smartmap.cache.EventContainer)
     */
    @Override
    public int updateEvent(EventContainer event) {

        ContentValues values = new ContentValues();
        values.put(KEY_ID, event.getId());
        values.put(KEY_NAME, event.getName());
        values.put(KEY_EVTDESC, event.getDescription());
        values.put(KEY_USER_ID, event.getCreatorId());
        values.put(KEY_LONGITUDE, event.getLocation().getLongitude());
        values.put(KEY_LATITUDE, event.getLocation().getLatitude());
        values.put(KEY_DATE, event.getStartDate().getTimeInMillis());
        values.put(KEY_ENDDATE, event.getEndDate().getTimeInMillis());

        if (event.getParticipantIds() != null) {

            // Remove and re-add the event-user pairs
            mDatabase.delete(TABLE_EVENT_USER, KEY_EVENT_ID + "=" + event.getId(), null);

            ContentValues pairValues = null;
            for (long id : event.getParticipantIds()) {
                pairValues = new ContentValues();
                pairValues.put(KEY_EVENT_ID, event.getId());
                pairValues.put(KEY_USER_ID, id);
                mDatabase.insert(TABLE_EVENT_USER, null, pairValues);
            }
        }

        return mDatabase.update(TABLE_EVENT, values, KEY_ID + " = ?",
            new String[]{String.valueOf(event.getId())});
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.database.DatabaseHelperInterface#updateFilter(ch.epfl.smartmap.cache.FilterContainer)
     */
    @Override
    public void updateFilter(FilterContainer filter) {
        ContentValues filterValues = new ContentValues();
        filterValues.put(KEY_NAME, filter.getName());
        filterValues.put(KEY_ACTIVE, filter.isActive() ? 1 : 0);

        mDatabase.update(TABLE_FILTER, filterValues, KEY_ID + " = ?",
            new String[]{String.valueOf(filter.getId())});

        if (filter.getIds() != null) {

            // Remove and re-add the event-user pairs
            mDatabase.delete(TABLE_FILTER_USER, KEY_FILTER_ID + "=" + filter.getId(), null);

            ContentValues pairValues = null;
            for (long id : filter.getIds()) {
                pairValues = new ContentValues();
                pairValues.put(KEY_FILTER_ID, filter.getId());
                pairValues.put(KEY_USER_ID, id);
                mDatabase.insert(TABLE_FILTER_USER, null, pairValues);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.database.DatabaseHelperInterface#updateFriend(ch.epfl.smartmap.cache.UserContainer)
     */
    @Override
    public int updateFriend(UserContainer friend) {
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
        if ((friend.getLocation() != null)
            && ((friend.getLocation().getLatitude() != User.NO_LATITUDE) || (friend.getLocation()
                .getLongitude() != User.NO_LONGITUDE))) {
            values.put(KEY_LONGITUDE, friend.getLocation().getLongitude());
            values.put(KEY_LATITUDE, friend.getLocation().getLatitude());
        }
        if ((friend.getLocationString() != null)
            && !friend.getLocationString().equals(Friend.NO_LOCATION_STRING)) {
            values.put(KEY_POSNAME, friend.getLocationString());
        }

        values.put(KEY_FRIENDSHIP, friend.getFriendship());

        boolean isBlocked = false;
        if (friend.isBlocked() == User.BlockStatus.BLOCKED) {
            isBlocked = true;
        }
        values.put(KEY_BLOCKED, isBlocked ? 1 : 0);

        return mDatabase.update(TABLE_USER, values, KEY_USER_ID + " = ?",
            new String[]{String.valueOf(friend.getId())});
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#updateFromCache()
     */
    @Override
    public void updateFromCache() {
        Log.d(TAG, "Update Database from Cache");

        mDatabase.delete(TABLE_USER, null, null);
        mDatabase.delete(TABLE_FILTER, null, null);
        mDatabase.delete(TABLE_FILTER_USER, null, null);
        mDatabase.delete(TABLE_EVENT, null, null);
        mDatabase.delete(TABLE_EVENT_USER, null, null);
        mDatabase.delete(TABLE_INVITATIONS, null, null);

        Set<User> users = ServiceContainer.getCache().getAllUsers();
        Set<Event> events = ServiceContainer.getCache().getMyEvents();
        events.addAll(ServiceContainer.getCache().getParticipatingEvents());
        Set<Filter> filters = ServiceContainer.getCache().getAllFilters();
        Set<Invitation> invitations = ServiceContainer.getCache().getAllInvitations();

        for (User user : users) {
            Log.d(TAG, "Store user " + user.getId());
            this.addUser(user.getContainerCopy());
        }
        for (Event event : events) {
            Log.d(TAG, "Store event " + event.getId());
            this.addEvent(event.getImmutableCopy());
        }
        for (Filter filter : filters) {
            Log.d(TAG, "Store filter " + filter.getId());
            this.addFilter(filter.getContainerCopy());
        }
        for (Invitation invitation : invitations) {
            Log.d(TAG, "Store invitation " + invitation.getId());
            this.addInvitation(invitation.getContainerCopy());
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.database.DatabaseHelperInterface#updateInvitation(ch.epfl.smartmap.cache.
     * InvitationContainer)
     */
    @Override
    public int updateInvitation(InvitationContainer invitation) {

        ContentValues values = new ContentValues();
        values.put(KEY_ID, invitation.getId());
        if (invitation.getUser() != null) {
            values.put(KEY_USER_ID, invitation.getUser().getId());
        }
        values.put(KEY_EVENT_ID, invitation.getEventId());
        values.put(KEY_STATUS, invitation.getStatus());
        values.put(KEY_DATE, invitation.getTimeStamp());
        values.put(KEY_TYPE, invitation.getType());

        return mDatabase.update(TABLE_INVITATIONS, values, KEY_ID + " = ?",
            new String[]{String.valueOf(invitation.getId())});
    }
}
