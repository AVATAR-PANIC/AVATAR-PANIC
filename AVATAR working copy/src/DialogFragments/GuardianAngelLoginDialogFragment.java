package DialogFragments;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;

import com.guardian_angel.uav_tracker.NotificationService;

import gupta.ashutosh.avatar.R;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * 
 * @author Garrett - emrickgarrett@gmail.com
 * 
 * This class is used for the Guardian Angels' log-in screen. Basically a re-implementation
 * of most of their main class.
 *
 */
public class GuardianAngelLoginDialogFragment extends DialogFragment implements OnClickListener {

	private Button ok;
	private Button register;
	private Button cancel;
	private ProgressBar loginProgress;
	private ProgressBar registerProgress;
	
	// Set the private variables
	private String host = "";
	// XMPP default port is 5222
	private String port = "";
	private String room = "";
	private String userid = "";
	private String password = "";
	// Set the saved connection variables
	private String savedHost = "";
	private String savedPort = "";
	private String savedRoom = "";
	private String savedUsername = "";
	private String savedPassword = "";

	private boolean fromQuickStart = true;
	private static boolean ranOnce = false;

	private Handler progressHandler;
	
	
	public GuardianAngelLoginDialogFragment(){
		
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.guardian_angel_xmpp_connection_settings, container, false);
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		getDialog().setTitle("Connection Settings:");
		progressHandler = new Handler();
		// Setup the XMPPConnectionDialog interface elements
		ok = (Button) view.findViewById(R.id.xmpp_connection_settings_ok);
		register = (Button) view
				.findViewById(R.id.xmpp_connection_settings_register);
		cancel = (Button) view
				.findViewById(R.id.xmpp_connection_settings_cancel);
		loginProgress = (ProgressBar) view
				.findViewById(R.id.xmpp_connection_settings_progress_bar_login);
		registerProgress = (ProgressBar) view
				.findViewById(R.id.xmpp_connection_settings_progress_bar_register);

		// Open the previously successful connection information
		openSaveData();
		// Set the login button listener
		ok.setOnClickListener(this);
		// Set the register button listener
		register.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				register();
			}
		});
		// Set the cancel button listener
		cancel.setOnClickListener(new OnClickListener() {
			// Called when the Cancel button is pressed

			public void onClick(View v) {
				getDialog().cancel();
			}
		});

		// Load the last saved connection data
		try {
			loadConnectionInfo();
			} catch (NullPointerException nex) {
				nex.printStackTrace();
			}
		
		return view;
	}

/**
 * Load the latest saved connection data and fill the text boxes with the
 * information. This will allow the user to view the previously successful
 * connection information.
 */
public void loadConnectionInfo() {
	EditText sHost = (EditText) getDialog()
			.findViewById(R.id.xmpp_connection_settings_host);
	sHost.setText(savedHost);
	EditText sPort = (EditText) getDialog()
			.findViewById(R.id.xmpp_connection_settings_port);
	sPort.setText(savedPort);
	EditText sRoom = (EditText) getDialog()
			.findViewById(R.id.xmpp_connection_settings_room);
	sRoom.setText(savedRoom);
	EditText sUsername = (EditText) getDialog()
			.findViewById(R.id.xmpp_connection_settings_userid);
	sUsername.setText(savedUsername);
	EditText sPassword = (EditText) getDialog()
			.findViewById(R.id.xmpp_connection_settings_password);
	sPassword.setText(savedPassword);
}

/**
 * This will begin the connection to the XMPP server using the information
 * in the current text boxes of the XMPP Connection Dialog. It will check
 * for any unavailable characters that the user attempts to put into their
 * name. The characters "_" and "@" are unavailable due to the nature of the
 * delimiting done by the rest of the application and the PSSA network.
 */
public void onClick(View v) {

	EditText sHost = (EditText) getDialog()
			.findViewById(R.id.xmpp_connection_settings_host);
	host = sHost.getText().toString();
	EditText sPort = (EditText) getDialog()
			.findViewById(R.id.xmpp_connection_settings_port);
	port = sPort.getText().toString();
	EditText sRoom = (EditText) getDialog()
			.findViewById(R.id.xmpp_connection_settings_room);
	room = sRoom.getText().toString() + "@conference." + host;
	EditText sUsername = (EditText) getDialog()
			.findViewById(R.id.xmpp_connection_settings_userid);
	userid = sUsername.getText().toString();
	// Check to make sure the userid does not have any unavailable
	// characters, if it does, return to the connection dialog.
	if (userid.contains("_") || userid.contains("@")) {
		updateUI(
				"Username cannot contain \"_\" or \"@\". Please submit a new username.",
				0);
		return;
	} else {
		userid = userid + "@" + host;
	}
	EditText sPassword = (EditText) getDialog()
			.findViewById(R.id.xmpp_connection_settings_password);
	password = sPassword.getText().toString();
	// Begin the connection to the XMPP server.
	setConnection();
}

/**
 * Connects to an XMPP server using the information input into the dialog's
 * text boxes.
 */
