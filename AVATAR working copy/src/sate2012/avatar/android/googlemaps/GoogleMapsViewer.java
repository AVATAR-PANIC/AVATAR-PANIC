package sate2012.avatar.android.googlemaps;

import sate2012.avatar.android.MapsForgeMapViewer;
import sate2012.avatar.android.augmentedrealityview.CameraView;
import gupta.ashutosh.avatar.*;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class GoogleMapsViewer extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.googlemap_viewer);
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
