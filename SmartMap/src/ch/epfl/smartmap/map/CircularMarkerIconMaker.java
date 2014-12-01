/**
 * 
 */
package ch.epfl.smartmap.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import ch.epfl.smartmap.R;

/**
 * An implementation of {@link MarkerIconMaker}, that creates circular icons for markers
 * 
 * @author hugo-S
 */
public class CircularMarkerIconMaker implements MarkerIconMaker {

    private Bitmap mMarkerShape;
    private final Context mContext;

    public CircularMarkerIconMaker(Context context) {
        mContext = context;
    }

    private void setMarkerShape() {
        int idForm = R.drawable.marker_forme;
        mMarkerShape = BitmapFactory.decodeResource(mContext.getResources(), idForm);
        mMarkerShape = Bitmap.createScaledBitmap(mMarkerShape, 46, 60, false);
        Log.d("MarkerTool", "found form " + idForm);

    }

    private Bitmap cropProfilePicture(Bitmap profPic, int radius) {
        Paint color = new Paint();
        color.setColor(Color.BLACK);
        Bitmap preOut;

        if ((profPic.getWidth() != radius) || (profPic.getHeight() != radius)) {
            preOut = Bitmap.createScaledBitmap(profPic, 2 * radius, 2 * radius, false);
        } else {
            preOut = profPic;
        }

        Bitmap output = Bitmap.createBitmap(preOut.getWidth(), preOut.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, preOut.getWidth(), preOut.getHeight());

        paint.setAntiAlias(true); // AntiAliasing smooths out the edges of what is being drawn
        paint.setFilterBitmap(true); // Filtering affects the sampling of bitmaps when they are transformed.
        paint.setDither(true);// Dithering affects how colors that are higher precision than the device are
                              // down-sampled
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle((preOut.getWidth() / 2) + 0.7f, (preOut.getHeight() / 2) + 0.7f,
            (preOut.getWidth() / 2) + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); // crop the image depending on the drawn //
                                                                // object
        canvas.drawBitmap(preOut, rect, rect, paint);

        Log.d("MAKER TOOL", "crop round");
        return output;

    }

    private Bitmap scaleMarker(Bitmap img, float coeff) {
        int newWidth = Math.round(coeff * img.getWidth());
        int newHeight = Math.round(coeff * img.getHeight());
        return Bitmap.createScaledBitmap(img, newWidth, newHeight, false);

    }

    // this function combines the center of the second image to the center of the first image
    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        try {
            int maxWidth = (bmp1.getWidth() > bmp2.getWidth() ? bmp1.getWidth() : bmp2.getWidth());
            int maxHeight = (bmp1.getHeight() > bmp2.getHeight() ? bmp1.getHeight() : bmp2.getHeight());
            Bitmap bmOverlay = Bitmap.createBitmap(maxWidth, maxHeight, bmp1.getConfig());
            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(bmp1, 0, 0, null);
            int c_x = (bmp1.getWidth() / 2) - (bmp2.getWidth() / 2) - 2;
            int c_y = (bmp1.getHeight() / 2) - (bmp2.getHeight() / 2) - 6;
            canvas.drawBitmap(bmp2, c_x, c_y, null);
            Log.d("MAKER TOOL", "overlay done");
            // bmOverlay = scaleImage(bmOverlay, 1.2f);
            return bmOverlay;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.map.MarkerIconMaker#getMarkerIcon(android.graphics.Bitmap)
     */
    @Override
    public Bitmap getMarkerIcon(Bitmap profilePicture) {
        // TODO Auto-generated method stub
        this.setMarkerShape();
        profilePicture =
            Bitmap.createScaledBitmap(profilePicture, mMarkerShape.getWidth() - 12,
                mMarkerShape.getWidth() - 12, false);
        Bitmap roundProfile = this.cropProfilePicture(profilePicture, profilePicture.getWidth());
        Bitmap finalMarker = this.scaleMarker(this.overlay(mMarkerShape, roundProfile), 1.7f);
        Log.d("MAKER TOOL", "makeProfileMarker done");
        return finalMarker;
    }

}
