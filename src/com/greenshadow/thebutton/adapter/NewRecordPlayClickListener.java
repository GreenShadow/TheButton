package com.greenshadow.thebutton.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.View;
import android.widget.ImageView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.util.BmobLog;
import cn.bmob.im.util.BmobUtils;

import com.greenshadow.thebutton.R;

/**
 * ����¼���ļ�
 */
public class NewRecordPlayClickListener implements View.OnClickListener {

	private BmobMsg message;
	private ImageView iv_voice;
	private AnimationDrawable anim = null;
	private Context context;
	private String currentObjectId = "";
	private MediaPlayer mediaPlayer = null;
	private static BmobMsg currentMsg = null; // ��������������ͬ�����Ĳ���

	private BmobUserManager userManager;

	public static boolean isPlaying = false;
	public static NewRecordPlayClickListener currentPlayListener = null;

	public NewRecordPlayClickListener(Context context, BmobMsg msg,
			ImageView voice) {
		this.iv_voice = voice;
		this.message = msg;
		this.context = context;
		currentMsg = msg;
		currentPlayListener = this;
		currentObjectId = BmobUserManager.getInstance(context)
				.getCurrentUserObjectId();
		userManager = BmobUserManager.getInstance(context);
	}

	/**
	 * ��������
	 */
	@SuppressWarnings("resource")
	public void startPlayRecord(String filePath, boolean isUseSpeaker) {
		if (!(new File(filePath).exists())) {
			return;
		}
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		mediaPlayer = new MediaPlayer();
		if (isUseSpeaker) {
			audioManager.setMode(AudioManager.MODE_NORMAL);
			audioManager.setSpeakerphoneOn(true);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		} else {
			audioManager.setSpeakerphoneOn(false);// �ر�������
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
		}

		try {
			mediaPlayer.reset();
			FileInputStream fis = new FileInputStream(new File(filePath));
			mediaPlayer.setDataSource(fis.getFD());
			mediaPlayer.prepare();
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					isPlaying = true;
					currentMsg = message;
					arg0.start();
					startRecordAnimation();
				}
			});
			mediaPlayer
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							stopPlayRecord();
						}

					});
			currentPlayListener = this;
		} catch (Exception e) {
			BmobLog.i("���Ŵ���:" + e.getMessage());
		}
	}

	/**
	 * ֹͣ����
	 */
	public void stopPlayRecord() {
		stopRecordAnimation();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		isPlaying = false;
	}

	/**
	 * �������Ŷ���
	 */
	private void startRecordAnimation() {
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
	private void stopRecordAnimation() {
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
		if (isPlaying) {
			currentPlayListener.stopPlayRecord();
			if (currentMsg != null
					&& currentMsg.hashCode() == message.hashCode()) {
				currentMsg = null;
				return;
			}
		}
		BmobLog.i("voice", "����¼�");
		if (message.getBelongId().equals(currentObjectId)) {// ������Լ����͵�������Ϣ���򲥷ű��ص�ַ
			String localPath = message.getContent().split("&")[0];
			startPlayRecord(localPath, true);
		} else {// ������յ�����Ϣ������Ҫ�����غ󲥷�
			String localPath = getDownLoadFilePath(message);
			BmobLog.i("voice", "�յ��������洢�ĵ�ַ:" + localPath);
			startPlayRecord(localPath, true);
		}
	}

	public String getDownLoadFilePath(BmobMsg msg) {
		String accountDir = BmobUtils.string2MD5(userManager
				.getCurrentUserObjectId());
		File dir = new File(BmobConfig.BMOB_VOICE_DIR + File.separator
				+ accountDir + File.separator + msg.getBelongId());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// �ڵ�ǰ�û���Ŀ¼������¼���ļ�
		File audioFile = new File(dir.getAbsolutePath() + File.separator
				+ msg.getMsgTime() + ".amr");
		try {
			if (!audioFile.exists()) {
				audioFile.createNewFile();
			}
		} catch (IOException e) {
		}
		return audioFile.getAbsolutePath();
	}

}