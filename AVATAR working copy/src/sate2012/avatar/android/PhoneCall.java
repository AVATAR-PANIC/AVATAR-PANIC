package sate2012.avatar.android;

import gupta.ashutosh.avatar.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PhoneCall extends Activity implements OnClickListener {

	final Context context = this;
	private Button policeB;
	private Button fireB;
	private Button parentsB;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_call);

		// add PhoneStateListener
		PhoneCallListener phoneListener = new PhoneCallListener();
		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(phoneListener,
				PhoneStateListener.LISTEN_CALL_STATE);

		policeB = (Button) findViewById(R.id.police);
		fireB = (Button) findViewById(R.id.fire);
		parentsB = (Button) findViewById(R.id.parents);

		policeB.setOnClickListener(this);
		fireB.setOnClickListener(this);
		parentsB.setOnClickListener(this);
		// add button listener

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.police:
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:911"));
			startActivity(callIntent);
			break;
		case R.id.fire:
			Intent callIntent1 = new Intent(Intent.ACTION_CALL);
			callIntent1.setData(Uri.parse("tel:911"));
			startActivity(callIntent1);
			break;
		case R.id.parents:
			Intent callIntent2 = new Intent(Intent.ACTION_CALL);
			callIntent2.setData(Uri.parse("tel:9376027201"));
			startActivity(callIntent2);
			break;
		}
	}

	// monitor phone call activities
	private class PhoneCallListener extends PhoneStateListener {

		private boolean isPhoneCalling = false;

		String LOG_TAG = "LOGGING 123";

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			if (TelephonyManager.CALL_STATE_RINGING == state) {
				// phone ringing
				Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
			}

			if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
				// active
				Log.i(LOG_TAG, "OFFHOOK");

				isPhoneCalling = true;
			}

			if (TelephonyManager.CALL_STATE_IDLE == state) {
				// run when class initial and phone call ended,
				// need detect flag from CALL_STATE_OFFHOOK
				Log.i(LOG_TAG, "IDLE");

				if (isPhoneCalling) {

					Log.i(LOG_TAG, "restart app");

					// restart app
					Intent i = getBaseContext().getPackageManager()
							.getLaunchIntentForPackage(
									getBaseContext().getPackageName());
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);

					isPhoneCalling = false;
				}

			}
		}
	}

}
