package sate2012.avatar.android;

import gupta.ashutosh.avatar.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.maps.model.LatLng;

/**
 * The Upload Menu Allows the user to select different media types to upload to
 * the server
 */
public class UploadMedia extends Activity implements OnClickListener {
	private File sd;
	private File storageFolder;
	private File mediaFolder;
	private ImageButton pictureB;
	private ImageButton videoB;
	private ImageButton audioB;
	private ImageButton commentB;
	private Button emergency;
	private Button gpsB;
	private String dataType;
	private String media_filepath;
	private String media_filename;
	private String media_extension;
	private static String image_filepath;
	public static Context thisContext;
	public static String ptName;
	public static boolean isEmergency = false;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		thisContext = getApplicationContext();
		setContentView(R.layout.avatar_upload_menu);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		createStorageDirectory();
		pictureB = (ImageButton) findViewById(R.id.cameraButton);
		pictureB.setOnClickListener(this);

		videoB = (ImageButton) findViewById(R.id.videoButton);
		videoB.setOnClickListener(this);
		audioB = (ImageButton) findViewById(R.id.audioButton);
		audioB.setOnClickListener(this);
		commentB = (ImageButton) findViewById(R.id.commentButton);
		commentB.setOnClickListener(this);
//		emergency = (Button) findViewById(R.id.emergency);
//		emergency.setOnClickListener(this);

	}

	/**
	 * Responds to whatever button is pressed
	 * 
	 * @param View
	 *            v - the button clicked
	 */
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case (R.id.cameraButton):
			dataType = getResources().getString(R.string.type_picture);
			i = new Intent(UploadMedia.this, Photographer.class);
			startActivityForResult(i, Constants.CAMERA_REQUEST);
			break;
		case (R.id.videoButton):
			dataType = getResources().getString(R.string.type_video);
			i = new Intent(UploadMedia.this, VideoRecorder.class);
			startActivityForResult(i, Constants.VIDEO_REQUEST);
			break;
		case (R.id.audioButton):
			dataType = getResources().getString(R.string.type_audio);
			i = new Intent(UploadMedia.this, VoiceNotes.class);
			startActivityForResult(i, Constants.VOICE_REQUEST);
			break;
		case (R.id.commentButton):
			dataType = getResources().getString(R.string.type_comment);
			i = new Intent(getApplicationContext(), MailSenderActivity.class);
			i.putExtra("Type", dataType);
			Intent intent = getIntent();
			i.putExtra("LatLng", intent.getParcelableExtra("LatLng"));
			startActivity(i);
			break;
