package ch.epfl.smartmap.cache;

import android.graphics.Bitmap;

/**
 * @author jfperren
 */
public abstract class AbstractUser implements User {

    private final long mId;
    private String mName;
    private Bitmap mImage;
    private boolean mBlocked;

    protected AbstractUser(ImmutableUser user) {
        mId = (user.getId() >= 0) ? user.getId() : User.NO_ID;
        mName = (user.getName() != null) ? user.getName() : User.NO_NAME;
        mImage = (user.getImage() != null) ? user.getImage() : User.NO_IMAGE;
        mBlocked = user.isBlocked();
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
    public ImmutableUser getImmutableCopy() {
        return new ImmutableUser(mId, mName, null, null, null, null, mImage, mBlocked, this.getFriendship());
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

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#isBlocked()
     */
    @Override
    public boolean isBlocked() {
        return mBlocked;
    }

    @Override
    public void update(ImmutableUser user) {
        if (user.getName() != null) {
            mName = user.getName();
        }
        if (user.getImage() != null) {
            mImage = user.getImage();
        }
    }

    public static User createFromContainer(ImmutableUser userInfos) {
        switch (userInfos.getFriendship()) {
            case User.FRIEND:
                return new Friend(userInfos);
            case User.STRANGER:
                return new Stranger(userInfos);
            case User.SELF:
                return new Self();
            default:
                throw new IllegalArgumentException("Unknown type of user");
        }
    }
}
