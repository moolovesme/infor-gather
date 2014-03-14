package com.dabeshackers.infor.gather.helpers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkConnectivityHelper {
	public static boolean isDeviceConnectedToInternet(Context context){
		boolean isDeviceConnectedToInternet = false;
		NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

		if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
			try {
				InetAddress.getByName("google.com").isReachable(3);
				isDeviceConnectedToInternet = true;
			} catch (UnknownHostException e){
				isDeviceConnectedToInternet = false;
			} catch (IOException e){
				isDeviceConnectedToInternet = false;
			}
		}

		return isDeviceConnectedToInternet;
	}
}
