package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import android.graphics.Bitmap;
import android.location.Location;
import ch.epfl.smartmap.gui.Utils;
import ch.epfl.smartmap.listeners.OnDisplayableUpdateListener;
import ch.epfl.smartmap.listeners.OnUserUpdateListener;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Represents a Friend, for each there is only one single instance that can be accessed via static method
 * {@code getFriendFromId(int id)}
 * 
 * @author jfperren
 */
public final class Friend implements User {

    // Instance Listeners
    private final List<OnDisplayableUpdateListener> mOnDisplayableUpdateListeners;
    private final List<OnUserUpdateListener> mOnUserUpdateListeners;

    // Friend informations
    private final long mID;
    private String mName;
    private String mPhoneNumber;
    private String mEmail;
    private String mLocationString;
    private Location mLocation;
    private Bitmap mImage;

    protected Friend(ImmutableUser user) {
        mID = user.getId();
        mName = user.getName();
        mPhoneNumber = user.getPhoneNumber();
        mEmail = user.getEmail();
        mLocationString = user.getLocationString();
        mLocation = user.getLocation();
        mImage = user.getImage();

        mOnDisplayableUpdateListeners = new LinkedList<OnDisplayableUpdateListener>();
        mOnUserUpdateListeners = new LinkedList<OnUserUpdateListener>();
    }

    @Override
    public void addOnDisplayableUpdateListener(OnDisplayableUpdateListener listener) {
        mOnDisplayableUpdateListeners.add(listener);
    }

    public void addOnUserUpdateListener(OnUserUpdateListener listener) {
        mOnUserUpdateListeners.add(listener);
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
     * @see java.lang.Object#hashcode
     */
    @Override
    public int hashCode() {
        return (int) mID;
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
     * @see ch.epfl.smartmap.cache.Localisable#isShown()
     */
    @Override
    public boolean isShown() {
        // TODO : Implement settings here
        return true;
    }

    @Override
    public void removeOnDisplayableUpdateListener(OnDisplayableUpdateListener listener) {
        mOnDisplayableUpdateListeners.remove(listener);
    }

    public void removeOnUserUpdateListener(OnUserUpdateListener listener) {
        mOnUserUpdateListeners.remove(listener);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setEmail(java.lang.String)
     */
    @Override
    public void setEmail(String newEmail) {
        if (newEmail != null) {
            mEmail = newEmail;
            this.onEmailChanged();
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setImage(android.graphics.Bitmap)
     */
    @Override
    public void setImage(Bitmap newImage) {
        mImage = newImage;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setLocation(android.location.Location)
     */
    @Override
    public void setLocation(Location newLocation) {
        this.mLocation = newLocation;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setName(java.lang.String)
     */
    @Override
    public void setName(String newName) {
        if ((newName != null) && !newName.equals("")) {
            this.mName = newName;
            this.onNameChanged();
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
            this.onPhoneNumberChange();
        }
    }

    /**
     * Calls listeners on email field
     */
    private void onEmailChanged() {
        for (OnUserUpdateListener listener : mOnUserUpdateListeners) {
            listener.onEmailChanged();
        }
    }

    /**
     * Calls listeners on image field
     */
    private void onImageChanged() {
        for (OnDisplayableUpdateListener listener : mOnDisplayableUpdateListeners) {
            listener.onImageChanged();
        }
        for (OnUserUpdateListener listener : mOnUserUpdateListeners) {
            listener.onImageChanged();
        }
    }

    /**
     * Calls listeners on lastSeen field
     */
    private void onLastSeenChanged() {
        for (OnUserUpdateListener listener : mOnUserUpdateListeners) {
            listener.onLastSeenChanged();
        }
        for (OnDisplayableUpdateListener listener : mOnDisplayableUpdateListeners) {
            listener.onShortInfoChanged();
        }
    }

    /**
     * Calls listeners on location field
     */
    private void onLocationChanged() {
        for (OnDisplayableUpdateListener listener : mOnDisplayableUpdateListeners) {
            listener.onLocationChanged();
        }
        for (OnUserUpdateListener listener : mOnUserUpdateListeners) {
            listener.onLocationChanged();
        }
    }

    /**
     * Calls listeners on locationString field
     */
    private void onLocationStringChanged() {
        for (OnDisplayableUpdateListener listener : mOnDisplayableUpdateListeners) {
            listener.onShortInfoChanged();
        }
        for (OnUserUpdateListener listener : mOnUserUpdateListeners) {
            listener.onLocationStringChanged();
        }
    }

    /**
     * Calls listeners on name field
     */
    private void onNameChanged() {
        for (OnDisplayableUpdateListener listener : mOnDisplayableUpdateListeners) {
            listener.onNameChanged();
        }
        for (OnUserUpdateListener listener : mOnUserUpdateListeners) {
            listener.onNameChanged();
        }
    }

    /**
     * Calls listeners on phone number field
     */
    private void onPhoneNumberChange() {
        for (OnUserUpdateListener listener : mOnUserUpdateListeners) {
            listener.onPhoneNumberChanged();
        }
    }
}
