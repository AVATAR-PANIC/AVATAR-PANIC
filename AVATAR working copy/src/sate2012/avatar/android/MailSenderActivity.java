package sate2012.avatar.android;

import com.google.android.gms.maps.model.LatLng;

import gupta.ashutosh.avatar.R;
import DialogFragments.ElevationDialogFragment;
import DialogFragments.MapSettingsDialogFragment;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
import android.telephony.TelephonyManager;

public class MailSenderActivity extends Activity implements OnClickListener {
	public String subj;
	public String body;
	public String from;
	public String toList;
	public String ptName;
	public String ptDesc;
	public String ptType;
	public String ptURL;
	private String ptURL_noFTP;
	private String ptLat;
	private String ptLng;
	private String item_sep;
	private Context c;
	private Button send;
	private Button button_return;
	public int addedAlt;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		c = super.getApplicationContext();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		body = "Body.";
		ptLat = Constants.lat;
		ptLng = Constants.lng;
		addedAlt = 0;
		final LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		final LocationListener mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 2000, (float) 1.0, mlocListener);
		Intent thisIntent = getIntent();
		ptType = thisIntent.getStringExtra("Type");
		ptURL = "ftp://opensim:widdlyscuds@virtualdiscoverycenter.net/../../var/www/AVATAR/" + thisIntent.getStringExtra("Filename");
		ptURL_noFTP = "virtualdiscoverycenter.net/AVATAR/media/" + thisIntent.getStringExtra("Filename");
		ptName = thisIntent.getStringExtra("Filename");
		setContentView(R.layout.avatar_mail_prep_apv);
		setLayout(ptType);
		send = (Button) findViewById(R.id.Send);
		send.setOnClickListener(this);

		FragmentManager fragMgr;
		fragMgr = getFragmentManager();

		ElevationDialogFragment dialog = new ElevationDialogFragment();
		dialog.setParent(this);
		dialog.show(fragMgr, "EXTRA_ELEVATION");

	}

	public void onClick(View v) {
		switch (v.getId()) {
			case (R.id.Send):
				Toast.makeText(getApplicationContext(), "Sending, it may take a little while", Toast.LENGTH_LONG).show();
				try {
					InputMethodManager inputManager = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					from = "sate2012.avatar@gmail.com";
					toList = "sate2012.avatar@gmail.com";
					EditText etName = (EditText) findViewById(R.id.pointName);
					ptName = etName.getText().toString();

					System.out.println("NAME SET");
					Intent i = getIntent();
					LatLng latlng = i.getParcelableExtra("LatLng");

					UploadMedia.ElevationFinder finder = new UploadMedia.ElevationFinder();
					finder.execute(latlng.latitude, latlng.longitude);
					double elevation = finder.get();
					UploadMedia.HttpSender httpSender = new UploadMedia.HttpSender();
					System.out.println(latlng);
					System.out.println(ptName);
					if (i.getStringExtra("Filename") != null) {
						httpSender.execute(ptName, latlng.latitude + "", latlng.longitude + "", (elevation + addedAlt) + "", "http://"
								+ Constants.SERVER_FTP_ADDRESS + "/" + Constants.SERVER_SCRIPT_SUBFOLDER + "/media/" + i.getStringExtra("Filename"));
					} else {
						httpSender.execute(ptName, latlng.latitude + "", latlng.longitude + "", elevation + "",
								((EditText) findViewById(R.id.pointDesc)).getText().toString());

					}
					System.out.println("POINT UPLOADED");

					EditText etDesc = (EditText) findViewById(R.id.pointDesc);
					ptDesc = etDesc.getText().toString();
					item_sep = getResources().getString(R.string.item_separator);
					subj = "POINT: " + ptName + item_sep + ptLat + item_sep + ptLng + item_sep + ptType + item_sep + ptDesc;
					GMailSender sender = new GMailSender("sate2012.avatar@gmail.com", "SATE2013AVATARpass");
					// sender.sendMail(subj, body, from, toList);
					setContentView(R.layout.avatar_sent);
					button_return = (Button) findViewById(R.id.Return);
					button_return.setOnClickListener(this);
				} catch (Exception e) {
					System.out.println("EXCEPTION: " + e);
					e.printStackTrace();
					setContentView(R.layout.avatar_send_failed);
				}
				break;
			case (R.id.Return):
				System.out.println("Exiting");
				finish();
				break;
		}
	}

	public void setLayout(String type) {
		if (type.equals(getResources().getString(R.string.type_comment)))
			setContentView(R.layout.avatar_mail_prep_comment);
		else if (type.equals(getResources().getString(R.string.type_android))) {
			setContentView(R.layout.avatar_mail_prep_android);
			EditText pointName = (EditText) findViewById(R.id.pointName);
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			pointName.setText(telephonyManager.getDeviceId());
		} else if (type.equals(getResources().getString(R.string.type_audio)) || type.equals(getResources().getString(R.string.type_picture))
				|| type.equals(getResources().getString(R.string.type_video))) {
			setContentView(R.layout.avatar_mail_prep_apv);
			EditText pointDesc = (EditText) findViewById(R.id.pointDesc);
			pointDesc.setText(ptURL_noFTP);
			EditText pointName = (EditText) findViewById(R.id.pointName);
			pointName.setText(ptName);
		}

	}

	public void onBackPressed() {
		setResult(Activity.RESULT_CANCELED, null);
		finish();
	}

	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location loc) {
			Toast.makeText(c, "Getting Location.", Toast.LENGTH_SHORT).show();
			ptLat = "" + loc.getLatitude();
			ptLng = "" + loc.getLongitude();
		}

		public void onProviderDisabled(String provider) {
			Toast.makeText(c, "GPS Disabled", Toast.LENGTH_SHORT).show();
		}

		public void onProviderEnabled(String provider) {
			Toast.makeText(c, "GPS Enabled", Toast.LENGTH_SHORT).show();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
}