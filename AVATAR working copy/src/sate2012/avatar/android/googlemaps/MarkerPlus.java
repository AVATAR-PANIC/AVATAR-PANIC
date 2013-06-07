package sate2012.avatar.android.googlemaps;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerPlus {
	
	private MarkerOptions markerOptions = new MarkerOptions();
	private String data;
	private double altitude;
	
	public MarkerPlus(MarkerOptions marker, double altitude, String info) {
		this.markerOptions = marker;
		this.data = info;
		this.altitude = altitude;
	}
	
	public MarkerPlus(MarkerOptions markerOptions, double altitude) {
		this.markerOptions = markerOptions;
		this.altitude = altitude;
	}
	
	public MarkerPlus(MarkerOptions markerOptions) {
		this.markerOptions = markerOptions;
	}
	
	public MarkerPlus(double latitude, double longitude){
		markerOptions.position(new LatLng(latitude, longitude));
	}
	
	public MarkerPlus(double latitude, double longitude, double altitude){
		this(latitude, longitude);
		this.altitude = altitude;
	}
	public MarkerPlus(double latitude, double longitude, double altitude, String info){
		this(latitude, longitude, altitude);
		this.data = info;
	}

	public MarkerOptions getMarkerOptions() {
		return markerOptions;
	}

	public void setMarkerOptions(MarkerOptions markerOptions) {
		this.markerOptions = markerOptions;
	}

	public String getInfo() {
		return data;
	}

	public void setInfo(String info) {
		this.data = info;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
}
