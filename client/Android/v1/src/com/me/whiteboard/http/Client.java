package com.me.whiteboard.http;

import android.os.AsyncTask;


public class Client {
	//try to see if there is already  a room named @room,
	// if yes , call @exist
	// else @notexist is called.
	public static void GetIfRoomExists(final String room, final Runnable exist,
			final Runnable notexsit) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return JsonTransfer.httpTransfor("/ifexists?name=" + room);
			}

			protected void onPostExecute(String s) {
				if (s.equals("1"))
					exist.run();
				else
					notexsit.run();
			}

		}.execute();

	};
	static interface onRoomEntered
	{
		void Entered(String room,int usernum);
		void Error();
	};
	//enter a room 
	public static void EnterRoom(final String room,final onRoomEntered o )
	{
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return JsonTransfer.httpTransfor("/enter?name=" + room);
			}

			protected void onPostExecute(String s) {
				if (s.equals("!error"))
					o.Error();
				else
					o.Entered(room, Integer.parseInt(s));
			}

		}.execute();
	}
	//create a room
	public static void CreateRoom(final String room,final Runnable done,final Runnable error)
	{
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return JsonTransfer.httpTransfor("/create?name=" + room);
			}

			protected void onPostExecute(String s) {
				if (s.equals("1"))
					done.run();
				else
					error.run();
			}

		}.execute();
	}
	//remove the room 
	public static void removeRoom(final String room, final Runnable done) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return JsonTransfer.httpTransfor("/rmroom?name=" + room);
			}

			protected void onPostExecute(String s) {
				done.run();

			}

		}.execute();

	};

	//send data to server 
	public static void SendData(final String room, final String Data,
			final Runnable done, final Runnable error) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return JsonTransfer.httpTransfor("/post?name=" + room, true,
						Data);
			}

			protected void onPostExecute(String s) {
				if (s.equals("saved"))
					done.run();
				else
					error.run();
			}

		}.execute();

	}
	
	//set what to do when there are data received.
	// after call this, the function will begin keeping asking the server
	// after new data received  o.onRecv would be called
	public static void setOnDataRecv(String room, onNewDataRecv o) {
		new GetData(room, o).execute();
	}

	static class GetData extends AsyncTask<Void, String[], Void> {
		String room;
		int fromid;
		onNewDataRecv o;

		public GetData(String r, onNewDataRecv onl) {
			super();
			room = r;
			fromid = 0;
			o = onl;

		}

		 protected void onProgressUpdate(String[]...progress) {
	         o.onRecv(progress[0]);
	     }
		@Override
		protected Void doInBackground(Void... params) {

			while (true) {
				String s = JsonTransfer.httpTransfor("/get?name=" + room
						+ "&fromid=" + fromid);
				if (!s.equals("!error") && s.length() != 0) {
					String[] ss = s.split(",");
					fromid += ss.length;
					publishProgress(ss);

				}
			}
		}

	}

	public static interface onNewDataRecv {
		void onRecv(String[] datas);
	}

}
