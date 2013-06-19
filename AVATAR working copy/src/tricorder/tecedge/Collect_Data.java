package tricorder.tecedge;

import gupta.ashutosh.avatar.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Collect_Data extends Activity {

	int position = 0;

	Boolean beginning = null;
	static boolean full = false;
	static String type = "";
	int x1, x2, dx;
	static float Thermdata[];
	static String Thermdata_time[];
	static float luxdata[];
	static String luxdata_time[];
	static float pressdata[];
	static String pressdata_time[];
	static float winddata[];
	static String winddata_time[];
	static boolean refreshed = false;
	static float data[];
	static String data_time[];

	private FrameLayout thermg;
	private FrameLayout pressg;
	private FrameLayout luxaneg;
	private FrameLayout windg;

	private LinearLayout thermlayout;
	private LinearLayout presslayout;
	private LinearLayout luxlayout;
	private LinearLayout windlayout;

	private TextView thermtext;
	private TextView presstext;
	private TextView luxtext;
	private TextView windtext;

	static boolean thermtable = false;
	static boolean windtable = false;
	static boolean luxtable = false;
	static boolean presstable = false;

	DragableSpace space;

	DataHandler database = new DataHandler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("Position", "1");

		Graph.screen = 2;

		if (!refreshed) {
			Update.run();
			refreshed = true;
		}

		// setContentView(R.layout.collect_data);

		space = (DragableSpace) findViewById(R.id.space);
		thermlayout = (LinearLayout) findViewById(R.id.thermlayout);
		presslayout = (LinearLayout) findViewById(R.id.presslayout);
		luxlayout = (LinearLayout) findViewById(R.id.luxlayout);
		windlayout = (LinearLayout) findViewById(R.id.windlayout);

		Log.d("Position", "1.12");
		if (!thermtable) {
			space.removeView(thermlayout);
		}
		if (!presstable) {
			space.removeView(presslayout);
		}
		if (!luxtable) {
			space.removeView(luxlayout);
		}
		if (!windtable) {
			space.removeView(windlayout);
		}
		Log.d("Position", "1.3");
		setonclick.run();
		Log.d("Position", "2");
		TableCreate.run();
		Log.d("Position", "3");
	}

	Runnable setonclick = new Runnable() {

		public void run() {
			if (thermtable) {
				thermg = (FrameLayout) findViewById(R.id.thermg);
				thermg.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						data = Thermdata;
						data_time = Thermdata_time;
						full = true;
						startActivity(new Intent("com.tricorder.FULL_GRAPH"));
					}
				});
			}
			if (presstable) {
				pressg = (FrameLayout) findViewById(R.id.pressg);
				pressg.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {

						data = pressdata;
						data_time = pressdata_time;
						full = true;
						startActivity(new Intent("com.tricorder.FULL_GRAPH"));

					}
				});
			}
			if (luxtable) {
				luxaneg = (FrameLayout) findViewById(R.id.luxaneg);
				luxaneg.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {

						data = luxdata;
						data_time = luxdata_time;
						full = true;
						startActivity(new Intent("com.tricorder.FULL_GRAPH"));

					}
				});
			}
			if (windtable) {
				windg = (FrameLayout) findViewById(R.id.windg);
				windg.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {

						data = winddata;
						data_time = winddata_time;
						full = true;
						startActivity(new Intent("com.tricorder.FULL_GRAPH"));

					}
				});
			}

		}

	};

	Runnable Update = new Runnable() {
		public void run() {
			luxdata_time = database.getDate("Luminosity", "0");
			luxdata = database.getData("Luminosity", "0");
			Thermdata_time = database.getDate("Temperature", "0");
			Thermdata = database.getData("Temperature", "0");
			pressdata_time = database.getDate("Pressure", "0");
			pressdata = database.getData("Pressure", "0");
			winddata_time = database.getDate("Wind", "0");
			winddata = database.getData("Wind", "0");
		}
	};

	Runnable TableCreate = new Runnable() {
		public void run() {

			// Table for Thermometer
			if (thermtable) {
				LinearLayout ThermTableHolder = (LinearLayout) findViewById(R.id.ThermT);
				TableLayout ThermLayout = new TableLayout(Collect_Data.this);
				ThermLayout.setLayoutParams(new TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT,
						TableRow.LayoutParams.MATCH_PARENT));
				ThermLayout.setStretchAllColumns(true);
				List<TableRow> thermrows = new ArrayList<TableRow>();
				List<TextView> thermtext1 = new ArrayList<TextView>();
				List<TextView> thermtext2 = new ArrayList<TextView>();
				for (int i = 0; i < Array.getLength(Thermdata); i++) {
					// Create the second row and add 2 text views
					thermrows.add(new TableRow(Collect_Data.this));
					thermtext1.add(new TextView(Collect_Data.this));
					thermtext1.get(i).setText(
							Graph.readabledate(Thermdata_time[i]));
					thermtext2.add(new TextView(Collect_Data.this));
					thermtext2.get(i).setText(Float.toString(Thermdata[i]));
					thermtext2.get(i).setGravity(android.view.Gravity.CENTER);
					thermtext1.get(i).setGravity(android.view.Gravity.CENTER);
					thermrows.get(i).addView(thermtext1.get(i));
					thermrows.get(i).addView(thermtext2.get(i));
					ThermLayout.addView(thermrows.get(i));
				}
				ThermTableHolder.addView(ThermLayout);
			}
			// End of Thermometer Table

			// Table for Wind
			if (windtable) {
				LinearLayout windTableHolder = (LinearLayout) findViewById(R.id.windT);
				TableLayout windLayout = new TableLayout(Collect_Data.this);
				windLayout.setLayoutParams(new TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT,
						TableRow.LayoutParams.MATCH_PARENT));
				windLayout.setStretchAllColumns(true);
				List<TableRow> windrows = new ArrayList<TableRow>();
				List<TextView> windtext1 = new ArrayList<TextView>();
				List<TextView> windtext2 = new ArrayList<TextView>();

				for (int i = 0; i < Array.getLength(winddata); i++) {
					// Create the second row and add 2 text views
					windrows.add(new TableRow(Collect_Data.this));
					windtext1.add(new TextView(Collect_Data.this));
					windtext1.get(i).setText(
							Graph.readabledate(winddata_time[i]));
					windtext2.add(new TextView(Collect_Data.this));
					windtext2.get(i).setText(Float.toString(winddata[i]));
					windtext2.get(i).setGravity(android.view.Gravity.CENTER);
					windtext1.get(i).setGravity(android.view.Gravity.CENTER);
					windrows.get(i).addView(windtext1.get(i));
					windrows.get(i).addView(windtext2.get(i));
					windLayout.addView(windrows.get(i));
				}
				windTableHolder.addView(windLayout);
			}
			// End of Wind Table

			// Table for lux
			if (luxtable) {
				LinearLayout luxTableHolder = (LinearLayout) findViewById(R.id.luxT);
				TableLayout luxLayout = new TableLayout(Collect_Data.this);
				luxLayout.setLayoutParams(new TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT,
						TableRow.LayoutParams.MATCH_PARENT));
				luxLayout.setStretchAllColumns(true);
				List<TableRow> luxrows = new ArrayList<TableRow>();
				List<TextView> luxtext1 = new ArrayList<TextView>();
				List<TextView> luxtext2 = new ArrayList<TextView>();
				for (int i = 0; i < Array.getLength(luxdata); i++) {
					// Create the second row and add 2 text views
					luxrows.add(new TableRow(Collect_Data.this));
					luxtext1.add(new TextView(Collect_Data.this));
					luxtext1.get(i)
							.setText(Graph.readabledate(luxdata_time[i]));
					luxtext2.add(new TextView(Collect_Data.this));
					luxtext2.get(i).setText(Float.toString(luxdata[i]));
					luxtext2.get(i).setGravity(android.view.Gravity.CENTER);
					luxtext1.get(i).setGravity(android.view.Gravity.CENTER);
					luxrows.get(i).addView(luxtext1.get(i));
					luxrows.get(i).addView(luxtext2.get(i));
					luxLayout.addView(luxrows.get(i));
				}
				luxTableHolder.addView(luxLayout);
			}
			// End of lux Table

			// Table for press
			if (presstable) {
				LinearLayout pressTableHolder = (LinearLayout) findViewById(R.id.pressT);
				TableLayout pressLayout = new TableLayout(Collect_Data.this);
				pressLayout.setLayoutParams(new TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT,
						TableRow.LayoutParams.MATCH_PARENT));
				pressLayout.setStretchAllColumns(true);
				List<TableRow> pressrows = new ArrayList<TableRow>();
				List<TextView> presstext1 = new ArrayList<TextView>();
				List<TextView> presstext2 = new ArrayList<TextView>();

				for (int i = 0; i < Array.getLength(pressdata); i++) {
					// Create the second row and add 2 text views
					pressrows.add(new TableRow(Collect_Data.this));
					presstext1.add(new TextView(Collect_Data.this));
					presstext1.get(i).setText(
							Graph.readabledate(pressdata_time[i]));
					presstext2.add(new TextView(Collect_Data.this));
					presstext2.get(i).setText(Float.toString(pressdata[i]));
					presstext2.get(i).setGravity(android.view.Gravity.CENTER);
					presstext1.get(i).setGravity(android.view.Gravity.CENTER);
					pressrows.get(i).addView(presstext1.get(i));
					pressrows.get(i).addView(presstext2.get(i));
					pressLayout.addView(pressrows.get(i));
				}
				pressTableHolder.addView(pressLayout);
			}
			// End of press Table

		}

	};

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater settings = getMenuInflater();
		settings.inflate(R.menu.opening_menu_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.killapp:
			System.exit(0);
			return true;
		case R.id.settings:
			startActivity(new Intent("tricorder.tecedge.SETTINGS"));
			return true;
		case R.id.refresh:
			refreshed = false;
			startActivity(new Intent("tricorder.tecedge.NAME_LOADER"));
			return true;
		}
		return false;

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}