package com.greenshadow.thebutton.ui;

import java.util.ArrayList;

import com.greenshadow.thebutton.CustomApplication;
import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.adapter.UninterestedListAdapter;

import android.os.Bundle;
import android.widget.ListView;

public class ManageUninterestedActivity extends ActivityBase {
	private ListView list;
	private UninterestedListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_my_uninterested);

		initTopBarForLeft("管理我不感兴趣的标签");

		list = (ListView) findViewById(R.id.list_uninterested);
		mAdapter = new UninterestedListAdapter(this);
		mAdapter.setData(getData());
		list.setAdapter(mAdapter);
	}

	private ArrayList<String> getData() {
		return CustomApplication.getInstance().getSpUtil().getAllUninterested();
	}
}
