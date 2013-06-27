package com.guardian_angel.uav_tracker;


import gupta.ashutosh.avatar.R;

import java.io.File;
import java.util.Date;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class UserData extends DialogFragment {

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

	public UserData(){
		
	}
	
	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.guardian_angel_user_data, container, false);
		getDialog().setTitle("Data Information");
		
		// initialize and instantiate variables
		silent = (RadioButton) view.findViewById(R.id.silent);
		quiet = (RadioButton) view.findViewById(R.id.quiet);
		loud = (RadioButton) view.findViewById(R.id.loud);
		small = (RadioButton) view.findViewById(R.id.small);
		medium = (RadioButton) view.findViewById(R.id.medium);
		large = (RadioButton) view.findViewById(R.id.large);
		fifty = (RadioButton) view.findViewById(R.id.ten);
		oneHundred = (RadioButton) view.findViewById(R.id.fifty);
		twoHundred = (RadioButton) view.findViewById(R.id.onehundred);
		done = (Button) view.findViewById(R.id.guardian_angel_done_button);
		messageText = (EditText) view.findViewById(R.id.landmarkanswer);
		
		
		// set the listener for the done button
		done.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				try{
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
							Toast.makeText(getActivity(), "There was a problem sending the data", Toast.LENGTH_SHORT).show();
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
					Toast.makeText(getActivity(), "There was a huge problem sending your data", Toast.LENGTH_SHORT).show();
				}
				
//				Intent nextScreen = new Intent(getApplicationContext(),
//						MainActivity.class);
//				nextScreen.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				startActivity(nextScreen);
				
				getDialog().cancel();
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
		
		return view;

	}

}
