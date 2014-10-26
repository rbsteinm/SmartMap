package ch.epfl.smartmap.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import ch.epfl.smartmap.R;

/**
 * A class to represent the user's friends
 * 
 * @author ritterni
 */
public class Friend implements User {

    private long id; // the user's unique ID
    private String name; // the user's name as it will be displayed
    private String phoneNumber;
    private String email;
    private Point position;
    private String positionName;
    private GregorianCalendar lastSeen;
    private boolean online;

    public static final String NO_NUMBER = "No phone number specified";
    public static final String NO_EMAIL = "No email address specified";
    public static final String POSITION_UNKNOWN = "Unknown position";
    public static final int DEFAULT_PICTURE = R.drawable.ic_launcher; // placeholder
    public static final int IMAGE_QUALITY = 100;

    /**
     * Friend constructor
     * 
     * @param userID
     *            The id of the contact we're creating
     * @param userName
     *            The name of the friend
     * @param userNumber
     *            The friend's phone number
     * @author ritterni
     */
    public Friend(long userID, String userName) {
        id = userID;
        name = userName;
        phoneNumber = NO_NUMBER;
        email = NO_EMAIL;
        position = new Point(0, 0); // needs to be updated by the server
        positionName = POSITION_UNKNOWN;
        lastSeen = new GregorianCalendar();
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNumber() {
        return phoneNumber;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public void setName(String newName) {
        name = newName;
    }

    @Override
    public void setNumber(String newNumber) {
        phoneNumber = newNumber;
    }

    @Override
    public void setEmail(String newEmail) {
        email = newEmail;
    }

    @Override
    public void setX(double x) {
        position.setX(x);

    }

    @Override
    public void setY(double y) {
        position.setY(y);
    }

    @Override
    public void setPosition(Point p) {
        position.setX(p.getX());
        position.setY(p.getY());
    }

    @Override
    public String getPositionName() {
        return positionName;
    }

    @Override
    public void setPositionName(String posName) {
        positionName = posName;
    }

    @Override
    public Bitmap getPicture(Context context) {

        File file = new File(context.getFilesDir(), id + ".png");

        Bitmap pic = null;

        if (file.exists()) {
            pic = BitmapFactory.decodeFile(file.getAbsolutePath());
        } else {
            pic = BitmapFactory.decodeResource(context.getResources(), DEFAULT_PICTURE); // placeholder
        }
        return pic;
    }

    @Override
    public void setPicture(Bitmap pic, Context context) {

        File file = new File(context.getFilesDir(), id + ".png");

        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream out = context.openFileOutput(id + ".png", Context.MODE_PRIVATE);
            pic.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public GregorianCalendar getLastSeen() {
        return lastSeen;
    }

    @Override
    public boolean isOnline() {
        return online;
    }

    @Override
    public void setLastSeen(GregorianCalendar date) {
        lastSeen.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE),
                date.get(Calendar.HOUR), date.get(Calendar.MINUTE));
    }

    @Override
    public void setOnline(boolean status) {
        online = status;
    }

    @Override
    public void deletePicture(Context context) {
        File file = new File(context.getFilesDir(), id + ".png");
        if (file.exists()) {
            file.delete();
        }
    }
}