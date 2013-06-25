package tricorder.tecedge;

import gupta.ashutosh.avatar.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class PictureUpload extends Activity {
	InputStream is;
	String path;
	ImageView targetImage;
	Bitmap bitmap;

	@Override
	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.tricorder_servercomm);

		// Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),
		// R.drawable.icon);

		targetImage = (ImageView) findViewById(R.id.ivPic);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Uri targetUri = data.getData();
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(targetUri));
			targetImage.setImageBitmap(bitmap);
			new DownloadFilesTask().execute();
			// upload(bitmap);
		} catch (Exception e) {
		}

	}

	public void upload(Bitmap bitmapOrg) {

		if (bitmapOrg == null) {
			Toast.makeText(getApplicationContext(), "null: ", Toast.LENGTH_LONG)
					.show();
			return;
		}
		ByteArrayOutputStream bao = new ByteArrayOutputStream();

		bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 90, bao);

		byte[] ba = bao.toByteArray();

		String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("image", ba1));

		try {

			HttpClient httpclient = new DefaultHttpClient();

			HttpPost httppost = new HttpPost(
					"http://wbi-icc.com/students/SL/Tricorder/uploadAndroid.php");

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();

			is = entity.getContent();

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
		}
	}

	private class DownloadFilesTask extends
			AsyncTask<Context, Integer, Integer> {

		@Override
		protected Integer doInBackground(Context... params) {

			// bitmap = BitmapFactory.decodeResource(getResources(),
			// R.drawable.airforce));

			ByteArrayOutputStream bao = new ByteArrayOutputStream();

			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);

			byte[] ba = bao.toByteArray();

			String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);

			ArrayList<NameValuePair> nameValuePairs = new

			ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("image", ba1));

			try {

				HttpClient httpclient = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wbi-icc.com/students/SL/Tricorder/uploadAndroid.php");

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpclient.execute(httppost);

				HttpEntity entity = response.getEntity();

				is = entity.getContent();

			} catch (Exception e) {

				Log.e("log_tag", "Error in http connection " + e.toString());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {

			super.onPostExecute(result);
		}

	}

}
