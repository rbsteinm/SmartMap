package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.graphics.Bitmap;
import android.location.Location;
import ch.epfl.smartmap.gui.Utils;
import ch.epfl.smartmap.listeners.DisplayableListener;
import ch.epfl.smartmap.listeners.LocalisableListener;
import ch.epfl.smartmap.listeners.UserListener;

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
public final class Friend extends AbstractUser {

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
        mID = user.getId();
        mName = user.getName();
        mPhoneNumber = user.getPhoneNumber();
        mEmail = user.getEmail();
        mLocationString = user.getLocationString();
        mLocation = user.getLocation();
        mImage = user.getImage();
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
    public MarkerOptions getMarkerOptions() {
        Bitmap friendProfilePicture =
            Bitmap.createScaledBitmap(this.getImage(), PICTURE_WIDTH, PICTURE_HEIGHT, false);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()))
            .title(this.getName()).icon(BitmapDescriptorFactory.fromBitmap(friendProfilePicture))
            .anchor(MARKER_ANCHOR_X, MARKER_ANCHOR_Y);
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

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setEmail(java.lang.String)
     */
    @Override
    public void setEmail(String newEmail) {
        if (newEmail != null) {
            mEmail = newEmail;
            for (UserListener listener : mUserListeners) {
                listener.onEmailChanged();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setImage(android.graphics.Bitmap)
     */
    @Override
    public void setImage(Bitmap newImage) {
        if (newImage != null) {
            mImage = newImage;
            for (DisplayableListener listener : mDisplayableListeners) {
                listener.onImageChanged();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setLocation(android.location.Location)
     */
    @Override
    public void setLocation(Location newLocation) {
        this.mLocation = newLocation;
        for (LocalisableListener listener : mLocalisableListeners) {
            listener.onLocationChanged();
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setName(java.lang.String)
     */
    @Override
    public void setName(String newName) {
        if ((newName != null) && !newName.equals("")) {
            this.mName = newName;
            for (UserListener listener : mUserListeners) {
                listener.onNameChanged();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setNumber(java.lang.String)
     */
    @Override
    public void setPhoneNumber(String newPhoneNumber) {
        if ((newPhoneNumber != null) && !newPhoneNumber.equals("")) {
            this.mPhoneNumber = newPhoneNumber;
            for (UserListener listener : mUserListeners) {
                listener.onPhoneNumberChanged();
            }
        }
    }
}
