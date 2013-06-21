package sate2012.avatar.android.googlemaps;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import sate2012.avatar.android.Constants;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

class GoogleMapsViewerImage {
	
	final GoogleMapsViewer outer;
	
	public GoogleMapsViewerImage(GoogleMapsViewer outer){
		this.outer = outer;
	}
	
	
    class ImageGrabber extends AsyncTask<String, Void,  Bitmap>{

		private ImageView imageSlot;
		private GoogleMapsViewer map;
		private String url;
		
		/**
		 * Constructor, make sure to put the ImageView where we want the image to display.
		 * @param imageSlot : The ImageView where we want the image to appear.
		 * @param map : The map that we want to have the image to appear on (Should be a reference to above class).
		 */
		public ImageGrabber(ImageView imageSlot, GoogleMapsViewer map){
			this.imageSlot = imageSlot;
			this.map = map;
		}
		
		/**
		 * Asynchronous task that gets the Image from a URL
		 * @return : The Bitmap the method got from the URL
		 */
		@Override
		protected Bitmap doInBackground(String...params) {
			// TODO Auto-generated method stub
			try {
				outer.currentImage = null;
				url = params[0];
				if(outer.asyncTaskCancel){
					//System.out.println("CANCEL 1");
					outer.asyncTaskCancel = false;
					outer.gettingURL = false;
					outer.currentImage = null;
					this.cancel(true);
				}
				//System.out.println("Getting URL!");
				
				//Get the connection, set it to a bitmap
				HttpURLConnection connection = (HttpURLConnection) new URL(params[0]).openConnection();
			    connection.connect();
			    connection.setConnectTimeout(5000);
			    connection.setReadTimeout(5000);
			    InputStream input = connection.getInputStream();
			    Bitmap x = BitmapFactory.decodeStream(input);
			    
			    //Max Image Height and Width
			    int MAXWIDTH = 150;//= 270;
			    int MAXHEIGHT = 100;//=150;
			    
			    if(x != null){
				    int imageWidth = x.getWidth();
				    int imageHeight = x.getHeight();
				    
				    //Find out if image dimensions are too large, then sizes it appropriately.
				    if(imageWidth > MAXWIDTH || imageHeight > MAXHEIGHT){
				    	
				    	//Ternary, determines which is larger: image or height?
				    	double ratio = (imageWidth > imageHeight)? ((float) MAXWIDTH)/imageWidth: ((float) MAXHEIGHT)/imageHeight;
				    	
				    	imageWidth =(int) (imageWidth*ratio);
				    	imageHeight =(int) (imageHeight*ratio);
				    	
				    	x = Bitmap.createScaledBitmap(x, imageWidth, imageHeight, false); //Create scaled Bitmap
				    }
				    
				    if(outer.asyncTaskCancel){
				    	//System.out.println("CANCEL 2");
				    	outer.asyncTaskCancel = false;
				    	outer.gettingURL = false;
				    	outer.currentImage = null;
				    	x = null;
				    	this.cancel(true);
				    }
				    
				    //close connections, return the x value. Goes to OnPostExecute() method.
				    input.close();
				    connection.disconnect();
					return x;
				    }
			    input.close();
			    connection.disconnect();
			    return null;
			} catch (Exception e){
				outer.gettingURL = false;
				e.printStackTrace();
				return null;
			}
		}
		
		/**
		 * Runs after the thread, sets the image and calls the re-draw method if image is correct.
		 */
		@Override
		protected void onPostExecute(Bitmap results){
			if(outer.asyncTaskCancel && !(outer.activeMarker.getSnippet().substring(outer.activeMarker.getSnippet().lastIndexOf(" ")).equals(url))){
				//System.out.println("CANCEL 3");
				outer.asyncTaskCancel = false;
				outer.gettingURL = false;
				results = null;
				outer.currentImage = null;
				map.drawMarkers(true);
				this.cancel(true);
			}else if(!(outer.activeMarker.getSnippet().substring(outer.activeMarker.getSnippet().lastIndexOf(" ")).equals(url))){
				//System.out.println("CANCEL 5");
				outer.asyncTaskCancel = false;
				outer.gettingURL = false;
				results = null;
				outer.currentImage = null;
				map.drawMarkers(true);
				this.cancel(true);
			}else{
				imageSlot.setImageBitmap(results);
				outer.currentImage = results;
				map.drawMarkers(true);
				outer.gettingURL = false;
				imageSlot = null;
				map = null;
			}
			
		}
			
	}
    
    class PointDeleter extends AsyncTask<String, Void, Boolean>{

		@Override
		protected Boolean doInBackground(String... params) {
			Boolean deleted = new Boolean(false);
			// TODO Auto-generated method stub
			int tries = 0;
			while(tries < 3){
				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
					nameValuePairs.add(new BasicNameValuePair("date", params[0]));
					System.out.println("TRYING TO CONNECT");
					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(new URI("http://" + Constants.SERVER_ADDRESS + "/deletePoint.php"));
					post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					client.execute(post);
					//HELP!!!
					tries = 3;
				} catch (Exception e) {
					e.printStackTrace();
					tries++;
				}
			}
			return deleted;
		}
	}
}
