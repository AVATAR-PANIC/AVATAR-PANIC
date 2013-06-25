package sate2012.avatar.android.googlemaps;

import gupta.ashutosh.avatar.R;

import java.util.Timer;
import java.util.TimerTask;

import sate2012.avatar.android.googlemaps.GoogleMapsViewer.Listener;
import DialogFragments.MapSettingsDialogFragment;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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

public class EagleEyeGoogleMapsViewer extends Fragment implements InfoWindowAdapter, 
OnCameraChangeListener, OnMapClickListener, OnMarkerClickListener, OnInfoWindowClickListener, OnMapLongClickListener {

	private static View view;
	public GoogleMap map;
	public int currentMapType = GoogleMap.MAP_TYPE_NORMAL;
	public int currentMap = 4;
	
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
			view = inflater.inflate(R.layout.googlemap_viewer, container, false);
		}catch(InflateException e){
			
		}
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		//Need to create it's own unique menu.
		inflater.inflate(R.menu.avatar_map_menu, menu);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		FragmentManager fragMgr;
		fragMgr = getFragmentManager();
		
		DialogFragment dialog;
		
		dialog = new MapSettingsDialogFragment(currentMap, currentMapType);
		dialog.show(fragMgr, "MAP_SETTINGS");
		
		return false;
	}
	
	/**
	 * When the fragment is started, this runs.
	 */
	@Override
	public void onStart() {
		super.onStart();
		MapFragment mapfrag = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.googlemap));
		setHasOptionsMenu(true);
		map = mapfrag.getMap();
		Bundle b = getArguments();
		currentMapType = b.getInt("MAP_TYPE");
		map.setMapType(currentMapType);
		
		//Set the Maps listeners
		map.setOnMapLongClickListener(this);
		map.setInfoWindowAdapter(this);
		map.setOnCameraChangeListener(this);
		map.setOnMapClickListener(this);
		map.setOnMarkerClickListener(this);
        map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(this);
        
		
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onMapClick(LatLng point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public View getInfoContents(Marker marker) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onMapLongClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
	}
}
