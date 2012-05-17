package com.me.whiteboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.me.whiteboard.http.Client;

public class LoginActivity extends Activity {
	ProgressDialog dialog=null;
    public void onDestroy()
    {
    	if(dialog!=null)
    		dialog.dismiss();
    	super.onDestroy();
    }
    public void setWaitingState(boolean waiting,int ResId)
    {
    	setWaitingState(waiting, getResources().getString(ResId));
    }
    public void setWaitingState(boolean waiting,String info)
    {
    	
    	if(waiting)
    	{
    		if(dialog==null)
    		{
    			dialog=new ProgressDialog(this);
    			dialog.setIndeterminate(true);
    			dialog.setCancelable(false);
    		}
    		
    		dialog.setMessage(info);
    		dialog.show();
    	}
    	else
    	{
    		if(dialog!=null)
    			dialog.dismiss();
    	}
    }
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);

		Button loginButton = (Button) findViewById(R.id.loginRoom);
		Button creatButton = (Button) findViewById(R.id.creatRoom);

		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				EditText roomnum = (EditText) findViewById(R.id.RoomNum);
				String room = roomnum.getText().toString();
				setWaitingState(true, R.string.check_room);
				Client.GetIfRoomExists(room, new EnterRoom(room),
						new onNoRoom());
			}
		});

		creatButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				EditText roomnum = (EditText) findViewById(R.id.RoomNum);
				final String room = roomnum.getText().toString();
				setWaitingState(true, R.string.check_room);
				Client.GetIfRoomExists(room, new Runnable() {

					public void run() {
						setWaitingState(false, R.string.check_room);
						new AlertDialog.Builder(LoginActivity.this)
								.setTitle(R.string.error).setMessage(R.string.room_already_exists)
								.setPositiveButton(R.string.positive, null).show();

					}
				}, new CreateAndEnter(room));

			}
		});
	}

	class CreateAndEnter implements Runnable {
		String room;

		public CreateAndEnter(String room) {
			this.room = room;
		}

		public void run() {
			setWaitingState(true, R.string.createing_room);
			Client.CreateRoom(room, new EnterRoom(room), new CreateError());
		}

	}

	class EnterRoom implements Runnable {
		String room;

		public EnterRoom(String room) {
			this.room = room;
		}

		public void run() {
			setWaitingState(true, R.string.entering_room);
			Client.EnterRoom(room, new onEnter());
		}
	}

	class onEnter implements Client.onRoomEntered {
		// if room exits, enter room and begin drawing
		public void Entered(String room, int usernum) {
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, MainActivity.class);
			intent.putExtra("room", room);
			intent.putExtra("id", usernum);
			setWaitingState(false, null);
			startActivity(intent);
			
			LoginActivity.this.finish();
		}

		public void Error() {
			setWaitingState(false, null);
			Toast.makeText(LoginActivity.this, R.string.loginerror, Toast.LENGTH_LONG)
					.show();
		}
	}

	// if room exists and creating failed, alert msg
	class onNoRoom implements Runnable {
		public void run() {
			setWaitingState(false, null);
			OnClickListener onclick = new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case Dialog.BUTTON_NEGATIVE:
						break;
					case Dialog.BUTTON_POSITIVE:
						EditText roomnum = (EditText) findViewById(R.id.RoomNum);
						final String room = roomnum.getText().toString();
						new CreateAndEnter(room).run();
						break;
					}
				}
			};

			AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
					.setTitle(R.string.error).setMessage(R.string.room_not_exists)
					.setPositiveButton(R.string.positive, onclick)
					.setNegativeButton(R.string.negative, onclick).create();
			dialog.show();
		}
	}

	class CreateError implements Runnable {
		public void run() {
			setWaitingState(false, null);
			Toast.makeText(LoginActivity.this, R.string.room_create_error, Toast.LENGTH_LONG)
					.show();
		}
	}
}
