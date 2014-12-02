package ch.epfl.smartmap.cache;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Represents an online User which is not a Friend, therefore allowing less informations. Every instanc is
 * unique and store in a local cache that can be accessed via static methods on this class.
 * 
 * @author jfperren
 */
public class Stranger implements User {

    private long mId;
    private String mName;
    private Bitmap mImage;

    protected Stranger(ImmutableUser user) {
        if (user.getId() < 0) {
            throw new IllegalArgumentException();
        } else {
            mId = user.getId();
        }

        if ((user.getName() == null) || user.getName().equals("")) {
            throw new IllegalArgumentException();
        } else {
            mName = user.getName();
        }

        if (user.getImage() == null) {
            mImage = User.NO_IMAGE;
        } else {
            mImage = Bitmap.createBitmap(user.getImage());
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getEmail()
     */
    @Override
    public String getEmail() {
        return NO_EMAIL;
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
     * @see ch.epfl.smartmap.cache.Displayable#getImage()
     */
    @Override
    public Bitmap getImage() {
        return mImage;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getImmutableCopy()
     */
    @Override
    public ImmutableUser getImmutableCopy() {
        // TODO Auto-generated method stub
        return new ImmutableUser(mId, mName, null, null, null, null, mImage);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getLastSeen()
     */
    @Override
    public Calendar getLastSeen() {
        return NO_LAST_SEEN;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Localisable#getLatLng()
     */
    @Override
    public LatLng getLatLng() {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getLocation()
     */
    @Override
    public Location getLocation() {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getLocationString()
     */
    @Override
    public String getLocationString() {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Localisable#getMarkerOptions()
     */
    @Override
    public MarkerOptions getMarkerOptions(Context context) {
        throw new UnsupportedOperationException();
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
    public String getPhoneNumber() {
        return NO_PHONE_NUMBER;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getSubtitle()
     */
    @Override
    public String getSubtitle() {
        return "Add as Friend to see more informations";
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getTitle()
     */
    @Override
    public String getTitle() {
        return mName;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getType()
     */
    @Override
    public Type getType() {
        return Type.STRANGER;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Localisable#isVisible()
     */
    @Override
    public boolean isVisible() {
        return false;
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
}
