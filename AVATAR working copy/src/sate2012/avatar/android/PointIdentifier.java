package sate2012.avatar.android;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.mapsforge.android.maps.GeoPoint;

import android.location.Location;
import android.location.LocationManager;

public class PointIdentifier
{

    private LocationDataReceiverAVATAR plotter;
    private static double x1;
    private static double x2;
    private static double x;
    private static double y1;
    private static double y2;
    private static double y;
    private static double dist;
    private static double radius;
    private static Array[] locationDataArray = new Array[500];
    private static double i;

    public PointIdentifier() throws Exception
    {
	// this method has to be able to identify point groups whether they are
	// within clusters or a large pasting of points

	// if the points are within the clusters
	// create a new point that contains a map with all of the key value
	// pairs for the points within it. This map will be used
	// to put all of the data within scroll view

	// if the points are within the pastings
	// create a cluster of points like before, but set a value for either
	// size or number of points within the radius of the cluster grouping
	// when a cluster is create, set the points within the cluster to
	// invisible when at a arbitrarily chosen zoom level.
	
	//use a swtich case block for determining the zoom level and what action to take for that zoom level

    }

    public void onMediaCluster(GeoPoint p)
    {
    }

    // private static final int earthRadius = 6371;
    static double lat;
    static double lon;

    public static void calculateDistance(float lat1, float lon1, float lat2,
	    float lon2)
    {
	// once we get all of the points, we put them into the geoDataRepository
	// for the point clusters and then calculate the distanceTo()
	GeoDataRepository repo = geoPointDataRetriever();
	final Map<GeoPoint, DataObject> entriesColl = repo
			.getCollectionOfDataEntries();
	for (int i = 0; i < entriesColl.size(); i++) {
		Entry<? extends GeoPoint, ? extends DataObject> entry = ((ArrayList<Entry<GeoPoint, DataObject>>) entriesColl).get(i);

	    // first thing, you are going to have to create a new
	    // GeoDataRepository for each cluster.
	    GeoDataRepository pointClusterRepository = new GeoDataRepository();
	    Location newLocation = new Location(
		    LocationManager.NETWORK_PROVIDER);
	    newLocation.setLatitude(lat);
	    newLocation.setLongitude(lon);
	    // once we get the rough average between the all of the points in
	    // the point clusters, we can create a new point with
	    // latitude and long of the mean distance.
	    radius = lat;
	    x1 = 0;
	    y1 = 0;
	    x2 = 0;
	    y2 = 0;
	    x = 0;
	    y = 0;
	    dist = 0;

	    for (i = 0; i < locationDataArray.length; i++)
	    {
		x2 = locationDataArray.length;
		y2 = locationDataArray.length;
		x = x1 - x2;
		y = y1 - y2;
		dist = Math.sqrt(x * x + y * y);
		if (dist <= radius)

		    // once that is done we will need to calculate the distance
		    // between the points using the distanceTo() method
		    newLocation.distanceTo(null);
		// in this method we will calculate the distance.

	    }
	    // Location location;
	    // double lat = location.getLatitude();
	    // double lng = location.getLongitude();

	    // float dLat = (float) Math.toRadians(lat2 - lat1);
	    // float dLon = (float) Math.toRadians(lon2 - lon1);
	    // float a =
	    // (float) (Math.sin(dLat / 2) * Math.sin(dLat / 2) +
	    // Math.cos(Math.toRadians(lat1))
	    // * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) *
	    // Math.sin(dLon / 2));
	    // float c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 -
	    // a)));
	    // float d = earthRadius * c;
//	    return d;

	}

	Location currentLocation;
	Location distanceLocation;
	double area;

	// set your current location
	

	// set your destination location
//	distanceLocation = new Location("");
//	distanceLocation.setLatitude(destinationLat);
//	distanceLocation.setLongitude(destinationLong);

//	area = isBetterLocation.distanceTo(distanceLocation) / 1000;

    }
    
    protected static GeoDataRepository geoPointDataRetriever()
    {
	final String dataString = LocationDataReceiverAVATAR
		.loadDataStringFromURL();
	final GeoDataRepository repo = new GeoDataRepository();
	repo.addEntriesFromURLString(dataString);
	return repo;
    }
}