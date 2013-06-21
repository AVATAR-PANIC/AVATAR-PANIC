/**
 * This class is used to break down how the XMPP messages are sent. It takes a much more abstract approach for easy implementation of the code
 * in to other programs that might need it. The class has 3 constructors that correspond to the 3 phases of data that the application needs to send. It
 * will also provide the encoding necessary to break down an image to be able to be sent through the XMPP room. It should be noted that each XMPPSender object created
 * by other Activities should be deleted / set to null after use.
 */
package com.guardian_angel.uav_tracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.jivesoftware.smack.packet.Message;

import biz.source_code.base64Coder.Base64Coder;

import android.os.Environment;

/**
 * 
 * @author Michael J. Fox <fox.117@wright.edu>
 * @version 1.0
 * @since 07.30.12
 *
 */

public class XMPPSender{
	
	/**
	 * These variables comprise the guts of the XMPP message. They are set to default values here in the result of some
	 * mishap in calling of the constructors or accessing of the variables.
	 */
	private String additionalInfo = "null";
	private String userLocation = "Lat=\"null\" Lng=\"null\" Elv=\"null\"";
	private String sendTimeStamp = "null";
	private String userGeoCode = "Str=\"null\" Cty=\"null\" Ste=\"null\"";
	private String planeID = "null";
	private String features = "Volume=\"null\" Size=\"null\" Distance=\"null\"";
	private String phonePitch = "null";
	private String userHeading =  "null";
	private ArrayList<String> planePosGuess = new ArrayList<String>();
	private Message msg;
	private String sequence = "";
	private String size = "";
	private String thread = "";
	private String sendThread = "";
	private String imageAttached = "false";
	private String imageType = "null";
	private String imageName = "null";
	private int phase;
	private int numberInStream = CameraRecord.numOfReadings;
	
	/**
	 * This is the constructor for the phase 1 data. As arguments it will take the current location of the user in both
	 * coordinate and region (street, city, state) form. It will then add on the date from the calling method and the ArrayList
	 * containing the coordinates of the position guesses done by the user on the Map Activity. It will also set the phase marker to 1.
	 * @param geoLocation = Coordinates of the user in string format
	 * @param geoCode = Region of the user in string format
	 * @param date = Timestamp from calling activity in string format
	 * @param planeGuess = Position guesses of the UAV in coordinate / string format.
	 */
	public XMPPSender(String geoLocation, String geoCode, String date, ArrayList<String> planeGuess){
		userLocation = geoLocation;
		userGeoCode = geoCode;
		sendTimeStamp = date;
		planePosGuess = planeGuess;
		phase = 1;
		
		
	}
	
	/**
	 * This is the constructor for the phase 2 data. The arguments will be assigned to the private variables of this object.
	 * Those include the current timestamp from the calling activity, as well as the current angle and heading of the phone. The current
	 * stream number will be adjusted, and a null value will be added to the planePosGuess ArrayList in order to prevent breakage later on.
	 * Finally, the phase marker will be set to 2. 
	 * @param date = Timestamp from calling activity in string format
	 * @param pitch = Current angle of the device 
	 * @param heading = Current heading of the device
	 * @param streamNumber = Current message in the stream
	 */
	public XMPPSender(String date, String pitch, String heading, int streamNumber)
	{
		sendTimeStamp = date;
		phonePitch = pitch;
		userHeading = heading;
		planePosGuess.add("null");
		numberInStream = streamNumber;
		phase = 2;
	}
	
	/**
	 * This constructor will be used for phase 3 data. Phase 3 will send the features of the phone, as defined by the user in the UserData Activity
	 * which will also provide a place for the message to be sent. Like the other constructors, the timestamp will be appended to the message. This constructor,
	 * although it corresponds to phase 3 data, will not worry about if a picture needs to be attached. This is dealt with through a later method. Finally,
	 * the constructor will update the phase marker to 3.
	 * @param date = Timestamp from calling activity in string format
	 * @param planeFeatures = Features of the UAV as described by the user (volume, size, distance away)
	 * @param message = Extra information the user would like to put with it (landmarks the UAV is flying next to, etc)
	 */
	public XMPPSender(String date, String planeFeatures, String message)
	{
		sendTimeStamp = date;
		planePosGuess.add("null");
		features = planeFeatures;
		additionalInfo = message;
		phase = 3;
	}
	
