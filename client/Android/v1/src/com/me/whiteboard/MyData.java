package com.me.whiteboard;

import java.util.HashMap;

import com.me.whiteboard.actions.ActionList;

public class MyData {
	public ActionList msgList;
	public HashMap<Short, String> nametable;
	public ActionList actionList;
	static MyData instance = null;
	String room;
	public short usr_ID;
	static short local_ID = 0;
	public static MyData getInstance() {
		if (instance == null)
			instance = new MyData();
		return instance;
	}

	MyData() {
		msgList = new ActionList();
		nametable = new HashMap<Short, String>();
		actionList = new ActionList();
	}

}
