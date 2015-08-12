package com.greenshadow.thebutton.util;

import com.greenshadow.thebutton.CustomApplication;
import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.config.Config;
import com.greenshadow.thebutton.ui.BrowserActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class NotificationUtil {
	private NotificationManager mNotificationManager;
	private int notificationCount = 10; // 这是随便取的一个值，不要在意这些细节^_^

	public NotificationUtil(Context content) {
		mNotificationManager = (NotificationManager) content
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public NotificationManager getNotificationManager() {
		return mNotificationManager;
	}

	/**
	 * 生成推送通知
	 * 
	 * @param context
	 * @param title
	 * @param content
	 * @param url
	 * @param tag
	 * @param id
	 * @return
	 */
	private Notification generatePushNotification(Context context,
			String title, String content, String url, String tag, int id) {
		Intent intent = new Intent(context, BrowserActivity.class); // 通知点击的Intent
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("url", url);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Intent uninterestedButtonIntent = new Intent(Config.UNINTERESTE_ACTION); // 不感兴趣按钮的Intent
		uninterestedButtonIntent.putExtra("tag", tag);
		uninterestedButtonIntent.putExtra("id", id);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.notification_push_view);
		PendingIntent uninterestedButtonPendingIntent = PendingIntent
				.getBroadcast(context, 0, uninterestedButtonIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.btn_notification_uninterested,
				uninterestedButtonPendingIntent);
		remoteViews.setTextViewText(R.id.tv_notification_title, title);
		remoteViews.setTextViewText(R.id.tv_notification_content, content);

		Notification.Builder builder = new Notification.Builder(context);
		Notification notification = builder.setContent(remoteViews)
				.setTicker("扣子推送") //
				.setWhen(System.currentTimeMillis()) //
				.setSmallIcon(R.drawable.small_icon) //
				.setOngoing(false) //
				.setContentIntent(pendingIntent) //
				.getNotification();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_LIGHTS;
		boolean isAllowVoice = CustomApplication.getInstance().getSpUtil()
				.isAllowVoice();
		boolean isAllowVibrate = CustomApplication.getInstance().getSpUtil()
				.isAllowVibrate();
		if (isAllowVoice)
			notification.defaults |= Notification.DEFAULT_SOUND;
		if (isAllowVibrate)
			notification.defaults |= Notification.DEFAULT_VIBRATE;

		return notification;
	}

	/**
	 * 推送通知
	 * 
	 * @param context
	 * @param title
	 * @param content
	 * @param url
	 * @param tag
	 */
	public void notifyPush(Context context, String title, String content,
			String url, String tag) {
		mNotificationManager.notify(
				notificationCount,
				generatePushNotification(context, title, content, url, tag,
						notificationCount));
		notificationCount++;
	}

	/**
	 * 更新通知
	 * 
	 * @param context
	 */
	public void notifyUpdate(Context context) {
	}

	/**
	 * 取消了一个通知
	 */
	public void canceledANotification() {
		notificationCount--;
	}
}
