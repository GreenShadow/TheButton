package com.greenshadow.thebutton.util;

import java.util.ArrayList;
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

	/**
	 * 构造方法
	 * 
	 * @param context
	 * @param name
	 *            这里的名字是加入每一个用户的ID的，所以不会出现多用户混乱的情况
	 */
	public SharePreferenceUtil(Context context, String name) {
		mSharedPreferences = context.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		editor = mSharedPreferences.edit();
	}

	// 是否允许推送通知
	public boolean isAllowPushNotify() {
		return mSharedPreferences.getBoolean(SHARED_KEY_NOTIFY, true);
	}

	// 设置是否允许推送通知
	public void setPushNotifyEnable(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_NOTIFY, isChecked);
		editor.commit();
	}

	// 是否允许声音
	public boolean isAllowVoice() {
		return mSharedPreferences.getBoolean(SHARED_KEY_VOICE, true);
	}

	// 设置是否允许声音
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

	/**
	 * 添加不感兴趣
	 */
	public void addUninterestedTag(String tag) {
		HashSet<String> uninterestedSet = (HashSet<String>) mSharedPreferences
				.getStringSet(SHARED_KEY_UNINTERESTED, new HashSet<String>());
		uninterestedSet.add(tag);
		editor.putStringSet(SHARED_KEY_UNINTERESTED, uninterestedSet);
		editor.commit();
	}

	/**
	 * 删除某一项不感兴趣
	 */
	public void deleteUninterestedTag(String tag) {
		HashSet<String> uninterestedSet = (HashSet<String>) mSharedPreferences
				.getStringSet(SHARED_KEY_UNINTERESTED, new HashSet<String>());
		if (uninterestedSet.contains(tag))
			uninterestedSet.remove(tag);
		editor.remove(SHARED_KEY_UNINTERESTED);
		editor.commit();
		editor.putStringSet(SHARED_KEY_UNINTERESTED, uninterestedSet);
		editor.commit();
	}

	/**
	 * 判断是否为不感兴趣
	 */
	public boolean isUninterested(String tag) {
		HashSet<String> uninterestedSet = (HashSet<String>) mSharedPreferences
				.getStringSet(SHARED_KEY_UNINTERESTED, new HashSet<String>());
		if (uninterestedSet.contains(tag))
			return true;
		return false;
	}

	/**
	 * 得到所有的不给你兴趣标签
	 */
	public ArrayList<String> getAllUninterested() {
		HashSet<String> uninterestedSet = (HashSet<String>) mSharedPreferences
				.getStringSet(SHARED_KEY_UNINTERESTED, new HashSet<String>());
		ArrayList<String> uninterestedList = new ArrayList<String>(
				uninterestedSet);
		return uninterestedList;
	}
}
