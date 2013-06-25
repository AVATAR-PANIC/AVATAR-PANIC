package DialogFragments;

import sate2012.avatar.android.googlemaps.GuardianAngelGoogleMapsViewer;
import gupta.ashutosh.avatar.R;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

@SuppressLint("ValidFragment")
public class MajorCitiesDialogFragment extends DialogFragment {

	protected Button ok;
	protected Button cancel;
	private RadioButton NewYorkCity;
	private RadioButton Chicago;
	private RadioButton Houston;
	private RadioButton Philadelphia;
	private RadioButton Phoenix;
	private RadioButton Boston;
	private RadioButton Denver;
	private RadioButton WashingtonDC;
	private RadioButton Miami;
	private RadioButton Indianapolis;
	private RadioButton Columbus;
	private RadioButton Memphis;
	private RadioButton OklahomaCity;
	private RadioButton LasVegas;
	private RadioButton Charlotte;
	private int MajorCitiesDialog = 0;
	double lat = 0, lon = 0;
	GuardianAngelGoogleMapsViewer map;
	
	
	public MajorCitiesDialogFragment(){
		
	}
	
	public MajorCitiesDialogFragment(GuardianAngelGoogleMapsViewer map){
		this.map = map;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
		View view = inflater.inflate(R.layout.guardian_angel_major_cities, container);
		
		getDialog().getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		getDialog().setTitle("Major Cities");

		this.ok = (Button) view.findViewById(R.id.major_cities_ok);
		this.cancel = (Button) view.findViewById(R.id.major_cities_cancel);
		// initialize and instantiate variables
		this.NewYorkCity = (RadioButton) view.findViewById(R.id.NewYorkCity);
		this.Chicago = (RadioButton) view.findViewById(R.id.Chicago);
		this.Houston = (RadioButton) view.findViewById(R.id.Houston);
		this.Philadelphia = (RadioButton) view.findViewById(R.id.Philadelphia);
		this.Phoenix = (RadioButton) view.findViewById(R.id.Phoenix);
		this.Boston = (RadioButton) view.findViewById(R.id.Boston);
		this.Denver = (RadioButton) view.findViewById(R.id.Denver);
		this.WashingtonDC = (RadioButton) view.findViewById(R.id.WashingtonDC);
		this.Miami = (RadioButton) view.findViewById(R.id.Miami);
		this.Indianapolis = (RadioButton) view.findViewById(R.id.Indianapolis);
		this.Columbus = (RadioButton) view.findViewById(R.id.Columbus);
		this.Memphis = (RadioButton) view.findViewById(R.id.Memphis);
		this.OklahomaCity = (RadioButton) view.findViewById(R.id.OklahomaCity);
		this.LasVegas = (RadioButton) view.findViewById(R.id.LasVegas);
		this.Charlotte = (RadioButton) view.findViewById(R.id.Charlotte);

		// City RadioButton listeners
		NewYorkCity.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (NewYorkCity.isChecked()) {
					lat = 40.713739;
					lon = -74.005398;
				}
			}
		});
		Chicago.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Chicago.isChecked()) {
					lat = 41.875371;
					lon = -87.625314;
				}
			}
		});
		Houston.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Houston.isChecked()) {
					lat = 29.756547;
					lon = -95.36285;
				}
			}
		});
		Philadelphia.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Philadelphia.isChecked()) {
					lat = 39.946786;
					lon = -75.16862;
				}
			}
		});
		Phoenix.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Phoenix.isChecked()) {
					lat = 33.442321;
					lon = -112.071263;
				}
			}
		});
		Boston.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Boston.isChecked()) {
					lat = 42.348232;
					lon = -71.063412;
				}
			}
		});
		Denver.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Denver.isChecked()) {
					lat = 39.735568;
					lon = -104.985699;
				}
			}
		});
		WashingtonDC.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (WashingtonDC.isChecked()) {
					lat = 38.894734;
					lon = -77.036385;
				}
			}
		});
		Miami.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Miami.isChecked()) {
					lat = 25.784622;
					lon = -80.227742;
				}
			}
		});
		Indianapolis.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Indianapolis.isChecked()) {
					lat = 39.759292;
					lon = -86.164797;
				}
			}
		});
		Columbus.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Columbus.isChecked()) {
					lat = 39.958531;
					lon = -82.997965;
				}
			}
		});
		Memphis.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Memphis.isChecked()) {
					lat = 35.147173;
					lon = -90.048475;
				}
			}
		});
		OklahomaCity.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (OklahomaCity.isChecked()) {
					lat = 35.4672;
					lon = -97.515899;
				}
			}
		});
		LasVegas.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (LasVegas.isChecked()) {
					lat = 36.112631;
					lon = -115.173062;
				}
			}
		});
		Charlotte.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (Charlotte.isChecked()) {
					lat = 35.225808;
					lon = -80.845206;
				}
			}
		});
		
		// Set the ok button listener
				ok.setOnClickListener(new OnClickListener() {
					// Called when the ok button is pressed
					public void onClick(View v) {
						map.zoomToCity(lat, lon);
						getDialog().cancel();
					}
				});
				// Set the cancel button listener
				cancel.setOnClickListener(new OnClickListener() {
					// Called when the Cancel button is pressed
					public void onClick(View v) {
						try {
							getDialog().cancel();
						} catch (Exception ex) {
							ex.printStackTrace();
						}

					}
				});
		
		return view;
	}
	
}
