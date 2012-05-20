package com.me.whiteboard;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.me.whiteboard.ChatActivity.MsgSend;
import com.me.whiteboard.actions.Action;
import com.me.whiteboard.actions.MsgAction;
import com.me.whiteboard.actions.PathAction;
import com.me.whiteboard.compat.ActionBarActivity;
import com.me.whiteboard.http.Client;
import com.me.whiteboard.http.Client.GetData;
import com.me.whiteboard.http.Client.onNewDataRecv;
import com.me.whiteboard.http.Client.onSend;

public class MainActivity extends ActionBarActivity {

	Bitmap bmp;
	Canvas canvas;
	public static Paint paint;
	DrawView dw;
	int type = 1;
	Action acting;
	WindowManager wm;
	// WindowManager.LayoutParams wmParams;
	View floatview;
	static Sender sender;
	GetData g;

	static MainActivity instance;

	static MainActivity getinstance() {
		return instance;
	}

	protected void onPause() {
		super.onPause();
		if (g != null)
			g.cancel(false);
	}

	protected void onResume() {
		super.onResume();
		g = Client.setOnDataRecv(MyData.getInstance().room,
				new onNewDataRecv() {
					public void onRecv(final String[] datas) {

						Action action;
						for (int i = 0; i < datas.length; i++) {
							action = Action.base64ToAction(datas[i]);
							if (action.usr_ID != MyData.getInstance().usr_ID) {
								action.act(MainActivity.this, canvas);
							}
							action.addMeToList();
						}

					}
				});
	}

	class Sender {
		ArrayList<Action> toSend;
		ArrayList<String> reSend;

		Sender() {
			toSend = new ArrayList<Action>();
			reSend = new ArrayList<String>();
		}

		public void Flush() {
			MainActivity.this.getActionBarHelper().setRefreshActionItemState(
					true);
			new Thread(new Runnable() {
				public void run() {
					StringBuilder bl = new StringBuilder();
					for (int i = 0; i < toSend.size(); i++) {
						bl.append(toSend.get(i).toBase64());
						bl.append(',');
					}
					toSend.clear();
					for (int i = 0; i < reSend.size(); i++) {
						bl.append(reSend);
						bl.append(',');
					}
					reSend.clear();
					String b = bl.toString();
					if (b.endsWith(",")) {
						b = b.substring(0, b.length() - 1);
					}

					Client.SendData(MyData.getInstance().room, b, new onSend() {

						public void SendOK() {
							MainActivity.this.getActionBarHelper()
									.setRefreshActionItemState(false);
						}

						public void SendError(String Data) {
							MainActivity.this.getActionBarHelper()
									.setRefreshActionItemState(false);
							reSend.add(Data);

						}
					});

				}
			}).start();

		}

		public void add(Action action) {
			toSend.add(action);
		}

	}

	class DrawView extends View {
		public DrawView(Context context) {
			super(context);

		}

		public void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			if (Display.previousPointCount > 1) {
				Rect srcRect = new Rect((int) Display.x_ScreenPosToBmpPos(0),
						(int) Display.y_ScreenPosToBmpPos(0),
						(int) Display.x_ScreenPosToBmpPos(Display.screen_width),
						(int) Display.y_ScreenPosToBmpPos(Display.screen_height));
				Rect dstRect = new Rect(0, 0, Display.screen_width,
						Display.screen_height);
				canvas.drawBitmap(bmp, srcRect, dstRect, null);
			} else {
				canvas.drawBitmap(bmp, 0, 0, null);
				if (acting != null) {
					acting.act(MainActivity.this, canvas);
				}
			}
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			return true;
		case R.id.chat:
			DisplayMetrics metrics = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(metrics);
			if (metrics.heightPixels >= getResources().getDimensionPixelSize(
					R.dimen.chat_window_least_height)) {
				showChatWindow();
			} else {
				Intent i = new Intent();
				i.setClass(this, ChatActivity.class);
				startActivity(i);
			}
			return true;
		}
		return false;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public void FlushCanvas() {
		dw.invalidate();
	}

