package com.dabeshackers.infor.gather.http;

public class WebServiceUrls {

	public static class User {
		public static final String PUSH_RECORD = "http://codesndbx.com/android/lokal/user_insert_update.php";
		public static final String IS_EXISTS = "http://codesndbx.com/android/lokal/user_isexists.php";
		public static final String IS_TRADENAME_UNIQUE = "http://codesndbx.com/android/lokal/user_istradename_unique.php";
		public static final String SELECT_BY_ID = "http://codesndbx.com/android/lokal/user_select_by_id.php";

		public static final String SELECT_BY_USERNAME = "http://codesndbx.com/android/chronicler/user_select_by_username.php";
	}

	public static class Offer {
		public static final String PUSH_RECORD = "http://codesndbx.com/android/lokal/offer_insert_update.php";
		public static final String UPDATE_STATUS = "http://codesndbx.com/android/lokal/offer_update_status.php";
		public static final String SELECT_WITHIN_RANGE = "http://codesndbx.com/android/lokal/offer_select_within_range.php";
		public static final String SELECT_BY_USER_ID = "http://codesndbx.com/android/lokal/offer_select_by_user_id.php";
		public static final String SELECT_BY_USER_ID_DRAFTS = "http://codesndbx.com/android/lokal/offer_select_by_user_id_drafts.php";
	}

	public static class Media {
		public static final String PUSH_RECORD = "http://codesndbx.com/android/lokal/media_insert_update.php";
		public static final String SELECT_BY_OFFER_ID = "http://codesndbx.com/android/lokal/media_select_by_offer_id.php";
		public static final String IMAGES_URL = "http://codesndbx.com/android/lokal/images/";
		public static final String CLEAR_BY_OFFER_ID = "http://codesndbx.com/android/lokal/media_clear_by_offer_id.php";
	}

	public static class Tags {
		public static final String PUSH_RECORD = "http://codesndbx.com/android/lokal/tags_insert_update.php";
		public static final String SELECT_BY_OFFER_ID = "http://codesndbx.com/android/lokal/tags_select_by_offer_id.php";
		public static final String SELECT_DISTINCT = "http://codesndbx.com/android/lokal/tags_select_distinct.php";
		public static final String CLEAR_BY_OFFER_ID = "http://codesndbx.com/android/lokal/tags_clear_by_offer_id.php";
	}

	public static class GCM {
		public static final String REGISTRAION = "http://codesndbx.com/android/chronicler/gcm_insert_update.php";
	}

	public static class Mailer {
		public static final String SEND_MAIL = "http://codesndbx.com/android/chronicler/mailer.php";
	}

}
