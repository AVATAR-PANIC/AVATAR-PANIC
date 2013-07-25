package DialogFragments;

import com.guardian_angel.uav_tracker.CameraRecord;

import gupta.ashutosh.avatar.R;
import sate2012.avatar.android.googlemaps.augmentedreality.AugRelPointManager;
import sate2012.avatar.android.googlemaps.augmentedreality.CameraView;
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

/**
 * 
 * @author Garrett - emrickgarrett@gmail.com
 * 
 * This class is used for the point settings in the augmented reality sections of the map.
 * Basically allows the user to select the radius in which points will appear, and also
 * allows the user to select which Augmented Reality to use. Currently used for both applications,
 * and the point radius is simply disabled if it is not AVATAR's augmented reality.
 *
 */
public class PointSettingsDialogFragment extends DialogFragment{

	EditText pointRadius;
	Button saveButton;
	AugRelPointManager manager;
	private boolean hasManager = true;
	RadioButton avatar;
	RadioButton guardianAngel;
	private int currentARType = 0; //Default for no change. 1 = AVATAR 2 = Guardian Angel
	
	public PointSettingsDialogFragment(){
		
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.point_settings_dialog, container, false);
		
		Bundle bundle = getArguments();
		try{
			manager = (AugRelPointManager) bundle.get("POINT_MANAGER");
		}catch(NullPointerException ex){
			hasManager = false;
		}
		
		pointRadius = (EditText) view.findViewById(R.id.point_radius_input);
		avatar = (RadioButton) view.findViewById(R.id.ar_avatar);
		guardianAngel = (RadioButton) view.findViewById(R.id.areality_guardian_angel);
		saveButton = (Button) view.findViewById(R.id.ar_save_button);
		pointRadius.setHint("Example: 60");
		getDialog().setTitle("General Settings");
		
		
		if(!hasManager){
			pointRadius.setEnabled(false); //If it's guardian angel, disable the text area.
		}
		
		saveButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				if(hasManager){
					try{
						if(!pointRadius.getText().equals("")){
							int radius = Integer.parseInt(pointRadius.getText().toString());
							System.out.println("RADIUS: " + radius);
							manager.setClosePointsFromMiles(radius);
							}
						
					}catch(NumberFormatException ex){
						Toast.makeText(getActivity(), "Has to be a valid number!", 3000).show();
					}catch(Exception ex){
						ex.printStackTrace();
					}
					
				}
				FragmentManager fragMgr = getFragmentManager();
				FragmentTransaction xact = fragMgr.beginTransaction();
				
				if(currentARType == 1){
					CameraView tempCamera;
					if(fragMgr.findFragmentByTag("AVATAR_AUGMENTED_REALITY") != null){
						tempCamera = (CameraView) fragMgr.findFragmentByTag("AVATAR_AUGMENTED_REALITY");
						xact.remove(tempCamera);
					}
					tempCamera = new CameraView();
					xact.replace(R.id.container, tempCamera,"AVATAR_AUGMENTED_REALITY");
					xact.addToBackStack(null);
					xact.commit();
					
				}else if(currentARType == 2){
					CameraRecord tempCamera2;
					if(fragMgr.findFragmentByTag("GUARDIAN_ANGEL_AUGMENTED_REALITY") != null){
						tempCamera2 = (CameraRecord) fragMgr.findFragmentByTag("GUARDIAN_ANGEL_AVATAR_AUGMENTED_REALITY");
						xact.remove(tempCamera2);
					}
					tempCamera2 = new CameraRecord();
					xact.replace(R.id.container, tempCamera2,"GUARDIAN_ANGEL_AVATAR_AUGMENTED_REALITY");
					xact.addToBackStack(null);
					xact.commit();
				}
				
				getDialog().cancel();
				
			}
		});
		
		avatar.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				currentARType = 1;
			}
			
		});
		
		guardianAngel.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				currentARType = 2;
				
			}
		});
		
		return view;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		getDialog().getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		
	}
}
