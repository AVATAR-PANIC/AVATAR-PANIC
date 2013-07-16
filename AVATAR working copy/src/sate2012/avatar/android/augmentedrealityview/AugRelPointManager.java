package sate2012.avatar.android.augmentedrealityview;

import gupta.ashutosh.avatar.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import sate2012.avatar.android.googlemaps.MarkerPlus;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.util.Map;

/**
 * AugRelPointManager (Augmented Reality Point Manager)
 * Stores the points the user wishes to display in augmented reality.
 * Allows for the ArrayList backbone to be replaced with with custom code,
 * more functions to overlay the data stroage backbone, intuitive use, and easier readability.
 * 
 * @author Craig
 *
 */

public class AugRelPointManager implements Serializable {

	final transient CameraView outer;
	private ArrayList<MarkerPlus> onPoints;
	private ArrayList<MarkerPlus> allPoints;
	private ArrayList<MarkerPlus> activePoints;
	transient Paint paint;
	private Map<Rect, MarkerPlus> bitmapRects = new HashMap<Rect, MarkerPlus>();
	
	public AugRelPointManager(CameraView outer) {
		this.outer = outer;
		onPoints = new ArrayList<MarkerPlus>();
		allPoints = new ArrayList<MarkerPlus>();
		activePoints = new ArrayList<MarkerPlus>();
		paint = new Paint();
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
			allPoints = outer.markerArray;
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
	
	public void setClosePointsFromMiles(int miles){
		
		int meters = (int)(miles*1609.34);//conversion factor
		setClosePoints(meters);
		
	}
	
	private void drawOnPoint(Canvas canvas, int number, int total, MarkerPlus markerPlus){
		String name = markerPlus.getName();
		Bitmap closePointIcon = BitmapFactory.decodeResource(outer.getResources(),
				R.drawable.ic_launcher);
		canvas.drawBitmap(closePointIcon, 
				outer.mSurfaceView.getWidth()/2 - (total/2 - number) * closePointIcon.getScaledWidth(canvas),
				outer.mSurfaceView.getHeight()-closePointIcon.getScaledWidth(canvas), null);
		canvas.drawText( "Arrived at:" + name,
				outer.mSurfaceView.getWidth()/2 - (total/2 - number) * closePointIcon.getScaledWidth(canvas),
				outer.mSurfaceView.getHeight()-closePointIcon.getScaledWidth(canvas)-10, paint);
	}
	
	public void drawPoints(Canvas canvas, float myBearing, float myPitch)
	{
		bitmapRects.clear();
		onPoints.clear();
		if(outer.drawPointList.isEmpty() && activePoints.isEmpty()){
		}else{
			outer.drawPointList = activePoints;
		}
		for(MarkerPlus markerPlus: outer.drawPointList){
			//Log.i("TestForDate",markerPlus.getDate()+"END");
			Location gpLocation = new Location(LocationManager.NETWORK_PROVIDER);
			gpLocation.setLatitude(markerPlus.getLatitude());
			gpLocation.setLongitude(markerPlus.getLongitude());
			gpLocation.setAltitude(markerPlus.getAltitude());
			if(outer.myLocation.distanceTo(gpLocation)>10)
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
	
	public ArrayList<MarkerPlus> getAllPoints(){
		this.allPoints = outer.markerArray;
		return allPoints;
	}
	
	public void setActivePoints(ArrayList<MarkerPlus> tempArray){
		this.outer.drawPointList = tempArray;
		this.activePoints = tempArray;
	}
	
	public ArrayList<MarkerPlus> getActivePoints(){
		return this.activePoints;
	}
	
	public void drawPoint(MarkerPlus marker, Canvas canvas, float myBearing, float myPitch){
		//System.out.println(outer.fragWidth);
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
		double myPitchA = Math.atan2((myAlt - gpAlt) , outer.myLocation.distanceTo(gpLocation));
		double dist = outer.myLocation.distanceTo(gpLocation);
		// This checks where the point is in relation to the tablets
		// location and only draws it in the correct place.
		if (outer.myLocation.getLatitude() < gpLocation.getLatitude()) {
			if (myBearing >= 0) {
				if ((float) (Math.tan(gpBearing - myBearing)
						* (outer.mSurfaceView.getWidth() / 2)
						/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
						.getWidth() / 2)) > outer.fragWidth) {
					bitmapRects.put(new Rect((int)(Math.tan(gpBearing - myBearing) * (outer.mSurfaceView.getWidth() / 2)
							/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView.getWidth() / 2)), (int)(Math.tan(myPitchA - myPitch + Math.PI / 2.0) * (outer.mSurfaceView.getHeight() / 2)
							/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
							.getHeight() / 2)), (int)(Math.tan(gpBearing - myBearing)
									* (outer.mSurfaceView.getWidth() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getWidth() / 2)) + pointIcon.getWidth(), (int)(Math.tan(myPitchA - myPitch + Math.PI / 2.0) * (outer.mSurfaceView.getHeight() / 2)
											/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
													.getHeight() / 2)) + pointIcon.getHeight()), marker);
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
					canvas.drawText("" + dist,
							(float) (Math.tan(gpBearing - myBearing)
									* (outer.mSurfaceView.getWidth() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getWidth() / 2)),
							(float) (Math.tan(myPitchA - myPitch + Math.PI/ 2.0)
									* (outer.mSurfaceView.getHeight() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getHeight() / 2)) + pointIcon.getScaledHeight(canvas) + 10, paint);
				}
			}
		} else if (outer.myLocation.getLatitude() > gpLocation.getLatitude()) {
			if (myBearing <= 0) {

				if ((float) (Math.tan(gpBearing - myBearing) * (outer.mSurfaceView.getWidth() / 2)	/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
						.getWidth() / 2)) > outer.fragWidth) {
					bitmapRects.put(new Rect((int)(Math.tan(gpBearing - myBearing) * (outer.mSurfaceView.getWidth() / 2)
							/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView.getWidth() / 2)), (int)(Math.tan(myPitchA - myPitch + Math.PI / 2.0) * (outer.mSurfaceView.getHeight() / 2)
							/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
							.getHeight() / 2)), (int)(Math.tan(gpBearing - myBearing)
									* (outer.mSurfaceView.getWidth() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getWidth() / 2)) + pointIcon.getWidth(), (int)(Math.tan(myPitchA - myPitch + Math.PI / 2.0) * (outer.mSurfaceView.getHeight() / 2)
											/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
													.getHeight() / 2)) + pointIcon.getHeight()), marker);
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
					canvas.drawText( name,
							(float) (Math.tan(gpBearing - myBearing)
									* (outer.mSurfaceView.getWidth() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getWidth() / 2)),
							(float) (Math.tan(myPitchA - myPitch)
									* (outer.mSurfaceView.getHeight() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getHeight() / 2)), paint);
					canvas.drawText( dist + "",
							(float) (Math.tan(gpBearing - myBearing)
									* (outer.mSurfaceView.getWidth() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getWidth() / 2)) ,
							(float) (Math.tan(myPitchA - myPitch)
									* (outer.mSurfaceView.getHeight() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getHeight() / 2)) + pointIcon.getScaledHeight(canvas) + 10, paint);
				}
			}
		} else {
			if (outer.myLocation.getLongitude() < gpLocation.getLongitude()) {
				if (Math.abs(myBearing) > Math.PI / 2) {
					bitmapRects.put(new Rect((int)(Math.tan(gpBearing - myBearing) * (outer.mSurfaceView.getWidth() / 2)
							/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView.getWidth() / 2)), (int)(Math.tan(myPitchA - myPitch + Math.PI / 2.0) * (outer.mSurfaceView.getHeight() / 2)
							/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
							.getHeight() / 2)), (int)(Math.tan(gpBearing - myBearing)
									* (outer.mSurfaceView.getWidth() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getWidth() / 2)) + pointIcon.getWidth(), (int)(Math.tan(myPitchA - myPitch + Math.PI / 2.0) * (outer.mSurfaceView.getHeight() / 2)
											/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
													.getHeight() / 2)) + pointIcon.getHeight()), marker);
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
					canvas.drawText(
							name,
							(float) (Math.tan(gpBearing - myBearing)
									* (outer.mSurfaceView.getWidth() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getWidth() / 2)),
							(float) (Math.tan(myPitchA - myPitch)
									* (outer.mSurfaceView.getHeight() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getHeight() / 2)), paint);
					canvas.drawText(
							dist + "",
							(float) (Math.tan(gpBearing - myBearing)
									* (outer.mSurfaceView.getWidth() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getWidth() / 2)),
							(float) (Math.tan(myPitchA - myPitch)
									* (outer.mSurfaceView.getHeight() / 2)
									/ Math.tan(Math.PI / 6.0) + (outer.mSurfaceView
									.getHeight() / 2)) + pointIcon.getScaledHeight(canvas) + 10, paint);
				}
			}
		}
	}
	
	public void drawInfo(int x, int y){
		Bitmap markerInfo = Bitmap.createBitmap(100,50, Config.ARGB_8888);
		Canvas canvas = new Canvas(markerInfo);
		Paint tempPaint = new Paint();
		tempPaint.setColor(Color.BLACK);
		tempPaint.setAntiAlias(true);
		tempPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
		ImageView image = (ImageView) outer.getActivity().findViewById(R.id.augmented_reality_point_info);
		MarkerPlus marker = new MarkerPlus();
		for(Rect rect : bitmapRects.keySet()){
			System.out.println(rect.toString());
			System.out.println(rect.left + ", " + rect.right + ", "  + rect.top + ", " + rect.bottom + "/" + rect.width());
			if(x > rect.left && x < rect.right && y < rect.bottom && y > rect.top){
				System.out.println("YAY!");
				marker = bitmapRects.get(rect);
				canvas.drawText(marker.getName(), 0, 20, tempPaint);
				canvas.drawText(marker.getData(), 0, 40, tempPaint);
				image.setImageBitmap(markerInfo);
				image.bringToFront();
				image.setMinimumHeight(markerInfo.getHeight());
				image.setMinimumWidth(markerInfo.getWidth());
				return;
			}
		}
	}
}
