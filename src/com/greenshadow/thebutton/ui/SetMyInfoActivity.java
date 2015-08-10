package com.greenshadow.thebutton.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.bean.User;
import com.greenshadow.thebutton.config.BmobConstants;
import com.greenshadow.thebutton.util.ImageLoadOptions;
import com.greenshadow.thebutton.util.PhotoUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * ��������ҳ��
 */
@SuppressLint({ "SimpleDateFormat", "ClickableViewAccessibility",
		"InflateParams" })
public class SetMyInfoActivity extends ActivityBase implements OnClickListener {

	private TextView tv_set_name, tv_set_nick, tv_set_gender;
	private ImageView iv_set_avator, iv_arraw;
	private EditText et_set_nick;
	private LinearLayout layout_all;

	private Button btn_chat, btn_add_friend;
	private RelativeLayout layout_head, layout_nick, layout_gender,
			layout_black_tips;

	private String from = "";
	private String username = "";
	private User user;
	private String[] sexs = new String[] { "��", "Ů" };
	private RelativeLayout layout_choose;
	private RelativeLayout layout_photo;
	private PopupWindow avatorPop;
	private RelativeLayout popRelativeLayout;
	private String filePath = "";

	private boolean isFromCamera = false;// ����������ת
	private int degree = 0;
	private String path;

