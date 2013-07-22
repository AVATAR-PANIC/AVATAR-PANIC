package sate2012.avatar.android.augmentedrealityview;

import gupta.ashutosh.avatar.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.mapsforge.android.maps.GeoPoint;

import sate2012.avatar.android.LocationDataReceiverAVATAR;
import sate2012.avatar.android.googlemaps.HttpThread;
import sate2012.avatar.android.googlemaps.MarkerPlus;
import DialogFragments.ActivePointDialogFragment;
import DialogFragments.PointSettingsDialogFragment;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Matt + old AVATAR team
 * Fragment used for the augmented reality aspect of the project.
 *
 */
public class CameraView extends Fragment implements Callback, OnTouchListener{
	
	//Use these variables to determine size of side fragment to offset in the PointerView class
	protected int fragWidth;
	private int z = 0;

	// Camera dependent variables
	private Camera mCamera;
	protected SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private PointerView pointerView;// pointer view variable
	boolean mPreviewRunning;
	private Canvas myCanvas = new Canvas();
	protected AugRelPointManager pointManager;
	protected ArrayList<MarkerPlus> drawPointList = new ArrayList<MarkerPlus>();
	MyLocationListener locationListener = new MyLocationListener();
	
	Location myLocation = new Location(LocationManager.NETWORK_PROVIDER);
	
	GeoPoint testPoint = new GeoPoint(myLocation.getLatitude() - 1,
			myLocation.getLongitude() - 1);
	// GeoPoint testPoint2 = new GeoPoint(testLocation.getLatitude() - 2,
	// testLocation.getLongitude() - 1);
	// GeoPoint[] pointArray = {testPoint, testPoint2};
	float[] currentValues = new float[3];
	public ArrayList<MarkerPlus> markerArray;// = MarkerMaker.makeMarkers();
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.ar_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		
		FragmentManager fragMgr;
		fragMgr = getFragmentManager();
		
		DialogFragment dialog;
		Bundle bundle = new Bundle();
		
		switch(item.getItemId()){
		case R.id.active_point:
			bundle.putSerializable("POINT_MANAGER", pointManager);
			
			dialog = new ActivePointDialogFragment();
			dialog.setArguments(bundle);
			dialog.show(fragMgr, "ACTIVE_POINTS");
			break;
		case R.id.ar_settings:
			bundle.putSerializable("POINT_MANAGER", pointManager);
			
			dialog = new PointSettingsDialogFragment();
			dialog.setArguments(bundle);
			dialog.show(fragMgr, "POINT_SETTINGS");
			break;
		
		}
		
