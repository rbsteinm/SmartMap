package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;

/**
 * SQLite helper
 * @author ritterni
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "SmartMapDB";
    
    public static final String TABLE_USER = "users";
    public static final String TABLE_FILTER = "filters";
    public static final String TABLE_FILTER_USER = "filter_users";
    
    private static final String KEY_USER_ID = "userID";
    private static final String KEY_NAME = "name";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_POSX = "posX";
    private static final String KEY_POSY = "posY";

    private static final String KEY_ID = "id";
    private static final String KEY_FILTER_ID = "filterID";
    
    //Columns for the User table
    private static final String[] USER_COLUMNS = {
        KEY_USER_ID, KEY_NAME, KEY_NUMBER, KEY_EMAIL, KEY_POSX, KEY_POSY
    };
    
    //Columns for the Filter table
    private static final String[] FILTER_COLUMNS = {
        KEY_ID, KEY_NAME
    };
    
    //Columns for the Filter/User table
    private static final String[] FILTER_USER_COLUMNS = {
        KEY_ID, KEY_FILTER_ID, KEY_USER_ID
    };
    
    //Table of users
    private static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS "
            + TABLE_USER + "("
            + KEY_USER_ID + " INTEGER PRIMARY KEY,"
            + KEY_NAME + " TEXT,"
            + KEY_NUMBER + " TEXT,"
            + KEY_EMAIL + " TEXT,"
            + KEY_POSX + " DOUBLE,"
            + KEY_POSY + " DOUBLE"
            + ")";
    
    //Table of filters
    private static final String CREATE_TABLE_FILTER = "CREATE TABLE IF NOT EXISTS "
            + TABLE_FILTER + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_NAME + " TEXT"
            + ")";
    
    //Table that maps filters to users
    private static final String CREATE_TABLE_FILTER_USER = "CREATE TABLE IF NOT EXISTS "
            + TABLE_FILTER_USER + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_FILTER_ID + " INTEGER,"
            + KEY_USER_ID + " INTEGER"
            + ")";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_FILTER);
        db.execSQL(CREATE_TABLE_FILTER_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTER_USER);
        
        onCreate(db);
    }

    /**
     * Clears the database. Mainly for testing purposes.
     */
    public void clearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, 1, 2);
    }
    
    /**
     * Adds a user to the internal database
     * @param user The user to add to the database
     */
    public void addUser(User user) {
        
        SQLiteDatabase db = this.getWritableDatabase();
     
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user.getID());
        values.put(KEY_NAME, user.getName());
        values.put(KEY_NUMBER, user.getNumber());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_POSX, user.getPosition().getX());
        values.put(KEY_POSY, user.getPosition().getY());
     
        db.insert(TABLE_USER, null, values);
     
        db.close();
    }
    
    /**
     * Gets a user from the database
     * @param id The user's unique ID
     * @return The user as a Friend object
     */
    public User getUser(long id) {
        
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(
                TABLE_USER,
                USER_COLUMNS,
                KEY_USER_ID + " = ?",
                new String[] {String.valueOf(id)},
                null,
                null,
                null,
                null);
     
        if (cursor != null) {
            cursor.moveToFirst();
        }
     
        Friend friend = new Friend(cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        friend.setNumber(cursor.getString(cursor.getColumnIndex(KEY_NUMBER)));
        friend.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
        friend.setX(cursor.getDouble(cursor.getColumnIndex(KEY_POSX)));
        friend.setY(cursor.getDouble(cursor.getColumnIndex(KEY_POSY)));
        
        return friend;
    }
    
    /**
     * Creates a SparseArray containing all users in the database, mapped to their ID
     * @return A SparseArray of all users
     */
    public SparseArray<User> getAllUsers() {
        SparseArray<User> friends = new SparseArray<User>();
  
        String query = "SELECT  * FROM " + TABLE_USER;
        
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        
        Friend friend = null;
        if (cursor.moveToFirst()) {
            do {
                friend = new Friend(cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                friend.setNumber(cursor.getString(cursor.getColumnIndex(KEY_NUMBER)));
                friend.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                friend.setX(cursor.getDouble(cursor.getColumnIndex(KEY_POSX)));
                friend.setY(cursor.getDouble(cursor.getColumnIndex(KEY_POSY)));
  
               // friends.put(friend.getID(), friend);
            } while (cursor.moveToNext());
        }
        
        return friends;
    }
    
    /**
     * Updates a user's values
     * @param user The user to update
     * @return The number of rows that were updated
     */
    public int updateUser(User user) {
        
        SQLiteDatabase db = this.getWritableDatabase();
     
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user.getID());
        values.put(KEY_NAME, user.getName());
        values.put(KEY_NUMBER, user.getNumber());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_POSX, user.getPosition().getX());
        values.put(KEY_POSY, user.getPosition().getY());
        
        int rows = db.update(TABLE_USER,
                values,
                KEY_USER_ID + " = ?",
                new String[] {String.valueOf(user.getID())});
     
        db.close();
     
        return rows;
    }
    
    /**
     * Deletes a user from the database
     * @param user The user to delete
     */
    public void deleteUser(User user) {
        
        SQLiteDatabase db = this.getWritableDatabase();
 
        db.delete(TABLE_USER,
                KEY_USER_ID + " = ?",
                new String[] {String.valueOf(user.getID())});
                
        db.close(); 
    }
    
    /**
     * Adds a filter/userlist/friendlist to the database
     * @param filter The filter/list to add
     * @return The ID of the newly added filter in the filter database
     */
    public long addFilter(UserList filter) {
        
        SQLiteDatabase db = this.getWritableDatabase();
     
        //First we insert the filter in the table of lists
        ContentValues filterValues = new ContentValues();
        filterValues.put(KEY_NAME, filter.getListName());
        long filterID = db.insert(TABLE_FILTER, null, filterValues);
        
        //Then we add the filter-user pairs to another table
        ContentValues pairValues = null;
        for (int id : filter.getList()) {
            pairValues = new ContentValues();
            pairValues.put(KEY_FILTER_ID, filterID);
            pairValues.put(KEY_USER_ID, id);
            db.insert(TABLE_FILTER_USER, null, pairValues);
        }
        db.close();
        return filterID;
    }
    
