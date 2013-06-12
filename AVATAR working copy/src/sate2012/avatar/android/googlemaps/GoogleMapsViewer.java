package sate2012.avatar.android.googlemaps;

import gupta.ashutosh.avatar.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import sate2012.avatar.android.MapsForgeMapViewer;
import sate2012.avatar.android.PhoneCall;
import sate2012.avatar.android.UploadMedia;
import sate2012.avatar.android.VideoPlayer;
import sate2012.avatar.android.augmentedrealityview.CameraView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
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
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapsViewer extends Activity implements LocationListener,
InfoWindowAdapter, OnCameraChangeListener, OnMapClickListener, OnMarkerClickListener, 
OnInfoWindowClickListener{

	public GoogleMap map;
	public GoogleMapsClusterMaker clusterMaker;
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
	private ArrayList<MarkerPlus> markerArray = new ArrayList<MarkerPlus>();// = MarkerMaker.makeMarkers();
	private Marker activeMarker = null;
	private Bitmap currentImage = null;
	private boolean gettingURL = false;
	private boolean asyncTaskCancel = false;

	sate2012.avatar.android.augmentedrealityview.CameraView myCameraView = new sate2012.avatar.android.augmentedrealityview.CameraView();
	sate2012.avatar.android.pointclustering.ClusterMaker geoPointClusterMaker = new sate2012.avatar.android.pointclustering.ClusterMaker();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.googlemap_viewer);
		MapFragment mapfrag = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.googlemap));
		map = mapfrag.getMap();
		new HttpThread(this).execute("");
		clusterMaker = new GoogleMapsClusterMaker();
		map.setOnMapLongClickListener(new Listener());
		
		map.setInfoWindowAdapter(this);
		map.setOnCameraChangeListener(this);
		map.setOnMapClickListener(this);
		map.setOnMarkerClickListener(this);
        map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(this);
        
        drawMarkers(true);
		map.setMapType(mapTypes[0]);
		lastKnownZoomLevel = map.getCameraPosition().zoom;
	}
	
	public void drawMarkers(boolean shouldClear){
	
		if(shouldClear){
			map.clear();
		}
		if(activeMarker != null){
			activeMarker.showInfoWindow();
		}
		LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
		
		
		if(markerArray != null){
			int i = 1;
			for(GoogleMapsClusterMarker marker: clusterMaker.generateClusters(markerArray, map.getProjection(), bounds)){
				//if(bounds.contains(marker.latlng)){
					if(marker.getPoints().size() > 1){
						map.addMarker(new MarkerOptions().position(marker.latlng).title("Cluster: " + i++).snippet(marker.getPointNames() + " | " + marker.getPoints().size()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
		//				System.out.println("Added Marker! Position: " + new LatLng(marker.latlng.latitude, marker.latlng.longitude).toString());
		//				System.out.println("Marker Name!: " + marker.getPointNames());
					}else{
						if(marker.getPoints().size() == 1){
						map.addMarker(new MarkerOptions().position(marker.latlng).title(marker.getPoints().get(0).getName()).snippet(marker.getPoints().get(0).getData()));
						}
					}
				//}
			}
		}
		
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
			break;
		case R.id.emergencyCall:
			i = new Intent(getApplicationContext(), PhoneCall.class);
			break;
		case R.id.exit:
			System.exit(0);
			break;
		}
	}
	
	public void onMapClick(LatLng latlng){

	}
	
	public void onInfoWindowClick(Marker marker){
		System.out.println("Clicked!");
		if(marker != null){
				Intent i;
				try{
					if(marker.getSnippet().contains(".f4v")){
						Intent playVideo = new Intent(getApplicationContext(), VideoPlayer.class);
						playVideo.putExtra("video_tag", marker.getSnippet().substring(marker.getSnippet().lastIndexOf(" ")));
						startActivity(playVideo);
					}
				}catch(Exception ex){
					ex.printStackTrace();
			}
		}
	}
	
	public boolean onMarkerClick(Marker marker){
		
		if(activeMarker != null){
			if(!activeMarker.getSnippet().equals(marker.getSnippet())){
				currentImage = null;
				asyncTaskCancel = true;
				//System.out.println("Snippets No Match");
			}
		}
		this.activeMarker = marker;
		return false;
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
	
	public double getMyLatitude(){
		return myLatitude;
	}
	
	public double getMyLongitude(){
		return myLongitude;
	}
	
	public double getMyAltitude(){
		return myAltitude;
	}

	class Listener implements OnMapLongClickListener  {

		@Override
		public void onMapLongClick(LatLng arg0) {
			try{
				Intent senderIntent = new Intent(getApplicationContext(),
						UploadMedia.class);
				senderIntent.putExtra("LatLng", arg0);
				startActivity(senderIntent);
			}catch(Exception ex){
				ex.printStackTrace();
			}
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
	       
			if(currentImage == null && !gettingURL && (marker.getSnippet().contains("png") || marker.getSnippet().contains("jpg") || marker.getSnippet().contains("gif"))){
				activeMarker = marker;
				gettingURL = true;
		        new ImageGrabber(image, this).execute(marker.getSnippet().substring(marker.getSnippet().lastIndexOf(" ")));
		        try {
					if(currentImage != null){
						image.setImageDrawable(new BitmapDrawable(null , currentImage));
					}
				} catch (Exception e){
					e.printStackTrace();
				}
			}else if(currentImage != null){
				image.setImageDrawable(new BitmapDrawable(null, currentImage));
			}else if(marker.getSnippet().contains("png") || marker.getSnippet().contains("jpg") || marker.getSnippet().contains("gif")){
				image.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.loading)));
			}else if(marker.getSnippet().contains(".f4v")){
				image.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.loading)));
			}
	        
		}else{
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

	@Override
	public void onCameraChange(CameraPosition arg0) {
		
		if(arg0.zoom != lastKnownZoomLevel){
			lastKnownZoomLevel = arg0.zoom;
			activeMarker = null;
			drawMarkers(true);
		}
		
		//drawMarkers(false);
		
	}
	
	public void setMarkerArray(ArrayList<MarkerPlus> array){
		this.markerArray = array;
		//System.out.println("Set the Array!");
	}
	
	private class ImageGrabber extends AsyncTask<String, Void,  Bitmap>{

		private ImageView imageSlot;
		private GoogleMapsViewer map;
		private String url;
		
		public ImageGrabber(ImageView imageSlot, GoogleMapsViewer map){
			this.imageSlot = imageSlot;
			this.map = map;
		}
		
		@Override
		protected Bitmap doInBackground(String...params) {
			// TODO Auto-generated method stub
			try {
				currentImage = null;
				url = params[0];
				if(asyncTaskCancel){
					//System.out.println("CANCEL 1");
					asyncTaskCancel = false;
					gettingURL = false;
					this.cancel(true);
				}else
				System.out.println("Getting URL!");
				HttpURLConnection connection = (HttpURLConnection) new URL(params[0]).openConnection();
			    connection.connect();
			    connection.setConnectTimeout(5000);
			    connection.setReadTimeout(5000);
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
				    
				    if(asyncTaskCancel){
				    	//System.out.println("CANCEL 2");
				    	asyncTaskCancel = false;
				    	gettingURL = false;
				    	x = null;
				    	this.cancel(true);
				    }
				    
				    input.close();
				    connection.disconnect();
				    //System.out.println("Set Bitmap :)");
					//return new BitmapDrawable(null, x);
					return x;
				    }
			    input.close();
			    connection.disconnect();
			    return null;
			} catch (Exception e){
				gettingURL = false;
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Bitmap results){
			if(asyncTaskCancel && !(activeMarker.getSnippet().substring(activeMarker.getSnippet().lastIndexOf(" ")).equals(url))){
				//System.out.println("CANCEL 3");
				asyncTaskCancel = false;
				gettingURL = false;
				results = null;
				currentImage = null;
				map.drawMarkers(true);
				this.cancel(true);
			}else if(!(activeMarker.getSnippet().substring(activeMarker.getSnippet().lastIndexOf(" ")).equals(url))){
				//System.out.println("CANCEL 5");
				asyncTaskCancel = false;
				gettingURL = false;
				results = null;
				currentImage = null;
				map.drawMarkers(true);
				this.cancel(true);
			}else{
				imageSlot.setImageBitmap(results);
				currentImage = results;
				map.drawMarkers(true);
				gettingURL = false;
				imageSlot = null;
				map = null;
			}
			
		}
			
	}

}
