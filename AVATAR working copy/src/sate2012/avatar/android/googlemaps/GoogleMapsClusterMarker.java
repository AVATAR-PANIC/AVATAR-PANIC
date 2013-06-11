package sate2012.avatar.android.googlemaps;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

/**
 * 
 * @author Garrett Emrick emrickgarrett@gmail.com
 *
 * This class is designed to hold a cluster of points, and to average out
 * the latitude and longitude to display on the map. Used with GoogleMapsClusterMaker
 * not to be confused with the GoogleMapsClusterMarker class
 */
public class GoogleMapsClusterMarker {

	private ArrayList<MarkerPlus> points = new ArrayList<MarkerPlus>();
	public double lat;
	public double lon;
	public LatLng latlng = new LatLng(0,0);
	
	/**
	 * 
	 * @param points : Points in the cluster (Not used in the program)
	 */
	public GoogleMapsClusterMarker(ArrayList<MarkerPlus> points){
		this.points = points;
		setLatLng();
		
	}
	
	/**
	 * Default Constructor
	 */
	public GoogleMapsClusterMarker(){
	}
	
	
	/**
	 *  Getter for the names of the points when displaying them on the map.
	 * @return : Point names. If the values are null, display the Point Index
	 */
	public String getPointNames(){
		String listOfNames = "";
		
		int i = 0;
		
		markerloop:
		for(MarkerPlus point: points){
			if(i != 4){
				if(point.getName() != null){
					listOfNames += point.getName() + "\n";
				}else{
					listOfNames += ("Point index: " + i + "\n");
				}
				i++;
			}else{
				listOfNames += ("Plus More...");
				break markerloop;
			}
		}
		
		if(listOfNames.equals("")){
			listOfNames = "";
			for(i = 0; i < points.size(); i++){
				listOfNames += ("Point Index: " + i + "\n");
			}
		}
		
		return listOfNames;
	}
	
	
	/**
	 * 
	 * @return : Points in cluster
	 */
	public ArrayList<MarkerPlus> getPoints(){
		return this.points;
	}
	
	
	/**
	 * Used to add a single point to the cluster
	 * @param point : Single Point in the cluster
	 */
	public void addPoint(MarkerPlus point){
		if(!this.points.contains(point)){
			this.points.add(point);
			setLatLng();
		}
	}
	
	/**
	 * Used to set the points to a new reference of points
	 * @param points : Set the points to a new reference of points
	 */
	public void setPoints(ArrayList<MarkerPlus> points){
		this.points = points;
		setLatLng();
	}
	
	
	/**
	 * Set the LatLng for the class, is the average. Needed for the GoogleMapsViewer
	 * @param points : Points in the array to set the LatLng average to.
	 */
	public void setLatLng(ArrayList<MarkerPlus> points){
		double latAverage = 0.0;
		double lonAverage = 0.0;
		
		for(int i = 0; i < points.size(); i++){
			latAverage += points.get(i).getMarkerOptions().getPosition().latitude;
			lonAverage += points.get(i).getMarkerOptions().getPosition().longitude;
		}
		
		this.lat = latAverage/points.size();
		this.lon = lonAverage/points.size();
		this.latlng = new LatLng(latAverage, lonAverage);
		
	}
	
	/**
	 * Set the LatLng for the class using the points array in the class. Needed for GoogleMapsViewer
	 */
	public void setLatLng(){
		double latAverage = 0.0;
		double lonAverage = 0.0;
		
		for(int i = 0; i < points.size(); i++){
			latAverage += points.get(i).getMarkerOptions().getPosition().latitude;
			lonAverage += points.get(i).getMarkerOptions().getPosition().longitude;
		}
		
		this.lat = latAverage/points.size();
		this.lon = lonAverage/points.size();
		this.latlng = new LatLng(this.lat, this.lon);
		
	}
	
}
