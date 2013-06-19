package tricorder.tecedge;

import android.util.Log;

public class DataHandler {

	PHPScriptQuery database = new PHPScriptQuery();

	public String[] getDate(String Sensor, String PhoneID) {
		if (database.getServerDate(Sensor, PhoneID).length > 1) {
			String data_time[] = database.getServerDate(Sensor, PhoneID);
			Log.d("hi", data_time[1]);
			return data_time;
		} else {
			String data_time[] = null;
			return null;
		}
	}

	public String[] getAllDate(String Sensor) {
		if (database.getAllServerDate(Sensor).length > 1) {
			String data_time[] = database.getAllServerDate(Sensor);
			Log.d("hi", data_time[1]);
			return data_time;
		} else {
			String data_time[] = null;
			return null;
		}
	}

	public float[] getData(String Sensor, String PhoneID) {
		if (database.getServerData(Sensor, PhoneID).length > 1) {
			String datas[] = database.getServerData(Sensor, PhoneID);
			float data[] = new float[datas.length];

			for (int i = 0; i < datas.length; i++) {
				Log.d("hi", datas[i]);
				data[i] = Float.parseFloat(datas[i]);

			}
			return data;
		} else {
			String datas[] = null;
			return null;
		}

	}

	public float[] getAllData(String Sensor) {
		if (database.getAllServerData(Sensor).length > 1) {
			String datas[] = database.getAllServerData(Sensor);
			float data[] = new float[datas.length];

			for (int i = 0; i < datas.length; i++) {
				Log.d("hi", datas[i]);
				data[i] = Float.parseFloat(datas[i]);

			}
			return data;
		} else {
			String datas[] = null;
			return null;
		}

	}

}
