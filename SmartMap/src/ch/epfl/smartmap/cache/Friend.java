package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.map.CircularMarkerIconMaker;
import ch.epfl.smartmap.map.MarkerIconMaker;
import ch.epfl.smartmap.util.Utils;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * Represents a Friend, for each there is only one single instance that can be accessed via static method
 * {@code getFriendFromId(int id)}
 * 
 * @author jfperren
 * @author ritterni
 */

public final class Friend extends User {

    private String mPhoneNumber;
    private String mEmail;
    private String mLocationString;
    private Location mLocation;
    private User.BlockStatus mIsBlocked;
    private final MarkerIconMaker mMarkerIconMaker;

    protected Friend(long id, String name, String phoneNumber, String email, Bitmap image, Location location,
        String locationString, User.BlockStatus isBlocked) {
        super(id, name, image);

        if (locationString == null) {
            mLocationString = User.NO_LOCATION_STRING;
        } else {
            mLocationString = locationString;
        }

        if (location == null) {
            mLocation = User.NO_LOCATION;
            mLocation.setTime(User.NO_LAST_SEEN.getTimeInMillis());
            mLocation.setLatitude(User.NO_LATITUDE);
            mLocation.setLongitude(User.NO_LONGITUDE);
        } else {
            mLocation = new Location(location);
        }

        if (email == null) {
            mEmail = NO_EMAIL;
        } else {
            mEmail = email;
        }

        if (phoneNumber == null) {
            mPhoneNumber = NO_PHONE_NUMBER;
        } else {
            mPhoneNumber = phoneNumber;
        }

        if ((isBlocked == BlockStatus.NOT_SET) || (isBlocked == null)) {
            mIsBlocked = BlockStatus.BLOCKED;
        } else {
            mIsBlocked = isBlocked;
        }

        mMarkerIconMaker = new CircularMarkerIconMaker(this);
    }

    @Override
    public User.BlockStatus getBlockStatus() {
        return mIsBlocked;
    }

    @Override
    public UserContainer getContainerCopy() {
        return super.getContainerCopy().setPhoneNumber(mPhoneNumber).setEmail(mEmail).setLocation(mLocation)
            .setLocationString(mLocationString).setBlocked(mIsBlocked);
    }

    public String getEmail() {
        return mEmail;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getFriendship()
     */
    @Override
    public int getFriendship() {
        return User.FRIEND;
    }

    public Calendar getLastSeen() {
        Calendar lastSeen = GregorianCalendar.getInstance(TimeZone.getDefault());
        lastSeen.setTimeInMillis(mLocation.getTime());
        return lastSeen;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Localisable#getLatLng()
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

    @Override
    public BitmapDescriptor getMarkerIcon(Context context) {
        return BitmapDescriptorFactory.fromBitmap(mMarkerIconMaker.getMarkerIcon(context));
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getShortInfos()
     */
    @Override
    public String getSubtitle() {
        String infos = Utils.getLastSeenStringFromCalendar(this.getLastSeen());
        if (!infos.equals(Utils.NEVER_SEEN)) {
            infos += " near " + mLocationString;
        }

        return infos;
    }

    public boolean isVisible() {
        return ServiceContainer.getCache().getAllActiveFilters().contains(this)
            && !mLocation.equals(NO_LOCATION);
    }

    @Override
    public boolean update(UserContainer newValues) {
        boolean hasChanged = super.update(newValues);

        if ((newValues.getLocation() != null)
            && (newValues.getLocation() != User.NO_LOCATION)
            && ((newValues.getLocation().getLatitude() != mLocation.getLatitude())
                || (newValues.getLocation().getLongitude() != mLocation.getLongitude()) || (newValues
                .getLocation().getTime() != mLocation.getTime()))) {
            mLocation = new Location(newValues.getLocation());
            mLocation.setLatitude(newValues.getLocation().getLatitude());
            mLocation.setLongitude(newValues.getLocation().getLongitude());

            hasChanged = true;
        }

        if ((newValues.getLocationString() != null)
            && (newValues.getLocationString() != User.NO_LOCATION_STRING)
            && !newValues.getLocationString().equals(mLocationString)) {
            mLocationString = newValues.getLocationString();
            hasChanged = true;
        }

        if ((newValues.isBlocked() != null) && (newValues.isBlocked() != User.BlockStatus.NOT_SET)
            && (newValues.isBlocked() != mIsBlocked)) {
            mIsBlocked = newValues.isBlocked();
            hasChanged = true;
        }

        if ((newValues.getEmail() != null) && (newValues.getEmail() != User.NO_EMAIL)
            && !newValues.getEmail().equals(mEmail)) {
            mEmail = newValues.getEmail();
            hasChanged = true;
        }

        if ((newValues.getPhoneNumber() != null) && (newValues.getPhoneNumber() != User.NO_PHONE_NUMBER)
            && !newValues.getPhoneNumber().equals(mPhoneNumber)) {
            mPhoneNumber = newValues.getPhoneNumber();
        }

        return hasChanged;
    }
}
