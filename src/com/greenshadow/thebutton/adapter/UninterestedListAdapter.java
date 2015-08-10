package com.greenshadow.thebutton.adapter;

import java.util.ArrayList;

import com.greenshadow.thebutton.CustomApplication;
import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.view.dialog.DialogTips;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class UninterestedListAdapter extends BaseAdapter {
	private ArrayList<String> mData;
	private LayoutInflater mInflater;
	private Context mContext;

	public UninterestedListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mData = new ArrayList<String>();
		mContext = context;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_uninterested, null);
			viewHolder = new ViewHolder();
			viewHolder.item = (TextView) convertView
					.findViewById(R.id.tv_uninterested_list);
			viewHolder.delete = (ImageButton) convertView
					.findViewById(R.id.ib_delete_uninterested);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (mData == null)
			return convertView;

		viewHolder.item.setText(mData.get(position));
		viewHolder.delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogTips dialog = new DialogTips(mContext, "删除标签", "您确定要删除 "
						+ mData.get(position) + " 标签吗？", "确定", true, true);
				dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						CustomApplication.getInstance().getSpUtil()
								.deleteUninterestedTag(mData.get(position));
						mData.remove(position);
						UninterestedListAdapter.this.notifyDataSetChanged();
						Toast.makeText(mContext, "删除成功！", Toast.LENGTH_LONG)
								.show();
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
		});

		return convertView;
	}

	/**
	 * 设置数据源
	 * 
	 * @param data
	 */
	public void setData(ArrayList<String> data) {
		mData = data;
	}

	private class ViewHolder {
		public TextView item;
		public ImageButton delete;
	}
}
