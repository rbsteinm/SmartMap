package ch.epfl.smartmap.cache;

import android.graphics.Bitmap;
import android.location.Location;
import ch.epfl.smartmap.cache.User.BlockStatus;

/**
 * This only serves as a container to transfer {@code User} informations between different parts of the
 * Application.
 * 
 * @author jfperren
 */
public final class UserContainer {

    // User informations
    private long mId;
    private String mName;
    private String mPhoneNumber;
    private String mEmail;
    private String mLocationString;
    private Location mLocation;
    private Bitmap mImage;
    private User.BlockStatus mIsBlocked;
    private int mFriendship;

    /**
     * Constructor, no check on any of the values
     * 
     * @param id
     *            User's id
     * @param name
     *            User's name
     * @param phoneNumber
     *            User's phone number
     * @param email
     *            User's email
     * @param location
     *            User's Location
     * @param locationString
     *            a String about the User's Location
     * @param image
     *            User's profile picture
     */
    public UserContainer(long id, String name, String phoneNumber, String email, Location location,
        String locationString, Bitmap image, User.BlockStatus isBlocked, int friendship) {
        mId = id;
        mName = name;
        mPhoneNumber = phoneNumber;
        mEmail = email;
        mLocation = location;
        mLocationString = locationString;
        mImage = image;
        mIsBlocked = isBlocked;
        mFriendship = friendship;
    }

    /**
     * @return email field
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * @return friendship field
     */
    public int getFriendship() {
        return mFriendship;
    }

    /**
     * @return id field
     */
    public long getId() {
        return mId;
    }

    /**
     * @return image field
     */
    public Bitmap getImage() {
        return mImage;
    }

    /**
     * @return location field
     */
    public Location getLocation() {
        return mLocation;
    }

    /**
     * @return locationString field
     */
    public String getLocationString() {
        return mLocationString;
    }

    /**
     * @return name field
     */
    public String getName() {
        return mName;
    }

    /**
     * @return phoneNumber field
     */
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    /**
     * @return isBlocked field
     */
    public User.BlockStatus isBlocked() {
        return mIsBlocked;
    }

    /**
     * @param blockedStatus
     * @return this
     */
    public UserContainer setBlocked(BlockStatus blockedStatus) {
        mIsBlocked = blockedStatus;
        return this;
    }

    /**
     * @param newEmail
     * @return this
     */
    public UserContainer setEmail(String newEmail) {
        mEmail = newEmail;
        return this;
    }

    /**
     * @param newFriendship
     * @return this
     */
    public UserContainer setFriendship(int newFriendship) {
        mFriendship = newFriendship;
        return this;
    }

    /**
     * @param newId
     * @return this
     */
    public UserContainer setId(long newId) {
        mId = newId;
        return this;
    }

    /**
     * @param newImage
     * @return this
     */
    public UserContainer setImage(Bitmap newImage) {
        mImage = newImage;
        return this;
    }

    /**
     * @param newLocation
     * @return this
     */
    public UserContainer setLocation(Location newLocation) {
        mLocation = newLocation;
        return this;
    }

    /**
     * @param newLocationString
     * @return this
     */
    public UserContainer setLocationString(String newLocationString) {
        mLocationString = newLocationString;
        return this;
    }

    /**
     * @param newName
     * @return this
     */
    public UserContainer setName(String newName) {
        mName = newName;
        return this;
    }

    /**
     * @param newPhoneNumber
     * @return this
     */
    public UserContainer setPhoneNumber(String newPhoneNumber) {
        mPhoneNumber = newPhoneNumber;
        return this;
    }
}
