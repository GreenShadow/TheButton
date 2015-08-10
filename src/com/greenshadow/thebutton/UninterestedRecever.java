package com.greenshadow.thebutton;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

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
		CustomApplication.getInstance().getSpUtil().addUninterestedTag(tag);

		// ȡ����֪ͨ
		CustomApplication.getInstance().getNotificationUtil()
				.getNotificationManager().cancel(id);

		// ����֪ͨid��
		CustomApplication.getInstance().getNotificationUtil()
				.canceledANotification();

		Toast.makeText(context, "�ѱ��Ϊ������Ȥ���������� ����-->�������� ���޸����ľ���",
				Toast.LENGTH_LONG).show();
	}
}
