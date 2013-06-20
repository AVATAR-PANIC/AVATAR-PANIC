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
	private String FILENAME = "AVATAR_DEVICEINFO";
	private Context context;
	private String ID;
	private String Tag;
	
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
		try{
			System.out.println("Getting File");
			File file = context.getFileStreamPath(FILENAME);
			System.out.println("Got File");
			
			//Used for debugging, should be without the "!".
			//For now since it has errors, will always try to grab from the server
			//To see if the data parses correctly. Once that works, remove the "!".
			if(!file.exists()){
				//Use the ID already on the device.
				System.out.println("File Exists");
				FileInputStream in = context.openFileInput(FILENAME);
				InputStreamReader inputStreamReader = new InputStreamReader(in);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				StringBuilder sb = new StringBuilder();
				String line;
				
				System.out.println("Appending String");
				while((line = bufferedReader.readLine()) != null){
					sb.append(line);
				}
				System.out.println(sb);
				in.close();
				inputStreamReader.close();
				bufferedReader.close();
				return true;
				
			}else{
				//Connect to server and get ID.
				try{
					System.out.println("Connecting to the server for ID");
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet(new URI(
							"http://" + Constants.SERVER_ADDRESS + "/sqlManageUsers.php"));
					HttpResponse response = client.execute(get);
					Scanner reader = new Scanner(response.getEntity().getContent());
					
					this.ID = reader.next();
					this.Tag = reader.next();
					
					FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
					fos.write((this.ID + " " + this.Tag).getBytes());
					fos.close();
					reader.close();
					
					System.out.println("All streams to server closed");
					
					return true;
				}catch(Exception ex){
					System.err.println("Something went wrong with my script :(");
					ex.printStackTrace();
					return false;
				}
			}
		}catch(Exception ex){
			System.err.println("Something Wrong with accessing the file");
			ex.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * What happens on post execute, should print one of the statements.
	 * @param bool : Whether it was successful or not.
	 */
	@Override
	public void onPostExecute(Boolean bool){
		
		if(bool){
			System.err.println("Success! The ID is " + this.ID + " and the Tag is : " + this.Tag);
		}else{
			System.err.println("Something went horribly wrong! Oh No!");
		}
		//TODO perform operations on the Android device.
	}
}