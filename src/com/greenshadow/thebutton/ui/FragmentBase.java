package com.greenshadow.thebutton.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.util.BmobLog;

import com.greenshadow.thebutton.CustomApplication;
import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.view.HeaderLayout;
import com.greenshadow.thebutton.view.HeaderLayout.HeaderStyle;
import com.greenshadow.thebutton.view.HeaderLayout.onLeftImageButtonClickListener;
import com.greenshadow.thebutton.view.HeaderLayout.onRightImageButtonClickListener;

/**
 * Fragmenet ����
 */
public abstract class FragmentBase extends Fragment {

	public BmobUserManager userManager;
	public BmobChatManager manager;

	/**
	 * ���õ�Header����
	 */
	public HeaderLayout mHeaderLayout;

	protected View contentView;
	private Toast mToast;

	public LayoutInflater mInflater;

	private Handler handler = new Handler();

	public void runOnWorkThread(Runnable action) {
		new Thread(action).start();
	}

	public void runOnUiThread(Runnable action) {
		handler.post(action);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mApplication = CustomApplication.getInstance();
		userManager = BmobUserManager.getInstance(getActivity());
		manager = BmobChatManager.getInstance(getActivity());
		mInflater = LayoutInflater.from(getActivity());
	}

	public void ShowToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

	public void ShowToast(int text) {
		if (mToast == null) {
			mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

	/**
	 * ��Log
	 */
	public void ShowLog(String msg) {
		BmobLog.i(msg);
	}

	public View findViewById(int paramInt) {
		return getView().findViewById(paramInt);
	}

	public CustomApplication mApplication;

	/**
	 * ֻ��title initTopBarLayoutByTitle
	 */
	public void initTopBarForOnlyTitle(String titleName) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
		mHeaderLayout.setDefaultTitle(titleName);
	}

	/**
	 * ��ʼ��������-�����Ұ�ť
	 */
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
		mHeaderLayout.init(HeaderStyle.TITLE_LIFT_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
	}

	/**
	 * �ұ�+title initTopBarForRight
	 */
	public void initTopBarForRight(String titleName, int rightDrawableId,
			onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_RIGHT_IMAGEBUTTON);
		mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
				listener);
	}

	// ��߰�ť�ĵ���¼�
	public class OnLeftButtonClickListener implements
			onLeftImageButtonClickListener {
		@Override
		public void onClick() {
			getActivity().finish();
		}
	}

	/**
	 * ��������ҳ�� startAnimActivity
	 */
	public void startAnimActivity(Intent intent) {
		this.startActivity(intent);
	}

	public void startAnimActivity(Class<?> cla) {
		getActivity().startActivity(new Intent(getActivity(), cla));
	}

}
