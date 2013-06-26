package sate2012.avatar.android.googlemaps;

import gupta.ashutosh.avatar.R;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * 
 * @author Garrett - emrickgarrett@gmail.com
 * 
 * This class is very similar to the MarkerPlus class used in the AVATAR 
 * Google Maps viewer. This one has more info though, which is needed
 * for the points used in the Tricorder Google Maps Viewer.
 *
 */
public class TricorderMarkerPlus extends MarkerPlus {

	private MarkerOptions markerOptions = new MarkerOptions();
	private String name;
	private String data;
	private double altitude;
	private int imageID = R.drawable.emergency;
	private String info;
	private String type;
	private String date;
	private String phone_ID;
	
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
	
	public TricorderMarkerPlus(double latitude, double longitude, String info, String type, String date, String phone_ID){
		this.markerOptions = new MarkerOptions();
		this.markerOptions.position(new LatLng(latitude, longitude));
		this.data = info;
		this.type = type;
		this.date = date;
		this.phone_ID = phone_ID;
	}
	
	public TricorderMarkerPlus(double latitude, double longitude, String info, String type, String date, String phone_ID, int imageID){
		this.markerOptions = new MarkerOptions();
		this.markerOptions.position(new LatLng(latitude, longitude));
		this.data = info;
		this.type = type;
		this.date = date;
		this.phone_ID = phone_ID;
		this.imageID = imageID;
	}

	public MarkerOptions getMarkerOptions() {
		this.markerOptions.title(this.type).snippet(data).icon(BitmapDescriptorFactory.fromResource(imageID));
		return markerOptions;
	}

	public void setMarkerOptions(MarkerOptions markerOptions) {
		this.markerOptions = markerOptions;
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

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public int getImageID() {
		return imageID;
	}

	public void setImageID(int imageID) {
		this.imageID = imageID;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
