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

	private UserFriendAdapter userAdapter;// ����
	private User user; // ѡ�е��û�

	private List<User> friends = new ArrayList<User>();
	private InputMethodManager inputMethodManager;

	private String urlMessage = ""; // Ҫ���͵�url
	private String urlTitle = ""; // Ҫ���͵���ַ�ı���

	/**
	 * ����ת����ƴ������
	 */
	private CharacterParser characterParser;
	/**
	 * ����ƴ��������ListView�����������
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
				// ���������
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
		// �������������ֵ�ĸı�����������
		mClearEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// ������������ֵΪ�գ�����Ϊԭ�����б�����Ϊ���������б�
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
	 * ����������е�ֵ���������ݲ�����ListView
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
		// ����a-z��������
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
	 * ��ȡ�����б� queryMyfriends
	 */
	private void queryMyfriends() {
		Map<String, BmobChatUser> users = CustomApplication.getInstance()
				.getContactList();
		// ��װ�µ�User
		filledData(CollectionUtils.map2list(users));
		userAdapter.notifyDataSetChanged();
	}

	/**
	 * ΪListView�������
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
			// ����ת����ƴ��
			String username = sortModel.getUsername();
			// ��û��username
			if (username != null) {
				String pinyin = characterParser.getSelling(sortModel
						.getUsername());
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
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
		// ����a-z��������
		Collections.sort(friends, pinyinComparator);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		user = (User) userAdapter.getItem(position);

		DialogTips dialog = new DialogTips(this, "���͸���" + user.getUsername(),
				urlTitle, "ȷ��", true, true);
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String msg = "�����ڿ��� " + urlTitle + " " + urlMessage;
				boolean isNetConnected = CommonUtils
						.isNetworkAvailable(ChooseFriendActivity.this);
				if (!isNetConnected) {
					ShowToast(R.string.network_tips);
					return;
				}
				// ��װBmobMessage����
				BmobMsg message = BmobMsg.createTextSendMsg(
						ChooseFriendActivity.this, user.getObjectId(), msg);
				message.setExtra("Bmob");
				// Ĭ�Ϸ�����ɣ������ݱ��浽������Ϣ�������Ự����
				manager.sendTextMessage(user, message);
				// ˢ�½���
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
	 * �������û������Activity�Ѵ���ˢ�½���
	 */
	private void refreshMessage(BmobMsg msg) {
		if (ChatActivity.chatActivityInstance != null
				&& ChatActivity.chatActivityInstance.targetUser.getObjectId()
						.equals(user.getObjectId())) {
			// ���½���
			ChatActivity.chatActivityInstance.mAdapter.add(msg);
			ChatActivity.chatActivityInstance.mListView
					.setSelection(ChatActivity.chatActivityInstance.mAdapter
							.getCount() - 1);
		}
	}
}
