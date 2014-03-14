package com.dabeshackers.infor.gather.helpers;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.dabeshackers.infor.gather.application.ApplicationUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class MediaCaptureHelper {
	
	private static final String TAG = MediaCaptureHelper.class.getSimpleName();

	// Activity request codes
	public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	public static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
	public static final int GALLERY_PICK_IMAGE_REQUEST_CODE = 300;
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	public static Uri captureImage(Context context, Uri fileUri) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = getOutputMediaFileUri(context, MEDIA_TYPE_IMAGE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		// start the image capture Intent
		((Activity)context).startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
		
		return fileUri;
	}
	
	public static void recordVideo(Context context, Uri fileUri) {
	    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
	 
	    fileUri = getOutputMediaFileUri(context, MEDIA_TYPE_VIDEO);
	 
	    // set video quality
	    // 1- for high quality video
	    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
	 
	    // start the video capture Intent
	    ((Activity)context).startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
	}
	
	public static Uri pickPhoto(Context context, Uri fileUri) {
		Intent intent = new Intent(Intent.ACTION_PICK);
		
		fileUri = getOutputMediaFileUri(context, MEDIA_TYPE_IMAGE);
		
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		intent.setType("image/*");
		
		// start the image picker Intent
		((Activity)context).startActivityForResult(intent, GALLERY_PICK_IMAGE_REQUEST_CODE);
		
				
		return fileUri;
	}


	/**
	 * Creating file uri to store image/video
	 */
	public static Uri getOutputMediaFileUri(Context context, int type) {
		return Uri.fromFile(getOutputMediaFile(context, type));
	}

	/*
	 * returning image / video
	 */
	private static File getOutputMediaFile(Context context, int type) {

		// External sdcard location
		File mediaStorageDir = new File(ApplicationUtils.Paths.getLocalAppImagesFolder(context));

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(TAG, "Failed to create " + ApplicationUtils.Paths.getLocalAppImagesFolder(context) + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".png");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

}