	private void updateViewPosition(float x, float y) {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) floatview
				.getLayoutParams();
		lp.topMargin = (int) (lp.topMargin + y - mTouchStartY);
		lp.leftMargin = (int) (lp.leftMargin + x - mTouchStartX);
		floatview.setLayoutParams(lp);
	}

	float mTouchStartX, mTouchStartY;
	boolean chatwindow = false;

	public void showChatWindow() {

		ViewGroup vg = (ViewGroup) findViewById(R.id.framelayout);
		if (chatwindow)
			closeChatWindow();
		vg.addView(floatview);
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) floatview
				.getLayoutParams();
		lp.height = getResources().getDimensionPixelSize(
				R.dimen.chat_window_height);
		lp.width = getResources().getDimensionPixelSize(
				R.dimen.chat_window_width);
		floatview.setLayoutParams(lp);
		chatwindow = true;

	}

	public void closeChatWindow() {
		if (chatwindow) {
			ViewGroup vg = (ViewGroup) findViewById(R.id.framelayout);
			vg.removeView(floatview);
			chatwindow = false;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(MyData.getInstance().room);

		sender = new Sender();

		setContentView(R.layout.canvas);

		wm = (WindowManager) getApplicationContext().getSystemService("window");
		// wmParams = new WindowManager.LayoutParams();
		floatview = getLayoutInflater().inflate(R.layout.chat_dialog, null);
		ListView lv = (ListView) floatview.findViewById(R.id.chatlist);
		MyData.getInstance().msgList.createAdapter(1, R.layout.msg_item);
		lv.setAdapter(MyData.getInstance().msgList.getAdapter(1, this));
		Button send = (Button) floatview.findViewById(R.id.send);
		floatview.findViewById(R.id.title).setOnTouchListener(
				new OnTouchListener() {

					public boolean onTouch(View v, MotionEvent event) {

						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN: // 捕获手指触摸按下动作
							// 获取相对View的坐标，即以此View左上角为原点
							mTouchStartX = event.getX();
							mTouchStartY = event.getY();

							break;

						case MotionEvent.ACTION_MOVE: // 捕获手指触摸移动动作
							updateViewPosition(event.getX(), event.getY());
							break;

						case MotionEvent.ACTION_UP: // 捕获手指触摸离开动作
							updateViewPosition(event.getX(), event.getY());
							mTouchStartX = mTouchStartY = 0;
							break;
						}
						return true;
					}

				});
		send.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				MsgAction msg = new MsgAction();
				msg.usr_ID = MyData.getInstance().usr_ID;
				msg.Msg = ((EditText) floatview
						.findViewById(R.id.msg_content_et)).getText()
						.toString();
				Client.SendData(MyData.getInstance().room, msg.toBase64(),
						new MsgSend(msg));
				((EditText) floatview.findViewById(R.id.msg_content_et))
						.setText("");
			}

		});
		floatview.findViewById(R.id.close).setOnClickListener(
				new OnClickListener() {

					public void onClick(View v) {
						closeChatWindow();
					}
				});

		findViewById(R.id.draw).post(new Runnable() {

			public void run() {
				new Display(findViewById(R.id.draw).getWidth(), findViewById(
						R.id.draw).getHeight());

				LayoutParams lp = new LayoutParams();
				lp.width = Display.screen_width;
				lp.height = Display.screen_height;

				// instance = MainActivity.this;
				bmp = Bitmap.createBitmap(Display.screen_width,
						Display.screen_height, Bitmap.Config.ARGB_8888);
				canvas = new Canvas(bmp);
				dw = new DrawView(MainActivity.this);

				((ViewGroup) findViewById(R.id.draw)).addView(dw, lp);
				paint = new Paint();
				paint.setColor(Color.BLACK);
				paint.setAntiAlias(true);
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeCap(Paint.Cap.ROUND);
				paint.setStrokeWidth(5);

				dw.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						int pointCount = event.getPointerCount();
						if (pointCount == 1) {
							if (Display.previousPointCount > 1) {
								Display.previousPointCount = 1;
								reSize();
							}
							switch (event.getAction()) {
							case MotionEvent.ACTION_UP:
								if (acting != null) {
									addAction();
									acting = null;
								}
								break;
							case MotionEvent.ACTION_DOWN:
								acting = new PathAction(
										MyData.getInstance().usr_ID, MyData
												.getInstance().local_ID, paint);
								((PathAction) acting).addPoint(event.getX(),
										event.getY());
								break;
							case MotionEvent.ACTION_MOVE:
								if (acting != null) {
									((PathAction) acting).addPoint(
											event.getX(), event.getY());
								}
								break;
							}
						} else {
							if (acting != null) {
								if (((PathAction) acting).x_history.size() > 1
										&& (System.currentTimeMillis() - acting.time) > 1000) {
									addAction();
								}
								acting = null;
							}

							float x_mean = 0, y_mean = 0, sumOfLength = 0;
							for (int i = 0; i < pointCount; i++) {
								x_mean += event.getX(i);
								y_mean += event.getY(i);
							}
							x_mean /= pointCount;
							y_mean /= pointCount;
							for (int i = 0; i < pointCount; i++) {
								sumOfLength += Math.sqrt(Math.pow(event.getX(i)
										- x_mean, 2)
										+ Math.pow(event.getY(i) - y_mean, 2));
							}

							Display.update(pointCount, x_mean, y_mean,
									sumOfLength);

						}

						// for testing point count
						// android.util.Log.v("pre",
						// Integer.toString(Display.previousPointCount));
						// android.util.Log.v("now",
						// Integer.toString(pointCount));

						Display.previousPointCount = pointCount;
						dw.invalidate();
						return true;
					}
				});

				// registerForContextMenu(dw);

				dw.setBackgroundColor(Color.WHITE);
			}
		});
	}

	private void reSize() {
		bmp = Bitmap.createBitmap(Display.screen_width, Display.screen_height,
				Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bmp);
		MyData.getInstance().actionList.actAll(MainActivity.this, canvas);
		Display.reSize();
	}

	private void addAction() {
		MyData.getInstance().local_ID++;
		acting.act(MainActivity.this, canvas);
		MyData.getInstance().actionList.add(acting);
		sender.add(acting);
		sender.Flush();
	}
}