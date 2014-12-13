package ch.epfl.smartmap.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

/**
 * Represents an online User which is not a Friend, therefore allowing less
 * informations. Every instanc is
 * unique and store in a local cache that can be accessed via static methods on
 * this class.
 * 
 * @author jfperren
 */
public class Stranger extends User {

    protected Stranger(long id, String name, Bitmap image) {
        super(id, name, image);
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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

    @Override
    public User.BlockStatus getBlockStatus() {
        return User.BlockStatus.UNBLOCKED;
    }
}
