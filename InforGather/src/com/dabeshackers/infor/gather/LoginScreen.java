package com.dabeshackers.infor.gather;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.dabeshackers.infor.gather.application.AppMain;
import com.dabeshackers.infor.gather.application.ApplicationUtils;
import com.dabeshackers.infor.gather.data.DBAdapter;
import com.dabeshackers.infor.gather.dialogs.ProgressDialogFragment;
import com.dabeshackers.infor.gather.entities.User;
import com.dabeshackers.infor.gather.helpers.LoginHelper;
import com.dabeshackers.infor.gather.helpers.NetworkConnectivityHelper;
import com.dabeshackers.infor.gather.http.ApplicationWebService;

public class LoginScreen extends SherlockFragmentActivity implements Runnable {
	private final String TAG = LoginScreen.class.getSimpleName();

	private DBAdapter db;
	private SharedPreferences settings;

	TextView txtRegister;
	EditText txtUser;
	EditText txtPass;
	Button btnLogon, btnCantLogon;

	//SOCIAL NETWORK COMPONENTS
	Button btnFacebook, btnGoogle;
	SocialAuthAdapter adapter;
	String providerName;
	//END SOCIAL NETWORK COMPONENTS

	AppMain appMain;

	private ProgressDialogFragment pd = null;

	private int DO_WHAT_THREAD;
	//	private static final int THREAD_SYNC = 0;
	private static final int THREAD_LOGIN = 1;

