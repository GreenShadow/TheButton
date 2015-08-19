package com.greenshadow.thebutton.adapter;

import java.util.List;
import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.adapter.base.BaseListAdapter;
import com.greenshadow.thebutton.adapter.base.ViewHolder;
import com.greenshadow.thebutton.bean.NewsBean;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NewsAdapter extends BaseListAdapter<NewsBean> {

	public NewsAdapter(Context context, List<NewsBean> list) {
		super(context, list);
	}

	@SuppressLint("InflateParams")
	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_news, null);
		}
		NewsBean item = getList().get(position);
		TextView tv_news_title = ViewHolder
				.get(convertView, R.id.tv_news_title);
		TextView tv_news_content = ViewHolder.get(convertView,
				R.id.tv_news_content);
		TextView tv_news_time = ViewHolder.get(convertView, R.id.tv_news_time);

		tv_news_title.setText(item.getTitle());
		tv_news_content.setText(item.getContent());
		tv_news_time.setText(item.getTime());

		return convertView;
	}
}
