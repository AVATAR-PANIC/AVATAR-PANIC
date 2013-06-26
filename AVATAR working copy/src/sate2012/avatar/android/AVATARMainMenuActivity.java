package sate2012.avatar.android;

import gupta.ashutosh.avatar.R;
import sate2012.avatar.android.augmentedrealityview.CameraView;
import sate2012.avatar.android.googlemaps.GoogleMapsViewer;
import sate2012.avatar.android.googlemaps.TricorderGoogleMapsViewer;
import DialogFragments.PANICDialogFragment;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
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
		setContentView(R.layout.avatar_main);
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

		Bundle bundle = new Bundle();
		bundle.putInt("MAP_TYPE", 1);
		GoogleMapsViewer tempMap = new GoogleMapsViewer();
		tempMap.setArguments(bundle);
		xact.add(R.id.container, tempMap, "AVATAR_MAP");
		
		xact.add(R.id.menu, new Frag(R.layout.map_menu_frag), "MENU");
		xact.commit();
		
		//new HandleID(this).execute();
	}

	/**
	 * Called when a button is clicked by the user. Navigates to the appropriate
	 * Activity
	 * 
	 * @param View
	 *            v - the button clicked
	 */
	public void myClickMethod(View v) {
		getActionBar().show();
		Intent i;
		FragmentManager fragMgr;
		FragmentTransaction xact;
		switch (v.getId()) {
		case R.id.map:
			//this.finish();
			fragMgr = getFragmentManager();
			xact = fragMgr.beginTransaction();
			Bundle bundle = new Bundle();
			bundle.putInt("MAP_TYPE", 1);
			GoogleMapsViewer tempMap;
			
			
			if(fragMgr.findFragmentByTag("AVATAR_MAP") != null){
				tempMap = (GoogleMapsViewer) fragMgr.findFragmentByTag("AVATAR_MAP");
				xact.detach(tempMap);
			}
			tempMap = new GoogleMapsViewer();
			xact.replace(R.id.container,tempMap, "AVATAR_MAP");
			xact.replace(R.id.menu, fragMgr.findFragmentByTag("MENU"), "MENU");
			xact.addToBackStack(null);
			tempMap.setArguments(bundle);
			xact.commit();
			break;
		case R.id.augmentedReality:
			//getActionBar().hide();
			fragMgr = getFragmentManager();
			xact = fragMgr.beginTransaction();
			if(fragMgr.findFragmentByTag("AVATAR_AUGMENTED_REALITY") != null){
				xact.replace(R.id.container, fragMgr.findFragmentByTag("AVATAR_AUGMENTED_REALITY"), "AVATAR_AUGMENTED_REALITY");
			}else{
				xact.replace(R.id.container, new CameraView(), "AVATAR_AUGMENTED_REALITY");
			}
			if(fragMgr.findFragmentByTag("AUG_MENU") != null){
				xact.replace(R.id.menu, fragMgr.findFragmentByTag("AUG_MENU"));
			}else{
				xact.replace(R.id.menu, new Frag(R.layout.augmented_reality_menu_frag), "AUG_MENU");
				xact.addToBackStack(null);
			}
				//xact.addToBackStack(null);
			xact.commit();
			break;
		case R.id.emergencyCall:
			
			fragMgr = getFragmentManager();
			DialogFragment dialog = new PANICDialogFragment();
			dialog.show(fragMgr, "PANIC");
//			fragMgr = getFragmentManager();
//			xact = fragMgr.beginTransaction();
//			if(fragMgr.findFragmentByTag("PHONE_CALL") != null){
//				xact.replace(R.id.menu, fragMgr.findFragmentByTag("PHONE_CALL"), "PHONE_CALL");
//			}else{
//				xact.replace(R.id.menu, new PhoneCall(), "PHONE_CALL");
//				xact.addToBackStack(null);
//			}
//			xact.commit();
			
			//i = new Intent(getApplicationContext(), PhoneCall.class);
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
	
	public void tricorderOnClick(View v){
		FragmentManager fragMgr = getFragmentManager();
		
		((TricorderGoogleMapsViewer)fragMgr.findFragmentByTag("TRICORDER_MAP")).tricorderOnClick(v);
	}

}
