package com.me.whiteboard.actions;

import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.me.whiteboard.Display;
import com.me.whiteboard.MainActivity;

public class TextAction extends Action {

	private int color;
	private float textSize;
	private float x, y;
	private String text;

	public TextAction() {
		super();
		type = TYPE_TEXT;
	}

	public TextAction(short usr_ID, short local_ID) {
		super(TYPE_TEXT, usr_ID, local_ID);
	}

	@Override
	public void act(MainActivity activity, Canvas canvas) {
		Paint paint = new Paint(MainActivity.paint);
		paint.setColor(color);
		paint.setTextSize(Display.width_AbsoluteToScreen(textSize));
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

}
