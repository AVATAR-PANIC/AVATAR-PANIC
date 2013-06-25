package com.guardian_angel.uav_tracker;

import gupta.ashutosh.avatar.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CameraRecord extends Activity implements SensorEventListener,
		SurfaceHolder.Callback {
	// camera view activity variables
	protected ImageButton stopRecording;
	protected ImageButton startRecording;
	protected ImageButton crosshair;
	private TextView headingView;
	private TextView headingViewOutline;
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	boolean mPreviewRunning;
	private Timer timer;
	protected static int numOfReadings;

	// data gathering variables
	protected ArrayList<Float> zList = new ArrayList<Float>();
	final Handler h = new Handler();
	private SensorManager mSensorManager;
	private SensorEventListener oSensorListener;
	private SensorEventListener aSensorListener;
	private Sensor sensor;
	private float z;
	private String headingValue = "";
	private String phonePitch = "";
	private ArrayList<String> headingList = new ArrayList<String>();
	private ArrayList<String> pitchList = new ArrayList<String>();
	private XMPPSender xmppSender;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.guardianangel_camera_record);
		
		// Initialize timer for data
		timer = new Timer();

		// Initialize the surface
		mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// Initialize the Buttons and images
		startRecording = (ImageButton) findViewById(R.id.startRecording);
		stopRecording = (ImageButton) findViewById(R.id.stopRecording);
		crosshair = (ImageButton) findViewById(R.id.crosshair);
		crosshair.setAlpha(1);
		headingView = (TextView) findViewById(R.id.heading);
		headingView.setText(headingValue);
		headingViewOutline = (TextView) findViewById(R.id.headingOutline);
		headingViewOutline.setText(headingValue);

		// create sensors to be used for the compass
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		oSensorListener = new SensorEventListener() {
			public void onAccuracyChanged(Sensor arg0, int arg1) {

			}

			public void onSensorChanged(SensorEvent event) {
				Sensor sensor = event.sensor;
				if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
					headingValue = "" + (event.values[0] + 90.0);
					headingView.setText(headingValue);
					headingView.invalidate();
					headingViewOutline.setText(headingValue);
					headingViewOutline.invalidate();

					if (Double.parseDouble(headingValue) > 360) {
						headingValue = ""
								+ (Double.parseDouble(headingValue) - 360);
						headingView.setText(headingValue);
						headingView.invalidate();
						headingViewOutline.setText(headingValue);
						headingViewOutline.invalidate();
					}
				}
			}
		};

		aSensorListener = new SensorEventListener() {
			public void onAccuracyChanged(Sensor arg0, int arg1) {

			}

			public void onSensorChanged(SensorEvent event) {
				Sensor sensor = event.sensor;
				if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
					z = event.values[2];
				}
			}
		};

		mSensorManager.registerListener(oSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(aSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		// program button to start recording data
		startRecording.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// display message
				String message = "Sensors now recording.";
				Toast.makeText(CameraRecord.this, message, Toast.LENGTH_SHORT)
						.show();
				numOfReadings = 0;
				
				// start timer for recording
				timer.schedule(new updateData(), 100, 100);
			}
		});

		/*
		 * This stops the recording of data and moves on to the next activity
		 */
		stopRecording.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// display message
				String message = "Sensors STOPPED recroding.";
				Toast.makeText(CameraRecord.this, message, Toast.LENGTH_SHORT)
						.show();
				
				// initialize activity change intent
				Intent nextScreen = new Intent(getApplicationContext(),
						UserData.class);
				
				// stop timer
				timer.cancel();
				timer.purge();
				
				// clear variables
				phonePitch = null;
				headingValue = null;
				numOfReadings = 0;
				headingToFile();
				pitchToFile();
				
				// start next activity
				nextScreen.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(nextScreen);
				finish();
			}
		});

		final Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
			public void onPictureTaken(byte[] imageData, Camera c) {
				try {
					String filePath = "uav_pic";
					File fileDir = new File(Environment
							.getExternalStorageDirectory().getPath()
							+ "/UAV_T/Images/");
					fileDir.mkdirs();
					// Create a File object for the output file
					File outputFile = new File(fileDir, (filePath + ".jpg"));
					FileOutputStream fos = new FileOutputStream(outputFile);
					fos.write(imageData);
					fos.close();
					String message = "Picture saved.";
					Toast.makeText(CameraRecord.this, message,
							Toast.LENGTH_SHORT).show();
					// restart camera preview
					mCamera.startPreview();
				} catch (Exception ex) {
					ex.printStackTrace();
					String message = "Save failed.";
					Toast.makeText(CameraRecord.this, message,
							Toast.LENGTH_LONG).show();
					// restart camera preview
					mCamera.startPreview();
				}
			}
		};

		// program button to take and save a picture
		crosshair.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String message = "Picture taken.";
				Toast.makeText(CameraRecord.this, message, Toast.LENGTH_SHORT)
						.show();

				mCamera.takePicture(null, null, pictureCallback);
				//mProgress.bringToFront();
			}
		});

	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	// Create camera preview.
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
	}

	// Change preview's properties
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}

		Camera.Parameters p = mCamera.getParameters();
		p.setPreviewSize(w, h);
		p.setPictureFormat(PixelFormat.JPEG);
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.startPreview();
		mPreviewRunning = true;
	}

	// Stop the preview.
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
	}

	@Override
	public void onBackPressed() {
		Intent nextScreen = new Intent(getApplicationContext(),
				MainActivity.class);
		nextScreen.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(nextScreen);
		finish();
	}

	/*
	 * Update the accelerometer values from when they were changed by the
	 * sensors.
	 */
	public void updatePitch() {
		float zTemp = 0;
		zTemp = z;
		phonePitch = "" + zTemp;
	}

	// Unregister the listener used for the compass data
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (sensor != null) {
			mSensorManager.unregisterListener(this);
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub

	}
	
	/*
	 * Updates the heading and angle data when the timer calls
	 * and sends this data to the XMPP server. This is called
	 * every tenth of a second until the user stops recording.
	 * 
	 * Precondition: Requires a heading and an angle value
	 * Postcondition: Stores the values of the heading and the angle
	 */
	class updateData extends TimerTask implements Runnable {

        @Override
        public void run() {
        	numOfReadings++;
        	updatePitch();
			Date date = new Date();
			String dateString = date.toGMTString();
			headingList.add("" + headingValue);
			pitchList.add(phonePitch);
			xmppSender = new XMPPSender(dateString, phonePitch,
					headingValue, numOfReadings);
			xmppSender.createMessage();
			xmppSender.sendMessage();
        }
    }
	
	
	/*
	 * Writes the contents of the heading data to a file on the external storage
	 * 
	 * Precondition: Requires heading values to write to a file
	 * Postcondition: Creates a directory and writes the heading contents to a file
	 */
	public void headingToFile() {
		String filePath = "Heading";
		PrintStream  ps = null;
		// create a directory for the file
		File fileDir = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/UAV_T/Data");
		fileDir.mkdirs();
		// Create a File object for the output file
		File outputFile = new File(fileDir, (filePath + ".txt"));
		try {
			FileOutputStream fos = new FileOutputStream(outputFile);
			ps = new PrintStream(fos);
			for (int i = 0; i < headingList.size(); i++) {
				ps.println(headingList.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ps.close();
	}
	
	/*
	 * Writes the contents of the pitch data to a file on the external storage
	 * 
	 * Precondition: Requires pitch values to write to a file
	 * Postcondition: Creates a directory and writes the pitch contents to a file
	 */
	public void pitchToFile() {
		String filePath = "Pitch";
		PrintStream  ps = null;
		// create a directory for the file
		File fileDir = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/UAV_T/Data");
		fileDir.mkdirs();
		// Create a File object for the output file
		File outputFile = new File(fileDir, (filePath + ".txt"));
		try {
			FileOutputStream fos = new FileOutputStream(outputFile);
			ps = new PrintStream(fos);
			for (int i = 0; i < pitchList.size(); i++) {
				ps.println(pitchList.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ps.close();
	}
}