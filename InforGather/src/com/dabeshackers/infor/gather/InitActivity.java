package com.dabeshackers.infor.gather;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.dabeshackers.infor.gather.application.AppMain;
import com.dabeshackers.infor.gather.application.ApplicationUtils;
import com.dabeshackers.infor.gather.entities.User;
import com.dabeshackers.infor.gather.helpers.NetworkConnectivityHelper;
import com.dabeshackers.infor.gather.http.ApplicationWebService;

public class InitActivity extends SherlockActivity {

	private SharedPreferences settings;

	private static final String TAG = InitActivity.class.getSimpleName();
	private static boolean DEBUG = true;
	private static int SPLASH_TIME_OUT = 3000;

	private AppMain appMain;

	ProgressDialog pd;

	// Instance fields
	Account mAccount;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//		overridePendingTransition(R.anim.fade_in, R.anim.fade_out); 

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_init);

		//		final ImageView animImageView = (ImageView) findViewById(R.id.loading);
		//	    animImageView.setBackgroundResource(R.drawable.loading);
		//	    animImageView.post(new Runnable() {
		//	        @Override
		//	        public void run() {
		//	            AnimationDrawable frameAnimation = (AnimationDrawable) animImageView.getBackground();
		//	            frameAnimation.start();
		//	        }
		//	    });
		//		
		//		

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				//				//Show Progress Dialog
				//		pd = new ProgressDialog(InitActivity.this);
				//		pd.setIndeterminate(true);
				//		pd.setTitle("Loading.");
				//		pd.setMessage("Please wait...");
				//		pd.setCancelable(false);
				//		pd.show();

				settings = ApplicationUtils.getSharedPreferences(InitActivity.this);

				//        		//Sync initialization
				//        		// Create the dummy account
				//        		mAccount = CreateSyncAccount(this);
				appMain = ((AppMain) getApplication());
				//        		appMain.setSyncAccount(mAccount);
				//        		appMain.setAuthority(AppMain.AUTHORITY);
				//        		appMain.setSyncAutomatically();
				//        		//End sync initialization
				//
				//        		//Register to GCM
				//        		new Thread(new Runnable(){
				//
				//        			@Override
				//        			public void run() {
				//        				appMain.registerGCM(InitActivity.this);
				//        				//Do something with regid e.g. send to backend
				//        			}
				//
				//        		}).start();
				//        		//End GCM Registration

				attemptSocialLogin(); //Uncomment or comment out as needed.
				//		attemptClassicLogin(); //Uncomment or comment out as needed.

			}
		}, SPLASH_TIME_OUT);
	}

	public static Account CreateSyncAccount(Context context) {
		// Create the account type and default account
		Account newAccount = new Account(AppMain.ACCOUNT, AppMain.ACCOUNT_TYPE);
		// Get an instance of the Android account manager
		AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
		/*
		 * Add the account and account type, no password or user data If
		 * successful, return the Account object, otherwise report an error.
		 */
		if (accountManager.addAccountExplicitly(newAccount, null, null)) {
			/*
			 * If you don't set android:syncable="true" in in your <provider>
			 * element in the manifest, then call context.setIsSyncable(account,
			 * AUTHORITY, 1) here.
			 */
		} else {
			/*
			 * The account exists or some other error occurred. Log this, report
			 * it, or handle it internally.
			 */
		}

		return newAccount;
	}

	private void attemptClassicLogin() {

		String password = settings.getString(ApplicationUtils.PREFS_LOGIN_PASSWORD, null);

		boolean result = true; //false;

		if (result) {
			if (DEBUG) {
				Intent ActivityIntent = new Intent(getApplicationContext(), LoginScreen.class);
				startActivity(ActivityIntent);
				InitActivity.this.finish();
			} else if (password.equals(((AppMain) this.getApplication()).getCurrentUser().getPassword_hash())) {
				proceedToHome();
			} else {
				Intent ActivityIntent = new Intent(getApplicationContext(), LoginScreen.class);
				startActivity(ActivityIntent);
				InitActivity.this.finish();
			}
		} else {
			Intent ActivityIntent = new Intent(getApplicationContext(), LoginScreen.class);
			startActivity(ActivityIntent);
			InitActivity.this.finish();
		}
	}

	SocialAuthAdapter adapter;

	private void attemptSocialLogin() {
		adapter = new SocialAuthAdapter(new DialogListener() {
			@Override
			public void onBack() {
			}

			@Override
			public void onCancel() {
			}

			@Override
			public void onComplete(Bundle values) {
				Log.i(TAG, "Successful");

				// Get the provider
				final String providerName = values.getString(SocialAuthAdapter.PROVIDER);
				Log.i(TAG, "providername = " + providerName);

				//				Toast.makeText(InitActivity.this, providerName + " connected", Toast.LENGTH_SHORT).show();

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
								if (NetworkConnectivityHelper.isDeviceConnectedToInternet(InitActivity.this)) {
									try {
										int tries = 0;
										int triesLimit = 10;
										while (!ApplicationWebService.GCM.pushRegistrationToBackend(InitActivity.this, appMain.getCurrentGCMObject(), appMain.getCurrentUser().getId())) {
											if (triesLimit >= 10) {
												Log.i(TAG, "GCM push to back-end failed after several retries. Exiting app.");
												InitActivity.this.finish();
												break;
											}

											tries++;
											Log.i(TAG, "GCM push to back-end failed. Retries: " + tries);
										}

										//										if(GCMWebService.pushRegistrationToBackend(appMain.getCurrentGCMObject(), appMain.getCurrentUser().getId())){
										//											Log.i(TAG, "GCM pushed to back-end");
										//										}else{
										//											Log.i(TAG, "GCM push to back-end failed.");
										//										}

										//Proceed to "Login"
										User u = appMain.getCurrentUser();
										u.setId(profileMap.getEmail());
										u.setEmail(profileMap.getEmail());
										u.setFirstName(profileMap.getFirstName());
										u.setLastName(profileMap.getLastName());

										//edit sharedprefs object to save the logged in user
										SharedPreferences.Editor editor = settings.edit();
										editor.putLong(ApplicationUtils.PREFS_LOGIN_LAST_LOGIN, new Date().getTime());
										editor.putString(ApplicationUtils.PREFS_LOGIN_ID, (appMain.getCurrentUser().getId()));
										editor.putString(ApplicationUtils.PREFS_LOGIN_USER, (appMain.getCurrentUser().getId()));
										editor.putString(ApplicationUtils.PREFS_LOGIN_SOCIAL_PROVIDER, providerName);
										editor.commit();

										runOnUiThread(new Runnable() {

											@Override
											public void run() {
												//Add this adapter instance to appMain object
												appMain.setSocialAdapter(adapter);

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

					}

				});
			}

			@Override
			public void onError(SocialAuthError arg0) {
			}

		});

		//AUTO-LOGIN
		final String prov = settings.getString(ApplicationUtils.PREFS_LOGIN_SOCIAL_PROVIDER, "");
		if (prov.length() > 0) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					//Populate last user
					Profile profileMap = appMain.getLoggedInProfile();
					User u = appMain.getCurrentUser();
					u.setId(profileMap.getEmail());
					u.setEmail(profileMap.getEmail());
					u.setFirstName(profileMap.getFirstName());
					u.setLastName(profileMap.getLastName());

					final String userId = settings.getString(ApplicationUtils.PREFS_LOGIN_ID, "");

					//validate local login based on last logged in
					long lastLog = settings.getLong(ApplicationUtils.PREFS_LOGIN_LAST_LOGIN, 0);
					long sevenDays = 1000 * 60 * 60 * 24 * 7;
					if (lastLog > 0 && (lastLog + sevenDays) >= new Date().getTime()) {
						//Make sure to save to GCM table in host
						try {
							boolean result = ApplicationWebService.GCM.pushRegistrationToBackend(InitActivity.this, appMain.getCurrentGCMObject(), userId);
							Log.i(TAG, "GCMWebService.pushRegistrationToBackend Result:" + result);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						//Proceed to login
						//Unload this and proceed to home
						proceedToHome();
					} else {
						//Attempt internet log in
						if (NetworkConnectivityHelper.isDeviceConnectedToInternet(InitActivity.this)) {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									if (prov.equals(Provider.GOOGLE.toString())) {
										adapter.addCallBack(Provider.GOOGLE, "http://socialauth.in/socialauthdemo");
										adapter.authorize(InitActivity.this, Provider.GOOGLE);
									} else if (prov.equals(Provider.FACEBOOK.toString())) {
										adapter.authorize(InitActivity.this, Provider.FACEBOOK);
									}
								}

							});
						} else {
							//Unable to log in. Try the classic way
							attemptClassicLogin();
						}
					}
				}

			}).start();

		} else {
			//Not yet logged in. Try the classic way
			attemptClassicLogin();
		}
	}

	private void proceedToHome() {
		//Retrieve mode and attach it to extras
		SharedPreferences settings = ApplicationUtils.getSharedPreferences(InitActivity.this);
		int mode = settings.getInt(ApplicationUtils.PREFS_LAST_OPERATION_MODE_USED, 0);

		//Unload this and proceed to home
		Intent activityIntent = new Intent(getApplicationContext(), MainContainerActivity.class);
		startActivity(activityIntent);
		InitActivity.this.finish();
	}

	//	private void fireUpBackgroundServices(){
	//		Log.i(TAG, "Background Services initilizing.");
	//		AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
	//
	//		Intent int  ent = new Intent(getApplicationContext(), AlarmReceiver.class);
	//		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), CommonMembers.BROADCAST_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	//		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (REPEAT_TIME), pendingIntent); //wake up every 5 minutes to ensure service stays alive
	//
	//		SharedPreferences.Editor editor = settings.edit();
	//		editor.putInt(PrefStrings.PREFS_SERVICES_IS_INITIALIZED, 1);
	//		editor.commit();
	//
	//	}
}
