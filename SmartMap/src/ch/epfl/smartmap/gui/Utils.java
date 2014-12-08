package ch.epfl.smartmap.gui;

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

import com.google.android.gms.maps.model.LatLng;

/**
 * Class that count the redraw badge on app icon
 * 
 * @author agpmilli
 */
public class Utils {

    public static Context sContext;
    public static final long ONE_SECOND = 1000;
    public static final long ONE_MINUTE = 60 * ONE_SECOND;
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;
    public static final long TEN_DAYS = 10 * ONE_DAY;
    public static final long ONE_YEAR = 365 * ONE_DAY;

    public static final String NEVER_SEEN = "Never seen on SmartMap";

    public static double distanceToMe(LatLng latLng) {
        return Math.sqrt(Math.pow(latLng.latitude
            - ServiceContainer.getSettingsManager().getLocation().getLatitude(), 2)
            + Math.pow(latLng.longitude, ServiceContainer.getSettingsManager().getLocation().getLongitude()));
    }

    public static String getCityFromLocation(Location location) {
        if (location == null) {
            return Displayable.NO_LOCATION_STRING;
        }

        Geocoder geocoder = new Geocoder(sContext, Locale.getDefault());

        try {
            List<Address> addresses =
                geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
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

    public static String getLastSeenStringFromCalendar(Calendar calendar) {

        long diff =
            GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00")).getTimeInMillis()
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
}