	private InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= 14) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		}
		setContentView(R.layout.activity_set_info);
		from = getIntent().getStringExtra("from");// me add other
		username = getIntent().getStringExtra("username");

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		initView();
	}

	private void initView() {
		layout_all = (LinearLayout) findViewById(R.id.layout_all);
		iv_set_avator = (ImageView) findViewById(R.id.iv_set_avator);
		iv_arraw = (ImageView) findViewById(R.id.iv_arraw);
		tv_set_name = (TextView) findViewById(R.id.tv_set_name);
		tv_set_nick = (TextView) findViewById(R.id.tv_set_nick);
		layout_head = (RelativeLayout) findViewById(R.id.layout_head);
		layout_nick = (RelativeLayout) findViewById(R.id.layout_nick);
		layout_gender = (RelativeLayout) findViewById(R.id.layout_gender);
		// ��������ʾ��
		layout_black_tips = (RelativeLayout) findViewById(R.id.layout_black_tips);
		tv_set_gender = (TextView) findViewById(R.id.tv_set_gender);
		btn_chat = (Button) findViewById(R.id.btn_chat);
		btn_add_friend = (Button) findViewById(R.id.btn_add_friend);

		btn_add_friend.setEnabled(false);
		btn_chat.setEnabled(false);
		if (from.equals("me")) {
			et_set_nick = (EditText) findViewById(R.id.et_set_nick);
			initTopBarForLeft("��������");
			layout_all.setOnClickListener(this);
			layout_head.setOnClickListener(this);
			layout_nick.setOnClickListener(this);
			layout_gender.setOnClickListener(this);
			iv_arraw.setVisibility(View.VISIBLE);
			btn_chat.setVisibility(View.GONE);
			btn_add_friend.setVisibility(View.GONE);
			setNickListener();
		} else {
			initTopBarForLeft("��ϸ����");
			iv_arraw.setVisibility(View.INVISIBLE);
			btn_chat.setVisibility(View.VISIBLE);
			btn_chat.setOnClickListener(this);
			if (from.equals("add")) {
				if (!mApplication.getContactList().containsKey(username)) {
					btn_add_friend.setVisibility(View.VISIBLE);
					btn_add_friend.setOnClickListener(this);
				}
			}
			initOtherData(username);
		}
	}

	/**
	 * Ϊet_set_nick������ɼ�����
	 */
	private void setNickListener() {
		et_set_nick
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_DONE)
							updateNick();
						return true;
					}
				});
	}

	/**
	 * �����ǳ�
	 */
	private void updateNick() {
		if (et_set_nick.getText().toString()
				.equals(tv_set_nick.getText().toString())) {
			ShowToast("δ���޸ģ�");
			et_set_nick.setVisibility(View.GONE);
			tv_set_nick.setText(et_set_nick.getText());
			tv_set_nick.setVisibility(View.VISIBLE);
			imm.hideSoftInputFromWindow(et_set_nick.getWindowToken(), 0); // ǿ���������뷨
			return;
		}

		User u = new User();
		u.setNick(et_set_nick.getText().toString());
		updateUserData(u, new UpdateListener() {
			@Override
			public void onSuccess() {
				ShowToast("�޸ĳɹ���");
				et_set_nick.setVisibility(View.GONE);
				tv_set_nick.setText(et_set_nick.getText());
				tv_set_nick.setVisibility(View.VISIBLE);
				imm.hideSoftInputFromWindow(et_set_nick.getWindowToken(), 0); // ǿ���������뷨
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast("�޸�ʧ�ܣ�" + arg1);
			}
		});
	}

	private void initMeData() {
		User user = userManager.getCurrentUser(User.class);
		BmobLog.i("hight = " + user.getHight() + ",sex= " + user.getSex());
		initOtherData(user.getUsername());
	}

	private void initOtherData(String name) {
		userManager.queryUser(name, new FindListener<User>() {
			@Override
			public void onError(int arg0, String arg1) {
				ShowLog("onError onError:" + arg1);
			}

			@Override
			public void onSuccess(List<User> arg0) {
				if (arg0 != null && arg0.size() > 0) {
					user = arg0.get(0);
					btn_chat.setEnabled(true);
					btn_add_friend.setEnabled(true);
					updateUser(user);
				} else {
					ShowLog("onSuccess ���޴���");
				}
			}
		});
	}

	private void updateUser(User user) {
		// ����
		refreshAvatar(user.getAvatar());
		tv_set_name.setText(user.getUsername());
		tv_set_nick.setText(user.getNick());
		tv_set_gender.setText(user.getSex() == true ? "��" : "Ů");
		// ����Ƿ�Ϊ�������û�
		if (from.equals("other")) {
			if (BmobDB.create(this).isBlackUser(user.getUsername())) {
				layout_black_tips.setVisibility(View.VISIBLE);
			} else {
				layout_black_tips.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * ����ͷ�� refreshAvatar
	 */
	private void refreshAvatar(String avatar) {
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_set_avator,
					ImageLoadOptions.getOptions());
		} else {
			iv_set_avator.setImageResource(R.drawable.default_head);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (from.equals("me")) {
			initMeData();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_all:
			if (et_set_nick != null) {
				if (et_set_nick.getVisibility() == View.VISIBLE)
					updateNick();
			}
			break;
		case R.id.btn_chat:// ��������
			if (ChatActivity.chatActivityInstance != null
					&& ChatActivity.chatActivityInstance.targetUser
							.getObjectId().equals(user.getObjectId())) {
			} else {
				Intent intent = new Intent(this, ChatActivity.class);
				intent.putExtra("user", user);
				startAnimActivity(intent);
			}
			this.finish();
			break;
		case R.id.layout_head:
			if (et_set_nick != null) {
				if (et_set_nick.getVisibility() == View.VISIBLE)
					updateNick();
			}
			showAvatarPop();
			break;
		case R.id.layout_nick:
			if (et_set_nick != null) {
				if (et_set_nick.getVisibility() == View.GONE) {
					et_set_nick.setVisibility(View.VISIBLE);
					et_set_nick.requestFocus();
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); // ��ʾ���뷨
					et_set_nick.setText(tv_set_nick.getText());
					et_set_nick.selectAll();
					tv_set_nick.setVisibility(View.GONE);
				} else {
					updateNick();
				}
			}
			break;
		case R.id.layout_gender:// �Ա�
			if (et_set_nick != null) {
				if (et_set_nick.getVisibility() == View.VISIBLE)
					updateNick();
			}
			showSexChooseDialog();
			break;
		case R.id.btn_add_friend:// ��Ӻ���
			addFriend();
			break;
		case R.id.rl_pop_show_avator: // ʹPopupWindow��ʧ
			if (avatorPop != null)
				avatorPop.dismiss();
			break;
		}
	}

	private void showSexChooseDialog() {
		final int choose = tv_set_gender.getText().toString().equals("��") ? 0
				: 1;
		new AlertDialog.Builder(this)
				.setSingleChoiceItems(sexs, choose,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == choose)
									ShowToast("δ���޸ģ�");
								else
									updateInfo(which);
								dialog.dismiss();
							}
						}) //
				.setNegativeButton("ȡ��", null) //
				.show();
	}

	/**
	 * �޸����� updateInfo
	 */
	private void updateInfo(int which) {
		final User u = new User();
		if (which == 0) {
			u.setSex(true);
		} else {
			u.setSex(false);
		}
		updateUserData(u, new UpdateListener() {
			@Override
			public void onSuccess() {
				ShowToast("�޸ĳɹ�");
				tv_set_gender.setText(u.getSex() == true ? "��" : "Ů");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast("onFailure:" + arg1);
			}
		});
	}

	/**
	 * ��Ӻ�������
	 */
	private void addFriend() {
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("�������...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		// ����tag����
		BmobChatManager.getInstance(this).sendTagMessage(
				BmobConfig.TAG_ADD_CONTACT, user.getObjectId(),
				new PushListener() {
					@Override
					public void onSuccess() {
						progress.dismiss();
						ShowToast("��������ɹ����ȴ��Է���֤��");
					}

					@Override
					public void onFailure(int arg0, final String arg1) {
						progress.dismiss();
						ShowToast("��������ɹ����ȴ��Է���֤��");
						ShowLog("��������ʧ��:" + arg1);
					}
				});
	}

	private void showAvatarPop() {
		View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator,
				null);
		popRelativeLayout = (RelativeLayout) view
				.findViewById(R.id.rl_pop_show_avator);
		popRelativeLayout.setOnClickListener(this);
		view.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (avatorPop != null) {
						avatorPop.dismiss();
					}
				}
				return true;
			}
		});
		layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
		layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
		layout_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ShowLog("�������");
				layout_choose.setBackgroundColor(getResources().getColor(
						R.color.base_color_text_white));
				layout_photo.setBackgroundResource(R.drawable.pop_bg_press);
				File dir = new File(BmobConstants.MyAvatarDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				// ԭͼ
				File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date()));
				filePath = file.getAbsolutePath();// ��ȡ��Ƭ�ı���·��
				Uri imageUri = Uri.fromFile(file);

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
			}
		});
		layout_choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ShowLog("������");
				layout_photo.setBackgroundColor(getResources().getColor(
						R.color.base_color_text_white));
				layout_choose.setBackgroundResource(R.drawable.pop_bg_press);
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
			}
		});

		avatorPop = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);

		// ����Ч�� �ӵײ�����
		avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
		avatorPop.setBackgroundDrawable(new BitmapDrawable((Resources) null));
		avatorPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
	}

	private void startImageAction(Uri uri, int outputX, int outputY,
			int requestCode) {
		Intent intent = null;
		intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		BmobLog.i(intent.toString());
		BmobLog.i(intent.getExtras().toString());
		startActivityForResult(intent, requestCode);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// �����޸�ͷ��
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD������");
					return;
				}
				isFromCamera = true;
				File file = new File(filePath);
				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
				Log.i("life", "���պ�ĽǶȣ�" + degree);
				startImageAction(Uri.fromFile(file), 200, 200,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP);
			}
			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION:// �����޸�ͷ��
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			if (data == null) {
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD������");
					return;
				}
				isFromCamera = false;
				Uri uri = data.getData();
				BmobLog.i("uri=" + uri.toString());
				startImageAction(uri, 200, 200,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP);
				BmobLog.i("ѡȡ���");
			} else {
				ShowToast("��Ƭ��ȡʧ��");
			}

			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP:// �ü�ͷ�񷵻�
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			if (resultCode == RESULT_OK) {
				if (data == null)
					return;
				saveCropAvator(data);
				// ��ʼ���ļ�·��
				filePath = "";
				// �ϴ�ͷ��
				uploadAvatar();
			} else {
				ShowToast("��Ƭ��ȡʧ��");
			}
			break;
		default:
			break;
		}
	}

	private void uploadAvatar() {
		BmobLog.i("ͷ���ַ��" + path);
		final BmobFile bmobFile = new BmobFile(new File(path));
		bmobFile.upload(this, new UploadFileListener() {
			@Override
			public void onSuccess() {
				String url = bmobFile.getFileUrl(SetMyInfoActivity.this);
				// ����BmobUser����
				updateUserAvatar(url);
			}

			@Override
			public void onProgress(Integer arg0) {
			}

			@Override
			public void onFailure(int arg0, String msg) {
				ShowToast("ͷ���ϴ�ʧ�ܣ�" + msg);
			}
		});
	}

	private void updateUserAvatar(final String url) {
		User u = new User();
		u.setAvatar(url);
		updateUserData(u, new UpdateListener() {
			@Override
			public void onSuccess() {
				ShowToast("ͷ����³ɹ���");
				// ����ͷ��
				refreshAvatar(url);
			}

			@Override
			public void onFailure(int code, String msg) {
				ShowToast("ͷ�����ʧ�ܣ�" + msg);
			}
		});
	}

	/**
	 * ����ü���ͷ��
	 */
	private void saveCropAvator(Intent data) {
		Bundle extras = data.getExtras();
		Bitmap bitmap = null;
		if (extras == null) {
			BmobLog.i("extras��");
			Uri uri = data.getData();
			if (uri == null) {
				BmobLog.i("uri��");
				return;
			}
			try {
				bitmap = MediaStore.Images.Media.getBitmap(
						this.getContentResolver(), uri);
			} catch (Exception e) {
				BmobLog.i("bitmap��");
				e.printStackTrace();
				return;
			}
		} else
			bitmap = extras.getParcelable("data");
		Log.i("life", "avatar - bitmap = " + bitmap);
		if (bitmap != null) {
			bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
			if (isFromCamera && degree != 0) {
				bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
			}
			iv_set_avator.setImageBitmap(bitmap);
			// ����ͼƬ
			String filename = new SimpleDateFormat("yyMMddHHmmss")
					.format(new Date()) + ".png";
			path = BmobConstants.MyAvatarDir + filename;
			BmobLog.i("pathΪ " + path);
			PhotoUtil.saveBitmap(BmobConstants.MyAvatarDir, filename, bitmap,
					true);
			// �ϴ�ͷ��
			if (bitmap != null && bitmap.isRecycled()) {
				bitmap.recycle();
			}
		}
	}

	private void updateUserData(User user, UpdateListener listener) {
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}
}
