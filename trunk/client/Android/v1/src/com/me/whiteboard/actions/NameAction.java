package com.me.whiteboard.actions;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.me.whiteboard.MainActivity;

public class NameAction extends Action {

	public String name;

	public NameAction() {
		super();
		type = TYPE_NAME;
	}

	public NameAction(short usr_ID, short local_ID, String name) {
		super(TYPE_NAME, usr_ID, local_ID);
		this.name = name;
	}

	@Override
	public void act(MainActivity acts, Canvas canvas) {
	}

	@Override
	public byte[] privateToBytes() {
		return name.getBytes();
	}

	@Override
	protected void bytesToPrivate(byte[] bytes) {
		name = new String(bytes);
	}

	@Override
	public View getView(LayoutInflater mLayoutInflater, int mResource,
			View convertView, ViewGroup parent, boolean selected) {
		return null;
	}

}
