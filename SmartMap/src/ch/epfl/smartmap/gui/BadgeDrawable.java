package ch.epfl.smartmap.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import ch.epfl.smartmap.R;

/**
 * Creates drawable that we can add on our action bar's app icon
 * 
 * @author agpmilli
 */
public class BadgeDrawable extends Drawable {

	private final float mTextSize;
	private final Paint mBadgePaint;
	private final Paint mTextPaint;
	private final Rect mTxtRect = new Rect();

	private String mCount = "";
	private boolean mWillDraw = false;

	public BadgeDrawable(Context context) {
		mTextSize = context.getResources().getDimension(R.dimen.badge_text_size);

		mBadgePaint = new Paint();
		mBadgePaint.setColor(Color.RED);
		mBadgePaint.setAntiAlias(true);
		mBadgePaint.setStyle(Paint.Style.FILL);

		mTextPaint = new Paint();
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
	}

	@Override
	public void draw(Canvas canvas) {
		if (!mWillDraw) {
			return;
		}

		Rect bounds = this.getBounds();
		float width = bounds.right - bounds.left;
		float height = bounds.bottom - bounds.top;

		// Position the badge in the top-right quadrant of the icon.
		float radius = ((Math.min(width, height) / 2) - 1) / 2;
		float centerX = width - radius - 1;
		float centerY = radius + 1;

		// Draw badge circle.
		canvas.drawCircle(centerX, centerY, radius, mBadgePaint);

		// Draw badge count text inside the circle.
		mTextPaint.getTextBounds(mCount, 0, mCount.length(), mTxtRect);
		float textHeight = mTxtRect.bottom - mTxtRect.top;
		float textY = centerY + (textHeight / 2f);
		canvas.drawText(mCount, centerX, textY, mTextPaint);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.UNKNOWN;
	}

	@Override
	public void setAlpha(int alpha) {
		// do nothing
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// do nothing
	}

	/*
	 * Sets the count (i.e notifications) to display.
	 */
	public void setCount(int count) {
		mCount = Integer.toString(count);

		// Only draw a badge if there are notifications.
		mWillDraw = count > 0;
		this.invalidateSelf();
	}
}
