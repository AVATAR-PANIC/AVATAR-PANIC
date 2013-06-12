package sate2012.avatar.android.pointclustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.MapView;

import sate2012.avatar.android.DataObject;
import sate2012.avatar.android.GeoDataRepository;
import sate2012.avatar.android.LocationDataReceiverAVATAR;

/**
 * @author William
 * 
 *         This class is designed to take a {@code Map} of key GeoPoint and
 *         value DataObjects and cluster those points together
 */
public class ClusterMaker {
	private static final double OFFSET = 268435456;
	private static final double RADIUS = 85445659.4471;
	private static Map<GeoPoint, Collection<DataObject>> clusteredGeoPointList;

	/**
	 * The default constructor has no implementation yet
	 */
	public ClusterMaker() {
		clusteredGeoPointList = new HashMap<GeoPoint, Collection<DataObject>>();
	}

	/**
	 * This method takes points from the surrounding area and clusters them into
	 * one larger point. it takes and array of points (going to be the map), the
	 * distance in pixels to cluster the points, and the current zoom level of
	 * the application.
	 * 
	 * @param distance
	 *            The the distance that the method compares against to determine
	 *            whether to cluster a point.
	 * @param mapView
	 *            The MapView that is being used in the map application
	 * @param repo
	 *            The GeoDataRepository that the program uses to get the data
	 *            for the point information
	 * @param pixelDistance
	 *            The distance that the cluster algorithm uses to determine when
	 *            to cluster a point.
	 * @param entriesColl
	 *            The collection of GeoPoint entries that is in the map
	 * 
	 * @return - returns a MegaPoint that contains the Geopoints that are within
	 *         a given distance.
	 */
	public static <E extends GeoPoint> Map<MegaPoint, DataObject> pointClusterMaker(
			Map<GeoPoint, DataObject> entriesColl, int distance, MapView mapView) {
		// this is the distance that i will calculate later in the program.
		double pixelDistance;
		// iterate through the map and convert the key/value pairs into
		// geoPoints
		ArrayList<GeoPoint> tempGeoPointArray = new ArrayList<GeoPoint>();
		// this is the array that i am going to use to check the distance with
		ArrayList<GeoPoint> geoPointArray = new ArrayList<GeoPoint>();
		// this ArrayList is the array that i am going to add to the megaPoint
		for (GeoPoint entry : entriesColl.keySet()) {
			tempGeoPointArray.add(entry);
		}
		Map<MegaPoint, DataObject> megaPointMap = new HashMap<MegaPoint, DataObject>();
		for (GeoPoint entry : entriesColl.keySet()) {

			GeoPoint tempGeoPoint = entry;

			for (int j = tempGeoPointArray.size() - 1; j >= 0; j--) {
				GeoPoint targetGeoPoint = tempGeoPointArray.get(j);
				pixelDistance = pixelDistance(tempGeoPoint.getLatitude(),
						tempGeoPoint.getLongitude(),
						targetGeoPoint.getLatitude(),
						targetGeoPoint.getLongitude(), mapView);
				// This gets the distance between the entry GeoPoint (see
				// outside of for) and the target GeoPoint (inside of for)
				if (pixelDistance < distance) {
					// If the pixel distance is less than the cluster making
					// distance (distance) then add that point to the
					// geoPointArray.
					geoPointArray.add(targetGeoPoint);
					// Remove that point from the original array to
					// ensure that it is not measured twice.
					tempGeoPointArray.remove(j);
				}
			}
			GeoPoint MegaPointLocation = getAverage(geoPointArray);
			// I called this GeoPoint MegaPointLocation because it contains the
			// lat/lon coordinates that the MegaPoint needs.
			// Create a new MegaPoint and add the GeoPoint array to that
			// MegaPoint
			MegaPoint clusterPoint = new MegaPoint(
					MegaPointLocation.getLatitude(),
					MegaPointLocation.getLatitude(), geoPointArray);
			megaPointMap.put(clusterPoint, entriesColl.get(tempGeoPoint));
		}
		return megaPointMap;

	}// end of method

	protected static GeoDataRepository geoPointDataRetriever() {
		final String dataString = LocationDataReceiverAVATAR
				.loadDataStringFromURL();
		final GeoDataRepository repo = new GeoDataRepository();
		repo.addEntriesFromURLString(dataString);
		return repo;
	}

	private static double lonToX(double lon) {
		return Math.round(OFFSET - RADIUS * lon * Math.PI / 180);
	}

	private static double lonToY(double lat) {
		return Math.round(OFFSET
				- RADIUS
				* Math.log((1 + Math.sin(lat * Math.PI / 180))
						/ (1 - Math.sin(lat * Math.PI / 180))) / 2);
	}

	private static double pixelDistance(double lat1, double lon1, double lat2,
			double lon2, MapView mapView) {
		int zoom = mapView.getZoomLevel();

		// this might not work... i don't know if a byte can be converted into
		// an int
		double x1 = lonToX(lon1);
		double y1 = lonToY(lat1);

		double x2 = lonToX(lon2);
		double y2 = lonToY(lat2);
		// this will calculate the pixel distance of the points on the screen
		return Math.sqrt((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2))
				/ Math.pow(2, (21 - zoom));
		// the php script references a bitshift operator >>. THe equivalent
		// statement is implemented above.
	}

	public static GeoPoint getAverage(ArrayList<GeoPoint> currentArrayList) {
		double totalLat = 0;
		double totalLon = 0;
		double averageLat = 0;
		double averageLon = 0;
		// now iterate through the other set of points
		for (int j = 1; j < currentArrayList.size(); j++) {
			// this loop iterates through the points in the ArrayList and adds
			// their contents to a number to be averaged.
			GeoPoint targetGeoPoint = currentArrayList.get(j);
			totalLat += targetGeoPoint.getLatitude();
			totalLon += targetGeoPoint.getLongitude();
		}// end of for

		// now to do the averaging
		averageLat = totalLat / currentArrayList.size();
		averageLon = totalLon / currentArrayList.size();
		GeoPoint returnGeo = new GeoPoint(averageLat, averageLon);
		return returnGeo;
	}
}