public void setConnection() {
	if (fromQuickStart == true) {
		// This will make sure it does not get a null pointer exception
	}

	else {
		loginProgress.setVisibility(View.VISIBLE);
	}

	// Start a new thread for attempting to connect to the server
	new Thread(new Runnable() {
		public void run() {
			// Variable used to stop subsequent checks if there was an error
			boolean continueConnection = true;
			// Check to see if the user is already connected. If they are,
			// and they aren't trying to change servers, alert them that
			// they are already connected
			try {
				if (NotificationService.connection.isConnected()) {
					if (NotificationService.connection.getHost().equals(
							host)) {
						continueConnection = false;
						updateUI("Connected.", 2);
						getDialog().cancel();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				continueConnection = true;
			}
			// Begin connecting if the user isn't already connected.
			if (continueConnection) {
				try {
					// Create a connection
					ConnectionConfiguration connConfig = new ConnectionConfiguration(
							host, Integer.parseInt(port), host);
					NotificationService.connection = new XMPPConnection(
							connConfig);

					// Attempt to connect to the specified server
					try {
						NotificationService.connection.connect();
					} catch (Exception ex) {
						updateUI(
								"The server could not be found. "
										+ "Please make sure all input information is correct.",
								0);
						continueConnection = false;
						ex.printStackTrace();
					}

					// Attempt to login as the specified user
					if (continueConnection) {
						try {
							NotificationService.connection.login(userid,
									password);

							// Set the status to available
							Presence presence = new Presence(
									Presence.Type.available);
							NotificationService.connection
									.sendPacket(presence);

							// Join the MUC
							if (NotificationService.connection != null) {
								NotificationService.muc = new MultiUserChat(
										NotificationService.connection,
										room);

								try {
									NotificationService.muc.join(userid);
								} catch (Exception ex) {
									updateUI(
											"Theere was an error connecting to the chat room. Please make sure"
													+ " that the specified room exists.",
											0);
									continueConnection = false;
									ex.printStackTrace();
								}
							}
						} catch (Exception ex) {
							updateUI(
									"There was an error logging in to the server. Please make sure"
											+ " that the specified account exists.",
									0);
							continueConnection = false;
							ex.printStackTrace();
						}

						// Output in a toast if the connection was
						// successful
						if ((continueConnection)
								&& (NotificationService.connection
										.isConnected())) {
							updateUI("Connection Successful.", 0);
							saveFiles();
							openSaveData();
							getActivity().startService(new Intent(getActivity().getBaseContext(),
									NotificationService.class));
							fromQuickStart = false;
							
						}
					}
				} catch (Exception ex) {
					updateUI(
							"Error connecting. Please make sure all input information is correct.",
							0);
					ex.printStackTrace();
				}
			}

		}
	}).start();
}

/**
 * Attempts to register a new account on the XMPP server described in the
 * text boxes.
 */
private void register() {
	registerProgress.setVisibility(View.VISIBLE);
	fromQuickStart = false;
	// Start a new thread for attempting to register an account
	new Thread(new Runnable() {
		public void run() {
			// Variable used to stop subsequent checks if there was an error
			boolean continueConnection = true;

			// Attempt to disconnect if the user was previously connected
			try {
				NotificationService.connection.disconnect();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				// Create a connection

				EditText het = (EditText) getDialog()
						.findViewById(R.id.xmpp_connection_settings_host);
				String userHost = het.getText().toString();

				EditText et = (EditText) getDialog()
						.findViewById(R.id.xmpp_connection_settings_port);
				String portCast = et.getText().toString();

				int portCasted = Integer.parseInt(portCast);

				ConnectionConfiguration connConfig = new ConnectionConfiguration(
						userHost, portCasted, userHost);
				NotificationService.connection = new XMPPConnection(
						connConfig);
				NotificationService.connection.connect();

			} catch (Exception ex) {
				updateUI(
						"The server could not be found. Please make sure all input information is correct.",
						1);
				continueConnection = false;
				ex.printStackTrace();
			}

			if (continueConnection) {
				try {

					EditText het = (EditText) getDialog()
							.findViewById(R.id.xmpp_connection_settings_host);
					String userHost = het.getText().toString();

					// Attempt to create an account
					EditText etUser = (EditText) getDialog()
							.findViewById(R.id.xmpp_connection_settings_userid);
					String userName = etUser.getText().toString();
					if (userName.contains("_") || userName.contains("@")) {
						updateUI(
								"Username cannot contain \"_\" or \"@\". Please submit a new username.",
								1);
						return;
					} else {
						userName = userName + "@" + userHost;
					}
					EditText etPass = (EditText) getDialog()
							.findViewById(R.id.xmpp_connection_settings_password);
					String passWord = etPass.getText().toString();

					userName = userName.substring(0, userName.indexOf("@"));
					NotificationService.connection.getAccountManager()
							.createAccount(userName, passWord);
					updateUI("An account for " + userName
							+ " has been successfully created.", 1);
					NotificationService.connection.disconnect();
				} catch (Exception ex) {
					updateUI("There was an error creating the account. "
							+ "It may already exist.", 1);
					ex.printStackTrace();
				}
			}
		}
	}).start();
}

/**
 * Updates the UI thread for removing progress bars and displaying toasts.
 * 
 * @param toastText
 *            The string to output as a toast
 * @param progressViewNumber
 *            The number of the progress view to remove visiblity from
 */
private void updateUI(final String toastText, final int progressViewNumber) {
	progressHandler.post(new Runnable() {
		public void run() {
			// Remove the visibility of the set progress view
			if (progressViewNumber == 0 && fromQuickStart == false) {
				loginProgress.setVisibility(View.GONE);
			} else if (progressViewNumber == 1 && fromQuickStart == false) {
				registerProgress.setVisibility(View.GONE);
			}

			// Output the toast
			Toast.makeText(getActivity().getApplicationContext(), toastText,
					Toast.LENGTH_SHORT).show();
		}
	});
}


/**
 * Open the saved XMPP connection data to make reconnection easier over
 * multiple sessions.
 */
private void openSaveData() {
	FileInputStream fis = null;
	InputStreamReader isr = null;
	char[] inputBuffer;
	String fileData;
	int index;

	try {
		// Read the stored host data
		inputBuffer = new char[255];
		fis = getActivity().openFileInput("host_file");
		isr = new InputStreamReader(fis);
		isr.read(inputBuffer);
		fileData = new String(inputBuffer);
		index = fileData.indexOf('\u0000');
		savedHost = fileData.substring(0, index);
		fis.close();
		isr.close();

		// Read the stored port data
		inputBuffer = new char[255];
		fis = getActivity().openFileInput("port_file");
		isr = new InputStreamReader(fis);
		isr.read(inputBuffer);
		fileData = new String(inputBuffer);
		index = fileData.indexOf('\u0000');
		savedPort = fileData.substring(0, index);
		fis.close();
		isr.close();

		// Read the stored room data
		inputBuffer = new char[255];
		fis = getActivity().openFileInput("room_file");
		isr = new InputStreamReader(fis);
		isr.read(inputBuffer);
		fileData = new String(inputBuffer);
		index = fileData.indexOf('\u0000');
		savedRoom = fileData.substring(0, index);
		fis.close();
		isr.close();

		// Read the stored username data
		inputBuffer = new char[255];
		fis = getActivity().openFileInput("username_file");
		isr = new InputStreamReader(fis);
		isr.read(inputBuffer);
		fileData = new String(inputBuffer);
		index = fileData.indexOf('\u0000');
		savedUsername = fileData.substring(0, index);
		fis.close();
		isr.close();

		// Read the stored password data
		// Please note that password data is currently unencrypted
		inputBuffer = new char[255];
		fis = getActivity().openFileInput("password_file");
		isr = new InputStreamReader(fis);
		isr.read(inputBuffer);
		fileData = new String(inputBuffer);
		index = fileData.indexOf('\u0000');
		savedPassword = fileData.substring(0, index);
		fis.close();
		isr.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
}

/**
 * Save the XMPP connection data to make reconnection easier over multiple
 * sessions.
 */
private void saveFiles() {
	FileOutputStream fos = null;
	OutputStreamWriter osw = null;

	try {
		// Save the current host data
		fos = getActivity().openFileOutput("host_file", Context.MODE_PRIVATE);
		osw = new OutputStreamWriter(fos);
		osw.write(host);
		osw.flush();
		osw.close();
		fos.close();

		// Save the current port data
		fos = getActivity().openFileOutput("port_file", Context.MODE_PRIVATE);
		osw = new OutputStreamWriter(fos);
		osw.write(port);
		osw.flush();
		osw.close();
		fos.close();

		// Save the current room data
		fos = getActivity().openFileOutput("room_file", Context.MODE_PRIVATE);
		osw = new OutputStreamWriter(fos);
		String[] temp = room.split("@");
		osw.write(temp[0]);
		osw.flush();
		osw.close();
		fos.close();

		// Save the current username data
		fos = getActivity().openFileOutput("username_file", Context.MODE_PRIVATE);
		osw = new OutputStreamWriter(fos);
		String[] temp2 = userid.split("@");
		osw.write(temp2[0]);
		osw.flush();
		osw.close();
		fos.close();

		// Save the current password data
		fos = getActivity().openFileOutput("password_file", Context.MODE_PRIVATE);
		osw = new OutputStreamWriter(fos);
		osw.write(password);
		osw.flush();
		osw.close();
		fos.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
}



	/**
	 * Quickly connect using the last saved data.
	 */
	public void quickStart() {
		host = savedHost;
		room = savedRoom;
		port = savedPort;
		userid = savedUsername;
		password = savedPassword;
		room = room + "@conference." + host;
		userid = userid + "@" + host;
		// This boolean is to ensure a null pointer is not thrown due to the
		// progress bar not being displayed
		fromQuickStart = true;
		setConnection();
	
	}

	
	
}
