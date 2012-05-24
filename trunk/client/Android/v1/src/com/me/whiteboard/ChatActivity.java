package com.me.whiteboard;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.me.whiteboard.actions.Action;
import com.me.whiteboard.actions.MsgAction;
import com.me.whiteboard.compat.ActionBarActivity;
import com.me.whiteboard.http.Client;
import com.me.whiteboard.http.Client.GetData;
import com.me.whiteboard.http.Client.onNewDataRecv;
import com.me.whiteboard.http.Client.onSend;

public class ChatActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity);
		ListView lv = (ListView) findViewById(R.id.chatlist);
		MyData.getInstance().msgList.createAdapter(0, R.layout.msg_item);
		lv.setAdapter(MyData.getInstance().msgList.getAdapter(0, this));
		Button send = (Button) findViewById(R.id.send);
		
		send.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				MsgAction msg = new MsgAction(MyData.getInstance().usr_ID,
						MyData.getInstance().local_ID,
						((EditText) findViewById(R.id.msg_content_et))
								.getText().toString());
				MyData.getInstance().local_ID++;
				//msg.addMeToList();
				((EditText) findViewById(R.id.msg_content_et)).setText("");
				Client.SendData(MyData.getInstance().room, msg.toBase64(),
						new MsgSend(msg));
			}

		});
	}

	static class MsgSend implements onSend {
		MsgAction b;

		MsgSend(MsgAction a) {
			b = a;
		}

		public void SendOK() {
		}

		public void SendError(String Data) {
			Client.SendData(MyData.getInstance().room, Data, this);
		}
	}

	GetData g;

	protected void onPause() {
		super.onPause();
		if (g != null)
			g.cancel(false);
	}

	protected void onResume() {
		super.onResume();
		g = Client.setOnDataRecv(MyData.getInstance().room,
				new onNewDataRecv() {
					public void onRecv(final String[] datas) {
						Action action;
						for (int i = 0; i < datas.length; i++) {
							action = Action.base64ToAction(datas[i]);
							action.addMeToList();
						}
					}
				});
	}
}
