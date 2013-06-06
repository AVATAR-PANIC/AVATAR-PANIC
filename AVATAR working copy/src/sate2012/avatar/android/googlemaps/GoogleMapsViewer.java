package sate2012.avatar.android.googlemaps;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import sate2012.avatar.android.MapsForgeMapViewer;
import sate2012.avatar.android.augmentedrealityview.CameraView;
import gupta.ashutosh.avatar.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class GoogleMapsViewer extends Activity {

	GoogleMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.googlemap_viewer);
		MapFragment mapfrag = ((MapFragment) getFragmentManager().findFragmentById(R.id.googlemap));
		map = mapfrag.getMap();
		map.addMarker(new MarkerOptions().position(new LatLng(0, 0)));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.google_maps_viewer, menu);
		return true;
	}

	public void myClickMethod(View v){
		  Intent i;
		  switch(v.getId()){
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
}
