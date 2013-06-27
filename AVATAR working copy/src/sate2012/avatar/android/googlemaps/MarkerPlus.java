package sate2012.avatar.android.googlemaps;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * 
 * @author Matt
 *
 *Class used to hold our marker objects information.
 */
public class MarkerPlus{
	
	private MarkerOptions markerOptions = new MarkerOptions();
	private String name;
	private String data;
	private double altitude;
	private Drawable image;
	
	/**
	 * Default Constructor
	 */
	public MarkerPlus(){
		this.markerOptions = new MarkerOptions();
		markerOptions.position(new LatLng(0, 0));
	}
	
	//Other constructors below.
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
	
	public MarkerPlus(LatLng point) {
		this(point.latitude, point.longitude);
	}

	public MarkerOptions getMarkerOptions() {
		return markerOptions;
	}
	public void setMarkerOptions(MarkerOptions markerOptions) {
		this.markerOptions = markerOptions;
	}
	public String getDate(){
		return this.data.substring(13, this.data.indexOf("\r"));
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

	/**
	 * Overrode the String method to return a more meaningful toString() return. - Garrett
	 */
	@Override
	public String toString(){
		return " Title: " + this.name + " Description: " + this.data + " Altitude: " + this.altitude;
	}
	
	/**
	 * Determines if two markers are equal, not really used in this project.
	 * @param other : The other marker to compare this to.
	 * @return : The boolean of whether or not they are equal.
	 */
	public boolean equals(MarkerPlus other){
		
		return this.toString().equals(other.toString());
	}
	
	/**
	 * Returns the distance in miles using the Haversine formula.
	 * @param other : The other point (The user location in our case)
	 * @return : The distance in miles.
	 */
	public double getDistance(MarkerPlus other){
		double earthRadius = 3958.75;
		double lat1 = this.getLatitude();
		double lat2 = other.getLatitude();
		double lon1 = this.getLongitude();
		double lon2 = other.getLongitude();
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lon2 - lon1);
		double a = Math.pow((Math.sin(dLat/2)), 2) + Math.cos(lat1) * Math.cos(lat2)
				* Math.pow(Math.sin(dLng/2), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		//System.out.println(earthRadius * c);
		//System.out.println("MY LAT = " + lat2 + " MY LON = " + lon2);
		return earthRadius * c;
	}
}

