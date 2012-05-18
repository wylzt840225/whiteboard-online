package com.me.whiteboard.actions;

import java.nio.charset.Charset;

import com.me.whiteboard.MainActivity;
import com.me.whiteboard.MyData;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MsgAction extends Action {

	static Charset utf8 = Charset.forName("UTF-8");
	String Msg;

	@Override
	public void act(MainActivity context,Canvas canvas) {
		MyData.getInstance().msgList.add(this);
	}

	@Override
	public byte[] privateToBytes() {
		return Msg.getBytes(utf8);
	}
	
	@Override
	protected void bytesToPrivate(byte[] bytes) {
		Msg = new String(bytes, utf8);
	}

	@Override
	public View getView(LayoutInflater mLayoutInflater, int mResource,
			View convertView, ViewGroup parent, boolean selected) {
		return null;
	}
}
