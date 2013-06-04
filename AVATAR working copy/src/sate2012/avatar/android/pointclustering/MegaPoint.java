package sate2012.avatar.android.pointclustering;

import java.util.ArrayList;

import org.mapsforge.android.maps.GeoPoint;

/**
 * @author William Giffin
 * 
 * 
 *         Mega Point is a sub class of GeoPoint. It includes a constructor for
 *         an {@code ArrayList} that allows the user to put a group of GeoPoints
 *         in the Mega Point
 * 
 */
public class MegaPoint extends GeoPoint
{

    /**
     * Basic constructor for integer values
     * 
     * @param latitudeE6
     *            The latitude of the MegaPoint
     * @param longitudeE6
     *            The Longitude of the MegaPoint
     */
    public MegaPoint(int latitudeE6, int longitudeE6)
    {
	super(latitudeE6, longitudeE6);
    }

    /**
     * This is the basic constructor for double values
     * 
     * @param latitude
     *            The latitude of the MegaPoint
     * @param longitude
     *            The Longitude of the MegaPoint
     */
    public MegaPoint(double latitude, double longitude)
    {
	super(latitude, longitude);
    }

    /**
     * This is a full constructor for the MegaPoint, it includes location
     * integer values and an array for GeoPoints
     * 
     * @param latitudeE6
     *            The latitude of the MegaPoint
     * @param longitudeE6
     *            the Longitude of the MegaPoint
     * @param GeoPointList
     *            The list of the all the GeoPoints contained in the Mega Point
     */
    public MegaPoint(int latitudeE6, int longitudeE6,
	    ArrayList<GeoPoint> GeoPointList)
    {
	super(latitudeE6, longitudeE6);
	GeoPointList = new ArrayList<GeoPoint>();
    }

    /**
     * This is a full constructor for the MegaPoint, it includes location double
     * values and an array for GeoPoints
     * 
     * @param latitude
     *            The latitude of the MegaPoint
     * @param longitude
     *            the Longitude of the MegaPoint
     * @param GeoPointList
     *            The list of the all the GeoPoints contained in the Mega Point
     */
    public MegaPoint(double latitude, double longitude,
	    ArrayList<GeoPoint> GeoPointList)
    {
	super(latitude, longitude);
	GeoPointList = new ArrayList<GeoPoint>();
    }
    // set an array or a map to hold the values for the pull down menu. These
    // values are the GeoPoints that the MeagPoint contains
}
