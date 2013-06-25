package tricorder.tecedge;

import gupta.ashutosh.avatar.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Mapm extends MapActivity {

	public static Handler h;
	static String initializing;
	PHPScriptQuery database;
	String result;
	Boolean value;

	static MapView map;
	MapController controller;
	List<Overlay> overlayList;
	MyLocationOverlay compass;

	POIm pin;
	POIm pressurePin;
	POIm luminosityPin;
	POIm CO2Pin;
	POIm imagePin;
	POIm methanePin;
	POIm COPin;
	POIm humidityPin;
	POIm radiationPin;
	POIm tempPin;

	long start;
	long stop;
	int lati, longi;
	int x, y;
	GeoPoint touchedPoint;
	LocationManager lm;
	String towers;
	Refreshm refresh;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loadm);
		if (!loadPreferences("firsttime").equalsIgnoreCase("false")) {
			savePreferences("timefilter", "false");
			savePreferences("firsttime", "false");
			savePreferences("alltype", "true");
			savePreferences("compass", "true");
			savePreferences("ZoomButtons", "true");
			savePreferences("SatelliteView", "true");
		}
		database = new PHPScriptQuery();
		new DownloadFilesTask().execute();
		h = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					finish();
					break;
				}
			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, 1, Menu.FIRST, "Settings").setIcon(R.drawable.menusettings)
				.setIntent(new Intent("tricorder.tecedge.SETTINGS"));
		menu.add(1, 2, Menu.FIRST + 1, "Upload").setIcon(R.drawable.menuupload)
				.setIntent(new Intent("tricorder.tecedge.Displaym"));
		menu.add(1, 3, Menu.FIRST + 1, "Refresh")
				.setIcon(R.drawable.menurefresh)
				.setIntent(new Intent("tricorder.tecedge.Refresh"));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	void initMap() {
		map = (MapView) findViewById(R.id.map);
		TouchOver t = new TouchOver();
		overlayList = map.getOverlays();
		overlayList.add(t);
		controller = map.getController();
		if (loadPreferences("compass").equalsIgnoreCase("true")) {
			compass = new MyLocationOverlay(Mapm.this, map);
			overlayList.add(compass);
			compass.enableCompass();
		}
		value = Boolean.parseBoolean(loadPreferences("ZoomButtons"));
		map.setBuiltInZoomControls(value);
		value = Boolean.parseBoolean(loadPreferences("SatelliteView"));
		map.setSatellite(value);
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria crit = new Criteria();
		towers = lm.getBestProvider(crit, false);
		Location location = lm.getLastKnownLocation(towers);
		if (initializing == null) {
			if (location != null) {
				controller.setZoom(15);
				lati = (int) (location.getLatitude() * 1E6);
				longi = (int) (location.getLongitude() * 1E6);
				controller.setCenter(new GeoPoint(lati, longi));
			} else {
				controller.setZoom(5);
				controller.setCenter(new GeoPoint((int) (39.9611 * 1E6),
						(int) (-82.9989 * 1E6)));
			}
		} else {
			initializing = null;
		}
		if (initializing != null) {
			System.out.println(initializing);
		}
	}

	public void addData(ArrayList<OverlayItem> currentData) {
		if (currentData.size() == 0) {
			Toast.makeText(this, "Loading Data...Please Wait",
					Toast.LENGTH_LONG).show();
			return;
		}

		pin = new POIm(Mapm.this, getResources().getDrawable(
				R.drawable.pinsensor), "Sensor");
		CO2Pin = new POIm(Mapm.this, getResources().getDrawable(
				R.drawable.pincotwo), "CO2");
		tempPin = new POIm(Mapm.this, getResources().getDrawable(
				R.drawable.pintemp), "Temperature");
		methanePin = new POIm(Mapm.this, getResources().getDrawable(
				R.drawable.pinmethane), "Methane");
		pressurePin = new POIm(Mapm.this, getResources().getDrawable(
				R.drawable.pinpressure), "Barometric Pressure");
		COPin = new POIm(Mapm.this, getResources()
				.getDrawable(R.drawable.pinco), "CO");
		humidityPin = new POIm(Mapm.this, getResources().getDrawable(
				R.drawable.pinhumidity), "Humidity");
		radiationPin = new POIm(Mapm.this, getResources().getDrawable(
				R.drawable.pinradiation), "Radiation");
		luminosityPin = new POIm(Mapm.this, getResources().getDrawable(
				R.drawable.pinluminosity), "Luminosity");
		imagePin = new POIm(Mapm.this, getResources().getDrawable(
				R.drawable.pinimage), "Image");

		for (int i = 0; i < currentData.size(); i++) {

			OverlayItem sensorItem = currentData.get(i);
			String sensorType = sensorItem.getTitle();

			if (sensorType.equalsIgnoreCase("Temperature")) {
				tempPin.insert(sensorItem);
			} else if (sensorType.equalsIgnoreCase("CO2")) {
				CO2Pin.insert(sensorItem);
			} else if (sensorType.equalsIgnoreCase("Methane")) {
				methanePin.insert(sensorItem);
			} else if (sensorType.equalsIgnoreCase("Barometric Pressure")) {
				pressurePin.insert(sensorItem);
			} else if (sensorType.equalsIgnoreCase("CO")) {
				COPin.insert(sensorItem);
			} else if (sensorType.equalsIgnoreCase("Humidity")) {
				humidityPin.insert(sensorItem);
			} else if (sensorType.equalsIgnoreCase("Radiation")) {
				radiationPin.insert(sensorItem);
			} else if (sensorType.equalsIgnoreCase("Luminosity")) {
				luminosityPin.insert(sensorItem);
			} else if (sensorType.equalsIgnoreCase("Image")) {
				imagePin.insert(sensorItem);
			} else {
				pin.insert(sensorItem);
			}

		}
		if (pin.size() != 0)
			overlayList.add(pin);
		if (tempPin.size() != 0)
			overlayList.add(tempPin);
		if (CO2Pin.size() != 0)
			overlayList.add(CO2Pin);
		if (methanePin.size() != 0)
			overlayList.add(methanePin);
		if (COPin.size() != 0)
			overlayList.add(COPin);
		if (humidityPin.size() != 0)
			overlayList.add(humidityPin);
		if (radiationPin.size() != 0)
			overlayList.add(radiationPin);
		if (pressurePin.size() != 0)
			overlayList.add(pressurePin);
		if (luminosityPin.size() != 0)
			overlayList.add(luminosityPin);
		if (imagePin.size() != 0)
			overlayList.add(imagePin);
	}

	private String loadPreferences(String key) {
		SharedPreferences preferences = getSharedPreferences(
				Settings.PREF_FILE_NAME, MODE_PRIVATE);
		String save = preferences.getString(key, "sensor_type=''");
		return save;
	}

	private void savePreferences(String key, String value) {
		SharedPreferences preferences = getSharedPreferences(
				Settings.PREF_FILE_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public class DownloadFilesTask extends AsyncTask<Context, Integer, Integer> {

		private ArrayList<OverlayItem> serverData;

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
			//serverData = database.getMapData(condition);
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			setContentView(R.layout.tricorder_mapsm);
			initMap();
			addData(serverData);
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
				touchedPoint = map.getProjection().fromPixels(x, y);
				// getLatitudeandLongitude

			}
			if (e.getAction() == MotionEvent.ACTION_UP) {
				stop = e.getEventTime();
			}
			if (stop - start > 2000) {
				Geocoder geocoder = new Geocoder(getBaseContext(),
						Locale.getDefault());
				try {
					List<Address> address = geocoder.getFromLocation(
							touchedPoint.getLatitudeE6() / 1E6,
							touchedPoint.getLongitudeE6() / 1E6, 1);
					System.out.println(touchedPoint.getLatitudeE6() / 1E6);
					if (address.size() > 0) {
						String display = "";
						for (int z = 0; z < address.get(0)
								.getMaxAddressLineIndex(); z++) {
							display += address.get(0).getAddressLine(z) + "\n";
						}
						Toast t = Toast.makeText(getBaseContext(), display,
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