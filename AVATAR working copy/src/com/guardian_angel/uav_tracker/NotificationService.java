/**
 * This Service class is used to notify the user when another user close to them (within 15km) spots a UAV. The class will calculate the distance between
 * this user and the user sending the phase 1 data. It will pull out where the other user is, assuming it was provided (city, state). It will also
 * provide a point to paint on the Map class should the user want to see where the other user is. This Service also maintains the connection to the XMPP
 * server, allowing it to stay alive longer (approx. 1 hour). The Service is not bound to a single Activity, making it free floating in case the rest of the process
 * gets closed out by the Android memory management.
 */
package com.guardian_angel.uav_tracker;

import gupta.ashutosh.avatar.R;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

// From the asmack library
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

/**
 * @author Michael J. Fox <fox.117@wright.edu>
 * @version 1.0
 * @since 07.30.12
 *
 */
public class NotificationService extends Service{
	
	/**
	 * These variables are marked as public static due to their requirement by other Activities inside of the application.
	 * The memory trade off for the ease of accessibility and use is necessary and worth while.
	 */
	public static XMPPConnection connection;
	public static MultiUserChat muc;
	public static String latLongElvString = "Lat=\"0.0\" Lng=\"0.0\" Elv=\"0.0\"";
	public static String geoCodeString = "Str=\"null\" Cty=\"null\" Ste=\"null\"";
	public static double userLat = 0.0;
	public static double userLng = 0.0;
	public static double latFromMessage = 0.0;
	public static double lngFromMessage = 0.0;
	
	// Required to launch the notification
	private static final int NOTIFICATION_ID = 1;

	// Required to retrieve the location of the user
	private LocationManager manager;
	private LocationListener locationListener;
	private Geocoder mGeoCoder;
	private Location location;

	/**
	 * Required stub method, would only be used if the Service needs to be bounded to an Activity
	 * This is not ideal, however, since we want the Service always running in the background
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	/**
	 * This will create the Notification Service when called and create a Packet Listener that will
	 * be used to create the notifications when other users send phase 1 data. It should be noted that
	 * the XMPP server should NOT persist old messages to new users, otherwise the user will be notified when the application starts.
	 */
	@Override
	public void onCreate(){
		
		//Instantiate variables needed to retrieve the current location of the user, including city, state and street. This listener allows for location updating
		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 50, locationListener);
		mGeoCoder = new Geocoder(getApplicationContext());
		
