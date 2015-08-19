package com.greenshadow.thebutton.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.task.BRequest;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;

import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.adapter.AddFriendAdapter;
import com.greenshadow.thebutton.util.CollectionUtils;
import com.greenshadow.thebutton.view.xlist.XListView;
import com.greenshadow.thebutton.view.xlist.XListView.IXListViewListener;

/**
 * 添加好友
 */
public class AddFriendActivity extends ActivityBase implements OnClickListener,
		IXListViewListener, OnItemClickListener {

	private EditText et_find_name;
	private Button btn_search;

	private ProgressDialog progress;
	private String searchName = "";

	private List<BmobChatUser> users = new ArrayList<BmobChatUser>();
	private XListView mListView;
	private AddFriendAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		initView();
	}

	private void initView() {
		initTopBarForLeft("查找好友");
		et_find_name = (EditText) findViewById(R.id.et_find_name);
		btn_search = (Button) findViewById(R.id.btn_search);
		btn_search.setOnClickListener(this);
		initXListView();
	}

	private void initXListView() {
		mListView = (XListView) findViewById(R.id.list_search);
		// 首先不允许加载更多
		mListView.setPullLoadEnable(false);
		// 不允许下拉
		mListView.setPullRefreshEnable(false);
		// 设置监听器
		mListView.setXListViewListener(this);
		//
		mListView.pullRefreshing();

		adapter = new AddFriendAdapter(this, users);
		mListView.setAdapter(adapter);

		mListView.setOnItemClickListener(this);
	}

	private void initSearchList(final boolean isUpdate) {
		if (!isUpdate) {
			progress = new ProgressDialog(AddFriendActivity.this);
			progress.setMessage("正在搜索...");
			progress.setCanceledOnTouchOutside(true);
			progress.show();
		}
		userManager.queryUserByPage(isUpdate, 0, searchName,
				new FindListener<BmobChatUser>() {
					@Override
					public void onError(int arg0, String arg1) {
						BmobLog.i("查询错误:" + arg1);
						if (users != null) {
							users.clear();
						}
						ShowToast("用户不存在");
						mListView.setPullLoadEnable(false);
						refreshPull();
					}

					@Override
					public void onSuccess(List<BmobChatUser> arg0) {
						if (CollectionUtils.isNotNull(arg0)) {
							if (isUpdate) {
								users.clear();
							}
							adapter.addAll(arg0);
							if (arg0.size() < BRequest.QUERY_LIMIT_COUNT) {
								mListView.setPullLoadEnable(false);
								ShowToast("用户搜索完成!");
							} else {
								mListView.setPullLoadEnable(true);
							}
						} else {
							BmobLog.i("查询成功:无返回值");
							if (users != null) {
								users.clear();
							}
							ShowToast("用户不存在");
						}
						if (!isUpdate) {
							progress.dismiss();
						} else {
							refreshPull();
						}
					}
				});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		BmobChatUser user = (BmobChatUser) adapter.getItem(position - 1);
		Intent intent = new Intent(this, SetMyInfoActivity.class);
		intent.putExtra("from", "add");
		intent.putExtra("username", user.getUsername());
		startAnimActivity(intent);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_search:// 搜索
			users.clear();
			searchName = et_find_name.getText().toString();
			if (searchName != null && !searchName.equals("")) {
				initSearchList(false);
			} else {
				ShowToast("请输入用户名");
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onRefresh() {
	}

	private void refreshPull() {
		if (mListView.getPullRefreshing()) {
			mListView.stopRefresh();
		}
	}
}
