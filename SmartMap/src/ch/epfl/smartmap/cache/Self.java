package ch.epfl.smartmap.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import ch.epfl.smartmap.background.ServiceContainer;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author jfperren
 */
public class Self extends User {

    /**
     * Constructor. Constructs a new {@code User} based on info from the {@code SettingsManager}
     * 
     * @param image
     *            The user's profile picture
     */
    public Self(Bitmap image) {
        super(ServiceContainer.getSettingsManager().getUserId(), ServiceContainer.getSettingsManager()
            .getUserName(), image);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.UserInterface#getBlockStatus()
     */
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
        return User.SELF;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getLatLng()
     */
    @Override
    public LatLng getLatLng() {
        return new LatLng(ServiceContainer.getSettingsManager().getLocation().getLatitude(), ServiceContainer
            .getSettingsManager().getLocation().getLongitude());
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getLocation()
     */
    @Override
    public Location getLocation() {
        return ServiceContainer.getSettingsManager().getLocation();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getLocationString()
     */
    @Override
    public String getLocationString() {
        return ServiceContainer.getSettingsManager().getLocationName();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getMarkerIcon(android.content.Context)
     */
    @Override
    public BitmapDescriptor getMarkerIcon(Context context) {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getSubtitle()
     */
    @Override
    public String getSubtitle() {
        return "You are near " + ServiceContainer.getSettingsManager().getLocationName();
    }
}
