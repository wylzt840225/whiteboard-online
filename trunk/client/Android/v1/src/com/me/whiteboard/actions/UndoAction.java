package com.me.whiteboard.actions;

import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.me.whiteboard.MainActivity;
import com.me.whiteboard.MyData;

public class UndoAction extends Action {

	private short undo_local_ID;

	public UndoAction() {
		super();
		type = TYPE_UNDO;
	}

	// ������һ��
	public UndoAction(short usr_ID, short local_ID) {
		super(TYPE_UNDO, usr_ID, local_ID);
		undo_local_ID = (short) (local_ID - 1);
	}

	// ����ָ������
	public UndoAction(short usr_ID, short local_ID, short undo_local_ID) {
		super(Action.TYPE_UNDO, usr_ID, local_ID);
		this.undo_local_ID = undo_local_ID;
	}

	@Override
	public void act(MainActivity activity, Canvas canvas) {
		int undoIndex = MyData.getInstance().actionList.findIndex(usr_ID, undo_local_ID);
		if (undoIndex > 0) {
			MyData.getInstance().actionList.list.get(undoIndex).valid = false;
			if (MyData.getInstance().actionList.list.get(undoIndex).type == TYPE_CLEAR) {
				ActionList.minDisplayIndex = 0;
			}
		}
		valid = false;// ִֻ��һ�飬����Ч��
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