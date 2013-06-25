package tricorder.tecedge;

import gupta.ashutosh.avatar.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Password_request extends Activity {
	static public String[] password;
	LocalGraphs lg;
	InputStream is;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		password = LocalGraphs.projPassword;
		lg = new LocalGraphs();
		// if password is not on the database
		if (Array.getLength(password) == 0) {
			setContentView(R.layout.tricorder_password_create);
			Button submit = (Button) findViewById(R.id.submit);
			final EditText passwordfield = (EditText) findViewById(R.id.passwordField);
			final EditText passwordfield2 = (EditText) findViewById(R.id.passwordField2);
			submit.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// check if passwords are the same
					if (passwordfield.getText().toString()
							.equals(passwordfield2.getText().toString())
							&& passwordfield.getText().toString().length() >= 5) {
						ServerComm.create_password(passwordfield.getText()
								.toString());
						String something = ServerComm.upload_data();
						new DownloadFilesTask().execute();

						String toasty;
						if (!something.contains("error")) {
							toasty = "Upload Complete";
						} else {
							toasty = "Upload Error";
						} // creates the upload toast
						Toast myToast = Toast.makeText(Password_request.this,
								toasty, Toast.LENGTH_SHORT);

						myToast.show();

						finish();
					} else {
						Toast.makeText(
								getApplicationContext(),
								"Make sure passwords match and are at least 5 characters.",
								Toast.LENGTH_LONG).show();
						passwordfield.setText("");
						passwordfield2.setText("");
					}
				}
			});
		} else {
			setContentView(R.layout.tricorder_password_request);
			Button submit = (Button) findViewById(R.id.submit);
			final EditText passwordfield = (EditText) findViewById(R.id.passwordField);
			submit.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (passwordfield.getText().toString().equals(password[0])) {

						String something = ServerComm.upload_data();
						new DownloadFilesTask().execute();
						String toasty;
						if (!something.contains("error")) {
							toasty = "Upload Complete";
						} else {
							toasty = "Upload Error";
						} // creates the upload toast
						Toast myToast = Toast.makeText(Password_request.this,
								toasty, Toast.LENGTH_SHORT);

						myToast.show();

						finish();
					} else {

						Toast.makeText(getApplicationContext(),
								"Incorrect password", Toast.LENGTH_SHORT)
								.show();
						passwordfield.setText("");
					}
					Log.d("hi", passwordfield.getText().toString());
				}
			});
		}

	}

	public class DownloadFilesTask extends AsyncTask<Context, Integer, Integer> {

		@Override
		protected Integer doInBackground(Context... params) {

			// bitmap = BitmapFactory.decodeResource(getResources(),
			// R.drawable.airforce));

			ByteArrayOutputStream bao = new ByteArrayOutputStream();

			ServerComm.bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);

			byte[] ba = bao.toByteArray();

			String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);

			ArrayList<NameValuePair> nameValuePairs = new

			ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("image", ba1));
			nameValuePairs.add(new BasicNameValuePair("name",
					ServerComm.dbFormat + ServerComm.android_id));

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
	}

}