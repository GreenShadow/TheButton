package com.greenshadow.thebutton.ui.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import cn.bmob.im.util.BmobJsonUtil;
import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.adapter.NewsAdapter;
import com.greenshadow.thebutton.bean.NewsBean;
import com.greenshadow.thebutton.config.Config;
import com.greenshadow.thebutton.ui.BrowserActivity;
import com.greenshadow.thebutton.ui.FragmentBase;
import com.greenshadow.thebutton.view.xlist.XListView;

public class NewsFragment extends FragmentBase {

	private XListView list_news;
	private NewsAdapter newsAdapter;
	private List<NewsBean> datas;

	private static final int COMPLETE = 1;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == COMPLETE) {
				inflateView();
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_news, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	private void initView() {
		initTopBarForOnlyTitle("十条新闻");
		list_news = (XListView) findViewById(R.id.list_news);
		list_news.setVisibility(View.INVISIBLE);
		if (datas == null || datas.size() == 0)
			getList();
		else
			inflateView();

	}

	private void getList() {
		datas = new ArrayList<NewsBean>();
		new Thread(new Runnable() {
			@Override
			public void run() {
				BufferedReader bufferedReader = null;
				try {
					URL url = new URL(Config.NEWS_URL);
					String json = "";
					bufferedReader = new BufferedReader(new InputStreamReader(
							url.openStream()));
					String line;
					for (int i = 0; i < 10; i++) {
						if ((line = bufferedReader.readLine()) == null)
							break;
						json = line;
						JSONObject jo = new JSONObject(json);
						NewsBean bean = new NewsBean();
						bean.setTitle(BmobJsonUtil.getString(jo, "title"));
						bean.setContent(BmobJsonUtil.getString(jo, "content"));
						bean.setTime(BmobJsonUtil.getLong(jo, "time"));
						bean.setUrl(BmobJsonUtil.getString(jo, "url"));
						datas.add(bean);
					}

					mHandler.sendEmptyMessage(COMPLETE);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void inflateView() {
		newsAdapter = new NewsAdapter(getActivity(), datas);
		// 首先不允许加载更多
		list_news.setPullLoadEnable(false);
		// 允许下拉
		list_news.setPullRefreshEnable(true);
		list_news.pullRefreshing();
		list_news.setAdapter(newsAdapter);
		list_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), BrowserActivity.class);
				intent.putExtra("url", datas.get(position - 1).getUrl());
				startActivity(intent);
			}
		});
		list_news.setXListViewListener(new XListView.IXListViewListener() {
			@Override
			public void onRefresh() {
				getList();
			}
		});

		list_news.setVisibility(View.VISIBLE);
	}

}
