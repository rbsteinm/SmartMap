package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite helper
 * @author ritterni
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "SmartMapDB";
    
    private static final String TABLE_USER = "users";
    private static final String TABLE_FILTER = "filters";
    private static final String TABLE_FILTER_USER = "filter_users";
    
    private static final String KEY_USER_ID = "userID";
    private static final String KEY_NAME = "name";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_POSX = "posX";
    private static final String KEY_POSY = "posY";

    private static final String KEY_ID = "id";
    private static final String KEY_FILTER = "filterID";
    
    private static final String[] USER_COLUMNS = {
        KEY_USER_ID, KEY_NAME, KEY_NUMBER, KEY_EMAIL, KEY_POSX, KEY_POSY
    };
    
    private static final String[] FILTER_COLUMNS = {
        KEY_ID, KEY_NAME
    };
    
    private static final String[] FILTER_USER_COLUMNS = {
        KEY_ID, KEY_FILTER, KEY_USER_ID
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
            + KEY_FILTER + " INTEGER,"
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

    public void clearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FILTER, null, null);
        db.delete(TABLE_USER, null, null);
        db.close();
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
    public User getUser(int id) {
        
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
     * @return A list of all users in the database
     */
    public List<User> getAllUsers() {
        List<User> friends = new ArrayList<User>();
  
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
  
                friends.add(friend);
            } while (cursor.moveToNext());
        }
        
        Collections.sort(friends, new UserComparator());
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
                KEY_USER_ID+" = ?",
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
                KEY_USER_ID+" = ?",
                new String[] {String.valueOf(user.getID())});
                
        db.close(); 
    }
}
