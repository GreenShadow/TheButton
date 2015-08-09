package com.greenshadow.thebutton.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Url处理相关工具类
 */
public class UrlUtil {
	private static String regex = "(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*";

	/**
	 * 获取所有的Url
	 * 
	 * @param text
	 */
	public static ArrayList<String> getUrls(String text) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(text);
		ArrayList<String> urls = new ArrayList<String>();

		while (m.find())
			urls.add(m.group());
		return urls;
	}

	/**
	 * 将传入的字符串以Url为边界分解，并有序地存入ArrayList对象中
	 * 
	 * @param text
	 * @return 分解后的数据
	 */
	public static ArrayList<String> splitWithUrl(String text) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(text);
		ArrayList<String> splitResult = new ArrayList<String>();
		int startIndex = 0;
		while (m.find()) {
			String urlText = m.group();
			String backwardString = text.substring(startIndex,
					text.indexOf(urlText));
			splitResult.add(backwardString);
			splitResult.add(urlText);
			text = text.substring(startIndex + backwardString.length()
					+ urlText.length());
		}
		splitResult.add(text.substring(startIndex));

		return splitResult;
	}
}
