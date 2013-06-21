package sate2012.avatar.android;

import com.guardian_angel.uav_tracker.MainActivity;

import sate2012.avatar.android.augmentedrealityview.CameraView;
import sate2012.avatar.android.googlemaps.GoogleMapsViewer;
import sate2012.avatar.android.googlemaps.GuardianAngelGoogleMapsViewer;
import sate2012.avatar.android.googlemaps.TricorderGoogleMapsViewer;
import tricorder.tecedge.opening_menu;
import gupta.ashutosh.avatar.R;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
		
		xact.add(R.id.menu, new Frag(R.layout.map_menu_frag), "MENU");
		xact.commit();
		
		new HandleID(this).execute();
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
			if(fragMgr.findFragmentByTag("MENU") != null){
				xact.replace(R.id.menu, fragMgr.findFragmentByTag("MENU"), "MENU");
			}else{
				xact.replace(R.id.menu, new Frag(R.layout.map_menu_frag), "MENU");
			}
			xact.commit();
			break;
		case R.id.augmentedReality:
			fragMgr = getFragmentManager();
			xact = fragMgr.beginTransaction();
			if(fragMgr.findFragmentByTag("AUGMENTED_REALITY") != null){
				xact.replace(R.id.container, fragMgr.findFragmentByTag("AUGMENTED_REALITY"), "AUGMENTED_REALITY");
			}else{
				xact.replace(R.id.container, new CameraView(), "AUGMENTED_REALITY");
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
			xact = fragMgr.beginTransaction();
			if(fragMgr.findFragmentByTag("PHONE_CALL") != null){
				xact.replace(R.id.menu, fragMgr.findFragmentByTag("PHONE_CALL"), "PHONE_CALL");
			}else{
				xact.replace(R.id.menu, new PhoneCall(), "PHONE_CALL");
				xact.addToBackStack(null);
			}
			xact.commit();
			
			//i = new Intent(getApplicationContext(), PhoneCall.class);
			break;
		case R.id.exit:
			this.finish();//try activityname.finish instead of this
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case R.id.phone_exit_button:
			fragMgr = getFragmentManager();
			xact = fragMgr.beginTransaction();
			fragMgr.popBackStack();
			xact.commit();
			break;
			
		//All implentations of other projects, Create them as activities for now.
		case R.id.tricorder:
			//startActivity(new Intent(this, opening_menu.class));
			//Currently want to draw the fragment I'm working on
			fragMgr = getFragmentManager();
			xact = fragMgr.beginTransaction();
			if(fragMgr.findFragmentByTag("TRICORDER_MAP") != null){
				xact.replace(R.id.container, fragMgr.findFragmentByTag("TRICORDER_MAP"), "TRICORDER_MAP");
			}else{
				xact.replace(R.id.container, new TricorderGoogleMapsViewer(), "TRICORDER_MAP");
				xact.addToBackStack(null);
			}
			xact.commit();
			
			break;
		case R.id.guardian_angel:
//			fragMgr = getFragmentManager();
//			xact = fragMgr.beginTransaction();
//			if(fragMgr.findFragmentByTag("GUARDIAN_ANGEL_MAP") != null){
//				xact.replace(R.id.container, fragMgr.findFragmentByTag("GUARDIAN_ANGEL_MAP"), "GUARDIAN_ANGEL_MAP");
//			}else{
//				xact.replace(R.id.container, new GuardianAngelGoogleMapsViewer(), "GUARDIAN_ANGEL_MAP");
//				xact.addToBackStack(null);
//			}
//			xact.commit();
			//startActivity(new Intent(this, MainActivity.class));
			//Currently want to draw the fragment I'm working on
			fragMgr = getFragmentManager();
			xact = fragMgr.beginTransaction();
			if(fragMgr.findFragmentByTag("GUARDIAN_ANGEL_SERVER_CONNECT") != null){
				xact.replace(R.id.container, fragMgr.findFragmentByTag("GUARDIAN_ANGEL_SERVER_CONNECT"), "GUARDIAN_ANGEL_SERVER_CONNECT");
			}else{
				xact.replace(R.id.container, new MainActivity(), "GUARDIAN_ANGEL_SERVER_CONNECT");
				xact.addToBackStack(null);
			}
			xact.commit();
			break;
		case R.id.avatar:
			//this.finish();
			
			fragMgr = getFragmentManager();
			
			xact = fragMgr.beginTransaction();
			if(fragMgr.findFragmentByTag("MAP") != null){
				xact.replace(R.id.container, fragMgr.findFragmentByTag("MAP"), "MAP");
			}else{
				xact.replace(R.id.container, new GoogleMapsViewer(), "MAP");
			}
			xact.replace(R.id.menu, new Frag(R.layout.map_menu_frag), "MENU");
			xact.commit();
			break;
			
		//All Non fragment changing buttons. IE change map type
		case R.id.changeType:
			fragMgr = getFragmentManager();
			
			int[] mapTypeArray = GoogleMapsViewer.mapTypes;
			GoogleMapsViewer viewer = ((GoogleMapsViewer)fragMgr.findFragmentByTag("MAP"));
			
			mapTypeLoop:
			for(int a = 0; a < mapTypeArray.length; a++){
				if(mapTypeArray[a] == viewer.currentMapType){
					if(a == 3){
						viewer.getMap().setMapType(GoogleMapsViewer.mapTypes[0]);
						viewer.currentMapType = GoogleMapsViewer.mapTypes[0];
						break mapTypeLoop;
					}else{
						viewer.getMap().setMapType(GoogleMapsViewer.mapTypes[a+1]);
						viewer.currentMapType = GoogleMapsViewer.mapTypes[a+1];
						break mapTypeLoop;
					}
				}
			}
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
	
	/**
	 * Used by a method below to set the side menu correctly
	 */
	public void emergencyCall(){
		FragmentManager fragMgr;
		FragmentTransaction xact;
		
		fragMgr = getFragmentManager();
		xact = fragMgr.beginTransaction();
		if(fragMgr.findFragmentByTag("PHONE_CALL") != null){
			xact.replace(R.id.menu, fragMgr.findFragmentByTag("PHONE_CALL"), "PHONE_CALL");
		}else{
			xact.replace(R.id.menu, new PhoneCall(), "PHONE_CALL");
			xact.addToBackStack(null);
		}
		xact.commit();
		
	}
	
	/**
	 * When the app is resumed, will check media was uploaded and was emergency. If so, it will bring up the right menu.
	 */
	@Override
	public void onResume(){
		super.onResume();
		if(UploadMedia.isEmergency){
			emergencyCall();
		}else{
			
		}
	}
	
	@Override
	public Dialog onCreateDialog(int id) {
		FragmentManager fragMgr = getFragmentManager();
		System.out.println("HEY: " + id);
		Dialog dialog = ((MainActivity)(fragMgr.findFragmentByTag("GUARDIAN_ANGEL_SERVER_CONNECT"))).onCreateDialog(id);
		dialog.show();
		
		return dialog;
	}

}
