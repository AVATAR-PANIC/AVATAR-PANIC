package sate2012.avatar.android.googlemaps;

import gupta.ashutosh.avatar.R;
import sate2012.avatar.android.MapsForgeMapViewer;
import sate2012.avatar.android.UploadMedia;
import sate2012.avatar.android.augmentedrealityview.CameraView;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapsViewer extends Activity {

	GoogleMap map;
	Location myLocation = new Location(LocationManager.NETWORK_PROVIDER);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.googlemap_viewer);
		MapFragment mapfrag = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.googlemap));
		map = mapfrag.getMap();
		map.setOnMapLongClickListener(new Listener());
		LatLng location = new LatLng(myLocation.getLatitude(),
				myLocation.getLongitude());
		map.addMarker(new MarkerOptions().position(location));
		map.moveCamera(CameraUpdateFactory
				.newCameraPosition(new CameraPosition(
						map.getCameraPosition().target,
						map.getCameraPosition().zoom, 30, map
								.getCameraPosition().bearing)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.google_maps_viewer, menu);
		return true;
	}

	public void myClickMethod(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.map:
			i = new Intent(getApplicationContext(), MapsForgeMapViewer.class);
			startActivity(i);
			break;
		case R.id.augmentedReality:
			i = new Intent(getApplicationContext(), CameraView.class);
			startActivity(i);
			break;
		case R.id.emergencyCall:
			System.out.println("BOO");
			break;
		case R.id.exit:
			System.exit(0);
			break;
		}
	}

	class Listener implements OnMapLongClickListener {

		@Override
		public void onMapLongClick(LatLng arg0) {
			map.addMarker(new MarkerOptions().position(arg0));
			Intent senderIntent = new Intent(getApplicationContext(),
					UploadMedia.class);
			startActivity(senderIntent);
		}
	}

}
