package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.content.Context;
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

public final class Friend extends AbstractUser {

    private String mPhoneNumber;
    private String mEmail;
    private String mLocationString;
    private Location mLocation;

    private final MarkerIconMaker mMarkerIconMaker;

    protected Friend(ImmutableUser user) {
        super(user);

        if (user.getPhoneNumber() == null) {
            mPhoneNumber = User.NO_PHONE_NUMBER;
        } else {
            mPhoneNumber = user.getPhoneNumber();
        }
        if (user.getEmail() == null) {
            mEmail = User.NO_EMAIL;
        } else {
            mEmail = user.getEmail();
        }
        if (user.getLocationString() == null) {
            mLocationString = User.NO_LOCATION_STRING;
        } else {
            mLocationString = user.getLocationString();
        }
        if (user.getLocation() == null) {
            mLocation = User.NO_LOCATION;
        } else {
            mLocation = new Location(user.getLocation());
        }

        mMarkerIconMaker = new CircularMarkerIconMaker(this);
    }

    public String getEmail() {
        return mEmail;
    }

    @Override
    public ImmutableUser getImmutableCopy() {
        return super.getImmutableCopy().setPhoneNumber(mPhoneNumber).setEmail(mEmail).setLocation(mLocation)
            .setLocationString(mLocationString);
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

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#isFriend()
     */
    @Override
    public boolean isFriend() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#isVisible()
     */
    @Override
    public boolean isVisible() {
        return ServiceContainer.getCache().getAllActiveFilters().contains(this);
    }

    @Override
    public void update(ImmutableUser user) {
        super.update(user);

        mPhoneNumber = (user.getPhoneNumber() != null) ? user.getPhoneNumber() : mPhoneNumber;
        mEmail = (user.getEmail() != null) ? user.getEmail() : mEmail;
        mLocationString = (user.getLocationString() != null) ? user.getLocationString() : mLocationString;
        mLocation = (user.getLocation() != null) ? new Location(user.getLocation()) : mLocation;
    }
}
