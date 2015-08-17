package com.greenshadow.thebutton.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class TimeUtil {

	public final static String FORMAT_YEAR = "yyyy";
	public final static String FORMAT_MONTH_DAY = "MM��dd��";

	public final static String FORMAT_DATE = "yyyy-MM-dd";
	public final static String FORMAT_TIME = "HH:mm";
	public final static String FORMAT_MONTH_DAY_TIME = "MM��dd��  hh:mm";

	public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
	public final static String FORMAT_DATE1_TIME = "yyyy/MM/dd HH:mm";
	public final static String FORMAT_DATE_TIME_SECOND = "yyyy/MM/dd HH:mm:ss";

	private static SimpleDateFormat sdf = new SimpleDateFormat();
	private static final int YEAR = 365 * 24 * 60 * 60;// ��
	private static final int MONTH = 30 * 24 * 60 * 60;// ��
	private static final int DAY = 24 * 60 * 60;// ��
	private static final int HOUR = 60 * 60;// Сʱ
	private static final int MINUTE = 60;// ����

	/**
	 * ����ʱ�����ȡ������ʱ�䣬��3����ǰ��1��ǰ
	 */
	public static String getDescriptionTimeFromTimestamp(long timestamp) {
		long currentTime = System.currentTimeMillis();
		long timeGap = (currentTime - timestamp) / 1000;// ������ʱ���������
		System.out.println("timeGap: " + timeGap);
		String timeStr = null;
		if (timeGap > YEAR) {
			timeStr = timeGap / YEAR + "��ǰ";
		} else if (timeGap > MONTH) {
			timeStr = timeGap / MONTH + "����ǰ";
		} else if (timeGap > DAY) {// 1������
			timeStr = timeGap / DAY + "��ǰ";
		} else if (timeGap > HOUR) {// 1Сʱ-24Сʱ
			timeStr = timeGap / HOUR + "Сʱǰ";
		} else if (timeGap > MINUTE) {// 1����-59����
			timeStr = timeGap / MINUTE + "����ǰ";
		} else {// 1����-59����
			timeStr = "�ո�";
		}
		return timeStr;
	}

	/**
	 * ��ȡ��ǰ���ڵ�ָ����ʽ���ַ���
	 */
	public static String getCurrentTime(String format) {
		if (format == null || format.trim().equals("")) {
			sdf.applyPattern(FORMAT_DATE_TIME);
		} else {
			sdf.applyPattern(format);
		}
		return sdf.format(new Date());
	}

	public static String getTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
		return format.format(new Date(time));
	}

	public static String getHourAndMin(long time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(new Date(time));
	}

	/**
	 * ��ȡ����ʱ��
	 */
	public static String getChatTime(long timesamp) {
		long clearTime = timesamp * 1000;
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		Date today = new Date(System.currentTimeMillis());
		Date otherDay = new Date(clearTime);
		int temp = Integer.parseInt(sdf.format(today))
				- Integer.parseInt(sdf.format(otherDay));

		switch (temp) {
		case 0:
			result = "���� " + getHourAndMin(clearTime);
			break;
		case 1:
			result = "���� " + getHourAndMin(clearTime);
			break;
		case 2:
			result = "ǰ�� " + getHourAndMin(clearTime);
			break;

		default:
			result = getTime(clearTime);
			break;
		}
		return result;
	}
}