package com.dabeshackers.infor.gather.helpers;

import com.dabeshackers.infor.gather.MainActivity;
import com.dabeshackers.infor.gather.R;
import com.dabeshackers.infor.gather.application.ApplicationUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationsHelper {
	public static void dismissNotification(Context context) {
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(ApplicationUtils.getPackageHash(context));
	}

	public static void dismissNotification(int id, Context c) {
		NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(id);
	}

	@SuppressWarnings("deprecation")
	public static void updateNotification(String message, Context context) {
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = "";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);

		CharSequence contentTitle = context.getResources().getString(R.string.app_name);
		CharSequence contentText = message;
		//		Intent notificationIntent = new Intent(context, InitActivity.class);
		Intent notificationIntent = new Intent(context, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

		mNotificationManager.notify(ApplicationUtils.getPackageHash(context), notification);
	}
}
