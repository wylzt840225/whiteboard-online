package com.me.whiteboard.actions;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.me.whiteboard.MainActivity;
import com.me.whiteboard.MyData;

public class NameAction extends Action {
	
	public String Name;
	public NameAction() {
		super();
		type=Action.TYPE_NAME;
	}
	
	@Override
	public void act(MainActivity acts, Canvas canvas) {
		MyData.getInstance().nametable.put(this.usr_ID, Name);
	}
	
	@Override
	public byte[] privateToBytes() {
		return Name.getBytes();
	}
	
	@Override
	protected void bytesToPrivate(byte[] bytes) {
		Name=new String(bytes);
	}

	@Override
	public View getView(LayoutInflater mLayoutInflater, int mResource,
			View convertView, ViewGroup parent, boolean selected) {
		return null;
	}

}
