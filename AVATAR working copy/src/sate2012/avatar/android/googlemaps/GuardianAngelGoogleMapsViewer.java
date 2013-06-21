package sate2012.avatar.android.googlemaps;

import gupta.ashutosh.avatar.R;

import java.util.ArrayList;
import java.util.Date;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.android.gms.maps.CameraUpdateFactory;
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
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.guardian_angel.uav_tracker.MajorCitiesDialogFragment;
import com.guardian_angel.uav_tracker.Map.Coordinates;
import com.guardian_angel.uav_tracker.CameraRecord;
import com.guardian_angel.uav_tracker.CustomizedOverlay;
import com.guardian_angel.uav_tracker.NotificationService;
import com.guardian_angel.uav_tracker.XMPPSender;

public class GuardianAngelGoogleMapsViewer extends Fragment implements OnMapLongClickListener, InfoWindowAdapter, 
OnCameraChangeListener, OnMapClickListener, OnMarkerClickListener, OnInfoWindowClickListener {

	//Dialog Variables - May be switched to pop-ups once I get there.
	private MajorCitiesDialogFragment dialog;
	protected Button ok;
	protected Button cancel;
	private RadioButton NewYorkCity;
	private RadioButton Chicago;
	private RadioButton Houston;
	private RadioButton Philadelphia;
	private RadioButton Phoenix;
	private RadioButton Boston;
	private RadioButton Denver;
	private RadioButton WashingtonDC;
	private RadioButton Miami;
	private RadioButton Indianapolis;
	private RadioButton Columbus;
	private RadioButton Memphis;
	private RadioButton OklahomaCity;
	private RadioButton LasVegas;
	private RadioButton Charlotte;
	private int MajorCitiesDialog = 1;
	private double lat, lon;
	private boolean switchLocation = false;
	
	//Probably will remove these buttons
	Button Plot, Clear, Send;
	TextView tView;
	
	
	private static final int tapNum = 2;
	
	Location location;
	
	boolean canPlot;
	boolean canPlotU;
	private boolean viewOnly;
	private boolean userPlotted = false;
	
	protected LocationManager locationManager;
	
	public ArrayList<MarkerPlus> markers = new ArrayList<MarkerPlus>();
	private static ArrayList<MarkerPlus> incomingMarkers = new ArrayList<MarkerPlus>();
	
	Coordinates currentLocation;
	private static MarkerPlus gpCurrentLocation;
	
	private XMPPSender xmppSender;
	
	Animation animation;
	
	//Google Maps Variables
	public GoogleMap map;
	private static View view;
	
	
	
	/**
	 * When the Fragment View is created, this is called
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		getActivity().getActionBar().show();
		if(view != null){
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null){
				parent.removeView(view);
			}
		}
		try{
			view = inflater.inflate(R.layout.guardian_angel_googlemap_viewer, container, false);
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
		System.out.println("Started!");
		setHasOptionsMenu(true);
		MapFragment mapfrag = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.guardian_angel_googlemap));
		map = mapfrag.getMap();
		
		//Set the Maps listeners
		map.setOnMapLongClickListener(this);
		map.setInfoWindowAdapter(this);
		map.setOnCameraChangeListener(this);
		map.setOnMapClickListener(this);
		map.setOnMarkerClickListener(this);
        map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(this);
        
        animation = new AlphaAnimation(1, (float) .5);
        
        animation.setDuration(750);
        animation.setInterpolator(new LinearInterpolator());
        
        animation.setRepeatCount(Animation.INFINITE);
        
        animation.setRepeatMode(Animation.REVERSE);
        
        Plot = (Button) getActivity().findViewById(R.id.Plot);
        Clear = (Button) getActivity().findViewById(R.id.Clear);
        Send = (Button) getActivity().findViewById(R.id.Send);
        tView = (TextView) getActivity().findViewById(R.id.ga_map_text);
        tView.setText("");
        tView.setTextColor(Color.BLACK);
        tView.setBackgroundColor(Color.BLACK);
        tView.setBackground(this.getResources().getDrawable(R.drawable.guardian_angel_background));
        
        canPlotU = false;
        
        Clear.setEnabled(false);
        Send.setEnabled(false);
        
		locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


		Plot.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// user plots the first point for the UAV
				Plot.setEnabled(false);
				canPlot = true;

				tView.setText("First plot the location of the UAV.");

				Plot.clearAnimation();

			}
		});

		Clear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Clears the array storing the coordinates and wipes the map
				// clean of overlays

				Clear.setEnabled(false);
				Plot.setEnabled(true);
				canPlot = false;
				Send.setEnabled(false);

				map.clear();
				markers.clear();

				Canvas canvas = new Canvas();

				Clear.setEnabled(false);
				Plot.setEnabled(false);

				canPlotU = true;

				tView.setText("Please plot your current location.");

				tView.startAnimation(animation);
				Plot.clearAnimation();

			}
		});

		Send.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				canPlot = false;

				// Makes key visible (first image)
				Send.setEnabled(false);
				Clear.setEnabled(false);
				//geoPointToString();
				Date date = new Date();
				String dateString = date.toGMTString();
				// send the locations of the user and their points to the server
				//TODO need to edit this piece of code.
//				xmppSender = new XMPPSender(
//						NotificationService.latLongElvString,
//						NotificationService.geoCodeString, dateString,
//						geoPointStrings);
//				xmppSender.createMessage();
//				xmppSender.sendMessage();

				//TODO reimplement this part of the code.
//				Intent nextScreen = new Intent(getApplicationContext(),
//						CameraRecord.class);
//				nextScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(nextScreen);
//				finish();

			}
		});
        
        
	}
	
	public void zoomToCity(double lat, double lon) {
		Drawable offlineMapDrawable = this.getResources().getDrawable(
				R.drawable.placeholder);
		if(switchLocation)
		{	
			map.clear();
		}
		map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(new LatLng(lat, lon), (float) 6.0)));
		switchLocation = true;
	}
	
	/*  
	 * The menu opens up a dialog that list the US major cities
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Setup the MajorCitiesDialog interface elements
		
		FragmentManager fragMgr = getFragmentManager();
		dialog = new MajorCitiesDialogFragment(this);
		dialog.show(fragMgr, "MAJOR_CITIES");
		return true;
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.map_menu, menu);
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
	public void onMapLongClick(LatLng point) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
}
