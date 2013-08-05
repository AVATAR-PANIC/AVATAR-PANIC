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
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginDialogFragment extends DialogFragment{

	EditText server;
	EditText subfolder;
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

		server = (EditText) v.findViewById(R.id.server_field); //virtualdiscoverycenter.net
		subfolder = (EditText) v.findViewById(R.id.subfolder_field); //AVATAR
		username = (EditText) v.findViewById(R.id.username_field);
		password = (EditText) v.findViewById(R.id.password_field);
		save = (Button) v.findViewById(R.id.avatar_login_save);
		
		server.setText(Constants.SERVER_FTP_ADDRESS);
		subfolder.setText(Constants.SERVER_SCRIPT_SUBFOLDER);
		username.setText(Constants.username);
		password.setText(Constants.password);
		
		save.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				String serverInfo = server.getText().toString().replaceAll(" ", "");
				String subfolderInfo = subfolder.getText().toString().replaceAll(" ", "");
				String userNameInfo = username.getText().toString();
				String userNamePassword = password.getText().toString();
				if(userNameInfo != null && !userNameInfo.equals("")){
					if(userNamePassword != null && !userNamePassword.equals("")){
						Constants.username = userNameInfo;
						Constants.password = userNamePassword;
						Constants.SERVER_FTP_ADDRESS = serverInfo;
						Constants.SERVER_SCRIPT_SUBFOLDER = subfolderInfo;
						Toast.makeText(getActivity(), "Logging In", Toast.LENGTH_SHORT).show();
						new HttpThread(viewer).execute("");
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

	@Override
	public void onResume() {
		super.onResume();
		getDialog().getWindow().setLayout(650,LayoutParams.WRAP_CONTENT);
	}
	
}
