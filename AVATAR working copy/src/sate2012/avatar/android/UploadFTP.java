package sate2012.avatar.android;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.net.InetAddress;
import org.apache.commons.net.ftp.FTPClient;

public class UploadFTP extends AsyncTask<String, String, String> {

	public String doInBackground(String... params) {
		System.out.println("FTP START");
		FTPClient ftpClient = new FTPClient();
		long time = (System.currentTimeMillis());
		String filename = "T" + time;
		try {
			ftpClient.connect(InetAddress.getByName("10.0.1.189"));
			ftpClient.login("Sean", "");
			Looper.prepare();
			ftpClient.changeWorkingDirectory("AVATAR Pictures/");
			//if (ftpClient.getReplyString().contains("250")) {
				Handler progressHandler = new Handler();
				ftpClient
						.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
				BufferedInputStream buffIn = null;
				buffIn = new BufferedInputStream(new FileInputStream(params[0]));
				ftpClient.enterLocalPassiveMode();
				ProgressInputStream progressInput = new ProgressInputStream(
						buffIn, progressHandler);
				ftpClient.storeFile(filename + params[1], progressInput);
				System.out.println("File Sent");
				System.out.println(filename + params[1]);
				buffIn.close();
				ftpClient.logout();
				ftpClient.disconnect();
			//}
		} catch (IOException e) {
			System.out.println("WHOOPS");
		}
		return filename + params[1];
	}
}