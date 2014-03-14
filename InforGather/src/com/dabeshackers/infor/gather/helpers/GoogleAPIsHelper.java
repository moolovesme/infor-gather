package com.dabeshackers.infor.gather.helpers;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.dabeshackers.infor.gather.application.ApplicationUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class GoogleAPIsHelper {

	static final String TAG = GoogleAPIsHelper.class.getSimpleName();

	public static Drive getDriveService(GoogleAccountCredential credential) {
		return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
		.build();
	}

	public static List<File> retrieveAllFiles(Drive service) throws IOException {
		List<File> result = new ArrayList<File>();
		Files.List request = service.files().list();

		do {
			try {
				FileList files = request.execute();

				result.addAll(files.getItems());
				request.setPageToken(files.getNextPageToken());
			} catch (IOException e) {
				System.out.println("An error occurred: " + e);
				request.setPageToken(null);
			}
		} while (request.getPageToken() != null && request.getPageToken().length() > 0);

		return result;
	}


	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";

	static String SENDER_ID = "173808730396"; //https://cloud.google.com/console?redirected=true#/project/apps~valid-weaver-435
	static GoogleCloudMessaging gcm;
	
	public class GCMResultObject{
		public String regid;
		public String msg;
		public String device;
		
		public GCMResultObject(){
			regid = "";
			msg = "";
			device = "";
		}
	}

	public static GCMResultObject attemptGCMRegistration(Context context){

		GCMResultObject result = new GoogleAPIsHelper().new GCMResultObject();
		
		String msg = "";
		try {
			if (gcm == null) {
				gcm = GoogleCloudMessaging.getInstance(context);
			}
			result.device = ApplicationUtils.getDeviceName(context);
			result.regid = gcm.register(SENDER_ID);
			result.msg = "Device [ " + result.device + " ] registered, registration ID : " + result.regid;
			
			Log.d(TAG, msg);

		} catch (IOException ex) {
			result.msg = "Error :" + ex.getMessage();
		}
		return result;
	}

	

}
