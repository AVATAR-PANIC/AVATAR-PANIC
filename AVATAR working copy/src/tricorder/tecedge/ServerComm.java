package tricorder.tecedge;

import gupta.ashutosh.avatar.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * 
 * @author Tricorder Team This Class is the class that deals with the formating
 *         of the upload page and the data upload to the database
 * 
 */
public class ServerComm extends Activity implements OnItemSelectedListener {

	// instance variables
	private static final int CAMERA_PIC_REQUEST = 2500;

	TextView tv, tv2, sensorDate, sensorGPS, phoneID, textTest;
	static TextView sensorData;
	static EditText ev1, comments, projectName;
	Button but, bUpload, bChoose, bTake;
	ImageView iv;
	String toasty, something, format, data1;
	static String listViewStr, android_id, dbFormat;
	static PHPScriptQuery test;
	LocationManager lm;
	Location location;
	static float longitude, latitude;
	AlertDialog.Builder builder;
	Intent cameraIntent;
	static Bitmap bitmap;
	InputStream is;
	static Spinner spin, timeSpin;
	static ArrayList<String> data, timeArr;
	static boolean running = false;
	SimpleDateFormat s;
	ScrollView sv;

	// bluetooth variables
	private static final String TAG = "BluetoothChat";
	private static final boolean D = true;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	private static final byte TARGET_PIN_2 = 0x2;
	private static final byte WIND = 'b';
	private static final char VALUE_ON = 's';
	private static final char VALUE_OFF = 's';

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;
	private static final int REQ_CODE_PICK_IMAGE = 69;
	private String mConnectedDeviceName = null;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private BluetoothChatService mChatService = null;
	ImageView imageview;

	/**
	 * @param Bundle
	 *            savedInstanceState this is the oncreate this all happens when
	 *            the activity is called
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// makes it full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.tricorder_servercomm);
		// log into db from here
		test = new PHPScriptQuery();
		// get unique phone id
		android_id = Secure.getString(getBaseContext().getContentResolver(),
				Secure.ANDROID_ID);
		builder = new AlertDialog.Builder(this);
		data = new ArrayList<String>();
		timeArr = new ArrayList<String>();
		// checks if gps is on or not
		checkGPS();
		// adds to the data and time arrays
		data.add("0");
		timeArr.add("0");
		// instantiates the camera intent
		cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		// initializes all the widgets
		tv = (TextView) findViewById(R.id.tvSensorType);
		tv2 = (TextView) findViewById(R.id.tvProjectName);
		sensorData = (TextView) findViewById(R.id.textViewData);
		sensorDate = (TextView) findViewById(R.id.textViewDate);
		sensorGPS = (TextView) findViewById(R.id.textViewGPS);
		spin = (Spinner) findViewById(R.id.spinner);
		timeSpin = (Spinner) findViewById(R.id.spinner1);
		comments = (EditText) findViewById(R.id.editText1);
		but = (Button) findViewById(R.id.button1);
		iv = (ImageView) findViewById(R.id.ivPic);
		bUpload = (Button) findViewById(R.id.bPicUpload);
		projectName = (EditText) findViewById(R.id.etProjectName);
		sv = (ScrollView) findViewById(R.id.Scroll);

		sv.smoothScrollTo(0, 0);
		// update GPS text view
		sensorGPS.setText("Latitude: " + latitude + " Longitude: " + longitude);

		// data.add(0, "14.5");
		// timeArr.add(0, "120803142687");

		s = new SimpleDateFormat("yyMMddhhmmss");
		// database time format
		dbFormat = s.format(new Date());
		// format for the time entered into the phone
		SimpleDateFormat a = new SimpleDateFormat("MM/dd/yyyy hh:mm.ss");
		format = a.format(new Date());
		sensorDate.setText(format);
		but.setOnClickListener(new View.OnClickListener() { // button action
			// listener

			public void onClick(View arg0) { // TODO Auto-generated method stub

				if (spin.getSelectedItemPosition() == 0) {
					Toast myToast = Toast.makeText(ServerComm.this,
							"Choose a Sensor", Toast.LENGTH_SHORT);
					myToast.show();
				} else {
					Intent i = new Intent("tricorder.tecedge.LOCALGRAPHS");
					i.putExtra("PROJECT", projectName.getText().toString());
					running = true;
					startActivity(i);
				}

			}
		});
		// sets up the upload button for the camera
		bUpload.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				AlertDialog dialog = new AlertDialog.Builder(ServerComm.this)
						.create();

				dialog.setTitle("Choose Picture");
				dialog.setCancelable(true);
				dialog.setButton("Take Photo",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								startActivityForResult(cameraIntent,
										CAMERA_PIC_REQUEST);
							}
						});

				dialog.setButton2("Select Photo",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								Intent intent = new Intent();
								intent.setType("image/*");
								intent.setAction(Intent.ACTION_GET_CONTENT);
								startActivityForResult(Intent.createChooser(
										intent, "Select Picture"),
										REQ_CODE_PICK_IMAGE);
							}
						});
				dialog.show();
			}
		});

		spin.setOnItemSelectedListener(this);
		timeSpin.setOnItemSelectedListener(this);
	}

	/**
	 * uploads the entire array to the database
	 * 
	 * @param arr
	 *            the data array
	 * @param time
	 *            the time stamp for each data point
	 */
	public void uploadArray(ArrayList<String> arr, ArrayList<String> time) {
		for (int i = 0; i < arr.size(); i++) {

			// call SQL commands from this line
			something = test
					.execute("INSERT INTO sensors1 (sensor_type, sensor_data, sensor_date, sensor_gpsLat,sensor_gpsLong,phone_id )"
							+ "VALUES ('"
							+ spin.getItemAtPosition(spin
									.getSelectedItemPosition())
							+ "', '"
							+ arr.get(i)
							+ "','"
							+ time.get(i)
							+ "', "
							+ latitude
							+ ","
							+ longitude
							+ " ,'"
							+ android_id
							+ "');");
		}

		// checks if there is an error or not using the echo from the
		// php script
		Toast myToast;
		if (!something.contains("error")) {
			myToast = Toast.makeText(ServerComm.this, "Upload Complete",
					Toast.LENGTH_SHORT);
		} else {
			myToast = Toast.makeText(ServerComm.this, "Upload Error",
					Toast.LENGTH_SHORT);
		} // creates the upload toast

		myToast.show();
	}

