package tricorder.tecedge;

import sate2012.avatar.android.googlemaps.TricorderGoogleMapsViewer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Refreshm extends Activity {
	Mapm main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TricorderGoogleMapsViewer.h.sendEmptyMessage(0);
		finish();
	}

}
