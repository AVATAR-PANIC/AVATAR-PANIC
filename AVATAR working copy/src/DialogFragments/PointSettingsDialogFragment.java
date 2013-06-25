package DialogFragments;

import sate2012.avatar.android.augmentedrealityview.AugRelPointManager;
import gupta.ashutosh.avatar.R;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PointSettingsDialogFragment extends DialogFragment{

	EditText pointRadius;
	Button saveButton;
	AugRelPointManager manager;
	
	public PointSettingsDialogFragment(){
		
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.point_settings_dialog, container, false);
		
		Bundle bundle = getArguments();
		manager = (AugRelPointManager) bundle.get("POINT_MANAGER");
		
		pointRadius = (EditText) view.findViewById(R.id.point_radius_input);
		saveButton = (Button) view.findViewById(R.id.ar_save_button);
		pointRadius.setHint("Example: 60");
		getDialog().setTitle("General Settings");
		
		
		saveButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				try{
					
					int radius = Integer.parseInt(pointRadius.getText().toString());
					System.out.println("RADIUS: " + radius);
					manager.setClosePointsFromMiles(radius);
					getDialog().cancel();
					
				}catch(NumberFormatException ex){
					Toast.makeText(getActivity(), "Has to be a valid number!", 3000).show();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
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
