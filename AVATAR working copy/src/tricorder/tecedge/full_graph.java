package tricorder.tecedge;

import gupta.ashutosh.avatar.R;

import java.lang.reflect.Array;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.FrameLayout;

public class full_graph extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tricorder_full_graph);
		Log.d("full", "1");
		Graph fullgraph = (Graph) findViewById(R.id.fullgraph);
		Log.d("full", "2");
		fullgraph
				.setLayoutParams(new FrameLayout.LayoutParams(
						getWindowManager().getDefaultDisplay().getWidth()
								+ Array.getLength(Sensor_page.data) * 25,
						(int) (getWindowManager().getDefaultDisplay()
								.getHeight() * .9f)));
		Log.d("full", "3");
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Sensor_page.full = false;
	}
}
