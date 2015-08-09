package com.greenshadow.thebutton;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * ������Ȥ�Ľ�����
 */
public class UninterestedRecever extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String tag = intent.getStringExtra("tag");
		int id = intent.getIntExtra("id", 0);
		Log.i("Uninterested", "Uninterested");

		// ��Ӳ�����Ȥ
		CustomApplication.getInstance().getSpUtil().addUninterestedTags(tag);

		// ȡ����֪ͨ
		CustomApplication.getInstance().getNotificationUtil()
				.getNotificationManager().cancel(id);

		// ����֪ͨid��
		CustomApplication.getInstance().getNotificationUtil()
				.canceledANotification();
	}
}
