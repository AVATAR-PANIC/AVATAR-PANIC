/**
 * This Activity acts as the Splash screen for the application. It serves two purposes: 1) A loading screening to show the user who contributed to the application
 * and 2) An exiting screen to tell the user that the application has fully closed. It will last for approximately 1.5 seconds and alert the user when the app is closing.
 * When it has finished, it will load up the MainActivity of the application.
 */
package com.guardian_angel.uav_tracker;

import gupta.ashutosh.avatar.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

/**
 * 
 * @author Michael J. Fox < fox.117@wright.edu >
 * @vesion 1.0
 * @since 07.29.12
 *
 */

public class Splash extends Activity{
	
	private final int SPLASH_DISPLAY_LENGTH = 1500;
	private boolean exit = false;
	
	
	@Override
	public void onCreate(Bundle icicle){
		super.onCreate(icicle);
		
		// Set it so the Splash screen will take up the whole screen on the device, removing the status bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// Set the image to take up the full screen of the device
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.guardian_angel_splash);
		
		// Check to see if there are any extras with the bundle. This is used to verify if the application is attempting to be exited
		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			// If it is trying to be exited, set the boolean to passed intent
			exit = extras.getBoolean("exit");
		}
		
		// Create the runnable for the Splash screen that will do background work
		new Handler().postDelayed(new Runnable(){
			public void run(){
				
				// When the Splash is loading the application
				if(!exit)
				{
				Intent mainIntent = new Intent(Splash.this,MainActivity.class);
				Splash.this.startActivity(mainIntent);
				Splash.this.finish();
				}
				
				// When the Splash is exiting the application
				else
				{
				//android.os.Process.killProcess(android.os.Process.myPid());
				finish();
				}
			}
		}, SPLASH_DISPLAY_LENGTH);
	}
}