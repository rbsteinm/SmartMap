package ch.epfl.smartmap.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    protected Friend(long id, String name, String phoneNumber, String email, String locationString,
        Location location) {
        if (id < 0) {
            throw new IllegalArgumentException("Cannot create Friend with negative ID !");
        } else {
            this.mID = id;
        }

        if (name == null) {
            this.mName = NO_NAME;
        } else if (name.equals("")) {
            throw new IllegalArgumentException("Cannot create Friend with empty name !");
        } else {
            this.mName = name;
        }

        if (phoneNumber == null) {
            this.mPhoneNumber = NO_NUMBER;
        } else {
            mPhoneNumber = phoneNumber;
        }

        if (email == null) {
            this.mEmail = User.NO_EMAIL;
        } else {
            this.mEmail = email;
        }

        if (location == null) {
            this.mLocation = new Location(PROVIDER_NAME);
        } else {
            this.mLocation = location;
        }

        if (mLocationString == null) {
            this.mLocationString = NO_LOCATION_STRING;
        } else {
            this.mLocationString = locationString;
        }

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
        return (that != null) && (that instanceof Friend) && (mID == ((User) that).getID());
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
        File file = new File(context.getFilesDir(), mID + ".png");

        Bitmap pic = null;

        if (file.exists()) {
            pic = BitmapFactory.decodeFile(file.getAbsolutePath());
        } else {
            pic = NO_IMAGE;
        }
        return pic;
    }

    public ImmutableUser getImmutableCopy() {
        return new ImmutableUser(mID, mName, mPhoneNumber, mEmail, mLocation, mLocationString);
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
        Bitmap friendProfilePicture =
            Bitmap.createScaledBitmap(this.getImage(context), PICTURE_WIDTH, PICTURE_HEIGHT, false);
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
        // TODO Auto-generated method stub
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
     * @see ch.epfl.smartmap.cache.User#isVisible()
     */
    @Override
    public boolean isVisibleOnMap() {
        return (Calendar.getInstance(TimeZone.getTimeZone(("GMT+01:00"))).get(Calendar.MINUTE) - this
            .getLastSeen().get(Calendar.MINUTE)) < SettingsManager.getInstance()
            .getTimeToWaitBeforeHidingFriends();
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
     * @see ch.epfl.smartmap.cache.User#setPicture(android.graphics.Bitmap, android.content.Context)
     */
    @Override
    public void setImage(Bitmap newImage, Context context) throws FileNotFoundException, IOException {
        if (newImage != null) {
            File file = new File(context.getFilesDir(), mID + ".png");

            if (file.exists()) {
                file.delete();
            }

            FileOutputStream out = context.openFileOutput(mID + ".png", Context.MODE_PRIVATE);
            newImage.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, out);
            out.close();

            this.onImageChanged();
        }
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