	/**
	 * 
	 * This method is used to create the message from the information provided by the specific constructor up above.
	 * It will also append the correct phase information to the username which will be used to differentiate the messages
	 * once its inside the PSSA network. This method will grab all of the variables needed for its phase and use the default
	 * values for the other ones. It will then set the whole String to a single XMPP message, which is saved for later use.
	 * 
	 */
	public void createMessage(){
		//These checks are used to make sure the correct phase is appended to the username
		String phaseNum = "";
		switch(phase)
		{
		case 1:
			phaseNum = "_1@";
			break;
		case 2:
			phaseNum = "_2@";
			break;
		case 3:
			phaseNum = "_3@";
			break;
		}
		
		//Get the XMPP username and add the phase number to it
		String userName = NotificationService.connection.getUser();
		String[] temp = userName.split("@");
		temp[0] = temp[0] + phaseNum;
		userName = temp[0]+temp[1];
		
		//Create the XML styled string that will carry all of the data captured.
		String text = "<TecEdgeXMPPMessage UserID=\"" + userName + "\">\n" + 
	    		"<Message>" + additionalInfo + "</Message>\n" +
	    		"<UserGeoLocation " + userLocation + " />\n" +
	    		"<Date>" + sendTimeStamp + "</Date>\n" +
	    		"<Geocode " + userGeoCode + " />\n" +
	    		"<SentFrom>" + MainActivity.deviceID + "</SentFrom>\n" +
	    		"<PlaneID>" + planeID + "</PlaneID>\n" +
	    		"<Features " + features + "/>\n" +
	    		"<PhoneAngle NoReadings=\""+ numberInStream +"\">" + phonePitch + "</PhoneAngle>\n" +
	    		"<UserHeading NoReadings=\""+ numberInStream +"\">" + userHeading + "</UserHeading>\n" +
	    		"<PlanePosGuess NoReadings=\""+ getNoGuesses() +"\">" + getPosGuesses() + "</PlanePosGuess>\n" +
	    		"<FlightPath NoPoints=\"null\">" + "null" + "</FlightPath>\n" +
	    		"<ImgAttached>" + imageAttached + "</ImgAttached>\n" +
	    		"<Image type=\"" + imageType + "\"  size=\"" + size + "\" name=\"" + imageName + "\" seq=\"" + sequence + "\" />\n" + 
	    		"</TecEdgeXMPPMessage>";
		
		//Create the XMPP message
		msg = NotificationService.muc.createMessage();
    	msg.setType(Message.Type.groupchat); 
    	msg.setBody(text);
    	//This will add on image data should a picture need to be attached.
    	msg.setThread(sendThread);
	}
	
