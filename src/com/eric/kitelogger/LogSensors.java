package com.eric.kitelogger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
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
	private HashSet<Thread> asyncWriters;
	/*private Sensor accelSensor;
	private Sensor lightSensor;
	private Sensor magneticSensor;
	private Sensor orientationSensor;
	private Sensor temperatureSensor;*/
	
	private void recordDiagnosticInfo() throws IOException {
		Sensor s;
		FileWriter writer = FileUtilities.getEnviroDataStream();
		for (int i = 0; i < deviceSensors.size(); i++) {
			boolean use = false;
			s = deviceSensors.get(i);
			String info;
			if (s.getType() == Sensor.TYPE_ACCELEROMETER) {
				use = true;
				//accelSensor = s;
			}
			if (s.getType() == Sensor.TYPE_LIGHT) {
				use = true;
				//lightSensor = s;
			}
			if (s.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				use = true;
				//magneticSensor = s;
			}
			if (s.getType() == Sensor.TYPE_ORIENTATION) {
				use = true;
				//orientationSensor = s;
			}
			if (s.getType() == Sensor.TYPE_TEMPERATURE) {
				use = true;
				//temperatureSensor = s;
			}
			if (writer != null && use) {
				info = String.format("Sensor Name: %s\n" + 
						"Vendor: %s\n" +
						"Version: %d\n" +
						"Resolution: %f\n" +
						"Power: %f\n" +						
						"\n\n",
						s.getName(), s.getVendor(), s.getVersion(),
						s.getResolution(), s.getPower());
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
		asyncWriters = new HashSet<Thread>();
	}
	
	public void stopSensors(Activity activity) throws InterruptedException {
		sensorManager.unregisterListener(this);
		for (Thread afw: asyncWriters) {
			afw.join(); //wait for the writer threads to finish up
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent se) {
		Thread afw = new Thread(new AsyncFileWriter(se));
		asyncWriters.add(afw);
		afw.run(); // run it asynchronously so we don't block onSensorChanged()
	}
	
	class AsyncFileWriter implements Runnable {
		private SensorEvent se;
		
		@Override
		public void run() {
			FileWriter writer;
			if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				writer = FileUtilities.getAccelDataStream();
				try {
					writer.write(se.timestamp + "," + se.values[0] +
						"," + se.values[1] + "," + se.values[2] + "\n");
				} catch (IOException e) {
					Log.e("asyncwriter-accel", e.getMessage());
					e.printStackTrace();
				}
			}
			if (se.sensor.getType() == Sensor.TYPE_LIGHT) {
				writer = FileUtilities.getLightDataStream();
				try {
					writer.write(se.timestamp + "," + se.values[0] + "\n");
				} catch (IOException e) {
					Log.e("asyncwriter-light", e.getMessage());
					e.printStackTrace();
				}
			}
			if (se.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				writer = FileUtilities.getMagneticDataStream();
				try {
					writer.write(se.timestamp + "," + se.values[0] + "," +
							se.values[1] + "," + se.values[2] + "\n");
				} catch (IOException e) {
					Log.e("asyncwriter-magnets", e.getMessage());
					e.printStackTrace();
				}
			}
			if (se.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				writer = FileUtilities.getOrientationDataStream();
				try {
					writer.write(se.timestamp + "," + se.values[0] + "," +
							se.values[1] + "," + se.values[2] + "\n");
				} catch (IOException e) {
					Log.e("asyncwriter-orientation", e.getMessage());
					e.printStackTrace();
				}
			}
			if (se.sensor.getType() == Sensor.TYPE_TEMPERATURE) {
				writer = FileUtilities.getTemperatureDataStream();
				try {
					writer.write(se.timestamp + "," + se.values[0] + "\n");
				} catch (IOException e) {
					Log.e("asyncwriter-temperature", e.getMessage());
					e.printStackTrace();
				}
			}
			asyncWriters.remove(Thread.currentThread());
		}
		
		public AsyncFileWriter(SensorEvent s) {
			se = s;
		}
		
	}
}
