package sate2012.avatar.android.googlemaps;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerPlus{
	
	private MarkerOptions markerOptions = new MarkerOptions();
	private String name;
	private String data;
	private double altitude;
	private Drawable image;
	
	public MarkerPlus(){
		this.markerOptions = new MarkerOptions();
		markerOptions.position(new LatLng(0, 0));
	}
	
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

	public void setLatitude(double latitude){
		markerOptions.position(new LatLng(latitude, markerOptions.getPosition().longitude));
	}
	public void setLongitude(double longitude){
		markerOptions.position(new LatLng(markerOptions.getPosition().latitude, longitude));
	}
	
	public double getLatitude()
	{
		return markerOptions.getPosition().latitude;
	}
	
	public double getLongitude()
	{
		return markerOptions.getPosition().longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public Drawable getImage() {
		return image;
	}

	public void setImage(Drawable image) {
		this.image = image;
	}

	@Override
	public String toString(){
		return " Title: " + this.name + " Description: " + this.data + " Altitude: " + this.altitude;
	}
}

