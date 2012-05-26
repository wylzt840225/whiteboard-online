package com.me.whiteboard.actions;

import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.me.whiteboard.MainActivity;
import com.me.whiteboard.MyData;

public class RedoAction extends Action {

	private short redo_local_ID;

	public RedoAction() {
		super();
		type = TYPE_REDO;
	}

	// 撤销上一步
	public RedoAction(short usr_ID, short local_ID) {
		super(TYPE_REDO, usr_ID, local_ID);
		redo_local_ID = ActionList.minRedoLocalID;
		while (! ifCanRedo(redo_local_ID) && redo_local_ID < local_ID) {
			redo_local_ID ++;
		}
	}

	// 撤销指定动作
	public RedoAction(short usr_ID, short local_ID, short redo_local_ID) {
		super(Action.TYPE_REDO, usr_ID, local_ID);
		this.redo_local_ID = redo_local_ID;
	}

	@Override
	public void act(MainActivity activity, Canvas canvas) {
		valid = false;// 只执行一遍
		if (redo_local_ID >= local_ID) {
			return;
		}
		int redoIndex = MyData.getInstance().actionList.findIndex(usr_ID, redo_local_ID);
		if (redoIndex >= 0) {
			MyData.getInstance().actionList.list.get(redoIndex).valid = true;
			if (MyData.getInstance().actionList.list.get(redoIndex).type == TYPE_CLEAR) {
				ActionList.minDisplayIndex = 0;
			}
		}
		activity.rePaint();
	}

	@Override
	public byte[] privateToBytes() {
		ByteArrayBuffer baf = new ByteArrayBuffer(2);
		byte[] bytes = shortToBytes(redo_local_ID);
		baf.append(bytes, 0, bytes.length);
		return baf.toByteArray();
	}

	@Override
	protected void bytesToPrivate(byte[] bytes) {
		redo_local_ID = bytesToShort(bytes[0], bytes[1]);
	}

	@Override
	public View getView(LayoutInflater mLayoutInflater, int mResource,
			View convertView, ViewGroup parent, boolean selected) {
		return null;
	}
	
	private boolean ifCanRedo(short local_ID) {
		int index = MyData.getInstance().actionList.findIndex(usr_ID, local_ID);
		
		if (index < 0) {
			return false;
		}
		
		switch (MyData.getInstance().actionList.list.get(index).type) {
		case Action.TYPE_PATH:
		case Action.TYPE_CLEAR:
		case Action.TYPE_TEXT:
			if (MyData.getInstance().actionList.list.get(index).valid) {
				return false;
			} else {
				return true;
			}
		default:
			return false;
		}
	}
}
