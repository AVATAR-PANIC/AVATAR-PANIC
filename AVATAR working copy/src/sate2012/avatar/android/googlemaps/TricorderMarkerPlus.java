package sate2012.avatar.android.googlemaps;

import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TricorderMarkerPlus extends MarkerPlus {

	private MarkerOptions markerOptions = new MarkerOptions();
	private String name;
	private String data;
	private double altitude;
	private Drawable image;
	private String type;
	
	/**
	 * Default Constructor
	 */
	public TricorderMarkerPlus(){
		this.markerOptions = new MarkerOptions();
		markerOptions.position(new LatLng(0, 0));
	}
	
	//Other constructors below.
	public TricorderMarkerPlus(MarkerOptions marker, double altitude, String info) {
		this.markerOptions = marker;
		this.data = info;
		this.altitude = altitude;
	}
	
	public TricorderMarkerPlus(MarkerOptions markerOptions, double altitude) {
		this.markerOptions = markerOptions;
		this.altitude = altitude;
	}
	
	public TricorderMarkerPlus(MarkerOptions markerOptions) {
		this.markerOptions = markerOptions;
	}
	
	public TricorderMarkerPlus(double latitude, double longitude){
		markerOptions.position(new LatLng(latitude, longitude));
	}
	
	public TricorderMarkerPlus(double latitude, double longitude, double altitude){
		this(latitude, longitude);
		this.altitude = altitude;
	}
	public TricorderMarkerPlus(double latitude, double longitude, double altitude, String info){
		this(latitude, longitude, altitude);
		this.data = info;
	}
	
	public TricorderMarkerPlus(MarkerOptions markerOptions, double latitude, double longitude, double altitude, String info, String type, int imageID){
		this.markerOptions = markerOptions;
		this.markerOptions.position(new LatLng(latitude, longitude));
		this.altitude = altitude;
		this.data = info;
		this.type = type;
	    
		
	}
	
}
