package com.me.whiteboard.actions;

import java.util.ArrayList;

import org.apache.http.util.ByteArrayBuffer;

import com.me.whiteboard.MainActivity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PathAction extends Action {
	public int color;
	public ArrayList<Float> x_history;
	public ArrayList<Float> y_history;

	private static Path path;
	private static float x, y;

	public PathAction() {
		x_history = new ArrayList<Float>();
		y_history = new ArrayList<Float>();
	}

	public PathAction(short usr_ID, short local_ID, Paint paint) {
		super(TYPE_PATH, usr_ID, local_ID);
		x_history = new ArrayList<Float>();
		y_history = new ArrayList<Float>();
		color = paint.getColor();
		path = new Path();
	}

	public void act(MainActivity acts,Canvas canvas) {
		if (x_history.size() == 0 || x_history.size() != y_history.size()) {
			return;
		}
		
		Paint paint = new Paint(MainActivity.paint);
		paint.setColor(color);
		
		if (usr_ID == MainActivity.usr_ID) {
			canvas.drawPath(path, paint);
		} else {
			Path path = new Path();
			path.reset();
			path.moveTo(x_history.get(0), y_history.get(0));

			float x1, y1, x2, y2;
			x2 = x_history.get(0);
			y2 = y_history.get(0);
			for (int i = 0; i < x_history.size() - 1; i++) {
				x1 = x2;
				y1 = y2;
				x2 = x_history.get(i + 1);
				y2 = y_history.get(i + 1);
				path.quadTo(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2);
			}
			path.lineTo(x2, y2);
			canvas.drawPath(path, paint);
		}
		acts.FlushCanvas();
	}

	public byte[] privateToBytes() {
		ByteArrayBuffer baf = new ByteArrayBuffer(4 + x_history.size() + y_history.size());
		byte[] bytes = intToBytes(color);
		baf.append(bytes, 0, bytes.length);

		for (int i = 0; i < x_history.size(); i++) {
			bytes = shortToBytes((short) (x_history.get(i)
					/ MainActivity.width * Short.MAX_VALUE));
			baf.append(bytes, 0, bytes.length);
			bytes = shortToBytes((short) (y_history.get(i)
					/ MainActivity.height * Short.MAX_VALUE));
			baf.append(bytes, 0, bytes.length);
		}
		return baf.toByteArray();
	}

	protected void bytesToPrivate(byte[] bytes) {
		color = bytesToInt(bytes[0], bytes[1], bytes[2], bytes[3]);
		for (int i = 4; i < bytes.length - 3; i += 4) {
			x_history.add((float) (1.0 * bytesToShort(bytes[i], bytes[i + 1])
					/ Short.MAX_VALUE * MainActivity.width));
			y_history.add((float) (1.0 * bytesToShort(bytes[i + 2], bytes[i + 3])
					/ Short.MAX_VALUE * MainActivity.height));
		}
	}

	public void addPoint(float x, float y) {
		if (x_history.isEmpty()) {
			path.reset();
			path.moveTo(x, y);
		} else {
			path.quadTo(PathAction.x, PathAction.y, (PathAction.x+x)/2, (PathAction.y+y)/2);
		}
		x_history.add(x);
		y_history.add(y);
		PathAction.x = x;
		PathAction.y = y;
	}

	@Override
	public View getView(LayoutInflater mLayoutInflater, int mResource,
			View convertView, ViewGroup parent, boolean selected) {
		return null;
	}
}
