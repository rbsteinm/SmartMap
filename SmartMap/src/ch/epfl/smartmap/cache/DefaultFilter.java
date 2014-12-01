package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Bitmap;
import android.location.Location;
import android.util.LongSparseArray;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Describes a clientside, custom friend list (e.g. friends, family, etc.)
 * 
 * @author ritterni
 */
public class DefaultFilter implements Filter {

	private final List<Long> idList; // list of the friends' IDs
	private String listName;
	private long databaseID;

	/**
	 * @param name
	 *            The name of the friend list
	 * @param friendsDatabase
	 *            Whole database of friends referenced by the friendlist
	 */
	public DefaultFilter(String name) {
		// The friendlist needs a reference to the whole friend database in
		// order to find users from their ID
		listName = name;
		idList = new ArrayList<Long>();
	}

	@Override
	public void addUser(long id) {
		if (!idList.contains(id)) {
			idList.add(id);
		}
	}

	@Override
	public long getId() {
		return databaseID;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.epfl.smartmap.cache.Displayable#getImage()
	 */
	@Override
	public Bitmap getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.epfl.smartmap.cache.Displayable#getLatLng()
	 */
	@Override
	public LatLng getLatLng() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> getList() {
		return new ArrayList<Long>(idList);
	}

	@Override
	public String getListName() {
		return listName;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.epfl.smartmap.cache.Displayable#getLocation()
	 */
	@Override
	public Location getLocation() {
		return NO_LOCATION;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.epfl.smartmap.cache.Displayable#getLocationString()
	 */
	@Override
	public String getLocationString() {
		return NO_LOCATION_STRING;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.epfl.smartmap.cache.Displayable#getMarkerOptions()
	 */
	@Override
	public MarkerOptions getMarkerOptions() {
		return NO_MARKER_OPTIONS;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.epfl.smartmap.cache.Displayable#getSubtitle()
	 */
	@Override
	public String getSubtitle() {
		return "this is a default filter";
	}

	/*
	 * (non-Javadoc)
	 * @see ch.epfl.smartmap.cache.Displayable#getTitle()
	 */
	@Override
	public String getTitle() {
		return "Filter";
	}

	@Override
	public List<User> getUserList(LongSparseArray<User> friends) {
		List<User> uList = new ArrayList<User>();
		for (long id : idList) {
			uList.add(friends.get(id));
		}

		Collections.sort(uList, new UserComparator());

		return uList;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.epfl.smartmap.cache.Displayable#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public void removeUser(long id) {
		idList.remove(id);
	}

	@Override
	public void setID(long newID) {
		databaseID = newID;
	}

	@Override
	public void setListName(String newName) {
		listName = newName;
	}

}
