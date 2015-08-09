package com.greenshadow.thebutton.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import cn.bmob.im.BmobPlayManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.inteface.OnPlayChangeListener;
import cn.bmob.im.util.BmobLog;

import com.greenshadow.thebutton.R;

/**
 * ����¼���ļ�
 */
public class RecordPlayClickListener implements View.OnClickListener {

	private BmobMsg message;
	private ImageView iv_voice;
	private AnimationDrawable anim = null;
	private BmobPlayManager playMananger;
	private String currentObjectId = "";
	private static BmobMsg currentMsg = null;// ��������������ͬ�����Ĳ���

	public static RecordPlayClickListener currentPlayListener = null;

	public RecordPlayClickListener(Context context, BmobMsg msg, ImageView voice) {
		this.iv_voice = voice;
		this.message = msg;
		currentMsg = msg;
		currentPlayListener = this;
		currentObjectId = BmobUserManager.getInstance(context)
				.getCurrentUserObjectId();
		playMananger = BmobPlayManager.getInstance(context);
		playMananger.setOnPlayChangeListener(new OnPlayChangeListener() {
			@Override
			public void onPlayStop() {
				currentPlayListener.stopRecordAnimation();
			}

			@Override
			public void onPlayStart() {
				currentPlayListener.startRecordAnimation();
			}
		});
	}

	/**
	 * �������Ŷ���
	 */
	public void startRecordAnimation() {
		if (message.getBelongId().equals(currentObjectId)) {
			iv_voice.setImageResource(R.anim.anim_chat_voice_right);
		} else {
			iv_voice.setImageResource(R.anim.anim_chat_voice_left);
		}
		anim = (AnimationDrawable) iv_voice.getDrawable();
		anim.start();
	}

	/**
	 * ֹͣ���Ŷ���
	 */
	public void stopRecordAnimation() {
		if (message.getBelongId().equals(currentObjectId)) {
			iv_voice.setImageResource(R.drawable.voice_left3);
		} else {
			iv_voice.setImageResource(R.drawable.voice_right3);
		}
		if (anim != null) {
			anim.stop();
		}
	}

	@Override
	public void onClick(View arg0) {
		if (playMananger.isPlaying()) {
			playMananger.stopPlayback();
			if (currentMsg != null
					&& currentMsg.hashCode() == message.hashCode()) {// �Ƿ���ͬ��������Ϣ
				currentMsg = null;
				return;
			}
		} else {
			String localPath = message.getContent().split("&")[0];
			BmobLog.i("voice", "���ص�ַ:" + localPath);
			if (message.getBelongId().equals(currentObjectId)) {// ������Լ����͵�������Ϣ���򲥷ű��ص�ַ
				playMananger.playRecording(localPath, true);
			}
		}
	}
}