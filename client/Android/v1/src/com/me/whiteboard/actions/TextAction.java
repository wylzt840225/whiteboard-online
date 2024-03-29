package com.me.whiteboard.actions;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.me.whiteboard.Display;
import com.me.whiteboard.MainActivity;
import com.me.whiteboard.R;

public class TextAction extends Action {

	private int color;
	private float textSize;
	private float x, y;
	private String text;
	public boolean temp = false;
	
	private static float sumOfLength;
	private static float x_mean_absolute;
	private static float y_mean_absolute;
	private static Paint paint = new Paint(MainActivity.paint);
	
	private static Bitmap confimBmp;
	private static Bitmap cancelBmp;

	public TextAction() {
		super();
		type = TYPE_TEXT;
	}

	public TextAction(short usr_ID, short local_ID, String text, Paint paint, Context context) {
		super(TYPE_TEXT, usr_ID, local_ID);
		this.text = text;
		color = paint.getColor();
		textSize = paint.getTextSize();
		x = Display.screen_width / 2;
		y = Display.screen_height / 2;
		confimBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.confirm);
		cancelBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.cancel);
	}

	@Override
	public void act(MainActivity activity, Canvas canvas) {
		paint.setColor(color);
		paint.setTextSize(Display.length_AbsoluteToScreen(textSize));
		paint.setStrokeWidth(0);
		canvas.drawText(text, x, y, paint);
		
		if (temp) {
			FontMetrics fm = paint.getFontMetrics();
			float h = (float) (0.25 * Math.ceil(fm.descent - fm.top));
			Rect confirmRect = new Rect((int) (x - 40), (int) (y + h),
					(int) (x - 8), (int) (y + h + 32));
			Rect cancelRect = new Rect((int) (x + 8), (int) (y + h),
					(int) (x + 40), (int) (y + h + 32));
			canvas.drawBitmap(confimBmp, null, confirmRect, paint);
			canvas.drawBitmap(cancelBmp, null, cancelRect, paint);
		}
	}
	
	public boolean inConfirm(float eventX, float eventY) {
		FontMetrics fm = paint.getFontMetrics();
		float h = (float) (0.25 * Math.ceil(fm.descent - fm.top));
		
		if (eventX > x - 40 && eventX < x - 8 && eventY > y + h && eventY < y + h +32) {
			return true;
		}
		return false;
	}
	
	public boolean inCancel(float eventX, float eventY) {
		FontMetrics fm = paint.getFontMetrics();
		float h = (float) (0.25 * Math.ceil(fm.descent - fm.top));
		
		if (eventX > x + 8 && eventX < x + 40 && eventY > y + h && eventY < y + h +32) {
			return true;
		}
		return false;
	}

	@Override
	public byte[] privateToBytes() {
		ByteArrayBuffer baf = new ByteArrayBuffer(10 + text.getBytes().length);
		
		byte[] bytes = intToBytes(color);
		baf.append(bytes, 0, bytes.length);
		
		bytes = shortToBytes((short) (textSize / Display.screen_width * Short.MAX_VALUE));
		baf.append(bytes, 0, bytes.length);
		
		bytes = shortToBytes((short) (x / Display.screen_width * Short.MAX_VALUE));
		baf.append(bytes, 0, bytes.length);
		
		bytes = shortToBytes((short) (y / Display.screen_width * Short.MAX_VALUE));
		baf.append(bytes, 0, bytes.length);
		
		bytes = text.getBytes();
		baf.append(bytes, 0, bytes.length);
		
		return baf.toByteArray();
	}

	@Override
	protected void bytesToPrivate(byte[] bytes) {
		color = bytesToInt(bytes[0], bytes[1], bytes[2], bytes[3]);
		textSize = (float) bytesToShort(bytes[4], bytes[5])
				/ Short.MAX_VALUE * Display.screen_width;
		x = (float) bytesToShort(bytes[6], bytes[7])
				/ Short.MAX_VALUE * Display.screen_width;
		y = (float) bytesToShort(bytes[8], bytes[9])
				/ Short.MAX_VALUE * Display.screen_width;
		text = new String(bytes, 10, bytes.length - 10);
	}

	@Override
	public View getView(LayoutInflater mLayoutInflater, int mResource,
			View convertView, ViewGroup parent, boolean selected) {
		return null;
	}
	
	public void update(int pointCount, float x_mean, float y_mean,
			float sumOfLength) {
		if (pointCount != Display.previousPointCount) {
			reset(x_mean, y_mean, sumOfLength);
			return;
		}

		if (sumOfLength * TextAction.sumOfLength != 0) {
			textSize *= sumOfLength / TextAction.sumOfLength;
		}
		
		x -= x_mean_absolute - Display.x_ScreenPosToAbsolutePos(x_mean);
		y -= y_mean_absolute - Display.y_ScreenPosToAbsolutePos(y_mean);
		
		float textWidth = paint.measureText(text);
		FontMetrics fm = paint.getFontMetrics();
		float textHeight = (float) (0.75 * Math.ceil(fm.descent - fm.top));
		
		if (x < textWidth / 2) {
			x = textWidth / 2;
		} else if (x > Display.screen_width - textWidth / 2) {
			x = Display.screen_width - textWidth / 2;
		}
		
		if (y < textHeight) {
			y = textHeight;
		} else if (y > Display.screen_height) {
			y = Display.screen_height;
		}

		reset(x_mean, y_mean, sumOfLength);
	}
	
	private void reset(float x_mean, float y_mean, float sumOfLength) {
		TextAction.x_mean_absolute = Display.x_ScreenPosToAbsolutePos(x_mean);
		TextAction.y_mean_absolute = Display.y_ScreenPosToAbsolutePos(y_mean);
		TextAction.sumOfLength = sumOfLength;
	}
}
