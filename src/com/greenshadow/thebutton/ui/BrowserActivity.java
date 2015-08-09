package com.greenshadow.thebutton.ui;

import com.greenshadow.thebutton.CustomApplication;
import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.view.AnimationMenu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

/**
 * 浏览器
 */
public class BrowserActivity extends BaseActivity {
	private WebView webView;
	private View progressBar;
	private ImageButton close, openMenu;
	private TextView title;
	private AnimationMenu menu;

	private int screenWidth;
	private String from;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);

		Intent intent = getIntent();
		from = intent.getStringExtra("from");
		String url = intent.getStringExtra("url");
		if (!url.startsWith("http") && !url.startsWith("ftp"))
			url = "http://" + url;

		if (from.equals("push"))
			CustomApplication.getInstance().getNotificationUtil()
					.canceledANotification();

		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		screenWidth = size.x;

		webView = (WebView) findViewById(R.id.webView);
		progressBar = findViewById(R.id.progressBar);
		close = (ImageButton) findViewById(R.id.close);
		openMenu = (ImageButton) findViewById(R.id.openMenu);
		title = (TextView) findViewById(R.id.title);
		menu = (AnimationMenu) findViewById(R.id.animationMenu);

		openMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				menu.openOrCloseMenu();
			}
		});
		close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BrowserActivity.this.finish();
			}
		});

		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					progressBar.setVisibility(View.GONE);
					return;
				}
				if (progressBar.getVisibility() != View.VISIBLE)
					progressBar.setVisibility(View.VISIBLE);
				RelativeLayout.LayoutParams lp = (LayoutParams) progressBar
						.getLayoutParams();
				lp.width = screenWidth * newProgress / 100;
				progressBar.setLayoutParams(lp);
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				BrowserActivity.this.title.setText(title);
			}
		});
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		webView.loadUrl(url);

		menu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				menu.openOrCloseMenu();
			}
		});
		menu.setWebView(webView);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (from.equals("push")) // 如果从同送通知调起则按下返回键直接关闭Activity
				this.finish();
			else {
				if (webView.canGoBack())
					webView.goBack();
				else
					this.finish();
			}
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			openMenu.performClick();
		}

		return true;
	}

	@Override
	protected void onDestroy() {
		webView.clearCache(true); // 清除浏览器数据
		super.onDestroy();
	}
}
