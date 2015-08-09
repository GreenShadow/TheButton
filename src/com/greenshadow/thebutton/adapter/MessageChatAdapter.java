package com.greenshadow.thebutton.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.im.BmobDownloadManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.inteface.DownloadListener;

import com.greenshadow.thebutton.CustomApplication;
import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.adapter.base.BaseListAdapter;
import com.greenshadow.thebutton.adapter.base.ViewHolder;
import com.greenshadow.thebutton.ui.BrowserActivity;
import com.greenshadow.thebutton.ui.ImageBrowserActivity;
import com.greenshadow.thebutton.ui.LocationActivity;
import com.greenshadow.thebutton.ui.SetMyInfoActivity;
import com.greenshadow.thebutton.util.FaceTextUtils;
import com.greenshadow.thebutton.util.ImageLoadOptions;
import com.greenshadow.thebutton.util.TimeUtil;
import com.greenshadow.thebutton.util.UrlUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * ����������
 */
public class MessageChatAdapter extends BaseListAdapter<BmobMsg> {

	// 8��Item������
	// �ı�
	private final int TYPE_RECEIVER_TXT = 0;
	private final int TYPE_SEND_TXT = 1;
	// ͼƬ
	private final int TYPE_SEND_IMAGE = 2;
	private final int TYPE_RECEIVER_IMAGE = 3;
	// λ��
	private final int TYPE_SEND_LOCATION = 4;
	private final int TYPE_RECEIVER_LOCATION = 5;
	// ����
	private final int TYPE_SEND_VOICE = 6;
	private final int TYPE_RECEIVER_VOICE = 7;

	private String currentObjectId = "";

