package ch.epfl.smartmap.util;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
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
 * @author rbsteinm
 */
public class Utils {

    public static final long ONE_SECOND = 1000;
    public static final long ONE_MINUTE = 60 * ONE_SECOND;
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;
    public static final long TEN_DAYS = 10 * ONE_DAY;
    public static final long ONE_YEAR = 365 * ONE_DAY;
    public static final int DAYS_IN_A_WEEK = 7;
    private static final float MAX_COLOR = 255f;
    private static final double ONE_THOUSAND_METERS = 1000.0;
    private static final double TEN = 10.0;

    public static final String NEVER_SEEN = ServiceContainer.getSettingsManager().getContext()
            .getString(R.string.utils_never_seen_on_smartmap);
    private static final String TAG = Utils.class.getSimpleName();

    /**
     * 
     * Private onstructor so that Utils cannot be instantiated
     * 
     */
    private Utils() {
        super();
    }

    public static double distanceToMe(LatLng latLng) {
        return Math.sqrt(Math.pow(latLng.latitude - ServiceContainer.getSettingsManager().getLocation().getLatitude(),
                2) + Math.pow(latLng.longitude, ServiceContainer.getSettingsManager().getLocation().getLongitude()));
    }

    public static double distanceToMe(Location location) {
        return ServiceContainer.getSettingsManager().getLocation().distanceTo(location);
    }

    public static String getCityFromLocation(Location location) {
        if (location == null) {
            return Displayable.NO_LOCATION_STRING;
        }

        Geocoder geocoder = new Geocoder(ServiceContainer.getSettingsManager().getContext(), Locale.getDefault());

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
            Log.e(TAG, "Network error: " + e);
            return Displayable.NO_LOCATION_STRING;
        }
    }

    public static int getColorInInterval(double value, double startValue, double endValue, int startColor, int endColor) {
        if (startValue > endValue) {
            return getColorInInterval(value, endValue, startValue, endColor, startColor);
        } else {
            if ((startValue < value) && (value < endValue)) {
                // Compute a mix of the two colors
                double intervalLength = endValue - startValue;
                double percentageStart = (startValue - value) / intervalLength;
                double percentageEnd = (value - endValue) / intervalLength;

                int red = (int) ((percentageStart * Color.red(startColor)) + (percentageEnd * Color.red(endColor)));
                int green = (int) ((percentageStart * Color.green(startColor)) + (percentageEnd * Color.green(endColor)));
                int blue = (int) ((percentageStart * Color.blue(startColor)) + (percentageEnd * Color.blue(endColor)));

                return Color.rgb(red, green, blue);
            } else if (value < startValue) {
                return startColor;
            } else {
                return endColor;
            }
        }
    }

    public static String getCountryFromLocation(Location location) {
        if (location == null) {
            return Displayable.NO_LOCATION_STRING;
        }

        Geocoder geocoder = new Geocoder(ServiceContainer.getSettingsManager().getContext(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty() && (addresses.get(0).getCountryName() != null)) {
                return addresses.get(0).getCountryName();
            } else {
                return Displayable.NO_LOCATION_STRING;
            }
        } catch (IOException e) {
            Log.e(TAG, "Network error: " + e);
            return Displayable.NO_LOCATION_STRING;
        }
    }

    public static String getDateString(Calendar calendar) {

        Calendar now = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
        int yearsDiff = calendar.get(Calendar.YEAR) - now.get(Calendar.YEAR);
        int daysDiff = calendar.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR);

        if (yearsDiff == 0) {
            if (daysDiff > DAYS_IN_A_WEEK) {
                return calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH) + "."
                        + calendar.get(Calendar.YEAR);
            } else if (daysDiff > 1) {
                return ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_next) + " "
                        + calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
            } else if (daysDiff == 1) {
                return ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_tomorrow);
            } else if (daysDiff == 0) {
                return ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_today);
            } else if (daysDiff == -1) {
                return ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_yesterday);
            } else if (daysDiff > -DAYS_IN_A_WEEK) {
                return ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_last) + " "
                        + calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
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
            return ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_now);
        } else if (diff < ONE_HOUR) {
            // Give time in minutes
            int minutes = (int) (diff / ONE_MINUTE);
            if (minutes == 1) {
                return ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_one_min);
            } else {
                return minutes + " "
                        + ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_minutes_ago);
            }
        } else if (diff < ONE_DAY) {
            // Give time hours
            int hours = (int) (diff / ONE_HOUR);
            if (hours == 1) {
                return ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_one_hour_ago);
            } else {
                return "" + hours + " "
                        + ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_hours_ago);
            }
        } else if (diff < ONE_YEAR) {
            // Give time in days
            int days = (int) (diff / ONE_DAY);
            if (days == 1) {
                return ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_yesterday);
            } else {
                return "" + days + " "
                        + ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_days_ago);
            }
        } else {
            return ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_never_seen_on_smartmap);
        }
    }

    public static ColorMatrix getMatrixForColor(int color) {
        float r = Color.red(color) / MAX_COLOR;
        float g = Color.green(color) / MAX_COLOR;
        float b = Color.blue(color) / MAX_COLOR;

        float[] src = { r, 0, 0, 0, 0, 0, g, 0, 0, 0, 0, 0, b, 0, 0, 0, 0, 0, 1, 0 };
        return new ColorMatrix(src);
    }

    public static String getTimeString(Calendar calendar) {
        return formatForClock(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + formatForClock(calendar.get(Calendar.MINUTE));
    }

    /**
     * @param location
     *            point you want to have the distance to
     * @return a String telling how far you are from location
     */
    public static String printDistanceToMe(Location location) {

        double distance = distanceToMe(location);
        String textDistance = "";
        if (distance >= ONE_THOUSAND_METERS) {
            distance = distance / ONE_THOUSAND_METERS;
            distance = Math.round(distance * TEN) / TEN;
            textDistance = distance + " km "
                    + ServiceContainer.getSettingsManager().getContext().getString(R.string.utils_away_from_you);
        } else {
            distance = Math.round(distance);
            textDistance = ((int) distance) + " meters away from you";
        }
        return textDistance;
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
        if (time < TEN) {
            hourOfDayString += "0" + time;
        } else {
            hourOfDayString += time;
        }

        return hourOfDayString;
    }
}