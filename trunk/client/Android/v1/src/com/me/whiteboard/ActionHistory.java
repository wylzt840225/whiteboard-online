package com.me.whiteboard;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import android.util.Base64;

public class ActionHistory {

	public static List<Action> actionList = new Vector<Action>();

	@SuppressWarnings("serial")
	private class Action implements Serializable {

		@SuppressWarnings("unused")
		public short usr_ID;
		@SuppressWarnings("unused")
		public short local_ID;
		@SuppressWarnings("unused")
		public long time;
		@SuppressWarnings("unused")
		public short type;
		@SuppressWarnings("unused")
		public String data;

		Action(short usr_ID, short local_ID, long time, short type, String data) {
			this.usr_ID = usr_ID;
			this.local_ID = local_ID;
			this.time = time;
			this.type = type;
			this.data = data;
		}
	}

	ActionHistory() {
	}

	ActionHistory(Action[] actionList) {
		for (int i = 0; i < actionList.length; i++) {
			ActionHistory.actionList.add(actionList[i]);
		}
	}

	public void add(short usr_ID, short local_ID, short type, String data) {
		Action action = new Action(usr_ID, local_ID, System.currentTimeMillis(), type, data);
		actionList.add(action);
	}

	public void add(String base64String) {
		String baseByte = android.util.Base64.decode(base64String, Base64.DEFAULT).toString();

		short usr_ID = Short.parseShort(baseByte.substring(0, baseByte.indexOf(";") - 1));
		baseByte = baseByte.substring(baseByte.indexOf(";") + 1, baseByte.length() - 1);

		short local_ID = Short.parseShort(baseByte.substring(0, baseByte.indexOf(";") - 1));
		baseByte = baseByte.substring(baseByte.indexOf(";") + 1, baseByte.length() - 1);

		short type = Short.parseShort(baseByte.substring(0, baseByte.indexOf(";") - 1));
		baseByte = baseByte.substring(baseByte.indexOf(";") + 1, baseByte.length() - 1);

		Action action = new Action(usr_ID, local_ID, System.currentTimeMillis(), type, baseByte);
		actionList.add(action);
	}

}
