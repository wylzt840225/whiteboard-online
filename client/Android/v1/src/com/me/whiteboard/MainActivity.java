package com.me.whiteboard;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;

import com.me.whiteboard.compat.ActionBarActivity;
import com.me.whiteboard.http.Client;
import com.me.whiteboard.http.Client.onNewDataRecv;

public class MainActivity extends ActionBarActivity {
	/** Called when the activity is first created. */

	Bitmap bm;
	Bitmap tempbm;
	Float x, y;
	DrawView dw;
	String room;
	// boolean down;
	Canvas temp, c;
	int type = 1;
	int width, height;
	Path path;
	ArrayList<Float> x_History = new ArrayList<Float>();
	ArrayList<Float> y_History = new ArrayList<Float>();

	public static short usr_ID;
	public static short local_ID = 0;
	static ActionHistory actionHistory = new ActionHistory();

	static MainActivity instance;

	static MainActivity getinstance() {
		return instance;
	}

	

	class DrawView extends View {
		public DrawView(Context context) {
			super(context);

		}

		public void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.drawBitmap(tempbm, 0, 0, null);
		}
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		menu.findItem(R.id.load).setVisible(false);
		menu.findItem(R.id.save).setVisible(false);
		menu.findItem(R.id.nones).setVisible(false);
		menu.findItem(R.id.colorss).setVisible(false);

	}

	void draw(Float x, Float y) {
		// tempbm = bm;
		temp.drawPath(path, GlobalS.getinstance().mPaint);
		dw.invalidate();

		x_History.add(x);
		y_History.add(y);

	}

	void drawPath(String[] data) {
		if (data.length < 3) {
			return;
		}

		Paint paint = new Paint(GlobalS.getinstance().mPaint);
		paint.setColor(Integer.parseInt(data[0]));

		Path path = new Path();
		path.reset();
		path.moveTo(Float.parseFloat(data[1]) * width,
				Float.parseFloat(data[2]) * height);

		int i;
		for (i = 1; i < data.length - 3; i += 2) {
			path.quadTo(
					Float.parseFloat(data[i]) * width,
					Float.parseFloat(data[i + 1]) * height,
					(Float.parseFloat(data[i]) + Float.parseFloat(data[i + 2]))
							/ 2 * width,
					(Float.parseFloat(data[i + 1]) + Float
							.parseFloat(data[i + 3])) / 2 * height);
		}
		path.lineTo(Float.parseFloat(data[i]) * width,
				Float.parseFloat(data[i + 1]) * height);

		temp.drawPath(path, paint);
		dw.invalidate();
	}

	void sendPath() {
		String data = Integer.toString(GlobalS.getinstance().mPaint.getColor());

		for (int i = 0; i < x_History.size(); i++) {
			data += "," + Float.toString(x_History.get(i) / width) + ","
					+ Float.toString(y_History.get(i) / height);
		}

		x_History.clear();
		y_History.clear();

		actionHistory.add(usr_ID, local_ID, (short) type, data);

		String base64String = Base64Coder.encodeString(Short.toString(usr_ID)
				+ ";" + Short.toString(local_ID) + ";"
				+ Short.toString((short) type) + ";" + data);
		Client.SendData(room, base64String, new Runnable() {
			public void run() {
			}
		}, new Runnable() {
			public void run() {
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		room = getIntent().getStringExtra("room");
		usr_ID=(short) getIntent().getIntExtra("id", 0);
		setTitle(room);
		Client.setOnDataRecv(room, new onNewDataRecv() {
			public void onRecv(String[] datas) {
				for (int i = 0; i < datas.length; i++) {
					String[] data = Base64Coder.decodeString(datas[i]).split(
							";");

					short usr_ID_recv = Short.parseShort(data[0]);
					//short local_ID_recv = Short.parseShort(data[1]);
					//short type_recv = Short.parseShort(data[2]);

					if (usr_ID_recv != MainActivity.usr_ID) {
						drawPath(data[3].split(","));

					}
				}
			}
		});

		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (type == 1) {
						//sendPath();
					}
				}
			}
		}).start();
		setContentView(R.layout.canvas);
		findViewById(R.id.draw).post(new Runnable() {
			
			public void run() {
				width =findViewById(R.id.draw).getWidth();
				height = findViewById(R.id.draw).getHeight();
				instance = MainActivity.this;
				bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
				tempbm = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
				temp = new Canvas(tempbm);
				// list = new ArrayList<MainActivity.History>();
				c = new Canvas(bm);
				dw = new DrawView(MainActivity.this);
				path = new Path();
				
				LayoutParams lp=new LayoutParams();
				lp.width=android.view.ViewGroup.LayoutParams.FILL_PARENT;
				lp.height=android.view.ViewGroup.LayoutParams.FILL_PARENT;
				((ViewGroup)findViewById(R.id.draw)).addView(dw,lp);
				GlobalS.getinstance().mPaint.setColor(Color.BLACK);
				GlobalS.getinstance().mPaint.setAntiAlias(true);
				GlobalS.getinstance().mPaint.setStyle(Paint.Style.STROKE);
				GlobalS.getinstance().mPaint.setStrokeCap(Paint.Cap.ROUND);
				GlobalS.getinstance().mPaint.setStrokeWidth(0);
				// down = false;

				

				dw.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_UP:
							path.lineTo(x, y);
							// down = false;
							// drawOn(1, event.getX(), event.getY());
							draw(x, y);
							sendPath();
							c.drawPath(path, GlobalS.getinstance().mPaint);
							local_ID++;
							path.reset();
							break;
						case MotionEvent.ACTION_DOWN:
							x = event.getX();
							y = event.getY();
							path.reset();
							path.moveTo(x, y);
							// down = true;
							break;

						case MotionEvent.ACTION_MOVE:

							// if (down) {
							// if (type < 3)
							// drawOn(0, event.getX(), event.getY());
							// else {

							// drawOn(1, event.getX(), event.getY());
							path.quadTo(x, y, (x + event.getX()) / 2,
									(y + event.getY()) / 2);
							draw(x, y);
							x = event.getX();
							y = event.getY();
							// }
							// }
							break;
						}

						// return type == 3;
						return true;
					}
				});

				// registerForContextMenu(dw);

				dw.setBackgroundColor(Color.WHITE);
			}
		});
		

	}
}