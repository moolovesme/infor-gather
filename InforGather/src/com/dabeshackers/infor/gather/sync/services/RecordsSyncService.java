package com.dabeshackers.infor.gather.sync.services;

import com.dabeshackers.infor.gather.application.AppMain;
import com.dabeshackers.infor.gather.sync.adapters.RecordsSyncAdapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RecordsSyncService extends Service {

	private static final Object sSyncAdapterLock = new Object();
	private static RecordsSyncAdapter sSyncAdapter = null;

	@Override
	public void onCreate() {
		synchronized (sSyncAdapterLock) {
			if (sSyncAdapter == null)
				sSyncAdapter = new RecordsSyncAdapter(getApplicationContext(), true, (AppMain)getApplication());
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return sSyncAdapter.getSyncAdapterBinder();
	}

}