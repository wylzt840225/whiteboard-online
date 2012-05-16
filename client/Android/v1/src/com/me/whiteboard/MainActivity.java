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
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.me.whiteboard.compat.ActionBarActivity;
import com.me.whiteboard.http.Client;
import com.me.whiteboard.http.Client.onNewDataRecv;
import com.me.whiteboard.http.Client.onRoomEntered;

public class MainActivity extends ActionBarActivity {
	/** Called when the activity is first created. */
	
	Bitmap bm;
	Bitmap tempbm;
	Float x, y;
	DrawView dw;
	//boolean down;
	Canvas temp, c;
	int type = 1;
	int width, height;
	Path path;
	ArrayList<Float> x_History = new ArrayList<Float> ();
	ArrayList<Float> y_History = new ArrayList<Float> ();
	
	public static short usr_ID;
	public static short local_ID = 0;
	static ActionHistory actionHistory = new ActionHistory();
	
	static MainActivity instance;
	static MainActivity getinstance()
	{
		return instance;
	}

	/*
	@SuppressWarnings("serial")
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

	}*/

	//ArrayList<History> list;

	/*void drawlist() {
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
	}*/

	class DrawView extends View {
		public DrawView(Context context) {
			super(context);
			
		}

		public void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.drawBitmap(tempbm, 0, 0, null);
		}
	}

	/*void Draw(Canvas p, float x1, float y1) {
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
			//list.add(new History(x, y, x1, y1, type, GlobalS.getinstance().mPaint.getColor()));
			
			String data = Integer.toString(GlobalS.getinstance().mPaint.getColor()) + "," + 
						Float.toString(x) + "," + Float.toString(y) + "," + 
					Float.toString(x1) + "," + Float.toString(y1);
			actionHistory.add(usr_ID, local_ID, (short) 1, data);
			
			String base64String = Base64Coder.encodeString(usr_ID + ";" + local_ID + ";" +
					Short.toString((short) 1) + ";" + data);
			Client.SendData("test4", base64String, new Runnable() { public void run() {}},
					new Runnable() { public void run() {}});
		}

		if (type < 4)
			dw.invalidate();
	}*/

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
	/*public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}*/

	/*boolean itemhandler(MenuItem item) {
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
			//savelist("1");
			//list = new ArrayList<MainActivity.History>();
			break;
		case R.id.load:
			//loadlist("1");
			//drawlist();
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
	}*/

	/*void savelist(String fileName) {
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

	}*/
	
	void draw(Float x, Float y) {
		//tempbm = bm;
		temp.drawPath(path, GlobalS.getinstance().mPaint);
		dw.invalidate();
		
		x_History.add(x);
		y_History.add(y);
		
		/*String data = Integer.toString(GlobalS.getinstance().mPaint.getColor()) + "," + 
					Float.toString(x/width) + "," + Float.toString(y/height);
		actionHistory.add(usr_ID, local_ID, (short) type, data);*/
		
		/*String base64String = Base64Coder.encodeString(usr_ID + ";" + local_ID + ";" +
				Short.toString((short) type) + ";" + data);
		Client.SendData("test4", base64String, new Runnable() { public void run() {}},
				new Runnable() { public void run() {}});*/
	}
	
	void drawPath(String [] data) {
		if (data.length < 3) {
			return;
		}
		
		Paint paint = new Paint(GlobalS.getinstance().mPaint);
		paint.setColor(Integer.parseInt(data[0]));
		
		Path path = new Path();
		path.reset();
		path.moveTo(Float.parseFloat(data[1])*width, Float.parseFloat(data[2])*height);
		
		int i;
		for (i = 1; i < data.length - 3; i += 2) {
			path.quadTo(Float.parseFloat(data[i])*width, Float.parseFloat(data[i+1])*height, 
					(Float.parseFloat(data[i]) + Float.parseFloat(data[i+2]))/2*width, 
					(Float.parseFloat(data[i+1]) + Float.parseFloat(data[i+3]))/2*height);
		}
		path.lineTo(Float.parseFloat(data[i])*width, Float.parseFloat(data[i+1])*height);
		
		temp.drawPath(path, paint);
		dw.invalidate();
	}
	
	void sendPath() {
		String data = Integer.toString(GlobalS.getinstance().mPaint.getColor());
		
		for (int i = 0; i < x_History.size(); i++) {
			data += "," + Float.toString(x_History.get(i)/width) + 
					"," + Float.toString(y_History.get(i)/height);
		}
		
		x_History.clear();
		y_History.clear();
		
		actionHistory.add(usr_ID, local_ID, (short) type, data);
		
		String base64String = Base64Coder.encodeString(Short.toString(usr_ID) + ";" + 
													   Short.toString(local_ID) + ";" +
													   Short.toString((short) type) + ";" + data);
		Client.SendData("test4", base64String, new Runnable() { public void run() {}},
				new Runnable() { public void run() {}});
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Client.EnterRoom("test4", new onRoomEntered() {
			public void Error() {
			}
			public void Entered(String room, int usr_ID) {
				MainActivity.usr_ID = (short) usr_ID;
				setTitle(room);
				Client.setOnDataRecv("test4", new onNewDataRecv() {
					public void onRecv(String[] datas) {
						for (int i = 0; i < datas.length; i++) {
							String [] data = Base64Coder.decodeString(datas[i]).split(";");
							
							short usr_ID_recv = Short.parseShort(data[0]);
							short local_ID_recv = Short.parseShort(data[1]);
							short type_recv = Short.parseShort(data[2]);
							
							if (usr_ID_recv != MainActivity.usr_ID) {
								drawPath(data[3].split(","));
								/*String [] detail = data[3].split(",");
								Paint paint = new Paint();
								paint.setColor(Integer.parseInt(detail[0]));
								draw(paint, Float.parseFloat(detail[1])*width, 
										Float.parseFloat(detail[2])*height, 
										Float.parseFloat(detail[3])*width, 
										Float.parseFloat(detail[4])*height);
								actionHistory.add(usr_ID_recv, local_ID_recv, type_recv, data[3]);*/
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
			}
		});
		
		instance=this;
		bm = Bitmap.createBitmap(getWindow().getWindowManager()
				.getDefaultDisplay().getWidth(), getWindow().getWindowManager()
				.getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
		tempbm = Bitmap.createBitmap(getWindow().getWindowManager()
				.getDefaultDisplay().getWidth(), getWindow().getWindowManager()
				.getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
		temp = new Canvas(tempbm);
		//list = new ArrayList<MainActivity.History>();
		c = new Canvas(bm);
		dw = new DrawView(this);
		path = new Path();
		setContentView(dw);
		GlobalS.getinstance().mPaint.setColor(Color.BLACK);
		GlobalS.getinstance().mPaint.setAntiAlias(true);
		GlobalS.getinstance().mPaint.setStyle(Paint.Style.STROKE);
		GlobalS.getinstance().mPaint.setStrokeCap(Paint.Cap.ROUND);
		GlobalS.getinstance().mPaint.setStrokeWidth(0);
		//down = false;
		
		width = getWindowManager().getDefaultDisplay().getWidth();
		height = getWindowManager().getDefaultDisplay().getHeight();
		
		dw.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					path.lineTo(x, y);
					//down = false;
					//drawOn(1, event.getX(), event.getY());
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
					//down = true;
					break;

				case MotionEvent.ACTION_MOVE:

//					if (down) {
//						if (type < 3)
//							drawOn(0, event.getX(), event.getY());
//						else {

							//drawOn(1, event.getX(), event.getY());
					path.quadTo(x, y, (x + event.getX())/2, (y + event.getY())/2);
					draw(x, y);
					x = event.getX();
					y = event.getY();
//						}
//					}
					break;
				}

				//return type == 3;
				return true;
			}
		});

		//registerForContextMenu(dw);

		dw.setBackgroundColor(Color.WHITE);

	}
}