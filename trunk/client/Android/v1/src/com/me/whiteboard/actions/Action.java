package com.me.whiteboard.actions;

import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.me.whiteboard.MainActivity;
import com.me.whiteboard.MyData;
import com.me.whiteboard.compat.Base64Coder;

public abstract class Action {

	public static final short TYPE_NAME = 0;
	public static final short TYPE_PATH = 1;
	public static final short TYPE_MSG = 2;
	public static final short TYPE_CLEAR = 3;
	public static final short TYPE_UNDO = 4;
	public static final short TYPE_TEXT = 5;
	public static final short TYPE_REDO = 6;

	public short type;
	public short usr_ID;
	public short local_ID;
	public long time;
	public boolean valid;

	static ByteArrayBuffer baf = new ByteArrayBuffer(10000);

	public abstract void act(MainActivity activity, Canvas canvas);

	public abstract byte[] privateToBytes();

	protected abstract void bytesToPrivate(byte[] bytes);

	public abstract View getView(LayoutInflater mLayoutInflater, int mResource,
			View convertView, ViewGroup parent, boolean selected);

	public Action() {
		this.time = System.currentTimeMillis();
		// this.usr_ID = MyData.getInstance().usr_ID;
		this.valid = true;
	}

	public Action(short type, short usr_ID, short local_ID) {
		this.type = type;
		this.usr_ID = usr_ID;
		this.local_ID = local_ID;
		this.time = System.currentTimeMillis();
		this.valid = true;
	}

	private byte[] bytesToPublic(byte[] bytes) {
		type = bytesToShort(bytes[0], bytes[1]);
		usr_ID = bytesToShort(bytes[2], bytes[3]);
		local_ID = bytesToShort(bytes[4], bytes[5]);

		byte[] bytes_private = new byte[bytes.length - 6];
		System.arraycopy(bytes, 6, bytes_private, 0, bytes_private.length);
		return bytes_private;
	}

	protected byte[] publicToBytes() {
		byte[] bytes = new byte[6];
		System.arraycopy(shortToBytes(type), 0, bytes, 0, 2);
		System.arraycopy(shortToBytes(usr_ID), 0, bytes, 2, 2);
		System.arraycopy(shortToBytes(local_ID), 0, bytes, 4, 2);
		return bytes;
	}

	// add to msglist or actionlist
	public void addMeToList() {
		switch (type) {
		case Action.TYPE_MSG:
			MyData.getInstance().msgList.add(this);
			MyData.getInstance().msgList.notifyAllAdapter();
			break;
		case Action.TYPE_NAME:
			MyData.getInstance().nametable.put(usr_ID,
					((NameAction) (this)).name);
			break;
		default:
			if (usr_ID == MyData.getInstance().usr_ID) {
				if (MyData.getInstance().actionList.findIndex(usr_ID, local_ID) >= 0) {
					return;
				}
				if (type != TYPE_UNDO && type != TYPE_REDO) {
					ActionList.minRedoLocalID = local_ID;
				}
			}
			MyData.getInstance().actionList.add(this);
			break;
		}
	}

	public static Action base64ToAction(String base64String) {
		byte[] bytes = Base64Coder.decode(base64String);
		Action action = null;
		switch (bytesToShort(bytes[0], bytes[1])) {
		case TYPE_PATH:
			action = new PathAction();
			break;
		case TYPE_MSG:
			action = new MsgAction();
			break;
		case TYPE_NAME:
			action = new NameAction();
			break;
		case TYPE_CLEAR:
			action = new ClearAction();
			break;
		case TYPE_UNDO:
			action = new UndoAction();
			break;
		case TYPE_REDO:
			action = new RedoAction();
			break;
		case TYPE_TEXT:
			action = new TextAction();
			break;
		}
		if (action != null) {
			bytes = action.bytesToPublic(bytes);
			action.bytesToPrivate(bytes);
		}
		return action;
	}

	public String toBase64() {
		baf.clear();
		byte[] bytes = publicToBytes();
		if (bytes != null) {
			baf.append(bytes, 0, bytes.length);
		}
		bytes = privateToBytes();
		if (bytes != null) {
			baf.append(bytes, 0, bytes.length);
		}
		return new String(Base64Coder.encode(baf.toByteArray()));
	}

	protected static byte[] shortToBytes(short s) {
		byte[] shortBuf = new byte[2];
		for (int i = 0; i < 2; i++) {
			int offset = (shortBuf.length - 1 - i) * 8;
			shortBuf[i] = (byte) ((s >>> offset) & 0xff);
		}
		return shortBuf;
	}

	protected static final short bytesToShort(byte b1, byte b2) {
		return (short) ((b1 << 8) + (b2 & 0xFF));
	}

	protected static byte[] intToBytes(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			int offset = (b.length - 1 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}

	protected static final int bytesToInt(byte b1, byte b2, byte b3, byte b4) {
		return (b1 << 24) + ((b2 & 0xFF) << 16) + ((b3 & 0xFF) << 8)
				+ (b4 & 0xFF);
	}
}
