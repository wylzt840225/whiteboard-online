package com.me.whiteboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class ChatActivity extends Activity {
	private MyDialog dialog;
	private LinearLayout layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_dialog);
		dialog=new MyDialog(this);
		layout=(LinearLayout)findViewById(R.id.layout);
		layout.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		finish();
		return true;
	}
	
	class MyDialog extends AlertDialog {
		public MyDialog(Context context)
		{
			super(context);
		}
	}
}

