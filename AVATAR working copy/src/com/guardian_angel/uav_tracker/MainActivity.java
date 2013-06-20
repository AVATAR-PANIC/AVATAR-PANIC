/**
 * This is the primary activity for the UAV-T application. It provides the user with a way to start the application for recording a UAV and also view 
 * a map to see other users around them. Finally, it provides information to the user about who constructed the application and how to get in contact
 * should they need additional information.
 */
package com.guardian_angel.uav_tracker;

import gupta.ashutosh.avatar.R;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * 
 * @author Guardian Angel Team / SATE 2012
 * @author Michael J. Fox < fox.117@wright.edu >
 * @author Zac Audette < audette.2@wright.edu >
 * @since 7.29.12
 * @version 1.0
 * 
 */


public class MainActivity extends Activity implements
		android.view.View.OnClickListener, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImageButton start;
	private ImageButton toMap;
	private Context mainContext;

	public static String deviceID = "";

	// XMPPConnectionDialog information
	protected static final int XMPPConnectionDialog = 0; // Dialog ID
	private ProgressBar loginProgress;
	private ProgressBar registerProgress;
	private Dialog dialog;
	private Handler progressHandler;
	protected Button ok;
	protected Button register;
	protected Button cancel;
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
	private boolean ranOnce = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guardian_angel_main);

		// Setup the XMPPConnectionDialog interface elements
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.xmpp_connection_settings);
		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		dialog.setTitle("Connection Settings:");

		// Create the display with appropriate buttons
		progressHandler = new Handler();
		start = (ImageButton) findViewById(R.id.main_button);
		toMap = (ImageButton) findViewById(R.id.to_map_activity);

		// Grab the context for the application and the ID of the device the app
		// is running on for future use
		mainContext = this.getApplicationContext();
		deviceID = android.provider.Settings.Secure.getString(
				getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);

		// Check to see if this is the first time the application is starting
		if (ranOnce == false) {
			// Begin the quick start sequence, which attempts to connect to the
			// previous XMPP server
			openSaveData();
			quickStart();

			// Set this boolean to true so that it will not attempt to reconnect
			// whenever the user returns to the Main Activity page
			ranOnce = true;
		}

		// Set a listener for the start button of the application
		start.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				// This check is to ensure a null pointer is not obtained from
				// the Map class when a user attempts to send data without a
				// connection
				if (NotificationService.connection.isConnected()) {
					Intent nextScreen = new Intent(mainContext, Map.class);
					// This is used to signify if the Map class should allow the
					// user to plot points or simply view the map
					nextScreen.putExtra("viewOnly", false);
					nextScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(nextScreen);

					// Alert the user if they currently don't have an XMPP
					// connection
				} else {
					Toast.makeText(
							getApplicationContext(),
							"You must connect to the XMPP server before starting!",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// This listener is used to send the user to the Map class as a view
		// only, without the ability to plot and send data
		toMap.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// A check is needed to guarantee the user has an XMPP
				// connection so that the Map class will not throw a null
				// pointer
				if (NotificationService.connection.isConnected()) {
					Intent nextScreen = new Intent(mainContext, Map.class);
					// Set the map to only allow for viewing of it, meaning the
					// user will not be able to plot guesses and send data.
					nextScreen.putExtra("viewOnly", true);
					startActivity(nextScreen);

					// Alert the user in case they don't have an XMPP connection
				} else {
					Toast.makeText(
							getApplicationContext(),
							"You must connect to the XMPP server before starting!",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	/*
	 * Creates a menu to be displayed when the settings button is touched.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	/* Tells the menu buttons what to do. */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Allows the user to set the XMPP connection to a designated server
		case R.id.text1:
			showDialog(XMPPConnectionDialog);
			break;
		// Gives the user step by step directions for using the application.
		case R.id.text2:
			Intent nextScreen = new Intent(getApplicationContext(),
					GAdirections.class);
			startActivity(nextScreen);
			break;
		// Attempts to kill the application so that the user can exit it without
		// the Notification Service running in the background.
		case R.id.text3:
			Intent exitSplash = new Intent(getApplicationContext(),
					Splash.class);
			exitSplash.putExtra("exit", true);
			stopService(new Intent(getBaseContext(), NotificationService.class));
			startActivity(exitSplash);
			finish();

		}
		return true;
	}

	/*
	 * Reads in the directions that will be displayed. The directions are stored
	 * in the res/raw folder
	 */
	public String readTxt() {
//		InputStream inputStream = getResources().openRawResource(
//				R.raw.directions);
//
//		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//		int i;
//		try {
//			i = inputStream.read();
//			while (i != -1) {
//				byteArrayOutputStream.write(i);
//				i = inputStream.read();
//			}
//			inputStream.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return byteArrayOutputStream.toString();
		return "";
	}

	// Create the XMPP Connection Dialog box
	@Override
	protected Dialog onCreateDialog(int id) {
		// Setup the XMPPConnectionDialog interface elements
		dialog.setContentView(R.layout.xmpp_connection_settings);
		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		dialog.setTitle("Connection Settings:");

		ok = (Button) dialog.findViewById(R.id.xmpp_connection_settings_ok);
		register = (Button) dialog
				.findViewById(R.id.xmpp_connection_settings_register);
		cancel = (Button) dialog
				.findViewById(R.id.xmpp_connection_settings_cancel);
		loginProgress = (ProgressBar) dialog
				.findViewById(R.id.xmpp_connection_settings_progress_bar_login);
		registerProgress = (ProgressBar) dialog
				.findViewById(R.id.xmpp_connection_settings_progress_bar_register);

		// Open the previously successful connection information
		openSaveData();
		// Set the login button listener
		ok.setOnClickListener(this);
		// Set the register button listener
		register.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				register();
			}
		});
		// Set the cancel button listener
		cancel.setOnClickListener(new OnClickListener() {
			// Called when the Cancel button is pressed

			public void onClick(View v) {
				removeDialog(XMPPConnectionDialog);
			}
		});
		// Set the back function listener
		dialog.setOnCancelListener(new OnCancelListener() {
			// Called when the Back button is pressed

			public void onCancel(DialogInterface dialog) {
				removeDialog(XMPPConnectionDialog);
			}
		});

		// Load the last saved connection data
		try {
			loadConnectionInfo();
		} catch (NullPointerException nex) {
			nex.printStackTrace();
		}
		return dialog;

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

	/**
	 * Load the latest saved connection data and fill the text boxes with the
	 * information. This will allow the user to view the previously successful
	 * connection information.
	 */
	public void loadConnectionInfo() {
		EditText sHost = (EditText) dialog
				.findViewById(R.id.xmpp_connection_settings_host);
		sHost.setText(savedHost);
		EditText sPort = (EditText) dialog
				.findViewById(R.id.xmpp_connection_settings_port);
		sPort.setText(savedPort);
		EditText sRoom = (EditText) dialog
				.findViewById(R.id.xmpp_connection_settings_room);
		sRoom.setText(savedRoom);
		EditText sUsername = (EditText) dialog
				.findViewById(R.id.xmpp_connection_settings_userid);
		sUsername.setText(savedUsername);
		EditText sPassword = (EditText) dialog
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

		EditText sHost = (EditText) dialog
				.findViewById(R.id.xmpp_connection_settings_host);
		host = sHost.getText().toString();
		EditText sPort = (EditText) dialog
				.findViewById(R.id.xmpp_connection_settings_port);
		port = sPort.getText().toString();
		EditText sRoom = (EditText) dialog
				.findViewById(R.id.xmpp_connection_settings_room);
		room = sRoom.getText().toString() + "@conference." + host;
		EditText sUsername = (EditText) dialog
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
		EditText sPassword = (EditText) dialog
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
							removeDialog(XMPPConnectionDialog);
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
								startService(new Intent(getBaseContext(),
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

					EditText het = (EditText) dialog
							.findViewById(R.id.xmpp_connection_settings_host);
					String userHost = het.getText().toString();

					EditText et = (EditText) dialog
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

						EditText het = (EditText) dialog
								.findViewById(R.id.xmpp_connection_settings_host);
						String userHost = het.getText().toString();

						// Attempt to create an account
						EditText etUser = (EditText) dialog
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
						EditText etPass = (EditText) dialog
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
				Toast.makeText(getApplicationContext(), toastText,
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
			fis = this.openFileInput("host_file");
			isr = new InputStreamReader(fis);
			isr.read(inputBuffer);
			fileData = new String(inputBuffer);
			index = fileData.indexOf('\u0000');
			savedHost = fileData.substring(0, index);
			fis.close();
			isr.close();

			// Read the stored port data
			inputBuffer = new char[255];
			fis = this.openFileInput("port_file");
			isr = new InputStreamReader(fis);
			isr.read(inputBuffer);
			fileData = new String(inputBuffer);
			index = fileData.indexOf('\u0000');
			savedPort = fileData.substring(0, index);
			fis.close();
			isr.close();

			// Read the stored room data
			inputBuffer = new char[255];
			fis = this.openFileInput("room_file");
			isr = new InputStreamReader(fis);
			isr.read(inputBuffer);
			fileData = new String(inputBuffer);
			index = fileData.indexOf('\u0000');
			savedRoom = fileData.substring(0, index);
			fis.close();
			isr.close();

			// Read the stored username data
			inputBuffer = new char[255];
			fis = this.openFileInput("username_file");
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
			fis = this.openFileInput("password_file");
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
			fos = this.openFileOutput("host_file", Context.MODE_PRIVATE);
			osw = new OutputStreamWriter(fos);
			osw.write(host);
			osw.flush();
			osw.close();
			fos.close();

			// Save the current port data
			fos = this.openFileOutput("port_file", Context.MODE_PRIVATE);
			osw = new OutputStreamWriter(fos);
			osw.write(port);
			osw.flush();
			osw.close();
			fos.close();

			// Save the current room data
			fos = this.openFileOutput("room_file", Context.MODE_PRIVATE);
			osw = new OutputStreamWriter(fos);
			String[] temp = room.split("@");
			osw.write(temp[0]);
			osw.flush();
			osw.close();
			fos.close();

			// Save the current username data
			fos = this.openFileOutput("username_file", Context.MODE_PRIVATE);
			osw = new OutputStreamWriter(fos);
			String[] temp2 = userid.split("@");
			osw.write(temp2[0]);
			osw.flush();
			osw.close();
			fos.close();

			// Save the current password data
			fos = this.openFileOutput("password_file", Context.MODE_PRIVATE);
			osw = new OutputStreamWriter(fos);
			osw.write(password);
			osw.flush();
			osw.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}