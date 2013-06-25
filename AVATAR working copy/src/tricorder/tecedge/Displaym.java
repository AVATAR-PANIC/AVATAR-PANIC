package tricorder.tecedge;

import gupta.ashutosh.avatar.R;

import java.io.InputStream;
import java.net.URL;

import tricorder.tecedge.Mapm.DownloadFilesTask;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Displaym extends Activity {
	private String condition;
	private String type;
	private String data;
	private String[] result;
	public int lat, lon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tricorder_displaym);
		try{
		Bundle extras = getIntent().getExtras();
		type = extras.getString("Type");
		data = extras.getString("Data");
		result = data.split("\n");
		}catch(Exception ex){
			
		}

		// if (type.equalsIgnoreCase("Image")) {
		initImage();

		TextView sensortype = (TextView) findViewById(R.id.textView1);
		TextView info = (TextView) findViewById(R.id.textView2);
		sensortype.setText(loadPreferences("locPref"));
		info.setText(data);
	}

	String sensorinfo(String lat, String lon) {
		PHPScriptQuery database = new PHPScriptQuery();
		condition = "WHERE sensor_gpsLat = " + lat + " AND sensor_gpsLong = "
				+ lon;
		String pointinfo = database.execute(condition);
		return pointinfo;
	}

	private void initImage() {
		ImageView iv = (ImageView) findViewById(R.id.imageView1);
		iv.setVisibility(View.VISIBLE);
		try {
			InputStream is = (InputStream) new URL(
					"http://wbi-icc.com/students/SL/Tricorder/Pictures/"
							+ result[0] + result[1] + ".jpg").getContent();
			Drawable image = Drawable.createFromStream(is, "src");
			iv.setImageDrawable(image);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "no image",
					Toast.LENGTH_LONG).show();
		}
	}

	private String loadPreferences(String key) {
		SharedPreferences preferences = getSharedPreferences(
				Settings.PREF_FILE_NAME, MODE_PRIVATE);
		String save = preferences.getString(key, "");
		return save;
	}

}
