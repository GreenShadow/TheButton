package com.greenshadow.thebutton.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.greenshadow.thebutton.R;

/**
 * 重写通知窗口
 */
public abstract class DialogBase extends Dialog {
	protected OnClickListener onSuccessListener;
	protected Context mainContext;
	protected OnClickListener onCancelListener;// 提供给取消按钮
	protected OnDismissListener onDismissListener;

	protected View view;
	protected Button positiveButton, negativeButton;
	private boolean isFullScreen = false;

	private boolean hasTitle = true;// 是否有title

	private int width = 0, height = 0, x = 0, y = 0;
	private int iconTitle = 0;
	private String message, title;
	private String namePositiveButton, nameNegativeButton;
	private final int MATCH_PARENT = android.view.ViewGroup.LayoutParams.MATCH_PARENT;

	private boolean isCancel = true;

	public boolean isCancel() {
		return isCancel;
	}

	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}

	/**
	 * 构造方法
	 */
	public DialogBase(Context context) {
		super(context, R.style.alert);
		this.mainContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.v2_dialog_base);
		this.onBuilding();
		// 设置标题和消息
		View title_red_line = (View) findViewById(R.id.title_red_line);
		TextView titleTextView = (TextView) findViewById(R.id.dialog_title);
		// 是否有title
		if (hasTitle) {
			titleTextView.setVisibility(View.VISIBLE);
			title_red_line.setVisibility(View.VISIBLE);
		} else {
			titleTextView.setVisibility(View.GONE);
			title_red_line.setVisibility(View.GONE);
		}
		titleTextView.setText(this.getTitle());
		TextView messageTextView = (TextView) findViewById(R.id.dialog_message);
		messageTextView.setText(this.getMessage());

		if (view != null) {
			FrameLayout custom = (FrameLayout) findViewById(R.id.dialog_custom);
			custom.addView(view, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
			findViewById(R.id.dialog_contentPanel).setVisibility(View.GONE);
		} else {
			findViewById(R.id.dialog_customPanel).setVisibility(View.GONE);
		}

		// 设置按钮事件监听
		positiveButton = (Button) findViewById(R.id.dialog_positivebutton);
		negativeButton = (Button) findViewById(R.id.dialog_negativebutton);
		if (namePositiveButton != null && namePositiveButton.length() > 0) {
			positiveButton.setText(namePositiveButton);
			positiveButton
					.setOnClickListener(GetPositiveButtonOnClickListener());
		} else {
			positiveButton.setVisibility(View.GONE);
			findViewById(R.id.dialog_leftspacer).setVisibility(View.VISIBLE);
			findViewById(R.id.dialog_rightspacer).setVisibility(View.VISIBLE);
		}
		if (nameNegativeButton != null && nameNegativeButton.length() > 0) {
			negativeButton.setText(nameNegativeButton);
			negativeButton
					.setOnClickListener(GetNegativeButtonOnClickListener());
		} else {
			negativeButton.setVisibility(View.GONE);
		}

		// 设置对话框的位置和大小
		LayoutParams params = this.getWindow().getAttributes();
		if (this.getWidth() > 0)
			params.width = this.getWidth();
		if (this.getHeight() > 0)
			params.height = this.getHeight();
		if (this.getX() > 0)
			params.width = this.getX();
		if (this.getY() > 0)
			params.height = this.getY();

		// 如果设置为全屏
		if (isFullScreen) {
			params.width = WindowManager.LayoutParams.MATCH_PARENT;
			params.height = WindowManager.LayoutParams.MATCH_PARENT;
		}

		// 设置点击dialog外部区域可取消
		if (isCancel) {
			setCanceledOnTouchOutside(true);
			setCancelable(true);
		} else {
			setCanceledOnTouchOutside(false);
			setCancelable(false);
		}
		getWindow().setAttributes(params);
		this.setOnDismissListener(GetOnDismissListener());
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	/**
	 * 获取OnDismiss事件监听，释放资源
	 */
	protected OnDismissListener GetOnDismissListener() {
		return new OnDismissListener() {
			public void onDismiss(DialogInterface arg0) {
				DialogBase.this.onDismiss();
				DialogBase.this.setOnDismissListener(null);
				view = null;
				mainContext = null;
				positiveButton = null;
				negativeButton = null;
				if (onDismissListener != null) {
					onDismissListener.onDismiss(null);
				}
			}
		};
	}

	/**
	 * 获取确认按钮单击事件监听
	 */
	protected View.OnClickListener GetPositiveButtonOnClickListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				if (OnClickPositiveButton())
					DialogBase.this.dismiss();
			}
		};
	}

	/**
	 * 获取取消按钮单击事件监听
	 */
	protected View.OnClickListener GetNegativeButtonOnClickListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				OnClickNegativeButton();
				DialogBase.this.dismiss();
			}
		};
	}

	/**
	 * 获取焦点改变事件监听，设置EditText文本默认全选
	 */
	protected OnFocusChangeListener GetOnFocusChangeListener() {
		return new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus && v instanceof EditText) {
					((EditText) v).setSelection(0, ((EditText) v).getText()
							.length());
				}
			}
		};
	}

	/**
	 * 设置成功事件监听，用于提供给调用者的回调函数
	 */
	public void SetOnSuccessListener(OnClickListener listener) {
		onSuccessListener = listener;
	}

	/**
	 * 设置关闭事件监听，用于提供给调用者的回调函数
	 */
	public void SetOnDismissListener(OnDismissListener listener) {
		onDismissListener = listener;
	}

	/**
	 * 提供给取消按钮，用于实现类定制
	 */
	public void SetOnCancelListener(OnClickListener listener) {
		onCancelListener = listener;
	}

	/**
	 * 创建方法，用于子类定制创建过程
	 */
	protected abstract void onBuilding();

	/**
	 * 确认按钮单击方法，用于子类定制
	 */
	protected abstract boolean OnClickPositiveButton();

	/**
	 * 取消按钮单击方法，用于子类定制
	 */
	protected abstract void OnClickNegativeButton();

	/**
	 * 关闭方法，用于子类定制
	 */
	protected abstract void onDismiss();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setIconTitle(int iconTitle) {
		this.iconTitle = iconTitle;
	}

	public int getIconTitle() {
		return iconTitle;
	}

	protected String getMessage() {
		return message;
	}

	protected void setMessage(String message) {
		this.message = message;
	}

	protected View getView() {
		return view;
	}

	protected void setView(View view) {
		this.view = view;
	}

	public boolean getIsFullScreen() {
		return isFullScreen;
	}

	public void setIsFullScreen(boolean isFullScreen) {
		this.isFullScreen = isFullScreen;
	}

	public boolean isHasTitle() {
		return hasTitle;
	}

	public void setHasTitle(boolean hasTitle) {
		this.hasTitle = hasTitle;
	}

	protected int getWidth() {
		return width;
	}

	protected void setWidth(int width) {
		this.width = width;
	}

	protected int getHeight() {
		return height;
	}

	protected void setHeight(int height) {
		this.height = height;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	protected String getNamePositiveButton() {
		return namePositiveButton;
	}

	protected void setNamePositiveButton(String namePositiveButton) {
		this.namePositiveButton = namePositiveButton;
	}

	protected String getNameNegativeButton() {
		return nameNegativeButton;
	}

	protected void setNameNegativeButton(String nameNegativeButton) {
		this.nameNegativeButton = nameNegativeButton;
	}
}