		return true;
	}
	

	/**
	 * This is a lowpass filter. It is used to smooth out the tablets movements.
	 * It takes a 25% of the difference between the two arrays, and then adds
	 * that number to the old array.
	 * 
	 * @param input
	 *            - The new Array.
	 * @param output
	 *            - The old Array.
	 * @return - The edited array. It will be 25% between the old array and the
	 *         new array.
	 * 
	 *         EXAMPLE: float[] Orientation (The orientation of the tablet);
	 *         float[] values (The values from the rotation vector sensor);
	 *         Orientation = lowpass(values, Orientation);
	 */
	public float[] lowPass(float[] input, float[] output) {
		final float ALPHA = .25f;
		if (output == null)
			return input;
		for (int i = 0; i < input.length; i++) {
			output[i] += ALPHA * (input[i] - output[i]);
		}
		return output;
	}
	
	/**
	 * Method called when this fragment is created to return the View object.
	 */
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.avatar_camera_view, container, false);
	}
	

	/**
	 * This is the onStart method. It is called whenever this fragment is started
	 */
	@Override
	public void onStart() {
		super.onStart();
		new HttpThread(this).execute();
		// Initializes the button
//		backButton = (Button) getActivity().findViewById(R.id.to_main_activity);
		LocationManager mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = new MyLocationListener();   //TODO
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1, 10, mlocListener);
		myLocation = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		//Create helper class
		pointManager = new AugRelPointManager(this);
		setHasOptionsMenu(true);

		// Initialize the surface for the camera
		mSurfaceView = (SurfaceView) getActivity().findViewById(R.id.surface_camera);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// makeGeoDataRepository();

		// Initialize the view surface for the points
		pointerView = new PointerView(mSurfaceView.getContext());

		// Define layout parameters for the pointer view
		LayoutParams layoutParamsDrawing = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// add the pointer view
		getActivity().addContentView(pointerView, layoutParamsDrawing);
		pointerView.setPadding(0, pointerView.getPaddingTop(),
				pointerView.getPaddingRight(), pointerView.getPaddingBottom());
		
		this.getView().getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener(){

			@Override
			public void onGlobalLayout() {
				FragmentManager fragMgr = getFragmentManager();
				fragWidth = fragMgr.findFragmentByTag("AUG_MENU").getView().getWidth();
				fragMgr.findFragmentByTag("AVATAR_AUGMENTED_REALITY").getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
			
		});
		this.getView().setOnTouchListener(this);
		getActivity();
		// Set up the sensors
		final SensorManager SENSORMANAGER = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		final Sensor ROTATION = SENSORMANAGER
				.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		final Sensor ACCELEROMETER = SENSORMANAGER.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		final Sensor MAGNETIC = SENSORMANAGER.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		SensorEventListener listener = new SensorEventListener() {

			float[] rot = new float[9];
			float[] mGravity;
			float[] mGeoMagnetic;
			float pitch = 0;

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}

			public void onSensorChanged(SensorEvent event) {
				
				if(mPreviewRunning){
					
					// Initialize the values needed to store sensor data
					float[] values = new float[3];
					float[] angleChange = new float[3];
					// Do the calculations to determine orientation
					// currentValues = lowPass(event.values.clone(), currentValues);
					SensorManager.getRotationMatrixFromVector(rot, event.values);
					
					//Added code
//					SensorManager
//	                .remapCoordinateSystem(rot,
//	                        SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X,
//	                        rot);
					
					/////////////////////////
					SensorManager.getOrientation(rot, values);
	
					/*
					 * System.out.println(" "); System.out.println(Math.atan2(R[7],
					 * R[8])); System.out.println(Math.atan2(R[6], Math.sqrt(R[7] *
					 * R[7] + R[8] * R[8]))); System.out.println(Math.atan2(R[3],
					 * R[0])); System.out.println(" ");
					 */
					// This is an output to check the data.
					// System.out.println(event.values[0] + " " + event.values[1] +
					// " " + event.values[2]);
					// TODO: For whoever works with this app next, Use the above
					// print statements to see exactly how the values of the
					// Rotation vector change and see if you can figure out how to
					// better use them to solve the problem.
	
					// Update the bearing and pitch of the pointer view to keep the
					// points in the right place.
//					pointerView
//							.updateBearing((float) -(Math.atan2(rot[3], rot[0])));
//					pointerView
//							.updatePitch((float) -(Math.atan2(rot[7], rot[8]) - (Math.PI / 2)));
					// Redraw the screen
					//pointerView.postInvalidate();
//					
//					Log.d("Bearing", ""+pointerView.myBearing);
//					Log.d("Pitch", ""+pointerView.myPitch);
//					Log.d("Angle", ""+(Math.asin(event.values[2])*2));
//					
//					Log.d("X", ""+ values[0]);
//					Log.d("Y", ""+ values[1]);
//					Log.d("Z", ""+ values[2]);
//					
//					Log.d("Bearing", ""+Math.acos(-rot[8]/ Math.sqrt((1 - rot[2]*rot[2]))));
//					for(int i = 0; i < 9; i++){
//						Log.d(""+i, ""+rot[i]);
//					}
//					Log.d("ArcSin", ""+Math.asin(rot[2]));
					float yaw = (float) Math.atan2(rot[6], rot[7]);
					float roll = (float) -Math.atan2(rot[2], rot[5]);	
					
//					if(true){//roll > 1 && roll < 3){
//						
//						//Log.d("AUG", "IM AT THE SENSORS");
//						
//						if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
//							mGravity = event.values;
//							//Log.d("AUG", "HERE 1");
//						}
//						if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
//							mGeoMagnetic = event.values;
//							//Log.d("AUG", "HERE 2");
//						}
//						if(mGravity != null && mGeoMagnetic != null){
//							float R[] = new float[9];
//							float I[] = new float[9];
//							//Log.d("AUG", "HERE 3");
//							
//							boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeoMagnetic);
//							if(success){
//								float orientation[] = new float[3];
//								SensorManager.getOrientation(R, orientation);
//								float azimuth = orientation[0];
//								//if(Math.abs(pointerView.myBearing-azimuth) < .05) azimuth = pointerView.myBearing;
//								TextView temp = (TextView) getActivity().findViewById(gupta.ashutosh.avatar.R.id.azimuth_display);
//								temp.setText("Bearing: " + Math.toDegrees(pointerView.myBearing));
//								//Log.d("Bearing", ""+azimuth);
//								pointerView.updateBearing(azimuth);
//							}else{
//								//System.err.println("ERROR");
//							}
//						}
	
						
						float tmpPitch = (float) -Math.acos(rot[8]);
						//System.out.println("PITCH IS:::::::::::" + tmpPitch);
						if(Float.isNaN(tmpPitch))tmpPitch = pointerView.myPitch;
						if(Math.abs(tmpPitch - pointerView.myPitch) < .01)tmpPitch = pointerView.myPitch;
						
						
						if( z > 20){
							TextView temp = (TextView) getActivity().findViewById(gupta.ashutosh.avatar.R.id.pitch_display);
							temp.setText(""+pointerView.myPitch);
							temp = (TextView) getActivity().findViewById(gupta.ashutosh.avatar.R.id.yaw_display);
							temp.setText(""+yaw);
							temp = (TextView) getActivity().findViewById(gupta.ashutosh.avatar.R.id.roll_display);
							temp.setText(""+roll);
							temp = (TextView) getActivity().findViewById(gupta.ashutosh.avatar.R.id.azimuth_display);
							//temp.setText(""+myLocation.getBearing());
							z = 0;
						}
						z++;
						
						pointerView.updatePitch(tmpPitch);
						pointerView.updateBearing(0.0f);
						pointerView.postInvalidate();

					

					Log.d("AUG Bearing", pointerView.myBearing + "");
					Log.d("AUG Pitch", pointerView.myPitch + "");
				}
			}
		};
		SENSORMANAGER.registerListener(listener, ROTATION,
				SensorManager.SENSOR_DELAY_FASTEST);