	/**
	 * static upload method that can be called from a separate class
	 * 
	 * @return String whether or not it is an error
	 */
	public static String upload_data() {
		String something = "";
		for (int i = 0; i < data.size() - 1; i++) {

			// call SQL commands from this line
			something = test
					.execute("INSERT INTO sensors1 (sensor_type, sensor_data, sensor_date, sensor_gpsLat,sensor_gpsLong,phone_id, project_name )"
							+ "VALUES ('"
							+ listViewStr // extras.getString("SENSOR")
							+ "', '"
							+ data.get(i)
							+ "','"
							+ timeArr.get(i)
							+ "', " + latitude // extras.getString("LAT")
							+ "," + longitude // extras.getString("LONG")
							+ " ,'" + android_id // extras.getString("PHONE_ID")
							+ "','" + projectName.getText().toString() // extras.getString("PROJECT")
							+ "');");
		}
		return something;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.e(TAG, "++ ACTIVITY RESULT ++");

		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_SECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, true);
			}
			break;
		case REQUEST_CONNECT_DEVICE_INSECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, false);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
			break;

		case CAMERA_PIC_REQUEST:
			bitmap = (Bitmap) data.getExtras().get("data");
			imageview = (ImageView) findViewById(R.id.ivPic);
			bitmap.setDensity(100);
			imageview.setImageBitmap(bitmap);
			break;

		case REQ_CODE_PICK_IMAGE:
			Uri targetUri = data.getData();

			try {
				bitmap = BitmapFactory.decodeStream(getContentResolver()
						.openInputStream(targetUri));
				imageview = (ImageView) findViewById(R.id.ivPic);
				bitmap.setDensity(1000);
				imageview.setImageBitmap(bitmap);
			} catch (Exception e) {
			}
			break;
		}

	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Your GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(

							final DialogInterface dialog,

							final int id) {
								startActivity(new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,

					final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	public void checkGPS() {
		try {
			lm = (LocationManager) getSystemService(LOCATION_SERVICE);
			if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
					location = lm
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					longitude = (float) location.getLongitude();
					latitude = (float) location.getLatitude();
					Log.e("string", "" + longitude);
				} else {
					buildAlertMessageNoGps();
				}
			} else {
				lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

				location = lm
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				longitude = (float) location.getLongitude();
				latitude = (float) location.getLatitude();
			}

		} catch (Exception e) {
			if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				location = lm
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				longitude = (float) location.getLongitude();
				latitude = (float) location.getLatitude();
				Log.e("string", "" + longitude);
			} else {
				buildAlertMessageNoGps();
			}
		}

		finally {
		}

	}

	public static void create_password(String password) {
		Log.d("Test", "Confirmed is true");

		// call SQL commands from this line
		String something = test
				.execute("INSERT INTO Passwords (project_name, password, comments)"
						+ "VALUES ('"
						+ projectName.getText()
						+ "', '"
						+ password + "','" + comments.getText() + "');");

		// checks if there is an error or not using the echo from the // php
		// script
		// upload_data();
		String toasty;
		if (!something.contains("error")) {
			toasty = "Upload Complete";
		} else {
			toasty = "Upload Error";
		}

	}

	public void send(String s) {
		sendMessage(s);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			if (D)
				Log.e(TAG, "++ IF STATEMENT ++");

			// Intent enableIntent = new Intent(
			// BluetoothAdapter.ACTION_REQUEST_ENABLE);
			// startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (D)
				Log.e(TAG, "++ ELSE STATEMENT ++");
			if (mChatService == null)
				setupChat();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");
		checkGPS();
		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}
	}

	private void setupChat() {
		Log.d(TAG, "setupChat()");

		// findViewById(R.layout.servercomm).setVisibility(View.GONE);
		// setContentView(R.layout.collect_data);

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothChatService(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
	}

	@Override
	public synchronized void onPause() {
		super.onPause();

		if (D)
			Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();

		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */

	private void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);
			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);
		}
	}

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				// add to array here
				break;
			case MESSAGE_READ: // use this
				String readMessage = (String) msg.obj;
				if (msg.arg1 > 0) {
					data1 += readMessage;
					if (data1.contains("^")) {
						String[] value = data1.split("\\^");
						sensorData.setText(value[0]);
						if (running) {
							data.add(0, value[0]);
							timeArr.add(0, s.format(new Date()));
							LocalGraphs.adapterDate.notifyDataSetChanged();
							LocalGraphs.adapterTime.notifyDataSetChanged();
							LocalGraphs.listViewData.scrollTo(0, 0);
							LocalGraphs.listViewTime.scrollTo(0, 0);
						}

						data1 = "";
					}
				}
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	private void connectDevice(Intent data, boolean secure) {
		// Get the device MAC address
		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device, secure);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent = null;
		switch (item.getItemId()) {
		case R.id.secure_connect_scan:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
			return true;
		case R.id.insecure_connect_scan:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent,
					REQUEST_CONNECT_DEVICE_INSECURE);
			return true;
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		}
		return false;
	}

	public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
			long arg3) {
		// TODO Auto-generated method stub

		switch (arg0.getId()) {
		case R.id.spinner:
			if (pos == 0) {
				// sendMessage("s");
				listViewStr = "Select Sensor";
			} else if (pos == 1) {// h
				sendMessage("h");
				listViewStr = "Thermometer";
			} else if (pos == 2) {// L
				sendMessage("L");
				listViewStr = "Luminosity";
			} else if (pos == 3) {// w
				sendMessage("w");
				listViewStr = "Wind";
			} else if (pos == 4) {// B
				sendMessage("B");
				listViewStr = "Barometric Pressure";
			} else if (pos == 5) {// H
				sendMessage("H");
				listViewStr = "Humidity";
			} else if (pos == 6) {// r
				sendMessage("r");
				listViewStr = "Radiation";
			} else if (pos == 7) {// C
				sendMessage("C");
				listViewStr = "Carbon Monoxide";
			} else if (pos == 8) {// O
				sendMessage("O");
				listViewStr = "Carbon Dioxide";
			} else if (pos == 9) {// m
				sendMessage("m");
				listViewStr = "Methane";
			} else if (pos == 10) {// e
				sendMessage("e");
				listViewStr = "Propane";
			} else if (pos == 11) {// a
				sendMessage("a");
				listViewStr = "Alcohol Gas";
			} else if (pos == 12) {// T
				sendMessage("T");
				listViewStr = "IR Thermometer";
			} else if (pos == 13) {// I
				sendMessage("I");
				listViewStr = "Infared Break Beam";
			} else if (pos == 14) {// P
				sendMessage("P");
				listViewStr = "Motion";
			} else if (pos == 15) {// u
				sendMessage("u");
				listViewStr = "Ultrasonic";
			} else if (pos == 16) {// P
				sendMessage("p");
				listViewStr = "Heart Beat";
			} else if (pos == 17) {// b
				sendMessage("b");
				listViewStr = "Temperature (BP Sensor)";
			} else if (pos == 18) {
				sendMessage("s");
				// mOutStringBuffer.setLength(0);
				listViewStr = "Stop";
			}
			break;

		case R.id.spinner1:
			if (pos == 0) {// t
				sendMessage("1");

			} else if (pos == 1) {// l
				sendMessage("5");

			} else if (pos == 2) {// w
				sendMessage("30");

			} else if (pos == 3) {// b
				sendMessage("60");

			} else if (pos == 4) {// h
				sendMessage("300");

			} else if (pos == 5) {
				sendMessage("600");

			} else if (pos == 6) {
				sendMessage("1800");

			} else if (pos == 7) {
				sendMessage("3600");

			} else if (pos == 8) {
				sendMessage("0.5");

			}
			break;
		}
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}