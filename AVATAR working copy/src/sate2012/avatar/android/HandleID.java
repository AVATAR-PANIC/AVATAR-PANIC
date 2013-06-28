package sate2012.avatar.android;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.widget.Toast;

/**
 * 
 * @author Garrett
 * 
 * Class that should get an ID from the server and assign it to the device if
 * it doesn't have one already.
 *
 */
public class HandleID extends AsyncTask<Void, Void, Boolean> {

	//Variables
	private String FILENAME = "THE_USER_ID_AVATAR2"; //IMPORTANT! IF CHANGED ALSO CHANGE AVATARMAPSETTINGS!
	private Context context;
	public static String ID;
	public static String Tag;
	public static String Status;
	
	/**
	 * Need context for writing data to the device
	 * @param context : The activity
	 */
	public HandleID(Context context){
		System.out.println("Assigning the context");
		this.context = context;
	}
	
	public Boolean doInBackground(Void...voids){
		System.out.println("Starting task to get ID");
		Boolean bool = false;
		while(!bool){ //Need this while statement, Android will occasionally not run this correctly without.
			try{
				//System.out.println("Getting File");
				File file = context.getFileStreamPath(FILENAME);
				//System.out.println("Got File");
				
				if(file.exists()){
					//Use the ID already on the device.
					System.out.println("File Exists");
					FileInputStream in = context.openFileInput(FILENAME);
					//System.out.println(file.getAbsolutePath());
					InputStreamReader inputStreamReader = new InputStreamReader(in);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					StringBuilder sb = new StringBuilder();
					String line;
					
					//System.out.println("Appending String");
					while((line = bufferedReader.readLine()) != null){
						sb.append(line);
					}
					
					String lines = sb.toString();
					String[] info = lines.split("\\|");
					//System.out.println(sb.toString());
					
//					System.out.println("Info 0: " + info[0]);
//					System.out.println("Info 1: " + info[1]);
//					System.out.println("Info 2: " + info[2]);
					HandleID.ID = info[0];
					HandleID.Tag = info[1];
					HandleID.Status = info[2];
					if(HandleID.ID.equals("Null")){
						((File) context.getFileStreamPath(FILENAME)).delete();
					}
					in.close();
					inputStreamReader.close();
					bufferedReader.close();
					bool = true;
					
				}else{
					//Connect to server and get ID.
					try{
						System.out.println("Connecting to the server for ID");
						HttpClient client = new DefaultHttpClient();
						HttpGet get = new HttpGet(new URI(
								"http://" + Constants.SERVER_ADDRESS + "/sqlManageUsers.php"));
						HttpResponse response = client.execute(get);
						Scanner reader = new Scanner(response.getEntity().getContent());
						
						HandleID.ID = reader.next();
						HandleID.Tag = "NONE";
						HandleID.Status = "NONE";
						
						FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_APPEND);
						fos.write((HandleID.ID + "|" + HandleID.Tag + "|" + HandleID.Status).getBytes());
						fos.flush();
						fos.close();
						reader.close();
						
						System.out.println("All streams to server closed");
						
						bool = true;
					}catch(Exception ex){
						System.err.println("Something went wrong with my script :(");
						ex.printStackTrace();
						bool = false;
					}
				}
			}catch(Exception ex){
				System.err.println("Something Wrong with accessing the file");
				ex.printStackTrace();
				bool = false;
			}
			
			}
		
		return bool;
		
	}
	
	/**
	 * What happens on post execute, should print one of the statements.
	 * @param bool : Whether it was successful or not.
	 */
	@Override
	public void onPostExecute(Boolean bool){
		//System.out.println("What happened?");
		if(bool){
			System.err.println("Success! The ID is " + HandleID.ID + " and the Tag is : " + HandleID.Tag);
		}else{
			System.err.println("Something went horribly wrong! Oh No!");
		}
		//TODO perform operations on the Android device.
	}
}
