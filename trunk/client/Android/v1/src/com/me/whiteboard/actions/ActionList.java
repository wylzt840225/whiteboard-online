package com.me.whiteboard.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.me.whiteboard.MainActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.BaseAdapter;

public class ActionList {
	protected HashMap<Integer, ActionAdapter> adapterList;
	public static ArrayList<Action> list;
	public static int minDisplayIndex;

	protected boolean refreshing = false;

	public ActionList() {
		list = new ArrayList<Action>();
		adapterList = new HashMap<Integer, ActionAdapter>();
	}

	public void clear() {
		list.clear();
	}

	public void add(Action action) {
		list.add(action);
	}

	public void notifyAllAdapter() {
		Set<Integer> set = adapterList.keySet();
		Object[] array = set.toArray();
		for (int i = 0; i < set.size(); i++) {
			BaseAdapter adapter;
			adapter = adapterList.get(array[i]);
			if (adapter != null)
				adapter.notifyDataSetChanged();
		}
	}

	public void createAdapter(int tag, int resource) {
		if (!adapterList.containsKey(tag)) {
			ActionAdapter adapter = new ActionAdapter(resource);
			adapterList.put(tag, adapter);
		}
	}

	public ActionAdapter getAdapter(int tag, Context context) {
		ActionAdapter adapter = adapterList.get(tag);
		adapter.setContext(context);
		return adapter;
	}

	public int size() {
		if (list == null) {
			return 0;
		}
		return list.size();
	}

	public void actAll(MainActivity activity, Canvas canvas) {
		if (list == null) {
			return;
		}
		for (int i = minDisplayIndex; i < list.size(); i++) {
			list.get(i).act(activity, canvas);
		}
	}
	
	public static void setMinDisplayIndex(ClearAction clearAct) {
		minDisplayIndex = list.indexOf(clearAct) + 1;
	}
}
