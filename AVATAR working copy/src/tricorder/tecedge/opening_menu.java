package tricorder.tecedge;

import gupta.ashutosh.avatar.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ViewFlipper;

public class opening_menu extends Activity {
	int sensors_attached = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tricorder_opening_menu);

		Button uploadData = (Button) findViewById(R.id.UploadData);
		uploadData.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
						.getDefaultAdapter();
				if (mBluetoothAdapter == null) {
					// Device does not support Bluetooth
				} else {
					if (!mBluetoothAdapter.isEnabled()) {
						// Bluetooth is not enable :)
						Intent enableIntent = new Intent(
								BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableIntent, 3);
					}
					if (mBluetoothAdapter.isEnabled())
						startActivity(new Intent("tricorder.tecedge.SERVERCOMM"));
				}
			}
		});

		Button GraphData = (Button) findViewById(R.id.GraphData);
		GraphData.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent("tricorder.tecedge.NAME_LOADER"));
			}
		});

		Button mapData = (Button) findViewById(R.id.Map);
		mapData.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent("tricorder.tecedge.MAPM"));
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater settings = getMenuInflater();
		settings.inflate(R.menu.opening_menu_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Animation fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
		Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);

		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent("tricorder.tecedge.SETTINGS"));
			return true;
		case R.id.isconnected:
			switch (sensors_attached) {
			case 0:
				sensors_attached = 1;
				break;
			case 1:
				sensors_attached = 0;
				break;
			}
			return true;
		}
		return false;

	}
};