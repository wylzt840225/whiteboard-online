package com.me.whiteboard;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.me.whiteboard.compat.ActionBarActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MainActivity extends ActionBarActivity {
	/** Called when the activity is first created. */
	
	static MainActivity instance;
	static MainActivity getinstance()
	{
		return instance;
	}
	Bitmap bm;
	Bitmap tempbm;
	Float x, y;
	DrawView dw;
	boolean down;
	Canvas temp, c;
	int type = 4;

	class History implements Serializable {
		float x1, y1, x2, y2;
		int type, color;

		History(float x1, float y1, float x2, float y2, int type, int color) {
			this.x1 = x1;
			this.x2 = x2;
			this.y1 = y1;
			this.y2 = y2;
			this.type = type;
			this.color = color;

		}

		History(JSONObject js) {
			try {

				this.x1 = (float) js.getDouble("x1");
				this.x2 = (float) js.getDouble("x2");
				this.y1 = (float) js.getDouble("y1");
				this.y2 = (float) js.getDouble("y2");
				this.type = js.getInt("type");
				this.color = js.getInt("color");
			} catch (JSONException e) {
			}
		}

		String json() {
			JSONObject js = new JSONObject();
			try {
				js.put("x1", this.x1);
				js.put("x2", this.x2);
				js.put("y1", this.y1);
				js.put("y2", this.y2);
				js.put("type", this.type);
				js.put("color", this.color);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return js.toString();
		}

	}

	ArrayList<History> list;

	void drawlist() {
		History h;
		temp.drawColor(Color.WHITE);
		c.drawColor(Color.WHITE);
		for (int i = 0; i < list.size(); i++) {
			h = list.get(i);
			x = h.x1;
			y = h.y1;
			type = h.type;
			GlobalS.getinstance().mPaint.setColor(h.color);
			Draw(c, h.x2, h.y2);
			temp.drawBitmap(bm, 0, 0, GlobalS.getinstance().mPaint);
		}
		dw.invalidate();
	}

	class DrawView extends View {
		public DrawView(Context context) {
			super(context);
			
		}

		public void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			GlobalS.getinstance().mPaint.setColor(Color.BLACK);
			canvas.drawBitmap(tempbm, 0, 0,GlobalS.getinstance(). mPaint);
		}
	}

	void Draw(Canvas p, float x1, float y1) {
		switch (type) {
		case 0:
			p.drawRect(x, y, x1, y1,GlobalS.getinstance(). mPaint);
			break;
		case 1:
			p.drawOval(new RectF(x, y, x1, y1), GlobalS.getinstance().mPaint);
			break;
		case 2:
		case 3:
			p.drawLine(x, y, x1, y1, GlobalS.getinstance().mPaint);
			break;
		}
	}

	void drawOn(int cc, float x1, float y1) {
		Canvas p = c;
		if (cc == 0) {
			p = temp;

			p.drawColor(Color.WHITE);
			p.drawBitmap(bm, 0, 0, GlobalS.getinstance().mPaint);

		}
		Draw(p, x1, y1);
		if (type == 3 || cc == 1) {
			temp.drawBitmap(bm, 0, 0, GlobalS.getinstance().mPaint);
			list.add(new History(x, y, x1, y1, type, GlobalS.getinstance().mPaint.getColor()));
		}

		if (type < 4)
			dw.invalidate();
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
/*
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}
*/
	boolean itemhandler(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.rect:
			type = 0;
			break;
		case R.id.oval:
			type = 1;
			break;
		case R.id.line:
			type = 2;
			break;
		case R.id.free:
			type = 3;
			break;
		case R.id.colorss:
			Intent i=new Intent();
			i.setClass(this, ColorPicker.class);
			startActivity(i);
			break;
		case R.id.nones:
			type = 4;
			break;
		case R.id.clearss:
			c.drawColor(Color.WHITE);
			temp.drawColor(Color.WHITE);

			dw.invalidate();
			break;
		case R.id.save:
			savelist("1");
			list = new ArrayList<MainActivity.History>();
			break;
		case R.id.load:
			loadlist("1");
			drawlist();
			break;

		}
		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// do different action in different fragment page
		return itemhandler(item);
	}

	public boolean onContextItemSelected(MenuItem item) {

		return itemhandler(item);
	}

	void savelist(String fileName) {
		FileOutputStream fos;

		try {
			fos = openFileOutput(fileName, MODE_PRIVATE);
			OutputStreamWriter out = new OutputStreamWriter(fos);
			out.write("{data:[");
			if (list.size() > 0) {
				out.write(list.get(0).json());
			}
			for (int i = 1; i < list.size(); i++) {
				out.write(",");
				out.write(list.get(i).json());
			}
			out.write("]}");
			out.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void loadlist(String fileName) {
		FileInputStream fis;
		try {
			fis = openFileInput(fileName);
			InputStreamReader in = new InputStreamReader(fis);
			BufferedReader buffreader = new BufferedReader(in);
			JSONArray ja = new JSONObject(buffreader.readLine())
					.getJSONArray("data");
			list.clear();
			for (int i = 0; i < ja.length(); i++) {
				list.add(new History(ja.getJSONObject(i)));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance=this;
		bm = Bitmap.createBitmap(getWindow().getWindowManager()
				.getDefaultDisplay().getWidth(), getWindow().getWindowManager()
				.getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
		tempbm = Bitmap.createBitmap(getWindow().getWindowManager()
				.getDefaultDisplay().getWidth(), getWindow().getWindowManager()
				.getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
		temp = new Canvas(tempbm);
		list = new ArrayList<MainActivity.History>();
		c = new Canvas(bm);
		dw = new DrawView(this);
		setContentView(dw);
		down = false;

		dw.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int a = event.getAction();
				switch (a) {
				case MotionEvent.ACTION_UP:
					down = false;
					drawOn(1, event.getX(), event.getY());

					break;
				case MotionEvent.ACTION_DOWN:
					x = event.getX();
					y = event.getY();
					down = true;
					break;

				case MotionEvent.ACTION_MOVE:

					if (down) {
						if (type < 3)
							drawOn(0, event.getX(), event.getY());
						else {

							drawOn(1, event.getX(), event.getY());
							x = event.getX();
							y = event.getY();
						}
					}
					break;
				}

				return type == 3;
			}
		});

		registerForContextMenu(dw);

		dw.setBackgroundColor(Color.WHITE);

	}
}