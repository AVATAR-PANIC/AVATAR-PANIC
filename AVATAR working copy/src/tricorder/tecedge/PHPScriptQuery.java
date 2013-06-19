package tricorder.tecedge;

/*
 * Author: Oliver Ceccopieri
 * 
 * Contributors: 
 * 
 * Last edited 6/20/12
 * 
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class PHPScriptQuery {

	private String ip, username, password, database;

	public PHPScriptQuery() {
		ip = "wbi-icc.com";
		username = "p26t2to0_Student";
		password = "Summer2012";
		database = "p26t2to0_Student";
	}

	/*
	 * can use this as a generic database connection class
	 * 
	 * public PHPScriptQuery(String ipI, String usernameI, String passwordI,
	 * String databaseI) { ip = ipI.replaceAll("\\s+", "%20"); username =
	 * usernameI.replaceAll("\\s+", "%20"); password =
	 * passwordI.replaceAll("\\s+", "%20"); database =
	 * databaseI.replaceAll("\\s+", "%20"); }
	 */
	public String execute(String query) {
		String finalLine = "";
		try {
			String newQuery = query.replaceAll("\\s+", "%20");
			String fullAddress = "http://" + ip
					+ "/students/SL/Tricorder/query.php?ip=" + ip
					+ "&username=" + username + "&password=" + password
					+ "&database=" + database + "&query=" + newQuery;
			URL querySite = new URL(fullAddress);
			URLConnection queryConnection = querySite.openConnection();
			BufferedReader input = new BufferedReader(new InputStreamReader(
					queryConnection.getInputStream()));
			String inputLine;
			while ((inputLine = input.readLine()) != null)
				finalLine += inputLine + "\n";
			input.close();
		} catch (IOException e) {
			finalLine = "IOException: " + e.getMessage();
		}

		return finalLine;

	}

	/*
	 * public ArrayList<OverlayItem> getServerData() {
	 * 
	 * InputStream is = null; ArrayList<OverlayItem> list = new
	 * ArrayList<OverlayItem>(); OverlayItem over;
	 * 
	 * ArrayList<String> typeArr =
	 * parse(execute("SELECT sensor_type FROM sensors1;")); ArrayList<String>
	 * dataArr = parse(execute("SELECT sensor_data FROM sensors1;"));
	 * ArrayList<String> dateArr =
	 * parse(execute("SELECT sensor_date FROM sensors1;")); ArrayList<String>
	 * latArr = parse(execute("SELECT sensor_gpsLat FROM sensors1;"));
	 * ArrayList<String> longArr =
	 * parse(execute("SELECT sensor_gpsLong FROM sensors1;")); ArrayList<String>
	 * phoneArr = parse(execute("SELECT phone_id FROM sensors1;"));
	 * ArrayList<String> commentArr =
	 * parse(execute("SELECT comments FROM sensors1;"));
	 * 
	 * try { for (int i = 0; i < typeArr.size(); i++) {
	 * 
	 * String type = typeArr.get(i); String data = dataArr.get(i); float lat =
	 * Integer.parseInt(latArr.get(i)); float longitude =
	 * Integer.parseInt(longArr.get(i)); String comments = commentArr.get(i);
	 * String phone_ID = phoneArr.get(i); int date
	 * =Integer.parseInt(dateArr.get(i)); over = new OverlayItem(new
	 * GeoPoint((int)lat, (int)longitude), type, data); list.add(over); } }
	 * catch (Exception e) { e.printStackTrace(); } return list; }
	 */

	public String[] getServerDate(String sensor, String project_name) {
		ArrayList<String> dataArr = parse(execute("SELECT sensor_date FROM sensors1 WHERE sensor_type='"
				+ sensor
				+ "' and project_name='"
				+ project_name
				+ "' ORDER BY sensor_date;"));
		String data[] = dataArr.toArray(new String[dataArr.size()]);

		return data;
	}

	public String[] getAllServerDate(String sensor) {
		ArrayList<String> dataArr = parse(execute("SELECT sensor_date FROM sensors1 WHERE sensor_type='"
				+ sensor + "' ORDER BY sensor_date;"));
		String data[] = dataArr.toArray(new String[dataArr.size()]);

		return data;
	}

	public String[] getServerPassword(String project_name) {
		ArrayList<String> dataArr = parse(execute("SELECT Password FROM Passwords WHERE project_name='"
				+ project_name + "';"));
		String data[] = dataArr.toArray(new String[dataArr.size()]);

		return data;
	}

	public String[] getServerData(String sensor, String project_name) {
		ArrayList<String> dataArr = parse(execute("SELECT sensor_data FROM sensors1 WHERE sensor_type='"
				+ sensor
				+ "' and project_name='"
				+ project_name
				+ "' ORDER BY sensor_date;"));
		String data[] = dataArr.toArray(new String[dataArr.size()]);
		return data;
	}

	public String[] getAllServerData(String sensor) {
		ArrayList<String> dataArr = parse(execute("SELECT sensor_data FROM sensors1 WHERE sensor_type='"
				+ sensor + "' ORDER BY sensor_date;"));
		String data[] = dataArr.toArray(new String[dataArr.size()]);
		return data;
	}

	public String[] getProjectName() {
		ArrayList<String> dataArr = parse(execute("SELECT project_name FROM Passwords ORDER BY project_name;"));
		String data[] = dataArr.toArray(new String[dataArr.size()]);
		return data;
	}

	public String[] getComments(String name) {
		ArrayList<String> dataArr = parse(execute("SELECT Comments FROM Passwords WHERE project_name='"
				+ name + "';"));
		String data[] = dataArr.toArray(new String[dataArr.size()]);
		return data;
	}

	public ArrayList<String> parse(String result) {

		String[] newArr = result.split("\\{\"0\":\"");
		ArrayList<String> resultArr = new ArrayList<String>();

		for (int i = 1; i < newArr.length; i++) {
			String[] arr = newArr[i].split("\"");
			resultArr.add(arr[0]);
		}
		return resultArr;
	}

	public ArrayList<OverlayItem> getMapData(String condition) {

		ArrayList<OverlayItem> list = new ArrayList<OverlayItem>();
		OverlayItem over;

		ArrayList<String> typeArr = parse(execute("SELECT sensor_type FROM sensors1"
				+ condition + ";"));
		ArrayList<String> dataArr = parse(execute("SELECT sensor_data FROM sensors1"
				+ condition + ";"));
		ArrayList<String> dateArr = parse(execute("SELECT sensor_date FROM sensors1"
				+ condition + ";"));
		ArrayList<String> latArr = parse(execute("SELECT sensor_gpsLat FROM sensors1"
				+ condition + ";"));
		ArrayList<String> longArr = parse(execute("SELECT sensor_gpsLong FROM sensors1"
				+ condition + ";"));
		ArrayList<String> phoneArr = parse(execute("SELECT phone_id FROM sensors1"
				+ condition + ";"));

		for (int i = 0; i < typeArr.size(); i++) {
			String type = typeArr.get(i);
			String data = dataArr.get(i);
			float lat = Float.parseFloat(latArr.get(i));
			float longitude = Float.parseFloat(longArr.get(i));
			String phone_ID = phoneArr.get(i);
			String date = dateArr.get(i);
			String info = date + "\n" + phone_ID + "\n" + data;
			over = new OverlayItem(new GeoPoint((int) (lat * 1E6),
					(int) (longitude * 1E6)), type, info);
			list.add(over);
		}
		return list;
	}
}
