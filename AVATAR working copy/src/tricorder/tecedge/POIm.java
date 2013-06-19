package tricorder.tecedge;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class POIm extends ItemizedOverlay<OverlayItem> {

	int x, y;
	GeoPoint touchedPoint;
	private ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();
	private Context context;
	private String type;

	public POIm(Drawable defaultMarker) {
		super(boundCenter(defaultMarker));
	}

	public POIm(Context c, Drawable d, String t) {
		this(d);
		context = c;
		setType(t);
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mapOverlays.get(i);
	}

	@Override
	public int size() {
		return mapOverlays.size();
	}

	public void clearList() {
		if (!mapOverlays.isEmpty())
			mapOverlays.clear();
	}

	public void insert(OverlayItem item) {
		mapOverlays.add(item);
		this.populate();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	// @Override
	// public boolean onTouchEvent(MotionEvent e, MapView m, long start)
	// throws IOException {
	// if (e.getAction() == MotionEvent.ACTION_DOWN) {
	// start = e.getEventTime();
	// x = (int) e.getX();
	// y = (int) e.getY();
	// touchedPoint = Mapm.map.getProjection().fromPixels(x, y);
	// Geocoder geocoder = new Geocoder(getBaseContext(),
	// Locale.getDefault());
	// List<Address> address = geocoder.getFromLocation(
	// touchedPoint.getLatitudeE6() / 1E6,
	// touchedPoint.getLongitudeE6() / 1E6, 1);
	// System.out.println(touchedPoint.getLatitudeE6() / 1E6);
	// }
	// return false;
	// }

	@Override
	protected boolean onTap(int index) {
		final int curIndex = index;
		OverlayItem item = mapOverlays.get(index);

		AlertDialog dialog = new AlertDialog.Builder(context).create();
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.setCanceledOnTouchOutside(true);
		dialog.setButton("More Info", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// display
				Intent display = new Intent("tricorder.tecedge.Displaym");
				display.putExtra("Type", mapOverlays.get(curIndex).getTitle());
				display.putExtra("Data", mapOverlays.get(curIndex).getSnippet());
				context.startActivity(display);
			}
		});
		dialog.show();
		return true;
	}

	private Context getBaseContext() {
		// TODO Auto-generated method stub
		return null;
	}
}
