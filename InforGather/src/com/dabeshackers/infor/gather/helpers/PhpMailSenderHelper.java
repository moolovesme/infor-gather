package com.dabeshackers.infor.gather.helpers;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.dabeshackers.infor.gather.http.CustomHttpClient;
import com.dabeshackers.infor.gather.http.WebServiceUrls;

public class PhpMailSenderHelper {
	@SuppressWarnings("unused")
	private static final String TAG = "PhpMailSenderHelper";

	public static boolean sendEmail(String to, String subject, String message, String hasAttachments, String attachmentName) throws InterruptedException, UnsupportedEncodingException {

		String HTTP_POST_URL = WebServiceUrls.Mailer.SEND_MAIL;
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

		postParameters.add(new BasicNameValuePair("hasattachments", hasAttachments));
		postParameters.add(new BasicNameValuePair("attname", attachmentName));
		postParameters.add(new BasicNameValuePair("to", to));
		postParameters.add(new BasicNameValuePair("subject", subject));
		postParameters.add(new BasicNameValuePair("message", message));

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