	/**
	 * This method is simply used to send the previously constructed XMPP message. If the message was not successfully created,
	 * it will catch the exception so that the app does not crash.
	 * @return boolean = Tells the calling Activity if the message was sent sucessfully
	 */
	public boolean sendMessage()
	{
		// Attempt to send the message
		try 
		{
			NotificationService.muc.sendMessage(msg);
			return true;
		} 
		
		// If it fails, catch the exception
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * This method is used to convert the coordinates of the position guesses for the UAV into a single String to be sent in the XMPP message.
	 * It will add in delimiting characters to allow for easy reading and parsing at the PSSA bridge end.
	 * @return
	 */
	public String getPosGuesses(){
		String guesses = "";

		// Step through the ArrayList in order to retrieve all of the guesses
		for(int i = 0; i < planePosGuess.size(); i++)
		{
			// Check for the final item in the ArrayList
			if((i + 1) == planePosGuess.size())
			{
				guesses = guesses + planePosGuess.get(i);
			}
			
			// Check for the first item of ArrayList and of each coordinate pair
			else if((i % 2) == 0)
			{
				guesses = guesses + planePosGuess.get(i) + ",";
			}
			
			// For the second part of the coordinate pair, in order to differentiate different guesses
			else{
				guesses = guesses + planePosGuess.get(i) + ";";
			}

		}
		
		// Return the single String to be sent off
		return guesses;
	}

	/**
	 * This method is an old method from an earlier version of the application that has been left in for future use.
	 * Currently, the application only allows for the user to plot 2 points on the map, but using this method it is possible
	 * to track how many points the user puts on the map.
	 * @return noGuesses = String that holds the current number of guesses of where the UAV was
	 */
	public String getNoGuesses(){
		
		// Check to make sure the user is currently sending phase 1 data, if they are, this will not equal null
		if(planePosGuess.get(0).equals("null"))
		{
			// Since the user is not currently sending phase 1 data, the number of guesses can be set to 0
			String noGuesses = "0";
			return noGuesses;
		}
		
		 // Get the number of guesses. The division is required since the ArrayList holds both longitude and latitude.
		else{
			String noGuesses = Integer.toString(planePosGuess.size() / 2);
			return noGuesses;
		}
		
	}
	
	/**
	 * This method is used to send a picture that the user would take during sensor recordings of phase 2. The picture will be found in a predefined directory, located
	 * in the internal memory storage of the device. In order to send a picture with a protocol that only accepts Strings, the picture must be broken down into a 
	 * base64 string of characters and rebuilt on the otherside of the PSSA bridge. This method will have to send multiple messages in order to send the picture, due to the
	 * character limit on the XMPP messages. All of this is done automatically, as long as the picture is in the correct directory.
	 */
	public void sendPicture(){
		
		// Adjust the value to alert the PSSA bridge that there is an image attached.
		imageAttached = "true";
		
		// Find the picture and make a byte array that is the exact length as the length of the picture
		File file = new File(Environment.getExternalStorageDirectory().getPath() + "/UAV_T/Images/uav_pic.jpg");
		byte[] byteArray = new byte[(int) file.length()];
    	try 
    	{
    		// Read in the file to the byte array.
    		FileInputStream fileInputStream = new FileInputStream(file);
    		fileInputStream.read(byteArray);
    		fileInputStream.close();
    	} 
    	catch (FileNotFoundException e1) 
    	{
    		// Make sure the file exists
    		e1.printStackTrace();
    	}
    	catch (IOException e2)
    	{
    		// Make sure there is not an issue with the reading in of the file
    		e2.printStackTrace();
    	}
    	
    	// Get the file path
    	String name = file.getPath();
    	
    	// Remove the extension from the file path
    	int index = name.lastIndexOf("/") + 1;
    	imageName = name.substring(index);
    	
    	// Set the sequence to 0, to indicate this is the first message out of N messages that the image will be in
    	int seq = 0;
    	sequence = "" + seq;
    	
    	// Encode the byte array to a base64 string
    	thread = Base64Coder.encodeLines(byteArray);
    	// Get the number of characters of the base64 string as an int and string
    	long picSize = thread.length();
    	size = "" + picSize;
    	
    	// If the string to send is over 121072 characters, then chunk the info into separate packets
    	int increment = 121072;
    	
    	// This will check to see if the picture can be sent in a single message
        if (picSize <= increment) 
        {
        	// The picture is sent as the thread of the message, which allows for it to be viewed in the XMPP chat room server without a string of characters.
        	sendThread = thread;
        	this.createMessage();
        	this.sendMessage();
        	imageType = "complete-image";
        }
        
        // If the picture is too large for a single message, it will have to be broken down over multiple messages
        else 
        {
        	// Create the lower and upper limits of the String to fit into the message
        	int lowerLimit = 0;
        	int upperLimit = increment;
        	imageType = "partial-image";
        	
        	// While there are still chunks of data left to send
        	while (lowerLimit < picSize) 
        	{
        		sequence = "" + seq;

        		// If this is the last packet to send
        		if (upperLimit > picSize) 
        		{
        			sendThread = thread.substring(lowerLimit);
        			this.createMessage();
        			this.sendMessage();

        		}
        		
        		// Send the message with the broken chunk of the image
        		else{
        		sendThread = thread.substring(lowerLimit, upperLimit);
        		this.createMessage();
    			this.sendMessage();
        		}
        		seq++;
        		lowerLimit = lowerLimit + increment;
        		upperLimit = upperLimit + increment;
        	}
        }
        
        // This will delete the picture taken off of the SD card in order to save space.
        file.delete();
    	
	}
	
}