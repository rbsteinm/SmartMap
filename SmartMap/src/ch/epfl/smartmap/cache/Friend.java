package ch.epfl.smartmap.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import ch.epfl.smartmap.R;

import com.google.android.gms.maps.model.LatLng;

/**
 * A class to represent the user's friends
 * 
 * @author ritterni
 */
public class Friend implements User, Searchable, Displayable, Parcelable {

    private final long mId; // the user's unique ID
    private String mName; // the user's name as it will be displayed
    private String mPhoneNumber;
    private String mEmail;
    private String mPositionName;
    private final GregorianCalendar mLastSeen;
    private boolean mOnline;
    private final Location mLocation;
    private boolean mVisible;

    public static final String NO_NUMBER = "No phone number specified";
    public static final String NO_EMAIL = "No email address specified";
    public static final String POSITION_UNKNOWN = "Unknown position";
    public static final int DEFAULT_PICTURE = R.drawable.ic_default_user; // placeholder
    public static final int IMAGE_QUALITY = 100;
    public static final String PROVIDER_NAME = "SmartMapServers";

    private static final int LEFT_SHIFT_COUNT = 32;

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
        mId = userID;
        mName = userName;
        mPhoneNumber = NO_NUMBER;
        mEmail = NO_EMAIL;
        mPositionName = POSITION_UNKNOWN;
        mLastSeen = new GregorianCalendar();
        mLocation = new Location(PROVIDER_NAME);
        mVisible = true;
        mOnline = false;
    }

    public Friend(long userID, String userName, double latitude, double longitude) {
        this(userID, userName);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
    }

    public Friend(Parcel in) {
        this.mId = in.readLong();
        this.mName = in.readString();
        this.mPhoneNumber = in.readString();
        this.mEmail = in.readString();
        this.mLocation = in.readParcelable(Location.class.getClassLoader());
        this.mPositionName = in.readString();
        this.mLastSeen = new GregorianCalendar();
        this.mLastSeen.setTimeInMillis(in.readLong());
        boolean[] booleans = new boolean[2];
        in.readBooleanArray(booleans);
        this.mVisible = booleans[0];
        this.mOnline = booleans[1];
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
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Friend other = (Friend) obj;
        if (mId != other.mId) {
            return false;
        }
        if (mName == null) {
            if (other.mName != null) {
                return false;
            }
        } else if (!mName.equals(other.mName)) {
            return false;
        }
        return true;
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
        return mLastSeen;
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
    public String getName() {
        return mName;
    }

    @Override
    public String getNumber() {
        return mPhoneNumber;
    }

    @Override
    public Bitmap getPicture(Context context) {

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
    public String getPositionName() {
        return mPositionName;
    }

    @Override
    public String getShortInfos() {
        // TODO
        return "Seen 10 minutes ago near Lausanne";
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + (int) (mId ^ (mId >>> LEFT_SHIFT_COUNT));
        result = (prime * result) + ((mName == null) ? 0 : mName.hashCode());
        return result;
    }

    @Override
    public boolean isOnline() {
        return mOnline;
    }

    @Override
    public boolean isVisible() {
        return mVisible;
    }

    @Override
    public void setEmail(String newEmail) {
        mEmail = newEmail;
    }

    @Override
    public void setLastSeen(GregorianCalendar date) {
        mLastSeen.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE),
            date.get(Calendar.HOUR), date.get(Calendar.MINUTE));
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
        mName = newName;
    }

    @Override
    public void setNumber(String newNumber) {
        mPhoneNumber = newNumber;
    }

    @Override
    public void setOnline(boolean status) {
        mOnline = status;
    }

    @Override
    public void setPicture(Bitmap pic, Context context) {

        File file = new File(context.getFilesDir(), mId + ".png");

        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream out = context.openFileOutput(mId + ".png", Context.MODE_PRIVATE);
            pic.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPositionName(String posName) {
        mPositionName = posName;
    }

    @Override
    public void setVisible(boolean isVisible) {
        mVisible = isVisible;
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
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
        dest.writeString(mPhoneNumber);
        dest.writeString(mEmail);
        dest.writeParcelable(mLocation, flags);
        dest.writeString(mPositionName);
        dest.writeLong(mLastSeen.getTimeInMillis());
        boolean[] booleans = new boolean[]{mVisible, mOnline};
        dest.writeBooleanArray(booleans);
    }
}