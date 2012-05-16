package com.me.whiteboard;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class ActionHistory {

	public static List<Action> actionList = new Vector<Action>();

	@SuppressWarnings("serial")
	public class Action implements Serializable {

		public short usr_ID;
		public short local_ID;
		public long time;
		public short type;
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

	/*public void add(String base64String) {
		String data = Base64.decode(base64String, Base64.DEFAULT).toString();

		short usr_ID = Short.parseShort(data.substring(0, data.indexOf(";") - 1));
		data = data.substring(data.indexOf(";") + 1, data.length() - 1);

		short local_ID = Short.parseShort(data.substring(0, data.indexOf(";") - 1));
		data = data.substring(data.indexOf(";") + 1, data.length() - 1);

		short type = Short.parseShort(data.substring(0, data.indexOf(";") - 1));
		data = data.substring(data.indexOf(";") + 1, data.length() - 1);

		add(usr_ID, local_ID, type, data);
	}*/

}
