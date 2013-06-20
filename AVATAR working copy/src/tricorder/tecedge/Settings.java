package tricorder.tecedge;

import gupta.ashutosh.avatar.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import sate2012.avatar.android.googlemaps.TricorderGoogleMapsViewer;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class Settings extends PreferenceActivity {
	public static final String PREF_FILE_NAME = "Settings";
	String initializing = "Initialize";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TricorderGoogleMapsViewer.h.sendEmptyMessage(0);
		addPreferencesFromResource(R.layout.settings);
		Preference legend = (Preference) findPreference("legend");
		Preference timerange = (Preference) findPreference("timerange");
		Preference checkPref = (Preference) findPreference("autoupload");
		final CheckBoxPreference compass = (CheckBoxPreference) findPreference("compass");
		final CheckBoxPreference ZoomButtons = (CheckBoxPreference) findPreference("ZoomButtons");
		final CheckBoxPreference SatelliteView = (CheckBoxPreference) findPreference("SatelliteView");
		final CheckBoxPreference allType = (CheckBoxPreference) findPreference("allType");
		final CheckBoxPreference tempType = (CheckBoxPreference) findPreference("tempType");
		final CheckBoxPreference CO2Type = (CheckBoxPreference) findPreference("CO2Type");
		final CheckBoxPreference methaneType = (CheckBoxPreference) findPreference("methaneType");
		final CheckBoxPreference COType = (CheckBoxPreference) findPreference("COType");
		final CheckBoxPreference humidityType = (CheckBoxPreference) findPreference("humidityType");
		final CheckBoxPreference radiationType = (CheckBoxPreference) findPreference("radiationType");
		final CheckBoxPreference pressureType = (CheckBoxPreference) findPreference("pressureType");
		final CheckBoxPreference luminosityType = (CheckBoxPreference) findPreference("luminosityType");
		final CheckBoxPreference imgType = (CheckBoxPreference) findPreference("imgType");

		checkPref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref,
							Object value) {
						savePreferences(pref.getKey(), value.toString());
						return true;
					}

				});

		allType.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference pref, Object value) {
				savePreferences(pref.getKey(), value.toString());
				tempType.setChecked((Boolean) value);
				CO2Type.setChecked((Boolean) value);
				methaneType.setChecked((Boolean) value);
				COType.setChecked((Boolean) value);
				humidityType.setChecked((Boolean) value);
				radiationType.setChecked((Boolean) value);
				pressureType.setChecked((Boolean) value);
				luminosityType.setChecked((Boolean) value);
				imgType.setChecked((Boolean) value);
				if ((Boolean) value) {
					savePreferences(tempType.getKey(),
							"sensor_type='Temperature'");
					savePreferences(CO2Type.getKey(), "sensor_type='CO2'");
					savePreferences(methaneType.getKey(),
							"sensor_type='Methane'");
					savePreferences(COType.getKey(), "sensor_type='CO'");
					savePreferences(humidityType.getKey(),
							"sensor_type='Humidity'");
					savePreferences(radiationType.getKey(),
							"sensor_type='Radiation'");
					savePreferences(pressureType.getKey(),
							"sensor_type='Pressure'");
					savePreferences(luminosityType.getKey(),
							"sensor_type='Luminosity'");
					savePreferences(imgType.getKey(), "sensor_type='Image'");
				} else {
					savePreferences(tempType.getKey(), "sensor_type=''");
					savePreferences(CO2Type.getKey(), "sensor_type=''");
					savePreferences(methaneType.getKey(), "sensor_type=''");
					savePreferences(COType.getKey(), "sensor_type=''");
					savePreferences(humidityType.getKey(), "sensor_type=''");
					savePreferences(radiationType.getKey(), "sensor_type=''");
					savePreferences(pressureType.getKey(), "sensor_type=''");
					savePreferences(luminosityType.getKey(), "sensor_type=''");
					savePreferences(imgType.getKey(), "sensor_type=''");
				}
				return true;
			}
		});

		tempType.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference pref, Object value) {
				if ((Boolean) value)
					savePreferences(pref.getKey(), "sensor_type='Temperature'");
				else {
					savePreferences(pref.getKey(), "sensor_type=''");
					allType.setChecked(false);
					savePreferences(allType.getKey(), value.toString());
				}
				return true;
			}
		});
		CO2Type.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference pref, Object value) {
				if ((Boolean) value)
					savePreferences(pref.getKey(), "sensor_type='CO2'");
				else {
					savePreferences(pref.getKey(), "sensor_type=''");
					allType.setChecked(false);
					savePreferences(allType.getKey(), value.toString());
				}
				return true;
			}
		});
		methaneType
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref,
							Object value) {
						if ((Boolean) value)
							savePreferences(pref.getKey(),
									"sensor_type='Methane'");
						else {
							savePreferences(pref.getKey(), "sensor_type=''");
							allType.setChecked(false);
							savePreferences(allType.getKey(), value.toString());
						}
						return true;
					}
				});
		COType.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference pref, Object value) {
				if ((Boolean) value)
					savePreferences(pref.getKey(), "sensor_type='CO'");
				else {
					savePreferences(pref.getKey(), "sensor_type=''");
					allType.setChecked(false);
					savePreferences(allType.getKey(), value.toString());
				}
				return true;
			}
		});
		humidityType
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref,
							Object value) {
						if ((Boolean) value)
							savePreferences(pref.getKey(),
									"sensor_type='Humidity'");
						else {
							savePreferences(pref.getKey(), "sensor_type=''");
							allType.setChecked(false);
							savePreferences(allType.getKey(), value.toString());
						}
						return true;
					}
				});
		radiationType
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref,
							Object value) {
						if ((Boolean) value)
							savePreferences(pref.getKey(),
									"sensor_type='Radiation'");
						else {
							savePreferences(pref.getKey(), "sensor_type=''");
							allType.setChecked(false);
							savePreferences(allType.getKey(), value.toString());
						}
						return true;
					}
				});
		pressureType
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref,
							Object value) {
						if ((Boolean) value)
							savePreferences(pref.getKey(),
									"sensor_type='Pressure'");
						else {
							savePreferences(pref.getKey(), "sensor_type=''");
							allType.setChecked(false);
							savePreferences(allType.getKey(), value.toString());
						}
						return true;
					}
				});
		luminosityType
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref,
							Object value) {
						if ((Boolean) value)
							savePreferences(pref.getKey(),
									"sensor_type='Luminosity'");
						else {
							savePreferences(pref.getKey(), "sensor_type=''");
							allType.setChecked(false);
							savePreferences(allType.getKey(), value.toString());
						}
						return true;
					}
				});
		imgType.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference pref, Object value) {
				if ((Boolean) value)
					savePreferences(pref.getKey(), "sensor_type='Image'");
				else {
					savePreferences(pref.getKey(), "sensor_type=''");
					allType.setChecked(false);
					savePreferences(allType.getKey(), value.toString());
				}
				return true;
			}
		});
		compass.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference pref, Object value) {
				if ((Boolean) value) {
					savePreferences(pref.getKey(), "true");
				} else {
					savePreferences(pref.getKey(), "");
				}
				return true;
			}
		});
		ZoomButtons
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref,
							Object value) {
						if ((Boolean) value) {
							savePreferences(pref.getKey(), "true");
						} else {
							savePreferences(pref.getKey(), "");
						}
						return true;
					}
				});
		SatelliteView
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref,
							Object value) {
						if ((Boolean) value) {
							savePreferences(pref.getKey(), "true");
						} else {
							savePreferences(pref.getKey(), "");
						}
						return true;
					}
				});
		legend.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				final Dialog dialog = new Dialog(Settings.this);
				dialog.setContentView(R.layout.legendm);
				dialog.setTitle(R.string.legendt);
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
				return true;
			}
		});
		timerange
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						final Dialog dialog = new Dialog(Settings.this);
						System.out.print(loadPreferences("Start"));
						dialog.setContentView(R.layout.timem);
						dialog.setTitle("Choose Data Time Range");
						dialog.setCanceledOnTouchOutside(true);
						dialog.show();
						final CheckBox timeall = (CheckBox) dialog
								.findViewById(R.id.timeall);
						Button save = (Button) dialog.findViewById(R.id.save);
						final DatePicker startdate = (DatePicker) dialog
								.findViewById(R.id.datestart);
						final TimePicker starttime = (TimePicker) dialog
								.findViewById(R.id.timestart);
						final DatePicker stopdate = (DatePicker) dialog
								.findViewById(R.id.datestop);
						final TimePicker stoptime = (TimePicker) dialog
								.findViewById(R.id.timestop);
						if (loadPreferences("timeall").equalsIgnoreCase("true")) {
							timeall.setChecked(true);
						} else {
							timeall.setChecked(false);
						}
						timeall.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								if (timeall.isChecked()) {
									savePreferences("timeall", "true");
								} else {
									savePreferences("timeall", "false");
								}
							}
						});
						startdate.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
							}
						});
						starttime.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
							}
						});
						stopdate.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
							}
						});
						stoptime.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
							}
						});
						save.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (timeall.isChecked() == true) {
									SimpleDateFormat format = new SimpleDateFormat(
											"yyMMddHHmm");
									Calendar calender = Calendar.getInstance();
									calender.set(startdate.getYear(),
											startdate.getMonth(),
											startdate.getDayOfMonth(),
											starttime.getCurrentHour(),
											starttime.getCurrentMinute());
									String start = format.format(calender
											.getTime()) + "00";
									savePreferences("Start", start);
									calender.set(stopdate.getYear(),
											stopdate.getMonth(),
											stopdate.getDayOfMonth(),
											stoptime.getCurrentHour(),
											stoptime.getCurrentMinute());
									String stop = format.format(calender
											.getTime()) + "00";
									savePreferences("Stop", stop);
								}
								dialog.dismiss();
							}
						});
						return true;
					}
				});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private void savePreferences(String key, String value) {
		SharedPreferences preferences = getSharedPreferences(
				Settings.PREF_FILE_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	private String loadPreferences(String key) {
		SharedPreferences preferences = getSharedPreferences(
				Settings.PREF_FILE_NAME, MODE_PRIVATE);
		String save = preferences.getString(key, "");
		return save;
	}

}
