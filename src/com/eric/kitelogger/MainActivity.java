package com.eric.kitelogger;

import java.io.IOException;

import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private boolean isLogging;
	
	private LoggingStatus loggingStatus;
	private LogSensors logSensors;
	private PowerManager pm;
	private WakeLock wl;
	
	private void setButtonString() {
		String button_str = isLogging? "Stop":"Start";
		Button b = (Button) findViewById(R.id.buttonStateChange);
		b.setText(button_str);
	}
	
	private void setStatusTextString() {
		String status_str;
		switch(loggingStatus) {
		case OFF:
			status_str = "Not running.";
			break;
		case RUNNING:
			status_str = "Running.";
			break;
		case STARTING:
			status_str = "Initializing.";
			break;
		case STOPPING:
			status_str = "Stopping.";
			break;
		default:
			status_str = "Something is terribly wrong.";
			break;
		}
		TextView tv = (TextView) findViewById(R.id.statusText);
		tv.setText(status_str);
	}
	
	private void updateUI() {
		setButtonString();
		setStatusTextString();
	}
	
	private void stopEverything() throws IOException, InterruptedException {
		logSensors.stopSensors(this);
		FileUtilities.dest(this);
		updateUI();
		wl.release();
	}
	
	private void startEverything() throws IOException {
		pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "main");
		wl.acquire();
		FileUtilities.init(this);
		logSensors = new LogSensors();
		logSensors.startSensors(this);
		updateUI();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		isLogging = false;
		loggingStatus = LoggingStatus.OFF;
		updateUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void changeState(View view) throws IOException, InterruptedException {
		if (isLogging) {
			loggingStatus = LoggingStatus.STOPPING;
			stopEverything();
			loggingStatus = LoggingStatus.OFF;
		}
		else {
			loggingStatus = LoggingStatus.STARTING;
			startEverything();
			loggingStatus = LoggingStatus.RUNNING;
		}
		isLogging = isLogging? false:true;
		updateUI();
	}

}

enum LoggingStatus {
	OFF, STARTING, RUNNING, STOPPING
}