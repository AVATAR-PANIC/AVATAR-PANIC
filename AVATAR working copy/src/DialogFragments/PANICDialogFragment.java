package DialogFragments;

import gupta.ashutosh.avatar.R;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

/**
 * 
 * @author Garrett - emrickgarrett@gmail.com
 * 
 * This is the implementation of PANIC through the use of a dialog fragment.
 * If the user clicks on the EMERGENCY button on the navigation menu located on the left
 * of the application, this will open up allowing the user to select which ICE to call.
 *
 */
public class PANICDialogFragment extends DialogFragment {

	private Button police;
	private Button fire;
	private Button parents;
	
	
	public PANICDialogFragment(){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
		View view = inflater.inflate(R.layout.dialog_panic_layout, container, false);
		
		getDialog().setTitle("PANIC!");
		
		this.police = (Button) view.findViewById(R.id.panic_police);
		this.fire = (Button) view.findViewById(R.id.panic_fire);
		this.parents = (Button) view.findViewById(R.id.panic_parents);
		
		police.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v){
				//Police Code goes here.
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:911"));
				startActivity(callIntent);
				getDialog().cancel();
			}
		});
		
		fire.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v){
				//Fire code goes here.
				Intent callIntent1 = new Intent(Intent.ACTION_CALL);
				callIntent1.setData(Uri.parse("tel:911"));
				startActivity(callIntent1);
				getDialog().cancel();
			}
		});
		
		parents.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v){
				//Parents code goes here.
				Intent callIntent2 = new Intent(Intent.ACTION_CALL);
				callIntent2.setData(Uri.parse("tel:9379023700"));
				startActivity(callIntent2);
				getDialog().cancel();
			}
		});

		return view;
	}
	
	//Have to use this to set the layout sizes, STUPID.
	@Override
	public void onResume(){
		super.onResume();
		getDialog().getWindow().setLayout(217,LayoutParams.WRAP_CONTENT);
	}
}
