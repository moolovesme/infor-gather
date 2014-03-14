package com.dabeshackers.infor.gather.helpers;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.dabeshackers.infor.gather.data.DBAdapter;
import com.dabeshackers.infor.gather.entities.User;
import com.dabeshackers.infor.gather.http.CustomHttpClient;
import com.dabeshackers.infor.gather.http.WebServiceUrls;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class LoginHelper {
	@SuppressWarnings("unused")
	private static final String TAG = LoginHelper.class.getSimpleName();

	public static boolean fetchRecordByUserName(String username, DBAdapter db) throws InterruptedException, UnsupportedEncodingException {

		String HTTP_POST_URL = WebServiceUrls.User.SELECT_BY_USERNAME + "?username=" + username;

		String ret = "0";
		try {
			InputStream responseDetail = CustomHttpClient.executeHttpGetForGson(HTTP_POST_URL);
			Gson gsonDetail = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			JsonReader readerDetail = new JsonReader(new InputStreamReader(responseDetail, "UTF-8"));

			readerDetail.beginArray();
			while (readerDetail.hasNext()) {
				User user = gsonDetail.fromJson(readerDetail, User.class);
				//Perform persistence of user object here
				db.open();

				DBAdapter.UserEntity.createRecord(user);
				db.close();
				//End persistence
			}
			readerDetail.endArray();
			readerDetail.close();
			ret = "1";

		} catch (Exception e) {
			ret = "-1";
			e.printStackTrace();
		}

		if (ret.startsWith("-1")) {
			return false;
		} else {
			return true;
		}
	}

}
