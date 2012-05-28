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
import com.me.whiteboard.actions.ClearAction;
import com.me.whiteboard.actions.MsgAction;
import com.me.whiteboard.actions.PathAction;
import com.me.whiteboard.actions.RedoAction;
import com.me.whiteboard.actions.TextAction;
import com.me.whiteboard.actions.UndoAction;
import com.me.whiteboard.compat.ActionBarActivity;
import com.me.whiteboard.compat.ColorPickerDialog;
import com.me.whiteboard.compat.ColorPickerDialog.OnColorChangedListener;
import com.me.whiteboard.http.Client;
import com.me.whiteboard.http.Client.GetData;
import com.me.whiteboard.http.Client.onNewDataRecv;
import com.me.whiteboard.http.Client.onSend;

public class MainActivity extends ActionBarActivity {
	
	public static final int TYPE_LINE = 1;
	public static final int TYPE_TEXT = 2;
	public static final int TYPE_ERASER = 3;

	Bitmap bmp;
	Canvas canvas;
	public static Paint paint;
	DrawView dw;
	int type = TYPE_LINE;
	Action acting;
	WindowManager wm;
	// WindowManager.LayoutParams wmParams;
	View floatview;
	static Sender sender;
	GetData g;
	boolean animate;

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
							action.addMeToList();
							action.act(MainActivity.this, canvas);
							FlushCanvas();
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
			canvas.drawColor(Color.DKGRAY);

			// int srcLeft = (int) Display.x_ScreenPosToBmpPos(0),
			// dstLeft = 0,
			// srcTop = (int) Display.y_ScreenPosToBmpPos(0),
			// dstTop = 0,
			// srcRight = (int)
			// Display.x_ScreenPosToBmpPos(Display.screen_width),
			// dstRight = (int) Display.screen_width,
			// srcBottom = (int)
			// Display.y_ScreenPosToBmpPos(Display.screen_height),
			// dstBottom = (int) Display.screen_height;
			// if (Display.x_RelativeToAbsolute(0) < 0) {
			// srcLeft = (int) Display.x_AbsoluteToBmpPos(0);
			// dstLeft = (int) Display.x_AbsoluteToRelative(0);
			// }
			// if (Display.y_RelativeToAbsolute(0) < 0) {
			// srcTop = (int) Display.y_AbsoluteToBmpPos(0);
			// dstTop = (int) Display.y_AbsoluteToRelative(0);
			// }
			// if (Display.x_RelativeToAbsolute(Display.screen_width) >
			// Display.screen_width) {
			// srcRight = (int)
			// Display.x_AbsoluteToBmpPos(Display.screen_width);
			// dstRight = (int)
			// Display.x_AbsoluteToRelative(Display.screen_width);
			// }
			// if (Display.y_RelativeToAbsolute(Display.screen_height) >
			// Display.screen_height) {
			// srcBottom = (int)
			// Display.y_AbsoluteToBmpPos(Display.screen_height);
			// dstBottom = (int)
			// Display.y_AbsoluteToRelative(Display.screen_height);
			// }
			//
			// Rect srcRect = new Rect(srcLeft, srcTop, srcRight, srcBottom),
			// dstRect = new Rect(dstLeft, dstTop, dstRight, dstBottom);
			// canvas.drawBitmap(bmp, srcRect, dstRect, null);

			Rect dstRect = new Rect((int) Display.x_BmpPosToScreenPos(0),
					(int) Display.y_BmpPosToScreenPos(0),
					(int) Display.x_BmpPosToScreenPos((int) 
							(Display.screen_width * Display.bmpScale)),
					(int) Display.y_BmpPosToScreenPos((int) 
							(Display.screen_height * Display.bmpScale)));
			canvas.drawBitmap(bmp, null, dstRect, null);

