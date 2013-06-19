package tricorder.tecedge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Refreshm extends Activity {
	Mapm main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Mapm.h.sendEmptyMessage(0);
		Intent intent = new Intent(this, Mapm.class);
		startActivity(intent);
	}
}
