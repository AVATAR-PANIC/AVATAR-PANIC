package sate2012.avatar.android.augmentedrealityview;

import gupta.ashutosh.avatar.R;

import java.util.ArrayList;

import sate2012.avatar.android.googlemaps.MarkerPlus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;

/**
 * AugRelPointManager (Augmented Reality Point Manager)
 * Stores the points the user wishes to display in augmented reality.
 * Allows for the ArrayList backbone to be replaced with with custom code,
 * more functions to overlay the data stroage backbone, intuitive use, and easier readability.
 * 
 * @author Craig
 *
 */

class AugRelPointManager {

	final CameraView outer;
	protected ArrayList<MarkerPlus> onPoints;
	
	public AugRelPointManager(CameraView outer) {
		this.outer = outer;
		onPoints = new ArrayList<MarkerPlus>();
	}
	
	public void clearPoints(){
		outer.drawPointList.clear();
	}
	
	public void addPoint(MarkerPlus marker){
		if(! outer.drawPointList.contains(marker))
		{
			outer.drawPointList.add(marker);
		}
	}
	
	public boolean deletePoint(String marker){
		return outer.drawPointList.remove(marker);
	}
	
	public void setClosePoints(int dist){
		this.clearPoints();
		if(outer.markerArray != null)
		{
			for(MarkerPlus marker: outer.markerArray){
				
				Location gpLocation = new Location(LocationManager.NETWORK_PROVIDER);
				gpLocation.setLatitude(marker.getLatitude());
				gpLocation.setLongitude(marker.getLongitude());
				gpLocation.setAltitude(marker.getAltitude());
				
				
				if ( outer.myLocation.distanceTo(gpLocation) < dist){
						this.addPoint(marker);
				}
			}
		}
	}
	
	public void setClosePoints(){
		this.setClosePoints(16000);
	}
	
	private void drawOnPoint(Canvas canvas, int number, int total, MarkerPlus markerPlus){
		Bitmap closePointIcon = BitmapFactory.decodeResource(outer.getResources(),
				R.drawable.ic_launcher);
		canvas.drawBitmap(closePointIcon, 
				outer.mSurfaceView.getWidth()/2 - (total/2 - number) * closePointIcon.getScaledWidth(canvas),
				outer.mSurfaceView.getHeight()-closePointIcon.getScaledWidth(canvas), null);
		
		
		
	}
	
	public void drawPoints(Canvas canvas, float myBearing, float myPitch)
	{
		onPoints.clear();
		if(outer.drawPointList.isEmpty()){
			this.setClosePoints();
		}
		for(MarkerPlus markerPlus: outer.drawPointList){
			Location gpLocation = new Location(LocationManager.NETWORK_PROVIDER);
			gpLocation.setLatitude(markerPlus.getLatitude());
			gpLocation.setLongitude(markerPlus.getLongitude());
			gpLocation.setAltitude(markerPlus.getAltitude());
			if(outer.myLocation.distanceTo(gpLocation)<50)
			{
				drawPoint(markerPlus,canvas,myBearing,myPitch);
			}
			else
			{
				onPoints.add(markerPlus);
			}
		}
		int total = onPoints.size();
		int number = 0;
		for(MarkerPlus markerPlus: this.onPoints){
			this.drawOnPoint(canvas, number, total, markerPlus);
			number++;
		}
	}
	
	public void drawPoint(MarkerPlus marker, Canvas canvas, float myBearing, float myPitch){
		
		
		//For determining which icon to draw
		Bitmap pointIcon;
		
		if(marker.getName().equals("EMERGENCY")){
			pointIcon = BitmapFactory.decodeResource(outer.getResources(),
					R.drawable.emergency);
		}else{
			pointIcon = BitmapFactory.decodeResource(outer.getResources(),
					R.drawable.ic_launcher);
		}

		double myAlt = outer.myLocation.getAltitude();
		Location gpLocation = new Location(LocationManager.NETWORK_PROVIDER);
		gpLocation.setLatitude(marker.getLatitude());
		gpLocation.setLongitude(marker.getLongitude());
		gpLocation.setAltitude(marker.getAltitude());
		String name = marker.getName();

		// All of these angles are in Radians.
		double gpBearing = outer.myLocation.bearingTo(gpLocation) * Math.PI
				/ 180.0;
		double gpAlt = gpLocation.getAltitude();
		Paint paint = new Paint();
		double myPitchA = Math.atan((myAlt - gpAlt)
				/ outer.myLocation.distanceTo(gpLocation));
		double dist = outer.myLocation.distanceTo(gpLocation);
		// This checks where the point is in relation to the tablets
		// location and only draws it in the correct place.
		if (outer.myLocation.getLatitude() < gpLocation.getLatitude()) {
			if (myBearing >= 0) {
				if ((float) (Math.tan(gpBearing - myBearing)
						* (outer.mSurfaceView.getWidth() / 2)
						/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
						.getWidth() / 2)) > outer.fragWidth) {

					canvas.drawBitmap(
							pointIcon,
							(float) (Math.tan(gpBearing - myBearing)
									* (outer.mSurfaceView.getWidth() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getWidth() / 2)),
							(float) (Math.tan(myPitchA - myPitch + Math.PI
									/ 2.0)
									* (outer.mSurfaceView.getHeight() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getHeight() / 2)), null);
					canvas.drawText( name + ": " + dist,
							(float) (Math.tan(gpBearing - myBearing)
									* (outer.mSurfaceView.getWidth() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getWidth() / 2)),
							(float) (Math.tan(myPitchA - myPitch + Math.PI/ 2.0)
									* (outer.mSurfaceView.getHeight() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getHeight() / 2)), paint);
				}
			}
		} else if (outer.myLocation.getLatitude() > gpLocation.getLatitude()) {
			if (myBearing <= 0) {

				if ((float) (Math.tan(gpBearing - myBearing)
						* (outer.mSurfaceView.getWidth() / 2)
						/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
						.getWidth() / 2)) > outer.fragWidth) {

					canvas.drawBitmap(
							pointIcon,
							(float) (Math.tan(gpBearing - myBearing)
									* (outer.mSurfaceView.getWidth() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getWidth() / 2)),
							(float) (Math.tan(myPitchA - myPitch)
									* (outer.mSurfaceView.getHeight() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getHeight() / 2)), null);
					canvas.drawText( name + ": " + dist,
							(float) (Math.tan(gpBearing - myBearing)
									* (outer.mSurfaceView.getWidth() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getWidth() / 2)),
							(float) (Math.tan(myPitchA - myPitch)
									* (outer.mSurfaceView.getHeight() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getHeight() / 2)), paint);
					
				}
			}
		} else {
			if (outer.myLocation.getLongitude() < gpLocation.getLongitude()) {
				if (Math.abs(myBearing) > Math.PI / 2) {
					canvas.drawBitmap(
							pointIcon,
							(float) (Math.tan(gpBearing - myBearing)
									* (outer.mSurfaceView.getWidth() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getWidth() / 2)),
							(float) (Math.tan(myPitchA - myPitch)
									* (outer.mSurfaceView.getHeight() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getHeight() / 2)), null);
				}
			}
		}
	}
}
