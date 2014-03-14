package com.dabeshackers.infor.gather.helpers;

import android.util.Log;

public class YouTubeHelper {
	private final static String TAG = YouTubeHelper.class.getSimpleName();

	public static String getVideoIdFromUrl(String url) {

		Log.d(TAG, "getVideoIdFromUrl : " + url);
		String vid = null;
		try {

			int vindex = url.indexOf("v=");
			int ampIndex = url.indexOf("&", vindex);

			Log.d(TAG, "v= not found " + vindex);
			if (ampIndex < 0) {
				Log.d(TAG, "& not found " + ampIndex);
				vid = url.substring(vindex + 2);
			} else {
				Log.d(TAG, "& found at " + ampIndex);
				vid = url.substring(vindex + 2, ampIndex);
			}
		} catch (Exception e) {
			Log.e(TAG, "Failed to get the vid " + e.getMessage(), e);
		}

		Log.d(TAG, "vid : " + vid);

		return vid;

	}

}