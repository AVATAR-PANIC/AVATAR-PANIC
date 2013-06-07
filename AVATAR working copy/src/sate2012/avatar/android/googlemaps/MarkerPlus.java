package sate2012.avatar.android.googlemaps;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerPlus {
	
	private MarkerOptions markerOptions = new MarkerOptions();
	private String info;
	private double altitude;
	
	public MarkerPlus(MarkerOptions marker, double altitude, String info) {
		this.markerOptions = marker;
		this.info = info;
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
		this.info = info;
	}

	public MarkerOptions getMarkerOptions() {
		return markerOptions;
	}

	public void setMarkerOptions(MarkerOptions markerOptions) {
		this.markerOptions = markerOptions;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
}
