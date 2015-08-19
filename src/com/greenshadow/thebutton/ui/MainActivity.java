package com.greenshadow.thebutton.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;

import com.greenshadow.thebutton.CustomApplication;
import com.greenshadow.thebutton.MyMessageReceiver;
import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.ui.fragment.ContactFragment;
import com.greenshadow.thebutton.ui.fragment.NewsFragment;
import com.greenshadow.thebutton.ui.fragment.RecentFragment;
import com.greenshadow.thebutton.ui.fragment.SettingsFragment;

public class MainActivity extends ActivityBase implements EventListener {

	private Button[] mTabs;
	private ContactFragment contactFragment;
	private RecentFragment recentFragment;
	private SettingsFragment settingFragment;
	private NewsFragment newsFragment;
	private Fragment[] fragments;
	private int currentTabIndex;

	private ImageView iv_recent_tips, iv_contact_tips;// 消息提示
	private ViewPager viewPager;
	private NewBroadcastReceiver newReceiver;
	private TagBroadcastReceiver userReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 开启广播接收器
		initNewMessageBroadCast();
		initTagMessageBroadCast();
		initView();
	}

	private void initView() {
		mTabs = new Button[4];
		mTabs[0] = (Button) findViewById(R.id.btn_message);
		mTabs[1] = (Button) findViewById(R.id.btn_contract);
		mTabs[2] = (Button) findViewById(R.id.btn_news);
		mTabs[3] = (Button) findViewById(R.id.btn_set);
		// TODO
		iv_recent_tips = (ImageView) findViewById(R.id.iv_recent_tips);
		iv_contact_tips = (ImageView) findViewById(R.id.iv_contact_tips);

		contactFragment = new ContactFragment();
		recentFragment = new RecentFragment();
		settingFragment = new SettingsFragment();
		newsFragment = new NewsFragment();
		fragments = new Fragment[] { recentFragment, contactFragment,
				newsFragment, settingFragment };

		viewPager = (ViewPager) findViewById(R.id.view_pager);
		viewPager.setAdapter(new FragmentPagerAdapter(
				getSupportFragmentManager()) {
			@Override
			public int getCount() {
				return fragments.length;
			}

			@Override
			public Fragment getItem(int index) {
				return fragments[index];
			}
		});
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				mTabs[currentTabIndex].setSelected(false);
				mTabs[position].setSelected(true);
				currentTabIndex = position;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				ContactFragment contactFragment = (ContactFragment) fragments[1];
				if (state > 0)
					contactFragment.hide();
				else
					contactFragment.show();

			}
		});
		viewPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
		// 把第一个tab设为选中状态
		mTabs[0].setSelected(true);

	}

	/**
	 * button点击事件
	 */
	public void onTabSelect(View view) {
		switch (view.getId()) {
		case R.id.btn_message:
			viewPager.setCurrentItem(0);
			break;
		case R.id.btn_contract:
			viewPager.setCurrentItem(1);
			break;
		case R.id.btn_news:
			viewPager.setCurrentItem(2);
			break;
		case R.id.btn_set:
			viewPager.setCurrentItem(3);
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 小圆点提示
		if (BmobDB.create(this).hasUnReadMsg()) {
			iv_recent_tips.setVisibility(View.VISIBLE);
		} else {
			iv_recent_tips.setVisibility(View.GONE);
		}
		if (BmobDB.create(this).hasNewInvite()) {
			iv_contact_tips.setVisibility(View.VISIBLE);
		} else {
			iv_contact_tips.setVisibility(View.GONE);
		}
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		// 清空
		MyMessageReceiver.mNewNum = 0;
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
	}

	@Override
	public void onMessage(BmobMsg message) {
		refreshNewMsg(message);
	}

	/**
	 * 刷新界面
	 */
	private void refreshNewMsg(BmobMsg message) {
		// 声音提示
		boolean isAllow = CustomApplication.getInstance().getSpUtil()
				.isAllowVoice();
		if (isAllow) {
			CustomApplication.getInstance().getMediaPlayer().start();
		}
		iv_recent_tips.setVisibility(View.VISIBLE);
		// 也要存储起来
		if (message != null) {
			BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(
					true, message);
		}
		if (currentTabIndex == 0) {
			// 当前页面如果为会话页面，刷新此页面
			if (recentFragment != null) {
				recentFragment.refresh();
			}
		}
	}

	private void initNewMessageBroadCast() {
		// 注册接收消息广播
		newReceiver = new NewBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(
				BmobConfig.BROADCAST_NEW_MESSAGE);
		// 优先级要低于ChatActivity
		intentFilter.setPriority(3);
		registerReceiver(newReceiver, intentFilter);
	}

	/**
	 * 新消息广播接收者
	 */
	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 刷新界面
			refreshNewMsg(null);
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}

	private void initTagMessageBroadCast() {
		// 注册接收消息广播
		userReceiver = new TagBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(
				BmobConfig.BROADCAST_ADD_USER_MESSAGE);
		// 优先级要低于ChatActivity
		intentFilter.setPriority(3);
		registerReceiver(userReceiver, intentFilter);
	}

	/**
	 * 标签消息广播接收者
	 */
	private class TagBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			BmobInvitation message = (BmobInvitation) intent
					.getSerializableExtra("invite");
			refreshInvite(message);
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		if (isNetConnected) {
			ShowToast(R.string.network_tips);
		}
	}

	@Override
	public void onAddUser(BmobInvitation message) {
		refreshInvite(message);
	}

	/**
	 * 刷新好友请求
	 */
	private void refreshInvite(BmobInvitation message) {
		boolean isAllow = CustomApplication.getInstance().getSpUtil()
				.isAllowVoice();
		if (isAllow) {
			CustomApplication.getInstance().getMediaPlayer().start();
		}
		iv_contact_tips.setVisibility(View.VISIBLE);
		if (currentTabIndex == 1) {
			if (contactFragment != null) {
				contactFragment.refresh();
			}
		} else {
			// 同时提醒通知
			String tickerText = message.getFromname() + "请求添加好友";
			boolean isAllowVibrate = CustomApplication.getInstance()
					.getSpUtil().isAllowVibrate();
			BmobNotifyManager.getInstance(this).showNotify(isAllow,
					isAllowVibrate, R.drawable.ic_launcher, tickerText,
					message.getFromname(), tickerText.toString(),
					NewFriendActivity.class);
		}
	}

	@Override
	public void onOffline() {
		showOfflineDialog(this);
	}

	@Override
	public void onReaded(String conversionId, String msgTime) {
	}

	private static long firstTime;

	/**
	 * 连续按两次返回键就退出
	 */
	@Override
	public void onBackPressed() {
		if (firstTime + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
		} else {
			ShowToast("再按一次退出程序");
		}
		firstTime = System.currentTimeMillis();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(newReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(userReceiver);
		} catch (Exception e) {
		}
	}

}
