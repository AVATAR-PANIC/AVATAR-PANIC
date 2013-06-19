package sate2012.avatar.android;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.mapsforge.android.maps.GeoPoint;

/*
 * Not used in current version
 * Previously used for MapsForgeViewer
 */

public class GeoDataRepository {
	private static final char endl = Character.LINE_SEPARATOR;
	private final Map<GeoPoint, Collection<DataObject>> objectCollectionsByPoint;

	public GeoDataRepository() {
		objectCollectionsByPoint = new HashMap<GeoPoint, Collection<DataObject>>();
	}

	public void addItem(final GeoPoint point, final DataObject object) {
		Collection<DataObject> objects = objectCollectionsByPoint.get(point);
		// if the objects collection is null, then the GeoPoint
		// was not in the map - make new collection
		if (objects == null) {
			objects = new ArrayList<DataObject>(10);
			// add the new list to the Map
			objectCollectionsByPoint.put(point, objects);
		}
		// now add the object to the collection
		objects.add(object);
	}

	/**
	 * Takes a string that contains substrings that are separated by
	 * 
	 * @@@POINT: with the end of the list marked by
	 * @@@END OF MESSAGES
	 * @param urlString
	 */
	public void addEntriesFromURLString(final String urlString) {
		final String[] parts = urlString.split("@@@");
		System.out.println(parts[0]);
		for (String string : parts) {
			if (string.length() == 0) {
				continue;
			}
			if (string.startsWith("END OF MESSAGES")) {
				break;
			}
			addItem(getGeoPointFromURLString(string), new DataObject());
		}
	}

	public static GeoPoint getGeoPointFromURLString(final String urlString) {
		final char star = (char) 42;
		final char bslash = (char) 92;
		final String splitter = "_" + bslash + star + bslash + star + bslash
				+ star + "_";
		final String[] parts = urlString.split(splitter);
		// NOTE: lat is in part 1, lon is in part 2
		final double lat = Double.parseDouble(parts[0]);
		final double lon = Double.parseDouble(parts[1]);
		return new GeoPoint(lat, lon);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(1200);

		// iterate over the stuff in the map, printing the contents
		for (java.util.Map.Entry<GeoPoint, Collection<DataObject>> entry : objectCollectionsByPoint
				.entrySet()) {
			// for each of these, print the point along with each dataObject

			for (DataObject dataObject : entry.getValue()) {
				sb.append(entry.getKey().toString());
				sb.append(dataObject.toString());
				sb.append(endl);
			}
		}
		return sb.toString();
	}

	/*
	 * this method takes the contents of the map and returns them as a
	 * collection of Entries
	 */
	public Map<GeoPoint, DataObject> getCollectionOfDataEntries() {
		final Map<GeoPoint, DataObject> returnVal = new HashMap<GeoPoint, DataObject>();
		// iterate over the stuff in the map, printing the contents
		for (java.util.Map.Entry<GeoPoint, Collection<DataObject>> entry : objectCollectionsByPoint
				.entrySet()) {
			// for each of these, print the point along with each dataObject
			for (DataObject dataObject : entry.getValue()) {
				java.util.Map.Entry<GeoPoint, DataObject> newEntry = new SimpleEntry<GeoPoint, DataObject>(
						entry.getKey(), dataObject);
				returnVal.put(newEntry.getKey(), newEntry.getValue());
			}
		}
		return returnVal;
	}

	public class SimpleEntry<K extends Object, V extends Object> implements
			java.util.Map.Entry<K, V> {

		private final K key;
		private V value;

		private SimpleEntry() {
			throw new AssertionError();
		}

		public SimpleEntry(final K key, final V value) {
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public V setValue(V v) {
			this.value = v;
			return this.value;
		}
	}
}
