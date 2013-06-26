package sate2012.avatar.android.googlemaps;

import gupta.ashutosh.avatar.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import tricorder.tecedge.PHPScriptQuery;
import tricorder.tecedge.Refreshm;
import tricorder.tecedge.Settings;
import DialogFragments.MapSettingsDialogFragment;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * 
 * @author Garrett - emrickgarrett@gmail.com
 * 
 * This class is used for the implementation of the Tricorder Google Maps.
 *
 */
public class TricorderGoogleMapsViewer extends Fragment implements InfoWindowAdapter, OnCameraChangeListener, 
OnMapClickListener, OnMarkerClickListener, OnInfoWindowClickListener, OnMapLongClickListener {

	//Stuff for the map selection settings
	private int currentMapType = GoogleMap.MAP_TYPE_NORMAL;
	private int currentMap = 2; // The ID for Tricorder
	
	//For the Database side of the project
	public static Handler h;
	static String initializing;
	PHPScriptQuery database;
	String result;
	Boolean value;
	
	//For drawing Perhaps
	long start;
	long stop;
	int lati, longi;
	int x, y;
	//Geo Point would go here.
	LatLng touchedPoint;
	//Location manager would go here.
	String towers;
	//Need for information on the Refreshm class.
	Refreshm refresh;
	
	//Needed for the fragment and Google Map
	private static View view;
	public GoogleMap map;
	private TricorderGoogleMapsViewer mapViewer;
	private ArrayList<TricorderMarkerPlus> markers;
	
	/**
	 * When the Fragment View is created, this is called
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		if(view != null){
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null){
				parent.removeView(view);
			}
		}
		try{
			view = inflater.inflate(R.layout.tricorder_googlemap_viewer, container, false);
		}catch(InflateException e){
			
		}
		return view;
	}
	
	/**
	 * Used to set the Menu for the Options menu
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.tricorder_map_menu, menu);
	}
	
	/**
	 * When an options item is selected, only used Map Settings so we don't need a switch
	 * statement.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		FragmentManager fragMgr;
		fragMgr = getFragmentManager();
		DialogFragment dialog;
		
		dialog = new MapSettingsDialogFragment(currentMap, currentMapType);
		dialog.show(fragMgr, "MAP_SETTINGS");
		
		return true;
	}
	
	/**
	 * When the fragment is started, this runs.
	 */
	@Override
	public void onStart() {
		super.onStart();
		MapFragment mapfrag = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.tricorder_googlemap));
		setHasOptionsMenu(true);
		map = mapfrag.getMap();
		Bundle b = getArguments(); // The map type is sent from the bundle.
		currentMapType = b.getInt("MAP_TYPE");
		map.setMapType(currentMapType);
		
		markers = new ArrayList<TricorderMarkerPlus>();
		
		//Set the Maps listeners
		map.setOnMapLongClickListener(this);
		map.setInfoWindowAdapter(this);
		map.setOnCameraChangeListener(this);
		map.setOnMapClickListener(this);
		map.setOnMarkerClickListener(this);
        map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(this);
        
        //Loads preferences set from the settings part of Tricorder
		if (!loadPreferences("firsttime").equalsIgnoreCase("false")) {
			savePreferences("timefilter", "false");
			savePreferences("firsttime", "false");
			savePreferences("alltype", "true");
			savePreferences("compass", "true");
			savePreferences("ZoomButtons", "true");
			savePreferences("SatelliteView", "true");
		}
        
		mapViewer = this;
		
		//Starts downloading from the online database, 
		//Can't use in current state since we don't have the sensors.
        database = new PHPScriptQuery();
        new DownloadFilesTask().execute();
        
        //Handler that listens for a callback message.
        h = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					new DownloadFilesTask().execute();
					break;
				}
			}
		};
		
	}
	
	/**
	 * Adds data to the marker array, in it's current state we don't have the sensor
	 * so I have set it up to generate random data at the moment; - Garrett
	 * @param currentData : The data to be added to the database.
	 */
	public void addData(ArrayList<TricorderMarkerPlus> currentData){
		
		//For testing of the Map
		generateData();
		drawMarkers();
		
		//If the data size is zero, it's still loading.
		if (currentData.size() == 0) {
			try{
			Toast.makeText(getActivity(), "Loading Data...Please Wait",
					Toast.LENGTH_LONG).show();
			return;
			}catch(NullPointerException ex){
				ex.printStackTrace();
				//I have no idea how it could be null lol.
			}
		}
		
		//Used to add the markers depending on their sensor types.
		for (int i = 0; i < currentData.size(); i++) {

			TricorderMarkerPlus sensorItem = currentData.get(i);
			String sensorType = sensorItem.getType();
			//Not sure if this was correct
			//System.out.println(sensorType);

			if (sensorType.equalsIgnoreCase("Temperature")) {
				sensorItem.setImageID(R.drawable.pintemp);
				markers.add(sensorItem);
			} else if (sensorType.equalsIgnoreCase("CO2")) {
				sensorItem.setImageID(R.drawable.pincotwo);
				markers.add(sensorItem);
			} else if (sensorType.equalsIgnoreCase("Methane")) {
				sensorItem.setImageID(R.drawable.pinmethane);
				markers.add(sensorItem);
			} else if (sensorType.equalsIgnoreCase("Barometric Pressure")) {
				sensorItem.setImageID(R.drawable.pinpressure);
				markers.add(sensorItem);
			} else if (sensorType.equalsIgnoreCase("CO")) {
				sensorItem.setImageID(R.drawable.pinco);
				markers.add(sensorItem);
			} else if (sensorType.equalsIgnoreCase("Humidity")) {
				sensorItem.setImageID(R.drawable.pinhumidity);
				markers.add(sensorItem);
			} else if (sensorType.equalsIgnoreCase("Radiation")) {
				sensorItem.setImageID(R.drawable.pinradiation);
				markers.add(sensorItem);
			} else if (sensorType.equalsIgnoreCase("Luminosity")) {
				sensorItem.setImageID(R.drawable.pinluminosity);
				markers.add(sensorItem);
			} else if (sensorType.equalsIgnoreCase("Image")) {
				sensorItem.setImageID(R.drawable.pinimage);
				markers.add(sensorItem);
			} else {
				sensorItem.setImageID(R.drawable.ic_launcher);
				markers.add(sensorItem);
			}
		}
		
		//Draw the markers.
		drawMarkers();
		
	}
	
	/**
	 * Class used to draw the markers on the map.
	 */
	public void drawMarkers(){
		
		//System.out.println("DRAWING MARKERS! YOUR ARRAY SIZE IS: " + markers.size());
		map.clear();
		//If the markers size isn't 0, add the markers.
		if(markers.size() != 0){
			for(int i = 0; i < markers.size(); i ++){
				map.addMarker(markers.get(i).getMarkerOptions());
			}
		}
	}
	
	/**
	 * Random data generator, when you have the sensor you shouldn't use this.
	 * This is simply used for testing purposes.
	 */
	public void generateData(){
		Random r = new Random();
		int size = r.nextInt(20)+1;
		for(int i = 0; i < size; i++){
			double lat = -90 + (90 +90) * r.nextDouble();
			double lon = -180 + (180 + 180) * r.nextDouble();
			String info = "This is Point number: " + i;
			String date = new Date().toLocaleString();
			String phone_ID = i+"";
			int imageID = R.drawable.ic_launcher;
			String type = "";
			
			switch(r.nextInt(10)+1){
			case 1:
				imageID = R.drawable.pinsensor;
				type = "Sensor";
				break;
			case 2:
				imageID = R.drawable.pincotwo;
				type = "CO2";
				break;
			case 3:
				imageID = R.drawable.pintemp;
				type = "Temperature";
				break;
			case 4:
				imageID = R.drawable.pinmethane;
				type = "Methane";
				break;
			case 5:
				imageID = R.drawable.pinpressure;
				type = "Barometric Pressure";
				break;
			case 6:
				imageID = R.drawable.pinco;
				type = "CO";
				break;
			case 7:
				imageID = R.drawable.pinhumidity;
				type = "Humidity";
				break;
			case 8:
				imageID = R.drawable.pinradiation;
				type = "Radiation";
				break;
			case 9:
				imageID = R.drawable.pinluminosity;
				type = "Luminosity";
				break;
			case 10:
				imageID = R.drawable.pinimage;
				type = "Image";
				break;
			
			}
			
			markers.add(new TricorderMarkerPlus(lat, lon, info, type, date, phone_ID, imageID));
			
		}
		
	}

	@Override
	public void onMapLongClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onMapClick(LatLng point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	/**
	 * Used for the buttons on the Tricorder Map.
	 * @param v : The view.
	 */
	public void tricorderOnClick(View v){
		Intent i;
		//System.out.println("CLICK");
		
		switch(v.getId()){
		
		case R.id.tri_menu_settings:
			 i = new Intent("tricorder.tecedge.SETTINGS");
			 startActivity(i);
			break;
		case R.id.tri_menu_upload:
			i = new Intent("tricorder.tecedge.Displaym");
			startActivity(i);
			break;
		case R.id.tri_menu_refresh: // This currently generates random data.
			addData(new ArrayList<TricorderMarkerPlus>());
			//i = new Intent("tricorder.tecedge.Refresh");
			//startActivity(i);
			break;
		
		
		}
	}

	/**
	 * Loads the users preferences. 
	 * @param key : The key for the preferences file.
	 * @return : The save.
	 */
	private String loadPreferences(String key) {
		String save;
		try{
		SharedPreferences preferences = getActivity().getSharedPreferences(
				Settings.PREF_FILE_NAME, getActivity().MODE_PRIVATE);
		save = preferences.getString(key, "sensor_type=''");
		}catch(Exception ex){
			ex.printStackTrace();
			save = "";
		}
		return save;
	}
	
	/**
	 * Saves the users preferences
	 * @param key : The key for the file.
	 * @param value : The prefences to be saved.
	 */
	private void savePreferences(String key, String value) {
		SharedPreferences preferences = getActivity().getSharedPreferences(
				Settings.PREF_FILE_NAME, getActivity().MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	

	/**
	 * Downloads the data for the sensors asynchronously. Only useful when a 
	 * Arduino machine equipped with sensors is active and sending data to the 
	 * database.
	 * @author Garrett - emrickgarrett@gmail.com
	 *
	 */
	public class DownloadFilesTask extends AsyncTask<Context, Integer, Integer> {

		private ArrayList<TricorderMarkerPlus> serverData;

		@Override
		protected Integer doInBackground(Context... params) {
			String where = "";
			String sqltime = "";
			String condition = "";
			if (loadPreferences("timeall").equalsIgnoreCase("true")) {
				where = " WHERE sensor_date >= 0";
				sqltime = " AND sensor_date >= " + loadPreferences("Start")
						+ " AND sensor_date <= " + loadPreferences("Stop");
			} else {
				where = "";
				sqltime = "";
			}
			if (!loadPreferences("allType").equalsIgnoreCase("true")) {
				condition = " WHERE " + loadPreferences("tempType") + sqltime
						+ " OR " + loadPreferences("windType") + sqltime
						+ " OR " + loadPreferences("CO2Type") + sqltime
						+ " OR " + loadPreferences("imgType") + sqltime
						+ " OR " + loadPreferences("pressureType") + sqltime
						+ " OR " + loadPreferences("COType") + sqltime + " OR "
						+ loadPreferences("humidityType") + sqltime + " OR "
						+ loadPreferences("radiationType") + sqltime + " OR "
						+ loadPreferences("luminosityType") + sqltime + " OR "
						+ loadPreferences("methaneType");
			} else {
				condition = where + sqltime;
			}
			serverData = database.getMapData(condition);
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			addData(serverData);
			System.out.println("Downloaded Data!");
			super.onPostExecute(result);
		}

	}

	public static void initializing(String string) {
		initializing = string;
	};

	/**
	 * Don't use, import from old project that we may implement some day.
	 * @author Garrett - emrickgarrett@gmail.com
	 *
	 */
	class TouchOver extends Overlay {
		public boolean onTouchEvent(MotionEvent e, MapView m) {
			if (e.getAction() == MotionEvent.ACTION_DOWN) {
				start = e.getEventTime();
				x = (int) e.getX();
				y = (int) e.getY();
				touchedPoint = map.getProjection().fromScreenLocation(new Point(x, y));//.fromPixels(x, y);
				// getLatitudeandLongitude

			}
			if (e.getAction() == MotionEvent.ACTION_UP) {
				stop = e.getEventTime();
			}
			if (stop - start > 2000) {
				Geocoder geocoder = new Geocoder(getActivity().getBaseContext(),
						Locale.getDefault());
				try {
					List<Address> address = geocoder.getFromLocation(
							touchedPoint.latitude / 1E6,
							touchedPoint.longitude / 1E6, 1);
					System.out.println(touchedPoint.latitude / 1E6);
					if (address.size() > 0) {
						String display = "";
						for (int z = 0; z < address.get(0)
								.getMaxAddressLineIndex(); z++) {
							display += address.get(0).getAddressLine(z) + "\n";
						}
						Toast t = Toast.makeText(getActivity().getBaseContext(), display,
								Toast.LENGTH_LONG);
						t.show();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
				}
			}
			return false;
		}
	}
}
