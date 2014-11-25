package ch.epfl.smartmap.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Parcel;
import android.util.LongSparseArray;
import ch.epfl.smartmap.gui.Utils;
import ch.epfl.smartmap.listeners.OnDisplayableInformationsUpdateListener;
import ch.epfl.smartmap.listeners.OnFriendLocationChangedListener;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Represents a Friend, for each there is only one single instance that can be accessed via static method
 * {@code getFriendFromId(int id)}
 * 
 * @author jfperren
 */
public final class UniqueFriend implements User {

    // Class Map containing all unique instances of Friend
    private static final LongSparseArray<UniqueFriend> sFriendInstances = new LongSparseArray<UniqueFriend>();

    // Listeners
    private final List<OnFriendLocationChangedListener> mOnFriendLocationChangedListeners;
    private final List<OnDisplayableInformationsUpdateListener> mOnDisplayableInformationsUpdateListeners;

    // Friend informations
    private final long mID;
    private String mName;
    private String mPhoneNumber;
    private String mEmail;
    private String mLocationString;
    private Calendar mLastSeen;
    private final Location mLocation;

    private UniqueFriend(long id, String name, String phoneNumber, String email, String locationString,
        Calendar lastSeen, Location location) {
        if (id < 0) {
            throw new IllegalArgumentException("Cannot create Friend with negative ID !");
        } else {
            this.mID = id;
        }

        if (name == null) {
            this.mName = User.NO_NAME;
        } else if (name.equals("")) {
            throw new IllegalArgumentException("Cannot create Friend with empty name !");
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

        mOnDisplayableInformationsUpdateListeners = new LinkedList<OnDisplayableInformationsUpdateListener>();
        mOnFriendLocationChangedListeners = new LinkedList<OnFriendLocationChangedListener>();
    }

    public void addOnDisplayableInformationUpdateListener(OnDisplayableInformationsUpdateListener listener) {
        mOnDisplayableInformationsUpdateListeners.add(listener);
    }

    public void addOnFriendLocationChangedListener(OnFriendLocationChangedListener listener) {
        mOnFriendLocationChangedListeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#deletePicture(android.content.Context)
     */
    @Override
    public void deletePicture(Context context) {
        File file = new File(context.getFilesDir(), mID + ".png");
        if (file.exists()) {
            file.delete();
        }
    }

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
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
        Bitmap friendProfilePicture =
            Bitmap.createScaledBitmap(this.getPicture(context), PICTURE_WIDTH, PICTURE_HEIGHT, false);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(this.getLatLng()).title(this.getName())
            .icon(BitmapDescriptorFactory.fromBitmap(friendProfilePicture))
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
    public String getNumber() {
        return mPhoneNumber;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getPicture(android.content.Context)
     */
    @Override
    public Bitmap getPicture(Context context) {
        File file = new File(context.getFilesDir(), mID + ".png");

        Bitmap pic = null;

        if (file.exists()) {
            pic = BitmapFactory.decodeFile(file.getAbsolutePath());
        } else {
            pic = BitmapFactory.decodeResource(context.getResources(), DEFAULT_PICTURE);
        }
        return pic;
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
        return (Calendar.getInstance(TimeZone.getTimeZone(("GMT+01:00"))).get(Calendar.MINUTE) - this.mLastSeen
            .get(Calendar.MINUTE)) < SettingsManager.getInstance().getTimeToWaitBeforeHidingFriends();
    }

    public void
        removeOnDisplayableInformationUpdateListener(OnDisplayableInformationsUpdateListener listener) {
        mOnDisplayableInformationsUpdateListeners.remove(listener);
    }

    public void removeOnFriendLocationChangedListener(OnFriendLocationChangedListener listener) {
        mOnFriendLocationChangedListeners.remove(listener);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setEmail(java.lang.String)
     */
    @Override
    public void setEmail(String newEmail) {
        if (newEmail != null) {
            mEmail = newEmail;
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setLastSeen(java.util.Date)
     */
    @Override
    public void setLastSeen(Date date) {
        if (date != null) {
            this.mLastSeen.setTime(date);
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setLatitude(double)
     */
    @Override
    public void setLatitude(double newLatitude) {
        this.mLocation.setLatitude(newLatitude);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setLocation(android.location.Location)
     */
    @Override
    public void setLocation(Location newLocation) {
        this.setLatitude(newLocation.getLatitude());
        this.setLongitude(newLocation.getLongitude());
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setLongitude(double)
     */
    @Override
    public void setLongitude(double newLongitude) {
        this.mLocation.setLongitude(newLongitude);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setName(java.lang.String)
     */
    @Override
    public void setName(String newName) {
        if ((newName != null) && !newName.equals("")) {
            this.mName = newName;
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setNumber(java.lang.String)
     */
    @Override
    public void setNumber(String newNumber) {
        if ((newNumber != null) && !newNumber.equals("")) {
            this.mPhoneNumber = newNumber;
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setPicture(android.graphics.Bitmap, android.content.Context)
     */
    @Override
    public void setPicture(Bitmap pic, Context context) throws FileNotFoundException, IOException {
        File file = new File(context.getFilesDir(), mID + ".png");

        if (file.exists()) {
            file.delete();
        }

        FileOutputStream out = context.openFileOutput(mID + ".png", Context.MODE_PRIVATE);
        pic.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, out);
        out.close();
    }

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mID);
    }

    private void onEmailChanged() {

        // TODO : Update database
    }

    private void onImageChanged() {

        // TODO : Update database

        for (OnDisplayableInformationsUpdateListener l : mOnDisplayableInformationsUpdateListeners) {
            l.onImageChanged();
        }
    }

    private void onLastSeenChanged() {

        // TODO : Update database

    }

    private void onLocationChanged() {

        // TODO : Update database

        for (OnFriendLocationChangedListener l : mOnFriendLocationChangedListeners) {
            l.onFriendLocationChanged();
        }
    }

    private void onLocationStringChanged() {

        // TODO : Update database

        for (OnDisplayableInformationsUpdateListener l : mOnDisplayableInformationsUpdateListeners) {
            l.onShortInfoChanged();
        }
    }

    private void onNameChanged() {

        // TODO : Update database

        for (OnDisplayableInformationsUpdateListener l : mOnDisplayableInformationsUpdateListeners) {
            l.onNameChanged();
        }
    }

    private void onPhoneNumberChange() {

        // TODO : Update database

    }

    public static UniqueFriend getFriendFromId(long id) {
        // Try to get friend from cache
        UniqueFriend friend = sFriendInstances.get(Long.valueOf(id));

        if (friend == null) {
            // Try to get friend from local database
            User user = DatabaseHelper.getInstance().getUser(id);

            if (user != null) {
                friend =
                    new UniqueFriend(user.getID(), user.getName(), user.getNumber(), user.getEmail(),
                        user.getLocationString(), user.getLastSeen(), user.getLocation());

                // TODO : Add database listeners

                sFriendInstances.put(Long.valueOf(id), friend);
            } else {
                // TODO : Get online
            }
        }

        return friend;
    }
}
