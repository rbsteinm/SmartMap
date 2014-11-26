package ch.epfl.smartmap.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import ch.epfl.smartmap.gui.Utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Immutable implementation of User, which serves only as container purposes, therefore all set methods are
 * disabled.
 * 
 * @author jfperren
 */
public class ImmutableUser implements User {

    // User informations
    private final long mID;
    private String mName;
    private String mPhoneNumber;
    private String mEmail;
    private String mLocationString;
    private Calendar mLastSeen;
    private final Location mLocation;

    private ImmutableUser(long id, String name, String phoneNumber, String email, String locationString,
        GregorianCalendar lastSeen, Location location) {
        if (id < 0) {
            throw new IllegalArgumentException("Cannot create User with negative ID !");
        } else {
            this.mID = id;
        }

        if (name == null) {
            this.mName = User.NO_NAME;
        } else if (name.equals("")) {
            throw new IllegalArgumentException("Cannot create User with empty name !");
        } else {
            this.mName = name;
        }

        if (phoneNumber == null) {
            this.mPhoneNumber = User.NO_NUMBER;
        } else {
            mPhoneNumber = phoneNumber;
        }

        if (email == null) {
            this.mEmail = User.NO_EMAIL;
        } else {
            this.mEmail = email;
        }

        if (location == null) {
            this.mLocation = new Location(User.PROVIDER_NAME);
        } else {
            this.mLocation = location;
        }

        if (mLocationString == null) {
            this.mLocationString = User.NO_LOCATION;
        } else {
            this.mLocationString = locationString;
        }

        if (lastSeen == null) {
            this.mLastSeen = GregorianCalendar.getInstance();
            mLastSeen.setTimeInMillis(0);
        } else {
            this.mLastSeen = (Calendar) lastSeen.clone();
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#deletePicture(android.content.Context)
     */
    @Override
    public void deletePicture(Context context) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object that) {
        return (that != null) && (that instanceof UniqueFriend) && (mID == ((User) that).getID());
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getEmail()
     */
    @Override
    public String getEmail() {
        return mEmail;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getID()
     */
    @Override
    public long getID() {
        return mID;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getPicture(android.content.Context)
     */
    @Override
    public Bitmap getImage(Context context) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getLastSeen()
     */
    @Override
    public Calendar getLastSeen() {
        return (Calendar) mLastSeen.clone();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getLatLng()
     */
    @Override
    public LatLng getLatLng() {
        return new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getLocation()
     */
    @Override
    public Location getLocation() {
        Location location = new Location(mLocation.getProvider());
        location.setLatitude(mLocation.getLatitude());
        location.setLongitude(mLocation.getLongitude());
        return location;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getLocationString()
     */
    @Override
    public String getLocationString() {
        return mLocationString;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getMarkerOptions(android.content.Context)
     */
    @Override
    public MarkerOptions getMarkerOptions(Context context) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getName()
     */
    @Override
    public String getName() {
        return mName;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getNumber()
     */
    @Override
    public String getNumber() {
        return mPhoneNumber;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getShortInfos()
     */
    @Override
    public String getShortInfos() {
        String infos = "";
        infos += Utils.getLastSeenStringFromCalendar(this.getLastSeen());
        infos += " near ";
        infos += mLocationString;

        return infos;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashcode
     */
    @Override
    public int hashCode() {
        return (int) mID;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#isVisible()
     */
    @Override
    public boolean isVisibleOnMap() {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setEmail(java.lang.String)
     */
    @Override
    public void setEmail(String newEmail) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setPicture(android.graphics.Bitmap, android.content.Context)
     */
    @Override
    public void setImage(Bitmap newImage, Context context) throws FileNotFoundException, IOException {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setLastSeen(java.util.Date)
     */
    @Override
    public void setLastSeen(Date newDate) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setLatitude(double)
     */
    @Override
    public void setLatitude(double newLatitude) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setLocation(android.location.Location)
     */
    @Override
    public void setLocation(Location newLocation) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setLongitude(double)
     */
    @Override
    public void setLongitude(double newLongitude) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setName(java.lang.String)
     */
    @Override
    public void setName(String newName) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setNumber(java.lang.String)
     */
    @Override
    public void setPhoneNumber(String newNumber) {
        throw new UnsupportedOperationException();
    }
}
