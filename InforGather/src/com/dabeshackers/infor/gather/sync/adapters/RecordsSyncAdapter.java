package com.dabeshackers.infor.gather.sync.adapters;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.dabeshackers.infor.gather.application.AppMain;
import com.dabeshackers.infor.gather.application.ApplicationUtils;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public class RecordsSyncAdapter extends AbstractThreadedSyncAdapter {
	private final String TAG = RecordsSyncAdapter.class.getSimpleName();
	private AppMain appMain;
	String userId;

	public RecordsSyncAdapter(Context context, boolean autoInitialize, AppMain appMain) {
		this(context, autoInitialize);
		this.appMain = appMain;
	}

	public RecordsSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		//			mAccountManager = AccountManager.get(context);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

		try {

			Log.i(TAG, "Performing Sync");

			SharedPreferences settings = ApplicationUtils.getSharedPreferences(getContext());
			userId = settings.getString(ApplicationUtils.PREFS_LOGIN_ID, null);

			if (appMain != null && userId != null && userId.length() > 0) {

				//				syncChronicles(syncResult);
				//				syncMedia(syncResult);

			} else {
				Log.i(TAG, "No user has logged in yet or appMain is null.");
				syncResult.stats.numAuthExceptions++;
			}

			Log.i(TAG, "Done Performing Sync at " + new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.ENGLISH).format(new Date()) + " " + syncResult.stats.numEntries + " records processed.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//	private <T extends Chronicle> void syncChronicles(SyncResult syncResult) throws Exception{
	//		DBAdapter db = new DBAdapter(getContext(), appMain);
	//
	//		List<T> toRemote = new ArrayList<T>();
	//		List<T> toLocal = new ArrayList<T>();
	//		List<T> toUpdateRemote = new ArrayList<T>();
	//		List<T> toUpdateLocal = new ArrayList<T>();
	//
	//		// Get records from the remote server
	//		List<T> remoteRecords = (List<T>) ChronicleWebService.fetchRecordsByUserId(userId);
	//
	//		// Get shows from the local storage
	//		List<T> localRecords = new ArrayList<T>();
	//		db.open();
	//		localRecords = (List<T>) db.chronicles.fetchRecords();
	//		db.close();
	//
	//		/****************************************************
	//		 * See what Local records are missing on Remote
	//		 *****************************************************/
	//		for(T local:localRecords){
	//			boolean found = false;
	//			for(Chronicle remote:remoteRecords){
	//				if(local.getId().equals(remote.getId())){
	//					//found
	//					found = true;
	//					break;
	//				}
	//			}
	//
	//			if(!found){
	//				//Add to List that will be synced to web
	//				toRemote.add(local);
	//			}
	//		}
	//
	//		/****************************************************
	//		 * See what Remote records are missing on Local
	//		 *****************************************************/
	//		for(T remote:remoteRecords){
	//			boolean found = false;
	//			for(T local:localRecords){
	//				if(remote.getId().equals(local.getId())){
	//					//found
	//					found = true;
	//					break;
	//				}
	//			}
	//
	//			if(!found){
	//				//Add to List that will be synced to web
	//				toLocal.add(remote);
	//			}
	//		}
	//
	//		/****************************************************
	//		 * See what Remote records need to be updated
	//		 *****************************************************/
	//		for(T remote:remoteRecords){
	//			for(T local:localRecords){
	//				if(remote.getId().equals(local.getId())){
	//					//found
	//					if(remote.getVersion() < local.getVersion()){
	//						toUpdateRemote.add(local);
	//					}
	//					break;
	//				}
	//			}
	//		}
	//
	//		/****************************************************
	//		 * See what Local records need to be updated
	//		 *****************************************************/
	//		for(T local:localRecords){
	//			for(T remote:remoteRecords){
	//				if(local.getId().equals(remote.getId())){
	//					//found
	//					if(local.getVersion() < remote.getVersion()){
	//						toUpdateLocal.add(remote);
	//					}
	//					break;
	//				}
	//			}
	//		}
	//
	//		//Summarize operations:
	//		Log.i(TAG + " >> Chronicles", "Records to be created to local: " + toLocal.size());
	//		Log.i(TAG + " >> Chronicles", "Records to be updated to local: " + toUpdateLocal.size());
	//		Log.i(TAG + " >> Chronicles", "Records to be created to cloud: " + toRemote.size());
	//		Log.i(TAG + " >> Chronicles", "Records to be updated to cloud: " + toUpdateRemote.size());
	//
	//		//Process lists
	//		for(Chronicle local: toLocal){
	//			db.open();
	//			db.chronicles.createRecord(local);
	//			db.close();
	//			syncResult.stats.numEntries++;
	//		}
	//
	//		for(Chronicle local: toUpdateLocal){
	//			db.open();
	//			db.chronicles.updateRecord(local);
	//			db.close();
	//			syncResult.stats.numEntries++;
	//		}
	//
	//		for(Chronicle remote: toRemote){
	//			ChronicleWebService.pushRecordToBackend(remote);
	//			syncResult.stats.numEntries++;
	//		}
	//
	//		for(Chronicle remote: toUpdateRemote){
	//			ChronicleWebService.pushRecordToBackend(remote);
	//			syncResult.stats.numEntries++;
	//		}
	//	}
	//
	//	private <T extends Media> void syncMedia(SyncResult syncResult) throws Exception{
	//		DBAdapter db = new DBAdapter(getContext(), appMain);
	//
	//		List<T> toRemote = new ArrayList<T>();
	//		List<T> toLocal = new ArrayList<T>();
	//		List<T> toUpdateRemote = new ArrayList<T>();
	//		List<T> toUpdateLocal = new ArrayList<T>();
	//
	//		// Get records from the remote server
	//		List<T> remoteRecords = (List<T>) MediaWebService.fetchRecordsByUserId(userId);
	//
	//		// Get shows from the local storage
	//		List<T> localRecords = (List<T>) new ArrayList<T>();
	//		db.open();
	//		localRecords = (List<T>) db.media.fetchRecords();
	//		db.close();
	//
	//		/****************************************************
	//		 * See what Local records are missing on Remote
	//		 *****************************************************/
	//		for(T local:localRecords){
	//			boolean found = false;
	//			for(T remote:remoteRecords){
	//				if(local.getId().equals(remote.getId())){
	//					//found
	//					found = true;
	//					break;
	//				}
	//			}
	//
	//			if(!found){
	//				//Add to List that will be synced to web
	//				toRemote.add(local);
	//			}
	//		}
	//
	//		/****************************************************
	//		 * See what Remote records are missing on Local
	//		 *****************************************************/
	//		for(T remote:remoteRecords){
	//			boolean found = false;
	//			for(T local:localRecords){
	//				if(remote.getId().equals(local.getId())){
	//					//found
	//					found = true;
	//					break;
	//				}
	//			}
	//
	//			if(!found){
	//				//Add to List that will be synced to web
	//				toLocal.add(remote);
	//			}
	//		}
	//
	//		/****************************************************
	//		 * See what Remote records need to be updated
	//		 *****************************************************/
	//		for(T remote:remoteRecords){
	//			for(T local:localRecords){
	//				if(remote.getId().equals(local.getId())){
	//					//found
	//					if(remote.getVersion() < local.getVersion()){
	//						toUpdateRemote.add(local);
	//					}
	//					break;
	//				}
	//			}
	//		}
	//
	//		/****************************************************
	//		 * See what Local records need to be updated
	//		 *****************************************************/
	//		for(T local:localRecords){
	//			for(T remote:remoteRecords){
	//				if(local.getId().equals(remote.getId())){
	//					//found
	//					if(local.getVersion() < remote.getVersion()){
	//						toUpdateLocal.add(remote);
	//					}
	//					break;
	//				}
	//			}
	//		}
	//
	//		//Summarize operations:
	//		Log.i(TAG + " >> Media", "Records to be created to local: " + toLocal.size());
	//		Log.i(TAG + " >> Media", "Records to be updated to local: " + toUpdateLocal.size());
	//		Log.i(TAG + " >> Media", "Records to be created to cloud: " + toRemote.size());
	//		Log.i(TAG + " >> Media", "Records to be updated to cloud: " + toUpdateRemote.size());
	//
	//		//Process lists
	//		for(T local: toLocal){
	//			db.open();
	//			db.media.createRecord(local);
	//			db.close();
	//			syncResult.stats.numEntries++;
	//		}
	//
	//		for(T local: toUpdateLocal){
	//			db.open();
	//			db.media.updateRecord(local);
	//			db.close();
	//			syncResult.stats.numEntries++;
	//		}
	//
	//		for(T remote: toRemote){
	//			MediaWebService.pushRecordToBackend(remote);
	//			syncResult.stats.numEntries++;
	//		}
	//
	//		for(T remote: toUpdateRemote){
	//			MediaWebService.pushRecordToBackend(remote);
	//			syncResult.stats.numEntries++;
	//		}
	//	}
}