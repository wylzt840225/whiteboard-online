package com.me.whiteboard.actions;

import android.graphics.Color;
import android.graphics.Paint;

public class EraserAction extends PathAction {

	public EraserAction() {
		super();
	}

	public EraserAction(short usr_ID, short local_ID, Paint paint) {
		super(usr_ID, local_ID, paint);
		color = Color.WHITE;
	}

}
