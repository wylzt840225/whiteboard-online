package com.me.whiteboard;

import android.graphics.Paint;

public class GlobalS {
	static GlobalS instance=null;
	Paint mPaint=new Paint();
	static GlobalS getinstance()
	{
		if(instance==null)
			instance=new GlobalS();
		return instance;
	}
}
