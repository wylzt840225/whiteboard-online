package com.me.whiteboard.compat;

import android.view.Menu;
import android.view.MenuItem;

public interface MenuAdapter {
	public void showMenu(Menu menu);

	public boolean onOptionsItemSelected(MenuItem item,
			final ActionBarActivity act);
}