	private DisplayImageOptions options;

	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	public MessageChatAdapter(Context context, List<BmobMsg> msgList) {
		super(context, msgList);
		currentObjectId = BmobUserManager.getInstance(context)
				.getCurrentUserObjectId();

		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher)
				.resetViewBeforeLoading(true).cacheOnDisc(true)
				.cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
	}

	@Override
	public int getItemViewType(int position) {
		BmobMsg msg = list.get(position);
		if (msg.getMsgType() == BmobConfig.TYPE_IMAGE) {
			return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_IMAGE
					: TYPE_RECEIVER_IMAGE;
		} else if (msg.getMsgType() == BmobConfig.TYPE_LOCATION) {
			return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_LOCATION
					: TYPE_RECEIVER_LOCATION;
		} else if (msg.getMsgType() == BmobConfig.TYPE_VOICE) {
			return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_VOICE
					: TYPE_RECEIVER_VOICE;
		} else {
			return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_TXT
					: TYPE_RECEIVER_TXT;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 8;
	}

	@SuppressLint("InflateParams")
	private View createViewByType(BmobMsg message, int position) {
		int type = message.getMsgType();
		if (type == BmobConfig.TYPE_IMAGE) {// ͼƬ����
			return getItemViewType(position) == TYPE_RECEIVER_IMAGE ? mInflater
					.inflate(R.layout.item_chat_received_image, null)
					: mInflater.inflate(R.layout.item_chat_sent_image, null);
		} else if (type == BmobConfig.TYPE_LOCATION) {// λ������
			return getItemViewType(position) == TYPE_RECEIVER_LOCATION ? mInflater
					.inflate(R.layout.item_chat_received_location, null)
					: mInflater.inflate(R.layout.item_chat_sent_location, null);
		} else if (type == BmobConfig.TYPE_VOICE) {// ��������
			return getItemViewType(position) == TYPE_RECEIVER_VOICE ? mInflater
					.inflate(R.layout.item_chat_received_voice, null)
					: mInflater.inflate(R.layout.item_chat_sent_voice, null);
		} else {// ʣ��Ĭ�ϵĶ����ı�
			return getItemViewType(position) == TYPE_RECEIVER_TXT ? mInflater
					.inflate(R.layout.item_chat_received_message, null)
					: mInflater.inflate(R.layout.item_chat_sent_message, null);
		}
	}

	@Override
	public View bindView(final int position, View convertView, ViewGroup parent) {
		final BmobMsg item = list.get(position);
		if (convertView == null) {
			convertView = createViewByType(item, position);
		}
		// �ı�����
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
		final ImageView iv_fail_resend = ViewHolder.get(convertView,
				R.id.iv_fail_resend);// ʧ���ط�
		final TextView tv_send_status = ViewHolder.get(convertView,
				R.id.tv_send_status);// ����״̬
		TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
		TextView tv_message = ViewHolder.get(convertView, R.id.tv_message);
		// ͼƬ
		ImageView iv_picture = ViewHolder.get(convertView, R.id.iv_picture);
		final ProgressBar progress_load = ViewHolder.get(convertView,
				R.id.progress_load);// ������
		// λ��
		TextView tv_location = ViewHolder.get(convertView, R.id.tv_location);
		// ����
		final ImageView iv_voice = ViewHolder.get(convertView, R.id.iv_voice);
		// ��������
		final TextView tv_voice_length = ViewHolder.get(convertView,
				R.id.tv_voice_length);

		// ���ͷ������������
		String avatar = item.getBelongAvatar();
		if (avatar != null && !avatar.equals("")) {// ����ͷ��-Ϊ�˲�ÿ�ζ�����ͷ��
			ImageLoader.getInstance().displayImage(avatar, iv_avatar,
					ImageLoadOptions.getOptions(), animateFirstListener);
		} else {
			iv_avatar.setImageResource(R.drawable.head);
		}

		iv_avatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mContext, SetMyInfoActivity.class);
				if (getItemViewType(position) == TYPE_RECEIVER_TXT
						|| getItemViewType(position) == TYPE_RECEIVER_IMAGE
						|| getItemViewType(position) == TYPE_RECEIVER_LOCATION
						|| getItemViewType(position) == TYPE_RECEIVER_VOICE) {
					intent.putExtra("from", "other");
					intent.putExtra("username", item.getBelongUsername());
				} else {
					intent.putExtra("from", "me");
				}
				mContext.startActivity(intent);
			}
		});

		tv_time.setText(TimeUtil.getChatTime(Long.parseLong(item.getMsgTime())));

		if (getItemViewType(position) == TYPE_SEND_TXT
				|| getItemViewType(position) == TYPE_SEND_LOCATION
				|| getItemViewType(position) == TYPE_SEND_VOICE) {// ֻ���Լ����͵���Ϣ�����ط�����
			// ״̬����
			if (item.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {// ���ͳɹ�
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
					tv_send_status.setVisibility(View.GONE);
					tv_voice_length.setVisibility(View.VISIBLE);
				} else {
					tv_send_status.setVisibility(View.VISIBLE);
					tv_send_status.setText("�ѷ���");
				}
			} else if (item.getStatus() == BmobConfig.STATUS_SEND_FAIL) {// ����������Ӧ���߲�ѯʧ�ܵ�ԭ����ɵķ���ʧ�ܣ�����Ҫ�ط�
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.VISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
				if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
					tv_voice_length.setVisibility(View.GONE);
				}
			} else if (item.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED) {// �Է��ѽ��յ�
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
					tv_send_status.setVisibility(View.GONE);
					tv_voice_length.setVisibility(View.VISIBLE);
				} else {
					tv_send_status.setVisibility(View.VISIBLE);
					tv_send_status.setText("���Ķ�");
				}
			} else if (item.getStatus() == BmobConfig.STATUS_SEND_START) {// ��ʼ�ϴ�
				progress_load.setVisibility(View.VISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
				if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
					tv_voice_length.setVisibility(View.GONE);
				}
			}
		}
		// ����������ʾ����
		final String text = item.getContent();
		switch (item.getMsgType()) {
		case BmobConfig.TYPE_TEXT:
			try {
				final ArrayList<String> urls = UrlUtil.getUrls(text);
				tv_message.setText("");
				for (SpannableString spannableString : FaceTextUtils
						.toSpannableStringArrayIncludeFaceAndUrl(mContext, text)) {
					tv_message.append(spannableString);
				}

				tv_message.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						switch (urls.size()) {
						case 0:
							return;
						case 1:
							openUrl(urls.get(0));
							return;
						default:
							String[] items = new String[urls.size()];
							for (int i = 0; i < urls.size(); i++)
								items[i] = "�� " + urls.get(i);

							new AlertDialog.Builder(mContext)
									.setItems(
											items,
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													openUrl(urls.get(which));
												}
											}).create().show();
						}
					}
				});
				tv_message
						.setOnLongClickListener(new View.OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								final String[] items = new String[urls.size() + 1];
								items[0] = "����";
								for (int i = 1; (i - 1) < urls.size(); i++)
									items[i] = "�� " + urls.get(i - 1);
								new AlertDialog.Builder(mContext)
										.setItems(
												items,
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														switch (which) {
														case 0:
															CustomApplication
																	.getInstance()
																	.getClipboardManager()
																	.setPrimaryClip(
																			ClipData.newPlainText(
																					null,
																					text));
															Toast.makeText(
																	mContext,
																	"�Ѹ���",
																	Toast.LENGTH_LONG)
																	.show();
															break;
														default:
															openUrl(urls
																	.get(which - 1));
														}
													}
												}).create().show();
								return true;
							}
						});
			} catch (Exception e) {
			}
			break;

		case BmobConfig.TYPE_IMAGE:// ͼƬ��
			try {
				if (text != null && !text.equals("")) {// ���ͳɹ�֮��洢��ͼƬ���͵�content�ͽ��յ����ǲ�һ����
					dealWithImage(position, progress_load, iv_fail_resend,
							tv_send_status, iv_picture, item);
				}
				iv_picture.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(mContext,
								ImageBrowserActivity.class);
						ArrayList<String> photos = new ArrayList<String>();
						photos.add(getImageUrl(item));
						intent.putStringArrayListExtra("photos", photos);
						intent.putExtra("position", 0);
						mContext.startActivity(intent);
					}
				});

			} catch (Exception e) {
			}
			break;

		case BmobConfig.TYPE_LOCATION:// λ����Ϣ
			try {
				if (text != null && !text.equals("")) {
					String address = text.split("&")[0];
					final String latitude = text.split("&")[1];// ά��
					final String longtitude = text.split("&")[2];// ����
					tv_location.setText(address);
					tv_location.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							Intent intent = new Intent(mContext,
									LocationActivity.class);
							intent.putExtra("type", "scan");
							intent.putExtra("latitude",
									Double.parseDouble(latitude));// ά��
							intent.putExtra("longtitude",
									Double.parseDouble(longtitude));// ����
							mContext.startActivity(intent);
						}
					});
				}
			} catch (Exception e) {

			}
			break;
		case BmobConfig.TYPE_VOICE:// ������Ϣ
			try {
				if (text != null && !text.equals("")) {
					tv_voice_length.setVisibility(View.VISIBLE);
					String content = item.getContent();
					if (item.getBelongId().equals(currentObjectId)) {// ���͵���Ϣ
						if (item.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED
								|| item.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {// �����ͳɹ����߷������Ķ���ʱ������ʾ��������
							tv_voice_length.setVisibility(View.VISIBLE);
							String length = content.split("&")[2];
							tv_voice_length.setText(length + "\''");
						} else {
							tv_voice_length.setVisibility(View.INVISIBLE);
						}
					} else {// �յ�����Ϣ
						boolean isExists = BmobDownloadManager
								.checkTargetPathExist(currentObjectId, item);
						if (!isExists) {// ��ָ����ʽ��¼���ļ������ڣ�����Ҫ���أ���Ϊ���ļ��Ƚ�С���ʷ��ڴ�����
							String netUrl = content.split("&")[0];
							final String length = content.split("&")[1];
							BmobDownloadManager downloadTask = new BmobDownloadManager(
									mContext, item, new DownloadListener() {
										@Override
										public void onStart() {
											progress_load
													.setVisibility(View.VISIBLE);
											tv_voice_length
													.setVisibility(View.GONE);
											iv_voice.setVisibility(View.INVISIBLE);// ֻ��������ɲ���ʾ���ŵİ�ť
										}

										@Override
										public void onSuccess() {
											progress_load
													.setVisibility(View.GONE);
											tv_voice_length
													.setVisibility(View.VISIBLE);
											tv_voice_length.setText(length
													+ "\''");
											iv_voice.setVisibility(View.VISIBLE);
										}

										@Override
										public void onError(String error) {
											progress_load
													.setVisibility(View.GONE);
											tv_voice_length
													.setVisibility(View.GONE);
											iv_voice.setVisibility(View.INVISIBLE);
										}
									});
							downloadTask.execute(netUrl);
						} else {
							String length = content.split("&")[2];
							tv_voice_length.setText(length + "\''");
						}
					}
				}
				// ���������ļ�
				iv_voice.setOnClickListener(new NewRecordPlayClickListener(
						mContext, item, iv_voice));
			} catch (Exception e) {

			}

			break;
		default:
			break;
		}
		return convertView;
	}

	/**
	 * ����ַ
	 * 
	 * @param url
	 */
	private void openUrl(String url) {
		Intent intent = new Intent(mContext, BrowserActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("from", "chat");
		mContext.startActivity(intent);
	}

	/**
	 * ��ȡͼƬ�ĵ�ַ--
	 */
	private String getImageUrl(BmobMsg item) {
		String showUrl = "";
		String text = item.getContent();
		if (item.getBelongId().equals(currentObjectId)) {//
			if (text.contains("&")) {
				showUrl = text.split("&")[0];
			} else {
				showUrl = text;
			}
		} else {// ������յ�����Ϣ������Ҫ����������
			showUrl = text;
		}
		return showUrl;
	}

	/**
	 * ����ͼƬ
	 */
	private void dealWithImage(int position, final ProgressBar progress_load,
			ImageView iv_fail_resend, TextView tv_send_status,
			ImageView iv_picture, BmobMsg item) {
		String text = item.getContent();
		if (getItemViewType(position) == TYPE_SEND_IMAGE) {// ���͵���Ϣ
			if (item.getStatus() == BmobConfig.STATUS_SEND_START) {
				progress_load.setVisibility(View.VISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
			} else if (item.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.VISIBLE);
				tv_send_status.setText("�ѷ���");
			} else if (item.getStatus() == BmobConfig.STATUS_SEND_FAIL) {
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.VISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
			} else if (item.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED) {
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.VISIBLE);
				tv_send_status.setText("���Ķ�");
			}
			String showUrl = "";
			if (text.contains("&")) {
				showUrl = text.split("&")[0];
			} else {
				showUrl = text;
			}
			// Ϊ�˷���ÿ�ζ���ȡ����ͼƬ��ʾ
			ImageLoader.getInstance().displayImage(showUrl, iv_picture);
		} else {
			ImageLoader.getInstance().displayImage(text, iv_picture, options,
					new ImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							progress_load.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							progress_load.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							progress_load.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
							progress_load.setVisibility(View.INVISIBLE);
						}
					});
		}
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
