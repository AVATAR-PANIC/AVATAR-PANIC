package sate2012.avatar.android;

import gupta.ashutosh.avatar.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.MapViewMode;

import sate2012.avatar.android.augmentedrealityview.CameraView;
import sate2012.avatar.android.googlemaps.GoogleMapsViewer;
import sate2012.avatar.android.pointclustering.*;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MapsForgeMapViewer extends MapActivity implements
		LocationListener, OnClickListener {
	private static MapView mapView;
	private GeoPoint myCurrentLocation;
	private Location loc;
	// private double locLat;
	// private double locLon;
	// private static final int TWO_MINUTES = 1000 * 60 * 2;
	public static final int MEDIA_SELECTOR_REQUEST_CODE = 1845235;
	private double pointLocLat;
	private double pointLocLon;
	private Button findPosition;
	private Button exit;
	private Button ClearPointsButton;
	private Button AugmentedRealityViewerButton;

	MVItemizedOverlay itemizedOverlay;
	MVItemizedOverlay userPointOverlay;
	MVItemizedOverlay GeoDataRepositoryOverlay;
	PointSetter pointer;
	Drawable locationMarker;
	Drawable newMarker;
	Drawable newPoint;
	private static SensorManager mySensorManager;
	public static String EXTRA_MESSAGE;
	private boolean sensorrunning;
	private Compass myCompassView;
	// private SensorEventListener mySensorEventListener;
	private Button getPts;
	// private LocationDataReceiverAVATAR plotter;
	private final double FOV = .0025895977;
	private final double AngleOfView = 178.0;
	sate2012.avatar.android.augmentedrealityview.CameraView myCameraView = new sate2012.avatar.android.augmentedrealityview.CameraView();
	sate2012.avatar.android.pointclustering.ClusterMaker geoPointClusterMaker = new sate2012.avatar.android.pointclustering.ClusterMaker();

	public ArrayList<DataObjectItem> dataList = new ArrayList<DataObjectItem>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		this.setContentView(R.layout.map_view);
		myCompassView = (Compass) findViewById(R.id.mycompassview);
		mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		getPts = (Button) findViewById(R.id.Get_Points_Button);
		getPts.setOnClickListener(this);
		List<Sensor> mySensors = mySensorManager
				.getSensorList(Sensor.TYPE_ORIENTATION);

		if (mySensors.size() > 0) {
			mySensorManager.registerListener(mySensorEventListener,
					mySensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);
			sensorrunning = true;
		} else {
			sensorrunning = false;
			finish();
		}
		// setting up the location listener
		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				100, 0, mlocListener);
		// end of location listener setup
		// setting up the buttons that are in the MapView layout file
		findPosition = (Button) findViewById(R.id.findPositionButton);
		exit = (Button) findViewById(R.id.Exit);
		ClearPointsButton = (Button) findViewById(R.id.Clear_Points_Button);
		findPosition.setOnClickListener(this);
		exit.setOnClickListener(this);
		ClearPointsButton.setOnClickListener(this);
		AugmentedRealityViewerButton = (Button) findViewById(R.id.Augmented_Reality_Button);
		AugmentedRealityViewerButton.setOnClickListener(this);
		// end of mapView layout setup

		// setting the up the map at the proper zoom level and creating the
		// scale bars and buttons
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setMapViewMode(MapViewMode.MAPNIK_TILE_DOWNLOAD);
		mapView.setBuiltInZoomControls(true);
		mapView.setScaleBar(true);
		mapView.setClickable(true);
		// mapView.getController().set
		findPositionButton(myCurrentLocation);
		mapView.getController().setZoom(14);
		setCenterlocation();
		// end of mapView setup

		// setting up the markers with their correct images
		locationMarker = getResources().getDrawable(R.drawable.ic_launcher);
		pointer = new PointSetter(locationMarker, this);
		newMarker = getResources().getDrawable(R.drawable.ic_launcher);
		pointer = new PointSetter(newMarker, this);
		newPoint = getResources().getDrawable(R.drawable.ic_launcher);
		pointer = new PointSetter(newPoint, this);
		userPointOverlay = new MVItemizedOverlay(newMarker);
		itemizedOverlay = new MVItemizedOverlay(newMarker);
		mapView.getOverlays().add(userPointOverlay);
		mapView.getOverlays().add(itemizedOverlay);
		mapView.getOverlays().add(pointer);
		// creating a clustermaker

	}

	private SensorEventListener mySensorEventListener = new SensorEventListener() {
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {
			myCompassView.updateDirection((float) event.values[0]);
		}
	};

	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.findPositionButton):
			findPositionButton(myCurrentLocation);
			break;
		case (R.id.Exit):
			this.finish();//try activityname.finish instead of this
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case (R.id.Clear_Points_Button):
			userPointOverlay.clear();
			break;
		case (R.id.Get_Points_Button):
			drawGeoDataRepositoryPoints();
			mapView.getOverlays().add(GeoDataRepositoryOverlay);

			System.out.println("Created plotter");
			System.out.println("Translated data");
			break;
		case (R.id.Augmented_Reality_Button):
			AugmentedRealityView();
			break;
		}

	}

	public void findPositionButton(GeoPoint p) {
		mapView.getController().setCenter(p);
	}

	public void AugmentedRealityView() {

		//this.finish();
		Intent cameraView_activity = new Intent(getApplicationContext(),
				CameraView.class);
		// Starts the activity
		startActivity(cameraView_activity);

	}

	@Override
	/**
	 * onDestroy stops or "destroys" the program when this actions is called.
	 */
	protected void onDestroy() {
		super.onDestroy();
		if (sensorrunning)
			mySensorManager.unregisterListener(mySensorEventListener);
	}

    public void myClickMethod(View v){
		  Intent i;
		  switch(v.getId()){
			  case R.id.map:
				  i = new Intent(getApplicationContext(), GoogleMapsViewer.class);
				  startActivity(i);
				  break;
			  case R.id.augmentedReality:
				  i = new Intent(getApplicationContext(), CameraView.class);
				  startActivity(i);
				  break;
			  case R.id.emergencyCall:
				  System.out.println("BOO");
				  break;
			  case R.id.exit:
				  System.exit(0);
				  break;
		  }
	  }
    
    public void onLocationChanged(Location arg0)
    {
    }
	@Override
	/**
	 * onPause pauses the application and saves the data in the savedInstances Bundle. 
	 */
	protected void onPause() {
		super.onPause();
	}
	@Override
	/**
	 * onResume sets the actions that the application runs through when starting the application from pause.
	 */
	protected void onResume() {
		super.onResume();
	}

	/**
	 * setCenterLocation is the method that sets the center of the screen to a
	 * already specified point on the map.
	 */
	protected void setCenterlocation() {
		if (myCurrentLocation == null)
			mapView.getController().setCenter(new GeoPoint(39.00, -100.00));
		else
			mapView.getController().setCenter(myCurrentLocation);
	}

	/*
	 * this method takes the GeoDataRepository and creates GeoPoints from its
	 * entries. While it is doing this it is also finding the distance between
	 * the user's location and the point that it currently iterating over
	 */
	public void drawGeoDataRepositoryPoints() {
		GeoDataRepository repo = geoPointDataRetriever();
		final Map<GeoPoint, DataObject> entriesColl = repo
				.getCollectionOfDataEntries();

		// take the list of GeoPoints and enter that list into the cluster
		// maker.
		// then take that list and pump that into the draw function

		// the minimum pixel distance between the points is set here
		int minPixelDistance = 50;
		Map<MegaPoint, DataObject> tempMegaArray = ClusterMaker
				.pointClusterMaker(entriesColl, minPixelDistance, mapView);
		entriesColl
				.putAll((Map<? extends GeoPoint, ? extends DataObject>) tempMegaArray);
		// Map<GeoPoint, DataObject> tempMap =
		// geoPointClusterMaker.pointClusterMaker(entriesColl, minPixelDistance,
		// mapView);

		for (GeoPoint entry : entriesColl.keySet()) {
			drawMarker(entry, entriesColl.get(entry));
			// need to create a location out of the GeoPoint and then use the
			// distanceTo method to
			// find the distance between the entry and the user's location
			GeoPoint temporaryGeoPoint = (entry);
			Location geoPointTranslation = convertGeoPointToLocation(temporaryGeoPoint);
			Location newLocation = new Location(
					LocationManager.NETWORK_PROVIDER);
			newLocation.distanceTo(geoPointTranslation);
			// want to test the points being created to see if they are within
			// view distance. and (B) test to see if they are within the draw
			// distance

			if (newLocation.distanceTo(geoPointTranslation) <= 10F
					&& (newLocation.bearingTo(geoPointTranslation) <= AngleOfView)) {
				// myCameraView.drawGeoPoint(myCameraView.getMyCanvas(),
				// temporaryGeoPoint);
				// TODO: add pictures to the camera view
				// the distanceTo method may have to be set to something less
				// than 10F. the focal length of the camera is 4.39 mm
				// add the
				// System.out.println(loc.distanceTo(newLocation));
			}
			// System.out.println("the distance from user location to point is: "
			// + loc.distanceTo(newLocation));
			// System.out
			// .println("the bearing from the FAV of the user  to the point is: "
			// + loc.bearingTo(newLocation));
			// need to test to see where i want the application to show the
			// points (in bearing) and the max draw distance for the points.
		}

	}

	public static Location convertGeoPointToLocation(GeoPoint gp) {
		Location newLoc = new Location(LocationManager.NETWORK_PROVIDER);
		newLoc.setLatitude(gp.getLatitude());
		newLoc.setLongitude(gp.getLongitude());
		return newLoc;
	}

	/**
	 * GeoDataRepository is the method that starts the CameraView class.
	 */
	protected GeoDataRepository geoPointDataRetriever() {
		final String dataString = LocationDataReceiverAVATAR
				.loadDataStringFromURL();
		final GeoDataRepository repo = new GeoDataRepository();
		repo.addEntriesFromURLString(dataString);
		return repo;
	}

	/**
	 * findPositionButton takes GeoPoint p as its argument and sets the center
	 * of the screen to that point.
	 * 
	 * @param p
	 */

	public <E extends GeoPoint> void drawMarker(final E geoPoint,
			final DataObject dataObject) {
		Drawable newMarker = getResources().getDrawable(R.drawable.ic_launcher);
		Drawable locationMarker = getResources().getDrawable(
				R.drawable.ic_launcher);
		DataObjectItem overlayItem = new DataObjectItem(geoPoint, dataObject);
		overlayItem.setMarker(MVItemizedOverlay.boundCenterBottom(newMarker));
		DataObjectItem myLocationMarker = new DataObjectItem(myCurrentLocation,
				dataObject);
		myLocationMarker.setMarker(MVItemizedOverlay
				.boundCenterBottom(locationMarker));
		itemizedOverlay.addOverlay(overlayItem);
	}

	/**
	 * This class listens for the location manager. When the location manager
	 * sends the location listener the files for the map, it runs through the
	 * methods within the class body.
	 * 
	 * @author William
	 */
	public class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location loc) {
			GeoPoint gp = new GeoPoint(loc.getLatitude(), loc.getLongitude());
			if (gp != null)
				myCurrentLocation = gp;
			itemizedOverlay.clear();
			String LatLong = "Point1 --- "
					+ loc.getLatitude()
					+ " --- "
					+ loc.getLongitude()
					+ ";~~Point2 --- 40.12345 --- -85.12345;~~Point3 --- 41.54321 --- -83.54321";
			DataObject data = new DataObject();
			drawMarker(gp, data);
			// //drawGeoDataRepositoryPoints();
			// might have an issue here, because i do not know if the data from
			// the bearing and the distance will carry through
		}

		/**
		 * activates when the current provider is disabled, or not available
		 * anymore.
		 */
		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(), "GPS Disabled",
					Toast.LENGTH_LONG).show();
		}

		/**
		 * activates when a provider is found.
		 */
		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(), "GPS Enabled",
					Toast.LENGTH_LONG).show();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	}

	private class PointSetter extends MVItemizedOverlay {
		Context context;

		public PointSetter(Drawable marker, Context contextIn) {
			super(marker, contextIn);
			this.context = contextIn;
		}

		@Override
		public boolean onLongPress(GeoPoint point, MapView mapView) {
			DataObject data = new DataObject();
			if (point != null) {
				pointLocLat = point.getLatitude();
				pointLocLon = point.getLongitude();
				Constants.lat = "" + pointLocLat;
				Constants.lng = "" + pointLocLon;
				DataObjectItem newPointItem = new DataObjectItem(point, data);
				newPointItem.setMarker(MVItemizedOverlay
						.boundCenterBottom(newPoint));
				userPointOverlay.addOverlay(newPointItem);
				data.setLat(pointLocLat);
				data.setLon(pointLocLon);
				Intent senderIntent = new Intent(getApplicationContext(),
						UploadMedia.class);
				startActivity(senderIntent);
			}
			return true;
		}
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public static MapView getMapView() {
		return mapView;
	}

	public void setMapView(MapView mapView) {
		this.mapView = mapView;
	}

	public GeoPoint getMyCurrentLocation() {
		return myCurrentLocation;
	}

	public void setMyCurrentLocation(GeoPoint myCurrentLocation) {
		this.myCurrentLocation = myCurrentLocation;
	}

	public MVItemizedOverlay getUserPointOverlay() {
		return userPointOverlay;
	}

	public void setUserPointOverlay(MVItemizedOverlay userPointOverlay) {
		this.userPointOverlay = userPointOverlay;
	}

	public MVItemizedOverlay getGeoDataRepositoryOverlay() {
		return GeoDataRepositoryOverlay;
	}

	public void setGeoDataRepositoryOverlay(
			MVItemizedOverlay geoDataRepositoryOverlay) {
		GeoDataRepositoryOverlay = geoDataRepositoryOverlay;
	}

	public CameraView getMyCameraView() {
		return myCameraView;
	}

	public void setMyCameraView(CameraView myCameraView) {
		this.myCameraView = myCameraView;
	}
}
