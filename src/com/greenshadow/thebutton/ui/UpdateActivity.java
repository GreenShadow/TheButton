package com.greenshadow.thebutton.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.config.Config;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UpdateActivity extends ActivityBase implements
		View.OnClickListener {
	private TextView tv_update_state, tv_update_details;
	private Button btn_update, btn_install, btn_downloading;
	private ProgressDialog progress;

	private String details;
	public boolean isComplete = false;

	private DownloadFileThread dft;

	private static final int DOWNLOAD_FAIL = -1;
	private static final int NOT_FOUND = 0;
	private static final int HAS_NEW_VERSION = 1;
	private static final int DOWNLOAD_COMPLETE = 2;
	private static final int DOWNLOAD_PROGRESS_UPDATE = 3;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_FAIL:
				btn_downloading.setVisibility(View.GONE);
				btn_update.setVisibility(View.VISIBLE);
				btn_update.setText("����ʧ�ܣ��������");
				break;
			case NOT_FOUND:
				progress.dismiss();
				tv_update_state.setText("�������°汾��");
				tv_update_details.setText("");
				break;
			case HAS_NEW_VERSION:
				progress.dismiss();
				tv_update_state.setText("�����°汾��");
				if (!TextUtils.isEmpty(details))
					tv_update_details.setText(details);
				btn_update.setVisibility(View.VISIBLE);
				break;
			case DOWNLOAD_COMPLETE:
				btn_downloading.setVisibility(View.GONE);
				btn_install.setVisibility(View.VISIBLE);
				break;
			case DOWNLOAD_PROGRESS_UPDATE:
				btn_downloading.setText("�����أ�" + dft.getDownloadedSize()
						+ "MB ���ȡ��");
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		initView();
		checkNewVersion();
	}

	private void initView() {
		initTopBarForLeft("����");

		tv_update_state = (TextView) findViewById(R.id.tv_update_state);
		tv_update_details = (TextView) findViewById(R.id.tv_update_details);
		btn_update = (Button) findViewById(R.id.btn_update);
		btn_install = (Button) findViewById(R.id.btn_install);
		btn_downloading = (Button) findViewById(R.id.btn_downloading);
		progress = new ProgressDialog(this);

		btn_update.setVisibility(View.GONE);
		btn_install.setVisibility(View.GONE);
		btn_downloading.setVisibility(View.GONE);
		btn_update.setOnClickListener(this);
		btn_install.setOnClickListener(this);
		btn_downloading.setOnClickListener(this);

		progress.setCanceledOnTouchOutside(false);
		progress.setMessage("���ڼ�����");
		progress.show();

	}

	/**
	 * ����°汾
	 */
	private void checkNewVersion() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				int versionCode = -1;
				int versionOnServer = -1;
				try {
					PackageInfo pi = UpdateActivity.this.getPackageManager()
							.getPackageInfo(
									UpdateActivity.this.getPackageName(), 0);
					versionCode = pi.versionCode;
				} catch (NameNotFoundException e) {
					Log.w("�ж��°汾", "����δ�ҵ�");
					e.printStackTrace();
				}

				BufferedReader bufferedReader = null;
				try {
					URL url = new URL(Config.VERSION_URL);
					bufferedReader = new BufferedReader(new InputStreamReader(
							url.openStream()));
					// ��ȡ�������ļ�
					String line = bufferedReader.readLine();
					versionOnServer = Integer.parseInt(line); // ��ȡ�������ϵİ汾��
				} catch (MalformedURLException e) {
					Log.w("�ж��°汾", "url����ʧ��");
					e.printStackTrace();
				} catch (IOException e) {
					Log.w("�ж��°汾", "�ļ���ȡʧ��");
					e.printStackTrace();
				} finally {
					// ����Ҫ�ر�BufferedReader
					try {
						if (bufferedReader != null) {
							bufferedReader.close();
						}
					} catch (IOException e) {
						Log.w("�ж��°汾", "BufferedReader�ر��쳣");
						e.printStackTrace();
					}
				}

				if (versionCode > 0 && versionOnServer > 0) {
					if (versionCode < versionOnServer) {
						getDetails(); // ���°汾ʱ����ȡ�汾��Ϣ
						return;
					}
				}
				mHandler.sendEmptyMessage(NOT_FOUND);
				return;
			}
		});
		thread.start();
	}

	/**
	 * ��ȡ�汾��Ϣ
	 */
	private void getDetails() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				BufferedReader bufferedReader = null;
				try {
					URL url = new URL(Config.NEW_VERSION_DETAILS);
					bufferedReader = new BufferedReader(new InputStreamReader(
							url.openStream()));
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						details += line + "\n";
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				mHandler.sendEmptyMessage(HAS_NEW_VERSION); // ��handlerͨ��
			}
		});
		thread.start();
	}

	/**
	 * ����
	 */
	private void download() {
		String filePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		filePath += "/Android/data/com.greenshadow.thebutton";
		// String filePath = this.getFilesDir().getAbsolutePath();
		File path = new File(filePath);
		if (!path.exists())
			path.mkdir();
		String fileName = filePath + "/TheButtonUpdate.apk";
		File file = new File(fileName);
		if (file.exists())
			file.delete();
		dft = new DownloadFileThread(Config.APK_URL, fileName);
		Thread download = new Thread(dft);
		download.start();
		Thread progress = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!isComplete && dft != null) {
					mHandler.sendEmptyMessage(DOWNLOAD_PROGRESS_UPDATE);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				isComplete = false;
			}
		});
		progress.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_update:
			btn_update.setVisibility(View.GONE);
			btn_downloading.setVisibility(View.VISIBLE);
			download();
			break;
		case R.id.btn_install:
			Uri uri = Uri.fromFile(dft.getFile());
			Intent installIntent = new Intent(Intent.ACTION_VIEW);
			installIntent.setDataAndType(uri,
					"application/vnd.android.package-archive");
			startActivity(installIntent);
			break;
		case R.id.btn_downloading:
			if (dft != null) {
				dft.interuped();
				btn_downloading.setVisibility(View.GONE);
				btn_update.setVisibility(View.VISIBLE);
				dft = null;
			}
			break;
		}
	}

	/**
	 * �����߳�
	 */
	private class DownloadFileThread implements Runnable {
		private boolean isInterupted = false;
		private String fileName;
		private URL url;

		private int total = 0;

		public DownloadFileThread(String url, String fileName) {
			File file = new File(fileName);
			if (file.exists())
				file.delete();

			this.fileName = fileName;

			try {
				this.url = new URL(url);
			} catch (MalformedURLException e) {
				Log.w("�����ļ�", "url����ʧ��");
				e.printStackTrace();
			}
		}

		/**
		 * ����
		 */
		@Override
		public void run() {
			try {
				HttpsURLConnection connection = (HttpsURLConnection) url
						.openConnection();
				connection.setRequestProperty("Accept-Encoding", "identity");
				connection.setConnectTimeout(5000);
				connection.setReadTimeout(20000);
				connection.connect();
				InputStream is = connection.getInputStream();
				byte[] buffer = new byte[1024];
				int len;
				FileOutputStream fos = new FileOutputStream(fileName);
				while (!isInterupted && (len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);

					total += len;
				}

				// ��ȡ��ϣ��ر���
				is.close();
				fos.close();

				isComplete = true;

				if (isInterupted) {
					File file = new File(fileName);
					if (file.exists())
						file.delete();
				} else {
					mHandler.sendEmptyMessage(DOWNLOAD_COMPLETE);
				}
			} catch (FileNotFoundException e) {
				Log.w("�����ļ�", "�ļ�δ�ҵ�");
				e.printStackTrace();
				mHandler.sendEmptyMessage(DOWNLOAD_FAIL);
			} catch (IOException e) {
				Log.w("�����ļ�", "�ļ���ȡʧ��");
				e.printStackTrace();
				mHandler.sendEmptyMessage(DOWNLOAD_FAIL);
			}
		}

		public int getDownloadedSize() {
			return total / 1024 / 1024;
		}

		/**
		 * �������
		 */
		public void interuped() {
			isInterupted = true;
			isComplete = true;
		}

		public File getFile() {
			return new File(fileName);
		}
	}
}
