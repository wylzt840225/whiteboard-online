package com.me.whiteboard;

import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Canvas;

public abstract class Action {
	static ByteArrayBuffer baf=new ByteArrayBuffer(10000);
	public short usr_ID;
	public short local_ID;
	public long time;
	public short type;
	
	public abstract void doAction(MainActivity act,Canvas canvas);
	public abstract byte[] ToByte();
	private byte[] PrivateToByte()
	{
		
	
		return null;
		
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
