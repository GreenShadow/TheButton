package com.greenshadow.thebutton.ui;

import java.util.List;

import cn.bmob.v3.listener.FindListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfigeration;
import com.baidu.mapapi.map.MyLocationConfigeration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.greenshadow.thebutton.CustomApplication;
import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.bean.User;
import com.greenshadow.thebutton.util.CollectionUtils;
import com.greenshadow.thebutton.util.PixelUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class NearPeopleOnMapActivity extends ActivityBase {

	private MapView mMapView;
	private BaiduMap baiduMap;
	private ImageButton myLocation, refresh;

	// ��λ���
	private LocationClient mLocationClient;
	private double mLatitude;
	private double mLongitude;

	// ���������
	private BitmapDescriptor markerIconMan, markerIconWoman;

	// ��־λ
	private boolean isFirstIn = true; // �Ƿ�Ϊ�״ζ�λ

	private ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_near_people_on_map);

		progress = new ProgressDialog(NearPeopleOnMapActivity.this);
		progress.setMessage("���ڲ�ѯ��������...");
		progress.setCanceledOnTouchOutside(true);
		progress.show();

		initView();
		initLocation();

		// ��ʼ��������ͼ��
		markerIconMan = BitmapDescriptorFactory
				.fromResource(R.drawable.maker_woman);
		markerIconWoman = BitmapDescriptorFactory
				.fromResource(R.drawable.maker_woman);

		baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				final Bundle bundle = marker.getExtraInfo();
				final User userInfo = (User) bundle.getSerializable("info");

				InfoWindow infoWindow;
				Button button = new Button(NearPeopleOnMapActivity.this);
				button.setBackgroundResource(R.drawable.user_tips);
				button.setPadding(PixelUtil.dp2px(15f), 0,
						PixelUtil.dp2px(15f), PixelUtil.dp2px(18));
				button.setText(userInfo.getUsername());
				button.setTextColor(Color.WHITE);

				Point p = baiduMap.getProjection().toScreenLocation(
						marker.getPosition());
				p.y -= PixelUtil.dp2px(47f);
				p.x -= PixelUtil.dp2px(1.5f);

				infoWindow = new InfoWindow(button, //
						baiduMap.getProjection().fromScreenLocation(p),//
						new InfoWindow.OnInfoWindowClickListener() {
							@Override
							public void onInfoWindowClick() {
								if (CustomApplication.getInstance()
										.getContactList()
										.containsValue(userInfo)) {
									Intent intent = new Intent(
											NearPeopleOnMapActivity.this,
											SetMyInfoActivity.class);
									intent.putExtra("from", "other");
									intent.putExtra("username",
											userInfo.getUsername());
									startAnimActivity(intent);
								} else {
									Intent intent = new Intent(
											NearPeopleOnMapActivity.this,
											StrangerInfoActivity.class);
									intent.putExtras(bundle);
									startAnimActivity(intent);
								}
							}
						});
				baiduMap.showInfoWindow(infoWindow);
				return true;
			}
		});
		baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				baiduMap.hideInfoWindow();
			}
		});
	}

	private void initView() {
		// ��ʼ����ͼ�ؼ�
		mMapView = (MapView) findViewById(R.id.mapView);
		// �������Ű�ť
		mMapView.removeViewAt(2);

		mMapView.setVisibility(View.INVISIBLE);

		baiduMap = mMapView.getMap();

		UiSettings settings = baiduMap.getUiSettings();
		// ����˫ָ����
		settings.setOverlookingGesturesEnabled(false);
		// ������ת����
		settings.setRotateGesturesEnabled(false);
		// ����ָ����
		settings.setCompassEnabled(false);

		// �������ű���
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.3f);
		baiduMap.setMapStatus(msu);

		baiduMap.setMaxAndMinZoomLevel(19.0f, 14.5f);

		// ���õ�ͼģʽΪ����ģʽ
		baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

		refresh = (ImageButton) findViewById(R.id.refresh);
		myLocation = (ImageButton) findViewById(R.id.my_location);
		refresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				initNearPeople(true);
			}
		});
		myLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.3f);
				baiduMap.setMapStatus(msu);
				msu = MapStatusUpdateFactory.newLatLng(new LatLng(mLatitude,
						mLongitude));
				baiduMap.animateMapStatus(msu);
			}
		});
	}

	private void initLocation() {
		mLocationClient = new LocationClient(this);
		// ���ö�λ��ɼ���
		mLocationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				mMapView.setVisibility(View.VISIBLE);
				MyLocationData locationData = new MyLocationData.Builder() //
						.accuracy(1000f) // ������ʾ����Ȧ
						.latitude(location.getLatitude()) //
						.longitude(location.getLongitude()) //
						.build();
				BitmapDescriptor myLocationIcon = BitmapDescriptorFactory
						.fromResource(R.drawable.location);

				// ʹ���Զ���ͼ��
				MyLocationConfigeration config = new MyLocationConfigeration(
						LocationMode.NORMAL, false, myLocationIcon);
				baiduMap.setMyLocationConfigeration(config);
				baiduMap.setMyLocationData(locationData);

				mLatitude = location.getLatitude();
				mLongitude = location.getLongitude();

				if (isFirstIn) {
					LatLng point = new LatLng(location.getLatitude(), location
							.getLongitude());
					MapStatusUpdate msu = MapStatusUpdateFactory
							.newLatLng(point);
					baiduMap.animateMapStatus(msu);
					isFirstIn = false;
				}

				CustomApplication.getInstance().setLatitude(mLatitude + "");
				CustomApplication.getInstance().setLongtitude(mLongitude + "");

				initNearPeople(false);
			}
		});

		LocationClientOption option = new LocationClientOption();
		// ������������ ���ع���־�γ������ϵ��gcj02 ���ذٶ�ī��������ϵ ��bd09 ���ذٶȾ�γ������ϵ ��bd09ll
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setScanSpan(0);

		mLocationClient.setLocOption(option);
	}

	private void initNearPeople(final boolean isUpdate) {
		userManager.queryKiloMetersListByPage(isUpdate, 0, "location",
				mLongitude, mLatitude, true, 1, null, null,
				new FindListener<User>() {
					@Override
					public void onSuccess(List<User> userInfos) {
						if (CollectionUtils.isNotNull(userInfos)) {
							if (isUpdate) {
								baiduMap.clear();
							}
							LatLng latLng = null;
							Marker marker = null;
							OverlayOptions oo;

							for (User user : userInfos) {
								latLng = new LatLng(user.getLocation()
										.getLatitude(), user.getLocation()
										.getLongitude());

								if (user.getSex())
									oo = new MarkerOptions().position(latLng)
											.icon(markerIconMan).zIndex(5);
								else
									oo = new MarkerOptions().position(latLng)
											.icon(markerIconWoman).zIndex(5);
								marker = (Marker) baiduMap.addOverlay(oo);
								Bundle info = new Bundle();
								info.putSerializable("info", user);
								marker.setExtraInfo(info);
							}
							ShowToast("���������������!");
						} else
							ShowToast("������ɣ����޸�������!");
						progress.dismiss();
					}

					@Override
					public void onError(int arg0, String arg1) {
						ShowToast("��������!");
						Log.e("123456789", arg0 + " " + arg1);
						if (!isUpdate)
							progress.dismiss();
					}

				});

	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();

		// ����Ϊ���Զ�λ�ҵ�λ��
		baiduMap.setMyLocationEnabled(true);
		// ������λ
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();

		// ����Ϊ���ɶ�λ�ҵ�λ��
		baiduMap.setMyLocationEnabled(false);
		// �رն�λ
		mLocationClient.stop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}
}
