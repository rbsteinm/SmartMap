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
import ch.epfl.smartmap.R;

import com.google.android.gms.maps.model.LatLng;

/**
 * A class to represent the user's friends
 * 
 * @author ritterni
 */
public class Friend implements User {

	private long mId; // the user's unique ID
	private String mName; // the user's name as it will be displayed
	private String mPhoneNumber;
	private String mEmail;
	private String mPositionName;
	private GregorianCalendar mLastSeen;
	private boolean mOnline;
	private Location mLocation;
	private boolean mVisible;

	public static final String NO_NUMBER = "No phone number specified";
	public static final String NO_EMAIL = "No email address specified";
	public static final String POSITION_UNKNOWN = "Unknown position";
	public static final int DEFAULT_PICTURE = R.drawable.default_user_icon; // placeholder
	public static final int IMAGE_QUALITY = 100;
	public static final String PROVIDER_NAME = "SmartMapServers";

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
		setLatitude(latitude);
		setLongitude(longitude);
	}

	@Override
	public long getID() {
		return mId;
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
	public String getEmail() {
		return mEmail;
	}

	@Override
	public Location getLocation() {
		return mLocation;
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
	public void setEmail(String newEmail) {
		mEmail = newEmail;
	}

	@Override
	public void setLocation(Location p) {
		mLocation.setLatitude(p.getLatitude());
		mLocation.setLongitude(p.getLongitude());
	}

	@Override
	public String getPositionName() {
		return mPositionName;
	}

	@Override
	public void setPositionName(String posName) {
		mPositionName = posName;
	}

	@Override
	public void setLatitude(double latitude) {
		mLocation.setLatitude(latitude);

	}

	@Override
	public void setLongitude(double longitude) {
		mLocation.setLongitude(longitude);

	}
	
	@Override
	public LatLng getLatLng() {
	    return new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
	}

	@Override
	public Bitmap getPicture(Context context) {

		File file = new File(context.getFilesDir(), mId + ".png");

		Bitmap pic = null;

		if (file.exists()) {
			pic = BitmapFactory.decodeFile(file.getAbsolutePath());
		} else {
			pic = BitmapFactory.decodeResource(context.getResources(),
					DEFAULT_PICTURE); // placeholder
		}
		return pic;
	}

	@Override
	public void setPicture(Bitmap pic, Context context) {

		File file = new File(context.getFilesDir(), mId + ".png");

		if (file.exists()) {
			file.delete();
		}

		try {
			FileOutputStream out = context.openFileOutput(mId + ".png",
					Context.MODE_PRIVATE);
			pic.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public GregorianCalendar getLastSeen() {
		return mLastSeen;
	}

	@Override
	public boolean isOnline() {
		return mOnline;
	}

	@Override
	public void setLastSeen(GregorianCalendar date) {
		mLastSeen.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
				date.get(Calendar.DATE), date.get(Calendar.HOUR),
				date.get(Calendar.MINUTE));
	}

	@Override
	public void setOnline(boolean status) {
		mOnline = status;
	}

	@Override
	public void deletePicture(Context context) {
		File file = new File(context.getFilesDir(), mId + ".png");
		if (file.exists()) {
			file.delete();
		}
	}

    @Override
    public boolean isVisible() {
        return mVisible;
    }

    @Override
    public void setVisible(boolean isVisible) {
        mVisible = isVisible;
    }

}