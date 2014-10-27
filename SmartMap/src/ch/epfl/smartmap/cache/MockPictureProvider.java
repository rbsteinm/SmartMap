/**
 * 
 */
package ch.epfl.smartmap.cache;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

/**
 * A provisional class to retrieve friend's pics from the cache
 * 
 * @author hugo-S
 * 
 */
public class MockPictureProvider {

	public static final int BITMAP_WIDTH = 100;
	public static final int BITMAP_HEIGHT = 100;
	public static final float CANVAS_ORIGIN_X = 30;
	public static final float CANVAS_ORIGIN_Y = 40;
	public static final float PAINT_TEXT_SIZE = 35;

	public MockPictureProvider() {

	}

	public Bitmap getImage(long id) {
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		Bitmap bmp = Bitmap.createBitmap(BITMAP_WIDTH, BITMAP_HEIGHT, conf);
		Canvas canvas1 = new Canvas(bmp);

		String path = "/storage/emulated/0/Android/data/ch.epfl.smartmap"
				+ "/cache/medias/";
		File f = new File(path, id + ".png");
		if (!f.exists()) {
			f = new File(path, "unknown.png");
		}
		Paint color = new Paint();
		color.setTextSize(PAINT_TEXT_SIZE);
		color.setColor(Color.BLACK);

		// modify canvas
		canvas1.drawBitmap(BitmapFactory.decodeFile(f.getPath()), 0, 0, color);
		canvas1.drawText("User Name!", CANVAS_ORIGIN_X, CANVAS_ORIGIN_Y, color);
		bmp = BitmapFactory.decodeFile(f.getPath());
		if (bmp != null) {

			Log.d("search image:", path + id + ".png" + "  found");
		} else {
			Log.d("search image:", path + id + ".png" + "  not found");
		}

		return bmp;
	}

}
