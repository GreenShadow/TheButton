package com.greenshadow.thebutton.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;

import com.greenshadow.thebutton.CustomApplication;
import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.ui.AboutActivity;
import com.greenshadow.thebutton.ui.FragmentBase;
import com.greenshadow.thebutton.ui.LoginActivity;
import com.greenshadow.thebutton.ui.SetMyInfoActivity;
import com.greenshadow.thebutton.util.SharePreferenceUtil;

/**
 * 设置
 */
@SuppressLint("SimpleDateFormat")
public class SettingsFragment extends FragmentBase implements OnClickListener {

	private Button btn_logout;
	private TextView tv_set_name;
	private RelativeLayout layout_info, rl_switch_voice, rl_switch_vibrate,
			rl_switch_notifacation, rl_about;

	private ImageView iv_open_voice, iv_close_voice, iv_open_vibrate,
			iv_close_vibrate, iv_open_notifacation, iv_close_notifacation;

	private SharePreferenceUtil mSharedUtil;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSharedUtil = mApplication.getSpUtil();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_set, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initData();
	}

	private void initView() {
		initTopBarForOnlyTitle("设置");
		layout_info = (RelativeLayout) findViewById(R.id.layout_info);
		rl_switch_voice = (RelativeLayout) findViewById(R.id.rl_switch_voice);
		rl_switch_vibrate = (RelativeLayout) findViewById(R.id.rl_switch_vibrate);
		rl_switch_notifacation = (RelativeLayout) findViewById(R.id.rl_switch_notifacation);
		rl_about = (RelativeLayout) findViewById(R.id.rl_about);
		rl_switch_voice.setOnClickListener(this);
		rl_switch_vibrate.setOnClickListener(this);
		rl_switch_notifacation.setOnClickListener(this);
		rl_about.setOnClickListener(this);

		iv_open_voice = (ImageView) findViewById(R.id.iv_open_voice);
		iv_close_voice = (ImageView) findViewById(R.id.iv_close_voice);
		iv_open_vibrate = (ImageView) findViewById(R.id.iv_open_vibrate);
		iv_close_vibrate = (ImageView) findViewById(R.id.iv_close_vibrate);
		iv_open_notifacation = (ImageView) findViewById(R.id.iv_open_notifacation);
		iv_close_notifacation = (ImageView) findViewById(R.id.iv_close_notifacation);

		tv_set_name = (TextView) findViewById(R.id.tv_set_name);
		btn_logout = (Button) findViewById(R.id.btn_logout);

		boolean isAllowVoice = mSharedUtil.isAllowVoice();
		if (isAllowVoice) {
			iv_open_voice.setVisibility(View.VISIBLE);
			iv_close_voice.setVisibility(View.INVISIBLE);
		} else {
			iv_open_voice.setVisibility(View.INVISIBLE);
			iv_close_voice.setVisibility(View.VISIBLE);
		}
		boolean isAllowVibrate = mSharedUtil.isAllowVibrate();
		if (isAllowVibrate) {
			iv_open_vibrate.setVisibility(View.VISIBLE);
			iv_close_vibrate.setVisibility(View.INVISIBLE);
		} else {
			iv_open_vibrate.setVisibility(View.INVISIBLE);
			iv_close_vibrate.setVisibility(View.VISIBLE);
		}
		btn_logout.setOnClickListener(this);
		layout_info.setOnClickListener(this);
	}

	private void initData() {
		tv_set_name.setText(BmobUserManager.getInstance(getActivity())
				.getCurrentUser().getUsername());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_info:// 启动到个人资料页面
			Intent intent = new Intent(getActivity(), SetMyInfoActivity.class);
			intent.putExtra("from", "me");
			startActivity(intent);
			break;
		case R.id.btn_logout:
			CustomApplication.getInstance().logout();
			getActivity().finish();
			startAnimActivity(LoginActivity.class);
			break;
		case R.id.rl_switch_voice:
			if (iv_open_voice.getVisibility() == View.VISIBLE) {
				iv_open_voice.setVisibility(View.INVISIBLE);
				iv_close_voice.setVisibility(View.VISIBLE);
				mSharedUtil.setAllowVoiceEnable(false);
			} else {
				iv_open_voice.setVisibility(View.VISIBLE);
				iv_close_voice.setVisibility(View.INVISIBLE);
				mSharedUtil.setAllowVoiceEnable(true);
			}
			break;
		case R.id.rl_switch_vibrate:
			if (iv_open_vibrate.getVisibility() == View.VISIBLE) {
				iv_open_vibrate.setVisibility(View.INVISIBLE);
				iv_close_vibrate.setVisibility(View.VISIBLE);
				mSharedUtil.setAllowVibrateEnable(false);
			} else {
				iv_open_vibrate.setVisibility(View.VISIBLE);
				iv_close_vibrate.setVisibility(View.INVISIBLE);
				mSharedUtil.setAllowVibrateEnable(true);
			}
			break;
		case R.id.rl_switch_notifacation:
			if (iv_open_notifacation.getVisibility() == View.VISIBLE) {
				iv_open_notifacation.setVisibility(View.INVISIBLE);
				iv_close_notifacation.setVisibility(View.VISIBLE);
				mSharedUtil.setPushNotifyEnable(false);
			} else {
				iv_open_notifacation.setVisibility(View.VISIBLE);
				iv_close_notifacation.setVisibility(View.INVISIBLE);
				mSharedUtil.setPushNotifyEnable(true);
			}
			break;
		case R.id.rl_about:
			startAnimActivity(AboutActivity.class);
		}
	}
}
