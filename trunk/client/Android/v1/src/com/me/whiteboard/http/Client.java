package com.me.whiteboard.http;

import android.os.AsyncTask;

public class Client {
	// try to see if there is already a room named @room,
	// if yes , call @exist
	// else @notexist is called.
	public static void GetIfRoomExists(final String room, final Runnable exist,
			final Runnable notexsit) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return JsonTransfer.httpTransfor("/ifexists?name="
						+ java.net.URLEncoder.encode(room));
			}

			protected void onPostExecute(String s) {
				if (s.equals("1"))
					exist.run();
				else
					notexsit.run();
			}

		}.execute();

	};

	public static interface onRoomEntered {
		void Entered(String room, short usr_ID);

		void Error();
	};

	// enter a room
	public static void EnterRoom(final String room, final onRoomEntered o) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return JsonTransfer.httpTransfor("/enter?name="
						+ java.net.URLEncoder.encode(room));
			}

			protected void onPostExecute(String s) {
				if (s.equals("!error"))
					o.Error();
				else
					o.Entered(room, Short.parseShort(s));
			}

		}.execute();
	}

	// create a room
	public static void CreateRoom(final String room, final Runnable done,
			final Runnable error) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return JsonTransfer.httpTransfor("/create?name="
						+ java.net.URLEncoder.encode(room));
			}

			protected void onPostExecute(String s) {
				if (s.equals("1"))
					done.run();
				else
					error.run();
			}

		}.execute();
	}

	// remove the room
	public static void removeRoom(final String room, final Runnable done) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return JsonTransfer.httpTransfor("/rmroom?name="
						+ java.net.URLEncoder.encode(room));
			}

			protected void onPostExecute(String s) {
				done.run();

			}

		}.execute();

	};

	public interface onSend {
		void SendOK();

		void SendError(String Data);
	}

	// send data to server
	public static void SendData(final String room, final String Data,
			final onSend onSendListener) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return JsonTransfer.httpTransfor("/post?name="
						+ java.net.URLEncoder.encode(room), true, Data);
			}

			protected void onPostExecute(String s) {
				if (onSendListener != null) {
					if (s.equals("saved"))
						onSendListener.SendOK();
					else
						onSendListener.SendError(Data);
				}
			}

		}.execute();

	}

	// set what to do when there are data received.
	// after call this, the function will begin keeping asking the server
	// after new data received o.onRecv would be called
	public static GetData setOnDataRecv(String room, onNewDataRecv o) {
		GetData g = new GetData(room, o);
		g.execute();
		return g;
	}

	public static class GetData extends AsyncTask<Void, String[], Void> {
		String room;
		int fromid;
		boolean running = true;
		onNewDataRecv o;

		public GetData(String r, onNewDataRecv onl) {
			super();
			room = r;
			fromid = 0;
			o = onl;

		}

		protected void onCancelled() {
			running = false;
		}

		protected void onProgressUpdate(String[]... progress) {

			o.onRecv(progress[0]);
		}

		@Override
		protected Void doInBackground(Void... params) {

			while (running) {
				String s = JsonTransfer.httpTransfor("/get?name="
						+ java.net.URLEncoder.encode(room) + "&fromid="
						+ fromid);
				if (running) {
					if (!s.equals("!error") && s.length() != 0) {
						String[] ss = s.split(",");
						fromid += ss.length;
						publishProgress(ss);

					}
				}
			}
			return null;
		}

	}

	public static interface onNewDataRecv {
		void onRecv(String[] datas);
	}

}
