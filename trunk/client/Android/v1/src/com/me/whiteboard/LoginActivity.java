package com.me.whiteboard;

import com.me.whiteboard.http.Client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity
{
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login);
		
		Button loginButton = (Button)findViewById(R.id.loginRoom);
		Button creatButton = (Button)findViewById(R.id.creatRoom);
		
		loginButton.setOnClickListener(new View.OnClickListener() 
		{	
			public void onClick(View v) 
			{
				EditText roomnum = (EditText)findViewById(R.id.RoomNum);
				String room = roomnum.getText().toString();
				Client.GetIfRoomExists(room, new BeginDrawing(), new LoginFail());
			}
		});
		
		creatButton.setOnClickListener(new View.OnClickListener() 
		{	
			public void onClick(View v) 
			{
				EditText roomnum = (EditText)findViewById(R.id.RoomNum);
				String room = roomnum.getText().toString();
				Client.EnterRoom(room, new BeginEnter());
			}
		});
	}
	//enter succeed, begin drawing
	class BeginDrawing implements Runnable
	{
		public void run() 
		{
			Intent intent = new Intent();  
	        intent.setClass(LoginActivity.this, MainActivity.class);  
	        startActivity(intent);  
	        LoginActivity.this.finish();  
		}
	}
	class BeginEnter implements Client.onRoomEntered
	{
		//if room exits, enter room and begin drawing
		public void Entered(String room, int usernum) 
		{
			Intent intent = new Intent();  
	        intent.setClass(LoginActivity.this, MainActivity.class);  
	        startActivity(intent);  
	        LoginActivity.this.finish();  
		}
		//if room not exists, create a new room and enter
		public void Error() 
		{
			new AlertDialog.Builder(LoginActivity.this).setTitle("出错啦！")
			.setMessage("该房间已存在，请修改名称")
			.setPositiveButton("确定",null).show(); 
		}
	}
	//if room exists and creating failed, alert msg
	class LoginFail implements Runnable
	{
		public void run() 
		{
			OnClickListener onclick = new OnClickListener() 
			 {  
			        public void onClick(DialogInterface dialog, int which) 
			        {  
			            switch (which) {  
			                case Dialog.BUTTON_NEGATIVE:  
			                    break;  
			                case Dialog.BUTTON_POSITIVE:
			                	EditText roomnum = (EditText)findViewById(R.id.RoomNum);
			    				String room = roomnum.getText().toString();
			    				Client.CreateRoom(room, new BeginDrawing(), new ShowError());
			                    break;  
			            }  
			        }
			    };  
			    
			AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)  
			.setTitle("出错啦！")  
			.setMessage("没有找到该房间。是否为您新建房间？")
			.setPositiveButton("确定", onclick)  
			.setNegativeButton("返回",  onclick).create();  
			dialog.show(); 
		}
	}
	
	class ShowError implements Runnable
	{
		public void run() 
		{
			Toast.makeText(LoginActivity.this,"创建失败，请重试", Toast.LENGTH_LONG).show();
		}
	}
}
