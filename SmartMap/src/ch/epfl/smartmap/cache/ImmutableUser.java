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
    private final long mId;
    private final String mName;
    private final String mPhoneNumber;
    private final String mEmail;
    private final String mLocationString;
    private final Location mLocation;
    private final Bitmap mImage;

    public final static ImmutableUser NOT_FOUND = null;

    public ImmutableUser(long id, String name, String phoneNumber, String email, Location location,
        String locationString, Bitmap image) {
        if (id < 0) {
            throw new IllegalArgumentException("Cannot create User with negative ID !");
        }
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (phoneNumber == null) {
            throw new IllegalArgumentException("phoneNumber is null");
        }
        if (email == null) {
            throw new IllegalArgumentException("email is null");
        }
        if (location == null) {
            throw new IllegalArgumentException("location is null");
        }
        if (locationString == null) {
            throw new IllegalArgumentException("locationString is null");
        }
        if (image == null) {
            throw new IllegalArgumentException("image is null");
        }

        this.mId = id;
        this.mName = name;
        this.mPhoneNumber = phoneNumber;
        this.mEmail = email;
        this.mLocation = location;
        this.mLocationString = locationString;
        this.mImage = image;
    }

    public String getEmail() {
        return mEmail;
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
}
