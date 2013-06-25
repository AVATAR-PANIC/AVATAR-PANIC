package com.guardian_angel.uav_tracker;

import gupta.ashutosh.avatar.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GAdirections extends Activity {
	protected Button back;
	private String readme = "";
	private TextView directions;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guardian_angel_directions);
		directions = (TextView) findViewById(R.id.readme);

		back = (Button) findViewById(R.id.back);
		readme = readTxt();
		directions.setText(readme);

		back.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent nextScreen = new Intent(getApplicationContext(),
						MainActivity.class);
				nextScreen.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(nextScreen);
				finish();
			}
		});
	}

	private String readTxt() {
//
//		InputStream inputStream = getResources().openRawResource(R.raw.readme);
//
//		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//		int i;
//		try {
//			i = inputStream.read();
//			while (i != -1) {
//				byteArrayOutputStream.write(i);
//				i = inputStream.read();
//			}
//			inputStream.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return byteArrayOutputStream.toString();
		return "";
	}
}