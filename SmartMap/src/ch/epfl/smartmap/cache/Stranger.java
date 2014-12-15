package ch.epfl.smartmap.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

/**
 * Represents an {@code User} that is not your friend on SmartMap. You only have
 * access to his id, name and
 * profile picture. You should not instantiate a Stranger directly, but rather
 * use {@code User.createFromContainer( ... )}.
 * 
 * @author jfperren
 */
public class Stranger extends User {

    /**
     * Constructor
     * 
     * @param id
     *            Stranger's id
     * @param name
     *            Stranger's name
     * @param image
     *            Stranger's profile picture
     */
    protected Stranger(long id, String name, Bitmap image) {
        super(id, name, image);
    }

    @Override
    public User.BlockStatus getBlockStatus() {
        return User.BlockStatus.UNBLOCKED;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getFriendship()
     */
    @Override
    public int getFriendship() {
        return User.STRANGER;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Localisable#getLatLng()
     */
    @Override
    public LatLng getLatLng() {
        return new LatLng(User.NO_LATITUDE, User.NO_LONGITUDE);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getLocation()
     */
    @Override
    public Location getLocation() {
        return User.NO_LOCATION;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getLocationString()
     */
    @Override
    public String getLocationString() {
        return User.NO_LOCATION_STRING;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Localisable#getMarkerOptions()
     */
    @Override
    public BitmapDescriptor getMarkerIcon(Context context) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getSubtitle()
     */
    @Override
    public String getSubtitle() {
        return "Add as Friend to see more informations";
    }
}
