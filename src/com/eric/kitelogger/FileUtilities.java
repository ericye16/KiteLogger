package com.eric.kitelogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

public class FileUtilities {
	
	private static File root;
	private static File ourDir;
	private static String rootString;
	private static boolean isReady = false;
		
	private static FileWriter enviroDataStream;
	private static FileWriter accelDataStream;
	private static FileWriter lightDataStream;
	private static FileWriter magneticDataStream;
	private static FileWriter orientationDataStream;
	private static FileWriter temperatureDataStream;
	
	//private static File cameraOutFile;
	private static FileOutputStream cameraOutStream;
	
	public static String getRootFileName() {
		GregorianCalendar cal = new GregorianCalendar();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int date = cal.get(Calendar.DATE);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minutes = cal.get(Calendar.MINUTE);
		int seconds = cal.get(Calendar.SECOND);
		return String.format("KiteLogger%04d%02d%02d%02d%02d%02d", year, month, date, hour, minutes, seconds);
	}
	
	private static void makeDirectory() throws IOException {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Log.e("files", "Media not mounted");
			throw new IOException("Media not mounted.");
		}
		root = Environment.getExternalStorageDirectory();
		ourDir = new File(root.getAbsolutePath(), rootString);
		if (!ourDir.mkdirs()) {
			throw new IOException("Directory not made.");
		}
	}
	
	public static void init(Activity activity) throws IOException {
		rootString = getRootFileName();
		makeDirectory();
		File enviroDataFile = new File(ourDir, rootString + "Sensors.txt");
		enviroDataStream = new FileWriter(enviroDataFile);
		File accelDataFile = new File(ourDir, rootString + "Accelerometer.txt");
		accelDataStream = new FileWriter(accelDataFile);
		File lightDataFile = new File(ourDir, rootString + "Light.txt");
		lightDataStream = new FileWriter(lightDataFile);
		File magneticDataFile = new File(ourDir, rootString + "MagneticField.txt");
		magneticDataStream = new FileWriter(magneticDataFile);
		File orientationDataFile = new File(ourDir, rootString + "Orientation.txt");
		orientationDataStream = new FileWriter(orientationDataFile);
		File temperatureDataFile = new File(ourDir, rootString + "Temperature.txt");
		temperatureDataStream = new FileWriter(temperatureDataFile);
		File cameraOutFile = new File(ourDir, rootString + "Video.mp4");
		cameraOutStream = new FileOutputStream(cameraOutFile);
		Log.d("fileutils", "Init'd");
	}
	
	public static void dest(Activity activity) throws IOException {
		enviroDataStream.close();
		accelDataStream.close();
		lightDataStream.close();
		magneticDataStream.close();
		orientationDataStream.close();
		temperatureDataStream.close();
		Log.d("fileutils", "dest'd");
	}
	
	public static FileWriter getEnviroDataStream() {
		return enviroDataStream;
	}
	
	public static FileWriter getAccelDataStream() {
		return accelDataStream;
	}
	
	public static FileWriter getLightDataStream() {
		return lightDataStream;
	}
	
	public static FileWriter getMagneticDataStream() {
		return magneticDataStream;
	}
	
	public static FileWriter getOrientationDataStream() {
		return orientationDataStream;
	}
	
	public static FileWriter getTemperatureDataStream() {
		return temperatureDataStream;
	}
	
	public static FileOutputStream getCameraOutStream() {
		return cameraOutStream;
	}

}
