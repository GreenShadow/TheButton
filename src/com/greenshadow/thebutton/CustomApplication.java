package com.greenshadow.thebutton;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.config.Config;
import com.greenshadow.thebutton.util.CollectionUtils;
import com.greenshadow.thebutton.util.NotificationUtil;
import com.greenshadow.thebutton.util.SharePreferenceUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * 自定义全局Application类
 */
public class CustomApplication extends Application {

	private static CustomApplication mInstance; // 单例模式
	public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;

	public static BmobGeoPoint lastPoint = null;// 上一次定位到的经纬度
	private ClipboardManager clipboardManager;
	private NotificationUtil notificationUtil;
	private SharePreferenceUtil mSpUtil;
	private String longtitude = ""; // 经度
	private String latitude = ""; // 纬度

	private MediaPlayer mMediaPlayer;
	private Map<String, BmobChatUser> contactList = new HashMap<String, BmobChatUser>(); // 好友列表

	@Override
	public void onCreate() {
		super.onCreate();
		// 是否开启debug模式--默认开启状态
		BmobChat.DEBUG_MODE = Config.DEBUG_MODE;
		mInstance = this;
		init();
	}

	private void init() {
		mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
		clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		notificationUtil = new NotificationUtil(this);
		initImageLoader(getApplicationContext());
		// 若用户登陆过，则先从好友数据库中取出好友list存入内存中
		if (BmobUserManager.getInstance(getApplicationContext())
				.getCurrentUser() != null) {
			// 获取本地好友user list到内存,方便以后获取好友list
			contactList = CollectionUtils.list2map(BmobDB.create(
					getApplicationContext()).getContactList());
		}
		initBaidu();
	}

	/**
	 * 初始化百度相关sdk initBaidumap
	 */
	private void initBaidu() {
		// 初始化地图Sdk
		SDKInitializer.initialize(this);
		// 初始化定位sdk
		initBaiduLocClient();
	}

	/**
	 * 初始化百度定位sdk
	 */
	private void initBaiduLocClient() {
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
	}

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			double latitude = location.getLatitude();
			double longtitude = location.getLongitude();
			if (lastPoint != null) {
				if (lastPoint.getLatitude() == location.getLatitude()
						&& lastPoint.getLongitude() == location.getLongitude()) {
					mLocationClient.stop();
					return;
				}
			}
			lastPoint = new BmobGeoPoint(longtitude, latitude);
		}
	}

	/**
	 * 初始化ImageLoader
	 */
	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				"bmobim/Cache");// 获取到缓存的目录地址
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				// 线程池内加载的数量
				.threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);// 全局初始化此配置
	}

	public static CustomApplication getInstance() {
		return mInstance;
	}

	// 单例模式
	public synchronized SharePreferenceUtil getSpUtil() {
		if (mSpUtil == null) {
			String currentId = BmobUserManager.getInstance(
					getApplicationContext()).getCurrentUserObjectId();
			String sharedName = currentId + Config.PREFERENCE_NAME;
			mSpUtil = new SharePreferenceUtil(this, sharedName);
		}
		return mSpUtil;
	}

	// 单例模式
	public synchronized NotificationUtil getNotificationUtil() {
		if (notificationUtil == null) {
			notificationUtil = new NotificationUtil(this);
		}

		return notificationUtil;
	}

	public synchronized MediaPlayer getMediaPlayer() {
		if (mMediaPlayer == null)
			mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
		return mMediaPlayer;
	}

	/**
	 * 获取经度
	 */
	public String getLongtitude() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		longtitude = preferences.getString(Config.PREF_LONGTITUDE, "");
		return longtitude;
	}

	/**
	 * 设置经度
	 */
	public void setLongtitude(String lon) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(Config.PREF_LONGTITUDE, lon).commit()) {
			longtitude = lon;
		}
	}

	/**
	 * 获取纬度
	 */
	public String getLatitude() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		latitude = preferences.getString(Config.PREF_LATITUDE, "");
		return latitude;
	}

	/**
	 * 设置纬度
	 */
	public void setLatitude(String lat) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(Config.PREF_LATITUDE, lat).commit()) {
			latitude = lat;
		}
	}

	/**
	 * 获取内存中好友user list
	 */
	public Map<String, BmobChatUser> getContactList() {
		return contactList;
	}

	/**
	 * 设置好友user list到内存中
	 */
	public void setContactList(Map<String, BmobChatUser> contactList) {
		if (this.contactList != null) {
			this.contactList.clear();
		}
		this.contactList = contactList;
	}

	/**
	 * 退出登录,清空缓存数据
	 */
	public void logout() {
		BmobUserManager.getInstance(getApplicationContext()).logout();
		setContactList(null);
		setLatitude(null);
		setLongtitude(null);
	}

	public ClipboardManager getClipboardManager() {
		return clipboardManager;
	}
}
