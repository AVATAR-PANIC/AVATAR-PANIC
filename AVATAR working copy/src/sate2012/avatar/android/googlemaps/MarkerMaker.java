package sate2012.avatar.android.googlemaps;

import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.JsonReader;

public class MarkerMaker {

	public static ArrayList<MarkerPlus> makeMarkers(){
		HttpThread thread = new HttpThread();
		thread.execute();
		ArrayList<MarkerPlus> markerArray = null;
		try {
			markerArray = thread.get();
		} catch (Exception e){
			System.out.println("BLAHBLAHBLAH");
		}
		return markerArray;
	}
	
	private static class HttpThread extends AsyncTask<String, String, ArrayList<MarkerPlus>>{

		@Override
		protected ArrayList<MarkerPlus> doInBackground(String... arg0) {

			ArrayList<MarkerPlus> markerArray = new ArrayList<MarkerPlus>();
			try {
				
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(new URI("http://10.0.10.147/jsontest.php"));
				HttpResponse response = client.execute(get);
				JsonReader reader = new JsonReader(new InputStreamReader(response.getEntity().getContent()));
				
				reader.beginArray();
				while(reader.hasNext()){
					reader.beginObject();
					MarkerPlus marker = new MarkerPlus();
					while(reader.hasNext()){
						try{
						String name = reader.nextName();
						if(name.equals("Name")){
							System.out.println("NAME: " + reader.nextString());
						}else if(name.equals("Lat")){
							double latitude = reader.nextDouble();
							System.out.println("LATITUDE: " + latitude);
							marker.setLatitude(latitude);
						}else if(name.equals("Long")){
							double longitude = reader.nextDouble();
							System.out.println("LONGITUDE: " + longitude);
							marker.setLongitude(longitude);
						}else if(name.equals("Alt (ft)")){
							double altitude = reader.nextDouble();
							System.out.println("ALTITUDE: " + altitude);
							marker.setAltitude(altitude);
						}else if(name.equals("Date")){
							System.out.println("DATE: " + reader.nextString());
						}else if(name.equals("Link")){
							System.out.println("LINK: " + reader.nextString());
						}
						}catch(IllegalStateException e){
							System.out.println(reader.nextString());
						}
					}
					markerArray.add(marker);
					reader.endObject();	
				}
				reader.endArray();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return markerArray;
		}
	}
}
