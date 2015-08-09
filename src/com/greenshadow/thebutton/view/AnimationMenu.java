package com.greenshadow.thebutton.view;

import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.ui.ChooseFriendActivity;
import com.greenshadow.thebutton.util.PixelUtil;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AnimationMenu extends LinearLayout implements View.OnClickListener {

	private Context customContext;
	private AnimationMenuButton sendToFriend, openWithBrowser, copyLink,
			refresh;
	private WebView webView;
	private ClipboardManager clipboardManager;

	public AnimationMenu(Context context) {
		this(context, null);
	}

	public AnimationMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AnimationMenu(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		customContext = context;
		init();
	}

	@SuppressLint("InflateParams")
	private void init() {
		View v = LayoutInflater.from(customContext).inflate(
				R.layout.animation_menu, null);
		sendToFriend = (AnimationMenuButton) v.findViewById(R.id.sendToFriend);
		openWithBrowser = (AnimationMenuButton) v
				.findViewById(R.id.openWithBrowser);
		copyLink = (AnimationMenuButton) v.findViewById(R.id.copyLink);
		refresh = (AnimationMenuButton) v.findViewById(R.id.refresh);

		sendToFriend.setOnClickListener(this);
		openWithBrowser.setOnClickListener(this);
		copyLink.setOnClickListener(this);
		refresh.setOnClickListener(this);

		addView(v);
	}

	@Override
	public void onClick(View v) {
		AnimationMenuButton button = (AnimationMenuButton) v;
		if (webView != null) {
			String url = webView.getUrl();
			switch (button.getId()) {
			case R.id.sendToFriend:
				if (TextUtils.isEmpty(webView.getTitle())) {
					Toast.makeText(customContext, "网页还未加载完成，请稍后...",
							Toast.LENGTH_LONG).show();
					break;
				}
				Intent intentChooseFriend = new Intent(customContext,
						ChooseFriendActivity.class);
				intentChooseFriend.putExtra("url", url);
				intentChooseFriend.putExtra("title", webView.getTitle());
				customContext.startActivity(intentChooseFriend);
				break;
			case R.id.openWithBrowser:
				Intent intentOpenOtherBrowser = new Intent(Intent.ACTION_VIEW);
				intentOpenOtherBrowser.setData(Uri.parse(url));
				customContext.startActivity(intentOpenOtherBrowser);
				break;
			case R.id.copyLink:
				clipboardManager = (ClipboardManager) customContext
						.getSystemService(Context.CLIPBOARD_SERVICE);
				clipboardManager.setPrimaryClip(ClipData.newPlainText(null,
						webView.getUrl()));
				Toast.makeText(customContext, "已复制", Toast.LENGTH_LONG).show();
				break;
			case R.id.refresh:
				webView.reload();
				break;
			}
		}

		button.selectAnimation();
		this.setVisibility(View.GONE);
	}

	public void setWebView(WebView wv) {
		webView = wv;
	}

	public void openOrCloseMenu() {
		if (this.getVisibility() != View.VISIBLE) {
			long initalTime = 300l;
			long inervalTime = 300l;
			AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
			alphaAnimation.setDuration(200l);
			TranslateAnimation sendToFriendTranslateAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0.0f, // from X
					Animation.RELATIVE_TO_SELF, 0.0f, // to X
					Animation.ABSOLUTE, PixelUtil.dp2px(48.0f), // from Y
					Animation.RELATIVE_TO_PARENT, 0.0f);// to Y
			sendToFriendTranslateAnimation.setDuration(initalTime);
			TranslateAnimation openWithBrowserTranslateAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0.0f, // from X
					Animation.RELATIVE_TO_SELF, 0.0f, // to X
					Animation.ABSOLUTE, PixelUtil.dp2px(48.0f), // from Y
					Animation.RELATIVE_TO_PARENT, 0.0f);// to Y
			openWithBrowserTranslateAnimation.setDuration(initalTime
					+ inervalTime);
			TranslateAnimation copyLinkTranslateAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0.0f, // from X
					Animation.RELATIVE_TO_SELF, 0.0f, // to X
					Animation.ABSOLUTE, PixelUtil.dp2px(48.0f), // from Y
					Animation.RELATIVE_TO_PARENT, 0.0f);// to Y
			copyLinkTranslateAnimation
					.setDuration(initalTime + 2 * inervalTime);
			TranslateAnimation refreshTranslateAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0.0f, // from X
					Animation.RELATIVE_TO_SELF, 0.0f, // to X
					Animation.ABSOLUTE, PixelUtil.dp2px(48.0f), // from Y
					Animation.RELATIVE_TO_PARENT, 0.0f);// to Y
			refreshTranslateAnimation.setDuration(initalTime + 3 * inervalTime);
			this.startAnimation(alphaAnimation);
			this.setVisibility(View.VISIBLE);
			sendToFriend.startAnimation(sendToFriendTranslateAnimation);
			openWithBrowser.startAnimation(openWithBrowserTranslateAnimation);
			copyLink.startAnimation(copyLinkTranslateAnimation);
			refresh.startAnimation(refreshTranslateAnimation);
		} else {
			AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
			alphaAnimation.setDuration(200l);
			this.startAnimation(alphaAnimation);
			this.setVisibility(View.GONE);
		}
	}
}
