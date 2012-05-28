package com.me.whiteboard.compat;

import android.os.Bundle;
import android.app.*;
import android.content.Context;
import android.graphics.*;
import android.graphics.Shader.TileMode;
import android.util.*;
import android.view.*;
import android.view.ViewGroup.*;

public class ColorPickerDialog extends Dialog
{

	public interface OnColorChangedListener
	{
		void colorChanged(int color, int alpha);
	}

	private OnColorChangedListener mListener;
	private int mInitialColor;
	private int mInitialAlpha;
	private static class ColorPickerView extends View
	{
		private Paint mPaint,bwPaint;
		private Paint mCenterPaint;
		private final int[] mColors,bwColors;
		private OnColorChangedListener mListener;
		static int PAINT_WIDTH=40;
		ColorPickerView(Context c,OnColorChangedListener l,int color, int alpha)
		{
			super(c);
			CENTER_X=dm.widthPixels/2;
			CENTER_Y=dm.heightPixels/2;
			CENTER_RADIUS=(int) ((int)java.lang.Math.min(CENTER_X, CENTER_Y)/3.6f);
			CENTER_RANGE=CENTER_RADIUS*2;
			
			mListener = l;
			mColors = new int[]{0xFFFF0000,0xFFFF00FF,0xFF0000FF,0xFF00FFFF,
					0xFF00FF00,0xFFFFFF00,0xFFFF0000};
			bwColors=new int[16];bwColors[0]=0;bwColors[1]=0x11111111;
			for(int i=2;i<16;i++){bwColors[i]=i*bwColors[1];}
			Shader s = new SweepGradient((float)CENTER_X, (float)CENTER_Y, mColors, null);
			Shader bw= new LinearGradient(0, 0, dm.widthPixels, CENTER_RADIUS, bwColors, null, TileMode.REPEAT);

			bwPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			bwPaint.setShader(bw);
			bwPaint.setStyle(Paint.Style.STROKE);
			bwPaint.setStrokeWidth(PAINT_WIDTH);
			bwPaint.setAlpha(255);
			
			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint.setShader(s);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(PAINT_WIDTH);

			mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mCenterPaint.setColor(color);
			mCenterPaint.setAlpha(alpha);
			mCenterPaint.setStrokeWidth(5);
			
		}

		private boolean mTrackingCenter;
		private boolean mHighlightCenter;
		private float BW=1.0f;

		@Override
		protected void onDraw(Canvas canvas)
		{
			float rx = CENTER_X;//-mPaint.getStrokeWidth()*0.5f;
			float ry = CENTER_Y;//-mPaint.getStrokeWidth()*0.5f;
			canvas.drawOval(new RectF(rx-CENTER_RANGE, ry-CENTER_RANGE, rx+CENTER_RANGE, ry+CENTER_RANGE), mPaint);
			canvas.drawCircle(rx, ry, CENTER_RADIUS, mCenterPaint);
			
			canvas.drawRect(0,0,dm.widthPixels,0, bwPaint);

			if (mTrackingCenter)
			{
				int c = mCenterPaint.getColor();
				mCenterPaint.setStyle(Paint.Style.STROKE);
/*
				if (mHighlightCenter)
				{
					mCenterPaint.setAlpha(0xFF);
				}
				else
				{
					mCenterPaint.setAlpha(0x80);
				}
*/
				canvas.drawCircle(rx, ry,
						CENTER_RADIUS+mCenterPaint.getStrokeWidth(),
						mCenterPaint);

				mCenterPaint.setStyle(Paint.Style.FILL);
				mCenterPaint.setColor(c);
			}
		}

		@Override
		protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec)
		{
			setMeasuredDimension(CENTER_X*2, CENTER_Y*2);
		}

		private static int CENTER_X;
		private static int CENTER_Y;
		private static int CENTER_RADIUS;
		private static int CENTER_RANGE;

		private int floatToByte(float x)
		{
			int n = java.lang.Math.round(x);
			return n;
		}

		private int pinToByte(int n)
		{
			if (n<0)
			{
				n = 0;
			}
			else if (n>255)
			{
				n = 255;
			}
			return n;
		}

		private int ave(int s,int d,float p)
		{
			return s+java.lang.Math.round(p*(d-s));
		}

