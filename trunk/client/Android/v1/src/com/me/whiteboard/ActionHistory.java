package com.me.whiteboard;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class ActionHistory {
	
	public static List<Action> actionList = new Vector<Action> ();
	
	@SuppressWarnings("serial")
	private class Action implements Serializable {
		
		public short usr_ID;
		public short local_ID;
		public short type;
		public String data;
		
		Action(short usr_ID, short local_ID, short type, String data) {
			this.usr_ID = usr_ID;
			this.local_ID = local_ID;
			this.type = type;
			this.data = data;
		}
	}
	
	ActionHistory() {
	}
	
	ActionHistory(Action [] actionList) {
		for (int i = 0; i < actionList.length; i++) {
			ActionHistory.actionList.add(actionList[i]);
		}
	}
	
	public void add(short usr_ID, short local_ID, short type, String data) {
		Action action = new Action(usr_ID, local_ID, type, data);
		actionList.add(action);
	}

}
