package sate2012.avatar.android;

import sate2012.avatar.android.augmentedrealityview.CameraView;
import sate2012.avatar.android.googlemaps.GoogleMapsViewer;
import gupta.ashutosh.avatar.R;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Frag extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu_frag, container, false);
		return view;
	}

}
