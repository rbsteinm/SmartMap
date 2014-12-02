package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import ch.epfl.smartmap.gui.Utils;
import ch.epfl.smartmap.map.CircularMarkerIconMaker;
import ch.epfl.smartmap.map.MarkerIconMaker;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Represents a Friend, for each there is only one single instance that can be accessed via static method
 * {@code getFriendFromId(int id)}
 * 
 * @author jfperren
 * @author ritterni
 */

public final class Friend implements User {

    // Friend informations
    private final long mID;
    private String mName;
    private String mPhoneNumber;
    private String mEmail;
    private String mLocationString;
    private Location mLocation;
    private Bitmap mImage;

    protected Friend(ImmutableUser user) {
        super();

        if (user.getId() < 0) {
            throw new IllegalArgumentException();
        } else {
            mID = user.getId();
        }
        if (user.getName() == null) {
            throw new IllegalArgumentException();
        } else {
            mName = user.getName();
        }
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
        if (user.getImage() == null) {
            mImage = User.NO_IMAGE;
        } else {
            mImage = user.getImage();
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object that) {
        return (that != null) && (that instanceof Friend) && (mID == ((User) that).getId());
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
    public long getId() {
        return mID;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getPicture(android.content.Context)
     */
    @Override
    public Bitmap getImage() {
        return mImage;
    }

    @Override
    public ImmutableUser getImmutableCopy() {
        return new ImmutableUser(mID, mName, mPhoneNumber, mEmail, mLocation, mLocationString, mImage);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getLastSeen()
     */
    @Override
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

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Localisable#getMarkerOptions()
     */
    @Override
    public MarkerOptions getMarkerOptions(Context context) {
        MarkerIconMaker iconMaker = new CircularMarkerIconMaker(context);

        Bitmap profilePicture =
            Bitmap.createScaledBitmap(this.getImage(), PICTURE_WIDTH, PICTURE_HEIGHT, false);

        Bitmap markerIcon = iconMaker.getMarkerIcon(profilePicture);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(this.getLatLng()).title(this.getName())
            .icon(BitmapDescriptorFactory.fromBitmap(markerIcon)).anchor(MARKER_ANCHOR_X, MARKER_ANCHOR_Y);

        return markerOptions;
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
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getShortInfos()
     */
    @Override
    public String getSubtitle() {
        String infos = "";
        infos += Utils.getLastSeenStringFromCalendar(this.getLastSeen());
        infos += " near ";
        infos += mLocationString;

        return infos;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getTitle()
     */
    @Override
    public String getTitle() {
        return mName;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getType()
     */
    @Override
    public Type getType() {
        return Type.FRIEND;
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
     * @see ch.epfl.smartmap.cache.Localisable#isShown()
     */
    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void update(ImmutableUser user) {
        if (user.getName() != null) {
            mName = user.getName();
        }
        if (user.getPhoneNumber() != null) {
            mPhoneNumber = user.getPhoneNumber();
        }
        if (user.getEmail() != null) {
            mEmail = user.getEmail();
        }
        if (user.getLocationString() != null) {
            mLocationString = user.getLocationString();
        }
        if (user.getLocation() != null) {
            mLocation = new Location(user.getLocation());
        }
        if (user.getImage() != null) {
            mImage = user.getImage();
        }
    }
}
