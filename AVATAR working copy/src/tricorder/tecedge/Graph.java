package tricorder.tecedge;

import gupta.ashutosh.avatar.R;

import java.lang.reflect.Array;
import java.lang.System;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

public class Graph extends SurfaceView implements SurfaceHolder.Callback {

	long current_time = (long) (((((double) System.currentTimeMillis() / 60000d)) - 16309741d) * 60d);
	float last_time = 1000;
	float data[] = {};
	String data_time[] = {};
	float max_value = 0;
	float min_value = 0;
	long maxtime, mintime;
	float leftbuffer = 100;
	String type;
	boolean full;
	int places;
	ArrayList<String> datalist;
	final int data_length = Array.getLength(data);
	FrameLayout thermg = (FrameLayout) findViewById(R.id.thermg);
	static Canvas c;
	static int screen;

	public Graph(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
		getHolder().addCallback((SurfaceHolder.Callback) this);
	}

	private void init(AttributeSet attrs) {
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.Graph);
		full = a.getBoolean(R.styleable.Graph_full, false);
		type = a.getString(R.styleable.Graph_type);
		if (screen == 1) {
			if (!Sensor_page.full) {

				if (type.equalsIgnoreCase("thermometer")) {
					data = Sensor_page.Thermdata;
					data_time = Sensor_page.Thermdata_time;
				}
				if (type.equalsIgnoreCase("luxane")) {
					data = Sensor_page.luxdata;
					data_time = Sensor_page.luxdata_time;
				}
				if (type.equalsIgnoreCase("press")) {
					data = Sensor_page.pressdata;
					data_time = Sensor_page.pressdata_time;
				}
				if (type.equalsIgnoreCase("wind")) {
					data = Sensor_page.winddata;
					data_time = Sensor_page.winddata_time;
				}
			} else {

				data = Sensor_page.data;
				data_time = Sensor_page.data_time;
			}
		}
		if (screen == 2) {
			if (!Collect_Data.full) {

				if (type.equalsIgnoreCase("thermometer")) {
					data = Collect_Data.Thermdata;
					data_time = Collect_Data.Thermdata_time;
				}
				if (type.equalsIgnoreCase("luxane")) {
					data = Collect_Data.luxdata;
					data_time = Collect_Data.luxdata_time;
				}
				if (type.equalsIgnoreCase("press")) {
					data = Collect_Data.pressdata;
					data_time = Collect_Data.pressdata_time;
				}
				if (type.equalsIgnoreCase("wind")) {
					data = Collect_Data.winddata;
					data_time = Collect_Data.winddata_time;
				}
			} else {
				data = Collect_Data.data;
				data_time = Collect_Data.data_time;
			}
		}
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

	}

	public void surfaceCreated(SurfaceHolder holder) {
		drawr.run();
	}

	public Runnable drawr = new Runnable() {
		public void run() {
			c = getHolder().lockCanvas();
			draw(Graph.c);
			getHolder().unlockCanvasAndPost(c);
		}
	};

	public void draw(Canvas c) {
		maxtime = convert_to_minutes(data_time[data_time.length - 1]);
		mintime = convert_to_minutes(data_time[0]);
		max_value = -5000;
		min_value = 5000;
		for (int i = 0; i < Array.getLength(data_time); i++) {
			if (max_value < data[i])
				max_value = data[i];
			if (min_value > data[i])
				min_value = data[i];
		}

		float bump = (float) ((max_value - min_value) * .05f) + 1;
		max_value += bump;
		min_value -= bump;
		leftbuffer = c.getWidth() / 15;
		Paint linecolor = new Paint();
		Paint gray = new Paint();
		last_time = c.getWidth();
		gray.setColor(Color.GRAY);
		linecolor.setColor(Color.RED);
		linecolor.setStrokeWidth(3);
		gray.setStrokeWidth(5);
		gray.setTextSize(16);
		float distance = (float) ((c.getWidth() - leftbuffer) / 200);
		for (int i = 0; i < distance; i++) {
			c.drawLine(
					(float) leftbuffer
							+ ((float) (c.getWidth() - leftbuffer) * (float) (i / distance)),
					(8 * c.getHeight() / 10) + 20,
					(float) leftbuffer
							+ ((float) (c.getWidth() - leftbuffer) * (float) (i / distance)),
					(8 * c.getHeight() / 10) - 20, gray);
			String text = convert_to_date(((int) ((float) (i / distance) * (maxtime - mintime)) + (int) mintime));

			c.drawText(

					readabledate(text),
					(float) leftbuffer
							+ ((float) (c.getWidth() - leftbuffer) * (float) (i / distance)),
					(8 * c.getHeight() / 10) + 35, gray);
		}
		c.drawLine(leftbuffer, (8 * c.getHeight() / 10), last_time,
				(8 * c.getHeight() / 10), gray);
		c.drawLine(leftbuffer, (8 * c.getHeight() / 10), last_time,
				(8 * c.getHeight() / 10), gray);
		c.drawLine(leftbuffer, 0, last_time, 0, gray);
		c.drawLine(leftbuffer, 0, leftbuffer, 8 * c.getHeight() / 10, gray);

		c.drawLine(leftbuffer, 6 * c.getHeight() / 10, last_time,
				6 * c.getHeight() / 10, gray);
		c.drawLine(leftbuffer, 4 * c.getHeight() / 10, last_time,
				4 * c.getHeight() / 10, gray);
		c.drawLine(leftbuffer, 2 * c.getHeight() / 10, last_time,
				2 * c.getHeight() / 10, gray);
		gray.setColor(Color.WHITE);
		gray.setTextSize(c.getWidth() / 40);

		textdraw(min_value + (3 * (max_value - min_value) / 4f), c, gray,
				(2 * c.getHeight() / 10));
		textdraw(min_value + (2 * (max_value - min_value) / 4f), c, gray,
				(4 * c.getHeight() / 10));
		textdraw(min_value + (1 * (max_value - min_value) / 4f), c, gray,
				(6 * c.getHeight() / 10));
		textdraw(min_value, c, gray, (8 * c.getHeight() / 10));
		textdraw(max_value, c, gray, 0);
		for (int i = 1; i < Array.getLength(data); i++)
			c.drawLine(
					(float) (leftbuffer + ((float) ((convert_to_minutes(data_time[i - 1]) - mintime) / (float) (maxtime - mintime)) * ((float) c
							.getWidth() - (float) leftbuffer))),
					(float) ((float) c.getHeight() * .8f - ((float) c

					.getHeight() * .8f * ((float) data[i - 1] - min_value) / ((float) max_value - min_value))),

					(float) (leftbuffer + ((float) ((convert_to_minutes(data_time[i]) - mintime) / (float) (maxtime - mintime)) * ((float) c
							.getWidth() - (float) leftbuffer))),
					(float) ((float) c.getHeight() * .8f - ((float) c
							.getHeight() * .8f * ((float) data[i] - min_value) / ((float) max_value - min_value))),
					linecolor);
		// TESTING
		// c.drawText(datalist.get(0),
		// 100, 100, gray);
	}

	public void textdraw(float value, Canvas c, Paint gray, int location) {
		if (Float.toString(value).length() < 5) {
			c.drawText(Float.toString(value), 0,
					Float.toString(value).length(), 0,
					location + (gray.getTextSize()), gray);
		} else {
			c.drawText(Float.toString(value), 0, 5, 0,

			location + (gray.getTextSize()), gray);
		}
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {

	}

	public static long convert_to_minutes(String time) {
		long minutes = 0;
		switch (Integer.parseInt(time.substring(2, 4))) {
		case 1:
			minutes += (long) 0;
			break;
		case 2:
			minutes += (long) 31;
			break;
		case 3:
			minutes += (long) 59;
			break;
		case 4:
			minutes += (long) 90;
			break;
		case 5:
			minutes += (long) 120;
			break;
		case 6:
			minutes += (long) 151;
			break;
		case 7:
			minutes += (long) 181;
			break;
		case 8:
			minutes += (long) 212;
			break;
		case 9:
			minutes += (long) 243;
			break;
		case 10:
			minutes += (long) 273;
			break;
		case 11:
			minutes += (long) 304;
			break;
		case 12:
			minutes += (long) 334;
			break;
		default:
			minutes = -10;
			break;
		}
		minutes *= 1440l;
		minutes += (long) (Long.parseLong(time.substring(4, 6)) - 1) * 1440l;
		minutes += (long) (Long.parseLong(time.substring(0, 2)) - 1) * 525600l;
		minutes += (long) (Integer.parseInt(time.substring(6, 8)) - 1) * 60;
		minutes += (long) (Integer.parseInt(time.substring(8, 10)) - 1);
		minutes *= 60;
		minutes += (long) (Integer.parseInt(time.substring(10, 12)) - 1);
		return minutes;

	}

	public static String convert_to_date(long seconds) {
		seconds += 1;
		String date;
		int months = 0;
		int year;
		String smonths, syear, sseconds, sdays, shours, sminutes;
		year = (int) (seconds / 31536000) + 1;
		seconds %= 31536000;
		int days = (int) (seconds / 86400) + 1;
		seconds %= 86400;
		int hours = (int) (seconds / 3600) + 1;
		seconds %= 3600;
		int minutes = (int) (seconds / 60) + 1;
		seconds %= 60;

		if (days < 31) {
			months = 1;
		}
		if (days > 334) {
			months = 12;
			days -= 334;
		}
		if (days > 304) {

			months = 11;
			days -= 304;
		}
		if (days > 273) {
			months = 10;
			days -= 273;
		}
		if (days > 243) {
			months = 9;
			days -= 243;
		}
		if (days > 212) {
			months = 8;
			days -= 212;
		}
		if (days > 181) {
			months = 7;
			days -= 181;
		}
		if (days > 151) {
			months = 6;
			days -= 151;
		}
		if (days > 120) {
			months = 5;
			days -= 120;
		}
		if (days > 90) {
			months = 4;
			days -= 90;
		}
		if (days > 59) {
			months = 3;

			days -= 59;
		}
		if (days > 31) {
			months = 2;
			days -= 31;
		}

		if (months < 10) {
			smonths = "0" + Integer.toString(months);
		} else {
			smonths = Integer.toString(months);
		}
		if (year < 10) {
			syear = "0" + Integer.toString(year);
		} else {
			syear = Integer.toString(year);
		}
		if (days < 10) {
			sdays = "0" + Integer.toString(days);
		} else {
			sdays = Integer.toString(days);
		}
		if (hours < 10) {
			shours = "0" + Integer.toString(hours);
		} else {
			shours = Integer.toString(hours);
		}
		if (minutes < 10) {
			sminutes = "0" + Long.toString(minutes);
		} else {
			sminutes = Long.toString(minutes);
		}
		if (seconds < 10) {
			sseconds = "0" + Long.toString(seconds);
		} else {
			sseconds = Long.toString(seconds);
		}
		date = syear + smonths + sdays + shours + sminutes + sseconds;
		return date;
	}

	public static String readabledate(String text) {
		return (text.substring(2, 4) + "/" + text.substring(4, 6) + "/"
				+ text.substring(0, 2) + " " + text.substring(6, 8) + ":"
				+ text.substring(8, 10) + "." + text.substring(10, 12));
	}

	@Override
	public void setLayoutParams(LayoutParams params) {
		super.setLayoutParams(params);
	}

}