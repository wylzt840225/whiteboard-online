package com.me.whiteboard;

import java.nio.charset.Charset;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MsgAction extends Action {

	static Charset utf8=Charset.forName("UTF-8");
	String Msg;
	@Override
	public void doAction(MainActivity act, Canvas canvas) {
		MyData.getInstance().msgList.Add(this);
	}

	@Override
	public byte[] ToByte() {
		return Msg.getBytes(utf8);
	}

	@Override
	public View getView(LayoutInflater mLayoutInflater, int mResource,
			View convertView, ViewGroup parent, boolean selected) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setData(byte[] bs) {
		
		Msg=new String(bs,  utf8);
		}

}
