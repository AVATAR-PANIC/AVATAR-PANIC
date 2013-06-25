package tricorder.tecedge;

import gupta.ashutosh.avatar.R;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class LocalGraphs extends Activity {

	static ListView listViewData, listViewTime;
	Button stop;
	ServerComm sc;
	static ArrayAdapter<String> adapterDate;
	static ArrayAdapter<String> adapterTime;
	AlertDialog.Builder builder;
	String something = "";
	PHPScriptQuery test;
	Bundle extras;
	static String[] projPassword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tricorder_localgraphs);

		sc = new ServerComm();

		listViewData = (ListView) findViewById(R.id.lvData);

		listViewTime = (ListView) findViewById(R.id.lvTime);
		stop = (Button) findViewById(R.id.bStopRead);

		adapterDate = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, ServerComm.data);

		adapterTime = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, ServerComm.timeArr);

		// Assign adapter to ListView
		listViewData.setAdapter(adapterDate);
		listViewTime.setAdapter(adapterTime);
		builder = new AlertDialog.Builder(this);

		// pass date into create event form
		String newString;
		if (savedInstanceState == null) {
			extras = getIntent().getExtras();
			if (extras == null) {
				newString = null;
			} else {
				newString = extras.getString("date");
			}
		} else {
			newString = (String) savedInstanceState.getSerializable("date");
		}

		if (newString != null) {
			// dateUpdate.setText(newString);
		}
		projPassword = ServerComm.test.getServerPassword(extras
				.getString("PROJECT"));

		stop.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				ServerComm.running = false;

				AlertDialog dialog = new AlertDialog.Builder(LocalGraphs.this)
						.create();

				dialog.setTitle("Upload?");
				dialog.setCancelable(false);
				dialog.setButton("Upload",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub

								if (ServerComm.data.size() < 2
										|| ServerComm.timeArr.size() < 2) {
									Toast myToast = Toast.makeText(
											LocalGraphs.this,
											"Record Data first.",
											Toast.LENGTH_SHORT);
									myToast.show();
								} else {
									startActivity(new Intent(
											"tricorder.tecedge.PASSWORD_REQUEST"));
									// uploadArray(ServerComm.data,
									// ServerComm.timeArr);
									finish();
								}
							}
						});

				dialog.setButton2("Go Back",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.cancel();
							}

						});

				dialog.show();
			}
		});
	}
}