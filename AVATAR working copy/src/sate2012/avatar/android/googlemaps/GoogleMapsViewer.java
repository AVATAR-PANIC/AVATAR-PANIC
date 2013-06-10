package sate2012.avatar.android.googlemaps;

import gupta.ashutosh.avatar.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import sate2012.avatar.android.Frag;
import sate2012.avatar.android.MapsForgeMapViewer;
import sate2012.avatar.android.UploadMedia;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapsViewer extends Activity implements LocationListener,
		InfoWindowAdapter {

	public GoogleMap map;
	private Frag frag = new Frag();
	public Location myLocation = new Location(LocationManager.NETWORK_PROVIDER);
	public Location myCurrentLocation;
	private double myAltitude;
	private double myLatitude;
	private double myLongitude;
	private static SensorManager mySensorManager;
	private boolean sensorrunning;
	private boolean hasMapCentered = false;
	private int[] mapTypes = { GoogleMap.MAP_TYPE_NORMAL,
			GoogleMap.MAP_TYPE_SATELLITE, GoogleMap.MAP_TYPE_HYBRID,
			GoogleMap.MAP_TYPE_TERRAIN };
	private int currentMapType = mapTypes[0];
	private ArrayList<MarkerPlus> markerArray = MarkerMaker.makeMarkers();

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

		for (MarkerPlus marker : markerArray) {
			map.addMarker(marker.getMarkerOptions().title(marker.getName())
					.snippet(marker.getData()));
		}

		map.setMyLocationEnabled(true);

		// System.out.println(myLocation.getLatitude() + " " +
		// myLocation.getLongitude());
		// LatLng location = new LatLng(myLocation.getLatitude(),
		// myLocation.getLongitude());
		// map.addMarker(new MarkerOptions().position(location));
		// map.moveCamera(CameraUpdateFactory.newCameraPosition(new
		// CameraPosition(map.getCameraPosition().target,
		// map.getCameraPosition().zoom, 30, map.getCameraPosition().bearing)));
		// map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(new
		// LatLng(myLocation.getLatitude(), myLocation.getLongitude()),
		// map.getMaxZoomLevel()/3)));
		map.setMapType(mapTypes[0]);
		//How to add marker
		//map.addMarker(new MarkerOptions().title("TITLE").snippet("DESCRIPTION").position(new LatLng(0,0)));
		

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

			break;
		default:

			frag.myClickMethod(v, getApplicationContext());

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
			map.addMarker(new MarkerOptions().position(arg0));
			Intent senderIntent = new Intent(getApplicationContext(),
					UploadMedia.class);
			Bundle bundle = new Bundle();
			senderIntent.putExtra("LatLng", arg0);
			startActivity(senderIntent);
		}
	}

	private SensorEventListener mySensorEventListener = new SensorEventListener() {
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {
			// myCompassView.updateDirection((float) event.values[0]);
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

		title.setText(marker.getTitle());
		info.setText(marker.getSnippet());
		// image.setImageDrawable();

        title.setText(marker.getTitle() );
        info.setText(marker.getSnippet() );
        //image.setImageDrawable();
        
        ImageGrabber grab = new ImageGrabber();
         grab = (ImageGrabber) grab.execute(marker.getSnippet().substring(marker.getSnippet().lastIndexOf(" ")));
        try {
			image.setImageDrawable(grab.get());
		} catch (Exception e){
			e.printStackTrace();
		}
        // Returning the view containing InfoWindow contents
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
			    InputStream input = connection.getInputStream();
			    Bitmap x = BitmapFactory.decodeStream(input);
				return new BitmapDrawable(null, x);
			} catch (Exception e){
				e.printStackTrace();
			}
			return null;
		}
		
	}
}
