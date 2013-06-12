package sate2012.avatar.android;

import gupta.ashutosh.avatar.R;
import sate2012.avatar.android.augmentedrealityview.CameraView;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayer extends Activity {

	@Override
	public void onCreate(Bundle b){
		super.onCreate(b);
//		Intent i = getIntent();
//  	String path = i.getStringExtra("video_tag");
		setContentView(R.layout.video_player_layout);
//		
//		VideoView videoView = (VideoView) findViewById(R.id.video_player);
//		videoView.setKeepScreenOn(true);
//		
//		if(path != null) videoView.setVideoURI(Uri.parse(path));
//		
//		MediaController mediaController = new MediaController(this);
//		mediaController.setAnchorView(videoView);
//		videoView.setMediaController(mediaController);
//		if(videoView.canSeekForward()){
//			videoView.seekTo(videoView.getDuration()/2);
//		}
//		
//		videoView.start();
		
		//VideoView videoView = (VideoView) findViewById(R.id.video_player);
		//videoView.setVideoURI(Uri.parse("http://commonsware.com/misc/test2.3gp"));
		//videoView.start();
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video_player_view, menu);
		return true;
	}
	
	/**
	 * Method that determines what happens depending on which button was clicked
	 * 
	 * @param v
	 *            : The view
	 */
	public void myClickMethod(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.map:
			i = new Intent(getApplicationContext(), MapsForgeMapViewer.class);
			startActivity(i);
			break;
		case R.id.augmentedReality:
			i = new Intent(getApplicationContext(), CameraView.class);
			startActivity(i);
			break;
		case R.id.emergencyCall:
			i = new Intent(getApplicationContext(), PhoneCall.class);
			break;
		case R.id.exit:
			System.exit(0);
			break;
		}
	}
	
	@Override
	/**
	 * onDestroy stops or "destroys" the program when this actions is called.
	 */
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
	}
	
}
