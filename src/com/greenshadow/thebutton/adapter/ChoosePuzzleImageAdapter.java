package com.greenshadow.thebutton.adapter;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.adapter.base.BaseListAdapter;
import com.greenshadow.thebutton.adapter.base.ViewHolder;
import com.greenshadow.thebutton.bean.PuzzleImage;

public class ChoosePuzzleImageAdapter extends BaseListAdapter<PuzzleImage> {

	public ChoosePuzzleImageAdapter(Context context, List<PuzzleImage> list) {
		super(context, list);
	}

	@SuppressLint("InflateParams")
	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = mInflater.inflate(R.layout.item_choose_puzzle_image,
					null);
		PuzzleImage item = getList().get(position);
		ImageView iv_choose_puzzle_image_item = ViewHolder.get(convertView,
				R.id.iv_choose_puzzle_image_item);
		iv_choose_puzzle_image_item.setImageResource(item.getResId());

		return convertView;
	}

}
