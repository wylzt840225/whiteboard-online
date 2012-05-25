package com.me.whiteboard.actions;

import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.TextSize;

import com.me.whiteboard.Display;
import com.me.whiteboard.MainActivity;

public class TextAction extends Action {

	private int color;
	private float textSize;
	private float x, y;
	private String text;
	
	private static int previousPointCount = 0;
	private static float sumOfLength;
	private static float x_mean_absolute;
	private static float y_mean_absolute;

	public TextAction() {
		super();
		type = TYPE_TEXT;
	}

	public TextAction(short usr_ID, short local_ID, Paint paint) {
		super(TYPE_TEXT, usr_ID, local_ID);
		color = paint.getColor();
		textSize = paint.getTextSize();
	}

	@Override
	public void act(MainActivity activity, Canvas canvas) {
		Paint paint = new Paint(MainActivity.paint);
		paint.setColor(color);
		paint.setTextSize(Display.length_AbsoluteToScreen(textSize));
		canvas.drawText(text, x, y, paint);
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
		if (pointCount != previousPointCount) {
			reset(x_mean, y_mean, sumOfLength);
			return;
		}

		textSize *= sumOfLength / TextAction.sumOfLength;
		x += x_mean_absolute - Display.x_ScreenPosToAbsolutePos(x_mean);
		y += y_mean_absolute - Display.y_ScreenPosToAbsolutePos(y_mean);

		reset(x_mean, y_mean, sumOfLength);
	}
	
	private void reset(float x_mean, float y_mean, float sumOfLength) {
		TextAction.x_mean_absolute = Display.x_ScreenPosToAbsolutePos(x_mean);
		TextAction.y_mean_absolute = Display.y_ScreenPosToAbsolutePos(y_mean);
		TextAction.sumOfLength = sumOfLength;
	}
}
