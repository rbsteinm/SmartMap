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
 * @author hugo
 * 
 */
public class MockPictureProvider {

	public MockPictureProvider() {

	}

	public Bitmap getImage(int id) {
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		Bitmap bmp = Bitmap.createBitmap(100, 100, conf);
		Canvas canvas1 = new Canvas(bmp);

		String path = "/storage/emulated/0/Android/data/ch.epfl.smartmap"
				+ "/cache/medias/";
		File f = new File(path, id + ".png");
		if (!f.exists())
			f = new File(path, "unknown.png");

		Paint color = new Paint();
		color.setTextSize(35);
		color.setColor(Color.BLACK);

		try {
			// modify canvas
			canvas1.drawBitmap(BitmapFactory.decodeFile(f.getPath()), 0, 0,
					color);
			canvas1.drawText("User Name!", 30, 40, color);
			bmp = BitmapFactory.decodeFile(f.getPath());
			if (bmp != null) {
				// Toast.makeText(this, f.getAbsoluteFile()+ " found",
				// Toast.LENGTH_SHORT).show();
				Log.d("search image:", path + id + ".png" + "  found");
			} else {
				Log.d("search image:", path + id + ".png" + "  not found");
			}
			// Bitmap bMap = BitmapFactory.decodeFile(f.getAbsolutePath());
			return bmp;
		} catch (Exception e) {
			// Toast.makeText(this, f.getPath()+" not accible",
			// Toast.LENGTH_SHORT).show();
			Log.d("search image:", path + id + ".png" + " not accessible");

		}
		return bmp;
	}

}
