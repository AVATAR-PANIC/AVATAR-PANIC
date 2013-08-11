package sate2012.avatar.android.googlemaps.augmentedreality;

import gupta.ashutosh.avatar.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.mapsforge.android.maps.GeoPoint;

import sate2012.avatar.android.Constants;
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
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;

/**
 * 
 * @author Matt + old AVATAR team Fragment used for the augmented reality aspect
 *         of the project.
 * 
 */
public class CameraView extends Fragment implements OnPreparedListener, Callback, OnTouchListener, ConnectionCallbacks, OnConnectionFailedListener,
		OnClickListener {

	// Use these variables to determine size of side fragment to offset in the
	// PointerView class
	protected int fragWidth;
	private int z = 0;

	MarkerPlus marker;
	MediaPlayer mp;
	boolean imageShowing, videoShowing;

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

	private LocationClient myLocationClient;
	Location myLocation = new Location(LocationManager.NETWORK_PROVIDER);
	// GeoPoint testPoint2 = new GeoPoint(testLocation.getLatitude() - 2,
	// testLocation.getLongitude() - 1);
	// GeoPoint[] pointArray = {testPoint, testPoint2};
	float[] currentValues = new float[3];
	public ArrayList<MarkerPlus> markerArray;// = MarkerMaker.makeMarkers();

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.ar_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		FragmentManager fragMgr;
		fragMgr = getFragmentManager();

		DialogFragment dialog;
		Bundle bundle = new Bundle();

		switch (item.getItemId()) {
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.avatar_camera_view, container, false);
	}

	/**
	 * This is the onStart method. It is called whenever this fragment is
	 * started
	 */
	@Override
	public void onStart() {
		super.onStart();
		new HttpThread(this).execute();
		// Initializes the button
		// backButton = (Button)
		// getActivity().findViewById(R.id.to_main_activity);
		LocationManager mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = new MyLocationListener(); // TODO
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 10, mlocListener);
		myLocation = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		// Create helper class
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
		LayoutParams layoutParamsDrawing = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// add the pointer view
		getActivity().addContentView(pointerView, layoutParamsDrawing);
		pointerView.setPadding(0, pointerView.getPaddingTop(), pointerView.getPaddingRight(), pointerView.getPaddingBottom());

		this.getView().getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				FragmentManager fragMgr = getFragmentManager();
				fragWidth = fragMgr.findFragmentByTag("AUG_MENU").getView().getWidth();
				fragMgr.findFragmentByTag("AVATAR_AUGMENTED_REALITY").getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}

		});
		this.getView().setOnTouchListener(this);

		myLocationClient = new LocationClient(getActivity(), this, this);
		myLocationClient.connect();
		// Set up the sensors
		final SensorManager SENSORMANAGER = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		final Sensor ROTATION = SENSORMANAGER.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
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

				if (mPreviewRunning) {

					// Initialize the values needed to store sensor data
					float[] values = new float[3];
					float[] angleChange = new float[3];
					// Do the calculations to determine orientation
					// currentValues = lowPass(event.values.clone(),
					// currentValues);
					SensorManager.getRotationMatrixFromVector(rot, event.values);

					SensorManager.getOrientation(rot, values);

					float yaw = (float) Math.atan2(rot[6], rot[7]);
					float roll = (float) Math.atan2(rot[2], rot[5]);
					System.out.println("Bearing Values: " + rot[2] +", " + rot[5]);
					float tmpPitch = (float) -(Math.acos(rot[8]) - (Math.PI / 2));
					
					if (Float.isNaN(tmpPitch))
						tmpPitch = pointerView.myPitch;
					if (Math.abs(tmpPitch - pointerView.myPitch) < .01)
						tmpPitch = pointerView.myPitch;

					if (z > 20) {
						TextView temp = (TextView) getActivity().findViewById(gupta.ashutosh.avatar.R.id.pitch_display);
						temp.setText("Pitch: " + pointerView.myPitch);
						temp = (TextView) getActivity().findViewById(gupta.ashutosh.avatar.R.id.yaw_display);
						temp.setText("Yaw: " + yaw);
						temp = (TextView) getActivity().findViewById(gupta.ashutosh.avatar.R.id.roll_display);
						temp.setText("Roll: " + roll);
						temp = (TextView) getActivity().findViewById(gupta.ashutosh.avatar.R.id.azimuth_display);
						z = 0;
					}
					z++;

					pointerView.updatePitch(tmpPitch);
					pointerView.updateBearing(roll);
					System.out.println(roll);
					pointerView.postInvalidate();
				}
			}
		};
		SENSORMANAGER.registerListener(listener, ROTATION, SensorManager.SENSOR_DELAY_FASTEST);
		getActivity().findViewById(R.id.aug_rel_info).setOnClickListener(this);
	}

	// This controls what the view will do when the screen is changed
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
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
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		mPreviewRunning = false;
	}

	@Override
	public void onStop() {
		super.onStop();
		mPreviewRunning = false;
	}

	@Override
	public void onResume() {
		super.onResume();
		// mCamera = Camera.open();
		mPreviewRunning = true;
	}

	public class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location loc) {
			if (loc != null) {
				myLocation.set(loc);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			Toast.makeText(getActivity().getApplicationContext(), "GPS Disabled", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			Toast.makeText(getActivity().getApplicationContext(), "GPS Enabled", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	}

	public float getMyPitch() {
		return pointerView.myPitch;
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
			// if(f >= -Math.PI/2){f -= Math.PI/2;}
			// else{f += (3 * Math.PI / 2);}
			if (f < 0) {
				f = (float) (Math.PI + f);
			} else if (f >= 0) {
				f = (float) -(Math.PI - f);
			}
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
			if (mPreviewRunning) {
				Bitmap pointIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
				// for (java.util.Map.Entry<GeoPoint, DataObject> entry :
				// repo.getCollectionOfDataEntries()){
				// for(GeoPoint testPoint: pointArray){
				if (Float.isNaN(myPitch)) {
					myPitch = 1.5f;
					// System.out.println("I HAVE FIXED THE NAN PROBLEM HERE");
				}
				pointManager.drawPoints(canvas, myBearing, myPitch);
				drawGUI(canvas);
			} else {
				canvas = new Canvas();
			}

		}

		// Returns true if the point is within 3 degrees on longitude and
		// latitude.
		// assumes myLocation to be the user's location

		protected void drawGUI(Canvas canvas) {
			// TODO Auto-generated method stub
			/*
			 * Bitmap connected = BitmapFactory.decodeResource(getResources(),
			 * R.drawable.connectedimagesmall); Bitmap disconneceted =
			 * BitmapFactory.decodeResource(getResources(),
			 * R.drawable.disconnectedimagesmall);
			 */// currently unused
			ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			/*
			 * if (mWifi.isConnected()) { canvas.drawBitmap(connected,
			 * mSurfaceView.getWidth()/7,0, null); } else {
			 * canvas.drawBitmap(disconnected, mSurfaceView.getWidth()/7, 0,
			 * null); //TODO Change values to something reasonable }
			 */// currently unused
			if (mPreviewRunning && myLocation != null) {
				Paint white = new Paint();
				white.setColor(Color.WHITE);
				Paint red = new Paint();
				red.setColor(Color.RED);
				canvas.drawText("Latitude: " + myLocation.getLatitude(), 1, 10, white); 
				canvas.drawText("Longitude: " + myLocation.getLongitude(), 1, 20, white);
				double myAlt = myLocation.getAltitude();
				if(!myLocation.hasAltitude()){
					myAlt = Constants.defaultElevation;
				}
				canvas.drawText("Altitude: " + myAlt, 1, 30, white);
				
				if (mWifi.isConnected()) {
					canvas.drawText("Internet Status: Connected", 1, 40, white);
				} else {
					canvas.drawText("Internet Status:", 1, 40, white);
					canvas.drawText("Disconnected", 1, 50, red);
				}
			}

		}

	}

	public void drawGeoPoint(Canvas canvas, GeoPoint overLayPoint) {
		canvas.drawBitmap(null, overLayPoint.getLongitudeE6(), overLayPoint.getLongitudeE6(), null);
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

	public void setMarkerArray(ArrayList<MarkerPlus> array) {
		this.markerArray = array;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			System.out.println((int) event.getX() + fragWidth);
			System.out.println((int) event.getY());
			marker = pointManager.drawInfo((int) event.getX() + fragWidth, (int) event.getY());
			System.out.println(marker);
		}
		return true;
	}

	public void showInfoWindow() {

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d("YAY", "IT WORKED!");
		myLocation = myLocationClient.getLastLocation();
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	public void playSound(String url) {
		try {
			if (mp != null) {
				mp.release();
				mp.stop();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		mp = new MediaPlayer();
		try {
			mp.setDataSource(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mp.prepareAsync();
		mp.setOnPreparedListener(this);

	}

	/**
	 * Listener to tell when the MediaPlayer has prepared the sound
	 * 
	 * @param mp
	 *            : The media player that prepared the sound.
	 */
	public void onPrepared(MediaPlayer mp) {
		mp.start();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d("WHOOPS", result.toString());
	}

	@Override
	public void onClick(View v) {
		System.out.println("CLICKED!");
		LinearLayout textView = (LinearLayout) getActivity().findViewById(R.id.aug_rel_info_text);
		try {
			if (marker.getData().contains(".f4v")) {
				VideoView videoView = (VideoView) getActivity().findViewById(R.id.aug_rel_info_video);

				if (!videoShowing) {
					System.out.println("BLAH");
					String path = marker.getData().substring(marker.getData().lastIndexOf(" ") + 1);
					videoView.setVisibility(View.VISIBLE);
					textView.setVisibility(View.INVISIBLE);
					if (path != null)
						videoView.setVideoURI(Uri.parse(path));

					MediaController mediaController = new MediaController(getActivity());
					mediaController.setAnchorView(videoView);
					videoView.setMediaController(mediaController);
					if (videoView.canSeekForward()) {
						videoView.seekTo(videoView.getDuration() / 2);
					}

					videoView.start();
					videoShowing = true;
				} else {
					videoView.setVisibility(View.INVISIBLE);
					textView.setVisibility(View.VISIBLE);
					videoShowing = false;
					videoView.stopPlayback();
				}
			}
			if (marker.getData().contains(".png") || marker.getData().contains(".jpg") || marker.getData().contains(".gif")) {
				ImageView imageView = (ImageView) getActivity().findViewById(R.id.aug_rel_info_image);
				if (imageShowing) {
					imageView.setVisibility(View.INVISIBLE);
					textView.setVisibility(View.VISIBLE);
					imageShowing = false;
				} else {
					System.out.println("TESTING");
					String path = marker.getData().substring(marker.getData().lastIndexOf(" ") + 1);
					new ImageGrabber(imageView).execute(path);
					System.out.println(path);
					imageView.setVisibility(View.VISIBLE);
					textView.setVisibility(View.INVISIBLE);
					imageShowing = true;
			        
				}
			}
			if (marker.getData().contains(".mp4")) {
				playSound(marker.getData().substring(marker.getData().lastIndexOf(" ") + 1));
				// new SoundPlayer(this,
				// marker.getSnippet().substring(marker.getSnippet().lastIndexOf(" ")
				// + 1)).execute();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	private class ImageGrabber extends AsyncTask<String, Void,  Bitmap>{

		private ImageView imageSlot;
		private String url;
		
		/**
		 * Constructor, make sure to put the ImageView where we want the image to display.
		 * @param imageSlot : The ImageView where we want the image to appear.
		 * @param map : The map that we want to have the image to appear on (Should be a reference to above class).
		 */
		public ImageGrabber(ImageView imageSlot){
			this.imageSlot = imageSlot;
		}
		
		/**
		 * Asynchronous task that gets the Image from a URL
		 * @return : The Bitmap the method got from the URL
		 */
		@Override
		protected Bitmap doInBackground(String...params) {
			// TODO Auto-generated method stub
			try {
				url = params[0];
				
				//System.out.println("Getting URL!");
				
				//Get the connection, set it to a bitmap
				HttpURLConnection connection = (HttpURLConnection) new URL(params[0]).openConnection();
			    connection.connect();
			    connection.setConnectTimeout(5000);
			    connection.setReadTimeout(5000);
			    InputStream input = connection.getInputStream();
			    Bitmap x = BitmapFactory.decodeStream(input);
			    Log.d("LOADING", "LOADING");
			    //Max Image Height and Width
			    int MAXWIDTH = 150;//= 270;
			    int MAXHEIGHT = 100;//=150;
			    
			    if(x != null){
				    int imageWidth = x.getWidth();
				    int imageHeight = x.getHeight();
				    
				    //Find out if image dimensions are too large, then sizes it appropriately.
				    if(imageWidth > MAXWIDTH || imageHeight > MAXHEIGHT){
				    	
				    	//Ternary, determines which is larger: image or height?
				    	double ratio = (imageWidth > imageHeight)? ((float) MAXWIDTH)/imageWidth: ((float) MAXHEIGHT)/imageHeight;
				    	
				    	imageWidth =(int) (imageWidth*ratio);
				    	imageHeight =(int) (imageHeight*ratio);
				    	
				    	x = Bitmap.createScaledBitmap(x, imageWidth, imageHeight, false); //Create scaled Bitmap
				    }
				    
				    //close connections, return the x value. Goes to OnPostExecute() method.
				    input.close();
				    System.out.println("DONE");
				    connection.disconnect();
					return x;
				    }
			    input.close();
			    connection.disconnect();
			    return null;
			} catch (Exception e){
				e.printStackTrace();
				return null;
			}
		}
		
		/**
		 * Runs after the thread, sets the image and calls the re-draw method if image is correct.
		 */
		@Override
		protected void onPostExecute(Bitmap results){
			imageSlot.setImageBitmap(results);
		}
			
	}
}
