package com.me.whiteboard.actions;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.me.whiteboard.MainActivity;

public class TextAction extends Action {

	private String text;

	public TextAction() {
		super();
		type = TYPE_TEXT;
	}

	public TextAction(short usr_ID, short local_ID) {
		super(TYPE_TEXT, usr_ID, local_ID);
	}

	@Override
	public void act(MainActivity activity, Canvas canvas) {
	}

	@Override
	public byte[] privateToBytes() {
		return null;
	}

	@Override
	protected void bytesToPrivate(byte[] bytes) {
	}

	@Override
	public View getView(LayoutInflater mLayoutInflater, int mResource,
			View convertView, ViewGroup parent, boolean selected) {
		return null;
	}

}
