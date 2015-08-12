package com.greenshadow.thebutton.util;

import java.util.ArrayList;
import java.util.HashSet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * ��ѡ�����
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

	// �Ƿ���������֪ͨ
	public boolean isAllowPushNotify() {
		return mSharedPreferences.getBoolean(SHARED_KEY_NOTIFY, true);
	}

	public void setPushNotifyEnable(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_NOTIFY, isChecked);
		editor.commit();
	}

	// ��������
	public boolean isAllowVoice() {
		return mSharedPreferences.getBoolean(SHARED_KEY_VOICE, true);
	}

	public void setAllowVoiceEnable(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_VOICE, isChecked);
		editor.commit();
	}

	// ������
	public boolean isAllowVibrate() {
		return mSharedPreferences.getBoolean(SHARED_KEY_VIBRATE, true);
	}

	public void setAllowVibrateEnable(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_VIBRATE, isChecked);
		editor.commit();
	}

	/**
	 * ��Ӳ�����Ȥ
	 */
	public void addUninterestedTag(String tag) {
		HashSet<String> uninterestedSet = (HashSet<String>) mSharedPreferences
				.getStringSet(SHARED_KEY_UNINTERESTED, new HashSet<String>());
		uninterestedSet.add(tag);
		editor.putStringSet(SHARED_KEY_UNINTERESTED, uninterestedSet);
		editor.commit();
	}

	/**
	 * ɾ��ĳһ�����Ȥ
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
	 * �ж��Ƿ�Ϊ������Ȥ
	 */
	public boolean isUninterested(String tag) {
		HashSet<String> uninterestedSet = (HashSet<String>) mSharedPreferences
				.getStringSet(SHARED_KEY_UNINTERESTED, new HashSet<String>());
		if (uninterestedSet.contains(tag))
			return true;
		return false;
	}

	/**
	 * �õ����еĲ�������Ȥ��ǩ
	 */
	public ArrayList<String> getAllUninterested() {
		HashSet<String> uninterestedSet = (HashSet<String>) mSharedPreferences
				.getStringSet(SHARED_KEY_UNINTERESTED, new HashSet<String>());
		ArrayList<String> uninterestedList = new ArrayList<String>(
				uninterestedSet);
		return uninterestedList;
	}
}
