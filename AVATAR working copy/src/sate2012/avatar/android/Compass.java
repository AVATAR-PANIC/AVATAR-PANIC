package sate2012.avatar.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


public class Compass extends View {
	private float direction = 0;
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private boolean firstDraw;

	public Compass(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public Compass(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Compass(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		paint.setColor(Color.BLACK);
		paint.setTextSize(30);
		firstDraw = true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
				MeasureSpec.getSize(heightMeasureSpec));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int cxCompass = getMeasuredWidth() / 2;
		int cyCompass = getMeasuredHeight() / 2;
		float radiusCompass;
		if (cxCompass > cyCompass) {
			radiusCompass = (float) (cyCompass * 0.9);
		} else {
			radiusCompass = (float) (cxCompass * 0.9);
		}
		canvas.drawCircle(cxCompass, cyCompass, radiusCompass, paint);
		canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
		if (!firstDraw) {
			canvas.drawLine(
					cxCompass,
					cyCompass,
					(float) (cxCompass + radiusCompass
							* Math.sin((double) (-direction) * 3.14 / 180)),
					(float) (cyCompass - radiusCompass
							* Math.cos((double) (-direction) * 3.14 / 180)),
					paint);
			canvas.drawText(String.valueOf(Math.round(direction)), cxCompass
					- radiusCompass + 18, cyCompass + radiusCompass / 2 + 6,
					paint);
		}
	}

	public void updateDirection(float dir) {
		firstDraw = false;
		direction = dir;
		invalidate();
	}

	// this is some code that might be useful to use for a finer orientation
	// sensor
	// import android.app.Activity;
	// import android.os.Bundle;
	// import android.widget.TextView;
	// import android.hardware.Sensor;
	// import android.hardware.SensorEvent;
	// import android.hardware.SensorEventListener;
	// import android.hardware.SensorManager;
	//
	// public class Compass extends Activity implements SensorEventListener {
	// /** Called when the activity is first created. */
	//
	// private SensorManager sensorManager;
	// private TextView txtRawData;
	// private TextView txtDirection;
	// private float myAzimuth = 0;
	// private float myPitch = 0;
	// private float myRoll = 0;
	//
	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	//
	// setContentView(R.layout.main);
	// txtRawData = (TextView) findViewById(R.id.txt_info);
	// txtDirection = (TextView) findViewById(R.id.txt_direction);
	// txtRawData.setText("Compass");
	// txtDirection.setText("");
	//
	// // Real sensor manager
	// sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	// }
	//
	// /** Register for the updates when Activity is in foreground */
	// @Override
	// protected void onResume() {
	// super.onResume();
	// sensorManager.registerListener(this,
	// sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
	// SensorManager.SENSOR_DELAY_NORMAL);
	// }
	//
	// /** Stop the updates when Activity is paused */
	// @Override
	// protected void onPause() {
	// super.onPause();
	// sensorManager.unregisterListener(this);
	// }
	//
	//
	// public void onAccuracyChanged(Sensor sensor, int accuracy) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// public void onSensorChanged(SensorEvent event) {
	// // TODO Auto-generated method stub
	//
	// myAzimuth = Math.round(event.values[0]);
	// myPitch = Math.round(event.values[1]);
	// myRoll = Math.round(event.values[2]);
	//
	// String out =
	// String.format("Azimuth: %.2f\n\nPitch:%.2f\n\nRoll:%.2f\n\n",
	// myAzimuth, myPitch, myRoll);
	// txtRawData.setText(out);
	//
	// printDirection();
	// }
	//
	// private void printDirection() {
	//
	// if (myAzimuth < 22)
	// txtDirection.setText("N");
	// else if (myAzimuth >= 22 && myAzimuth < 67)
	// txtDirection.setText("NE");
	// else if (myAzimuth >= 67 && myAzimuth < 112)
	// txtDirection.setText("E");
	// else if (myAzimuth >= 112 && myAzimuth < 157)
	// txtDirection.setText("SE");
	// else if (myAzimuth >= 157 && myAzimuth < 202)
	// txtDirection.setText("S");
	// else if (myAzimuth >= 202 && myAzimuth < 247)
	// txtDirection.setText("SW");
	// else if (myAzimuth >= 247 && myAzimuth < 292)
	// txtDirection.setText("W");
	// else if (myAzimuth >= 292 && myAzimuth < 337)
	// txtDirection.setText("NW");
	// else if (myAzimuth >= 337)
	// txtDirection.setText("N");
	// else
	// txtDirection.setText("");
	//
	// }
	// }
}
