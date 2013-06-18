package sate2012.avatar.android;

import gupta.ashutosh.avatar.R;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * 
 * @author Garrett emrickgarrett@gmail.comj
 * 
 * Really simple class, plays a video.
 *
 */
public class VideoPlayer extends Fragment {
	
	/**
	 * Method called when the view is created.
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.video_player_layout, container, false);
	}
	
	/**
	 * Method called when the Fragment is started.
	 */
	@Override
	public void onStart(){
		super.onStart();
		Bundle b = this.getArguments();
		String path = b.getString("video_tag");
		
		VideoView videoView = (VideoView) getActivity().findViewById(R.id.video_player);
		videoView.setKeepScreenOn(true);
		
		if(path != null) videoView.setVideoURI(Uri.parse(path));
		
			MediaController mediaController = new MediaController(getActivity());
			mediaController.setAnchorView(videoView);
			videoView.setMediaController(mediaController);
			if(videoView.canSeekForward()){
				videoView.seekTo(videoView.getDuration()/2);
		}
		
		videoView.start();
		
		
	}

}
