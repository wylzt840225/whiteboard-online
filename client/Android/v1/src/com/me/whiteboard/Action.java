package com.me.whiteboard;



import java.util.Arrays;

import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class Action {
	
	static final int PATH=0;
	static final int MSG=1;
	static final int NAME=2;
	
	static ByteArrayBuffer baf=new ByteArrayBuffer(10000);
	public short usr_ID;
	public short local_ID;
	public long time;
	public short type;
	
	public void PrivateDecode(byte[] bs)
	{
		
	}
	public abstract void setData(byte[] bs);
	public abstract void doAction(MainActivity act,Canvas canvas);
	public abstract byte[] ToByte();
	public abstract View getView( LayoutInflater mLayoutInflater, int mResource, View convertView, ViewGroup parent,boolean selected);
	private byte[] PrivateToByte()
	{
		
		byte[] bs=new byte[6];
		
		bs[0]=(byte) (type>>8);
		bs[1]=(byte) (type%256);
		
		bs[2]=(byte) (usr_ID>>8);
		bs[3]=(byte) (usr_ID%8);
		
		bs[4]=(byte) (local_ID>>8);
		bs[5]=(byte) (local_ID%8);
		return bs;
		
	}
	static Action Base64ToAction(String base64)
	{
		byte[] bs=Base64Coder.decode(base64);
		short type=(short) (bs[0]<<8+bs[1]);
		Action a = null;
		switch(type)
		{
		case PATH:
			break;
		case MSG:
			a=new MsgAction();
		}
		a.PrivateDecode(bs);
		byte[] bs2=Arrays.copyOfRange(bs, 6, bs.length);
		if(a!=null)
			a.setData(bs2);
		return a;
		
	}
	public String Base64()
	{
		baf.clear();
		byte[] bs=ToByte();
		baf.append(bs, 0, bs.length);
		bs=PrivateToByte();
		baf.append(bs, 0, bs.length);
		return new String(Base64Coder.encode(baf.toByteArray()));
	}
}
