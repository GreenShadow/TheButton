package com.greenshadow.thebutton.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobMsg;

import com.greenshadow.thebutton.CustomApplication;
import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.adapter.UserFriendAdapter;
import com.greenshadow.thebutton.bean.User;
import com.greenshadow.thebutton.util.CharacterParser;
import com.greenshadow.thebutton.util.CollectionUtils;
import com.greenshadow.thebutton.util.CommonUtils;
import com.greenshadow.thebutton.util.PinyinComparator;
import com.greenshadow.thebutton.view.ClearEditText;
import com.greenshadow.thebutton.view.MyLetterView;
import com.greenshadow.thebutton.view.MyLetterView.OnTouchingLetterChangedListener;
import com.greenshadow.thebutton.view.dialog.DialogTips;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ChooseFriendActivity extends ActivityBase implements
		OnItemClickListener {
	private ClearEditText mClearEditText;
	private TextView dialog;
	private ListView list_friends;
	private MyLetterView right_letter;

	private UserFriendAdapter userAdapter;// 好友
	private User user; // 选中的用户

	private List<User> friends = new ArrayList<User>();
	private InputMethodManager inputMethodManager;

	private String urlMessage = ""; // 要发送的url
	private String urlTitle = ""; // 要发送的网址的标题

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_contacts);
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		urlMessage = getIntent().getStringExtra("url");
		urlTitle = getIntent().getStringExtra("title");
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}

	private void init() {
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		initListView();
		initRightLetterView();
		initEditText();
	}

	private void initListView() {
		list_friends = (ListView) findViewById(R.id.list_friends);

		userAdapter = new UserFriendAdapter(this, friends);
		list_friends.setAdapter(userAdapter);
		list_friends.setOnItemClickListener(this);

		list_friends.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 隐藏软键盘
				if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
					if (getCurrentFocus() != null)
						inputMethodManager.hideSoftInputFromWindow(
								getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
		});
	}

	private void initRightLetterView() {
		right_letter = (MyLetterView) findViewById(R.id.right_letter);
		dialog = (TextView) findViewById(R.id.dialog);
		right_letter.setTextView(dialog);
		right_letter
				.setOnTouchingLetterChangedListener(new LetterListViewListener());
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {
		@Override
		public void onTouchingLetterChanged(String s) {
			int position = userAdapter.getPositionForSection(s.charAt(0));
			if (position != -1) {
				list_friends.setSelection(position);
			}
		}
	}

	private void initEditText() {
		mClearEditText = (ClearEditText) findViewById(R.id.et_msg_search);
		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 */
	private void filterData(String filterStr) {
		List<User> filterDateList = new ArrayList<User>();
		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = friends;
		} else {
			filterDateList.clear();
			for (User sortModel : friends) {
				String name = sortModel.getUsername();
				if (name != null) {
					if (name.indexOf(filterStr.toString()) != -1
							|| characterParser.getSelling(name).startsWith(
									filterStr.toString())) {
						filterDateList.add(sortModel);
					}
				}
			}
		}
		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		userAdapter.updateListView(filterDateList);
	}

	public void refresh() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					queryMyfriends();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取好友列表 queryMyfriends
	 */
	private void queryMyfriends() {
		Map<String, BmobChatUser> users = CustomApplication.getInstance()
				.getContactList();
		// 组装新的User
		filledData(CollectionUtils.map2list(users));
		userAdapter.notifyDataSetChanged();
	}

	/**
	 * 为ListView填充数据
	 */
	@SuppressLint("DefaultLocale")
	private void filledData(List<BmobChatUser> datas) {
		friends.clear();
		int total = datas.size();
		for (int i = 0; i < total; i++) {
			BmobChatUser user = datas.get(i);
			User sortModel = new User();
			sortModel.setAvatar(user.getAvatar());
			sortModel.setNick(user.getNick());
			sortModel.setUsername(user.getUsername());
			sortModel.setObjectId(user.getObjectId());
			sortModel.setContacts(user.getContacts());
			// 汉字转换成拼音
			String username = sortModel.getUsername();
			// 若没有username
			if (username != null) {
				String pinyin = characterParser.getSelling(sortModel
						.getUsername());
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					sortModel.setSortLetters(sortString.toUpperCase());
				} else {
					sortModel.setSortLetters("#");
				}
			} else {
				sortModel.setSortLetters("#");
			}
			friends.add(sortModel);
		}
		// 根据a-z进行排序
		Collections.sort(friends, pinyinComparator);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		user = (User) userAdapter.getItem(position);

		DialogTips dialog = new DialogTips(this, "发送给：" + user.getUsername(),
				urlTitle, "确定", true, true);
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String msg = "我正在看： " + urlTitle + " " + urlMessage;
				boolean isNetConnected = CommonUtils
						.isNetworkAvailable(ChooseFriendActivity.this);
				if (!isNetConnected) {
					ShowToast(R.string.network_tips);
					return;
				}
				// 组装BmobMessage对象
				BmobMsg message = BmobMsg.createTextSendMsg(
						ChooseFriendActivity.this, user.getObjectId(), msg);
				message.setExtra("Bmob");
				// 默认发送完成，将数据保存到本地消息表和最近会话表中
				manager.sendTextMessage(user, message);
				// 刷新界面
				refreshMessage(message);
				ChooseFriendActivity.this.finish();
			}
		});
		dialog.SetOnCancelListener(new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * 如果与该用户聊天的Activity已打开则刷新界面
	 */
	private void refreshMessage(BmobMsg msg) {
		if (ChatActivity.chatActivityInstance != null
				&& ChatActivity.chatActivityInstance.targetUser.getObjectId()
						.equals(user.getObjectId())) {
			// 更新界面
			ChatActivity.chatActivityInstance.mAdapter.add(msg);
			ChatActivity.chatActivityInstance.mListView
					.setSelection(ChatActivity.chatActivityInstance.mAdapter
							.getCount() - 1);
		}
	}
}
