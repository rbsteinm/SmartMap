package ch.epfl.smartmap.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import ch.epfl.smartmap.listeners.OnDisplayableUpdateListener;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Represents an online User which is not a Friend, therefore allowing less informations. Every instanc is
 * unique and store in a local cache that can be accessed via static methods on this class.
 * 
 * @author jfperren
 */
public class Stranger implements User {

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#addOnDisplayableUpdateListener(ch.epfl.smartmap.listeners.
     * OnDisplayableUpdateListener)
     */
    @Override
    public void addOnDisplayableUpdateListener(OnDisplayableUpdateListener listener) {
        // TODO Implement this method
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#deletePicture(android.content.Context)
     */
    @Override
    public void deletePicture(Context context) {
        // TODO Implement this method
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getEmail()
     */
    @Override
    public String getEmail() {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getLastSeen()
     */
    @Override
    public Calendar getLastSeen() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getLocationString()
     */
    @Override
    public String getLocationString() {
        // TODO Auto-generated method stub
        return null;
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

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getNumber()
     */
    @Override
    public String getNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getShortInfos()
     */
    @Override
    public String getShortInfos() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#isVisibleOnMap()
     */
    @Override
    public boolean isVisibleOnMap() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#removeOnDisplayableUpdateListener(ch.epfl.smartmap.listeners.
     * OnDisplayableUpdateListener)
     */
    @Override
    public void removeOnDisplayableUpdateListener(OnDisplayableUpdateListener listener) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setEmail(java.lang.String)
     */
    @Override
    public void setEmail(String newEmail) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setImage(android.graphics.Bitmap, android.content.Context)
     */
    @Override
    public void setImage(Bitmap newImage, Context context) throws FileNotFoundException, IOException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setLastSeen(java.util.Date)
     */
    @Override
    public void setLastSeen(Date newDate) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setLatitude(double)
     */
    @Override
    public void setLatitude(double newLatitude) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setLocation(android.location.Location)
     */
    @Override
    public void setLocation(Location newLocation) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setLongitude(double)
     */
    @Override
    public void setLongitude(double newLongitude) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setName(java.lang.String)
     */
    @Override
    public void setName(String newName) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#setPhoneNumber(java.lang.String)
     */
    @Override
    public void setPhoneNumber(String newNumber) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getID()
     */
    @Override
    long getID() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getImage(android.content.Context)
     */
    @Override
    Bitmap getImage(Context context) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getLatLng()
     */
    @Override
    LatLng getLatLng() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getLocation()
     */
    @Override
    Location getLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.User#getName()
     */
    @Override
    String getName() {
        // TODO Auto-generated method stub
        return null;
    }
}
