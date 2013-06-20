package com.guardian_angel.uav_tracker; 
import java.util.ArrayList; 
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import com.google.android.maps.ItemizedOverlay; 
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;


//Used to draw the Arrows where the user taps

public class CustomizedOverlay extends ItemizedOverlay<OverlayItem>{
	
	private ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();
	   

	   
	   public CustomizedOverlay(Drawable defaultMarker) {
	        super(boundCenterBottom(defaultMarker));
	   }
	   
	   public CustomizedOverlay(Drawable defaultMarker, Context context) {
	        this(defaultMarker);
	   }
	   

	   @Override
	   protected OverlayItem createItem(int i) {
	      return mapOverlays.get(i);
	   }

	   @Override
	   public int size() {
	      return mapOverlays.size();
	   }
	   
	
	   
	   public void addOverlay(OverlayItem overlay) {
	      mapOverlays.add(overlay);
	       this.populate();
	   }

	   @Override
	    public void draw(Canvas canvas, MapView mapView, boolean shadow)
	    {
	        if(!shadow)
	        {
	            super.draw(canvas, mapView, false);
	        }
	    }
	   
	   
	   public void removeOverlay(OverlayItem overlay){
		   mapOverlays.remove(overlay);
		   populate();
	   }
	   
	   public void clear(){
		   mapOverlays.clear();
		   populate();
	   }

};
