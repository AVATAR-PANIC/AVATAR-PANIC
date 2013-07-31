package sate2012.avatar.android;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import android.os.AsyncTask;
import android.util.Log;

public class UploadFTP extends AsyncTask<String, String, String> {

	public String doInBackground(String... params) {
		System.out.println("FTP START");
		FTPClient ftpClient = new FTPClient();
		ftpClient.setDefaultPort(23);
		Log.d("HELP", ftpClient.toString());
		long time = (System.currentTimeMillis());
		String filename = "T" + time;
		int exitVar = 0;
		try {
			ftpClient.connect("sftp://www.virtualdiscoverycenter.net", 23);
			while (exitVar == 0) {

				System.out.println("I'm not stuck");
				if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
					exitVar = 1;
				}
			}
			ftpClient.login(Constants.username, Constants.password);
			System.out.println(Constants.username);
			ftpClient.changeWorkingDirectory("/var/www/AVATAR");
			// Looper.prepare();
			// if (ftpClient.getReplyString().contains("250")) {
			// Handler progressHandler = new Handler();
			ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
			BufferedInputStream buffIn = null;
			System.out.println(params[0]);
			buffIn = new BufferedInputStream(new FileInputStream(params[0]));
			ftpClient.enterLocalPassiveMode();
			// ProgressInputStream progressInput = new ProgressInputStream(
			// buffIn, progressHandler);
			ftpClient.storeFile(filename + params[1], buffIn);
			System.out.println(ftpClient.getReplyString());
			System.out.println("File Sent");
			System.out.println(filename + params[1]);
			buffIn.close();
			System.out.println("DIR: " + ftpClient.printWorkingDirectory());
			System.out.println(ftpClient.getReplyString());
			ftpClient.logout();
			ftpClient.disconnect();
			// }
		} catch (IOException e) {
			System.out.println("WHOOPS");
			e.printStackTrace();
		}
		System.out.println("COMPLETE LOOPER");
		return filename + params[1];
	}
}