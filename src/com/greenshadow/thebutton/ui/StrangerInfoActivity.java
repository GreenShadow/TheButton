package com.greenshadow.thebutton.ui;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.PushListener;

import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.bean.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 陌生人
 */
public class StrangerInfoActivity extends ActivityBase {
	private TextView strangerNick, strangerName;
	private Button playGame;

	private final int GAME = 0;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stranger);

		strangerNick = (TextView) findViewById(R.id.tv_stranger_nick);
		strangerName = (TextView) findViewById(R.id.tv_stranger_name);
		playGame = (Button) findViewById(R.id.btn_play_game);

		Bundle bundle = getIntent().getExtras();
		user = (User) bundle.getSerializable("info");

		strangerNick.setText(user.getNick());
		strangerName.setText(user.getUsername());
		playGame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StrangerInfoActivity.this,
						PuzzleGameActivity.class);
				intent.putExtra("name", user.getUsername());
				intent.putExtra("difficulty", user.getDifficulty());
				startActivityForResult(intent, GAME);
			}
		});

		initTopBarForLeft("陌生人");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case GAME:
			final ProgressDialog progress = new ProgressDialog(this);
			progress.setMessage("正在添加...");
			progress.setCanceledOnTouchOutside(false);
			progress.show();
			// 发送tag请求
			BmobChatManager.getInstance(this).sendTagMessage(
					BmobConfig.TAG_ADD_CONTACT, user.getObjectId(),
					new PushListener() {
						@Override
						public void onSuccess() {
							progress.dismiss();
							ShowToast("发送请求成功，等待对方验证！");
							StrangerInfoActivity.this.finish();
						}

						@Override
						public void onFailure(int arg0, final String arg1) {
							progress.dismiss();
							ShowToast("发送请求成功，等待对方验证！");
							ShowLog("发送请求失败:" + arg1);
						}
					});
			break;
		default:
			break;
		}
	}
}
