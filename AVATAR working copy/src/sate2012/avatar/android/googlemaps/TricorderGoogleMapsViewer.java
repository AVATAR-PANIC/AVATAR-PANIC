package sate2012.avatar.android.googlemaps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import gupta.ashutosh.avatar.R;
import tricorder.tecedge.PHPScriptQuery;
import tricorder.tecedge.Refreshm;
import tricorder.tecedge.Settings;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.InflateException;
import android.view.LayoutInflater;
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
import com.google.android.maps.OverlayItem;

public class TricorderGoogleMapsViewer extends Fragment implements InfoWindowAdapter, OnCameraChangeListener, 
OnMapClickListener, OnMarkerClickListener, OnInfoWindowClickListener, OnMapLongClickListener {

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
	 * When the fragment is started, this runs.
	 */
	@Override
	public void onStart() {
		super.onStart();
		MapFragment mapfrag = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.googlemap));
		map = mapfrag.getMap();
		
		//Set the Maps listeners
		map.setOnMapLongClickListener(this);
		map.setInfoWindowAdapter(this);
		map.setOnCameraChangeListener(this);
		map.setOnMapClickListener(this);
		map.setOnMarkerClickListener(this);
        map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(this);
        
		if (!loadPreferences("firsttime").equalsIgnoreCase("false")) {
			savePreferences("timefilter", "false");
			savePreferences("firsttime", "false");
			savePreferences("alltype", "true");
			savePreferences("compass", "true");
			savePreferences("ZoomButtons", "true");
			savePreferences("SatelliteView", "true");
		}
        
		mapViewer = this;
		
        database = new PHPScriptQuery();
        new DownloadFilesTask().execute();
		
	}
	
	public void addData(ArrayList<TricorderMarkerPlus> currentData){
		
	}
	
	public void drawMarkers(){
		
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
	
	private void savePreferences(String key, String value) {
		SharedPreferences preferences = getActivity().getSharedPreferences(
				Settings.PREF_FILE_NAME, getActivity().MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	

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
