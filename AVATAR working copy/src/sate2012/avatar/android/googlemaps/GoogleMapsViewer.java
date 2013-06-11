package sate2012.avatar.android.googlemaps;

import gupta.ashutosh.avatar.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import sate2012.avatar.android.MapsForgeMapViewer;
import sate2012.avatar.android.UploadMedia;
import sate2012.avatar.android.augmentedrealityview.CameraView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapsViewer extends Activity implements LocationListener,
InfoWindowAdapter, OnCameraChangeListener {

	public GoogleMap map;
	public GoogleMapsClusterMaker clusters = new GoogleMapsClusterMaker();
	public Location myLocation = new Location(LocationManager.NETWORK_PROVIDER);
	public Location myCurrentLocation;
	private double myAltitude;
	private double myLatitude;
	private double myLongitude;
	private float lastKnownZoomLevel;
	private static SensorManager mySensorManager;
	private boolean sensorrunning;
	private boolean hasMapCentered = false;
	private int[] mapTypes = { GoogleMap.MAP_TYPE_NORMAL,
			GoogleMap.MAP_TYPE_SATELLITE, GoogleMap.MAP_TYPE_HYBRID,
			GoogleMap.MAP_TYPE_TERRAIN };
	private int currentMapType = mapTypes[0];
	private ArrayList<MarkerPlus> markerArray = MarkerMaker.makeMarkers();
	private ArrayList<MarkerPlus> offlineMarkerArray = new ArrayList<MarkerPlus>();

	sate2012.avatar.android.augmentedrealityview.CameraView myCameraView = new sate2012.avatar.android.augmentedrealityview.CameraView();
	sate2012.avatar.android.pointclustering.ClusterMaker geoPointClusterMaker = new sate2012.avatar.android.pointclustering.ClusterMaker();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.googlemap_viewer);
		MapFragment mapfrag = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.googlemap));
		map = mapfrag.getMap();
		map.setOnMapLongClickListener(new Listener());
		
		map.setInfoWindowAdapter(this);

		map.setInfoWindowAdapter(this);
		map.setOnCameraChangeListener(this);
		
