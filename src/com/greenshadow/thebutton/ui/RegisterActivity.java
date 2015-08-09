package com.greenshadow.thebutton.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.bean.User;
import com.greenshadow.thebutton.config.BmobConstants;
import com.greenshadow.thebutton.util.CommonUtils;
import com.greenshadow.thebutton.util.ImageLoadOptions;
import com.greenshadow.thebutton.util.PhotoUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("SimpleDateFormat")
public class RegisterActivity extends BaseActivity {

	private Button btn_register;
	private EditText et_username, et_password, et_password_confirm, et_nick;
	private ImageView iv_set_avator;
	private RelativeLayout layout_set_avator;
	private boolean haveAvator = false;
	private String path;
	private String filePath = "";
	private PopupWindow avatorPop;
	private boolean isFromCamera = false;// ����������ת
	private int degree = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		initTopBarForLeft("ע��");

		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		et_password_confirm = (EditText) findViewById(R.id.et_password_confirm);
		et_nick = (EditText) findViewById(R.id.et_nick);
		iv_set_avator = (ImageView) findViewById(R.id.iv_set_avator);
		layout_set_avator = (RelativeLayout) findViewById(R.id.layout_set_avator);
		layout_set_avator.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAvatarPop();
			}
		});

		btn_register = (Button) findViewById(R.id.btn_register);
		btn_register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				register();
			}
		});
		checkUser();
	}

	private void checkUser() {
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("username", "smile");
		query.findObjects(this, new FindListener<User>() {
			@Override
			public void onError(int arg0, String arg1) {
			}

			@Override
			public void onSuccess(List<User> arg0) {
				if (arg0 != null && arg0.size() > 0) {
					User user = arg0.get(0);
					user.setPassword("1234567");
					user.update(RegisterActivity.this, new UpdateListener() {
						@Override
						public void onSuccess() {
							userManager.login("smile", "1234567",
									new SaveListener() {
										@Override
										public void onSuccess() {
											Log.i("smile", "��½�ɹ�");
										}

										@Override
										public void onFailure(int code,
												String msg) {
											Log.i("smile", "��½ʧ�ܣ�" + code
													+ ".msg = " + msg);
										}
									});
						}

						@Override
						public void onFailure(int code, String msg) {
						}
					});
				}
			}
		});
	}

	/**
	 * ע��
	 */
	private void register() {
		String name = et_username.getText().toString();
		String password = et_password.getText().toString();
		String pwd_again = et_password_confirm.getText().toString();
		String nick = et_nick.getText().toString();

		if (!haveAvator) {
			ShowToast("��ѡ��ͷ��");
			return;
		}

		if (TextUtils.isEmpty(name)) {
			ShowToast(R.string.toast_error_username_null);
			return;
		}

		if (TextUtils.isEmpty(password)) {
			ShowToast(R.string.toast_error_password_null);
			return;
		}
		if (!pwd_again.equals(password)) {
			ShowToast(R.string.toast_error_comfirm_password);
			return;
		}

		boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
		if (!isNetConnected) {
			ShowToast(R.string.network_tips);
			return;
		}

		final ProgressDialog progress = new ProgressDialog(
				RegisterActivity.this);
		progress.setMessage("����ע��...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		final User bu = new User();
		bu.setUsername(name);
		bu.setPassword(password);
		bu.setNick(nick);
		// ��user���豸id���а�aa
		bu.setSex(true);
		bu.setDeviceType("android");
		bu.setInstallId(BmobInstallation.getInstallationId(this));
		bu.signUp(RegisterActivity.this, new SaveListener() {
			@Override
			public void onSuccess() {
				// �ϴ�ͷ��
				uploadAvatar();
				progress.dismiss();
				ShowToast("ע��ɹ�");
				// ���豸��username���а�
				userManager.bindInstallationForRegister(bu.getUsername());
				// ���µ���λ����Ϣ
				updateUserLocation();
				// ���㲥֪ͨ��½ҳ���˳�
				sendBroadcast(new Intent(
						BmobConstants.ACTION_REGISTER_SUCCESS_FINISH));
				// ������ҳ
				Intent intent = new Intent(RegisterActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				BmobLog.i(arg1);
				ShowToast("ע��ʧ��:" + arg1);
				progress.dismiss();
			}
		});
	}

	private void showAvatarPop() {
		String[] items = { "����", "�����ѡȡ", };
		Dialog dialog = new AlertDialog.Builder(this).setItems(items,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent;
						switch (which) {
						case 0:
							File dir = new File(BmobConstants.MyAvatarDir);
							if (!dir.exists()) {
								dir.mkdirs();
							}
							// ԭͼ
							File file = new File(dir, new SimpleDateFormat(
									"yyMMddHHmmss").format(new Date()));
							filePath = file.getAbsolutePath();// ��ȡ��Ƭ�ı���·��
							Uri imageUri = Uri.fromFile(file);

							intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
							startActivityForResult(
									intent,
									BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
							break;
						case 1:
							intent = new Intent(Intent.ACTION_PICK, null);
							intent.setDataAndType(
									MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
									"image/*");
							startActivityForResult(
									intent,
									BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
							break;
						}
					}
				}).create();
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
				startImageAction(Uri.fromFile(file), 200, 200,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP);
			}
			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION:// �����޸�ͷ��
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			Uri uri = null;
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
				uri = data.getData();
				startImageAction(uri, 200, 200,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP);
			} else {
				ShowToast("��Ƭ��ȡʧ��");
			}
			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP:// �ü�ͷ�񷵻�
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			if (data == null) {
				return;
			} else {
				saveCropAvator(data);
			}
			// ��ʼ���ļ�·��
			filePath = "";
			haveAvator = true;
			break;
		default:
			break;
		}
	}

	private void startImageAction(Uri uri, int outputX, int outputY,
			int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
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
		startActivityForResult(intent, requestCode);
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

	private void updateUserData(User user, UpdateListener listener) {
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
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

	/**
	 * ������õ�ͷ��
	 */
	private void saveCropAvator(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
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
				PhotoUtil.saveBitmap(BmobConstants.MyAvatarDir, filename,
						bitmap, true);
				// �ϴ�ͷ��
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}

	/**
	 * �ϴ�ͷ��
	 */
	private void uploadAvatar() {
		BmobLog.i("ͷ���ַ��" + path);
		final BmobFile bmobFile = new BmobFile(new File(path));
		bmobFile.upload(this, new UploadFileListener() {
			@Override
			public void onSuccess() {
				String url = bmobFile.getFileUrl(RegisterActivity.this);
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
}
