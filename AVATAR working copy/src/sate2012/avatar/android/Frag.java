package sate2012.avatar.android;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint("ValidFragment")
public class Frag extends Fragment {

	private int menu_ID;
	
	public Frag(int menu_ID){
		this.menu_ID = menu_ID;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(menu_ID, container, false);
		return view;
	}

}
