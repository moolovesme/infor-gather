package com.dabeshackers.infor.gather.helpers;

import android.content.Context;
import android.media.AudioManager;

public class DeviceAudioHelper {
	public final static int SILENT = AudioManager.RINGER_MODE_SILENT;
	public final static int NORMAL = AudioManager.RINGER_MODE_NORMAL;
	public final static int VIBRATE = AudioManager.RINGER_MODE_VIBRATE;
	
	public static void setDeviceAudioToSilent(Context context, int audioType){
		AudioManager audiomanager =(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		audiomanager.setRingerMode(audioType);
	}
	
	public static int getRingerMode(Context context){
		AudioManager audiomanager =(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		return audiomanager.getRingerMode();
	}
	
	
	
	
}
