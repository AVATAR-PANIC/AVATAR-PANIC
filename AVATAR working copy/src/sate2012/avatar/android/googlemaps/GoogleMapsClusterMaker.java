package sate2012.avatar.android.googlemaps;

import java.util.ArrayList;

import org.mapsforge.android.maps.MapView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * 
 * @author Garrett Emrick emrickgarrett@gmail.com
 * This class groupClusters the points on the map based on distance. 
 * 
 * If you want to change the distance points will cluster, I recommend changing 
 * the multiplication factor in the MAXDISTANCE variable below.
 * The value for MAXDISTANCE (1.89.....E7) is the distance between points
 * of 1 latitude & longitude difference with zoom level of 4.
 * 
 * This ClusterMaker is called whenever the zoom level of the GoogleMapsViewer is changed
 */
public class GoogleMapsClusterMaker {

	//Temp points to prevent from tampering with the points in the main array
	public ArrayList<MarkerPlus> tempPoints;
	//Distance in which the points should cluster
	private final double MAXDISTANCE = 1.8934702197223254E7*64; // 64 is the current Multiplication factor
	
	//Offset and Radius
	private static final double OFFSET = 268435456;
	private static final double RADIUS = 85445659.4471;
	
	
	/**
	 * Default Constructor
	 */
	public GoogleMapsClusterMaker(){
		
	}

	/**
	 * This method groupClusters all points, and points will always be in a cluster as well to allow this to 
	 * work effectively, and maintain simplicity.
	 * 
	 * @param zoomLevel : Current Zoom Level of the Google Maps Class
	 * @param points : Points to be clustered
	 * @return Array of GoogleMapsClusterMarker Objects
	 */
	public ArrayList<GoogleMapsClusterMarker> generateClusters(float zoomLevel, ArrayList<MarkerPlus> points, LatLngBounds bounds){
		//System.out.println(MAXDISTANCE / zoomLevel*.5);
		double pixelDistance;
		ArrayList<GoogleMapsClusterMarker> groupClusters = new ArrayList<GoogleMapsClusterMarker>();
		ArrayList<GoogleMapsClusterMarker> allClusters = new ArrayList<GoogleMapsClusterMarker>();
		tempPoints = new ArrayList<MarkerPlus>(points); //Prevent tampering with the points, Copy Constructor
		double tempMAXDISTANCE =(MAXDISTANCE * Math.pow(.5, zoomLevel));
		
		//Put all points within their own groupClusters for comparison
		for(int i = 0; i < tempPoints.size(); i++){
			MarkerPlus tempPoint = tempPoints.get(i);
			//if(bounds.contains(new LatLng(tempPoint.getLatitude(), tempPoint.getLongitude()))){
				groupClusters.add(new GoogleMapsClusterMarker());
				groupClusters.get(groupClusters.size()-1).addPoint(tempPoint);
			//}
//			else{
//				allClusters.add(new GoogleMapsClusterMarker());
//				allClusters.get(allClusters.size()-1).addPoint(tempPoint);
//			}
		}
		
		if(zoomLevel <= 18){
			
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
					pixelDistance = pixelDistance(groupClusters.get(i), groupClusters.get(j), zoomLevel);
//					System.out.println("Pixel Distance: " + pixelDistance);
//					System.out.println("Max Distance: " + MAXDISTANCE * Math.pow(.5, zoomLevel));
//					System.out.println("Point Names 1: " + groupClusters.get(i).getPointNames());
//					System.out.println("Point Names 2: " + groupClusters.get(j).getPointNames());
					
					if(pixelDistance < tempMAXDISTANCE && groupClusters.get(i).equals(groupClusters.get(j)) == false){
						groupClusters.add(mergegroupClusters(groupClusters.get(i), groupClusters.get(j)));
						groupClusters.remove(j);
						groupClusters.remove(i);
						
						wasMerged = true;
						j=0;

						
						//System.out.println("Point added!");
						
					}
					
				}
				
			}
			
		}
		
//		for(int i = 0; i < groupClusters.size(); i++){
//			allClusters.add(groupClusters.get(i));
//		}
		

		return groupClusters; //Clustered Points
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
			if(!points.contains(point)){
				points.add(point);
			}
		}
		for(MarkerPlus point: c2.getPoints()){
			if(!points.contains(point)){
				points.add(point);
			}
		}
		
		cluster.setPoints(points);
		
		return cluster; //Merged Cluster
	}
	
	/**
	 * Converts to an X value
	 * @param lon : Longitude
	 * @return X value
	 */
	private double lonToX(double lon) {
		return Math.round(OFFSET - RADIUS * lon * Math.PI / 180);
	}

	/**
	 * Converts to a Y value
	 * @param lat : Latitude
	 * @return Y value
	 */
	private double latToY(double lat) {
		return Math.round(OFFSET
				- RADIUS
				* Math.log((1 + Math.sin(lat * Math.PI / 180))
						/ (1 - Math.sin(lat * Math.PI / 180))) / 2);
	}
	
	/**
	 * Pretty much taken from the ClusterMaker for the original maps, with some adjustments
	 * @param c1 : First cluster
	 * @param c2 : Second cluster
	 * @param zoom : zoom level
	 * @return : Supposedly distance in pixels? I'd say it's an imaginary unit of some kind. Combination of distance and pixels/zoom? - Garrett
	 */
	private double pixelDistance(GoogleMapsClusterMarker c1, GoogleMapsClusterMarker c2, float zoom){
		// this might not work... i don't know if a byte can be converted into
		// an int
		double x1 = lonToX(c1.lon);
		double y1 = latToY(c1.lat);

		double x2 = lonToX(c2.lon);
		double y2 = latToY(c2.lat);
		// this will calculate the pixel distance of the points on the screen
		return Math.sqrt((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2))
				/ Math.pow(2, (21 - zoom));
		// the php script references a bitshift operator >>. THe equivalent
		// statement is implemented above.
	}
	
	private double pixelDistance(GoogleMapsClusterMarker c1, MarkerPlus p, float zoom){
		// this might not work... i don't know if a byte can be converted into
				// an int
				double x1 = lonToX(c1.lon);
				double y1 = latToY(c1.lat);

				double x2 = lonToX(p.getLongitude());
				double y2 = latToY(p.getLatitude());
				// this will calculate the pixel distance of the points on the screen
				return Math.sqrt((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2))
						/ Math.pow(2, (21 - zoom));
				// the php script references a bitshift operator >>. THe equivalent
				// statement is implemented above.
		
	}
	
}
