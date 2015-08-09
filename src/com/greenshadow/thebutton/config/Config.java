package com.greenshadow.thebutton.config;

/**
 * 全局设置
 */
public class Config {
	/**
	 * Bmob Application ID
	 */
	public static String applicationId = "5c2fbd213c1e747b7e8ea7585ce45cc4";

	/**
	 * Bmob调试模式
	 */
	public static final boolean DEBUG_MODE = true;

	/**
	 * 不感兴趣广播的Action
	 */
	public static final String UNINTERESTE_ACTION = "com.greenshadow.thebutton.uninterested";

	/**
	 * SharePreference名字
	 */
	public static final String PREFERENCE_NAME = "_sharedinfo";

	/**
	 * 经度
	 */
	public static final String PREF_LONGTITUDE = "longtitude";

	/**
	 * 纬度
	 */
	public static final String PREF_LATITUDE = "latitude";

	/**
	 * 地球半径
	 */
	public static final double EARTH_RADIUS = 6378137;
}