	private int LOGIN_RESULT;
	private static final int LOGIN_RESULT_CREDENTIALS_INVALID = 0;
	private static final int LOGIN_RESULT_FAIL = 1;
	private static final int LOGIN_RESULT_SUCCESS = 2;
	private static final int LOGIN_RESULT_NO_CONNECTION = 3;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (DO_WHAT_THREAD) {

			case THREAD_LOGIN:
				switch (LOGIN_RESULT) {
				case LOGIN_RESULT_CREDENTIALS_INVALID:

					Toast.makeText(getApplicationContext(), "Incorrect Username and/or Password", Toast.LENGTH_SHORT).show();
					pd.dismiss();
					break;

				case LOGIN_RESULT_FAIL:

					Toast.makeText(getApplicationContext(), "Log in failed.", Toast.LENGTH_SHORT).show();
					pd.dismiss();
					break;

				case LOGIN_RESULT_NO_CONNECTION:

					Toast.makeText(getApplicationContext(), "Unable to Login. Please check your internet connection.", Toast.LENGTH_SHORT).show();
					pd.dismiss();
					break;

				case LOGIN_RESULT_SUCCESS:
					//Push GCM to back-end
					new Thread(new Runnable() {

						@Override
						public void run() {
							if (NetworkConnectivityHelper.isDeviceConnectedToInternet(LoginScreen.this)) {
								try {
									int tries = 0;
									int triesLimit = 10;
									while (!ApplicationWebService.GCM.pushRegistrationToBackend(LoginScreen.this, appMain.getCurrentGCMObject(), appMain.getCurrentUser().getId())) {
										if (triesLimit >= 10) {
											Log.i(TAG, "GCM push to back-end failed after several retries. Exiting app.");
											LoginScreen.this.finish();
											break;

										}

										tries++;
										Log.i(TAG, "GCM push to back-end failed. Retries: " + tries);
									}

									//									if(GCMWebService.pushRegistrationToBackend(appMain.getCurrentGCMObject(), appMain.getCurrentUser().getId())){
									//										Log.i(TAG, "GCM pushed to back-end");
									//									}else{
									//										Log.i(TAG, "GCM push to back-end failed.");
									//									}
								} catch (UnsupportedEncodingException e) {
									Log.e(TAG, "Error: " + e.getMessage());
									e.printStackTrace();
								} catch (InterruptedException e) {
									Log.e(TAG, "Error: " + e.getMessage());
									e.printStackTrace();
								}
							}
						}

					}).start();

					//edit sharedprefs object to save the logged in user
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(ApplicationUtils.PREFS_LOGIN_ID, ((AppMain) LoginScreen.this.getApplication()).getCurrentUser().getId());
					editor.putString(ApplicationUtils.PREFS_LOGIN_USER, txtUser.getText().toString());
					editor.putString(ApplicationUtils.PREFS_LOGIN_PASSWORD, txtPass.getText().toString()); //TODO: Should be encrypted
					editor.commit();

					pd.dismiss();

					proceedToHome();

					break;
				}
				break;

			}

		}
	};

	@SuppressWarnings("unused")
	private void setUpClassicalLogin() {
		txtUser = (EditText) findViewById(R.id.txtUser);
		txtPass = (EditText) findViewById(R.id.txtPass);
		txtPass.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				logIn();
				return false;
			}
		});

		btnLogon = (Button) findViewById(R.id.btnLogin);
		btnLogon.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				logIn();
			}
		});

		btnCantLogon = (Button) findViewById(R.id.btnCantLogon);
		btnCantLogon.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//				showCantLoginDialog("Reset password");
			}
		});

		//AUTO-LOGIN
		String user = settings.getString(ApplicationUtils.PREFS_LOGIN_USER, "");
		String pass = settings.getString(ApplicationUtils.PREFS_LOGIN_PASSWORD, "");
		if (user.length() > 0 && pass.length() > 0) {
			txtUser.setText(user);
			txtPass.setText(pass);

			logIn();
		}
	}

	private void setUpSocialLogin() {

		adapter = new SocialAuthAdapter(new SocialAuthResponseListener());
		btnFacebook = (Button) findViewById(R.id.btnFacebook);
		btnFacebook.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// This method will enable the selected provider
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); //get the fragment
				pd = ProgressDialogFragment.newInstance("Logging in", "Please wait...");
				pd.setCancelable(false);
				pd.show(ft, "ProgressDialog");
				adapter.setTitleVisible(false);
				adapter.authorize(LoginScreen.this, Provider.FACEBOOK);
			}
		});

		btnGoogle = (Button) findViewById(R.id.btnGoogle);
		btnGoogle.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// This method will enable the selected provider
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); //get the fragment
				pd = ProgressDialogFragment.newInstance("Logging in", "Please wait...");
				pd.setCancelable(false);
				pd.show(ft, "ProgressDialog");
				adapter.setTitleVisible(false);
				adapter.addCallBack(Provider.GOOGLE, "http://socialauth.in/socialauthdemo");
				adapter.authorize(LoginScreen.this, Provider.GOOGLE);
			}
		});

		String prov = settings.getString(ApplicationUtils.PREFS_LOGIN_SOCIAL_PROVIDER, "");
		if (prov.length() > 0) {
			if (prov.equals(Provider.GOOGLE.toString())) {
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); //get the fragment
				pd = ProgressDialogFragment.newInstance("Logging in", "Please wait...");
				pd.setCancelable(false);
				pd.show(ft, "ProgressDialog");
				adapter.addCallBack(Provider.GOOGLE, "http://socialauth.in/socialauthdemo");
				adapter.authorize(LoginScreen.this, Provider.GOOGLE);
			} else if (prov.equals(Provider.FACEBOOK.toString())) {
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); //get the fragment
				pd = ProgressDialogFragment.newInstance("Logging in", "Please wait...");
				pd.setCancelable(false);
				pd.show(ft, "ProgressDialog");
				adapter.authorize(LoginScreen.this, Provider.FACEBOOK);
			}

		}

	}

	// To receive the response after authentication
	public final class SocialAuthResponseListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {

			Log.i(TAG, "Successful");

			// Get the provider
			providerName = values.getString(SocialAuthAdapter.PROVIDER);
			Log.i(TAG, "providername = " + providerName);

			//			Toast.makeText(LoginScreen.this, providerName + " connected", Toast.LENGTH_SHORT).show();

			//Retrieve profile data
			adapter.getUserProfileAsync(new SocialAuthListener<Profile>() {

				@Override
				public void onExecute(String provider, Profile t) {

					Log.i(TAG, "Receiving Data");
					final Profile profileMap = t;
					appMain.setLoggedInProfile(profileMap);

					//Push GCM to back-end
					new Thread(new Runnable() {
						@Override
						public void run() {
							if (NetworkConnectivityHelper.isDeviceConnectedToInternet(LoginScreen.this)) {
								try {

									//Proceed to "Login"
									User u = appMain.getCurrentUser();
									u.setId(profileMap.getEmail());
									u.setEmail(profileMap.getEmail());
									u.setFirstName(profileMap.getFirstName());
									u.setLastName(profileMap.getLastName());

									int tries = 0;
									int triesLimit = 10;
									while (!ApplicationWebService.GCM.pushRegistrationToBackend(LoginScreen.this, appMain.getCurrentGCMObject(), appMain.getCurrentUser().getId())) {
										if (triesLimit >= 10) {
											Log.i(TAG, "GCM push to back-end failed after several retries. Exiting app.");
											LoginScreen.this.finish();
											break;

										}

										tries++;
										Log.i(TAG, "GCM push to back-end failed. Retries: " + tries);
									}

									//									if(GCMWebService.pushRegistrationToBackend(appMain.getCurrentGCMObject(), appMain.getCurrentUser().getId())){
									//										Log.i(TAG, "GCM pushed to back-end");
									//									}else{
									//										Log.i(TAG, "GCM push to back-end failed.");
									//									}

									//edit sharedprefs object to save the logged in user
									SharedPreferences.Editor editor = settings.edit();
									editor.putLong(ApplicationUtils.PREFS_LOGIN_LAST_LOGIN, new Date().getTime());
									editor.putString(ApplicationUtils.PREFS_LOGIN_ID, (appMain.getCurrentUser().getId()));
									editor.putString(ApplicationUtils.PREFS_LOGIN_USER, (appMain.getCurrentUser().getId()));
									editor.putString(ApplicationUtils.PREFS_LOGIN_SOCIAL_PROVIDER, providerName); //TODO: Should be encrypted
									editor.commit();

									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											//Add this adapter instance to appMain object
											appMain.setSocialAdapter(adapter);

											//Unload this and proceed to home
											if (pd != null) {
												pd.dismiss();
											}

											proceedToHome();

										}

									});

								} catch (UnsupportedEncodingException e) {
									Log.e(TAG, "Error: " + e.getMessage());
									e.printStackTrace();
								} catch (InterruptedException e) {
									Log.e(TAG, "Error: " + e.getMessage());
									e.printStackTrace();
								}
							}
						}

					}).start();

				}

				@Override
				public void onError(SocialAuthError e) {
					if (pd != null) {
						pd.dismiss();
					}

				}

			});
		}

		@Override
		public void onError(SocialAuthError error) {
			Log.i(TAG, "Error");
			error.printStackTrace();
			if (pd != null) {
				pd.dismiss();
			}
		}

		@Override
		public void onCancel() {
			Log.i(TAG, "Cancelled");
			if (pd != null) {
				pd.dismiss();
			}
		}

		@Override
		public void onBack() {
			Log.i(TAG, "Dialog Closed by pressing Back Key");
			if (pd != null) {
				pd.dismiss();
			}

		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_login);

		//		getSupportActionBar().hide();

		appMain = (AppMain) LoginScreen.this.getApplication();
		settings = ApplicationUtils.getSharedPreferences(LoginScreen.this);

		//SETUP CLASSICAL WAY OF LOGIN (comment/uncomment as needed)
		//		setUpClassicalLogin();

		//SETUP SOCIAL NETWORK CAPABILITY (comment/uncomment as needed)
		setUpSocialLogin();

	}

	private void logIn() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); //get the fragment
		pd = ProgressDialogFragment.newInstance("Logging in", "Please wait...");
		pd.setCancelable(false);
		pd.show(ft, "ProgressDialog");

		//		Intent ActivityIntent = new Intent(getApplicationContext(), HomeActivity.class);
		//		startActivity(ActivityIntent);
		//		LoginScreen.this.finish();

		DO_WHAT_THREAD = THREAD_LOGIN;
		Thread thread = new Thread(LoginScreen.this);
		thread.start();
	}

	public void run() {

		switch (DO_WHAT_THREAD) {
		case THREAD_LOGIN:
			try {
				db = new DBAdapter(this, (AppMain) this.getApplication());

				db.open();
				boolean verifyUser = DBAdapter.UserEntity.VerifyUserByName(txtUser.getText().toString().trim());
				db.close();

				if (!verifyUser) {
					//Login with WEB
					if (NetworkConnectivityHelper.isDeviceConnectedToInternet(this)) {

						LoginHelper.fetchRecordByUserName(txtUser.getText().toString().trim(), db);

						//Proceed to Login locally
						db.open();
						boolean result = DBAdapter.UserEntity.fetchUserPassword(txtUser.getText().toString().trim());
						db.close();

						String pass = User.md5(txtPass.getText().toString()); //hashed to md5 
						if (result) {
							if (pass.equals(((AppMain) this.getApplication()).getCurrentUser().getPassword_hash())) {
								LOGIN_RESULT = LOGIN_RESULT_SUCCESS;
							} else {
								LOGIN_RESULT = LOGIN_RESULT_CREDENTIALS_INVALID;
							}
						} else {
							LOGIN_RESULT = LOGIN_RESULT_CREDENTIALS_INVALID;
						}

					} else {
						Log.i("Login", "Unable to Login. No internet connection");
						LOGIN_RESULT = LOGIN_RESULT_NO_CONNECTION;
					}

				} else {
					//Login locally

					db.open();
					boolean result = DBAdapter.UserEntity.fetchUserPassword(txtUser.getText().toString().trim());
					db.close();

					String pass = User.md5(txtPass.getText().toString()); //hashed to md5 
					if (result) {
						if (pass.equals(((AppMain) this.getApplication()).getCurrentUser().getPassword_hash())) {
							LOGIN_RESULT = LOGIN_RESULT_SUCCESS;
						} else {
							LOGIN_RESULT = LOGIN_RESULT_CREDENTIALS_INVALID;
						}
					} else {
						LOGIN_RESULT = LOGIN_RESULT_CREDENTIALS_INVALID;
					}

				}

			} catch (Exception e) {
				LOGIN_RESULT = LOGIN_RESULT_FAIL;
			}
			break;
		}

		handler.sendEmptyMessage(0);
	}

	private void proceedToHome() {
		//Retrieve mode and attach it to extras
		SharedPreferences settings = ApplicationUtils.getSharedPreferences(LoginScreen.this);
		int mode = settings.getInt(ApplicationUtils.PREFS_LAST_OPERATION_MODE_USED, 0);

		//Unload this and proceed to home
		Intent activityIntent = new Intent(getApplicationContext(), MainContainerActivity.class);
		startActivity(activityIntent);
		LoginScreen.this.finish();
	}

	//	@SuppressWarnings({ "unchecked", "rawtypes" })
	//	private void showCantLoginDialog(String message){
	//		final Dialog alert = new Dialog(LoginScreen.this);
	//		alert.setContentView(R.layout.dialog_reset_password);
	//		alert.setTitle(message);
	//
	//		//Define filter text events
	//		final EditText email = (EditText) alert.findViewById(R.id.email);
	//
	//		//Define ok button events
	//		Button reset = (Button) alert.findViewById(R.id.reset);
	//		reset.setOnClickListener(new OnClickListener() {
	//			public void onClick(View v) {
	//				String emailStr = email.getText().toString();
	//				
	//				if(InputValidationHelper.isEmailValid(emailStr)){
	//					
	//					
	//					if(emailStr.length() > 0){
	//						String title = "Email sent";
	//						String message = "An email has been sent to %s. Please follow the instructions inside the message to reset your password.";
	//						
	//						AlertDialogHelper.showAlertDialog(LoginScreen.this, title, String.format(message, emailStr), null);
	//						
	//						alert.dismiss();
	//
	//					}
	//					
	//					
	//				}else{
	//					String title = "Email not valid";
	//					String message = "Please check your email and try again.";
	//					
	//					AlertDialogHelper.showAlertDialog(LoginScreen.this, title, String.format(message, emailStr), null);
	//				}
	//
	//			}
	//		});
	//
	//		/**
	//		 * *************************************
	//		 * END VIEW COMPONENTS EVENT DEFINITIONS
	//		 * *************************************
	//		 */
	//
	//		alert.show();
	//	}
}
