package sate2012.avatar.android.googlemaps;

import java.util.ArrayList;

import android.graphics.Point;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * 
 * @author Garrett Emrick emrickgarrett@gmail.com
 * This class groupClusters the points on the map based on pixel distance. 
 * 
 * If you want to make the radius larger, edit the constant below.
 */
public class GoogleMapsClusterMaker extends AsyncTask<String, Void, ArrayList<GoogleMapsClusterMarker>> {

	//Temp points to prevent from tampering with the points in the main array
	public ArrayList<MarkerPlus> tempPoints;
	//Distance in which the points should cluster
	private final double MAXDISTANCEPIXELS = 40.0;
	
	//AsyncTask Variables (Necessary)
	private ArrayList<MarkerPlus> points;
	private GoogleMapsViewer map;
	private LatLngBounds bounds;
	private boolean shouldClear;
	private Projection projection;
	//Perhaps need to find a way to include the bounds of the map?
	
	/**
	 * Constructor, gets all necessary requirements to cluster and draw the points
	 */
	public GoogleMapsClusterMaker(ArrayList<MarkerPlus> points, GoogleMapsViewer map, Projection projection, boolean shouldClear){
		this.points = points;
		this.map = map;
		this.bounds = bounds;
		this.projection = projection;
		this.shouldClear = shouldClear;

	}
	
	/**
	 * Calculates the pixel distance between two points using the projection of the map
	 * @param c1 : The first point/Cluster
	 * @param c2 : The second point/Cluster
	 * @param projection : The projection of the map
	 * @return The distance (double) in pixels between the two points
	 */
	public double pixelDistance(GoogleMapsClusterMarker c1, GoogleMapsClusterMarker c2, Projection projection){
		
		Point p1 = projection.toScreenLocation(c1.latlng);
		Point p2 = projection.toScreenLocation(c2.latlng);
		
		return Math.sqrt(((p1.x - p2.x) * (p1.x - p2.x)) + ((p1.y - p2.y) * (p1.y - p2.y))); 
	}
	
	/**
	 * Helper method, merges the two Cluster Marker objects and returns the final version.
	 * @param c1 : The first cluster to be merged.
	 * @param c2 : The second cluster to be merged.
	 * @return A combination of the two groupClusters, no repeats.
	 */
	public GoogleMapsClusterMarker mergegroupClusters(GoogleMapsClusterMarker c1, GoogleMapsClusterMarker c2){
		
		GoogleMapsClusterMarker cluster = new GoogleMapsClusterMarker();
		ArrayList<MarkerPlus> points = new ArrayList<MarkerPlus>();
		
		//System.out.println("Cluster 1 Size: " + c1.getPoints().size() + "\nCluster 2 Size: " + c2.getPoints().size());
		
		for(MarkerPlus point: c1.getPoints()){
			//if(!points.contains(point)){
				points.add(point);
			//}
		}
		for(MarkerPlus point: c2.getPoints()){
			//if(!points.contains(point)){
				points.add(point);
			//}
		}
		
		cluster.setPoints(points);
		
		return cluster; //Merged Cluster
	}
	
	/**
	 * Does this in the background of the application, creates the clusters
	 */
	public ArrayList<GoogleMapsClusterMarker> doInBackground(String... args){
		
		double pixelDistance;
		ArrayList<GoogleMapsClusterMarker> groupClusters = new ArrayList<GoogleMapsClusterMarker>();
		ArrayList<GoogleMapsClusterMarker> allClusters = new ArrayList<GoogleMapsClusterMarker>();
		tempPoints = new ArrayList<MarkerPlus>(points); //Prevent tampering with the points, Copy Constructor
		//double tempMAXDISTANCE =(MAXDISTANCE * Math.pow(.5, zoomLevel));
		
		//Put all points within their own groupClusters for comparison
		for(int i = 0; i < tempPoints.size(); i++){
			MarkerPlus tempPoint = tempPoints.get(i);
			if(!tempPoint.getName().equals("EMERGENCY")){
				groupClusters.add(new GoogleMapsClusterMarker());
				groupClusters.get(groupClusters.size()-1).addPoint(tempPoint);
			}
			else{
				allClusters.add(new GoogleMapsClusterMarker());
				allClusters.get(allClusters.size()-1).addPoint(tempPoint);
			}
		}
			
			//Boolean used to determine if it needs to restart the cluster comparison from 0
			boolean wasMerged = false;
			//Loop to compare groupClusters
			for(int i = 0; i < groupClusters.size()-1; i++){
				
				//If there was a merge, restart at 0 to begin comparisons
				if(wasMerged){
					i = 0;
					wasMerged = false;
				}
				
				for(int j = i+1; j < groupClusters.size(); j++){
					//System.out.println("I: " + i + " | J: " + j);
					pixelDistance = pixelDistance(groupClusters.get(i), groupClusters.get(j), projection);
//					System.out.println("Pixel Distance: " + pixelDistance);
//					System.out.println("Max Distance: " + MAXDISTANCE * Math.pow(.5, zoomLevel));
//					System.out.println("Point Names 1: " + groupClusters.get(i).getPointNames());
//					System.out.println("Point Names 2: " + groupClusters.get(j).getPointNames());
					
					if(pixelDistance < MAXDISTANCEPIXELS && groupClusters.get(i).equals(groupClusters.get(j)) == false){
						groupClusters.add(mergegroupClusters(groupClusters.get(i), groupClusters.get(j)));
						groupClusters.remove(j);
						groupClusters.remove(i);
						
						wasMerged = true;

						
						//System.out.println("Point added!");
						
					}
					
				}
				
			}
			
		for(int i = 0; i < groupClusters.size(); i++){
			allClusters.add(groupClusters.get(i));
		}
		
		
		return allClusters;
	}
	
	/**
	 * When the thread finishes, this is the last method called
	 */
	protected void onPostExecute(ArrayList<GoogleMapsClusterMarker> clusters){
		
		map.drawClusters(clusters, shouldClear);
		
	}
	
}