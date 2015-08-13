package com.greenshadow.thebutton;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.config.BmobConstant;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnReceiveListener;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;

import com.greenshadow.thebutton.ui.MainActivity;
import com.greenshadow.thebutton.util.CollectionUtils;
import com.greenshadow.thebutton.util.CommonUtils;

/**
 * 推送消息接收器
 */
public class MyMessageReceiver extends BroadcastReceiver {

	// 事件监听
	public static ArrayList<EventListener> ehList = new ArrayList<EventListener>();

	public static int mNewNum = 0;

	private BmobUserManager userManager;
	private BmobChatUser currentUser;

	@Override
	public void onReceive(Context context, Intent intent) {
		String json = intent.getStringExtra("msg");
		BmobLog.i("收到的message = " + json);

		userManager = BmobUserManager.getInstance(context);
		currentUser = userManager.getCurrentUser();
		boolean isNetConnected = CommonUtils.isNetworkAvailable(context);
		if (isNetConnected) {
			parseMessage(context, json);
		} else {
			for (int i = 0; i < ehList.size(); i++)
				((EventListener) ehList.get(i)).onNetChange(isNetConnected);
		}
	}

	/**
	 * 解析Json字符串
	 */
	private void parseMessage(final Context context, String json) {
		JSONObject jo;
		try {
			jo = new JSONObject(json);
			String tag = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TAG);
			if (tag == null)
				return;
			if (tag.equals(BmobConfig.TAG_OFFLINE)) {// 下线通知
				if (currentUser != null) {
					if (ehList.size() > 0) {// 有监听的时候，传递下去
						for (EventListener handler : ehList)
							handler.onOffline();
					} else {
						// 清空数据
						CustomApplication.getInstance().logout();
					}
				}
			} else if (tag.equals("push")) { // 推送通知
				String title = BmobJsonUtil.getString(jo, "title");
				String content = BmobJsonUtil.getString(jo, "content");
				String url = BmobJsonUtil.getString(jo, "url");
				String pushTag = BmobJsonUtil.getString(jo, "pushtag");
				// 如果用户设置不允许推送则不推送
				if (!CustomApplication.getInstance().getSpUtil()
						.isAllowPushNotify())
					return;

				// 如果该tag已被用户标为不感兴趣则不推送
				if (CustomApplication.getInstance().getSpUtil()
						.isUninterested(pushTag))
					return;

				// 发送推送通知
				CustomApplication.getInstance().getNotificationUtil()
						.notifyPush(context, title, content, url, pushTag);
			} else if (tag.equals("update")) {
				CustomApplication.getInstance().getNotificationUtil()
						.notifyUpdate(context);
			} else {
				String fromId = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_KEY_TARGETID);
				final String toId = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_KEY_TOID);
				String msgTime = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_READED_MSGTIME);
				if (fromId != null
						&& !BmobDB.create(context, toId).isBlackUser(fromId)) {// 该消息发送方不为黑名单用户
					if (TextUtils.isEmpty(tag)) {// 不携带tag标签--此可接收陌生人的消息
						BmobChatManager.getInstance(context).createReceiveMsg(
								json, new OnReceiveListener() {
									@Override
									public void onSuccess(BmobMsg msg) {
										if (ehList.size() > 0) {// 有监听的时候，传递下去
											for (int i = 0; i < ehList.size(); i++) {
												((EventListener) ehList.get(i))
														.onMessage(msg);
											}
										} else {
											if (currentUser != null
													&& currentUser
															.getObjectId()
															.equals(toId)) {// 当前登陆用户存在并且也等于接收方id
												mNewNum++;
												showMsgNotify(context, msg);
											}
										}
									}

									@Override
									public void onFailure(int code, String arg1) {
										BmobLog.i("获取接收的消息失败：" + arg1);
									}
								});

					} else {// 带tag标签
						if (tag.equals(BmobConfig.TAG_ADD_CONTACT)) {
							// 保存好友请求道本地，并更新后台的未读字段
							BmobInvitation message = BmobChatManager
									.getInstance(context).saveReceiveInvite(
											json, toId);
							if (currentUser != null) {// 有登陆用户
								if (toId.equals(currentUser.getObjectId())) {
									if (ehList.size() > 0) {// 有监听的时候，传递下去
										for (EventListener handler : ehList)
											handler.onAddUser(message);
									} else {
									}
								}
							}
						} else if (tag.equals(BmobConfig.TAG_ADD_AGREE)) {
							String username = BmobJsonUtil.getString(jo,
									BmobConstant.PUSH_KEY_TARGETUSERNAME);
							// 收到对方的同意请求之后，就得添加对方为好友--已默认添加同意方为好友，并保存到本地好友数据库
							BmobUserManager.getInstance(context)
									.addContactAfterAgree(username,
											new FindListener<BmobChatUser>() {
												@Override
												public void onError(int arg0,
														final String arg1) {
												}

												@Override
												public void onSuccess(
														List<BmobChatUser> arg0) {
													CustomApplication
															.getInstance()
															.setContactList(
																	CollectionUtils
																			.list2map(BmobDB
																					.create(context)
																					.getContactList()));
												}
											});
							// 创建一个临时验证会话--用于在会话界面形成初始会话
							BmobMsg.createAndSaveRecentAfterAgree(context, json);
						} else if (tag.equals(BmobConfig.TAG_READED)) {// 已读回执
							String conversionId = BmobJsonUtil.getString(jo,
									BmobConstant.PUSH_READED_CONVERSIONID);
							if (currentUser != null) {
								// 更改某条消息的状态
								BmobChatManager.getInstance(context)
										.updateMsgStatus(conversionId, msgTime);
								if (toId.equals(currentUser.getObjectId())) {
									if (ehList.size() > 0) {// 有监听的时候，传递下去--便于修改界面
										for (EventListener handler : ehList)
											handler.onReaded(conversionId,
													msgTime);
									}
								}
							}
						}
					}
				} else {// 在黑名单期间所有的消息都应该置为已读，不然等取消黑名单之后又可以查询的到
					BmobChatManager.getInstance(context).updateMsgReaded(true,
							fromId, msgTime);
					BmobLog.i("该消息发送方为黑名单用户");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			BmobLog.i("parseMessage错误：" + e.getMessage());
		}
	}

	/**
	 * 显示与聊天消息的通知
	 */
	private void showMsgNotify(Context context, BmobMsg msg) {
		// 更新通知栏
		int icon = R.drawable.ic_launcher;
		String trueMsg = "";
		if (msg.getMsgType() == BmobConfig.TYPE_TEXT
				&& msg.getContent().contains("\\ue")) {
			trueMsg = "[表情]";
		} else if (msg.getMsgType() == BmobConfig.TYPE_IMAGE) {
			trueMsg = "[图片]";
		} else if (msg.getMsgType() == BmobConfig.TYPE_VOICE) {
			trueMsg = "[语音]";
		} else if (msg.getMsgType() == BmobConfig.TYPE_LOCATION) {
			trueMsg = "[位置]";
		} else {
			trueMsg = msg.getContent();
		}
		CharSequence tickerText = msg.getBelongUsername() + ":" + trueMsg;
		String contentTitle = msg.getBelongUsername() + " (" + mNewNum
				+ "条新消息)";

		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		boolean isAllowVoice = CustomApplication.getInstance().getSpUtil()
				.isAllowVoice();
		boolean isAllowVibrate = CustomApplication.getInstance().getSpUtil()
				.isAllowVibrate();

		BmobNotifyManager.getInstance(context).showNotifyWithExtras(
				isAllowVoice, isAllowVibrate, icon, tickerText.toString(),
				contentTitle, tickerText.toString(), intent);
	}
}