		private int interpColor(int colors[],float unit)
		{
			if (unit<=0)
			{
				return colors[0];
			}
			if (unit>=1)
			{
				return colors[colors.length-1];
			}

			float p = unit*(colors.length-1);
			int i = (int) p;
			p -= i;

			// now p is just the fractional part [0...1) and i is the index
			int c0 = colors[i];
			int c1 = colors[i+1];
			int a = ave(Color.alpha(c0), Color.alpha(c1), p);
			int r = ave(Color.red(c0), Color.red(c1), p);
			int g = ave(Color.green(c0), Color.green(c1), p);
			int b = ave(Color.blue(c0), Color.blue(c1), p);

			return Color.argb(a, r, g, b);
		}

		@SuppressWarnings("unused")
		private int rotateColor(int color,float rad)
		{
			float deg = rad*180/3.1415927f;
			int r = Color.red(color);
			int g = Color.green(color);
			int b = Color.blue(color);

			ColorMatrix cm = new ColorMatrix();
			ColorMatrix tmp = new ColorMatrix();

			cm.setRGB2YUV();
			tmp.setRotate(0, deg);
			cm.postConcat(tmp);
			tmp.setYUV2RGB();
			cm.postConcat(tmp);

			final float[] a = cm.getArray();

			int ir = floatToByte(a[0]*r+a[1]*g+a[2]*b);
			int ig = floatToByte(a[5]*r+a[6]*g+a[7]*b);
			int ib = floatToByte(a[10]*r+a[11]*g+a[12]*b);

			return Color.argb(Color.alpha(color), pinToByte(ir), pinToByte(ig),
					pinToByte(ib));
		}

		private static final float PI = 3.1415926f;

		@Override
		public boolean onTouchEvent(MotionEvent event)
		{
			
			float x = event.getX()-(float)CENTER_X;
			float y = event.getY()-(float)CENTER_Y;
			boolean inCenter = java.lang.Math.sqrt(x*x+y*y)<=(CENTER_RANGE+PAINT_WIDTH);
			boolean bw = (boolean)(event.getY()<CENTER_RADIUS);
			switch (event.getAction())
			{
			case MotionEvent.ACTION_DOWN:
				mTrackingCenter = inCenter;
				if (inCenter)
				{
					mHighlightCenter = true;
					invalidate();
					break;
				}
				if(bw)
				{
					BW=((float)event.getX()/(float)dm.widthPixels);
					mCenterPaint.setAlpha((int)(BW*0xFF));
					invalidate();
					break;
				}
			case MotionEvent.ACTION_MOVE:
				if (!mTrackingCenter)
				{
					if (mHighlightCenter!=inCenter)
					{
						mHighlightCenter = inCenter;
						invalidate();
					}
				}
				else
				{
					float angle = (float) java.lang.Math.atan2(y, x);
					// need to turn angle [-PI ... PI] into unit [0....1]
					float unit = angle/(2*PI);
					if (unit<0)
					{
						unit += 1;
					}
					mCenterPaint.setColor(interpColor(mColors, unit));
					invalidate();
				}
				if(bw)
				{
					BW=((float)event.getX()/(float)dm.widthPixels);
					mCenterPaint.setAlpha((int)(BW*0xFF));
					invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				if (mTrackingCenter)
				{
					if (java.lang.Math.sqrt(x*x+y*y)<=CENTER_RADIUS)
					{
						mListener.colorChanged(mCenterPaint.getColor(),mCenterPaint.getAlpha());
					}
					mTrackingCenter = false; // so we draw w/o halo
					invalidate();
				}
				if(bw)
				{
					BW=((float)event.getX()/(float)dm.widthPixels);
					mCenterPaint.setAlpha((int)(BW*0xFF));
					invalidate();
				}
				break;
			}
			return true;
		}
	}

	public ColorPickerDialog(Context context,OnColorChangedListener listener,
			int initialColor,int alpha)
	{
		super(context);

		mListener = listener;
		mInitialColor = initialColor;
		mInitialAlpha= alpha;
	}

	static private DisplayMetrics dm=new DisplayMetrics();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		OnColorChangedListener l = new OnColorChangedListener()
		{
			public void colorChanged(int color, int alpha)
			{
				mListener.colorChanged(color,alpha);
				dismiss();
			}
		};
		LayoutParams params=getWindow().getAttributes(); 
		getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
		setContentView(new ColorPickerView(getContext(), l, mInitialColor,mInitialAlpha));
		
        params.height =dm.heightPixels;
        params.width = dm.widthPixels;   
        
        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
		setTitle("Ñ¡È¡ÑÕÉ«");
	}
}