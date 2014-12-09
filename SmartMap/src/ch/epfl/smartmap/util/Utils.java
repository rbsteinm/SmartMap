package ch.epfl.smartmap.util;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.gui.BadgeDrawable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class that count the redraw badge on app icon
 * 
 * @author agpmilli
 * @author jfperren
 * @author SpicyCH
 */
public class Utils {

    public static final long ONE_SECOND = 1000;
    public static final long ONE_MINUTE = 60 * ONE_SECOND;
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;
    public static final long TEN_DAYS = 10 * ONE_DAY;
    public static final long ONE_YEAR = 365 * ONE_DAY;

    public static final String NEVER_SEEN = "Never seen on SmartMap";
    private static Context mContext;

    public static double distanceToMe(LatLng latLng) {
        return Math.sqrt(Math.pow(latLng.latitude - ServiceContainer.getSettingsManager().getLocation().getLatitude(),
                2) + Math.pow(latLng.longitude, ServiceContainer.getSettingsManager().getLocation().getLongitude()));
    }

    public static String getCityFromLocation(Location location) {
        if (location == null) {
            return Displayable.NO_LOCATION_STRING;
        }

        mContext = ServiceContainer.getSettingsManager().getContext();
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty() && (addresses.get(0).getLocality() != null)) {
                return addresses.get(0).getLocality();
            } else if (!addresses.isEmpty() && (addresses.get(0).getCountryName() != null)) {
                return addresses.get(0).getCountryName();
            } else {
                return Displayable.NO_LOCATION_STRING;
            }
        } catch (IOException e) {
            return Displayable.NO_LOCATION_STRING;
        }
    }

    public static String getCountryFromLocation(Location location) {
        if (location == null) {
            return Displayable.NO_LOCATION_STRING;
        }

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty() && (addresses.get(0).getCountryName() != null)) {
                return addresses.get(0).getCountryName();
            } else {
                return Displayable.NO_LOCATION_STRING;
            }
        } catch (IOException e) {
            return Displayable.NO_LOCATION_STRING;
        }
    }

    public static String getDateString(Calendar calendar) {

        Calendar now = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
        int yearsDiff = calendar.get(Calendar.YEAR) - now.get(Calendar.YEAR);
        int daysDiff = calendar.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR);

        if (yearsDiff == 0) {
            if (daysDiff > 7) {
                return calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH) + "."
                        + calendar.get(Calendar.YEAR);
            } else if (daysDiff > 1) {
                return "Next " + calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
            } else if (daysDiff == 1) {
                return "Tomorrow";
            } else if (daysDiff == 0) {
                return "Today";
            } else if (daysDiff == -1) {
                return "Yesterday";
            } else if (daysDiff > -7) {
                return "Last " + calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
            } else {
                return calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH) + "."
                        + calendar.get(Calendar.YEAR);
            }
        } else {
            return formatForClock(calendar.get(Calendar.DAY_OF_MONTH)) + "."
                    + formatForClock(calendar.get(Calendar.MONTH)) + "." + calendar.get(Calendar.YEAR);
        }
    }

    public static String getLastSeenStringFromCalendar(Calendar calendar) {

        long diff = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00")).getTimeInMillis()
                - calendar.getTimeInMillis();

        if (diff < ONE_MINUTE) {
            // Give time in seconds
            return "Now";
        } else if (diff < ONE_HOUR) {
            // Give time in minutes
            int minutes = (int) (diff / ONE_MINUTE);
            if (minutes == 1) {
                return "1 minute ago";
            } else {
                return "" + minutes + " minutes ago";
            }
        } else if (diff < ONE_DAY) {
            // Give time hours
            int hours = (int) (diff / ONE_HOUR);
            if (hours == 1) {
                return "1 hour ago";
            } else {
                return "" + hours + " hours ago";
            }
        } else if (diff < ONE_YEAR) {
            // Give time in days
            int days = (int) (diff / ONE_DAY);
            if (days == 1) {
                return "Yesterday";
            } else {
                return "" + days + " days ago";
            }
        } else {
            return "Never seen on SmartMap";
        }
    }

    public static String getTimeString(Calendar calendar) {
        return formatForClock(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + formatForClock(calendar.get(Calendar.MINUTE));
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if ((reuse != null) && (reuse instanceof BadgeDrawable)) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    /**
     * @param time
     *            a second, minute, hour, day or month
     * @return the time prefixed with 0 if it was < 10
     * @author SpicyCH
     */
    private static String formatForClock(int time) {
        String hourOfDayString = "";
        if (time < 10) {
            hourOfDayString += "0" + time;
        } else {
            hourOfDayString += time;
        }

        return hourOfDayString;
    }
}