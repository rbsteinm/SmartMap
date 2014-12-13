package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;

/**
 * Represents an User on the application SmartMap.
 * 
 * @author jfperren
 */
public abstract class User implements UserInterface {

    // Default values
    public static final long NO_ID = -1;
    public static final String NO_NAME = "Unknown User";
    public static final Bitmap NO_IMAGE = BitmapFactory.decodeResource(ServiceContainer.getSettingsManager()
        .getContext().getResources(), R.drawable.ic_default_user);
    public static final String NO_PHONE_NUMBER = "No phone Number";
    public static final String NO_EMAIL = "No email";
    public static final Calendar NO_LAST_SEEN = GregorianCalendar.getInstance();
    public static final BlockStatus NO_BLOCK_STATUS = BlockStatus.BLOCKED;

    // Default User
    public static final User NOBODY = new Stranger(NO_ID, NO_NAME, NO_IMAGE);

    // Possible type of friendship
    public static final int STRANGER = 0;
    public static final int FRIEND = 1;
    public static final int SELF = 2;
    public static final int DONT_KNOW = -1;

    private final long mId;
    private String mName;
    private Bitmap mImage;

    /**
     * Constructor
     * 
     * @param id
     *            User's id
     * @param name
     *            User's name
     * @param image
     *            User's profile picture
     */
    protected User(long id, String name, Bitmap image) {
        mId = (id >= 0) ? id : User.NO_ID;
        mName = (name != null) ? name : User.NO_NAME;
        mImage = (image != null) ? Bitmap.createBitmap(image) : User.NO_IMAGE;

        if ((mId != User.NO_ID) && ((mName == User.NO_NAME) || (mImage == User.NO_IMAGE))) {
            // If missing informations, tell cache to ask the client
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
     * @see ch.epfl.smartmap.cache.User#getPicture(android.content.Context)
     */
    @Override
    public Bitmap getActionImage() {
        return mImage;
    }

    @Override
    public UserContainer getContainerCopy() {
        return new UserContainer(mId, mName, null, null, null, null, mImage, User.BlockStatus.UNBLOCKED,
            this.getFriendship());
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
        return this.getActionImage();
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
        boolean hasChanged = false;

        if (user.getId() != mId) {
            throw new IllegalArgumentException("Trying to update wrong user !");
        }

        if ((user.getName() != null) && (user.getName() != User.NO_NAME) && !user.getName().equals(mName)) {
            mName = user.getName();
            hasChanged = true;
        }
        if ((user.getImage() != null) && (user.getImage() != User.NO_IMAGE)
            && !user.getImage().sameAs(mImage)) {
            mImage = user.getImage();
            hasChanged = true;
        }

        return hasChanged;
    }

    /**
     * Does the conversion container -> live instance. DO NOT CALL THIS METHOD OUTSIDE CACHE.
     * 
     * @param userContainer
     *            a Container that has all informations about the {@code User} you want to create.
     * @return the {@code User} live instance.
     */
    public static User createFromContainer(UserContainer userContainer) {
        switch (userContainer.getFriendship()) {
            case User.FRIEND:
                return new Friend(userContainer.getId(), userContainer.getName(),
                    userContainer.getPhoneNumber(), userContainer.getEmail(), userContainer.getImage(),
                    userContainer.getLocation(), userContainer.getLocationString(), userContainer.isBlocked());
            case User.STRANGER:
                return new Stranger(userContainer.getId(), userContainer.getName(), userContainer.getImage());
            case User.SELF:
                return new Self();
            default:
                throw new IllegalArgumentException("Unknown type of user");
        }
    }

    /**
     * Possible Block values
     * 
     * @author rbsteinm
     */
    public enum BlockStatus {
        BLOCKED,
        UNBLOCKED,
        NOT_SET
    }
}
