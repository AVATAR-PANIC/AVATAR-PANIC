package sate2012.avatar.android;

import sate2012.avatar.android.augmentedrealityview.CameraView;
import sate2012.avatar.android.googlemaps.GoogleMapsViewer;
import gupta.ashutosh.avatar.R;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * The main menu of the AVATAR Application This menu navigates users to
 * different parts of the program.
 */
public class AVATARMainMenuActivity extends Activity implements OnClickListener {
	private Button uploadB; // Button that switches to a menu that lets the user
							// upload different types of media

	/*
	 * private Button mapB; // Button that switches to map view private Button
	 * naoB; // Button that switches to NAO Robot Control private Button arB; //
	 * Button that switches to Augmented Reality private Button settingB; //
	 * Button that switches to the Settings Menu
	 */
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.main);
//		uploadB = (Button) findViewById(R.id.uploadB);
//		uploadB.setOnClickListener(this);
		/*
		 * mapB = (Button) findViewById(R.id.mapB);
		 * mapB.setOnClickListener(this); naoB = (Button)
		 * findViewById(R.id.naoB); naoB.setOnClickListener(this); arB =
		 * (Button) findViewById(R.id.arB); arB.setOnClickListener(this);
		 * settingB = (Button) findViewById(R.id.settingB);
		 * settingB.setOnClickListener(this);
		 */
		
		FragmentManager fragMgr = getFragmentManager();
		
		FragmentTransaction xact = fragMgr.beginTransaction();
		xact.add(R.id.container, new GoogleMapsViewer(), "MAP");
		xact.commit();
	}

	/**
	 * Called when a button is clicked by the user. Navigates to the appropriate
	 * Activity
	 * 
	 * @param View
	 *            v - the button clicked
	 */
	public void myClickMethod(View v) {
		Intent i;
		FragmentManager fragMgr;
		FragmentTransaction xact;
		switch (v.getId()) {
		case R.id.map:
			//this.finish();
			
			fragMgr = getFragmentManager();
			
			xact = fragMgr.beginTransaction();
			if(fragMgr.findFragmentByTag("MAP") != null){
				xact.replace(R.id.container, fragMgr.findFragmentByTag("MAP"), "MAP");
			}else{
				xact.replace(R.id.container, new GoogleMapsViewer(), "MAP");
			}
				xact.addToBackStack(null);
			xact.commit();
			break;
		case R.id.augmentedReality:
			fragMgr = getFragmentManager();
			xact = fragMgr.beginTransaction();
			//if(null == fragMgr.findFragmentByTag("CAMERA")){
				xact.replace(R.id.container, new CameraView());
			//}
				xact.addToBackStack(null);
			xact.commit();
		case R.id.emergencyCall:
			i = new Intent(getApplicationContext(), PhoneCall.class);
			break;
		case R.id.exit:
			this.finish();//try activityname.finish instead of this
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		//When button was clicked.
		
	}
}
