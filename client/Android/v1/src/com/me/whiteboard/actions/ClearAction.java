package com.me.whiteboard.actions;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.me.whiteboard.MainActivity;

public class ClearAction extends Action {

	public ClearAction() {
	}

	public ClearAction(short usr_ID, short local_ID) {
		super(Action.TYPE_CLEAR, usr_ID, local_ID);
	}

	@Override
	public void act(MainActivity activity, Canvas canvas) {
		//Display.minDisplayTime = System.currentTimeMillis();
		ActionList.setMinDisplayIndex(this);
		activity.clear();
		activity.FlushCanvas();
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
