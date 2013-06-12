package sate2012.avatar.android.augmentedrealityview;

import gupta.ashutosh.avatar.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.mapsforge.android.maps.GeoPoint;

import sate2012.avatar.android.DataObject;
import sate2012.avatar.android.GeoDataRepository;
import sate2012.avatar.android.LocationDataReceiverAVATAR;
import sate2012.avatar.android.MapsForgeMapViewer;
import sate2012.avatar.android.PhoneCall;
import sate2012.avatar.android.googlemaps.GoogleMapsViewer;
import sate2012.avatar.android.googlemaps.HttpThread;
import sate2012.avatar.android.googlemaps.MarkerPlus;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class CameraView extends Activity implements Callback {
	
	//Use these variables to determine size of side fragment to offset in the PointerView class
	private int fragWidth;
	

	// Camera dependent variables
	private GeoDataRepository repo;
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private PointerView pointerView;// pointer view variable
	boolean mPreviewRunning;
	private Canvas myCanvas = new Canvas();
	private Button backButton;
	Location myLocation = new Location(LocationManager.NETWORK_PROVIDER);
	GeoPoint testPoint = new GeoPoint(myLocation.getLatitude() - 1,
			myLocation.getLongitude() - 1);
	// GeoPoint testPoint2 = new GeoPoint(testLocation.getLatitude() - 2,
	// testLocation.getLongitude() - 1);
	// GeoPoint[] pointArray = {testPoint, testPoint2};
	float[] currentValues = new float[3];
	private ArrayList<MarkerPlus> markerArray; // = MarkerMaker.makeMarkers();

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
	 * This is the onCreate method. It is called by the OS to create the
	 * application.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_view);

		new HttpThread(this).execute("");
		// Initializes the button
		backButton = (Button) findViewById(R.id.to_main_activity);
		makeGeoDataRepository();


		// Initialize the surface for the camera
		mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
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
		this.addContentView(pointerView, layoutParamsDrawing);
		pointerView.setPadding(300, pointerView.getPaddingTop(),
				pointerView.getPaddingRight(), pointerView.getPaddingBottom());

		// Set up the sensors
		final SensorManager SENSORMANAGER = (SensorManager) getSystemService(SENSOR_SERVICE);
		final Sensor ROTATION = SENSORMANAGER
				.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		SensorEventListener listener = new SensorEventListener() {

			float[] rot = new float[9];

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}

			public void onSensorChanged(SensorEvent event) {
				// Initialize the values needed to store sensor data
				float[] values = new float[3];
				float[] angleChange = new float[3];
				// Do the calculations to determine orientation
				// currentValues = lowPass(event.values.clone(), currentValues);
				SensorManager.getRotationMatrixFromVector(rot, event.values);
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
				pointerView
						.updateBearing((float) -(Math.atan2(rot[3], rot[0])));
				pointerView
						.updatePitch((float) -(Math.atan2(rot[7], rot[8]) - (Math.PI / 2)));

				// Redraw the screen
				pointerView.postInvalidate();
				fragWidth = getFragmentManager().findFragmentById(R.id.frag1)
						.getView().getWidth();
			}
		};
		SENSORMANAGER.registerListener(listener, ROTATION,
				SensorManager.SENSOR_DELAY_FASTEST);

		// Tells the button what to do
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Creates activity intent
				Intent main_activity = new Intent(getApplicationContext(),
						MapsForgeMapViewer.class);
				startActivity(main_activity);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_camera_view, menu);
		return true;
	}

	/**
	 * This gets all of the points from the database and puts them in
	 * entriesColl.
	 */
	private void makeGeoDataRepository() {
		
		try{
		repo = geoPointDataRetriever();
		final Map<GeoPoint, DataObject> entriesColl = repo
				.getCollectionOfDataEntries();
		}catch(Exception ex){
			System.err.println("Possible Error connecting to the DataBase");
		}
	}

	/**
	 * This is what actually goes to the database and grabs the information.
	 * 
	 * @return
	 */
	protected GeoDataRepository geoPointDataRetriever() {
		final String dataString = LocationDataReceiverAVATAR
				.loadDataStringFromURL();
		final GeoDataRepository repo = new GeoDataRepository();
		repo.addEntriesFromURLString(dataString);
		return repo;
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
		pointerView.invalidate();
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
		mCamera.release();
		mCamera = null;
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}

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
		 * This is where the tablet draws all of the points it needs.
		 */
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			Bitmap pointIcon = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher);
			// for (java.util.Map.Entry<GeoPoint, DataObject> entry :
			// repo.getCollectionOfDataEntries()){
			// for(GeoPoint testPoint: pointArray){
			int x = 0;
			
			//this is for testing purposes ONLY
			myLocation.setLatitude(39.759727);
			myLocation.setLongitude(-84.120383);
			
			if(markerArray != null){
				for(MarkerPlus marker: markerArray){
					Log.i("Augmented Reality", "myLongitude is: " + myLocation.getLongitude() + "  myLatitude is: " + myLocation.getLatitude());
		        	if(pointClose(marker)){
		        		drawPoint(marker, canvas);
		        	}
		        	x++;
		        }
			}
			System.out.println(x + " points created.");
	}
		
	  //Returns true if the point is within 3 degrees on longitude and latitude.
	  //assumes myLocation to be the user's location
		
	protected boolean pointClose(MarkerPlus marker){
		boolean returnVal = false;
		if ( ( ( marker.getLatitude() - myLocation.getLatitude() ) > -3 ) && 
				( ( marker.getLatitude() - myLocation.getLatitude() ) < 3 )){
			if ( ( ( marker.getLongitude() - myLocation.getLongitude() ) > -3 ) && 
					( ( marker.getLongitude() - myLocation.getLongitude() ) < 3 )){
				returnVal = true;
			}
			
		}
		return returnVal;
	}
		
	
		//draws a point relative to the tablet's perspective.
		
	public void drawPoint(MarkerPlus marker, Canvas canvas){
		
		Bitmap pointIcon = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		// for (java.util.Map.Entry<GeoPoint, DataObject> entry :
		// repo.getCollectionOfDataEntries()){
		// for(GeoPoint testPoint: pointArray){
		double myAlt = myLocation.getAltitude();
		Location gpLocation = new Location(LocationManager.NETWORK_PROVIDER);
		gpLocation.setLatitude(marker.getLatitude());
		gpLocation.setLongitude(marker.getLongitude());
		gpLocation.setAltitude(marker.getAltitude());

		// All of these angles are in Radians.
		double gpBearing = myLocation.bearingTo(gpLocation) * Math.PI
				/ 180.0;
		double gpAlt = gpLocation.getAltitude();
		Paint paint = new Paint();
		double myPitchA = Math.atan((myAlt - gpAlt)
				/ myLocation.distanceTo(gpLocation));
		double dist = myLocation.distanceTo(gpLocation);
		Log.i("Augmented Reality", "myAltitude = " + myAlt + " gpLocation Altitude = " + gpAlt);
		// System.out.println((Math.tan(gpBearing - myBearing) * 640 /
		// Math.tan(Math.PI / 6.0) + 640) + " "
		// + (Math.tan(myPitchA - myPitch) * 400 / Math.tan(Math.PI / 6.0) +
		// 400));
		// System.out.println(gpBearing + " " + myBearing);
		// if(gpBearing <= myBearing + 26 || gpBearing >= myBearing - 26)
		// if(myPitchA <= 20 + myPitch && myPitchA >= -20 + myPitch)

		// This checks where the point is in relation to the tablets
		// location and only draws it in the correct place.
		if (myLocation.getLatitude() < gpLocation.getLatitude()) {
			if (myBearing >= 0) {

				if ((float) (Math.tan(gpBearing - myBearing)
						* (mSurfaceView.getWidth() / 2)
						/ Math.tan(Math.PI / 6.0) + (mSurfaceView
						.getWidth() / 2)) > fragWidth) {

					canvas.drawBitmap(
							pointIcon,
							(float) (Math.tan(gpBearing - myBearing)
									* (mSurfaceView.getWidth() / 2)
									/ Math.tan(Math.PI / 6.0) + (mSurfaceView
									.getWidth() / 2)),
							(float) (Math.tan(myPitchA - myPitch + Math.PI
									/ 2.0)
									* (mSurfaceView.getHeight() / 2)
									/ Math.tan(Math.PI / 6.0) + (mSurfaceView
									.getHeight() / 2)), null);
					canvas.drawText( dist + "",
							(float) (Math.tan(gpBearing - myBearing)
									* (mSurfaceView.getWidth() / 2)
									/ Math.tan(Math.PI / 6.0) + (mSurfaceView
									.getWidth() / 2)),
							(float) (Math.tan(myPitchA - myPitch + Math.PI/ 2.0)
									* (mSurfaceView.getHeight() / 2)
									/ Math.tan(Math.PI / 6.0) + (mSurfaceView
									.getHeight() / 2)), paint);
				}
			}
		} else if (myLocation.getLatitude() > gpLocation.getLatitude()) {
			if (myBearing <= 0) {

				if ((float) (Math.tan(gpBearing - myBearing)
						* (mSurfaceView.getWidth() / 2)
						/ Math.tan(Math.PI / 6.0) + (mSurfaceView
						.getWidth() / 2)) > fragWidth) {

					canvas.drawBitmap(
							pointIcon,
							(float) (Math.tan(gpBearing - myBearing)
									* (mSurfaceView.getWidth() / 2)
									/ Math.tan(Math.PI / 6.0) + (mSurfaceView
									.getWidth() / 2)),
							(float) (Math.tan(myPitchA - myPitch)
									* (mSurfaceView.getHeight() / 2)
									/ Math.tan(Math.PI / 6.0) + (mSurfaceView
									.getHeight() / 2)), null);
					canvas.drawText( dist + "",
							(float) (Math.tan(gpBearing - myBearing)
									* (mSurfaceView.getWidth() / 2)
									/ Math.tan(Math.PI / 6.0) + (mSurfaceView
									.getWidth() / 2)),
							(float) (Math.tan(myPitchA - myPitch)
									* (mSurfaceView.getHeight() / 2)
									/ Math.tan(Math.PI / 6.0) + (mSurfaceView
									.getHeight() / 2)), paint);
					
				}
			}
		} else {
			if (myLocation.getLongitude() < gpLocation.getLongitude()) {
				if (Math.abs(myBearing) > Math.PI / 2) {
					canvas.drawBitmap(
							pointIcon,
							(float) (Math.tan(gpBearing - myBearing)
									* (mSurfaceView.getWidth() / 2)
									/ Math.tan(Math.PI / 6.0) + (mSurfaceView
									.getWidth() / 2)),
							(float) (Math.tan(myPitchA - myPitch)
									* (mSurfaceView.getHeight() / 2)
									/ Math.tan(Math.PI / 6.0) + (mSurfaceView
									.getHeight() / 2)), null);
				}
			}
		}
	}
	}

	public void drawGeoPoint(Canvas canvas, GeoPoint overLayPoint) {
		canvas.drawBitmap(null, overLayPoint.getLongitudeE6(),
				overLayPoint.getLongitudeE6(), null);
	}

	public void myClickMethod(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.map:
			i = new Intent(getApplicationContext(), GoogleMapsViewer.class);
			startActivity(i);
			break;
		case R.id.augmentedReality:
			i = new Intent(getApplicationContext(), CameraView.class);
			startActivity(i);
			break;
		case R.id.emergencyCall:
			i = new Intent(getApplicationContext(), PhoneCall.class);
			break;
		case R.id.exit:
			System.exit(0);
			break;
		}
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

	
	public Button getBackButton() {
		return backButton;
	}

	public void setBackButton(Button backButton) {
		this.backButton = backButton;
	}
	
	public void setMarkerArray(ArrayList<MarkerPlus> array){
		this.markerArray = array;
	}
}
