package ch.epfl.smartmap.cache;

import android.graphics.Bitmap;
import android.location.Location;

/**
 * Immutable implementation of User, which serves only as container purposes, therefore all set methods are
 * disabled.
 * 
 * @author jfperren
 */
public final class ImmutableUser {

    // User informations
    private long mId;
    private String mName;
    private String mPhoneNumber;
    private String mEmail;
    private String mLocationString;
    private Location mLocation;
    private Bitmap mImage;
    private boolean mIsBlocked;
    private int mFriendship;

    /**
     * Constructor, put {@code null} (or {@code User.NO_ID} for id) if you dont want the value to be taken
     * into account.
     * 
     * @param id
     * @param name
     * @param phoneNumber
     * @param email
     * @param location
     * @param locationString
     * @param image
     */
    public ImmutableUser(long id, String name, String phoneNumber, String email, Location location,
        String locationString, Bitmap image, boolean isBlocked, int friendship) {
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

    public String getEmail() {
        return mEmail;
    }

    public int getFriendship() {
        return mFriendship;
    }

    public long getId() {
        return mId;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public Location getLocation() {
        return mLocation;
    }

    public String getLocationString() {
        return mLocationString;
    }

    public String getName() {
        return mName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public Boolean isBlocked() {
        return mIsBlocked;
    }

    public ImmutableUser setEmail(String newEmail) {
        mEmail = newEmail;
        return this;
    }

    public ImmutableUser setFriendship(int newFriendship) {
        mFriendship = newFriendship;
        return this;
    }

    public ImmutableUser setImage(Bitmap newImage) {
        mImage = newImage;
        return this;
    }

    public ImmutableUser setLocation(Location newLocation) {
        mLocation = new Location(newLocation);
        return this;
    }

    public ImmutableUser setLocationString(String newLocationString) {
        mLocationString = newLocationString;
        return this;
    }

    public ImmutableUser setPhoneNumber(String newPhoneNumber) {
        mPhoneNumber = newPhoneNumber;
        return this;
    }
}
