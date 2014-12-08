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

    /**
     * the user that the icon represents
     */
    private final User mUser;

    /**
     * Several composants of the marker icon saved in fields for performance purposes
     */
    private Bitmap mBaseMarkerShape;
    private Bitmap mCurrentMarkerShape;
    private Bitmap mProfilePicture;
    private Bitmap mMarkerIcon; // the combined icon (marker shape + profile picture)
    private Canvas mCanvasCurrentShape;
    private Bitmap mBaseOverlay;

    public static final float CIRCLE_CENTER_INCREMENT = 0.7f;
    public static final float CIRCLE_RADIUS_INCREMENT = 0.1f;
    public static final int SHAPE_BORDER_WIDTH = 115;
    public static final float SCALE_MARKER = 0.22f;
    public static final int SHAPE_TAIL_LENGTH = 53;
    public static final float SATURATION_BEGIN = 2f;
    public static final float SATURATION_END = -0.08f;
    public static final long TIMEOUT_COLOR = 30; // minutes
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int MILLISECONDS_IN_SECOND = 1000;

    public CircularMarkerIconMaker(User user) {
        mUser = user;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.map.MarkerIconMaker#getMarkerIcon(android.graphics.Bitmap)
     */
    @Override
    public Bitmap getMarkerIcon(Context context) {
        if ((mBaseMarkerShape == null) || (mCurrentMarkerShape == null)) {
            this.initializeMarkerShape(context);
        }
        if (mProfilePicture == null) {
            this.initializeProfilePicture();
        }
        if (mMarkerIcon == null) {
            this.initializeMarkerIcon();
        }

        long timeElapsed =
            GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00")).getTimeInMillis()
                - mUser.getLastSeen().getTimeInMillis();
        this.setColorOfMarkerShape(timeElapsed);

        return mMarkerIcon;
    }

    /**
     * This function creates a bitmap with right dimensions, in preparation to be an overlay of the two given
     * bitmaps
     * 
     * @param bmp1
     * @param bmp2
     * @return the dimensioned base overlay
     */

    private Bitmap createDimensionedOverlay(Bitmap bmp1, Bitmap bmp2) {
        int maxWidth = bmp1.getWidth() > bmp2.getWidth() ? bmp1.getWidth() : bmp2.getWidth();
        int maxHeight = bmp1.getHeight() > bmp2.getHeight() ? bmp1.getHeight() : bmp2.getHeight();
        return Bitmap.createBitmap(maxWidth, maxHeight, bmp1.getConfig());
    }

    /**
     * This function crops the given image in a circle form with the given radius
     * 
     * @param image
     * @param radius
     * @return the cropped image
     */
    private Bitmap cropCircle(Bitmap image, int radius) {
        Paint color = new Paint();
        color.setColor(Color.BLACK);
        Bitmap preOut;

        if ((image.getWidth() != radius) || (image.getHeight() != radius)) {
            preOut = Bitmap.createScaledBitmap(image, 2 * radius, 2 * radius, true);
        } else {
            preOut = image;
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
     * This function initializes the marker
     */
    private void initializeMarkerIcon() {
        mBaseOverlay = this.createDimensionedOverlay(mBaseMarkerShape, mProfilePicture);
        mMarkerIcon = this.overlay(mCurrentMarkerShape, mProfilePicture);
        mMarkerIcon = this.scaleMarker(mMarkerIcon, SCALE_MARKER);
    }

    /**
     * This function initializes the markers's shape by retrieving it from resources
     * 
     * @param context
     */
    private void initializeMarkerShape(Context context) {
        int idForm = R.drawable.marker_form;
        mBaseMarkerShape = BitmapFactory.decodeResource(context.getResources(), idForm);
        mCurrentMarkerShape = mBaseMarkerShape.copy(mBaseMarkerShape.getConfig(), true);
        mCanvasCurrentShape = new Canvas(mCurrentMarkerShape);
    }

    /**
     * This function initializes the profile picture, by retrieving it from resources and cropping it in a
     * circle form
     */
    private void initializeProfilePicture() {
        mProfilePicture =
            Bitmap.createScaledBitmap(mUser.getImage(), User.PICTURE_WIDTH, User.PICTURE_HEIGHT, false);

        mProfilePicture =
            Bitmap.createScaledBitmap(mProfilePicture, mBaseMarkerShape.getWidth() - SHAPE_BORDER_WIDTH,
                mBaseMarkerShape.getWidth() - SHAPE_BORDER_WIDTH, true);

        mProfilePicture = this.cropCircle(mProfilePicture, mProfilePicture.getWidth());

    }

    private long minutesToMilliseconds(long minutes) {
        return minutes * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND;
    }

    /**
     * this function combines the center of the second image on the center of the first image, on the base on
     * the already prepared base bitmap mBmOverlay
     * 
     * @param bmp1
     * @param bmp2
     * @return the combined bitmap
     */
    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {

        Canvas canvas = new Canvas(mBaseOverlay);
        canvas.drawBitmap(bmp1, 0, 0, null);
        int cx = (bmp1.getWidth() / 2) - (bmp2.getWidth() / 2);
        int cy = ((bmp1.getHeight() / 2) - (bmp2.getHeight() / 2)) - SHAPE_TAIL_LENGTH;
        canvas.drawBitmap(bmp2, cx, cy, null);
        return mBaseOverlay;

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

    /**
     * Set the marker's color in terms of the elapsed time
     * 
     * @param elapsedTime
     */
    private void setColorOfMarkerShape(long elapsedTime) {
        mCanvasCurrentShape.drawBitmap(mBaseMarkerShape, 0, 0, null);
        long timeoutInMillis = this.minutesToMilliseconds(TIMEOUT_COLOR);

        Canvas canvas = new Canvas(mCurrentMarkerShape);
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
        canvas.drawBitmap(mCurrentMarkerShape, 0, 0, paintLightening);

        // update the marker icon with the new marker shape
        mMarkerIcon = this.overlay(mCurrentMarkerShape, mProfilePicture);
        mMarkerIcon = this.scaleMarker(mMarkerIcon, SCALE_MARKER);

    }
}