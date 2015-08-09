package com.greenshadow.thebutton.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Url������ع�����
 */
public class UrlUtil {
	private static String regex = "(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*";

	/**
	 * ��ȡ���е�Url
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
	 * ��������ַ�����UrlΪ�߽�ֽ⣬������ش���ArrayList������
	 * 
	 * @param text
	 * @return �ֽ�������
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
