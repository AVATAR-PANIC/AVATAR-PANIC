package sate2012.avatar.android.googlemaps;

import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import sate2012.avatar.android.Constants;
import sate2012.avatar.android.googlemaps.augmentedreality.CameraView;
import android.os.AsyncTask;
import android.util.JsonReader;

/**
 * 
 * @author ? + Tweaking by Garrett Emrick emrickgarrett@gmail.com
 * 
 * Reworked this thread to be asynchronous and to continue establishing connections.
 * Will now use methods in both camera view and the Google Maps Viewer to update
 * the points, as to not make those applications wait on them.
 *
 */
public class HttpThread extends AsyncTask<String, MarkerPlus, ArrayList<MarkerPlus>>{

		private GoogleMapsViewer maps;
		private CameraView cameraView;
		private EagleEyeGoogleMapsViewer eMap;
		private boolean success;
		
		/**
		 * Constructor. Use this for the GoogleMapsViewer
		 * @param maps : The GoogleMapsViewer
		 */
		public HttpThread(GoogleMapsViewer maps){
			this.maps = maps;
		}
		
		/**
		 * Constructor. Use this for the CameraView
		 * @param view : The CameraView
		 */
		public HttpThread(CameraView view){
			this.cameraView = view;
		}
		
		/**
		 * Constructor. Use this for Eagle Eye
		 * @param eMap : The Eagle Eye map.
		 */
		public HttpThread(EagleEyeGoogleMapsViewer eMap){
			this.eMap = eMap;
		}
		
		
		/**
		 * Asynchronous task, runs in background. Gets the connection for the application to the server
		 */
		@Override
		protected ArrayList<MarkerPlus> doInBackground(String...args) {

			ArrayList<MarkerPlus> markerArray = new ArrayList<MarkerPlus>();
			int tries = 0;
			
			connect:
			while(tries < 3){
				try {
					System.out.println("TRYING TO CONNECT");
					System.out.println(Constants.SERVER_SCRIPT_SUBFOLDER);
					HttpClient client = new DefaultHttpClient();
					
					HttpGet get= new HttpGet(new URI("http://" + Constants.SERVER_FTP_ADDRESS + "/"+ Constants.SERVER_SCRIPT_SUBFOLDER + "/jsonPoints.php"));;
					if(eMap != null){ //If it is an Eagle Eye map, use different php script.
						get= new HttpGet(new URI("http://" + Constants.SERVER_FTP_ADDRESS + "/" + Constants.SERVER_SCRIPT_SUBFOLDER + "/jsonPointsEE.php"));
					}
					
					HttpResponse response = client.execute(get);
					JsonReader reader = new JsonReader(new InputStreamReader(response.getEntity().getContent()));
					reader.setLenient(true);
					reader.beginArray();
					while(reader.hasNext()){
						reader.beginObject();
						String data = "";
						MarkerPlus marker = new MarkerPlus();
						while(reader.hasNext()){
							try{
							String name = reader.nextName();
							if(name.equalsIgnoreCase("Name")){
								String pointName = reader.nextString();
								//System.out.println("NAME: " + pointName);
								marker.setName(pointName);
							}else if(name.equalsIgnoreCase("Lat")){
								double latitude = reader.nextDouble();
								//System.out.println("LATITUDE: " + latitude);
								marker.setLatitude(latitude);
							}else if(name.equalsIgnoreCase("Long") || name.equalsIgnoreCase("lon")){
								double longitude = reader.nextDouble();
								//System.out.println("LONGITUDE: " + longitude);
								marker.setLongitude(longitude);
							}else if(name.equalsIgnoreCase("Alt (ft)")){
								double altitude = reader.nextDouble();
								//System.out.println("ALTITUDE: " + altitude);
								marker.setAltitude(altitude);
							}else if(name.equalsIgnoreCase("Date")){
								String date = reader.nextString();
								data += "Upload Date: " + date + "\r\n";
								//System.out.println("DATE: " + date);
							}else if(name.equalsIgnoreCase("Link") || name.equalsIgnoreCase("mesg")){
								String link = reader.nextString();
								data += "Data: " + link;
								//System.out.println("LINK: " + link);
								//marker.setImage(Drawable.createFromStream(((InputStream)new java.net.URL(link).getContent()), "BLAH"));
							}else{
								reader.nextString();
							}
							}catch(IllegalStateException e){
								System.out.println(reader.nextString());
							}
						}
						marker.setData(data);
						markerArray.add(marker);
						reader.endObject();	
					}
					reader.endArray();
					reader.close();
					break connect;
				} catch (Exception e) {
					e.printStackTrace();
					tries++;
				}
			}
			return markerArray;
		}
		
		/**
		 * Called after the doInBackground is complete.
		 * Puts the markers where they need to go.
		 */
		@Override
		public void onPostExecute(ArrayList<MarkerPlus> array){
			
			if(array!= null){
				if(maps != null){
					maps.setMarkerArray(array);
					maps.drawMarkers(true);
					maps = null;
				}else if(cameraView != null){
					cameraView.setMarkerArray(array);
					cameraView = null;
				}else if(eMap != null){
					eMap.setMarkers(array);
					eMap.drawMarkers(true);
					eMap = null;
				}
			}
		}
}
