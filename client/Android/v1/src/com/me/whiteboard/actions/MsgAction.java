package com.me.whiteboard.actions;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.me.whiteboard.MainActivity;
import com.me.whiteboard.MyData;
import com.me.whiteboard.R;

public class MsgAction extends Action {

	// static Charset utf8 = Charset.forName("UTF-8");
	public String msg;

	public MsgAction() {
		super();
		this.type = TYPE_MSG;
	}

	public MsgAction(short usr_ID, short local_ID, String msg) {
		super(TYPE_MSG, usr_ID, local_ID);
		this.msg = msg;
	}

	@Override
	public void act(MainActivity context, Canvas canvas) {
	}

	@Override
	public byte[] privateToBytes() {
		return msg.getBytes();
	}

	@Override
	protected void bytesToPrivate(byte[] bytes) {
		msg = new String(bytes);
	}

	@Override
	public View getView(LayoutInflater mLayoutInflater, int mResource,
			View convertView, ViewGroup parent, boolean selected) {
		View v = mLayoutInflater.inflate(mResource, null);
		String name = MyData.getInstance().nametable.get((Short) usr_ID);
		if (name == null) {
			name = "User" + usr_ID;
		}
		((TextView) v.findViewById(R.id.msg_item_who_tv)).setText(name);
		((TextView) v.findViewById(R.id.msg_item_content_tv)).setText(msg);
		return v;
	}
}
