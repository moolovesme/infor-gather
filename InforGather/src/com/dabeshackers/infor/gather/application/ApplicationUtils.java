package com.dabeshackers.infor.gather.application;


import java.io.File;

import com.dabeshackers.infor.gather.helpers.DeviceInfoHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;

public class ApplicationUtils {
	public static String getPackageName(Context context) throws NullPointerException {
		if (context == null) {
			throw new NullPointerException();
		}
		return context.getPackageName();
	}

	public static int getPackageHash(Context context) {
		String s = getPackageName(context);
		return s.hashCode();
	}

	public static String getCurrentUser(Context context) {
		return DeviceInfoHelper.getDeviceName();
	}

	public static String getDeviceName(Context context) {
		return DeviceInfoHelper.getDeviceName();
	}

	public static SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(getPackageName(context) + "_preferences", Context.MODE_PRIVATE);
	}

	public static SharedPreferences.Editor getSharedPreferencesEditor(Context context) {
		return context.getSharedPreferences(getPackageName(context) + "_preferences", Context.MODE_PRIVATE).edit();
	}

	public static boolean doesDeviceSupportsCamera(Context context) {
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	public static final String INTENT_EXTRAS_URL = "URL";
	public static final String INTENT_EXTRAS_SELECTED = "SELECTED";
	public static final String HTTP_PROTOCOL = "http://";

	public static final String AUTHENTICATION_FAILED = "0";

	public static final String HOME_URL = "http://codesndbx.com";
	public static final String FTP_ROOT_IMAGES_URL = "/lokal/images/";
	public static final String FTP_ROOT_TEMP_URL = "/lokal/temp/";
	public static final String ACCOUNT_IMAGES_URL = HOME_URL + "/android/lokal/images/";

	public static final int BROADCAST_REQUEST_CODE = 192837;

	//Preferences Strings
	public static final String PREFS_NAME = "preferences";
	public static final String PREFS_LOGIN_ID = "UserId";
	public static final String PREFS_LOGIN_USER = "UserName";
	public static final String PREFS_LOGIN_PASSWORD = "Password";
	public static final String PREFS_LOGIN_SOCIAL_PROVIDER = "SocialProvider";
	public static final String PREFS_LOGIN_LAST_LOGIN = "LastLogin";
	public static final String PREFS_ACTIVATION_IS_ACTIVATED = "isActivated";
	public static final String PREFS_ACTIVATION_TOKEN = "ActivationToken";
	public static final String PREFS_SERVICES_IS_INITIALIZED = "isServicesInitialized";
	public static final String PREFS_LAST_SYNC = "LastSync";
	public static final String PREFS_GCM_REGID = "GCMRegId";
	public static final String PREFS_PACKAGE_NAME = "PackageName";
	public static final String PREFS_LAST_OPERATION_MODE_USED = "LastModeUsed";

	public static final String PREFS_LAST_KNOWN_LOCATION_LNG = "lkl_lng";
	public static final String PREFS_LAST_KNOWN_LOCATION_LAT = "lkl_lat";

	public static final String IMAGE_EXTENSION = ".jpg";

	public static class Paths {
		public static String getLocalAppRootFolder(Context context) {
			String path = Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName(context) + "/";
			String path2 = path + "files/";
			File f = new File(path2);
			if (!f.exists()) {
				f.mkdirs();
			}
			return path;
		}

		public static String getLocalAppFilesFolder(Context context) {
			String path = getLocalAppRootFolder(context) + "files/";
			File f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
			}
			return path;
		}

		public static String getLocalAppTempFolder(Context context) {
			String path = getLocalAppFilesFolder(context) + "temp/";
			File f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
			}
			return path;
		}

		public static String getLocalAppImagesFolder(Context context) {
			String path = getLocalAppFilesFolder(context) + "images/";
			File f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
			}
			return path;
		}
	}
}
