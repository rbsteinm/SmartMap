package ch.epfl.smartmap.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import ch.epfl.smartmap.gui.Utils;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A class to represent the user's friends
 * 
 * @author ritterni
 */
public class Friend implements User, Displayable, Parcelable {

    private final long mId; // the user's unique ID
    private String mName; // the user's name as it will be displayed
    private String mPhoneNumber;
    private String mEmail;
    private String mLocationString;
    private GregorianCalendar mLastSeen;
    private final Location mLocation;
    private boolean mVisible;

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public Friend createFromParcel(Parcel source) {
            return new Friend(source);
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };

    /**
     * Friend constructor
     * 
     * @param userID
     *            The id of the contact we're creating
     * @param userName
     *            The name of the friend
     * @param userNumber
     *            The friend's phone number
     * @author ritterni
     */
    public Friend(long userID, String userName) {
        if (userID < 0) {
            throw new IllegalArgumentException("Invalid user ID!");
        }
        if (userName == null) {
            throw new IllegalArgumentException("Invalid user name!");
        }
        mId = userID;
        mName = userName;
        mPhoneNumber = NO_NUMBER;
        mEmail = NO_EMAIL;
        mLocationString = Utils.UNKNOWN_LOCATION;
        mLastSeen = new GregorianCalendar();
        mLastSeen.setTimeInMillis(0);
        mLocation = new Location(PROVIDER_NAME);
        mVisible = true;
    }

    public Friend(long userID, String userName, double longitude, double latitude) {
        this(userID, userName);

        this.setLatitude(latitude);
        this.setLongitude(longitude);
    }

    public Friend(Parcel in) {
        mId = in.readLong();
        mName = in.readString();
        mPhoneNumber = in.readString();
        mEmail = in.readString();
        mLocation = in.readParcelable(Location.class.getClassLoader());
        mLocationString = in.readString();
        mLastSeen = new GregorianCalendar();
        mLastSeen.setTimeInMillis(in.readLong());
        boolean[] booleans = new boolean[1];
        in.readBooleanArray(booleans);
        mVisible = booleans[0];
    }

    @Override
    public void deletePicture(Context context) {
        File file = new File(context.getFilesDir(), mId + ".png");
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
        return 1;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object that) {
        return (that != null) && (that instanceof Friend) && (mId == ((Friend) that).mId);
    }

    @Override
    public String getEmail() {
        return mEmail;
    }

    @Override
    public long getID() {
        return mId;
    }

    @Override
    public GregorianCalendar getLastSeen() {
        GregorianCalendar g = new GregorianCalendar(TimeZone.getTimeZone("GMT+01:00"));
        g.setTimeInMillis(mLastSeen.getTimeInMillis());
        return g;
    }

    @Override
    public LatLng getLatLng() {
        return new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
    }

    @Override
    public Location getLocation() {
        return mLocation;
    }

    @Override
    public String getLocationString() {
        return mLocationString;
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.cache.Displayable#getMarkerOptions(android.content.Context
     * )
     * @author hugo-S
     */
    @Override
    public MarkerOptions getMarkerOptions(Context context) {
        Bitmap friendProfilePicture =
            Bitmap.createScaledBitmap(this.getImage(context), PICTURE_WIDTH, PICTURE_HEIGHT, false);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(this.getLatLng()).title(this.getName())
            .icon(BitmapDescriptorFactory.fromBitmap(friendProfilePicture))
            .anchor(MARKER_ANCHOR_X, MARKER_ANCHOR_Y);
        return markerOptions;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String getNumber() {
        return mPhoneNumber;
    }

    @Override
    public Bitmap getImage(Context context) {

        File file = new File(context.getFilesDir(), mId + ".png");

        Bitmap pic = null;

        if (file.exists()) {
            pic = BitmapFactory.decodeFile(file.getAbsolutePath());
        } else {
            pic = BitmapFactory.decodeResource(context.getResources(), DEFAULT_PICTURE);
        }
        return pic;
    }

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
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;

        return ((int) mId) * prime;
    }

    public boolean isOnline() {
        return (new GregorianCalendar().getTimeInMillis() - mLastSeen.getTimeInMillis()) < ONLINE_TIMEOUT;
    }

    @Override
    public boolean isVisibleOnMap() {
        return mVisible;
    }

    @Override
    public void setEmail(String newEmail) {
        mEmail = newEmail;
    }

    @Override
    public void setLastSeen(Date date) {
        mLastSeen.setTime(date);
    }

    @Override
    public void setLatitude(double latitude) {
        mLocation.setLatitude(latitude);
    }

    @Override
    public void setLocation(Location p) {
        mLocation.set(p);
    }

    @Override
    public void setLongitude(double longitude) {
        mLocation.setLongitude(longitude);

    }

    @Override
    public void setName(String newName) {
        if ((newName == null) || newName.isEmpty()) {
            throw new IllegalArgumentException("Invalid user name!");
        }
        mName = newName;
    }

    @Deprecated
    public void setOnline(boolean status) {
        // deprecated
    }

    @Override
    public void setPhoneNumber(String newNumber) {
        mPhoneNumber = newNumber;
    }

    @Override
    public void setImage(Bitmap pic, Context context) throws FileNotFoundException, IOException {

        File file = new File(context.getFilesDir(), mId + ".png");

        if (file.exists()) {
            file.delete();
        }

        FileOutputStream out = context.openFileOutput(mId + ".png", Context.MODE_PRIVATE);
        pic.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, out);
        out.close();

    }

    public void setPositionName(String posName) {
        mLocationString = posName;
    }

    public void setVisible(boolean isVisible) {
        mVisible = isVisible;
    }

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
        dest.writeString(mPhoneNumber);
        dest.writeString(mEmail);
        dest.writeParcelable(mLocation, flags);
        dest.writeString(mLocationString);
        dest.writeLong(mLastSeen.getTimeInMillis());
        boolean[] booleans = new boolean[]{mVisible};
        dest.writeBooleanArray(booleans);
    }

    private void updateLocationString() {
        mLocationString = Utils.getCityFromLocation(this.getLocation());
    }
}