			if (acting != null) {
				acting.act(MainActivity.this, canvas);
				FlushCanvas();
			}
		}
	}

	public void FlushCanvas() {
		if (dw != null) {
			dw.postInvalidate();
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
		case R.id.colorss:
			final MyListener listener = new MyListener();
			ColorPickerDialog colorPickerDialog = new ColorPickerDialog(
					MainActivity.this, listener,MainActivity.paint.getColor(), MainActivity.paint.getAlpha());
			colorPickerDialog.show();
			return true;
		case R.id.clearss:
			ClearAction clearAction = new ClearAction(
					MyData.getInstance().usr_ID, MyData.getInstance().local_ID);
			addAction(clearAction);
			return true;
		case R.id.undoss:
			UndoAction undoAction = new UndoAction(MyData.getInstance().usr_ID,
					MyData.getInstance().local_ID);
			addAction(undoAction);
			return true;
		case R.id.redoss:
			RedoAction redoAction = new RedoAction(MyData.getInstance().usr_ID,
					MyData.getInstance().local_ID);
			addAction(redoAction);
			return true;
//		case R.id.textss:
//			type = TYPE_TEXT;
//			acting = new TextAction(MyData.getInstance().usr_ID, 
//									MyData.getInstance().local_ID, 
//									"Text", paint);
//			FlushCanvas();
//			return true;
		case R.id.exit:
			exitRoom();
			return true;
		}

		return false;

	}

	public void exitRoom() {
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
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

		animate = false;

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
				MsgAction msg = new MsgAction(MyData.getInstance().usr_ID,
						MyData.getInstance().local_ID, ((EditText) floatview
								.findViewById(R.id.msg_content_et)).getText()
								.toString());
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

				dw = new DrawView(MainActivity.this);
				rePaint();

				((ViewGroup) findViewById(R.id.draw)).addView(dw, lp);
				paint = new Paint();
				paint.setColor(Color.BLACK);
				paint.setAntiAlias(true);
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeCap(Paint.Cap.ROUND);
				paint.setStrokeWidth((float) (Display.screen_width * 0.005));
				paint.setTextSize((float) (Display.screen_width * 0.1));
				paint.setTextAlign(Paint.Align.CENTER);

				dw.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						int pointCount = event.getPointerCount();
						
						switch (type) {
						case TYPE_TEXT:
							if (pointCount == 1 && event.getAction() == MotionEvent.ACTION_DOWN) {
								Display.previousPointCount = 0;
							}
							float x_mean_text = 0, y_mean_text = 0, sumOfLength_text = 0;
							for (int i = 0; i < pointCount; i++) {
								x_mean_text += event.getX(i);
								y_mean_text += event.getY(i);
							}
							x_mean_text /= pointCount;
							y_mean_text /= pointCount;
							for (int i = 0; i < pointCount; i++) {
								sumOfLength_text += Math.sqrt(Math.pow(event.getX(i)
										- x_mean_text, 2)
										+ Math.pow(event.getY(i) - y_mean_text, 2));
							}

							((TextAction) acting).update(pointCount, x_mean_text, y_mean_text,
									sumOfLength_text);
							break;
						default:
							if (pointCount == 1) {
								if (Display.previousPointCount > 1) {
									animate = true;
									new Thread(new Runnable() {
										public void run() {
											Display.animate(MainActivity.this);
											animate = false;
											rePaint();
										}
									}).start();
									FlushCanvas();
									// reSize();
								}
								switch (event.getAction()) {
								case MotionEvent.ACTION_UP:
									if (acting != null) {
										if (((PathAction) acting).getHistoryCount() > 1) {
											addAction(acting);
										}
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
									if (((PathAction) acting).getHistoryCount() > 1
											&& (System.currentTimeMillis() - acting.time) > 1000) {
										addAction(acting);
									}
									acting = null;
								}

								float x_mean_path = 0, y_mean_path = 0, sumOfLength_path = 0;
								for (int i = 0; i < pointCount; i++) {
									x_mean_path += event.getX(i);
									y_mean_path += event.getY(i);
								}
								x_mean_path /= pointCount;
								y_mean_path /= pointCount;
								for (int i = 0; i < pointCount; i++) {
									sumOfLength_path += Math.sqrt(Math.pow(event.getX(i)
											- x_mean_path, 2)
											+ Math.pow(event.getY(i) - y_mean_path, 2));
								}

								Display.update(pointCount, x_mean_path, y_mean_path,
										sumOfLength_path);
							}
							break;
						}

						// //for testing point count
						// android.util.Log.v("pre",
						// Integer.toString(Display.previousPointCount));
						// android.util.Log.v("now",
						// Integer.toString(pointCount));

						Display.previousPointCount = pointCount;
						FlushCanvas();
						return true;
					}
				});
				// registerForContextMenu(dw);

				// dw.setBackgroundColor(Color.WHITE);
			}
		});
	}

	public void clear() {
		bmp = Bitmap.createBitmap((int) (Display.screen_width * Display.bmpScale), 
				(int) (Display.screen_height * Display.bmpScale),
				Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bmp);
		canvas.drawColor(Color.WHITE);
	}

	public void rePaint() {
		Display.reScaleBmp();
		clear();
		Display.reSize();
		MyData.getInstance().actionList.actAll(MainActivity.this, canvas);
		FlushCanvas();
	}

	private void addAction(Action acting) {
		MyData.getInstance().local_ID++;
		acting.addMeToList();
		acting.act(MainActivity.this, canvas);
		sender.add(acting);
		sender.Flush();
	}
}

class MyListener implements OnColorChangedListener {

	public void colorChanged(int color,int alpha) {
		MainActivity.paint.setColor(color);
		MainActivity.paint.setAlpha(alpha);
	}
}