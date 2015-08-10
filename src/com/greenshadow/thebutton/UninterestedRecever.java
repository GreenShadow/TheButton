package com.greenshadow.thebutton;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

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
		CustomApplication.getInstance().getSpUtil().addUninterestedTag(tag);

		// 取消该通知
		CustomApplication.getInstance().getNotificationUtil()
				.getNotificationManager().cancel(id);

		// 更新通知id数
		CustomApplication.getInstance().getNotificationUtil()
				.canceledANotification();

		Toast.makeText(context, "已标记为不感兴趣，您可以在 设置-->个人资料 中修改您的决定",
				Toast.LENGTH_LONG).show();
	}
}
