package com.eric.kitelogger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class LogSensors implements SensorEventListener {
	
	private SensorManager sensorManager;
	private List<Sensor> deviceSensors;
	private Sensor accelSensor;
	private Sensor lightSensor;
	private Sensor magneticSensor;
	private Sensor orientationSensor;
	private Sensor temperatureSensor;
	
	private void recordDiagnosticInfo() throws IOException {
		Sensor s;
		FileWriter writer = FileUtilities.getEnviroDataStream();
		for (int i = 0; i < deviceSensors.size(); i++) {
			boolean use = false;
			s = deviceSensors.get(i);
			String info;
			if (s.getType() == Sensor.TYPE_ACCELEROMETER) {
				use = true;
				accelSensor = s;
			}
			if (s.getType() == Sensor.TYPE_LIGHT) {
				use = true;
				lightSensor = s;
			}
			if (s.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				use = true;
				magneticSensor = s;
			}
			if (s.getType() == Sensor.TYPE_ORIENTATION) {
				use = true;
				orientationSensor = s;
			}
			if (s.getType() == Sensor.TYPE_TEMPERATURE) {
				use = true;
				temperatureSensor = s;
			}
			if (writer != null && use) {
				info = s.getName() + " " + s.getVendor() + " " + s.getVersion() + " " + s.getResolution() + "\n";
				Log.d("sensors", info);
				writer.write(info);	
			}
			//while we're at it, let's register these for listening
			if (use) {
				sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_FASTEST);
			}
		}
	}
	
	public void startSensors(Activity activity) throws IOException {
		sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
		deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
		recordDiagnosticInfo();
	}
	
	public void stopSensors(Activity activity) {
		sensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		
		
		
	}
}