/**
 * Gets a specific filter by id
 * @param name The filter's id
 * @return The filter as a FriendList object
 */
    public UserList getFilter(long id) {
        
        SQLiteDatabase db = this.getWritableDatabase();
        
        //First query to get the filter's name
        Cursor cursor = db.query(
                TABLE_FILTER,
                FILTER_COLUMNS,
                KEY_ID + " = ?",
                new String[] {String.valueOf(id)},
                null,
                null,
                null,
                null);
     
        if (cursor != null) {
            cursor.moveToFirst();
        }
        
        FriendList filter = new FriendList(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        
        //Second query to get the associated list of IDs
        cursor = db.query(
                TABLE_FILTER_USER,
                FILTER_USER_COLUMNS,
                KEY_FILTER_ID + " = ?",
                new String[] {String.valueOf(id)},
                null,
                null,
                null,
                null);
            
        if (cursor.moveToFirst()) {
            do {
                filter.addUser(cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)));
            } while (cursor.moveToNext());
        }
        return filter;
    }
    
/**
 * Method to get all the filters from the database
 * @return A list of FriendLists
 */
    public List<UserList> getAllFilters() {
        
        ArrayList<UserList> filters = new ArrayList<UserList>();
        String query = "SELECT  * FROM " + TABLE_FILTER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
     
        if (cursor != null) {
            cursor.moveToFirst();
        }
        //Loops to get all the filters
        int index = 0;
        if (cursor.moveToFirst()) {
            do {
                filters.add(new FriendList(cursor.getString(cursor.getColumnIndex(KEY_NAME))));
                //Second query to get the list of ids
                Cursor cursor2 = db.query(
                        TABLE_FILTER_USER,
                        FILTER_USER_COLUMNS,
                        KEY_FILTER_ID + " = ?",
                        new String[] {String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_ID)))},
                        null,
                        null,
                        null,
                        null);
                    
                if (cursor2.moveToFirst()) {
                    do {
                        filters.get(index).addUser(cursor2.getInt(cursor.getColumnIndex(KEY_USER_ID)));
                    } while (cursor2.moveToNext());
                }
                index++;
            } while (cursor.moveToNext()); 
        }
        return filters;
    }
}
