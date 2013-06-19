package sate2012.avatar.android;

import gupta.ashutosh.avatar.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * 
 * @author Garrett emrickgarrett@gmail.com 
 * 
 * Modified version of Matt's original Fragment class. This class takes the 
 * ID of the xml layout to determine which buttons should be present on the menu.
 * Will not work with a default Constructor, so don't use.
 *
 */
@SuppressLint("ValidFragment")
public class Frag extends Fragment {

	private int menu_ID;
	
	/**
	 * Constructor
	 * @param menu_ID : The ID of the layout
	 */
	public Frag(int menu_ID){
		this.menu_ID = menu_ID;
	}
	
	public Frag(){
		this.menu_ID = R.layout.map_menu_frag;
	}
	
	/**
	 * When the view for the fragment is created, shows this first.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(menu_ID, container, false);
		return view;
	}

}