//		case (R.id.emergency):
//			dataType = getResources().getString(R.string.type_emergency);
//			
//			Intent emergencyIntent = getIntent();
//
//			LatLng latlng = (LatLng) emergencyIntent.getParcelableExtra("LatLng");
//			HttpSender connect = new HttpSender();
//			connect.execute("EMERGENCY", latlng.latitude + "",
//					latlng.longitude + "", "0", "EMERGENCY");
//			UploadMedia.isEmergency = true;
//			finish();
//			break;
		}
	}

	/**
	 * Called when the individual activities (picture, video, audio) finish.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == Constants.VIDEO_REQUEST) {
				media_filepath = VideoRecorder.getPath();
				media_extension = "_V.f4v";
			}
			if (requestCode == Constants.VOICE_REQUEST) {
				media_filepath = VoiceNotes.getPath();
				media_extension = "_A.mp4";
			}
			if (requestCode == Constants.CAMERA_REQUEST) {
				media_filepath = getImage_filepath();
				media_extension = "_P.png";
			}
			Intent i = getIntent();

			
			NewFTP ftp = new NewFTP();
			ftp.execute(media_filepath, media_extension);
			System.out.println("FTP RUN");
			
			try {
				media_filename = ftp.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LatLng latlng = (LatLng) i.getParcelableExtra("LatLng");
			Intent MailIntent = new Intent(getApplicationContext(),
					MailSenderActivity.class);
			MailIntent.putExtra("Type", dataType);
			MailIntent.putExtra("Filename", media_filename);
			MailIntent.putExtra("LatLng", latlng);
			startActivity(MailIntent);
			finish();
		}
	}

	public static void setImage_filepath(String fp) {
		image_filepath = fp;
	}

	public String getImage_filepath() {
		return image_filepath;
	}
	
	public static void setPtName(String name){
		ptName = name;
		System.out.println("NAME IS: " + ptName);
	}
	
	public static String getPtName(){
		return ptName;
	}

	@Override
	public void onDestroy() {
		finish();
		super.onDestroy();
	}

	public void createStorageDirectory() {
		sd = Environment.getExternalStorageDirectory();
		storageFolder = new File(sd, Constants.STORAGE_DIRECTORY);
		if (sd.canWrite()) {
			if (!storageFolder.exists())
				storageFolder.mkdir();
			mediaFolder = new File(sd, Constants.STORAGE_DIRECTORY
					+ Constants.MEDIA_DIRECTORY);
			if (!mediaFolder.exists())
				mediaFolder.mkdir();
		}
	}

	public static class HttpSender extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			params[0] = params[0].replaceAll(" ", "%20");
			params[4] = params[4].replaceAll(" ", "%20");
			try {
				HttpClient client = new DefaultHttpClient();
				System.out.println("http://" + Constants.SERVER_FTP_ADDRESS + "/" + Constants.SERVER_SCRIPT_SUBFOLDER + "/sqlAddRow.php?Name=" + params[0]
								+ "&Lat=" + params[1] + "&Long=" + params[2]
								+ "&Alt=" + params[3] + "&Link=" + params[4]);
				HttpGet get = new HttpGet(new URI(
						"http://" + Constants.SERVER_FTP_ADDRESS + "/" + Constants.SERVER_SCRIPT_SUBFOLDER + "/sqlAddRow.php?Name=" + params[0]
								+ "&Lat=" + params[1] + "&Long=" + params[2]
								+ "&Alt=" + params[3] + "&Link=" + params[4]));
				HttpResponse response = client.execute(get);
				System.out.println(new Scanner(response.getEntity().getContent()).nextLine());
			} catch (Exception e) {
				System.out.println("SOMETHING WENT BOOM!");
				e.printStackTrace();
			}
			return null;
		}

	}
	public static class ElevationFinder extends AsyncTask<Double, Double, Double>{

		@Override
		protected Double doInBackground(Double... arg0) {
			double result = Double.NaN;
	        HttpClient httpClient = new DefaultHttpClient();
	        String url = "http://maps.googleapis.com/maps/api/elevation/"
	                + "xml?locations=" + String.valueOf(arg0[0])
	                + "," + String.valueOf(arg0[1])
	                + "&sensor=true";
	        HttpGet httpGet = new HttpGet(url);
	        try {
	            HttpResponse response = httpClient.execute(httpGet);
	            HttpEntity entity = response.getEntity();
	            if (entity != null) {
	                InputStream instream = entity.getContent();
	                int r = -1;
	                StringBuffer respStr = new StringBuffer();
	                while ((r = instream.read()) != -1)
	                    respStr.append((char) r);
	                String tagOpen = "<elevation>";
	                String tagClose = "</elevation>";
	                if (respStr.indexOf(tagOpen) != -1) {
	                    int start = respStr.indexOf(tagOpen) + tagOpen.length();
	                    int end = respStr.indexOf(tagClose);
	                    String value = respStr.substring(start, end);
	                    result = (double)(Double.parseDouble(value)*3.2808399); // convert from meters to feet
	                }
	                instream.close();
	            }
	        } catch (ClientProtocolException e) {} 
	        catch (IOException e) {}

	        return result;
		}
		
	}
}
