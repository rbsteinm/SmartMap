package ch.epfl.smartmap.cache;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import ch.epfl.smartmap.R;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A class to represent the user's friends
 * 
 * @author ritterni
 */
public class FriendInvitation implements Invitation, Displayable {
    private long mId;
    private Intent mIntent;
    private String mTitle;
    private String mText;
    private User mUser;
    private boolean mIsRead;
    public static final int DEFAULT_PICTURE = R.drawable.ic_default_user; // placeholder
    public static final int IMAGE_QUALITY = 100;
    public static final String PROVIDER_NAME = "SmartMapServers";

    @Override
    public long getID() {
        return mId;
    }

    @Override
    public Intent getIntent() {
        return mIntent;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getLatLng()
     */
    @Override
    public LatLng getLatLng() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Location getLocation() {
        return mUser.getLocation();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getMarkerOptions(android.content.Context)
     */
    @Override
    public MarkerOptions getMarkerOptions(Context context) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return mUser.getName();
    }

    @Override
    public Bitmap getImage(Context context) {
        return mUser.getImage(context);
    }

    @Override
    public String getShortInfos() {
        return new String("Position : " + mUser.getLocationString() + "\n" + "Last seen : "
            + mUser.getLastSeen());
    }

    @Override
    public String getText() {
        return mText;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public User getUser() {
        return mUser;
    }

    @Override
    public boolean isRead() {
        return mIsRead;
    }

    @Override
    public void setIntent(Intent intent) {
        mIntent = intent;

    }

    @Override
    public void setRead(boolean isRead) {
        mIsRead = isRead;

    }

    @Override
    public void setText(String text) {
        mText = text;
    }

    @Override
    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public void setUser(User user) {
        mUser = user;
    }

}