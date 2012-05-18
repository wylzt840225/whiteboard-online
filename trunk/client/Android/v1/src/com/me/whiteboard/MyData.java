package com.me.whiteboard;

import java.util.HashMap;

public class MyData {
	ActionList msgList;
	HashMap<Integer, String> nametable;

	static MyData instance = null;

	public static MyData getInstance() {
		if (instance == null)
			instance = new MyData();
		return instance;
	}

	MyData() {
		msgList = new ActionList();
		nametable = new HashMap<Integer, String>();
	}

}