//		offlineMarkerArray.add(new MarkerPlus(10.0,10.0,10.0, "POINT 1"));
//		offlineMarkerArray.add(new MarkerPlus(9.9,9.9,9.0, "POINT 2"));
//		offlineMarkerArray.add(new MarkerPlus(9.5, 9.5, 8.0, "POINT 3"));
//		offlineMarkerArray.add(new MarkerPlus(9.0, 9.0, 8.0, "POINT 4"));
//		
//		int name = 1;
//		for(MarkerPlus tempMark: offlineMarkerArray){
//			tempMark.setName("POINT " + name++);
//		}
        
        map.setMyLocationEnabled(true);
        drawMarkers(false);
		map.setMapType(mapTypes[0]);

		

		
		lastKnownZoomLevel = map.getCameraPosition().zoom;
	}
	
	private void drawMarkers(boolean shouldClear){
	
		if(shouldClear){
			map.clear();
		}
		
		LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
		
		int i = 1;
		for(GoogleMapsClusterMarker marker: clusters.generateClusters(map.getCameraPosition().zoom, markerArray)){
			if(bounds.contains(marker.latlng)){
				if(marker.getPoints().size() > 1){
					map.addMarker(new MarkerOptions().position(marker.latlng).title("Cluster: " + i++).snippet(marker.getPointNames() + " | " + marker.getPoints().size()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
	//				System.out.println("Added Marker! Position: " + new LatLng(marker.latlng.latitude, marker.latlng.longitude).toString());
	//				System.out.println("Marker Name!: " + marker.getPointNames());
				}else{
					if(marker.getPoints().size() == 1){
					map.addMarker(new MarkerOptions().position(marker.latlng).title(marker.getPoints().get(0).getName()).snippet(marker.getPoints().get(0).getData()));
					}
				}
			}
		}
		
//		int i = 1;
//		for(GoogleMapsClusterMarker marker: clusters.generateClusters(map.getCameraPosition().zoom, offlineMarkerArray)){
//			
//			if(marker.getPoints().size() > 1){
		      // if(bounds.contains(marker.latlng)){
	//				map.addMarker(new MarkerOptions().position(marker.latlng).title("Cluster: " + i++).snippet(marker.getPointNames()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
	//				System.out.println("Added Marker! Position: " + new LatLng(marker.latlng.latitude, marker.latlng.longitude).toString());
	//				System.out.println("Marker Name!: " + marker.getPointNames());
	//			}else{
	//				map.addMarker(new MarkerOptions().position(marker.latlng).title(marker.getPoints().get(0).getName()).snippet(marker.getPoints().get(0).getData()));
	//			}
		       //}
//		}
		
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.google_maps_viewer, menu);
		return true;
	}

	/**
	 * Method that determines what happens depending on which button was clicked
	 * 
	 * @param v
	 *            : The view
	 */
	public void myClickMethod(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.map:
			i = new Intent(getApplicationContext(), MapsForgeMapViewer.class);
			startActivity(i);
			break;
		case R.id.augmentedReality:
			i = new Intent(getApplicationContext(), CameraView.class);
			startActivity(i);
			break;
		case R.id.changeType:

			for (int c = 0; c < mapTypes.length; c++) {
				if (mapTypes[c] == currentMapType) {
					if (mapTypes.length - 1 != c) {
						currentMapType = mapTypes[c + 1];
						map.setMapType(currentMapType);
					} else {
						currentMapType = mapTypes[0];
						map.setMapType(currentMapType);
					}
					c = mapTypes.length;
				}
			}

		}
	}

	/**
	 * Method called whenever the user's location is changed
	 * 
	 * @param loc
	 *            : New location of the user.
	 */
	public void onLocationChanged(Location loc) {

		myLatitude = loc.getLatitude();
		myLongitude = loc.getLongitude();
		myAltitude = loc.getAltitude();

		// Debugging Tools
		// String TAG = "Testing: ";
		//
		// Log.d(TAG, "Latitude: " + String.valueOf(myLatitude));
		// Log.d(TAG, "Longitude: " + String.valueOf(myLongitude));
		// Log.d(TAG, "Altitude: " + String.valueOf(myAltitude));
		// map.addMarker(new MarkerOptions().position(new LatLng(myLatitude,
		// myLongitude)));
		// System.out.println("HEY");

		if (!hasMapCentered) {
			map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition
					.fromLatLngZoom(new LatLng(myLocation.getLatitude(),
							myLocation.getLongitude()),
							map.getMaxZoomLevel() / 3)));
			hasMapCentered = true;
		}
	}

	class Listener implements OnMapLongClickListener {

		@Override
		public void onMapLongClick(LatLng arg0) {
			Intent senderIntent = new Intent(getApplicationContext(),
					UploadMedia.class);
			senderIntent.putExtra("LatLng", arg0);
			startActivity(senderIntent);
			MarkerPlus tempPoint = new MarkerPlus(arg0.latitude, arg0.longitude, map.getMyLocation().getAltitude());
			tempPoint.setName("User Point");
			tempPoint.setInfo("User Submitted Point");
			markerArray.add(tempPoint);
			drawMarkers(true);
		}
	}

	private SensorEventListener mySensorEventListener = new SensorEventListener() {
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {
		}
	};

	@Override
	/**
	 * onDestroy stops or "destroys" the program when this actions is called.
	 */
	protected void onDestroy() {
		super.onDestroy();
		if (sensorrunning)
			mySensorManager.unregisterListener(mySensorEventListener);
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

	@Override
	public void onProviderDisabled(String arg0) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	// Info Window Adapter implemented methods

	@Override
	public View getInfoContents(Marker marker) {
		View v = getLayoutInflater().inflate(R.layout.marker_contents, null);

		// Getting reference to the TextView to set title
		TextView title = (TextView) v.findViewById(R.id.marker_title);
		TextView info = (TextView) v.findViewById(R.id.marker_info);
		ImageView image = (ImageView) v.findViewById(R.id.marker_image);

		//Determining what is the snippit, what is not.
		int snippetIndex = marker.getSnippet().length();
		int clusterSize = 1;
		if(marker.getSnippet().contains("|")){
			snippetIndex = marker.getSnippet().lastIndexOf("|")-1;
			clusterSize = Integer.parseInt(marker.getSnippet().substring(snippetIndex+3));
		}
		
		title.setText(marker.getTitle());
		info.setText(marker.getSnippet().substring(0, snippetIndex));
		// image.setImageDrawable();
        
		if(!(new String("Cluster").regionMatches(0, marker.getTitle(), 0, 6))){
	        ImageGrabber grab = new ImageGrabber();
	         grab = (ImageGrabber) grab.execute(marker.getSnippet().substring(marker.getSnippet().lastIndexOf(" ")));
	        try {
				image.setImageDrawable(grab.get());
			} catch (Exception e){
				e.printStackTrace();
			}
	        
		}else{
			image.setImageResource(R.drawable.ic_launcher);
			
			Bitmap clusterImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			Bitmap tempImage = clusterImage.copy(Bitmap.Config.ARGB_8888, true);
			Canvas canvas = new Canvas(tempImage);
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			int xOffset = (int) (3*(clusterSize+"").length());
			canvas.drawText(clusterSize+"", tempImage.getWidth()/2-xOffset, tempImage.getHeight()/2+4, paint);
			
			image.setImageBitmap(tempImage);
		}
        //Returning the view containing InfoWindow contents
        return v;

	}

	@Override
	public View getInfoWindow(Marker marker) {
		View v = null;// getLayoutInflater().inflate(R.layout.marker_info_window,
						// null);
		return v;
	}
	
	private class ImageGrabber extends AsyncTask<String, String, Drawable>{

		@Override
		protected Drawable doInBackground(String... params) {
			try {
				HttpURLConnection connection = (HttpURLConnection) new URL(params[0]).openConnection();
			    connection.connect();
			    connection.setConnectTimeout(1000);
			    connection.setReadTimeout(1000);
			    InputStream input = connection.getInputStream();
			    Bitmap x = BitmapFactory.decodeStream(input);
			    
			    
			    //Max Image Height and Width
			    int MAXWIDTH = 150;//= 270;
			    int MAXHEIGHT = 100;//=150;
			    
			    if(x != null){
				    int imageWidth = x.getWidth();
				    int imageHeight = x.getHeight();
				    
				    if(imageWidth > MAXWIDTH || imageHeight > MAXHEIGHT){
				    	double ratio = (imageWidth > imageHeight)? ((float) MAXWIDTH)/imageWidth: ((float) MAXHEIGHT)/imageHeight;
				    	
				    	imageWidth =(int) (imageWidth*ratio);
				    	imageHeight =(int) (imageHeight*ratio);
				    	
				    	x = Bitmap.createScaledBitmap(x, imageWidth, imageHeight, false);
				    }
				    
				    input.close();
					return new BitmapDrawable(null, x);
				    }
			    input.close();
			    return null;
			} catch (Exception e){
				e.printStackTrace();
			}
			return null;
		}
		
	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
		
		if(arg0.zoom != lastKnownZoomLevel){
			map.clear();
			lastKnownZoomLevel = arg0.zoom;
		}
		
		drawMarkers(false);
		
	}
}
