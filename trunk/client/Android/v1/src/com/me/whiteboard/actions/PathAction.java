package com.me.whiteboard.actions;

import java.util.ArrayList;

import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.me.whiteboard.Display;
import com.me.whiteboard.MainActivity;
import com.me.whiteboard.MyData;

public class PathAction extends Action {
	
	private int color;
	private float strokeWidth;
	private ArrayList<Float> x_history;
	private ArrayList<Float> y_history;

	private static Path path;
	private static float x_relative, y_relative;

	public PathAction() {
		super();
		type = TYPE_PATH;
		x_history = new ArrayList<Float>();
		y_history = new ArrayList<Float>();
	}

	public PathAction(short usr_ID, short local_ID, Paint paint) {
		super(TYPE_PATH, usr_ID, local_ID);
		x_history = new ArrayList<Float>();
		y_history = new ArrayList<Float>();
		color = paint.getColor();
		strokeWidth = paint.getStrokeWidth();
		path = new Path();
	}

	public void act(MainActivity activity, Canvas canvas) {
		if (x_history.size() == 0 || x_history.size() != y_history.size()) {
			return;
		}

		// if (time < Display.minDisplayTime) {
		// return;
		// }

		Paint paint = new Paint(MainActivity.paint);
		paint.setColor(color);
		paint.setStrokeWidth(Display.width_AbsoluteToScreen(strokeWidth));

		if (usr_ID == MyData.getInstance().usr_ID
				&& local_ID == MyData.getInstance().local_ID) {
			canvas.drawPath(path, paint);
		} else {
			Path path = new Path();
			path.reset();
			path.moveTo(Display.x_AbsolutePosToBmpPos(x_history.get(0)),
					Display.y_AbsolutePosToBmpPos(y_history.get(0)));

			float x1, y1, x2, y2;
			x2 = Display.x_AbsolutePosToBmpPos(x_history.get(0));
			y2 = Display.y_AbsolutePosToBmpPos(y_history.get(0));
			for (int i = 0; i < x_history.size() - 1; i++) {
				x1 = x2;
				y1 = y2;
				x2 = Display.x_AbsolutePosToBmpPos(x_history.get(i + 1));
				y2 = Display.y_AbsolutePosToBmpPos(y_history.get(i + 1));
				path.quadTo(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2);
			}
			path.lineTo(x2, y2);
			canvas.drawPath(path, paint);
		}
		// activity.FlushCanvas();
	}

	public byte[] privateToBytes() {
		ByteArrayBuffer baf = new ByteArrayBuffer(6 + x_history.size()
				+ y_history.size());
		byte[] bytes = intToBytes(color);
		baf.append(bytes, 0, bytes.length);

		bytes = shortToBytes((short) (strokeWidth / Display.screen_width * Short.MAX_VALUE));
		baf.append(bytes, 0, bytes.length);

		for (int i = 0; i < x_history.size(); i++) {
			bytes = shortToBytes((short) (x_history.get(i)
					/ Display.screen_width * Short.MAX_VALUE));
			baf.append(bytes, 0, bytes.length);
			bytes = shortToBytes((short) (y_history.get(i)
					/ Display.screen_height * Short.MAX_VALUE));
			baf.append(bytes, 0, bytes.length);
		}
		return baf.toByteArray();
	}

	protected void bytesToPrivate(byte[] bytes) {
		color = bytesToInt(bytes[0], bytes[1], bytes[2], bytes[3]);
		strokeWidth = (float) bytesToShort(bytes[4], bytes[5])
				/ Short.MAX_VALUE * Display.screen_width;
		for (int i = 6; i < bytes.length - 3; i += 4) {
			x_history.add((float) (1.0 * bytesToShort(bytes[i], bytes[i + 1])
					/ Short.MAX_VALUE * Display.screen_width));
			y_history.add((float) (1.0
					* bytesToShort(bytes[i + 2], bytes[i + 3])
					/ Short.MAX_VALUE * Display.screen_height));
		}
	}

	public void addPoint(float x_relative, float y_relative) {
		float x_absolute = Display.x_ScreenPosToAbsolutePos(x_relative);
		float y_absolute = Display.y_ScreenPosToAbsolutePos(y_relative);

		x_absolute = x_absolute < 0 ? 0 : x_absolute;
		x_absolute = x_absolute > Display.screen_width ? Display.screen_width
				: x_absolute;
		y_absolute = y_absolute < 0 ? 0 : y_absolute;
		y_absolute = y_absolute > Display.screen_height ? Display.screen_height
				: y_absolute;

		if (x_history.isEmpty()) {
			path.reset();
			path.moveTo(x_relative, y_relative);
		} else {
			path.quadTo(PathAction.x_relative, PathAction.y_relative,
					(PathAction.x_relative + x_relative) / 2,
					(PathAction.y_relative + y_relative) / 2);
		}

		x_history.add(x_absolute);
		y_history.add(y_absolute);
		PathAction.x_relative = x_relative;
		PathAction.y_relative = y_relative;
	}
	
	public int getHistoryCount() {
		return x_history.size() == y_history.size() ? x_history.size() : -1;
	}

	@Override
	public View getView(LayoutInflater mLayoutInflater, int mResource,
			View convertView, ViewGroup parent, boolean selected) {
		return null;
	}
}
