package ch.epfl.smartmap.cache;

import android.graphics.Bitmap;

/**
 * @author jfperren
 */
public abstract class User implements UserInterface {

    private final long mId;
    private String mName;
    private Bitmap mImage;

    protected User(long id, String name, Bitmap image) {
        mId = (id >= 0) ? id : UserInterface.NO_ID;
        mName = (name != null) ? name : UserInterface.NO_NAME;
        mImage = (image != null) ? Bitmap.createBitmap(image) : UserInterface.NO_IMAGE;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object that) {
        return (that != null) && (that instanceof UserInterface) && (mId == ((UserInterface) that).getId());
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
        return new ImmutableUser(mId, mName, null, null, null, null, mImage, false, this.getFriendship());
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

    @Override
    public void update(ImmutableUser user) {
        if (user.getName() != null) {
            mName = user.getName();
        }
        if (user.getImage() != null) {
            mImage = user.getImage();
        }
    }

    public static UserInterface createFromContainer(ImmutableUser userInfos) {
        switch (userInfos.getFriendship()) {
            case UserInterface.FRIEND:
                return new Friend(userInfos.getId(), userInfos.getName(), userInfos.getImage(),
                    userInfos.getLocation(), userInfos.getLocationString(), userInfos.isBlocked());
            case UserInterface.STRANGER:
                return new Stranger(userInfos.getId(), userInfos.getName(), userInfos.getImage());
            case UserInterface.SELF:
                return new Self();
            default:
                throw new IllegalArgumentException("Unknown type of user");
        }
    }
}
