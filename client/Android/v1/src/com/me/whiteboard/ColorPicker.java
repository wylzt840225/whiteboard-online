package com.me.whiteboard;

import com.me.whiteboard.compat.ActionBarActivity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ColorPicker extends ActionBarActivity {
	int color;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final Paint mpaint=GlobalS.getinstance().mPaint;
		RadioButton rButton=null;
		color=mpaint.getColor();
		RadioGroup rg=(RadioGroup) findViewById(R.id.colors);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId)
				{
				case R.id.black:
					color=Color.BLACK;
					break;
				case R.id.red:
					color=Color.RED;
					break;
				case R.id.green:
					color=Color.GREEN;
					break;
				}
				
			}
		});
		switch(color)
		{
			case Color.BLACK:
				rButton=(RadioButton) findViewById(R.id.black);
				break;
			case Color.RED:
				rButton=(RadioButton) findViewById(R.id.red);
				break;
			case Color.GREEN:
				rButton=(RadioButton) findViewById(R.id.green);
				break;
			
		}
		if(rButton!=null)
			rButton.setChecked(true);

		Button b=(Button) findViewById(R.id.yes);
		b.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {

				GlobalS.getinstance().mPaint.setColor(color);
				ColorPicker.this.finish();
			}
		});
		b=(Button) findViewById(R.id.cancel);
		b.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				ColorPicker.this.finish();
			}
		});
	}
}
