package com.me.whiteboard.actions;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ActionAdapter extends BaseAdapter {

	protected LayoutInflater mLayoutInflater;
	Context context;
	protected int mResource;
	List<Action> list;

	public ActionAdapter(int resouce, ActionList l) {
		this.mResource = resouce;
		this.list = l.list;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public void setSelectItem(int selectItem) {
		this.selectItem = selectItem;
	}

	public void SelectAndMark(int a) {
		setSelectItem(a);
		this.notifyDataSetChanged();
	}

	protected int selectItem = -1;

	public View getView(int position, View convertView, ViewGroup parent) {
		return ((Action) getItem(position)).getView(mLayoutInflater, mResource,
				convertView, parent, selectItem == position);
	}

	public void setContext(Context context) {
		this.mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
	}
}
