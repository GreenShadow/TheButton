package com.greenshadow.thebutton.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;

import com.greenshadow.thebutton.bean.FaceText;

/**
 * 表情相关工具类
 */
public class FaceTextUtils {

	public static List<FaceText> faceTexts = new ArrayList<FaceText>();
	static {
		faceTexts.add(new FaceText("\\ue056"));
		faceTexts.add(new FaceText("\\ue057"));
		faceTexts.add(new FaceText("\\ue058"));
		faceTexts.add(new FaceText("\\ue059"));
		faceTexts.add(new FaceText("\\ue105"));
		faceTexts.add(new FaceText("\\ue106"));
		faceTexts.add(new FaceText("\\ue107"));
		faceTexts.add(new FaceText("\\ue108"));
		faceTexts.add(new FaceText("\\ue401"));
		faceTexts.add(new FaceText("\\ue402"));
		faceTexts.add(new FaceText("\\ue403"));
		faceTexts.add(new FaceText("\\ue404"));
		faceTexts.add(new FaceText("\\ue405"));
		faceTexts.add(new FaceText("\\ue406"));
		faceTexts.add(new FaceText("\\ue407"));
		faceTexts.add(new FaceText("\\ue408"));
		faceTexts.add(new FaceText("\\ue409"));
		faceTexts.add(new FaceText("\\ue40a"));
		faceTexts.add(new FaceText("\\ue40b"));
		faceTexts.add(new FaceText("\\ue40d"));
		faceTexts.add(new FaceText("\\ue40e"));
		faceTexts.add(new FaceText("\\ue40f"));
		faceTexts.add(new FaceText("\\ue410"));
		faceTexts.add(new FaceText("\\ue411"));
		faceTexts.add(new FaceText("\\ue412"));
		faceTexts.add(new FaceText("\\ue413"));
		faceTexts.add(new FaceText("\\ue414"));
		faceTexts.add(new FaceText("\\ue415"));
		faceTexts.add(new FaceText("\\ue416"));
		faceTexts.add(new FaceText("\\ue417"));
		faceTexts.add(new FaceText("\\ue418"));
		faceTexts.add(new FaceText("\\ue41f"));
		faceTexts.add(new FaceText("\\ue00e"));
		faceTexts.add(new FaceText("\\ue421"));
	}

	/**
	 * 识别emoji表情及Url，并将其分割后重新组装为ArrayList对象
	 * 
	 * @param context
	 * @param text
	 * @return 分割后的数据
	 */
	public static ArrayList<SpannableString> toSpannableStringArrayIncludeFaceAndUrl(
			final Context context, String text) {
		if (!TextUtils.isEmpty(text)) {
			ArrayList<String> split = UrlUtil.splitWithUrl(text);
			ArrayList<SpannableString> ssArray = new ArrayList<SpannableString>();
			for (int i = 0; i < split.size(); i++) {
				String currentText = split.get(i);
				if (currentText == null || currentText.equals(""))
					continue;

				SpannableString spannableString = new SpannableString(
						currentText);
				if (i % 2 == 0) {
					spannableString = toSpannableString(context, currentText);
				} else {
					spannableString.setSpan(new URLSpan(currentText), 0, split
							.get(i).length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				ssArray.add(spannableString);
			}

			return ssArray;
		} else {
			return new ArrayList<SpannableString>();
		}
	}

	/**
	 * 识别emoji表情
	 * 
	 * @param context
	 * @param text
	 * @return SpannableString对象
	 */
	public static SpannableString toSpannableString(Context context, String text) {
		if (!TextUtils.isEmpty(text)) {
			SpannableString spannableString = new SpannableString(text);
			int start = 0;
			Pattern pattern = Pattern.compile("\\\\ue[a-z0-9]{3}",
					Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				String faceText = matcher.group();
				String key = faceText.substring(1);
				BitmapFactory.Options options = new BitmapFactory.Options();
				Bitmap bitmap = BitmapFactory.decodeResource(
						context.getResources(),
						context.getResources().getIdentifier(key, "drawable",
								context.getPackageName()), options);
				ImageSpan imageSpan = new ImageSpan(context, bitmap);
				int startIndex = text.indexOf(faceText, start);
				int endIndex = startIndex + faceText.length();
				if (startIndex >= 0)
					spannableString.setSpan(imageSpan, startIndex, endIndex,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				start = (endIndex - 1);
			}
			// 在某些情况下，这个while循环退出时会跳出if代码块=.=
			return spannableString;
		}
		return new SpannableString("");
	}
}
