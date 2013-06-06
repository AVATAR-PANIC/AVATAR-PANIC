package sate2012.avatar.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.mapsforge.android.maps.GeoPoint;

import android.content.Context;
import android.widget.Toast;

public class LocationDataReceiverAVATAR {

	private LocationDataReceiverAVATAR() {
	}

	public static String loadDataStringFromURL() {
		String input = null;
		try {
			URL url = new URL(
					"http://virtualdiscoverycenter.net/avatar/php_files/email_rec_VW.php");

			URLConnection connection = url.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			if (connection.getInputStream() != null) {
				System.out.println("inputStream is not null");
			} else {
				System.out.println("inputStream is null");
			}

			input = in.readLine();

			in.close();

			return input;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		final char star = (char) 42;
		final char bslash = (char) 92;
		final String splitter = "_" + bslash + star + bslash + star + bslash
				+ star + "_";
		return "39.7_***_-84.2_***_Photo_***_virtualdiscoverycenter.net/../../var/www/avatar/uploadedT1342804869789_P.png";
	}
}
// ***_39.7_
// ***_-84.2_
// ***_Photo_
// ***_virtualdiscoverycenter.net/../../var/www/avatar/uploadedT1342804869789_P.png
// @@@POINT: Jason's phone _
// ***_39.744198275730014_
// ***_-84.06325832940638_
// ***_Android_
// ***_358940040608809
// @@@POINT: fairwood and Andrea w_
// ***_39.74605109542608_
// ***_-84.06306638382375_
// ***_Audio_
// ***_virtualdiscoverycenter.net/../../var/www/avatar/uploadedT1342575995075_A.mp4
// @@@POINT: rich and willow creek_
// ***_39.74753949683275_
// ***_-84.06054045404257_
// ***_Photo_
// ***_virtualdiscoverycenter.net/../../var/www/avatar/uploadedT1342575610631_P.png
// @@@POINT: fairwood and rich_
// ***_39.74535351153463_
// ***_-84.06127944588661_
// ***_Video_
// ***_virtualdiscoverycenter.net/../../var/www/avatar/uploadedT1342575338419_V.f4v
// @@@POINT: fairwood and old north fairfield_
// ***_39.74523251876235_
// ***_-84.05700098723173_
// ***_Comment_
// ***_intersection
// @@@POINT: 3349 andrea_
// ***_39.745109556242824_
// ***_-84.05778947286308_
// ***_Photo_
// ***_virtualdiscoverycenter.net/../../var/www/avatar/uploadedT1342574804267_P.png
// @@@POINT: home_
// ***_39.74332961719483_
// ***_-84.06092271208763_
// ***_Photo_
// ***_virtualdiscoverycenter.net/../../var/www/avatar/uploadedT1342573881519_P.png
// @@@END OF MESSAGES

/*
 * //@Override public void onCreate(Bundle savedInstanceState) {
 * super.onCreate(savedInstanceState); setContentView(R.layout.map_view); Intent
 * intent = getIntent(); String message =
 * intent.getStringExtra(MapsForgeMapViewer.EXTRA_MESSAGE);
 * CoordinateDataTranslator();
 * 
 * }
 * 
 * 
 * @Override public boolean onCreateOptionsMenu(Menu menu) { //
 * getMenuInflater()
 * .inflate(R.upload_menu.activity_location_data_receiver_avatar, // menu);
 * return true; }
 */
