package sate2012.avatar.android.googlemaps;

import java.util.ArrayList;

import org.mapsforge.android.maps.MapView;

/**
 * 
 * @author Garrett Emrick emrickgarrett@gmail.com
 * This class clusters the points on the map based on distance. 
 * This value can be changed by changing the MAXDISTANCE value
 * This ClusterMaker is called whenever the zoom level of the GoogleMapsViewer is changed
 */
public class GoogleMapsClusterMaker {

	//Temp points to prevent from tampering with the points in the main array
	public ArrayList<MarkerPlus> tempPoints;
	//Distance in which the points should cluster
	private double MAXDISTANCE = 1.8934702197223254E7*64; //= 3E8;
	
	//Offset and Radius
	private static final double OFFSET = 268435456;
	private static final double RADIUS = 85445659.4471;
	
	
	/**
	 * Default Constructor
	 */
	public GoogleMapsClusterMaker(){
		
	}

	/**
	 * This method clusters all points, and points will always be in a cluster as well to allow this to 
	 * work effectively, and maintain simplicity.
	 * 
	 * @param zoomLevel : Current Zoom Level of the Google Maps Class
	 * @param points : Points to be clustered
	 * @return Array of GoogleMapsClusterMarker Objects
	 */
	public ArrayList<GoogleMapsClusterMarker> generateClusters(float zoomLevel, ArrayList<MarkerPlus> points){
		//System.out.println(MAXDISTANCE / zoomLevel*.5);
		double pixelDistance;
		ArrayList<GoogleMapsClusterMarker> clusters = new ArrayList<GoogleMapsClusterMarker>();
		tempPoints = new ArrayList<MarkerPlus>(points); //Prevent tampering with the points, Copy Constructor
		
		//Put all points within their own clusters for comparison
		for(int i = 0; i < tempPoints.size(); i++){
			clusters.add(i,new GoogleMapsClusterMarker());
			clusters.get(i).addPoint(tempPoints.get(i));
		}
		
		if(zoomLevel <= 18){
			boolean wasMerged = false;
			//Loop to compare Clusters
			for(int i = 0; i < clusters.size(); i++){
				
				if(wasMerged){
					i = 0;
					wasMerged = false;
				}
				
				for(int j = i+1; j < clusters.size(); j++){
					pixelDistance = pixelDistance(clusters.get(i), clusters.get(j), zoomLevel);
					System.out.println("Pixel Distance: " + pixelDistance);
					System.out.println("Max Distance: " + MAXDISTANCE * Math.pow(.5, zoomLevel));
					System.out.println("Point Names 1: " + clusters.get(i).getPointNames());
					System.out.println("Point Names 2: " + clusters.get(j).getPointNames());
					
					if(pixelDistance < (MAXDISTANCE * Math.pow(.5, zoomLevel))){
						clusters.add(mergeClusters(clusters.get(i), clusters.get(j)));
						clusters.remove(j);
						clusters.remove(i);
						
						wasMerged = true;
						j=1;
						System.out.println("Point added!");
						
					}
					
				}
			}
			
		}
		
		return clusters; //Clustered Points
	}
	
	/**
	 * Helper method, merges the two Cluster Marker objects and returns the final version.
	 * @param c1 : The first cluster to be merged.
	 * @param c2 : The second cluster to be merged.
	 * @return A combination of the two clusters, no repeats.
	 */
	public GoogleMapsClusterMarker mergeClusters(GoogleMapsClusterMarker c1, GoogleMapsClusterMarker c2){
		
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
	 * Pretty much tooken from the ClusterMaker for the original maps, with some adjustments
	 * @param c1 : First cluster
	 * @param c2 : Second cluster
	 * @param zoom : zoom level
	 * @return : Supposedly distance in pixels?
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
	
}
