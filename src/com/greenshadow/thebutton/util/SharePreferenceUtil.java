package com.greenshadow.thebutton.util;

import java.util.HashSet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 首选项管理
 */
@SuppressLint("CommitPrefEdits")
public class SharePreferenceUtil {
	private SharedPreferences mSharedPreferences;
	private static SharedPreferences.Editor editor;
	private String SHARED_KEY_NOTIFY = "shared_key_notify";
	private String SHARED_KEY_VOICE = "shared_key_sound";
	private String SHARED_KEY_VIBRATE = "shared_key_vibrate";
	private String SHARED_KEY_UNINTERESTED = "shared_key_uninterested";

	public SharePreferenceUtil(Context context, String name) {
		mSharedPreferences = context.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		editor = mSharedPreferences.edit();
	}

	// 是否允许推送通知
	public boolean isAllowPushNotify() {
		return mSharedPreferences.getBoolean(SHARED_KEY_NOTIFY, true);
	}

	public void setPushNotifyEnable(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_NOTIFY, isChecked);
		editor.commit();
	}

	// 允许声音
	public boolean isAllowVoice() {
		return mSharedPreferences.getBoolean(SHARED_KEY_VOICE, true);
	}

	public void setAllowVoiceEnable(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_VOICE, isChecked);
		editor.commit();
	}

	// 允许震动
	public boolean isAllowVibrate() {
		return mSharedPreferences.getBoolean(SHARED_KEY_VIBRATE, true);
	}

	public void setAllowVibrateEnable(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_VIBRATE, isChecked);
		editor.commit();
	}

	public void addUninterestedTags(String tag) {
		HashSet<String> uninterestedSet = (HashSet<String>) mSharedPreferences
				.getStringSet(SHARED_KEY_UNINTERESTED, new HashSet<String>());
		uninterestedSet.add(tag);
		editor.putStringSet(SHARED_KEY_UNINTERESTED, uninterestedSet);
		editor.commit();
	}

	public boolean isUninterested(String tag) {
		HashSet<String> uninterestedSet = (HashSet<String>) mSharedPreferences
				.getStringSet(SHARED_KEY_UNINTERESTED, new HashSet<String>());
		// for (String item : uninterestedSet) {
		// if (tag.equals(item))
		// return true;
		// }
		if (uninterestedSet.contains(tag))
			return true;
		return false;
	}
}
