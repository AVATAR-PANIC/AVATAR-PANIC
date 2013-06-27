package com.guardian_angel.uav_tracker;

import gupta.ashutosh.avatar.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class Map extends MapActivity {
	/** Called when the activity is first created. */

	// Declare & initialize global variables

	private ArrayList<String> geoPointStrings = new ArrayList<String>();
	
	// dialog variables
		private Dialog dialog;
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
		private int MajorCitiesDialog = 0;
		private double lat, lon;
		private boolean switchLocation = false;

	Button Plot, Clear, Send;
	TextView text;
	ImageView compass;

	MapController mapController;

	private static final int tapNum = 2;

	Location location;

	boolean canPlot;
	boolean canPlotU;
	private boolean viewOnly;
	private boolean userPlotted = false;

	private MapView mapView;
	private Projection projection;

	protected LocationManager locationManager;

	public ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
	private static ArrayList<GeoPoint> incomingUserPoints = new ArrayList<GeoPoint>();

	Coordinates currentLocation;
	private static GeoPoint gpCurrentLocation;
	private GeoPoint contiguousUSACenter = new GeoPoint(39830000,-98580000);

	CustomizedOverlay arrowOverlay;
	CustomizedOverlay offlineMapOverlay;
	CustomizedOverlay userOverlay;
	CustomizedOverlay incomingUserOverlay;

	List<Overlay> mapOverlays;

	private XMPPSender xmppSender;

	Animation animation;

	// Sets up the interfaces properties
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.guardian_angel_map);

		animation = new AlphaAnimation(1, (float) .5); // Change alpha from
														// fully visible to
														// invisible
		animation.setDuration(750); // duration - half a second
		animation.setInterpolator(new LinearInterpolator()); // do not alter
																// animation
																// rate
		animation.setRepeatCount(Animation.INFINITE); // Repeat animation
														// infinitely
		animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
													// end so the button will
													// fade back in
		
		// initialize the buttons
		Plot = (Button) findViewById(R.id.Plot);
		Clear = (Button) findViewById(R.id.Clear);
		Send = (Button) findViewById(R.id.Send);
		compass = (ImageView) findViewById(R.id.compass);
		text = (TextView) findViewById(R.id.text);
		text.setText("");
		text.setTextColor(Color.BLACK);
		text.setBackgroundColor(Color.BLACK);
		text.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.background));
		
		canPlotU = false;
		Bundle extras = getIntent().getExtras();
		viewOnly = extras.getBoolean("viewOnly");
		

		if(viewOnly)
		{
			// hide the buttons if only viewing
			Plot.setVisibility(Button.INVISIBLE);
			Clear.setVisibility(Button.INVISIBLE);
			Send.setVisibility(Button.INVISIBLE);
			text.setVisibility(TextView.INVISIBLE);
			if(userPlotted)
			{
				OverlayItem previouslyPlottedUser = new OverlayItem(gpCurrentLocation,"","");
				userOverlay.addOverlay(previouslyPlottedUser);
				mapOverlays.add(userOverlay);
			}
		}

		Clear.setEnabled(false);
		Send.setEnabled(false);

		// This Code Disables Zooming - the abouve code enable it.
		mapView = (MapView) findViewById(R.id.map_view);
		mapView.setBuiltInZoomControls(false);

		// Below code draws the offline map image.
		mapOverlays = mapView.getOverlays();
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mMobile = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		mapController = mapView.getController();

		// what to do if there is no GPS signal
		if (!mWifi.isConnected() && !mMobile.isConnected()) {
			Drawable offlineMapDrawable = this.getResources().getDrawable(
					R.drawable.offlinemap);
			offlineMapOverlay = new CustomizedOverlay(offlineMapDrawable, this);
			
			// zooms the user to this location (Dayton, OH area)
			int lat = (int) (39.77174 * 1000000);
			int lon = (int) (-84.10841 * 1000000);

			GeoPoint defaultUserGeoPoint = new GeoPoint(lat, lon);
			OverlayItem defaultUserOverlayItem = new OverlayItem(
					defaultUserGeoPoint, "", "");

			offlineMapOverlay.addOverlay(defaultUserOverlayItem);
			mapOverlays.add(offlineMapOverlay);

			mapView.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					if (event.getPointerCount() > 1) {
						return true;
					}
					return false;
				}
			});

			mapController.animateTo(defaultUserGeoPoint);
			mapController.setZoom(15);
		}

		// initialize variables used for the overlays
		mapView.setSatellite(true);
		Drawable arrowDrawable = this.getResources().getDrawable(
				R.drawable.arrow);
		arrowOverlay = new CustomizedOverlay(arrowDrawable, this);
		Drawable userDrawable = this.getResources().getDrawable(
				R.drawable.phoneuser);
		userOverlay = new CustomizedOverlay(userDrawable, this);
		projection = mapView.getProjection();
		final Canvas canvas = new Canvas();
		MyOverlay mainOverlay = new MyOverlay(0, 0, 0, 0);
		mainOverlay.draw(canvas, mapView, false);
		mapOverlays.add(mainOverlay);
		Drawable incomingUserDrawable = this.getResources().getDrawable(
				R.drawable.otheruser);
		incomingUserOverlay = new CustomizedOverlay(incomingUserDrawable, this);
		
		// displays other users locations that have been notified
		if (incomingUserPoints.size() > 0) {
			for (int i = 0; i < incomingUserPoints.size(); i++) {
				OverlayItem incomingUserOverlayItem = new OverlayItem(
						incomingUserPoints.get(i), "", "");
				incomingUserOverlay.addOverlay(incomingUserOverlayItem);
				
			}
			mapOverlays.add(incomingUserOverlay);
		}

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, new MyLocationListener());

		// Plot & Zoom into Current Location when the app starts
		currentLocation = showCurrentLocation();
		if (gpCurrentLocation != null)
		{
			// if there is a GPS signal an icon is added at the user's location
			OverlayItem remapOldUserItem = new OverlayItem(gpCurrentLocation,"","");
			userOverlay.addOverlay(remapOldUserItem);
			mapOverlays.add(userOverlay);
			mapController.animateTo(gpCurrentLocation);
			mapController.setZoom(15);
		}
		else if (currentLocation == null || NotificationService.latLongElvString.contains("0.0")) {
			// if there is no GPS signal then the user plots their loaction
			text.setText("No GPS signal. Please tap at your location to zoom.");
			text.startAnimation(animation);
			Send.setEnabled(false);
			Plot.setEnabled(false);
			Clear.setEnabled(false);
			mapController.animateTo(contiguousUSACenter);
			mapController.setZoom(5);
		

			canPlotU = true;
		} else {

			// Below (3 changed to 4)
			GeoPoint gpsDefinedGeoPoint = new GeoPoint(
					(int) (currentLocation.getLad() * 1000000),
					(int) (currentLocation.getLong() * 1000000));
			OverlayItem overlayitem4 = new OverlayItem(gpsDefinedGeoPoint, "",
					"");

			userOverlay.addOverlay(overlayitem4);
			mapOverlays.add(userOverlay);

			gpCurrentLocation = gpsDefinedGeoPoint;

			text.setText("Your current location: \nLatitude: "
					+ gpCurrentLocation.getLatitudeE6() / 1000000.0
					+ "\nLongitude: " + gpCurrentLocation.getLongitudeE6()
					/ 1000000.0);

			mapController.animateTo(gpCurrentLocation);
			mapController.setZoom(15);

			Plot.startAnimation(animation);
			userPlotted = true;

		}

		Plot.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// user plots the first point for the UAV
				Plot.setEnabled(false);
				canPlot = true;

				text.setText("First plot the location of the UAV.");

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

				mapOverlays.clear();
				geoPoints.clear();

				mapView.invalidate();

				arrowOverlay.clear();
				userOverlay.clear();

				Canvas canvas = new Canvas();
				
				// redraw the overlay
				MyOverlay drawOverlay = new MyOverlay(0, 0, 0, 0);
				drawOverlay.draw(canvas, mapView, false);
				mapOverlays.add(drawOverlay);

				Clear.setEnabled(false);
				Plot.setEnabled(false);

				canPlotU = true;

				text.setText("Please plot your current location.");

				text.startAnimation(animation);
				Plot.clearAnimation();

			}
		});

		Send.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				// clears Arrows
				//mapOverlays.clear();
				arrowOverlay.clear();
				//userOverlay.clear();
				mapView.invalidate();
				canPlot = false;

				// Makes key visible (first image)
				Send.setEnabled(false);
				Clear.setEnabled(false);
				geoPointToString();
				Date date = new Date();
				String dateString = date.toGMTString();
				// send the locations of the user and their points to the server
				xmppSender = new XMPPSender(
						NotificationService.latLongElvString,
						NotificationService.geoCodeString, dateString,
						geoPointStrings);
				xmppSender.createMessage();
				xmppSender.sendMessage();

				Intent nextScreen = new Intent(getApplicationContext(),
						CameraRecord.class);
				nextScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(nextScreen);
				finish();

			}
		});

	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		finish();
	}
	
	/*
	 * Creates a menu to be displayed when the settings button is touched.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.guardian_angel_map_menu, menu);
		return true;
	}
	//TODO create Dialog Fragment.
	/*  
	 * The menu opens up a dialog that list the US major cities
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Setup the MajorCitiesDialog interface elements
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.guardian_angel_major_cities);
		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		dialog.setTitle("Major Cities:");
		showDialog(MajorCitiesDialog);
		return true;
	}
	
	/*
	 * Creates the cities that are located in the dialog and adds
	 * functionality to them
	 * 
	 * Precondtion: Requires a dialog
	 * Postcondition: A new location is stored and the user is zoomed
	 * to its position on the map
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		// Setup the MajorCitiesDialog interface elements
		dialog.setContentView(R.layout.guardian_angel_major_cities);
		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		dialog.setTitle("Major Cities:");

		this.ok = (Button) dialog.findViewById(R.id.major_cities_ok);
		this.cancel = (Button) dialog.findViewById(R.id.major_cities_cancel);
		// initialize and instantiate variables
		this.NewYorkCity = (RadioButton) dialog.findViewById(R.id.NewYorkCity);
		this.Chicago = (RadioButton) dialog.findViewById(R.id.Chicago);
		this.Houston = (RadioButton) dialog.findViewById(R.id.Houston);
		this.Philadelphia = (RadioButton) dialog.findViewById(R.id.Philadelphia);
		this.Phoenix = (RadioButton) dialog.findViewById(R.id.Phoenix);
		this.Boston = (RadioButton) dialog.findViewById(R.id.Boston);
		this.Denver = (RadioButton) dialog.findViewById(R.id.Denver);
		this.WashingtonDC = (RadioButton) dialog.findViewById(R.id.WashingtonDC);
		this.Miami = (RadioButton) dialog.findViewById(R.id.Miami);
		this.Indianapolis = (RadioButton) dialog.findViewById(R.id.Indianapolis);
		this.Columbus = (RadioButton) dialog.findViewById(R.id.Columbus);
		this.Memphis = (RadioButton) dialog.findViewById(R.id.Memphis);
		this.OklahomaCity = (RadioButton) dialog.findViewById(R.id.OklahomaCity);
		this.LasVegas = (RadioButton) dialog.findViewById(R.id.LasVegas);
		this.Charlotte = (RadioButton) dialog.findViewById(R.id.Charlotte);

		// City RadioButton listeners
		NewYorkCity.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (NewYorkCity.isChecked()) {
					lat = 40.713739;
					lon = -74.005398;
				}
			}
		});
		Chicago.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Chicago.isChecked()) {
					lat = 41.875371;
					lon = -87.625314;
				}
			}
		});
		Houston.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Houston.isChecked()) {
					lat = 29.756547;
					lon = -95.36285;
				}
			}
		});
		Philadelphia.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Philadelphia.isChecked()) {
					lat = 39.946786;
					lon = -75.16862;
				}
			}
		});
		Phoenix.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Phoenix.isChecked()) {
					lat = 33.442321;
					lon = -112.071263;
				}
			}
		});
		Boston.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Boston.isChecked()) {
					lat = 42.348232;
					lon = -71.063412;
				}
			}
		});
		Denver.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Denver.isChecked()) {
					lat = 39.735568;
					lon = -104.985699;
				}
			}
		});
		WashingtonDC.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (WashingtonDC.isChecked()) {
					lat = 38.894734;
					lon = -77.036385;
				}
			}
		});
		Miami.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Miami.isChecked()) {
					lat = 25.784622;
					lon = -80.227742;
				}
			}
		});
		Indianapolis.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Indianapolis.isChecked()) {
					lat = 39.759292;
					lon = -86.164797;
				}
			}
		});
		Columbus.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Columbus.isChecked()) {
					lat = 39.958531;
					lon = -82.997965;
				}
			}
		});
		Memphis.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Memphis.isChecked()) {
					lat = 35.147173;
					lon = -90.048475;
				}
			}
		});
		OklahomaCity.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (OklahomaCity.isChecked()) {
					lat = 35.4672;
					lon = -97.515899;
				}
			}
		});
		LasVegas.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (LasVegas.isChecked()) {
					lat = 36.112631;
					lon = -115.173062;
				}
			}
		});
		Charlotte.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Charlotte.isChecked()) {
					lat = 35.225808;
					lon = -80.845206;
				}
			}
		});
		
		// Set the ok button listener
				ok.setOnClickListener(new OnClickListener() {
					// Called when the ok button is pressed
					public void onClick(View v) {
						zoomToCity();
						removeDialog(MajorCitiesDialog);
					}
				});
				// Set the cancel button listener
				cancel.setOnClickListener(new OnClickListener() {
					// Called when the Cancel button is pressed
					public void onClick(View v) {
						try {
							removeDialog(MajorCitiesDialog);
						} catch (Exception ex) {
							ex.printStackTrace();
						}

					}
				});
				// Set the back function listener
				dialog.setOnCancelListener(new OnCancelListener() {
					// Called when the Back button is pressed
					public void onCancel(DialogInterface dialog) {
						try {
							removeDialog(MajorCitiesDialog);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				});

				return dialog;

			}

	// Overlay Class for Drawing the Line Connecting the points
	private class MyOverlay extends Overlay {

		int gx1, gx2, gy1, gy2;

		public MyOverlay(int x1, int y1, int x2, int y2) {

			gx1 = x1;
			gx2 = x2;
			gy1 = y1;
			gy2 = y2;
		}
		
		
		/*
		 * Draws the map view on the screen with the geopoint
		 * overlays
		 */
		public void draw(Canvas canvas, MapView mapv, boolean shadow) {
			super.draw(canvas, mapv, shadow);

			Paint mPaint = new Paint();
			mPaint.setDither(true);

			mPaint.setColor(Color.BLACK);

			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(3);

			GeoPoint gP1 = new GeoPoint(gx1, gy1);
			GeoPoint gP2 = new GeoPoint(gx2, gy2);

			Point p1 = new Point();
			Point p2 = new Point();
			Path path = new Path();

			projection.toPixels(gP1, p1);
			projection.toPixels(gP2, p2);

			path.moveTo(p2.x, p2.y);
			path.lineTo(p1.x, p1.y);

			canvas.drawPath(path, mPaint);
		}

		/*
		 * Draws a geopoint at the location where the user
		 * touches the screen
		 */
		@Override
		public boolean onTap(GeoPoint point, MapView mapView) {
			// Takes the coordinates of the spot tapped, adds them to an array,
			// and displays the point on the screen

			if (canPlot && geoPoints.size() < tapNum) {

				GeoPoint usersPlot = point;
				OverlayItem overlayitem3 = new OverlayItem(usersPlot, "", "");

				arrowOverlay.addOverlay(overlayitem3);
				mapOverlays.add(arrowOverlay);

				geoPoints.add(usersPlot);

				text.setText("Now tap in the direction the UAV is going.");

			}

			if (geoPoints.size() == tapNum) {
				Send.startAnimation(animation);
				Send.setEnabled(true);
				text.setText("Data can now be sent.");
			}

			// enables clear and send buttons once you've begun plotting
			if (geoPoints.size() > 0) {
				Clear.setEnabled(true);
			}

			// This segment of code is for the user to plot his position if
			// he/she has no GPS signal.
			if (canPlotU) {

				Geocoder mGeoCoder = new Geocoder(getApplicationContext());
				List<Address> myList = null;
				GeoPoint usersGeoPoint = point;
				String street = "";
				String city = "";
				String state = "";
				OverlayItem userOverlayItem = new OverlayItem(usersGeoPoint, "", "Your current location");

				gpCurrentLocation = usersGeoPoint;

				text.setText("You are located at\nLatitude: "
						+ usersGeoPoint.getLatitudeE6() / 1000000.0 + "\nLongitude: "
						+ usersGeoPoint.getLongitudeE6() / 1000000.0);
				
				// set the location for the notification service
				NotificationService.userLat = usersGeoPoint.getLatitudeE6() / 1000000.0;
				NotificationService.userLng = usersGeoPoint.getLongitudeE6() / 1000000.0;

				NotificationService.latLongElvString = "Lat=\""
						+ NotificationService.userLat + "\" Lng=\""
						+ NotificationService.userLng + " \" Elv=\"0.0\"";

				try {
					myList = mGeoCoder.getFromLocation(
							NotificationService.userLat,
							NotificationService.userLng, 5);
					street = (myList.get(0)).getThoroughfare();
					city = (myList.get(0)).getLocality();
					state = (myList.get(0)).getAdminArea();
					NotificationService.geoCodeString = "Str=\"" + street
							+ "\" Cty=\"" + city + "\" Ste=\"" + state + "\"";
				} catch (Exception e) {
					e.printStackTrace();
					NotificationService.geoCodeString = "Str=\"null\" Cty=\"null\" Ste=\"null\"";
				}

				text.clearAnimation();
				Plot.startAnimation(animation);

				canPlotU = false;

				Plot.setEnabled(true);

				Clear.setEnabled(true);

				userOverlay.addOverlay(userOverlayItem);
				mapOverlays.add(userOverlay);
				mapController.animateTo(usersGeoPoint);
				mapController.setZoom(15);
				
				userPlotted = true;

			}

			return true;
		}

	}

	/* 
	 * Draws wide red line (Flight Path / with error) and 
	 * creates the overlays that are displayed
	 */
	class MyOverlayOval extends Overlay {

		int gx1, gx2, gy1, gy2;
		ArrayList<Double> array;

		// constructor for the overlays: requires a position
		public MyOverlayOval(int x1, int y1, int x2, int y2, ArrayList<Double> a) {

			gx1 = x1;
			gx2 = x2;
			gy1 = y1;
			gy2 = y2;
			array = a;
		}

		/* 
		 * Draws wide red line (Flight Path / with error) and 
		 * creates the overlays that are displayed
		 */
		public void draw(Canvas canvas, MapView mapv, boolean shadow) {
			super.draw(canvas, mapv, shadow);

			Paint mPaint = new Paint();
			mPaint.setDither(true);

			mPaint.setColor(Color.RED);

			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);

			double sum = 0;
			double ave;
			for (int i = 0; i < array.size(); i++) {
				sum += array.get(i);
			}
			ave = sum / array.size();

			// changes size of line based on distance from the points you are
			// plotting
			if (ave < 1) {
				mPaint.setStrokeWidth(20);
			}
			if (ave > 1 && ave < 3) {
				mPaint.setStrokeWidth(40);
			}
			if (ave > 3) {
				mPaint.setStrokeWidth(60);
			}

			mPaint.setAlpha(100);

			GeoPoint gP1 = new GeoPoint(gx1, gy1);
			GeoPoint gP2 = new GeoPoint(gx2, gy2);

			Point p1 = new Point();
			Point p2 = new Point();
			Path path = new Path();

			projection.toPixels(gP1, p1);
			projection.toPixels(gP2, p2);

			path.moveTo(p2.x, p2.y);
			path.lineTo(p1.x, p1.y);

			canvas.drawPath(path, mPaint);
		}

	}

	/*
	 * Creates a constructor that stores the latitude and longitude
	 * of the user as coordinates
	 */
	public class Coordinates {
		private double lad;
		private double lon;

		Coordinates(double longitude, double latitude) {
			lon = longitude;
			lad = latitude;
		}

		public double getLong() {
			return lon;
		}

		public double getLad() {
			return lad;
		}

	}

	/*
	 * Gets the current location of the user and returns the coordinates
	 * 
	 * Precondition: Needs the user's location
	 * Postcondition: Returns the user's coordinate location
	 * @return coordinates
	 */
	protected Coordinates showCurrentLocation() {
		// Get the current location of the user
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String provider = locationManager.getBestProvider(criteria, true);
		location = locationManager.getLastKnownLocation(provider);

		// Get the coordinates of the user based on the location
		if (location != null) {
			Coordinates coord = new Coordinates(location.getLongitude(),
					location.getLatitude());
			return coord;
		} else {
			return null;
		}

	}

	/*
	 * Listener that listens to the GPS to see if the user's
	 * location has changed and if it has then the location
	 * of the user is updated.
	 */
	public class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {

			currentLocation = showCurrentLocation();

			GeoPoint newUserGeoPoint = new GeoPoint(
					(int) (currentLocation.getLad() * 1000000),
					(int) (currentLocation.getLong() * 1000000));
			OverlayItem newGeoPointItem = new OverlayItem(newUserGeoPoint, "", "");

			userOverlay.addOverlay(newGeoPointItem);
			mapOverlays.add(userOverlay);

			gpCurrentLocation = newUserGeoPoint;

			text.setText("Latitude: " + gpCurrentLocation.getLatitudeE6()
					/ 1000000.0 + "\nLongitude: "
					+ gpCurrentLocation.getLongitudeE6() / 1000000.0);

			//mapController.animateTo(gpCurrentLocation);

		}

		public void onStatusChanged(String s, int i, Bundle b) {

		}

		public void onProviderDisabled(String s) {

		}

		public void onProviderEnabled(String s) {

		}

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/*
	 * Converts the latitude and longitude of the geopints into
	 * a string so that it can be sent to the XMPP server
	 */
	public void geoPointToString() {
		double lat;
		double lng;
		String latString;
		String lngString;

		for (int i = 0; i < geoPoints.size(); i++) {

			lat = geoPoints.get(i).getLatitudeE6() / 1000000.0;
			lng = geoPoints.get(i).getLongitudeE6() / 1000000.0;

			latString = "" + lat;
			lngString = "" + lng;

			geoPointStrings.add(lngString);
			geoPointStrings.add(latString);

		}
	}
	
	/*
	 * Creates a geopoint at the location of the user based on
	 * a given latitude and longitude. Zooms to this new geopoint.
	 */
	public static void createGeoPoint(double lat, double lng)
	{
		GeoPoint incomingUserGeoPoint = new GeoPoint(
				(int) (lat * 1000000),
				(int) (lng * 1000000));
		incomingUserPoints.add(incomingUserGeoPoint);
	}
	
	public void zoomToCity() {
		Drawable offlineMapDrawable = this.getResources().getDrawable(
				R.drawable.placeholder);
		if(switchLocation)
		{	
			mapOverlays.remove(offlineMapOverlay);
			offlineMapOverlay.clear();
			mapView.invalidate();
		}
		offlineMapOverlay = new CustomizedOverlay(offlineMapDrawable, this);
		GeoPoint offlineCity = new GeoPoint((int) (lat * 1000000.0), (int) (lon * 1000000.0));
		OverlayItem offlineOverlayItem = new OverlayItem(offlineCity, "", "");
		offlineMapOverlay.addOverlay(offlineOverlayItem);
		mapOverlays.add(offlineMapOverlay);
		mapController.animateTo(offlineCity);
		mapController.setZoom(10);
		switchLocation = true;
	}

}