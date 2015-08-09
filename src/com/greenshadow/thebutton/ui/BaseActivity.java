package com.greenshadow.thebutton.ui;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.greenshadow.thebutton.CustomApplication;
import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.bean.User;
import com.greenshadow.thebutton.util.CollectionUtils;
import com.greenshadow.thebutton.view.HeaderLayout;
import com.greenshadow.thebutton.view.HeaderLayout.HeaderStyle;
import com.greenshadow.thebutton.view.HeaderLayout.onLeftImageButtonClickListener;
import com.greenshadow.thebutton.view.HeaderLayout.onRightImageButtonClickListener;
import com.greenshadow.thebutton.view.dialog.DialogTips;

/**
 * Activity����
 */
public class BaseActivity extends FragmentActivity {

	BmobUserManager userManager;
	BmobChatManager manager;
	CustomApplication mApplication;

	protected HeaderLayout mHeaderLayout;

	protected int mScreenWidth;
	protected int mScreenHeight;
	private Toast mToast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȫ�ֽ�ֹ����
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		userManager = BmobUserManager.getInstance(this);
		manager = BmobChatManager.getInstance(this);
		mApplication = CustomApplication.getInstance();
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
	}

	public void ShowToast(final String text) {
		if (!TextUtils.isEmpty(text)) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mToast == null) {
						mToast = Toast.makeText(getApplicationContext(), text,
								Toast.LENGTH_LONG);
					} else {
						mToast.setText(text);
					}
					mToast.show();
				}
			});

		}
	}

	public void ShowToast(final int resId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mToast == null) {
					mToast = Toast.makeText(
							BaseActivity.this.getApplicationContext(), resId,
							Toast.LENGTH_LONG);
				} else {
					mToast.setText(resId);
				}
				mToast.show();
			}
		});
	}

	/**
	 * ��Log
	 */
	public void ShowLog(String msg) {
		Log.i("life", msg);
	}

	/**
	 * ֻ��title initTopBarLayoutByTitle
	 */
	public void initTopBarForOnlyTitle(String titleName) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
		mHeaderLayout.setDefaultTitle(titleName);
	}

	/**
	 * �����Ұ�ť
	 */
	public void initTopBarForBoth(String titleName, int rightDrawableId,
			String text, onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
		mHeaderLayout.setTitleAndRightButton(titleName, rightDrawableId, text,
				listener);
	}

	public void initTopBarForBoth(String titleName, int rightDrawableId,
			onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
		mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
				listener);
	}

	/**
	 * ֻ����߰�ť��Title initTopBarLayout
	 */
	public void initTopBarForLeft(String titleName) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
	}

	/**
	 * ��ʾ���ߵĶԻ��� showOfflineDialog
	 */
	public void showOfflineDialog(final Context context) {
		DialogTips dialog = new DialogTips(this, "�����˺����������豸�ϵ�¼!", "���µ�¼");
		// ���óɹ��¼�
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				CustomApplication.getInstance().logout();
				startActivity(new Intent(context, LoginActivity.class));
				finish();
				dialogInterface.dismiss();
			}
		});
		// ��ʾȷ�϶Ի���
		dialog.show();
		dialog = null;
	}

	// ��߰�ť�ĵ���¼�
	public class OnLeftButtonClickListener implements
			onLeftImageButtonClickListener {
		@Override
		public void onClick() {
			finish();
		}
	}

	public void startAnimActivity(Class<?> cla) {
		this.startActivity(new Intent(this, cla));
	}

	public void startAnimActivity(Intent intent) {
		this.startActivity(intent);
	}

	/**
	 * ���ڵ�½�����Զ���½����µ��û����ϼ��������ϵļ�����
	 */
	public void updateUserInfos() {
		// ���µ���λ����Ϣ
		updateUserLocation();
		userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {
			@Override
			public void onError(int arg0, String arg1) {
				if (arg0 == BmobConfig.CODE_COMMON_NONE) {
					ShowLog(arg1);
				} else {
					ShowLog("��ѯ�����б�ʧ�ܣ�" + arg1);
				}
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				// ���浽application�з���Ƚ�
				CustomApplication.getInstance().setContactList(
						CollectionUtils.list2map(arg0));
			}
		});
	}

	/**
	 * �����û��ľ�γ����Ϣ
	 */
	public void updateUserLocation() {
		if (CustomApplication.lastPoint != null) {
			String saveLatitude = mApplication.getLatitude();
			String saveLongtitude = mApplication.getLongtitude();
			String newLat = String.valueOf(CustomApplication.lastPoint
					.getLatitude());
			String newLong = String.valueOf(CustomApplication.lastPoint
					.getLongitude());
			if (!saveLatitude.equals(newLat) || !saveLongtitude.equals(newLong)) {// ֻ��λ���б仯�͸��µ�ǰλ�ã��ﵽʵʱ���µ�Ŀ��
				User u = (User) userManager.getCurrentUser(User.class);
				final User user = new User();
				user.setLocation(CustomApplication.lastPoint);
				user.setObjectId(u.getObjectId());
				user.update(this, new UpdateListener() {
					@Override
					public void onSuccess() {
						CustomApplication.getInstance()
								.setLatitude(
										String.valueOf(user.getLocation()
												.getLatitude()));
						CustomApplication.getInstance().setLongtitude(
								String.valueOf(user.getLocation()
										.getLongitude()));
					}

					@Override
					public void onFailure(int code, String msg) {
					}
				});
			}
		}
	}
}
