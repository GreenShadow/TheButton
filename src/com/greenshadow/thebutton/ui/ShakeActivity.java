package com.greenshadow.thebutton.ui;

import com.ant.liao.GifView;
import com.greenshadow.thebutton.R;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;

/**
 * ҡһҡ
 */
public class ShakeActivity extends ActivityBase implements SensorEventListener {
	private GifView shakeGif;
	private SensorManager sensorManager;
	private Vibrator vibrator;

	private static final float MINUMUM_NUMBER = 13.5f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shake);

		shakeGif = (GifView) findViewById(R.id.shake_gif);
		shakeGif.setGifImage(R.raw.shake);

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (sensorManager != null)
			sensorManager.registerListener(this,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] values = event.values;
		if (Math.abs(values[0]) > MINUMUM_NUMBER
				|| Math.abs(values[1]) > MINUMUM_NUMBER
				|| Math.abs(values[2]) > MINUMUM_NUMBER) {
			sensorManager.unregisterListener(this);
			vibrator.vibrate(200);
			updateUserLocation();
			startAnimActivity(new Intent(ShakeActivity.this,
					NearPeopleOnMapActivity.class));
			ShakeActivity.this.finish();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
}
