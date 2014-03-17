package com.dabeshackers.infor.gather.http;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.dabeshackers.infor.gather.application.ApplicationUtils;
import com.dabeshackers.infor.gather.entities.Offer;
import com.dabeshackers.infor.gather.entities.User;
import com.dabeshackers.infor.gather.helpers.BitmapHelper;
import com.dabeshackers.infor.gather.helpers.FtpHelper;
import com.dabeshackers.infor.gather.helpers.GoogleAPIsHelper.GCMResultObject;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class ApplicationWebService {

	public static class GCM {
		private static final String TAG = GCM.class.getSimpleName();

		public static boolean pushRegistrationToBackend(Context context, GCMResultObject gcm, String userId) throws InterruptedException, UnsupportedEncodingException {
			// Guard:
			if (gcm == null || userId == null || userId.length() == 0 || gcm.regid.length() == 0) {
				Log.w(TAG, "GUARD TRIGGERED; RETURNING FALSE ");
				return false;
			}

			Log.d(TAG, "pushing gcm registration to back-end: " + gcm.toString() + " and userId: " + userId);

			String HTTP_POST_URL = WebServiceUrls.GCM.REGISTRAION;

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("userid", userId));
			postParameters.add(new BasicNameValuePair("device", gcm.device));
			postParameters.add(new BasicNameValuePair("regid", gcm.regid));
			postParameters.add(new BasicNameValuePair("created", String.valueOf(new Date().getTime())));

			String ret = "0";
			try {
				ret = CustomHttpClient.executeHttpPost(HTTP_POST_URL, postParameters).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (ret.startsWith("-1")) {
				Log.w(TAG, "Registration Failed to be pushed to the cloud.");
				return false;
			} else {
				Log.i(TAG, "Registration pushed to the cloud.");
				return true;
			}

		}
	}

	public static class Users {
		private static final String TAG = Users.class.getSimpleName();

		public static boolean pushRecordToBackEnd(Context context, User record) {
			Log.d(TAG, "pushUser() method invoked!");

			String HTTP_POST_URL = WebServiceUrls.User.PUSH_RECORD;

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("id", record.getId()));
			postParameters.add(new BasicNameValuePair("tradeName", record.getTradeName()));
			postParameters.add(new BasicNameValuePair("firstName", record.getFirstName()));
			postParameters.add(new BasicNameValuePair("lastName", record.getLastName()));
			postParameters.add(new BasicNameValuePair("birthday", String.valueOf(record.getBirthday())));
			postParameters.add(new BasicNameValuePair("add_line1", record.getAddLine1()));
			postParameters.add(new BasicNameValuePair("add_lat", String.valueOf(record.getAdd_lat())));
			postParameters.add(new BasicNameValuePair("add_lng", String.valueOf(record.getAdd_lng())));
			postParameters.add(new BasicNameValuePair("zipCode", record.getZipCode()));
			postParameters.add(new BasicNameValuePair("businessURL", record.getBusinessURL()));
			postParameters.add(new BasicNameValuePair("facebookURL", record.getFacebookURL()));
			postParameters.add(new BasicNameValuePair("gplusURL", record.getGplusURL()));
			postParameters.add(new BasicNameValuePair("twitterURL", record.getTwitterURL()));
			postParameters.add(new BasicNameValuePair("landline", record.getLandline()));
			postParameters.add(new BasicNameValuePair("mobile", record.getMobile()));

			postParameters.add(new BasicNameValuePair("edited_by", record.getEdited_by()));
			postParameters.add(new BasicNameValuePair("created", String.valueOf(record.getCreated())));
			postParameters.add(new BasicNameValuePair("updated", String.valueOf(record.getUpdated())));
			postParameters.add(new BasicNameValuePair("version", String.valueOf(record.getVersion())));

			String ret = "0";
			try {
				ret = CustomHttpClient.executeHttpPost(HTTP_POST_URL, postParameters).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (ret.startsWith("-1")) {
				return false;
			} else {
				return true;
			}

		}

		public static boolean isTradeNameUnique(Context context, String tradeName) {
			Log.d(TAG, "isTradeNameUnique() method invoked!");
			String HTTP_POST_URL = WebServiceUrls.User.IS_TRADENAME_UNIQUE;

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("tradeName", tradeName));

			String ret = "0";
			try {
				ret = CustomHttpClient.executeHttpPost(HTTP_POST_URL, postParameters).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (ret.startsWith("-1")) {
				return false; // Meaning, it's existing
			} else {
				return true; // Meaning, it's unqie
			}
		}

		public static boolean isUserExists(Context context, String id) {
			Log.d(TAG, "isUserExists() method invoked!");

			String HTTP_POST_URL = WebServiceUrls.User.IS_EXISTS;

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("id", id));

			String ret = "0";
			try {
				ret = CustomHttpClient.executeHttpPost(HTTP_POST_URL, postParameters).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (ret.startsWith("-1")) {
				return false;
			} else {
				return true;
			}

		}

		public static User getUser(Context context, User currentUser) {
			Log.d(TAG, "getUser() method invoked!");
			User record = new User();
			String HTTP_POST_URL = WebServiceUrls.User.SELECT_BY_ID + "?id=" + Uri.encode(currentUser.getId());
			try {
				InputStream responseDetail = CustomHttpClient.executeHttpGetForGson(HTTP_POST_URL);
				Gson gsonDetail = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
				JsonReader readerDetail = new JsonReader(new InputStreamReader(responseDetail, "UTF-8"));

				readerDetail.beginArray();
				while (readerDetail.hasNext()) {
					User item = gsonDetail.fromJson(readerDetail, User.class);

					// Perform update to preserve other fields' value e.g.
					// images
					currentUser.setTradeName(item.getTradeName());
					currentUser.setFirstName(item.getFirstName());
					currentUser.setLastName(item.getLastName());
					currentUser.setBirthday(item.getBirthday());
					currentUser.setAddLine1(item.getAddLine1());
					currentUser.setAdd_lat(item.getAdd_lat());
					currentUser.setAdd_lng(item.getAdd_lng());
					currentUser.setZipCode(item.getZipCode());
					currentUser.setBusinessURL(item.getBusinessURL());
					currentUser.setFacebookURL(item.getFacebookURL());
					currentUser.setGplusURL(item.getGplusURL());
					currentUser.setTwitterURL(item.getTwitterURL());
					currentUser.setLandline(item.getLandline());
					currentUser.setMobile(item.getMobile());

					currentUser.setEdited_by(item.getEdited_by());
					currentUser.setCreated(item.getCreated());
					currentUser.setUpdated(item.getUpdated());
					currentUser.setVersion(item.getVersion());

					record = currentUser;
				}
				readerDetail.endArray();
				readerDetail.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

			return record;
		}

	}

	public static class Offers {
		private static final String TAG = Offers.class.getSimpleName();

		public static boolean updateStatus(Context context, Offer record) {
			Log.d(TAG, "updateStatus() method invoked!");

			String HTTP_POST_URL = WebServiceUrls.Offer.UPDATE_STATUS;

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("id", record.getId()));
			postParameters.add(new BasicNameValuePair("status", record.getStatus()));

			postParameters.add(new BasicNameValuePair("edited_by", record.getEdited_by()));
			postParameters.add(new BasicNameValuePair("created", String.valueOf(record.getCreated())));
			postParameters.add(new BasicNameValuePair("updated", String.valueOf(record.getUpdated())));
			postParameters.add(new BasicNameValuePair("version", String.valueOf(record.getVersion())));

			String ret = "0";
			try {
				ret = CustomHttpClient.executeHttpPost(HTTP_POST_URL, postParameters).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (ret.startsWith("-1")) {
				return false;
			} else {
				return true;
			}

		}

		public static boolean pushRecordToBackEnd(Context context, Offer record) {
			//Ensure that all images have been uploaded first
			String ftpDir = ApplicationUtils.FTP_ROOT_IMAGES_URL + record.getId() + "/";
			boolean isFtpUploadFailed = false;
			try {
				FtpHelper.deleteDirectory(context, ftpDir);
			} catch (IllegalStateException e2) {
				e2.printStackTrace();
				isFtpUploadFailed = true;
			} catch (IOException e2) {
				e2.printStackTrace();
				isFtpUploadFailed = true;
			} catch (FTPIllegalReplyException e2) {
				e2.printStackTrace();
				isFtpUploadFailed = true;
			} catch (FTPException e2) {
				e2.printStackTrace();
				isFtpUploadFailed = true;
			} catch (FTPDataTransferException e2) {
				e2.printStackTrace();
				isFtpUploadFailed = true;
			} catch (FTPAbortedException e2) {
				e2.printStackTrace();
				isFtpUploadFailed = true;
			}

			//return false if failed
			Log.d(TAG, "isFtpUploadFailed " + isFtpUploadFailed);
			if (isFtpUploadFailed) {
				return false;
			}

			List<File> files = new ArrayList<File>();
			List<com.dabeshackers.infor.gather.entities.Media> images = record.getImagesList();

			for (com.dabeshackers.infor.gather.entities.Media media : images) {
				File f = new File(media.getLocalFilePath());
				files.add(f);
			}

			Log.d(TAG, "uploading images...");
			try {
				FtpHelper.uploadFiles(context, files, ftpDir);
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
				isFtpUploadFailed = true;
			} catch (IOException e1) {
				e1.printStackTrace();
				isFtpUploadFailed = true;
			} catch (FTPIllegalReplyException e1) {
				e1.printStackTrace();
				isFtpUploadFailed = true;
			} catch (FTPException e1) {
				e1.printStackTrace();
				isFtpUploadFailed = true;
			} catch (FTPDataTransferException e1) {
				e1.printStackTrace();
				isFtpUploadFailed = true;
			} catch (FTPAbortedException e1) {
				e1.printStackTrace();
				isFtpUploadFailed = true;
			}

			//return false if failed
			Log.d(TAG, "isFtpUploadFailed " + isFtpUploadFailed);
			if (isFtpUploadFailed) {
				return false;
			}

			//Proceed to saving
			Log.d(TAG, "pushOffer() method invoked!");

			String HTTP_POST_URL = WebServiceUrls.Offer.PUSH_RECORD;

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("id", record.getId()));
			postParameters.add(new BasicNameValuePair("title", record.getTitle()));
			postParameters.add(new BasicNameValuePair("merchant", record.getMerchant()));
			postParameters.add(new BasicNameValuePair("description", record.getDescription()));
			postParameters.add(new BasicNameValuePair("price", String.valueOf(record.getPrice_discounted())));
			postParameters.add(new BasicNameValuePair("loc_text", record.getLoc_text()));
			postParameters.add(new BasicNameValuePair("loc_lat", String.valueOf(record.getLoc_lat())));
			postParameters.add(new BasicNameValuePair("loc_lng", String.valueOf(record.getLoc_lng())));
			postParameters.add(new BasicNameValuePair("biz_url", record.getBiz_url()));
			postParameters.add(new BasicNameValuePair("status", record.getStatus()));
			postParameters.add(new BasicNameValuePair("youtube_url", record.getYoutube_url()));
			postParameters.add(new BasicNameValuePair("fb_url", record.getFacebook_url()));
			postParameters.add(new BasicNameValuePair("gplus_url", record.getGplus_url()));
			postParameters.add(new BasicNameValuePair("twitter_url", record.getTwitter_url()));
			postParameters.add(new BasicNameValuePair("landline", record.getLandline()));
			postParameters.add(new BasicNameValuePair("mobile", record.getMobile()));

			postParameters.add(new BasicNameValuePair("edited_by", record.getEdited_by()));
			postParameters.add(new BasicNameValuePair("created", String.valueOf(record.getCreated())));
			postParameters.add(new BasicNameValuePair("updated", String.valueOf(record.getUpdated())));
			postParameters.add(new BasicNameValuePair("version", String.valueOf(record.getVersion())));

			String ret = "0";
			try {
				ret = CustomHttpClient.executeHttpPost(HTTP_POST_URL, postParameters).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (ret.startsWith("-1")) {
				return false;
			} else {

				Media.clearRecordsByOfferId(context, record.getId());
				if (images != null) {
					for (com.dabeshackers.infor.gather.entities.Media media : images) {
						media.setOwner_id(record.getId());
						Media.pushRecordToBackEnd(context, media);
					}
				}
				List<String> tags = record.getTagsList();
				Tags.clearRecordsByOfferId(context, record.getId());
				if (tags != null) {
					for (String s : tags) {
						Tags.pushRecordToBackEnd(context, record.getId(), s);
					}
				}

				return true;
			}

		}

		public static List<Offer> fetchRecordsWithinRange(Context context, LatLng center, int dbCurrentRow) {

			Log.d(TAG, "fetchRecordsWithinRange() method invoked!");
			double radius = 5;
			List<Offer> items = new ArrayList<Offer>();

			// guard
			if (center == null) {
				return items;
			}

			String HTTP_POST_URL = WebServiceUrls.Offer.SELECT_WITHIN_RANGE + "?lat=" + Uri.encode(String.valueOf(center.latitude)) + "&lon=" + Uri.encode(String.valueOf(center.longitude), "UTF-8") + "&rad=" + Uri.encode(String.valueOf(radius)) + "&dbCurrentRow=" + dbCurrentRow;
			try {
				InputStream responseDetail = CustomHttpClient.executeHttpGetForGson(HTTP_POST_URL);
				Gson gsonDetail = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
				JsonReader readerDetail = new JsonReader(new InputStreamReader(responseDetail, "UTF-8"));

				readerDetail.beginArray();
				while (readerDetail.hasNext()) {
					Offer item = gsonDetail.fromJson(readerDetail, Offer.class);
					item.setImagesList(Media.fetchRecordsByOfferId(context, item.getId()));
					item.setTagsList(Tags.fetchRecordsByOfferId(context, item.getId()));
					items.add(item);
				}
				readerDetail.endArray();
				readerDetail.close();

			} catch (IOException e) {
				//Ignore 
			} catch (Exception e) {
				e.printStackTrace();
			}

			return items;

		}

		public static List<Offer> fetchRecordsByUserId(Context context, String user_id, boolean getDraftsOnly, int dbCurrentRow) {
			Log.d(TAG, "fetchRecordsWithinRange() method invoked!");
			List<Offer> items = new ArrayList<Offer>();

			String HTTP_POST_URL;
			if (getDraftsOnly) {
				HTTP_POST_URL = WebServiceUrls.Offer.SELECT_BY_USER_ID_DRAFTS + "?id=" + Uri.encode(String.valueOf(user_id)) + "&dbCurrentRow=" + dbCurrentRow;
			} else {
				HTTP_POST_URL = WebServiceUrls.Offer.SELECT_BY_USER_ID + "?id=" + Uri.encode(String.valueOf(user_id)) + "&dbCurrentRow=" + dbCurrentRow;
			}

			try {
				InputStream responseDetail = CustomHttpClient.executeHttpGetForGson(HTTP_POST_URL);
				Gson gsonDetail = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
				JsonReader readerDetail = new JsonReader(new InputStreamReader(responseDetail, "UTF-8"));

				readerDetail.beginArray();
				while (readerDetail.hasNext()) {
					Offer item = gsonDetail.fromJson(readerDetail, Offer.class);
					item.setImagesList(Media.fetchRecordsByOfferId(context, item.getId()));
					item.setTagsList(Tags.fetchRecordsByOfferId(context, item.getId()));
					items.add(item);
				}
				readerDetail.endArray();
				readerDetail.close();

			} catch (IOException e) {
				//Ignore 
			} catch (Exception e) {
				e.printStackTrace();
			}

			return items;
		}

	}

	public static class Media {
		private static final String TAG = Media.class.getSimpleName();

		public static boolean pushRecordToBackEnd(Context context, com.dabeshackers.infor.gather.entities.Media record) {
			Log.d(TAG, "pushRecordToBackEnd() method invoked!");

			String HTTP_POST_URL = WebServiceUrls.Media.PUSH_RECORD;

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("id", record.getId()));
			postParameters.add(new BasicNameValuePair("owner_id", record.getOwner_id()));
			postParameters.add(new BasicNameValuePair("type", record.getType()));
			postParameters.add(new BasicNameValuePair("name", record.getName()));
			postParameters.add(new BasicNameValuePair("status", String.valueOf(record.getStatus())));

			postParameters.add(new BasicNameValuePair("edited_by", record.getEdited_by()));
			postParameters.add(new BasicNameValuePair("created", String.valueOf(record.getCreated())));
			postParameters.add(new BasicNameValuePair("updated", String.valueOf(record.getUpdated())));
			postParameters.add(new BasicNameValuePair("version", String.valueOf(record.getVersion())));

			String ret = "0";
			try {
				ret = CustomHttpClient.executeHttpPost(HTTP_POST_URL, postParameters).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (ret.startsWith("-1")) {
				return false;
			} else {
				return true;
			}

		}

		public static List<com.dabeshackers.infor.gather.entities.Media> fetchRecordsByOfferId(Context context, String offer_id) {

			Log.d(TAG, "fetchRecordsByOfferId() method invoked!");
			List<com.dabeshackers.infor.gather.entities.Media> items = new ArrayList<com.dabeshackers.infor.gather.entities.Media>();

			String HTTP_POST_URL = WebServiceUrls.Media.SELECT_BY_OFFER_ID + "?id=" + Uri.encode(String.valueOf(offer_id));
			try {
				InputStream responseDetail = CustomHttpClient.executeHttpGetForGson(HTTP_POST_URL);
				Gson gsonDetail = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
				JsonReader readerDetail = new JsonReader(new InputStreamReader(responseDetail, "UTF-8"));

				readerDetail.beginArray();
				while (readerDetail.hasNext()) {
					com.dabeshackers.infor.gather.entities.Media item = gsonDetail.fromJson(readerDetail, com.dabeshackers.infor.gather.entities.Media.class);
					item.setContext(context);
					items.add(item);
					// Download
					File f = new File(ApplicationUtils.Paths.getLocalAppImagesFolder(context) + item.getOwner_id() + "/", item.getName());
					if (!f.getParentFile().exists()) {
						f.getParentFile().mkdirs();
					}

					if (!f.exists()) {
						try {
							FileUtils.copyURLToFile(new URL(WebServiceUrls.Media.IMAGES_URL + item.getOwner_id() + "/" + item.getName()), f);
						} catch (IOException e) {
							BitmapHelper.createDefaultImage(context.getResources(), "no_image", f.getAbsolutePath());
						}

					}

				}
				readerDetail.endArray();
				readerDetail.close();

			} catch (IOException e) {
				//Ignore 
			} catch (Exception e) {
				e.printStackTrace();
			}

			return items;

		}

		public static boolean clearRecordsByOfferId(Context context, String owner_id) {
			Log.d(TAG, "clearRecordsByOfferId() method invoked!");

			String HTTP_POST_URL = WebServiceUrls.Media.CLEAR_BY_OFFER_ID;

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("owner_id", owner_id));

			String ret = "0";
			try {
				ret = CustomHttpClient.executeHttpPost(HTTP_POST_URL, postParameters).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (ret.startsWith("-1")) {
				return false;
			} else {
				return true;
			}

		}
	}

	public static class Tags {
		private static final String TAG = Tags.class.getSimpleName();

		public static boolean pushRecordToBackEnd(Context context, String owner_id, String name) {
			Log.d(TAG, "pushRecordToBackEnd() method invoked!");

			String HTTP_POST_URL = WebServiceUrls.Tags.PUSH_RECORD;

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("owner_id", owner_id));
			postParameters.add(new BasicNameValuePair("name", name.toLowerCase()));

			String ret = "0";
			try {
				ret = CustomHttpClient.executeHttpPost(HTTP_POST_URL, postParameters).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (ret.startsWith("-1")) {
				return false;
			} else {
				return true;
			}

		}

		public static boolean clearRecordsByOfferId(Context context, String owner_id) {
			Log.d(TAG, "clearRecordsByOfferId() method invoked!");

			String HTTP_POST_URL = WebServiceUrls.Tags.CLEAR_BY_OFFER_ID;

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("owner_id", owner_id));

			String ret = "0";
			try {
				ret = CustomHttpClient.executeHttpPost(HTTP_POST_URL, postParameters).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (ret.startsWith("-1")) {
				return false;
			} else {
				return true;
			}

		}

		public static List<String> fetchRecordsByOfferId(Context context, String offer_id) {

			Log.d(TAG, "fetchRecordsByOfferId() method invoked!");
			List<String> items = new ArrayList<String>();

			String HTTP_POST_URL = WebServiceUrls.Tags.SELECT_BY_OFFER_ID + "?id=" + Uri.encode(String.valueOf(offer_id));
			try {
				InputStream responseDetail = CustomHttpClient.executeHttpGetForGson(HTTP_POST_URL);
				Gson gsonDetail = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
				JsonReader readerDetail = new JsonReader(new InputStreamReader(responseDetail, "UTF-8"));

				readerDetail.beginArray();
				while (readerDetail.hasNext()) {

					Tag item = gsonDetail.fromJson(readerDetail, Tag.class);
					items.add(item.name);
				}
				readerDetail.endArray();
				readerDetail.close();

			} catch (IOException e) {
				//Ignore 
			} catch (Exception e) {
				e.printStackTrace();
			}

			return items;

		}

		public static List<String> fetchDistinctRecords(Context context) {

			Log.d(TAG, "fetchRecordsByOfferId() method invoked!");
			List<String> items = new ArrayList<String>();

			String HTTP_POST_URL = WebServiceUrls.Tags.SELECT_DISTINCT;
			try {
				InputStream responseDetail = CustomHttpClient.executeHttpGetForGson(HTTP_POST_URL);
				Gson gsonDetail = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
				JsonReader readerDetail = new JsonReader(new InputStreamReader(responseDetail, "UTF-8"));

				readerDetail.beginArray();
				while (readerDetail.hasNext()) {

					Tag item = gsonDetail.fromJson(readerDetail, Tag.class);
					items.add(item.name);
				}
				readerDetail.endArray();
				readerDetail.close();

			} catch (IOException e) {
				//Ignore 
			} catch (Exception e) {
				e.printStackTrace();
			}

			return items;

		}

		public class Tag {
			String name;
		}
	}

	public class MyFTPDataTransferListener implements FTPDataTransferListener {

		private final int BACK_OFF_LIMIT = 5;
		private int backOffCtr;

		private Context context;
		private File file;
		private String name;

		public MyFTPDataTransferListener(Context context, File file, String name) {
			this.context = context;
			this.file = file;
			this.name = name;

			backOffCtr = 0;
		}

		@Override
		public void transferred(int arg0) {
		}

		@Override
		public void started() {
		}

		@Override
		public void failed() {
			if (backOffCtr <= BACK_OFF_LIMIT) {
				backOffCtr++;
				try {
					FtpHelper.uploadFile(context, file, name, this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		@Override
		public void completed() {
		}

		@Override
		public void aborted() {
		}
	}

}
