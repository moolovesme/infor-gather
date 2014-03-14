package com.dabeshackers.infor.gather.helpers;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

public class DeviceInfoHelper {
	public static String getMACAddress(Context cxt){
		WifiManager wifiMan = (WifiManager) cxt.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMan.getConnectionInfo();
		return wifiInfo.getMacAddress();
	}

	public static String getPhoneNumber(Context cxt){
		TelephonyManager tMgr =(TelephonyManager)cxt.getSystemService(Context.TELEPHONY_SERVICE);
		return tMgr.getLine1Number();
	}

	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return model;
		} else {
			return manufacturer + " " + model;
		}
	}

	public static String getIMEI(Context cxt){
		TelephonyManager tMgr =(TelephonyManager)cxt.getSystemService(Context.TELEPHONY_SERVICE);
		return tMgr.getDeviceId();
	}
}
