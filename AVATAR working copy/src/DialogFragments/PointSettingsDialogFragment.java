package DialogFragments;

import com.guardian_angel.uav_tracker.CameraRecord;

import gupta.ashutosh.avatar.R;
import sate2012.avatar.android.augmentedrealityview.AugRelPointManager;
import sate2012.avatar.android.augmentedrealityview.CameraView;
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

public class PointSettingsDialogFragment extends DialogFragment{

	EditText pointRadius;
	Button saveButton;
	AugRelPointManager manager;
	RadioButton avatar;
	RadioButton guardianAngel;
	private int currentARType = 0; //Default for no change. 1 = AVATAR 2 = Guardian Angel
	
	public PointSettingsDialogFragment(){
		
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.dialog_point_settings, container, false);
		
		Bundle bundle = getArguments();
		manager = (AugRelPointManager) bundle.get("POINT_MANAGER");
		
		pointRadius = (EditText) view.findViewById(R.id.point_radius_input);
		avatar = (RadioButton) view.findViewById(R.id.ar_avatar);
		guardianAngel = (RadioButton) view.findViewById(R.id.areality_guardian_angel);
		saveButton = (Button) view.findViewById(R.id.ar_save_button);
		pointRadius.setHint("Example: 60");
		getDialog().setTitle("General Settings");
		
		
		saveButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
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
