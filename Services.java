package com.cgii.humanblackboxandroid;

import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.text.format.Time;
import android.widget.TextView;

public class Services extends Service implements SensorEventListener{

	/** The refresh rate, in frames per second, of the compass. */
    private static final int REFRESH_RATE_FPS = 45;
	
	/** The duration, in milliseconds, of one frame. */
    private static final long FRAME_TIME_MILLIS = TimeUnit.SECONDS.toMillis(1) / REFRESH_RATE_FPS;
	private double time;
	
	/** Sensors*/
	private double X,Y,Z,mag;
	private double jerk;
	private double jerkx;
	private double jerky;
	private double jerkz;
	private int count;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private double now;
    /** TextView*/
    TextView textView;
    
    /** MediaRecorder*/
    MediaRecorder recorder;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
	@Override
	
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate(){
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		//Register Listener
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.print("onStartCommand called");
		return START_STICKY;
    }
	
	/*
	 * onSensorChanged and onAccuracyChanged are implemented from
	 * SensorEventListener
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		float vector = (float) Math.sqrt(event.values[0]*event.values[0] + event.values[1]*event.values[1] + event.values[2]*event.values[2]);
		count=1;
		//!!!!!!!!
		//Put your math calls or methods here...
		//!!!!!
	// Gives you your change in time from now to the last time 
		Time late =new Time(Time.getCurrentTimezone());	
		long latetime=late.toMillis(true);
		
		if(count==1){ // for the first iteration when we dont have any now data 
			time=latetime;
			jerkx=X-X/time;
			jerky=Y-Y/time;
			jerkz=Z-Z/time;
			jerk=0/time;
			count++;
		}
		else{
			 time=(latetime-now)/1000;
			jerkx=(event.values[0]-X)/time;
			jerky=(event.values[1]-Y)/time;
			jerkz=(event.values[2]-Z)/time;
			jerk=(vector-mag)/time;
			
			
		}
		now=latetime;// then set late to now
		
		double g=9.8;
		if (event.values[0] > 2*g|| event.values[1]>2*g||event.values[2]>2*g|| vector>2*g){
			if(jerk<0||jerk>0){
			beginRecording();
			}
		}
		
		textView = MainActivity.textView;
		textView.setText("X: " + event.values[0] + 
				"\nY: " + event.values[1] + 
				"\nZ: " + event.values[2] +
				"\nVector: " + vector);
		 X=event.values[0];
		 Y=event.values[1];
		 Z=event.values[2];
		 mag=(double)vector;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
	
	/*
	 * Camera stuff
	 */
	private void beginRecording(){
		//Get time
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		String date = today.year + "_" + today.month+1 + "_" + today.monthDay + "_" + 
				today.hour + ":" + today.minute + today.second;
		String pathToSDCard = Environment.getExternalStorageState(); //Returns something like "/mnt/sdcard"
		
//		recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
//	    recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
//
//	    CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
//	    recorder.setProfile(cpHigh);
//	    recorder.setOutputFile(pathToSDCard + "/DCIM/Camera/" + date + ".mp4");
//	    recorder.setMaxDuration(15000); // 15 seconds
	}
	
}
