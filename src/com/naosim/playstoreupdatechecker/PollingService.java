package com.naosim.playstoreupdatechecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Polling service
 * 
 * @Author Ryan
 * @Create 2013-7-13 上午10:18:44
 */
public class PollingService extends Service {

	public static final String ACTION = "com.naosim.playstoreupdatechecker.PollingService";

	private Notification mNotification;
	private NotificationManager mManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		initNotifiManager();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		new PollingThread().start();
	}

	private void initNotifiManager() {
		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		mNotification = new Notification();
		mNotification.icon = icon;
		mNotification.tickerText = "New Message";
		mNotification.defaults |= Notification.DEFAULT_SOUND;
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
	}

	private void showNotification() {
		mNotification.when = System.currentTimeMillis();
		// Navigator to the new activity when click the notification title
		Intent i = new Intent(this, MessageActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		mNotification.setLatestEventInfo(this,
				getResources().getString(R.string.app_name),
				"Application appdate!!!!!", pendingIntent);
		mManager.notify(0, mNotification);
	}

	/**
	 * Polling thread
	 * 
	 * @Author Ryan
	 * @Create 2013-7-13 上午10:18:34
	 */
	int count = 0;

	class PollingThread extends Thread {
		@Override
		public void run() {
			Log.i("PollingThread", "Polling...");
			String appPackageName = "com.example.hoge";
			String appVersion = "1.2.3";
			HttpGet req = new HttpGet(
					"http://naosim.sakura.ne.jp/app/versionChecker/check.cgi?pkg=" + appPackageName + "&ver=" + appVersion);
			DefaultHttpClient client = new DefaultHttpClient();
			try {
				HttpResponse res = client.execute(req);
				if(res.getStatusLine().getStatusCode() != 200) return;
				String contents = loadString(res.getEntity().getContent());
				JSONObject json = new JSONObject(contents);
				Log.e("json", contents);
				if(json.get("result") == Boolean.TRUE) {
					showNotification();
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public static String loadString(InputStream in) throws IOException {
		BufferedReader streamReader = new BufferedReader(new InputStreamReader(
				in, "UTF-8"));
		StringBuilder responseStrBuilder = new StringBuilder();

		String inputStr;
		while ((inputStr = streamReader.readLine()) != null)
			responseStrBuilder.append(inputStr);
		return responseStrBuilder.toString();
	}

}
