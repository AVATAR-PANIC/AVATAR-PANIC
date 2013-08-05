package sate2012.avatar.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;

import android.os.AsyncTask;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class NewFTP extends AsyncTask<String, String, String>{

	/**
	 * This class is used to demonstrate the usage of the JCraft JSch package to
	 * SFTP files. Edited to work with the AVATAR App.
	 * 
	 * @author Tim Archer 04/20/07 edits by Matthew Weber
	 * @version $Revision: 1.1 $
	 */

	/** Creates a new instance of TestCommonsNet */
	public NewFTP() {
	}

	/**
	 * main - Unit test program
	 * 
	 * @param args
	 *            Command line arguments
	 * 
	 */
	public String doInBackground(String... params) {
		String filename = null;
		try {
			String ftpHost = Constants.SERVER_FTP_ADDRESS;
			System.out.println(ftpHost);
			int ftpPort = 23;
			String ftpUserName = Constants.username;
			String ftpPassword = Constants.password;
			String ftpRemoteDirectory = "/var/www/" + Constants.SERVER_SCRIPT_SUBFOLDER + "/media";
			String fileToTransmit = params[0];
			long time = (System.currentTimeMillis());
			filename = "T" + time;

			//
			// First Create a JSch session
			//
			System.out.println("Creating session.");
			JSch jsch = new JSch();
			Session session = null;
			Channel channel = null;
			ChannelSftp c = null;

			//
			// Now connect and SFTP to the SFTP Server
			//
			try {
				// Create a session sending through our username and password
				session = jsch.getSession(ftpUserName, ftpHost, ftpPort);
				System.out.println("Session created.");
				session.setPassword(ftpPassword);
				// Security.addProvider(new com.sun.crypto.provider.SunJCE());

				//
				// Setup Strict HostKeyChecking to no so we dont get the
				// unknown host key exception
				//
				java.util.Properties config = new java.util.Properties();
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);
				session.connect();
				System.out.println("Session connected.");

				//
				// Open the SFTP channel
				//
				System.out.println("Opening Channel.");
				channel = session.openChannel("sftp");
				channel.connect();
				c = (ChannelSftp) channel;
			} catch (Exception e) {
				System.err.println("Unable to connect to FTP server. " + e.toString());
				throw e;
			}

			//
			// Change to the remote directory
			//
			
			System.out.println("Changing to FTP remote dir: " + ftpRemoteDirectory);
			c.cd(ftpRemoteDirectory);

			//
			// Send the file we generated
			//
			try {
				File f = new File(fileToTransmit);
				System.out.println("Storing file as remote filename: " + f.getName());
				c.put(new FileInputStream(f), filename + params[1]);
			} catch (Exception e) {
				System.err.println("Storing remote file failed. " + e.toString());
				throw e;
			}

			//
			// Disconnect from the FTP server
			//
			try {
				c.quit();
				session.disconnect();
				
			} catch (Exception exc) {
				System.err.println("Unable to disconnect from FTP server. " + exc.toString());
			}

		} catch (Exception e) {
			System.err.println("Error: " + e.toString());
			e.printStackTrace();
		}

		System.out.println("Process Complete.");
		return filename + params[1];
	}
}
