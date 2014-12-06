/**
 * 
 */
package ch.epfl.smartmap.map;

import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.User;

/**
 * An implementation of {@link MarkerIconMaker}, that creates a circular icon for a user
 * 
 * @author hugo-S
 */
public class CircularMarkerIconMaker implements MarkerIconMaker {

    private Bitmap mMarkerShape;
    private final Context mContext;
    private final User mUser;
    public static final float CIRCLE_CENTER_INCREMENT = 0.7f;
    public static final float CIRCLE_RADIUS_INCREMENT = 0.1f;
    public static final int SHAPE_BORDER_WIDTH = 230; // 230
    public static final float SCALE_MARKER = 0.15f;
    public static final int SHAPE_TAIL_LENGTH = 73;
    public static final float SATURATION_BEGIN = 2f;
    public static final float SATURATION_END = -0.08f;
    public static final long TIMEOUT_COLOR = 30; // minutes
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int MILLISECONDS_IN_SECOND = 1000;

    public CircularMarkerIconMaker(Context context, User user) {
        mContext = context;
        mUser = user;
        int idForm = R.drawable.marker_forme;
        mMarkerShape = BitmapFactory.decodeResource(mContext.getResources(), idForm);
        mMarkerShape = mMarkerShape.copy(Bitmap.Config.ARGB_8888, true); // make the Bitmap mMarkerShape
                                                                         // mutable to be changed by canvas
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.map.MarkerIconMaker#getMarkerIcon(android.graphics.Bitmap)
     */
    @Override
    public Bitmap getMarkerIcon() {
        Bitmap profilePicture =
            Bitmap.createScaledBitmap(mUser.getImage(), User.PICTURE_WIDTH, User.PICTURE_HEIGHT, false);

        profilePicture =
            Bitmap.createScaledBitmap(profilePicture, mMarkerShape.getWidth() - SHAPE_BORDER_WIDTH,
                mMarkerShape.getWidth() - SHAPE_BORDER_WIDTH, true);

        long timeElapsed =
            GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00")).getTimeInMillis()
                - mUser.getLastSeen().getTimeInMillis();
        this.setColorOfMarkerShape(timeElapsed);

        Bitmap roundProfile = this.cropProfilePicture(profilePicture, profilePicture.getWidth());
        Bitmap finalMarker = this.overlay(mMarkerShape, roundProfile);
        finalMarker = this.scaleMarker(finalMarker, SCALE_MARKER);
        return finalMarker;
    }

    /**
     * This function crops the given image in a circle form with the given radius
     * 
     * @param profPic
     * @param radius
     * @return the cropped image
     */
    private Bitmap cropProfilePicture(Bitmap profPic, int radius) {
        Paint color = new Paint();
        color.setColor(Color.BLACK);
        Bitmap preOut;

        if ((profPic.getWidth() != radius) || (profPic.getHeight() != radius)) {
            preOut = Bitmap.createScaledBitmap(profPic, 2 * radius, 2 * radius, true);
        } else {
            preOut = profPic;
        }

        Bitmap output = Bitmap.createBitmap(preOut.getWidth(), preOut.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, preOut.getWidth(), preOut.getHeight());

        paint.setAntiAlias(true); // AntiAliasing smooths out the edges of what is being drawn
        paint.setFilterBitmap(true); // Filtering affects the sampling of bitmaps when they are transformed.
        paint.setDither(true); // Dithering affects how colors that are higher precision than the device are
                               // down-sampled
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle((preOut.getWidth() / 2) + CIRCLE_CENTER_INCREMENT, (preOut.getHeight() / 2)
            + CIRCLE_CENTER_INCREMENT, (preOut.getWidth() / 2) + CIRCLE_RADIUS_INCREMENT, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); // crop the image depending on the drawn //
                                                                // object
        canvas.drawBitmap(preOut, rect, rect, paint);

        return output;

    }

    /**
     * this function combines the center of the second image on the center of the first image
     * 
     * @param bmp1
     * @param bmp2
     * @return the combined bitmap
     */
    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {

        int maxWidth = bmp1.getWidth() > bmp2.getWidth() ? bmp1.getWidth() : bmp2.getWidth();
        int maxHeight = bmp1.getHeight() > bmp2.getHeight() ? bmp1.getHeight() : bmp2.getHeight();
        Bitmap bmOverlay = Bitmap.createBitmap(maxWidth, maxHeight, bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, 0, 0, null);
        int cx = (bmp1.getWidth() / 2) - (bmp2.getWidth() / 2);
        int cy = ((bmp1.getHeight() / 2) - (bmp2.getHeight() / 2)) - SHAPE_TAIL_LENGTH;
        canvas.drawBitmap(bmp2, cx, cy, null);
        return bmOverlay;

    }

    /**
     * This function scales the given image with the given coefficient
     * 
     * @param img
     * @param coeff
     * @return the scaled image
     */
    private Bitmap scaleMarker(Bitmap img, float coeff) {
        int newWidth = Math.round(coeff * img.getWidth());
        int newHeight = Math.round(coeff * img.getHeight());
        return Bitmap.createScaledBitmap(img, newWidth, newHeight, true);

    }

    private long minutesToMilliseconds(long minutes) {
        return minutes * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND;
    }

    /**
     * Set the marker's color in terms of the elapsed time
     * 
     * @param elapsedTime
     */
    private void setColorOfMarkerShape(long elapsedTime) {

        long timeoutInMillis = this.minutesToMilliseconds(TIMEOUT_COLOR);
        Canvas canvas = new Canvas(mMarkerShape);
        ColorMatrix cm = new ColorMatrix();
        if (elapsedTime < timeoutInMillis) {
            cm.setSaturation((((SATURATION_BEGIN * timeoutInMillis) - elapsedTime) + (SATURATION_END * elapsedTime))
                / timeoutInMillis);
        } else {
            cm.setSaturation(SATURATION_END);
        }

        ColorMatrixColorFilter lightingColorFilter = new ColorMatrixColorFilter(cm);
        Paint paintLightening = new Paint();
        paintLightening.setColorFilter(lightingColorFilter);
        canvas.drawBitmap(mMarkerShape, 0, 0, paintLightening);
    }

}
