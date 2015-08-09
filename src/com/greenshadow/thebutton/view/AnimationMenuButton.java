package com.greenshadow.thebutton.view;

import com.greenshadow.thebutton.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AnimationMenuButton extends LinearLayout {

	private TextView text;
	private ImageView image;

	public AnimationMenuButton(Context context) {
		this(context, null);
	}

	public AnimationMenuButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AnimationMenuButton(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		View v = LayoutInflater.from(context).inflate(
				R.layout.animation_menu_button, null);
		text = (TextView) v.findViewById(R.id.menuButtonText);
		image = (ImageView) v.findViewById(R.id.menuButtonImage);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.animation_menu_button);
		text.setText(a.getString(R.styleable.animation_menu_button_text));
		image.setImageResource(a.getResourceId(
				R.styleable.animation_menu_button_image, R.drawable.icon_geo));
		a.recycle();

		addView(v);
	}

	/**
	 * 播放选中的动�?
	 */
	public void selectAnimation() {
	}
}
