package com.greenshadow.thebutton.ui;

import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.config.Config;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class AboutActivity extends ActivityBase implements View.OnClickListener {
	private RelativeLayout rl_about_features, rl_about_update,
			rl_about_main_page;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		initView();
	}

	private void initView() {
		initTopBarForLeft("¹ØÓÚ");

		rl_about_features = (RelativeLayout) findViewById(R.id.rl_about_features);
		rl_about_update = (RelativeLayout) findViewById(R.id.rl_about_update);
		rl_about_main_page = (RelativeLayout) findViewById(R.id.rl_about_main_page);
		rl_about_features.setOnClickListener(this);
		rl_about_update.setOnClickListener(this);
		rl_about_main_page.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_about_features:
			shouwURL(Config.README_URL);
			break;
		case R.id.rl_about_update:
			startActivity(new Intent(this, UpdateActivity.class));
			break;
		case R.id.rl_about_main_page:
			shouwURL(Config.MAIN_PAGE);
			break;
		}
	}

	private void shouwURL(String url) {
		Intent intent = new Intent(this, BrowserActivity.class);
		intent.putExtra("url", url);
		startActivity(intent);
	}
}
