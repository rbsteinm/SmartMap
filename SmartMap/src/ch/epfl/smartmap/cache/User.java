package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;

/**
 * @author jfperren
 */
public abstract class User implements UserInterface {

    private static final String TAG = User.class.getSimpleName();

    public static final int STRANGER = 0;
    public static final int FRIEND = 1;
    public static final int SELF = 2;
    public static final int DONT_KNOW = -1;

    public static final long NO_ID = -1;
    public static final String NO_NAME = "Unknown User";
    public static final Bitmap NO_IMAGE = BitmapFactory.decodeResource(ServiceContainer.getSettingsManager()
        .getContext().getResources(), R.drawable.ic_default_user);

    public static final String NO_PHONE_NUMBER = "No phone Number";
    public static final String NO_EMAIL = "No email";
    public static final Calendar NO_LAST_SEEN = GregorianCalendar.getInstance();

    public static final User NOBODY = new Stranger(NO_ID, NO_NAME, NO_IMAGE);

    public static final int IMAGE_QUALITY = 100;;

    public static final long ONLINE_TIMEOUT = 1000 * 60 * 3; // time in millis
    public static final int PICTURE_WIDTH = 50;
    public static final int PICTURE_HEIGHT = 50;
    public static final double NO_LATITUDE = 0.0;
    public static double NO_LONGITUDE = 0.0;
    private final long mId;

    private String mName;
    private Bitmap mImage;

    protected User(long id, String name, Bitmap image) {
        mId = (id >= 0) ? id : User.NO_ID;
        mName = (name != null) ? name : User.NO_NAME;
        mImage = (image != null) ? Bitmap.createBitmap(image) : User.NO_IMAGE;

        Log.d(TAG, "Create new User Instance(" + id + ", " + "name, " + "image " + mImage);

        if ((mId != User.NO_ID) && ((mName == User.NO_NAME) || (mImage == User.NO_IMAGE))) {
            ServiceContainer.getCache().updateUserInfos(id);
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object that) {
        return (that != null) && (that instanceof User) && (mId == ((User) that).getId());
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getID()
     */
    @Override
    public long getId() {
        return mId;
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
    public UserContainer getImmutableCopy() {
        return new UserContainer(mId, mName, null, null, null, null, mImage, User.blockStatus.UNBLOCKED,
            this.getFriendship());
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
     * @see ch.epfl.smartmap.cache.Displayable#getSearchImage()
     */
    @Override
    public Bitmap getSearchImage() {
        return this.getImage();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getTitle()
     */
    @Override
    public String getTitle() {
        return this.getName();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashcode
     */
    @Override
    public int hashCode() {
        return (int) mId;
    }

    @Override
    public boolean update(UserContainer user) {
        // TODO : Update hasChanged to work correctly
        boolean hasChanged = false;

        if (user.getId() == 12) {
            Log.d("USER", "Update matt with new name " + user.getName());
        }
        if ((user.getName() != null) && (user.getName() != User.NO_NAME)) {
            mName = user.getName();
            hasChanged = true;
        }
        if ((user.getImage() != null) && (user.getImage() != User.NO_IMAGE)) {
            mImage = user.getImage();
            hasChanged = true;
        }

        return hasChanged;
    }

    public static User createFromContainer(UserContainer userInfos) {
        switch (userInfos.getFriendship()) {
            case User.FRIEND:
                return new Friend(userInfos.getId(), userInfos.getName(), userInfos.getImage(),
                    userInfos.getLocation(), userInfos.getLocationString(), userInfos.isBlocked());
            case User.STRANGER:
                return new Stranger(userInfos.getId(), userInfos.getName(), userInfos.getImage());
            case User.SELF:
                return new Self();
            default:
                throw new IllegalArgumentException("Unknown type of user");
        }
    }

    public enum blockStatus {
        BLOCKED,
        UNBLOCKED,
        NOT_SET
    }
}
