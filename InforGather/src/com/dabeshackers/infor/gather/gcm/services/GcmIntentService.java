package com.dabeshackers.infor.gather.gcm.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dabeshackers.infor.gather.application.AppMain;
import com.dabeshackers.infor.gather.gcm.receivers.GcmBroadcastReceiver;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
	public static final String TAG = GcmIntentService.class.getSimpleName();

	public static final int NOTIFICATION_ID = 1;
	@SuppressWarnings("unused")
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GcmIntentService() {
		super("GcmIntentService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		AppMain appMain = (AppMain) getApplication();

		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);
		if (!extras.isEmpty()) {

			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
				Log.i(TAG, "Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				sendNotification("Deleted messages on server: " + extras.toString());
				Log.i(TAG, "Deleted messages on server: " + extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				// This loop represents the service doing some work.
				appMain.requestSync();

				// Post notification of received message.
				sendNotification("Received: " + extras.toString());
				Log.i(TAG, "GCM Received: " + extras.toString() + "Requesting Sync...");
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg) {
		//		mNotificationManager = (NotificationManager)
		//				this.getSystemService(Context.NOTIFICATION_SERVICE);
		//
		//		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
		//				new Intent(this, MainActivity.class), 0);
		//
		//		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
		//		.setSmallIcon(R.drawable.ic_launcher)
		//		.setContentTitle("Flow Motion")
		//		.setStyle(new NotificationCompat.BigTextStyle()
		//		.bigText(msg))
		//		.setContentText(msg);
		//
		//		mBuilder.setContentIntent(contentIntent);
		//		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}

}