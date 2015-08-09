package com.greenshadow.thebutton;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 不感兴趣的接收器
 */
public class UninterestedRecever extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String tag = intent.getStringExtra("tag");
		int id = intent.getIntExtra("id", 0);
		Log.i("Uninterested", "Uninterested");

		// 添加不感兴趣
		CustomApplication.getInstance().getSpUtil().addUninterestedTags(tag);

		// 取消该通知
		CustomApplication.getInstance().getNotificationUtil()
				.getNotificationManager().cancel(id);

		// 更新通知id数
		CustomApplication.getInstance().getNotificationUtil()
				.canceledANotification();
	}
}
