package com.guardian_angel.uav_tracker;


import gupta.ashutosh.avatar.R;

import java.io.File;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;

public class UserData extends Activity {

	private Button done;
	private RadioButton silent;
	private RadioButton quiet;
	private RadioButton loud;
	private RadioButton small;
	private RadioButton medium;
	private RadioButton large;
	private RadioButton fifty;
	private RadioButton oneHundred;
	private RadioButton twoHundred;
	private EditText messageText;
	Bundle extras;
	
	private String volume;
	private String size;
	private String distance;
	private String message;
	
	private XMPPSender xmppSender;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guardian_angel_user_data);

		// initialize and instantiate variables
		silent = (RadioButton) findViewById(R.id.silent);
		quiet = (RadioButton) findViewById(R.id.quiet);
		loud = (RadioButton) findViewById(R.id.loud);
		small = (RadioButton) findViewById(R.id.small);
		medium = (RadioButton) findViewById(R.id.medium);
		large = (RadioButton) findViewById(R.id.large);
		fifty = (RadioButton) findViewById(R.id.ten);
		oneHundred = (RadioButton) findViewById(R.id.fifty);
		twoHundred = (RadioButton) findViewById(R.id.onehundred);
		done = (Button) findViewById(R.id.done_button);
		messageText = (EditText) findViewById(R.id.landmarkanswer);
		
		
		// set the listener for the done button
		done.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				message = messageText.getText().toString();
				if (message.equals(""))
				{
					message = "null";
				}
				
				String features = "Volume=\"" + volume + "\" Size=\"" + size
						+ "\" Distance=\"" + distance + "\"";
				
				Date date = new Date();
				String dateString = date.toGMTString();
				
				File savedFile = new File(Environment.getExternalStorageDirectory().getPath() + "/UAV_T/Images/uav_pic.jpg");
				if(savedFile.exists())
				{
					xmppSender = new XMPPSender(dateString, features, message);
					xmppSender.createMessage();
					xmppSender.sendPicture();
				}
				else
				{
					xmppSender = new XMPPSender(dateString, features, message);
					try{
						xmppSender.createMessage();
						xmppSender.sendMessage();
					}catch(Exception ex){
						Toast.makeText(getApplicationContext(), "There was a problem sending the data", Toast.LENGTH_SHORT).show();
					}
				}
				
				
//				Intent nextScreen = new Intent(getApplicationContext(),
//						MainActivity.class);
//				nextScreen.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				startActivity(nextScreen);
				
				finish();
			}
		});
		
		

		silent.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (silent.isChecked())	{
					volume = "silent";
				} else {
					volume = "";
				}
			
			}
		});

		quiet.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					if (quiet.isChecked())	{
						volume = "quiet";
					} else {
						volume = "";
					}
			}
		});
		
		loud.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					if (loud.isChecked())	{
						volume = "loud";
					} else {
						volume = "";
					}
			}
		});
		
		small.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (small.isChecked())	{
					size = "small";
				} else {
					size = "";
				}
			}
		});
		
		medium.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (medium.isChecked())	{
					size = "medium";
				} else {
					size = "";
				}
			}
		});
		
		large.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (large.isChecked())	{
					size = "large";
				} else {
					size = "";
				}
			}
		});
		
		fifty.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (fifty.isChecked())	{
					distance = "50";
				} else {
					distance = "";
				}
			}
		});
		
		oneHundred.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (oneHundred.isChecked())	{
					distance = "100";
				} else {
					distance = "";
				}
			}
		});
		
		twoHundred.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (twoHundred.isChecked())	{
					distance = "200+";
				} else {
					distance = "";
				}
			}
		});

	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		finish();
	}
	
	@Override
	public void onBackPressed()
	{
		finish();
	}

}