		/**
		 *  This will set the original location of the user to their last known location according to the GPS.
		 *  While somewhat unreliable, it is only used for locating a nearby user, and not for any major calculations of the data.
		 */
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); 
    	String provider = manager.getBestProvider(criteria, true);
    	location = manager.getLastKnownLocation(provider);

		//Check to make sure the phone has a stable connection to the XMPP server
		if ((connection != null) && (muc != null) && connection.isConnected() && muc.isJoined())
		{
			//Create the new packet listener retrieve phase 1 data from other users
			PacketFilter filter = new MessageTypeFilter(Message.Type.groupchat);
			connection.addPacketListener(new PacketListener() 
			{
				/**
				 * This method will process each packet as it gets submitted to the XMPP server and ultimately sent to the phone.
				 * It will have various checks along the processing of the packet to make sure the correction notification is outputted.
				 * @Param packet = Packet recently added to the XMPP chat room
				 */
				public void processPacket(Packet packet) 
				{	
					
					//Instantiate a local variable of the packet for manipulation
					Message message = (Message) packet;
					
					//Check to make sure that a completely blank message did not get sent, and that it contains a phase 1 marker.
					if (message.getBody() != null && message.getBody().contains("_1")) 
					{   
						
						//Instantiate a local variable of the body of the packet for manipulation
						String body = message.getBody();
						double sendingLat = 0;
						double sendingLng = 0;
						
						//Check to make sure that the packet is not one originating from this phone
						if (body.contains(MainActivity.deviceID) == false)
						{	
							// Create local variables for area manipulation and output
							String region = "";
						
						// Get the region where the message was sent from (City, State), this will be displayed in the notification
				        int rIndex1 = body.indexOf("Cty=\"") + 5;
				        int rIndex2 = body.indexOf("\"", rIndex1);
				        if ((rIndex1 != -1) && (rIndex2 != -1))
				        {
				        	region = body.substring(rIndex1, rIndex2);
				        }
				        int rIndex3 = body.indexOf("Ste=\"") + 5;
				        int rIndex4 = body.indexOf("\"", rIndex3);
				        if ((rIndex3 != -1) && (rIndex4 != -1))
				        {
				        	region = region + ", " + body.substring(rIndex3, rIndex4);
				        }
				        
				        // Get the coordinates from where the message was sent, this will be used to calculate how far away the other user is.
				        int cIndex1 = body.indexOf("Lng=\"") + 5;
				        int cIndex2 = body.indexOf("\"", cIndex1);
				        if ((cIndex1 != -1) && (cIndex2 != -1))
				        {
				        	// Put the longitude into a public static variable for manipulation by other activities and a local variable for manipulation in this class
				        	lngFromMessage = Double.parseDouble(body.substring(cIndex1, cIndex2));
				        	sendingLng = lngFromMessage;
				        }
				        int cIndex3 = body.indexOf("Lat=\"") + 5;
				        int cIndex4 = body.indexOf("\"", cIndex3);
				        if ((cIndex3 != -1) && (cIndex4 != -1))
				        {
				        	// Put the latitude into a public static variable for manipulation by other activities and a local variable for manipulation in this class
				        	latFromMessage = Double.parseDouble(body.substring(cIndex3, cIndex4));
				        	sendingLat = latFromMessage;
				        }
				      
						// This is rechecking that the message sent in is phase 1, and that the other user had an active / plotted GPS position
				        if (sendingLng != 0.0 && sendingLat != 0.0)
				        {
				        
			        	// Retrieve the most current latitude and longitude for this user to aid in the distance calculation
				        if(userLat == 0.0 && userLng == 0.0)
				        {
				        	try{
				        	userLat = location.getLatitude();
				        	userLng = location.getLongitude();
				        	}
				        	catch(Exception e)
				        	{
				        		e.printStackTrace();
				        		// Instantiate the notification service from the android OS
								String ns = Context.NOTIFICATION_SERVICE;
								NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
								
								// Create the rest of the notification in full and when it should be activated (now).
								int icon = R.drawable.ic_launcher;
								CharSequence tickerText = "Warning! UAV has been spotted in your area!";
								long when = System.currentTimeMillis();
								
								// Instantiate the notification and add all the parts to it
								Notification notification = new Notification(icon,tickerText,when);
								Context context = getApplicationContext();
								RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.guardian_angel_custom_notification);
								contentView.setImageViewResource(R.id.ic_launcher, R.drawable.ic_launcher);
								contentView.setTextViewText(R.id.title, "Spotted at: " + region);
								contentView.setTextViewText(R.id.text, "Unable to obtain distance or heading!");
								notification.contentView = contentView;
								
								// Allow the notification to launch the Map activity upon clicking it
								Intent notificationIntent = new Intent(context, Map.class);
								notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								notificationIntent.putExtra("viewOnly", false);
								PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,0);
								notification.contentIntent = contentIntent;
								
								// Set defaults for the notifications so that it will pop up on any Android device without issue
								notification.flags |= Notification.FLAG_AUTO_CANCEL;
								notification.defaults |= Notification.DEFAULT_ALL;
								
								// Launch the notification
								mNotificationManager.notify(NOTIFICATION_ID, notification);
				        	}
				        }
				        else
				        {
			        	
			        	// Instantiate local variables to do the distance calculation
			        	double rad = 6371;
			        	double dLat = 0;
			        	double dLon = 0;
			        	double deviceLat;
			        	
			        	/**
			        	 * This is the implementation of the Haversine Formula which will give the distance in kilometers from one user to the other by
			        	 * the variable "c".
			        	 */
			        	dLat = (sendingLat - userLat) * (Math.PI/180);
			        	dLon = (sendingLng - userLng) * (Math.PI/180);
			        	deviceLat = userLat * (Math.PI/180);
			        	sendingLat = sendingLat * (Math.PI/180);
			        	double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(deviceLat) * Math.cos(sendingLat);
			        	double b = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
			        	double c = rad * b;
			        	
			        	// Create a decimal format for the number of significant digits of the distance
			        	DecimalFormat sigFigs = new DecimalFormat("0.000");
			        	
			        	// Check to see if the distance between the users is within 15 kilometers. Otherwise, it will consider it to be too far away to be worthwhile.
			        	if(c <= 15)
			        	{
			        	
			        	// Create a geopoint in the Map activty for later drawing of the other user.
				        Map.createGeoPoint(latFromMessage, lngFromMessage);
				        
				        /**
			        	 * This is the implementation of the bearing formula, which finds the heading the user needs to go in order
			        	 * to reach the other user
			        	 */
			    		//yfinal is opposite of y, as if user 2's were swithed with user 1
			    		double yfinal = Math.sin(-dLon)*Math.cos(deviceLat);
			    		//x final is the same as the above note except for the x variable
			    		double xfinal = Math.cos(sendingLat)*Math.sin(deviceLat) - Math.sin(sendingLat)*Math.cos(deviceLat)*Math.cos(-dLon);
			    		//theta final is bearing at the second point
			    		double thetafinal = Math.atan2(yfinal, xfinal);
			    		//bearing f is theta final converted to degrees
			    		double bearingf = thetafinal*(180/Math.PI);
			    		//bearingfinal is the adjusted bearing at the second point in degerees
			    		double bearingfinal = (bearingf+180)%360;
			    		
			    		// This large list of if / else if statements is to retrieve the direction the user will need to go
			    		int direction = (int) bearingfinal;
			    		String directionToUAV = "";
			    		if(direction == 0 || direction == 360)
			    		{
			    			directionToUAV = "N";
			    		}
			    		
			    		else if (direction > 0 && direction < 45)
			    		{
			    			directionToUAV = "NNE";
			    		}
			    		
			    		else if(direction == 45)
			    		{
			    			directionToUAV = "NE";
			    		}
			    		
			    		else if (direction > 45 && direction < 90)
			    		{
			    			directionToUAV = "ENE";
			    		}
			    		
			    		else if(direction == 90)
			    		{
			    			directionToUAV = "E";
			    		}
			    		
			    		else if (direction > 90 && direction < 135)
			    		{
			    			directionToUAV = "ESE";
			    		}
			    		
			    		else if(direction == 135)
			    		{
			    			directionToUAV = "SE";
			    		}
			    		
			    		else if (direction > 135 && direction < 180)
			    		{
			    			directionToUAV = "SSE";
			    		}
			    		
			    		else if(direction == 180)
			    		{
			    			directionToUAV = "S";
			    		}
			    		
			    		else if (direction > 180 && direction < 225)
			    		{
			    			directionToUAV = "SSW";
			    		}
			    		
			    		else if(direction == 225)
			    		{
			    			directionToUAV = "SW";
			    		}
			    		
			    		else if (direction > 225 && direction < 270)
			    		{
			    			directionToUAV = "WSW";
			    		}
			    		
			    		else if(direction == 270)
			    		{
			    			directionToUAV = "W";
			    		}
			    		
			    		else if (direction > 270 && direction < 315)
			    		{
			    			directionToUAV = "WNW";
			    		}
			    		
			    		else if (direction == 315)
			    		{
			    			directionToUAV = "NW";
			    		}
			    		
			    		else if (direction > 315 && direction < 360)
			    		{
			    			directionToUAV = "NNW";
			    		}
			        	
			    		// Create the second line of the notification
			        	String distanceAway = "Km Away: " + sigFigs.format(c) + " Heading: " + directionToUAV;
			        	
			        	// Instantiate the notification service from the android OS
						String ns = Context.NOTIFICATION_SERVICE;
						NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
						
						// Create the rest of the notification in full and when it should be activated (now).
						int icon = R.drawable.ic_launcher;
						CharSequence tickerText = "Warning! UAV has been spotted in your area!";
						long when = System.currentTimeMillis();
						
						// Instantiate the notification and add all the parts to it
						Notification notification = new Notification(icon,tickerText,when);
						Context context = getApplicationContext();
						RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.guardian_angel_custom_notification);
						contentView.setImageViewResource(R.id.ic_launcher, R.drawable.ic_launcher);
						contentView.setTextViewText(R.id.title, "Spotted at: " + region);
						contentView.setTextViewText(R.id.text, distanceAway);
						notification.contentView = contentView;
						
						// Allow the notification to launch the Map activity upon clicking it
						Intent notificationIntent = new Intent(context, Map.class);
						notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						notificationIntent.putExtra("viewOnly", false);
						PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,0);
						notification.contentIntent = contentIntent;
						
						// Set defaults for the notifications so that it will pop up on any Android device without issue
						notification.flags |= Notification.FLAG_AUTO_CANCEL;
						notification.defaults |= Notification.DEFAULT_ALL;
						
						// Launch the notification
						mNotificationManager.notify(NOTIFICATION_ID, notification);
				        }
				        }	
				        }
					}
					}
				}
			},filter);
						
		}
		
	}
	
	/**
	 * When this Service is closed, safely disconnect the phone from the XMPP server and
	 *  alert the user that they will not receive any more notifications.
	 */
	@Override
	public void onDestroy(){
		connection.disconnect();
		Toast.makeText(this, "UAV NotificationService has stopped!", Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Required to receive active GPS updates, only 3 of the 4 required methods are actually utilized.
	 * This will also automatically produce the notification in the status bar that the application is scanning
	 * for a GPS lock, and will let the user know when one has been achieved.
	 */
	private class MyLocationListener implements LocationListener
	{
		/**
		 * This method will update the information to be sent out in phase 1 data and the current location of the user
		 * when the GPS service noticies that the location has been changed as described above.
		 */
		public void onLocationChanged(Location loc){
			try{
				// Variables to get latitude, longitude, and elevation of the user
		    	double lat;
		        double lng;
		        double elv;
		        
		        // Set the users location to the most up to date one
		        location = loc;

		        // Variables to get the Street Address, City, and State
		        String street;
		        String city;
		        String state;
		        List<Address> myList = null;
		        
		        	// Set the location information if the location could be determined
		        	// otherwise set the location data to null
		        	if (loc != null) 
		        	{
		        		lat = loc.getLatitude();
		        		lng = loc.getLongitude();
		        		elv = loc.getAltitude();
		        		latLongElvString = "Lat=\"" + lat + "\" Lng=\"" + lng + "\" Elv=\"" + elv + "\"";
		            
		        		try 
		        		{
		        			myList = mGeoCoder.getFromLocation(lat, lng, 5);
		        			street = (myList.get(0)).getThoroughfare();
		        			city = 	(myList.get(0)).getLocality();
		        			state = (myList.get(0)).getAdminArea();
		        			geoCodeString = "Str=\"" + street + "\" Cty=\"" + city + "\" Ste=\"" + state + "\"";
						} 
		        		catch (IOException e1) 
		        		{
		        			e1.printStackTrace();
							geoCodeString = "Str=\"null\" Cty=\"null\" Ste=\"null\"";
						}
		        	}
		        	else 
		        	{
		        		latLongElvString = "Lat=\"0.0\" Lng=\"0.0\" Elv=\"0.0\"";
		        		geoCodeString = "Str=\"null\" Cty=\"null\" Ste=\"null\"";
		        	}           	
		        }
		        catch(Exception ex)
		        {
		        	ex.printStackTrace();
		        }
		}
		
		/**
		 * Let the user know if the GPS is accidently or otherwise turned off.
		 */
		public void onProviderDisabled(String arg0) {
			Toast.makeText(getApplicationContext(), "GPS has been disabled", Toast.LENGTH_SHORT).show();
			
		}
		
		/**
		 * Let the user know when the GPS has been successfully enabled and is ready for use.
		 * Note: This does not mean the user has a GPS lock
		 */
		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(), "GPS has been enabled", Toast.LENGTH_SHORT).show();
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// not needed
			
		}
		}
	}
	
