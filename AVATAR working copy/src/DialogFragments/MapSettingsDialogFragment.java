package DialogFragments;

import gupta.ashutosh.avatar.R;
import sate2012.avatar.android.googlemaps.EagleEyeGoogleMapsViewer;
import sate2012.avatar.android.googlemaps.GoogleMapsViewer;
import sate2012.avatar.android.googlemaps.GuardianAngelGoogleMapsViewer;
import sate2012.avatar.android.googlemaps.TricorderGoogleMapsViewer;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

import com.google.android.gms.maps.GoogleMap;

/**
 * 
 * @author Garrett - emrickgarrett@gmail.com
 * 
 * This class is used for the Map settings menu item, and is used commonly
 * throughout the application. This is what is used to switch the map type
 * of the application.
 *
 */
@SuppressLint("ValidFragment")
public class MapSettingsDialogFragment extends DialogFragment {

	RadioButton avatar;
	RadioButton tricorder;
	RadioButton guardianAngel;
	RadioButton eagleEye;
	RadioButton normal;
	RadioButton satellite;
	RadioButton hybrid;
	RadioButton terrain;
	Button save;
	private int currentMap; // 1 is for AVATAR map, 2 Tricorder, 3 Guardian Angel
	private int currentMapType;
	
	public MapSettingsDialogFragment(){
		currentMap = 1;
		currentMapType = GoogleMap.MAP_TYPE_NORMAL;
	}
	
	public MapSettingsDialogFragment(int currentMap, int currentMapType){
		this.currentMap = currentMap;
		this.currentMapType = currentMapType;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.map_settings, container, false);
		getDialog().setTitle("Map Settings");
		
		avatar = (RadioButton) view.findViewById(R.id.avatar_map);
		tricorder = (RadioButton) view.findViewById(R.id.tricorder_map);
		guardianAngel = (RadioButton) view.findViewById(R.id.guardian_angel_map);
		eagleEye = (RadioButton) view.findViewById(R.id.eagle_eye_map);
		normal = (RadioButton) view.findViewById(R.id.normal_view);
		satellite = (RadioButton) view.findViewById(R.id.satellite_view);
		hybrid = (RadioButton) view.findViewById(R.id.hybrid_view);
		terrain = (RadioButton) view.findViewById(R.id.terrain_view);
		save = (Button) view.findViewById(R.id.map_settings_save);
		
		avatar.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton arg0, boolean arg1){
				currentMap = 1;
			}
		});
		
		tricorder.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton arg0, boolean arg1){
				currentMap = 2;
			}
		});
		
		guardianAngel.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton arg0, boolean arg1){
				currentMap = 3;
			}
		});
		
		eagleEye.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton arg0, boolean arg1){
				currentMap = 4;
			}
		});
		
		normal.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton arg0, boolean arg1){
				currentMapType = GoogleMap.MAP_TYPE_NORMAL;
			}
		});
		
		satellite.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton arg0, boolean arg1){
				currentMapType = GoogleMap.MAP_TYPE_SATELLITE;
			}
		});
		
		hybrid.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton arg0, boolean arg1){
				currentMapType = GoogleMap.MAP_TYPE_HYBRID;
			}
		});
		
		terrain.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton arg0, boolean arg1){
				currentMapType = GoogleMap.MAP_TYPE_TERRAIN;
			}
		});
		
		save.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				createFragment();
				getDialog().cancel();
			}
		});
		
		return view;
	}
	
	private void createFragment(){
		FragmentManager fragMgr = getFragmentManager();
		FragmentTransaction xact = fragMgr.beginTransaction();
		Bundle bundle = new Bundle();
		bundle.putInt("MAP_TYPE", currentMapType);
		switch(currentMap){
		case 1:
			GoogleMapsViewer tempMap;
			if(fragMgr.findFragmentByTag("AVATAR_MAP") != null){
				tempMap = (GoogleMapsViewer) fragMgr.findFragmentByTag("AVATAR_MAP");
				xact.detach(tempMap);
			}
			tempMap = new GoogleMapsViewer();
			xact.replace(R.id.container,tempMap, "AVATAR_MAP");
			xact.addToBackStack(null);
			tempMap.setArguments(bundle);
			xact.commit();
			
			break;
		case 2:
			TricorderGoogleMapsViewer tempMap2;
			if(fragMgr.findFragmentByTag("TRICORDER_MAP") != null){
				tempMap2 = (TricorderGoogleMapsViewer) fragMgr.findFragmentByTag("TRICORDER_MAP");
				xact.detach(tempMap2);
			}
			tempMap2 = new TricorderGoogleMapsViewer();
			xact.replace(R.id.container, tempMap2, "TRICORDER_MAP");
			xact.addToBackStack(null);
			tempMap2.setArguments(bundle);
			xact.commit();
			
			break;
		case 3:
			GuardianAngelGoogleMapsViewer tempMap3;
			if(fragMgr.findFragmentByTag("GUARDIAN_ANGEL_MAP") != null){
				tempMap3 = (GuardianAngelGoogleMapsViewer) fragMgr.findFragmentByTag("GUARDIAN_ANGEL_MAP");
				xact.detach(tempMap3);
			}
			tempMap3 = new GuardianAngelGoogleMapsViewer();
			xact.replace(R.id.container, tempMap3, "GUARDIAN_ANGEL_MAP");
			xact.addToBackStack(null);
			tempMap3.setArguments(bundle);
			xact.commit();
			
			break;
		case 4:
			EagleEyeGoogleMapsViewer tempMap4;
			if(fragMgr.findFragmentByTag("EAGLE_EYE_MAP") != null){
				tempMap4 = (EagleEyeGoogleMapsViewer) fragMgr.findFragmentByTag("EAGLE_EYE_MAP");
				xact.detach(tempMap4);
			}
			tempMap4 = new EagleEyeGoogleMapsViewer();
			xact.replace(R.id.container,tempMap4,"EAGLE_EYE_MAP");
			xact.addToBackStack(null);
			tempMap4.setArguments(bundle);
			xact.commit();
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		getDialog().getWindow().setLayout(250,LayoutParams.WRAP_CONTENT);
	}
	
}
