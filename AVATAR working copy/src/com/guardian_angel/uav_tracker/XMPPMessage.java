package com.guardian_angel.uav_tracker;

import org.jivesoftware.smack.packet.Message;


/**
 * @author      Michael Duncan <duncan.72@wright.edu>
 * @version     1.3
 * @since       2011.0721
 * 
 * Reads in an official XMPP message and converts it to a message
 * object that contains all the relevant data.
 */
public class XMPPMessage
{
	private boolean image = false;
	private String timeStamp = "";
	private String name = "";
	private String text = "";
	private String packetID = "";
	private String region = "";
	private String coords = "";
	
	/**
	 * Constructor that creates a usable message by extracting information from
	 * the input parameter message.
	 *
	 * @param  msg	The XMPP message to read data from
	 */
	public XMPPMessage(Message msg)
	{
		String body = msg.getBody();
		
		// Get if there is an image attached
		int iIndex1 = body.indexOf("<ImgAttached>") + 13;
        int iIndex2 = body.indexOf("</ImgAttached>");
        if ((iIndex1 != 12) && (iIndex2 != -1))
        {
        	String bImage = body.substring(iIndex1, iIndex2);
        	if (bImage.equalsIgnoreCase("true"))
        	{
        		image = true;
        	}
        }
		
		// Get the time the message was sent
        int tIndex1 = body.indexOf("<Date>") + 6;
        int tIndex2 = body.indexOf("</Date>");
        if ((tIndex1 != 5) && (tIndex2 != -1))
        {
        	timeStamp = "(" + body.substring(tIndex1, tIndex2) + ") ";
        }
        
        // Get the name of the sender
		String fromName = msg.getFrom();
        int nIndex1 = fromName.indexOf("/") + 1;
        int nIndex2 = fromName.indexOf("@", nIndex1);   
        if (nIndex2 == -1) 
        {
			name = fromName.substring(nIndex1);
		}
		else 
		{
			name = fromName.substring(nIndex1, nIndex2);
		}
        
        // Get the message text
        int bIndex1 = body.indexOf("<Message>") + 9;
        int bIndex2 = body.indexOf("</Message>");
        if ((bIndex1 == 8) || (bIndex2 == -1)) 
        {
    		text = body;
    	}
    	else 
    	{
    		text = body.substring(bIndex1, bIndex2);
    	}
        
        // Get the packet ID
        packetID = msg.getPacketID();
        
        // Get the region where the message was sent from (City, State)
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
        
        // Get the coordinates from where the message was sent
        int cIndex1 = body.indexOf("Lng=\"") + 5;
        int cIndex2 = body.indexOf("\"", cIndex1);
        if ((cIndex1 != -1) && (cIndex2 != -1))
        {
        	coords = "Lat: " + body.substring(cIndex1, cIndex2);
        }
        int cIndex3 = body.indexOf("Lat=\"") + 5;
        int cIndex4 = body.indexOf("\"", cIndex3);
        if ((cIndex3 != -1) && (cIndex4 != -1))
        {
        	coords = coords + " Lng: " + body.substring(cIndex3, cIndex4);
        }
        int cIndex5 = body.indexOf("Elv=\"") + 5;
        int cIndex6 = body.indexOf("\"", cIndex5);
        if ((cIndex3 != -1) && (cIndex4 != -1))
        {
        	coords = coords + " Elv: " + body.substring(cIndex5, cIndex6);
        }
	}
	
	/**
	 * Returns if an image is attached to the message.
	 * 
	 * @return	Returns true if an image is attached
	 */
	public boolean isImage()
	{
		return image;
	}

	/**
	 * Returns the time stamp of the message.
	 * 
	 * @return	Returns the time stamp as a string
	 */
	public String getTimeStamp() 
	{
		return timeStamp;
	}

	/**
	 * Returns the name of the message sender.
	 * 
	 * @return	Returns the name as a string
	 */
	public String getName() 
	{
		return name;
	}

	/**
	 * Returns the text of the message.
	 * 
	 * @return	Returns the text as a string
	 */
	public String getText() 
	{
		return text;
	}

	/**
	 * Returns the packet ID of the message.
	 * 
	 * @return	Returns the packet ID as a string
	 */
	public String getPacketID() 
	{
		return packetID;
	}

	/**
	 * Returns the region where the message was sent from.
	 * 
	 * @return	Returns the region as a string
	 */
	public String getRegion() 
	{
		return region;
	}

	/**
	 * Returns the coordinates of where the message was sent from.
	 * 
	 * @return	Returns the coordinates as a string
	 */
	public String getCoords() 
	{
		return coords;
	}
}
