package sate2012.avatar.android;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.OverlayItem;

public class DataObjectItem extends OverlayItem {
	private DataObject data;
	private GeoPoint point;

	public DataObjectItem(GeoPoint p, DataObject dataIn) {
		super(p, dataIn.getText(), dataIn.getText());
		this.point = p;
		this.data = dataIn;
	}

	public DataObject getData() {
		return data;
	}

	public void setData(DataObject data) {
		this.data = data;
	}

	public GeoPoint getPoint() {
		return point;
	}

	public void setPoint(GeoPoint point) {
		this.point = point;
	}
}
