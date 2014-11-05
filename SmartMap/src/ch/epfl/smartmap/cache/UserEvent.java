package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;

/**
 * An event that can be seen on the map
 * @author ritterni
 */
public class UserEvent implements Event {
    private String mEvtName;
    private long mEvtCreator; //the user who created the event
    private GregorianCalendar mStartDate;
    private GregorianCalendar mEndDate;
    private long evtID;
    private Location mLocation;
    private String mPositionName;
    
    public UserEvent(String name, long creator, GregorianCalendar startDate, GregorianCalendar endDate, Location p) {
        mEvtName = name;
        mEvtCreator = creator;
        mStartDate = new GregorianCalendar(startDate.get(Calendar.YEAR),
                startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DATE),
                startDate.get(Calendar.HOUR),
                startDate.get(Calendar.MINUTE));
        
        mEndDate = new GregorianCalendar(endDate.get(Calendar.YEAR),
                endDate.get(Calendar.MONTH),
                endDate.get(Calendar.DATE),
                endDate.get(Calendar.HOUR),
                endDate.get(Calendar.MINUTE));
        
        mLocation = new Location("");
        mLocation.setLatitude(p.getLatitude());
        mLocation.setLongitude(p.getLongitude());
        mPositionName = "";
    }
    
    @Override
    public String getName() {
        return mEvtName;
    }
    
    @Override
    public long getCreator() {
        return mEvtCreator;
    }
    
    @Override
    public GregorianCalendar getStartDate() {
        return mStartDate;
    }
    
    @Override
    public GregorianCalendar getEndDate() {
        return mEndDate;
    }
    
    @Override
    public void setName(String newName) {
        mEvtName = newName;
    }
    
    @Override
    public void setStartDate(GregorianCalendar newDate) {
        mStartDate.set(newDate.get(Calendar.YEAR),
                newDate.get(Calendar.MONTH),
                newDate.get(Calendar.DATE),
                newDate.get(Calendar.HOUR),
                newDate.get(Calendar.MINUTE));
    }
    
    @Override
    public void setEndDate(GregorianCalendar newDate) {
        mEndDate.set(newDate.get(Calendar.YEAR),
                newDate.get(Calendar.MONTH),
                newDate.get(Calendar.DATE),
                newDate.get(Calendar.HOUR),
                newDate.get(Calendar.MINUTE));
    }

    @Override
    public long getID() {
        return evtID;
    }

    @Override
    public void setID(long newID) {
        evtID = newID;
    }

    @Override
    public Location getLocation() {
        return mLocation;
    }

    @Override
    public void setLocation(Location p) {
    	mLocation.setLatitude(p.getLatitude());
        mLocation.setLongitude(p.getLongitude());
    }

	@Override
	public LatLng getLatLng() {
		return new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
	}

	@Override
	public void setLongitude(double x) {
		mLocation.setLongitude(x);
		
	}

	@Override
	public void setLatitude(double y) {
		mLocation.setLatitude(y);
	}

    @Override
    public String getPositionName() {
        return mPositionName;
    }

    @Override
    public void setPositionName(String posName) {
        mPositionName = posName;
    }
}