//		SENSORMANAGER.registerListener(listener, ACCELEROMETER, SensorManager.SENSOR_DELAY_FASTEST);
//		SENSORMANAGER.registerListener(listener, MAGNETIC, SensorManager.SENSOR_DELAY_FASTEST);
	}

	// This controls what the view will do when the screen is changed
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}

		Camera.Parameters p = mCamera.getParameters();
		p.setPreviewSize(width, height);
		p.setPictureFormat(PixelFormat.JPEG);
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.startPreview();
		mPreviewRunning = true;
		pointerView.postInvalidate();
	}

	// Initial creation of the camera when the view is started
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
	}

	// Stops and releases the camera view when the application is killed
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		mPreviewRunning = false;
	}
	
	@Override
	public void onStop(){
		super.onStop();
		mPreviewRunning = false;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		//mCamera = Camera.open();
		mPreviewRunning = true;
	}
	
	public class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location loc) {
			if (loc != null)
			{
			  myLocation.set(loc);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			Toast.makeText(getActivity().getApplicationContext(), "GPS Disabled",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			Toast.makeText(getActivity().getApplicationContext(), "GPS Enabled",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public float getMyPitch(){return pointerView.myPitch;}

	/**
	 * This is the Pointer view. It is what the points are drawn on. It lives in
	 * front of the Camera view, which allows it to e seen.
	 * 
	 * @author Josh Crank, Matthew Weber, William Giffin
	 * 
	 */
	private class PointerView extends View {
		float myBearing;
		float myPitch;

		public PointerView(Context context) {
			super(context);
		}

		/**
		 * This updates the angle the tablet is facing. (Think Compass)
		 * 
		 * @param f
		 *            - The new Bearing of the Tablet.
		 */
		private void updateBearing(float f) {
			myBearing = f;
		}

		/**
		 * This updates the Pitch/Angle of Inclination of the tablet.
		 * 
		 * @param f
		 *            - The new Pitch of the Tablet.
		 */
		private void updatePitch(float f) {
			if (f > Math.PI) {
				f -= 2 * Math.PI;
			}
			if (f < -Math.PI) {
				f += 2 * Math.PI;
			}
			myPitch = f;
		}

		/**
		 * onDraw calls all of the graphics methods.
		 */
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			if(mPreviewRunning){
				Bitmap pointIcon = BitmapFactory.decodeResource(getResources(),
						R.drawable.ic_launcher);
				// for (java.util.Map.Entry<GeoPoint, DataObject> entry :
				// repo.getCollectionOfDataEntries()){
				// for(GeoPoint testPoint: pointArray){
				if(Float.isNaN(myPitch)){
					myPitch = 1.5f;
					//System.out.println("I HAVE FIXED THE NAN PROBLEM HERE");
				}
				pointManager.drawPoints(canvas, myBearing, myPitch);
				drawGUI(canvas);
			}else{
				canvas = new Canvas();
			}
			
	}
		
	
		
	  //Returns true if the point is within 3 degrees on longitude and latitude.
	  //assumes myLocation to be the user's location
		
	protected void drawGUI(Canvas canvas) {
			// TODO Auto-generated method stub
			/*Bitmap connected = BitmapFactory.decodeResource(getResources(),
				R.drawable.connectedimagesmall);
			Bitmap disconneceted = BitmapFactory.decodeResource(getResources(),
					R.drawable.disconnectedimagesmall);*/  //currently unused
			ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			/*if (mWifi.isConnected()) {
				canvas.drawBitmap(connected, mSurfaceView.getWidth()/7,0, null);
			}
			else
			{
				canvas.drawBitmap(disconnected, mSurfaceView.getWidth()/7, 0, null);  //TODO Change values to something reasonable
			}*/   //currently unused
			if(mPreviewRunning && myLocation != null){
				Paint white = new Paint();
				white.setColor(Color.WHITE);
				Paint red = new Paint(); 
				red.setColor(Color.RED);
				System.out.println(myLocation);
				canvas.drawText("Latitude: " + myLocation.getLatitude(), 1, 10, white);  //TODO- Change all of these to be relative to the screen size.
				canvas.drawText("Longitude: " + myLocation.getLongitude(), 1, 20, white); 
				canvas.drawText("Altitude: " + myLocation.getAltitude(), 1, 30, white);
				if (mWifi.isConnected()) {
					canvas.drawText("Internet Status: Connected", 1, 40, white); 
				}
				else
				{
					canvas.drawText("Internet Status:", 1, 40, white);
					canvas.drawText("Disconnected", 1, 50, red);
				}
			}
			
	}

	}

	public void drawGeoPoint(Canvas canvas, GeoPoint overLayPoint) {
		canvas.drawBitmap(null, overLayPoint.getLongitudeE6(),
				overLayPoint.getLongitudeE6(), null);
	}

	public Canvas getMyCanvas() {
		return myCanvas;
	}

	public void setMyCanvas(Canvas myCanvas) {
		this.myCanvas = myCanvas;
	}

	public Camera getmCamera() {
		return mCamera;
	}

	public void setmCamera(Camera mCamera) {
		this.mCamera = mCamera;
	}
	

	public SurfaceView getmSurfaceView() {
		return mSurfaceView;
	}

	public void setmSurfaceView(SurfaceView mSurfaceView) {
		this.mSurfaceView = mSurfaceView;
	}

	public SurfaceHolder getmSurfaceHolder() {
		return mSurfaceHolder;
	}

	public void setmSurfaceHolder(SurfaceHolder mSurfaceHolder) {
		this.mSurfaceHolder = mSurfaceHolder;
	}

	public boolean ismPreviewRunning() {
		return mPreviewRunning;
	}

	public void setmPreviewRunning(boolean mPreviewRunning) {
		this.mPreviewRunning = mPreviewRunning;
	}

	public void setMarkerArray(ArrayList<MarkerPlus> array){
		this.markerArray = array;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			System.out.println((int)event.getX() + fragWidth);
			System.out.println((int)event.getY());
			pointManager.drawInfo((int) event.getX() + fragWidth, (int)event.getY());
		}
		return true;
	}
	public void showInfoWindow(){
		
	}
}
