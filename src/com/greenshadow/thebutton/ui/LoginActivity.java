package com.greenshadow.thebutton.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.SaveListener;

import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.bean.User;
import com.greenshadow.thebutton.config.BmobConstants;
import com.greenshadow.thebutton.util.CommonUtils;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private EditText et_username, et_password;
	private Button btn_login;
	private TextView btn_register;

	private MyBroadcastReceiver receiver = new MyBroadcastReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		init();
		// ע���˳��㲥
		IntentFilter filter = new IntentFilter();
		filter.addAction(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH);
		registerReceiver(receiver, filter);
	}

	private void init() {
		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_register = (TextView) findViewById(R.id.btn_register);
		btn_login.setOnClickListener(this);
		btn_register.setOnClickListener(this);
	}

	public class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null
					&& BmobConstants.ACTION_REGISTER_SUCCESS_FINISH
							.equals(intent.getAction())) {
				finish();
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v == btn_register) {
			Intent intent = new Intent(LoginActivity.this,
					RegisterActivity.class);
			startActivity(intent);
		} else {
			boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
			if (!isNetConnected) {
				ShowToast(R.string.network_tips);
				return;
			}
			login();
		}
	}

	private void login() {
		String name = et_username.getText().toString();
		String password = et_password.getText().toString();

		if (TextUtils.isEmpty(name)) {
			ShowToast(R.string.toast_error_username_null);
			return;
		}

		if (TextUtils.isEmpty(password)) {
			ShowToast(R.string.toast_error_password_null);
			return;
		}

		final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
		progress.setMessage("���ڵ�½...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		User user = new User();
		user.setUsername(name);
		user.setPassword(password);
		userManager.login(user, new SaveListener() {
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progress.setMessage("���ڻ�ȡ�����б�...");
					}
				});
				// �����û��ĵ���λ���Լ����ѵ�����
				updateUserInfos();
				progress.dismiss();
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onFailure(int errorcode, String arg0) {
				// TODO Auto-generated method stub
				progress.dismiss();
				BmobLog.i(arg0);
				ShowToast(arg0);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
}