package ch.epfl.smartmap.cache;

import android.content.Intent;
import android.graphics.Bitmap;
import ch.epfl.smartmap.R;

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
    public long getId() {
        return mId;
    }

    @Override
    public Bitmap getImage() {
        return mUser.getImage();
    }

    @Override
    public Intent getIntent() {
        return mIntent;
    }

    @Override
    public String getSubtitle() {
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