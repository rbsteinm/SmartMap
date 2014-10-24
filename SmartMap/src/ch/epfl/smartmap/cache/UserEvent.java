package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * An event that can be seen on the map
 * @author ritterni
 */
public class UserEvent implements Event {
    private String evtName;
    private int evtCreator; //the user who created the event
    private GregorianCalendar mStartDate;
    private GregorianCalendar mEndDate;
    private int evtID;
    private Point position;
    
    public UserEvent(String name, int creator, GregorianCalendar startDate, GregorianCalendar endDate, Point p) {
        evtName = name;
        evtCreator = creator;
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
        
        position.setX(p.getX());
        position.setY(p.getY());
    }
    
    @Override
    public String getName() {
        return evtName;
    }
    
    @Override
    public int getCreator() {
        return evtCreator;
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
        evtName = newName;
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
    public int getID() {
        return evtID;
    }

    @Override
    public void setID(int newID) {
        evtID = newID;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public void setPosition(Point p) {
        position.setX(p.getX());
        position.setY(p.getY());
    }

    @Override
    public void setX(double x) {
        position.setX(x);
    }

    @Override
    public void setY(double y) {
        position.setY(y);
    }
}
