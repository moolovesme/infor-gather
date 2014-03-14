package com.dabeshackers.infor.gather.application;


import java.io.File;

import org.acra.ACRA;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import android.accounts.Account;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.dabeshackers.infor.gather.InitActivity;
import com.dabeshackers.infor.gather.data.DBAdapter;
import com.dabeshackers.infor.gather.entities.User;
import com.dabeshackers.infor.gather.helpers.FileOperationsHelper;
import com.dabeshackers.infor.gather.helpers.GoogleAPIsHelper;
import com.dabeshackers.infor.gather.helpers.HashHelper;
import com.dabeshackers.infor.gather.helpers.GoogleAPIsHelper.GCMResultObject;
import com.google.gson.Gson;

//@ReportsCrashes(formKey = "", // will not be used
//mailTo = "jom.romero@gmail.com",
//mode = ReportingInteractionMode.DIALOG,
//resToastText = R.string.crash_toast_string, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
//resDialogText = R.string.crash_dialog_text,
//resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
//resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
//resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
//resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
//)
public class AppMain extends Application {
	private final String TAG = AppMain.class.getSimpleName();

	//Sync Constants
	private long SYNC_INTERVAL = 60L * 60L; //One minute * 60
	// The authority for the sync adapter's content provider
	public static final String AUTHORITY = "io.epiko.lokal.provider";
	// An account type, in the form of a domain name
	public static final String ACCOUNT_TYPE = "lokal.epiko.io";
	// The account name
	public static final String ACCOUNT = "Default Sync Account";

	private User currentUser;
	private GCMResultObject currentGCMObject;

	private Account syncAccount;
	private String authority = AUTHORITY;
	private SocialAuthAdapter socialAdapter;
	private Profile loggedInProfile;

	@Override
	public void onCreate() {
		super.onCreate();

		//		initACRA(); //Commented as this causes error in Signed Export TODO: fix this

		//Create required folders
		ApplicationUtils.Paths.getLocalAppImagesFolder(getApplicationContext());
		ApplicationUtils.Paths.getLocalAppTempFolder(getApplicationContext());
	}

	public User getCurrentUser() {
		if (currentUser == null) {
			currentUser = new User();
		}
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public Account getSyncAccount() {
		return syncAccount;
	}

	public void setSyncAccount(Account syncAccount) {
		this.syncAccount = syncAccount;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	private final String GCMRESULTOBJECTDATA = "gcmResultObject.dat";

	public GCMResultObject getCurrentGCMObject() {
		if (currentGCMObject == null) {
			String data = FileOperationsHelper.readFromFile(AppMain.this, GCMRESULTOBJECTDATA);
			Gson gson = new Gson();
			currentGCMObject = gson.fromJson(data, GCMResultObject.class);

			//If gcm object retrieved from file is null
			if (currentGCMObject == null) {
				currentGCMObject = new GoogleAPIsHelper().new GCMResultObject();
				currentGCMObject.msg = "null";
			}

		}
		return currentGCMObject;
	}

	public void setCurrentGCMObject(GCMResultObject currentGCMObject) {

		this.currentGCMObject = currentGCMObject;

		//Serialize
		Gson gson = new Gson();
		String json = gson.toJson(currentGCMObject);
		FileOperationsHelper.writeToFile(AppMain.this, json, GCMRESULTOBJECTDATA);
	}

	public void setSyncAutomatically() {

		Log.i(TAG, "Setting Automatic Sync...");

		Bundle settingsBundle = new Bundle();

		ContentResolver.setIsSyncable(syncAccount, AUTHORITY, 1);
		ContentResolver.setSyncAutomatically(syncAccount, authority, true);
		ContentResolver.addPeriodicSync(syncAccount, authority, settingsBundle, SYNC_INTERVAL);
		Log.i(TAG, "Automatic Sync Set every " + SYNC_INTERVAL + " seconds.");
	}

	public void requestSync() {

		Log.i(TAG, "Requesting Sync...");

		Bundle settingsBundle = new Bundle();
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

		ContentResolver.requestSync(syncAccount, authority, settingsBundle);
		Log.i(TAG, "Requested Sync.");
	}

	public void registerGCM(Context context) {

		Log.i(TAG, "Registering to GCM...");
		setCurrentGCMObject(GoogleAPIsHelper.attemptGCMRegistration(context));
		Log.i(TAG, "msg: " + getCurrentGCMObject().msg);

	}

	public SocialAuthAdapter getSocialAdapter() {
		return socialAdapter;
	}

	public void setSocialAdapter(SocialAuthAdapter socialAdapter) {
		this.socialAdapter = socialAdapter;
	}

	public void logOut(Activity activity) {
		socialAdapter = getSocialAdapter();

		SharedPreferences settings = ApplicationUtils.getSharedPreferences(this);
		String prov = settings.getString(ApplicationUtils.PREFS_LOGIN_SOCIAL_PROVIDER, "");
		boolean status = false;
		if (prov.length() > 0 && socialAdapter != null) {
			if (prov.equals(Provider.GOOGLE.toString())) {
				status = socialAdapter.signOut(Provider.GOOGLE.toString());
			} else if (prov.equals(Provider.FACEBOOK.toString())) {
				status = socialAdapter.signOut(Provider.FACEBOOK.toString());
			}
		}

		if (status) {
			//Clear Database
			DBAdapter db = new DBAdapter(activity, this);
			db.open();
			try {
				db.clearEntityTables();
			} catch (Exception e) {
				e.printStackTrace();
			}
			db.close();

			//Clear Image
			final File f = new File(ApplicationUtils.Paths.getLocalAppImagesFolder(AppMain.this), HashHelper.md5(loggedInProfile.getEmail()) + ApplicationUtils.IMAGE_EXTENSION);
			if (!f.delete()) {
				f.deleteOnExit();
			}

			//Clear SharedPreferences
			SharedPreferences.Editor editor = ApplicationUtils.getSharedPreferencesEditor(this);
			editor.putLong(ApplicationUtils.PREFS_LOGIN_LAST_LOGIN, 0);
			editor.putString(ApplicationUtils.PREFS_LOGIN_ID, "");
			editor.putString(ApplicationUtils.PREFS_LOGIN_USER, "");
			editor.putString(ApplicationUtils.PREFS_LOGIN_PASSWORD, "");
			editor.putString(ApplicationUtils.PREFS_LOGIN_SOCIAL_PROVIDER, "");
			editor.commit();

			//Unload this activity and restart the app
			Intent activityIntent = new Intent(this, InitActivity.class);
			activity.startActivity(activityIntent);
			activity.finish();
		}
	}

	private final String LOGGEDINPROFILEDATA = "loggedInProfile.dat";

	public Profile getLoggedInProfile() {
		if (loggedInProfile == null) {
			String data = FileOperationsHelper.readFromFile(AppMain.this, LOGGEDINPROFILEDATA);
			Gson gson = new Gson();
			loggedInProfile = gson.fromJson(data, Profile.class);
		}
		return loggedInProfile;
	}

	public void setLoggedInProfile(Profile loggedInProfile) {
		this.loggedInProfile = loggedInProfile;
		Gson gson = new Gson();
		String json = gson.toJson(loggedInProfile);
		FileOperationsHelper.writeToFile(AppMain.this, json, LOGGEDINPROFILEDATA);

	}

	public void initACRA() {
		// The following line triggers the initialization of ACRA
		ACRA.init(this);
	}
}