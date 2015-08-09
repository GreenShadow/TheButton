package com.greenshadow.thebutton.ui;

import android.os.Bundle;
import android.widget.EditText;
import cn.bmob.v3.listener.UpdateListener;

import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.bean.User;
import com.greenshadow.thebutton.view.HeaderLayout.onRightImageButtonClickListener;

/**
 * �����ǳƺ��Ա�
 */
public class UpdateInfoActivity extends ActivityBase {

	private EditText edit_nick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_updateinfo);
		initView();
	}

	private void initView() {
		initTopBarForBoth("�޸��ǳ�", R.drawable.base_action_bar_true_bg_selector,
				new onRightImageButtonClickListener() {
					@Override
					public void onClick() {
						String nick = edit_nick.getText().toString();
						if (nick.equals("")) {
							ShowToast("����д�ǳ�!");
							return;
						}
						updateInfo(nick);
					}
				});
		edit_nick = (EditText) findViewById(R.id.edit_nick);
	}

	/**
	 * �޸����� updateInfo
	 */
	private void updateInfo(String nick) {
		final User user = userManager.getCurrentUser(User.class);
		User u = new User();
		u.setNick(nick);
		u.setHight(110);
		u.setObjectId(user.getObjectId());
		u.update(this, new UpdateListener() {
			@Override
			public void onSuccess() {
				final User c = userManager.getCurrentUser(User.class);
				ShowToast("�޸ĳɹ�:" + c.getNick());
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast("onFailure:" + arg1);
			}
		});
	}
}
