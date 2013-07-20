package com.eric.kitelogger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class LogSensors implements SensorEventListener, LocationListener {
	
	private SensorManager sensorManager;
	private List<Sensor> deviceSensors;
	private boolean canStillRecord = false;
	private LocationManager locationManager;
	
	/*private HashSet<Thread> asyncWriters;
	private MediaRecorder mediaRecorder;
	private Camera camera;
	private SurfaceHolder surfaceHolder;*/
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
		locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		recordDiagnosticInfo();
		canStillRecord = true;
		/*asyncWriters = new HashSet<Thread>();*/
		
		/*camera = Camera.open();
		SurfaceView surfaceView = (SurfaceView)activity.findViewById(R.id.surfaceView1);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(this);
		camera.setPreviewDisplay(surfaceHolder);
		camera.startPreview();
		camera.unlock();
		if (mediaRecorder == null) mediaRecorder = new MediaRecorder();
		mediaRecorder.setCamera(camera);
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
		mediaRecorder.setVideoSize(352, 288);
		mediaRecorder.setVideoFrameRate(15);
		mediaRecorder.setOutputFile(FileUtilities.getCameraOutFile().getAbsolutePath());
		mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
		mediaRecorder.prepare();
		mediaRecorder.start();*/
		
	}
	
	public void stopSensors(Activity activity) throws InterruptedException {
		/*
		camera.stopPreview();
		mediaRecorder.stop();
		mediaRecorder.reset();
		camera.lock();
		*/
		locationManager.removeUpdates(this);
		sensorManager.unregisterListener(this);
		canStillRecord = false;
		/*for (Thread afw: asyncWriters) {
			afw.join(); //wait for the writer threads to finish up
		}*/
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent se) {
		if (canStillRecord) {
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
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		if (canStillRecord) {
			FileWriter writer = FileUtilities.getGPSDataStream();
			try {
				writer.write(location.getTime() + "," + location.getLatitude() + "," +
						location.getLongitude() + "," + location.getBearing() + "," +
						location.getSpeed() + "," + location.getAltitude() + "," +
						location.getAccuracy() + "," + location.getProvider() + "\n");
			} catch (IOException e) {
				Log.e("GPS-writer", e.getMessage());
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
/*	class AsyncFileWriter implements Runnable {
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
		
	}*/

	/*public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}*/
}
