package tricorder.tecedge;

import gupta.ashutosh.avatar.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class Name_Loader extends Activity // implements OnItemClickListener
{

	ListView projectname;
	ArrayList<String> name;
	public static String namess;
	String[] names;
	TextView textview;
	EditText edittext;
	protected Cursor cursor;
	ArrayAdapter<String> adapter;

	PHPScriptQuery database = new PHPScriptQuery();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tricorder_list_item);
		projectname = (ListView) findViewById(R.id.projectname);
		textview = new TextView(this);
		textview.setText("Projects");
		textview.setTextSize(40);
		edittext = new EditText(this);
		edittext.setHint("Search");
		projectname.addHeaderView(textview, null, false);
		projectname.addHeaderView(edittext);
		name = new ArrayList<String>();
		edittext.addTextChangedListener(filterTextWatcher);
		adapter = new ArrayAdapter<String>(Name_Loader.this,
				android.R.layout.simple_list_item_1, name);
		update.run();
		projectname.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				namess = (String) projectname.getItemAtPosition(position);

				AlertDialog dialog = new AlertDialog.Builder(Name_Loader.this)
						.create();

				dialog.setTitle("Continue?");
				dialog.setCancelable(true);
				dialog.setMessage(database.getComments(namess)[0]);
				dialog.setButton("Go", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						startActivity(new Intent(
								"tricorder.tecedge.SENSOR_PAGE"));
						Sensor_page.refreshed = false;
						finish();
					}
				});
				dialog.show();
			}
		});
	}

	private TextWatcher filterTextWatcher = new TextWatcher() {

		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			adapter.getFilter().filter(s);
		}

	};

	Runnable update = new Runnable() {
		public void run() {
			adapter.clear();
			names = database.getProjectName();
			for (int i = 0; i < Array.getLength(names) - 1; i++) {
				name.add(names[i]);
			}
			adapter.notifyDataSetChanged();
			projectname.setAdapter(adapter);
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

			update.run();
			return true;
		}
		return false;

	}

}
