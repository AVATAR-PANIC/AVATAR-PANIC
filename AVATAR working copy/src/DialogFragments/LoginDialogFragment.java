package DialogFragments;

import gupta.ashutosh.avatar.R;
import sate2012.avatar.android.Constants;
import sate2012.avatar.android.googlemaps.GoogleMapsViewer;
import sate2012.avatar.android.googlemaps.HttpThread;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginDialogFragment extends DialogFragment{

	EditText server;
	EditText username;
	EditText password;
	Button save;
	GoogleMapsViewer viewer;
	
	public LoginDialogFragment(){}
	
	public void setMap(GoogleMapsViewer map){
		viewer = map;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.avatar_login, container, false);
		getDialog().setTitle("Log-in Settings");

		server = (EditText) v.findViewById(R.id.server_field);
		username = (EditText) v.findViewById(R.id.username_field);
		password = (EditText) v.findViewById(R.id.password_field);
		save = (Button) v.findViewById(R.id.avatar_login_save);
		save.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				String serverInfo = server.getText().toString().replaceAll(" ", "");
				String userNameInfo = username.getText().toString();
				String userNamePassword = password.getText().toString();
				if(userNameInfo != null && !userNameInfo.equals("")){
					if(userNamePassword != null && !userNamePassword.equals("")){
						Constants.username = userNameInfo;
						Constants.password = userNamePassword;
						Constants.SERVER_FTP_ADDRESS = serverInfo;
						Toast.makeText(getActivity(), "Logging In", Toast.LENGTH_SHORT).show();
						new HttpThread((GoogleMapsViewer)null).execute("");
						getDialog().cancel();
					}else{
						Toast.makeText(getActivity(), "Enter a Password", Toast.LENGTH_SHORT).show();
					}
					
				}else{
					Toast.makeText(getActivity(), "Enter a Username", Toast.LENGTH_SHORT).show();
				}
				
			}
			
			
		});
		
		return v;
	}
}
