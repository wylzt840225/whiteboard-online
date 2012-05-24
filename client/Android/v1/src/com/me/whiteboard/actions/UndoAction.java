package com.me.whiteboard.actions;

import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.me.whiteboard.MainActivity;

public class UndoAction extends Action {
	
	private short undo_local_ID;
	
	public UndoAction() {
		super();
		type = Action.TYPE_UNDO;
	}
	
	//撤销上一步
	public UndoAction(short usr_ID, short local_ID) {
		super(Action.TYPE_UNDO, usr_ID, local_ID);
		undo_local_ID = (short) (local_ID - 1);
	}
	
	//撤销指定动作
	public UndoAction(short usr_ID, short local_ID, short undo_local_ID) {
		super(Action.TYPE_UNDO, usr_ID, local_ID);
		this.undo_local_ID = undo_local_ID;
	}

	@Override
	public void act(MainActivity activity, Canvas canvas) {
		int undoIndex = ActionList.findIndex(usr_ID, undo_local_ID);
		if (undoIndex > 0) {
			ActionList.list.get(undoIndex).valid = false;
		}
		valid = false;//只执行一遍，增加效率
		
		if (ActionList.list.get(undoIndex).type == TYPE_CLEAR) {
			ActionList.minDisplayIndex = 0;
		}
		
		activity.rePaint();
	}

	@Override
	public byte[] privateToBytes() {
		ByteArrayBuffer baf = new ByteArrayBuffer(2);
		byte[] bytes = shortToBytes(undo_local_ID);
		baf.append(bytes, 0, bytes.length);
		return baf.toByteArray();
	}

	@Override
	protected void bytesToPrivate(byte[] bytes) {
		undo_local_ID = bytesToShort(bytes[0], bytes[1]);
	}

	@Override
	public View getView(LayoutInflater mLayoutInflater, int mResource,
			View convertView, ViewGroup parent, boolean selected) {
		return null;
	}

}
