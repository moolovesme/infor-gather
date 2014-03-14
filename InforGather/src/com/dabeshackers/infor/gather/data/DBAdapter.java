package com.dabeshackers.infor.gather.data;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.dabeshackers.infor.gather.application.AppMain;
import com.dabeshackers.infor.gather.entities.Chronicle;
import com.dabeshackers.infor.gather.entities.Media;
import com.dabeshackers.infor.gather.entities.User;
import com.dabeshackers.infor.gather.helpers.GUIDHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

	private static final String TAG = "DbAdapter";

	private DatabaseHelper mDbHelper;
	private static SQLiteDatabase mDb;

	/**
	 * Database creation sql statement
	 */

	private static final String CHRONICLE_TABLE_CREATE = "create table chronicle (" + " id string PRIMARY KEY " +

	", body string null " + ", loc_text string null " + ", loc_lng int null " + ", loc_lat int null " + ", attachment string null " +

	", ispushed int null " + ", edited_by string null " + ", created long null " + ", updated long null " + ", version int null " + ");";

	private static final String MEDIA_TABLE_CREATE = "create table media (" + " id string PRIMARY KEY " +

	", chronicle_id string null " + ", content blob null " + ", type string null " + ", name int null " +

	", ispushed int null " + ", edited_by string null " + ", created long null " + ", updated long null " + ", version int null " + ");";

	private static final String USER_TABLE_CREATE = "create table user (" + "	id string PRIMARY KEY " + "	,email text null " + "	,password text null " + "	,firstName text null " + "	,middleName text null " + "	,lastName text null " + "	,birthday int null " + "	,type int null " + "	,image text null " + "	,gender int null " + "	,civilstatus text null " + "	,landline text null " + "	,mobile text null " +

	"	,ispushed int null " + "	,edited_by text null " + "	,created int null " + "	,updated int null " + "	,version int null " + ");";

	private static final String DB_NAME = "app_db.sqlite";
	private static final int DB_VERSION = 1;

	private final Context context;
	private static Context CONTEXT;
	private static AppMain applicationObj;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {

			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(USER_TABLE_CREATE);
			db.execSQL(CHRONICLE_TABLE_CREATE);
			db.execSQL(MEDIA_TABLE_CREATE);

			insertInitialValues(db);
		}

		private void insertInitialValues(SQLiteDatabase db) {
			// Insert initial values
			List<String> sql = DBInitValues.getInsertSql();
			for (String string : sql) {
				db.execSQL(string);
			}

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			//			db.execSQL("DROP TABLE IF EXISTS owners");
			//			db.execSQL("DROP TABLE IF EXISTS useridentity");
			//			db.execSQL("DROP TABLE IF EXISTS parent");
			//			db.execSQL("DROP TABLE IF EXISTS document");
			//			db.execSQL("DROP TABLE IF EXISTS student");
			onCreate(db);
		}

		public boolean isOpen() {
			SQLiteDatabase db = getReadableDatabase();
			return db.isOpen();
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public DBAdapter(Context ctx, AppMain application) {
		this.context = ctx;
		CONTEXT = ctx;
		applicationObj = application;
	}

	/**
	 * Open the database. If it cannot be opened, try to create a new instance
	 * of the database. If it cannot be created, throw an exception to signal
	 * the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public DBAdapter open() {
		// Log.i("DBAdapter", "Database opened!");
		try {
			mDbHelper = new DatabaseHelper(context);
			if (mDbHelper.isOpen()) {
				mDbHelper.close();
			}

			mDb = mDbHelper.getWritableDatabase();

		} catch (Exception e) {
			// Log.e("DBAdapter",
			// "Error encountered when opening the database");
			e.printStackTrace();

			close();
			mDbHelper = new DatabaseHelper(context);
			mDb = mDbHelper.getWritableDatabase();
		}

		return this;
	}

	public void close() {
		try {
			if (mDbHelper != null && mDbHelper.isOpen()) {
				// Log.i("DBAdapter", "Database closed!");
				mDbHelper.close();
			} else {
				Log.i("DBAdapter", "mDbHelper is open!");
			}
		} catch (Exception e) {
			// Log.e("DBAdapter",
			// "Error encountered when closing the database");
			e.printStackTrace();
		}

	}

	public void clearEntityTables() throws Exception {
		chronicles.clearTable();
	}

	public static ChronicleEntity chronicles = new ChronicleEntity();

	public static class ChronicleEntity {
		private static final String TABLE_NAME = "chronicle";
		private static final int OPERATION_INSERT = 0;
		private static final int OPERATION_UPDATE = 1;

		public String createRecord(Chronicle obj) throws Exception {
			ContentValues initialValues = new ContentValues();

			String id;
			long now;
			if (obj.getId() == null) {
				id = GUIDHelper.createGUID();
				now = new Date().getTime();
			} else {
				id = obj.getId();
				now = obj.getCreated();
			}

			initialValues.put("id", id);

			processContentValues(initialValues, obj, OPERATION_INSERT);

			initialValues.put("ispushed", 0);

			initialValues.put("edited_by", obj.getEdited_by());
			initialValues.put("created", now);
			initialValues.put("updated", now);
			initialValues.put("version", 1);

			mDb.insert(TABLE_NAME, null, initialValues);
			return id;
		}

		public String updateRecord(Chronicle obj) throws Exception {
			ContentValues initialValues = new ContentValues();

			String id;
			if (obj.getId().length() == 0) {
				id = GUIDHelper.createGUID();
			} else {
				id = obj.getId();
			}

			long now = new Date().getTime();
			initialValues.put("id", id);

			processContentValues(initialValues, obj, OPERATION_UPDATE);

			initialValues.put("ispushed", 0);

			initialValues.put("edited_by", applicationObj.getCurrentUser().getId());
			initialValues.put("created", obj.getCreated());
			initialValues.put("updated", now);
			initialValues.put("version", obj.getVersion());

			mDb.update(TABLE_NAME, initialValues, "id='" + id + "'", null);

			return id;
		}

		private void processContentValues(ContentValues initialValues, Chronicle obj, int operation) {

			initialValues.put("body", obj.getBody());
			initialValues.put("loc_text", obj.getLoc_text());
			initialValues.put("loc_lng", obj.getLoc_lng());
			initialValues.put("loc_lat", obj.getLoc_lat());
			initialValues.put("attachment", obj.getAttachment());

		}

		public boolean updateRecordAsPushed(int ispushed, String id) throws Exception {
			mDb.execSQL("UPDATE " + TABLE_NAME + " SET " + "ispushed = " + ispushed + "" + " WHERE id = '" + id + "'");
			return true;
		}

		public void clearTable() throws Exception {
			mDb.execSQL("DELETE FROM " + TABLE_NAME);
		}

		public List<Chronicle> fetchRecords() throws Exception {
			List<Chronicle> recordsList = new ArrayList<Chronicle>();
			Chronicle record = new Chronicle();
			Cursor mCursor = mDb.rawQuery("SELECT *  FROM " + TABLE_NAME + " ORDER BY created", null);
			mCursor.moveToFirst();
			while (mCursor.isAfterLast() == false) {
				record = processCursorForFetch(mCursor);
				recordsList.add(record);
				mCursor.moveToNext();
			}
			mCursor.close();
			return recordsList;
		}

		public List<Chronicle> fetchRecordsWithSearchParam(String searchParam) throws Exception {
			List<Chronicle> applicationList = new ArrayList<Chronicle>();
			Chronicle application = new Chronicle();
			Cursor mCursor = mDb.rawQuery("SELECT *  FROM " + TABLE_NAME + " WHERE (firstName LIKE '%" + searchParam + "%' OR lastName LIKE '%" + searchParam + "%') ORDER BY created DESC ", null);
			mCursor.moveToFirst();
			while (mCursor.isAfterLast() == false) {
				application = processCursorForFetch(mCursor);

				applicationList.add(application);
				mCursor.moveToNext();
			}
			mCursor.close();
			return applicationList;
		}

		public Chronicle fetchRecordById(String Id) throws Exception {

			Chronicle applicaion = new Chronicle();
			Cursor mCursor = mDb.rawQuery("SELECT *  FROM " + TABLE_NAME + " WHERE id = '" + Id + "'", null);
			mCursor.moveToFirst();
			while (mCursor.isAfterLast() == false) {
				applicaion = processCursorForFetch(mCursor);

				mCursor.moveToNext();
			}
			mCursor.close();
			return applicaion;
		}

		private Chronicle processCursorForFetch(Cursor mCursor) {
			Chronicle obj = new Chronicle();
			obj.setId(mCursor.getString(mCursor.getColumnIndex("id")));

			obj.setBody(mCursor.getString(mCursor.getColumnIndex("body")));
			obj.setLoc_text(mCursor.getString(mCursor.getColumnIndex("loc_text")));
			obj.setLoc_lng(mCursor.getInt(mCursor.getColumnIndex("loc_lng")));
			obj.setLoc_lat(mCursor.getInt(mCursor.getColumnIndex("loc_lat")));
			obj.setAttachment(mCursor.getString(mCursor.getColumnIndex("attachment")));

			obj.setEdited_by(mCursor.getString(mCursor.getColumnIndex("edited_by")));
			obj.setCreated(mCursor.getLong(mCursor.getColumnIndex("created")));
			obj.setUpdated(mCursor.getLong(mCursor.getColumnIndex("updated")));
			obj.setVersion(mCursor.getInt(mCursor.getColumnIndex("version")));

			//Retrieve attached media
			try {
				obj.setMediaList(media.fetchRecordsByChronicleId(obj.getId()));
			} catch (Exception e) {
				e.printStackTrace();
			}

			return obj;
		}

		public List<Chronicle> fetchRecordsUnsynced() throws Exception {
			List<Chronicle> applicationList = new ArrayList<Chronicle>();
			try {
				Chronicle application = new Chronicle();
				Cursor mCursor = mDb.rawQuery("SELECT *  FROM " + TABLE_NAME + " where  ispushed != 1", null);
				mCursor.moveToFirst();
				while (mCursor.isAfterLast() == false) {
					application = processCursorForFetch(mCursor);

					applicationList.add(application);
					mCursor.moveToNext();
				}
				mCursor.close();
			} catch (Exception e) {
			}

			return applicationList;
		}

		public List<String> fetchAllIds() throws Exception {
			List<String> ids = new ArrayList<String>();
			// initialize ids variable
			ids.add("'{00000000-0000-0000-0000-000000000000}'");
			Cursor mCursor = mDb.rawQuery("SELECT id FROM " + TABLE_NAME + " ", null);
			mCursor.moveToFirst();
			while (mCursor.isAfterLast() == false) {
				ids.add("'" + String.valueOf(mCursor.getString(mCursor.getColumnIndex("id"))) + "'");
				mCursor.moveToNext();
			}
			mCursor.close();
			return ids;
		}

		public boolean deleteRecord(String id) throws Exception {

			try {
				mDb.execSQL("DELETE FROM " + TABLE_NAME + " WHERE id = '" + id + "'");
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}

		public boolean VerifyUserByName(String userName) throws Exception {

			Cursor mCursor = mDb.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE LOWER(username) = '" + userName.toLowerCase(Locale.getDefault()) + "'", null);
			mCursor.moveToFirst();
			while (mCursor.isAfterLast() == false) {
				return true;
			}
			mCursor.close();

			return false;
		}

	}

	public static MediaEntity media = new MediaEntity();

	public static class MediaEntity {
		private static final String TABLE_NAME = "media";
		private static final int OPERATION_INSERT = 0;
		private static final int OPERATION_UPDATE = 1;

		public String createRecord(Media obj) throws Exception {
			ContentValues initialValues = new ContentValues();

			String id;
			long now;
			if (obj.getId() == null) {
				id = GUIDHelper.createGUID();
				now = new Date().getTime();
			} else {
				id = obj.getId();
				now = obj.getCreated();
			}

			initialValues.put("id", id);

			processContentValues(initialValues, obj, OPERATION_INSERT);

			initialValues.put("ispushed", 0);

			initialValues.put("edited_by", obj.getEdited_by());
			initialValues.put("created", now);
			initialValues.put("updated", now);
			initialValues.put("version", 1);

			mDb.insert(TABLE_NAME, null, initialValues);

			return id;
		}

		public String updateRecord(Media obj) throws Exception {
			ContentValues initialValues = new ContentValues();

			String id;
			if (obj.getId().length() == 0) {
				id = GUIDHelper.createGUID();
			} else {
				id = obj.getId();
			}

			long now = new Date().getTime();
			initialValues.put("id", id);

			processContentValues(initialValues, obj, OPERATION_UPDATE);

			initialValues.put("ispushed", 0);

			initialValues.put("edited_by", applicationObj.getCurrentUser().getId());
			initialValues.put("created", obj.getCreated());
			initialValues.put("updated", now);
			initialValues.put("version", obj.getVersion());

			mDb.update(TABLE_NAME, initialValues, "id='" + id + "'", null);

			return id;
		}

		private void processContentValues(ContentValues initialValues, Media obj, int operation) {

			initialValues.put("chronicle_id", obj.getOwner_id());
			initialValues.put("content", obj.getContent());
			initialValues.put("type", obj.getType());
			initialValues.put("name", obj.getName());

		}

		public void clearTable() throws Exception {
			mDb.execSQL("DELETE FROM " + TABLE_NAME);
		}

		public List<Media> fetchRecords() throws Exception {
			List<Media> recordsList = new ArrayList<Media>();
			Media record = new Media(CONTEXT);
			Cursor mCursor = mDb.rawQuery("SELECT *  FROM " + TABLE_NAME + " ORDER BY created", null);
			mCursor.moveToFirst();
			while (mCursor.isAfterLast() == false) {
				record = processCursorForFetch(mCursor);
				recordsList.add(record);
				mCursor.moveToNext();
			}
			mCursor.close();
			return recordsList;
		}

		public List<Media> fetchRecordsByChronicleId(String chronicle_id) throws Exception {
			List<Media> recordsList = new ArrayList<Media>();
			Media record = new Media(CONTEXT);
			Cursor mCursor = mDb.rawQuery("SELECT *  FROM " + TABLE_NAME + " WHERE chronicle_id = '" + chronicle_id + "' ORDER BY created", null);
			mCursor.moveToFirst();
			while (mCursor.isAfterLast() == false) {
				record = processCursorForFetch(mCursor);
				recordsList.add(record);
				mCursor.moveToNext();
			}
			mCursor.close();
			return recordsList;
		}

		public Media fetchRecordById(String Id) throws Exception {

			Media record = new Media(CONTEXT);
			Cursor mCursor = mDb.rawQuery("SELECT *  FROM " + TABLE_NAME + " WHERE id = '" + Id + "'", null);
			mCursor.moveToFirst();
			while (mCursor.isAfterLast() == false) {
				record = processCursorForFetch(mCursor);

				mCursor.moveToNext();
			}
			mCursor.close();
			return record;
		}

		private Media processCursorForFetch(Cursor mCursor) {

			Media obj = new Media(CONTEXT);
			obj.setId(mCursor.getString(mCursor.getColumnIndex("id")));

			obj.setOwner_id(mCursor.getString(mCursor.getColumnIndex("chronicle_id")));
			obj.setContent(mCursor.getBlob(mCursor.getColumnIndex("content")));
			obj.setType(mCursor.getString(mCursor.getColumnIndex("type")));
			obj.setName(mCursor.getString(mCursor.getColumnIndex("name")));

			obj.setEdited_by(mCursor.getString(mCursor.getColumnIndex("edited_by")));
			obj.setCreated(mCursor.getLong(mCursor.getColumnIndex("created")));
			obj.setUpdated(mCursor.getLong(mCursor.getColumnIndex("updated")));
			obj.setVersion(mCursor.getInt(mCursor.getColumnIndex("version")));

			return obj;
		}

		public boolean deleteRecord(String id) throws Exception {

			try {
				mDb.execSQL("DELETE FROM " + TABLE_NAME + " WHERE id = '" + id + "'");
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}

	}

	public static UserEntity users = new UserEntity();

	public static class UserEntity {
		private static final String TABLE_NAME = "user";
		private static final int OPERATION_INSERT = 0;
		private static final int OPERATION_UPDATE = 1;

		public static String createRecord(User user) throws Exception {
			ContentValues initialValues = new ContentValues();

			String id;
			if (user.getId() == null) {
				id = GUIDHelper.createGUID();
			} else {
				id = user.getId();
			}

			long now = new Date().getTime();
			initialValues.put("id", id);

			processContentValues(initialValues, user, OPERATION_INSERT);

			initialValues.put("ispushed", 0);

			initialValues.put("edited_by", user.getEdited_by());
			initialValues.put("created", now);
			initialValues.put("updated", now);
			initialValues.put("version", 1);

			mDb.insert(TABLE_NAME, null, initialValues);

			return id;
		}

		public String updateRecord(User user) throws Exception {
			ContentValues initialValues = new ContentValues();

			String id;
			if (user.getId().length() == 0) {
				id = GUIDHelper.createGUID();
			} else {
				id = user.getId();
			}

			long now = new Date().getTime();
			initialValues.put("id", id);

			processContentValues(initialValues, user, OPERATION_UPDATE);

			initialValues.put("ispushed", 0);

			initialValues.put("edited_by", applicationObj.getCurrentUser().getId());
			initialValues.put("created", user.getCreated());
			initialValues.put("updated", now);
			initialValues.put("version", user.getVersion());

			mDb.update(TABLE_NAME, initialValues, "id='" + id + "'", null);

			return id;
		}

		private static void processContentValues(ContentValues initialValues, User user, int operation) {
			initialValues.put("user_id", user.getUser_id());
			initialValues.put("email", user.getEmail());

			switch (operation) {
			case OPERATION_INSERT:
				if (user.getPassword_hash().equals("")) {
					initialValues.put("password", user.hashPassword());
				} else {
					initialValues.put("password", user.getPassword_hash());
				}

				break;

			case OPERATION_UPDATE:
				if (user.isPassword_changed()) { //Password changed
					initialValues.put("password", user.hashPassword());
				}
				break;

			default:
				break;
			}

			initialValues.put("firstName", user.getFirstName());
			initialValues.put("middleName", user.getMiddleName());
			initialValues.put("lastName", user.getLastName());
			initialValues.put("birthday", user.getBirthday());
			initialValues.put("type", user.getType());
			initialValues.put("image", user.getImage());
			initialValues.put("termination_image", user.getTermination_image());
			initialValues.put("add_region", user.getAddRegion());
			initialValues.put("add_city", user.getAddCity());
			initialValues.put("add_brgy", user.getAddBrgy());
			initialValues.put("add_line1", user.getAddLine1());
			initialValues.put("add_line2", user.getAddLine2());
			initialValues.put("zipCode", user.getZipCode());
			initialValues.put("sss", user.getSss());
			initialValues.put("pagibig", user.getPagibig());
			initialValues.put("philhealth", user.getPhilhealth());
			initialValues.put("tin", user.getTin());
			initialValues.put("gender", user.getGender());
			initialValues.put("civilstatus", user.getCivilstatus());
			initialValues.put("emergency_person", user.getEmergency_person());
			initialValues.put("emergency_number", user.getEmergency_number());
			initialValues.put("landline", user.getLandline());
			initialValues.put("mobile", user.getMobile());

			initialValues.put("suffix", user.getSuffix());
			initialValues.put("maiden_name", user.getMaiden_name());
			initialValues.put("ofc_number", user.getOfc_number());
			initialValues.put("ben_firstName", user.getBen_firstName());
			initialValues.put("ben_middleName", user.getBen_middleName());
			initialValues.put("ben_lastName", user.getBen_lastName());
			initialValues.put("ben_suffix", user.getBen_suffix());
			initialValues.put("ben_birthday", user.getBen_birthday());
			initialValues.put("ben_gender", user.getBen_gender());
			initialValues.put("ben_civilstatus", user.getBen_civilstatus());
			initialValues.put("ben_maiden_name", user.getBen_maiden_name());
			initialValues.put("ben_disabled", user.getBen_disabled());

			initialValues.put("ph_pen", user.getPh_pen());
			initialValues.put("ph_employer_name", user.getPh_employer_name());
			initialValues.put("ph_employer_address", user.getPh_employer_address());
			initialValues.put("ph_date_hired", user.getPh_date_hired());
			initialValues.put("ph_profession", user.getPh_profession());
			initialValues.put("ph_country_based", user.getPh_country_based());
			initialValues.put("ph_foreign_address", user.getPh_foreign_address());
			initialValues.put("ph_duration_from", user.getPh_duration_from());
			initialValues.put("ph_duration_to", user.getPh_duration_to());
			initialValues.put("ph_member_type", user.getPh_member_type());
			initialValues.put("ph_family_income", user.getPh_family_income());
			initialValues.put("ph_doc1", user.getPh_doc1());
			initialValues.put("ph_doc2", user.getPh_doc2());
			initialValues.put("ph_doc3", user.getPh_doc3());
			initialValues.put("ph_doc4", user.getPh_doc4());

			initialValues.put("ss_pension_type", user.getSs_pension_type());
			initialValues.put("ss_pension_account_number", user.getSs_pension_account_number());
			initialValues.put("ss_membership_type", user.getSs_membership_type());
			initialValues.put("ss_employer_id", user.getSs_employer_id());
			initialValues.put("ss_receipt_number", user.getSs_receipt_number());
			initialValues.put("ss_doc1", user.getSs_doc1());
			initialValues.put("ss_doc2", user.getSs_doc2());
			initialValues.put("ss_doc3", user.getSs_doc3());
			initialValues.put("ss_doc4", user.getSs_doc4());

			initialValues.put("pi_membership_type", user.getPi_membership_type());
			initialValues.put("pi_employer_business_name", user.getPi_employer_business_name());
			initialValues.put("pi_employee_number", user.getPi_employee_number());
			initialValues.put("pi_date_start", user.getPi_date_start());
			initialValues.put("pi_profession", user.getPi_profession());
			initialValues.put("pi_employement_status", user.getPi_employment_status());
			initialValues.put("pi_address", user.getPi_address());
			initialValues.put("pi_basic", user.getPi_basic());
			initialValues.put("pi_allowance", user.getPi_allowance());
			initialValues.put("pi_total", user.getPi_total());
			initialValues.put("pi_doc1", user.getPi_doc1());
			initialValues.put("pi_doc2", user.getPi_doc2());
			initialValues.put("pi_doc3", user.getPi_doc3());
			initialValues.put("pi_doc4", user.getPi_doc4());
		}

		public boolean updateRecordAsPushed(int ispushed, String id) throws Exception {
			mDb.execSQL("UPDATE " + TABLE_NAME + " SET " + "ispushed = " + ispushed + "" + " WHERE id = '" + id + "'");
			return true;
		}

		public void clearTable() throws Exception {
			mDb.execSQL("DELETE FROM " + TABLE_NAME);
		}

		public List<User> fetchRecords() throws Exception {
			List<User> userList = new ArrayList<User>();
			User user = new User();
			Cursor mCursor = mDb.rawQuery("SELECT *  FROM " + TABLE_NAME + " ORDER BY username ", null);
			mCursor.moveToFirst();
			while (mCursor.isAfterLast() == false) {
				user = processCursorForFetch(mCursor);
				userList.add(user);
				mCursor.moveToNext();
			}
			mCursor.close();
			return userList;
		}

		public List<User> fetchRecordsWithSearchParam(String searchParam) throws Exception {
			List<User> userList = new ArrayList<User>();
			User user = new User();
			Cursor mCursor = mDb.rawQuery("SELECT *  FROM " + TABLE_NAME + " WHERE (middleName LIKE '%" + searchParam + "%' OR firstName LIKE '%" + searchParam + "%' OR lastName LIKE '%" + searchParam + "%') AND (user_id ='" + applicationObj.getCurrentUser().getId() + "')  ORDER BY firstName ", null);
			mCursor.moveToFirst();
			while (mCursor.isAfterLast() == false) {
				user = processCursorForFetch(mCursor);

				userList.add(user);
				mCursor.moveToNext();
			}
			mCursor.close();
			return userList;
		}

		public User fetchRecordById(String Id) throws Exception {

			User user = new User();
			Cursor mCursor = mDb.rawQuery("SELECT *  FROM " + TABLE_NAME + " WHERE id = '" + Id + "'", null);
			mCursor.moveToFirst();
			while (mCursor.isAfterLast() == false) {
				user = processCursorForFetch(mCursor);

				mCursor.moveToNext();
			}
			mCursor.close();
			return user;
		}

		private User processCursorForFetch(Cursor mCursor) {
			User user = new User();
			user.setId(mCursor.getString(mCursor.getColumnIndex("id")));

			user.setUser_id(mCursor.getString(mCursor.getColumnIndex("user_id")));
			user.setEmail(mCursor.getString(mCursor.getColumnIndex("email")));
			user.setPassword_hash(mCursor.getString(mCursor.getColumnIndex("password")));
			user.setFirstName(mCursor.getString(mCursor.getColumnIndex("firstName")));
			user.setMiddleName(mCursor.getString(mCursor.getColumnIndex("middleName")));
			user.setLastName(mCursor.getString(mCursor.getColumnIndex("lastName")));
			user.setBirthday(mCursor.getLong(mCursor.getColumnIndex("birthday")));
			user.setType(mCursor.getInt(mCursor.getColumnIndex("type")));
			user.setImage(mCursor.getString(mCursor.getColumnIndex("image")));
			user.setTermination_image(mCursor.getString(mCursor.getColumnIndex("termination_image")));
			user.setAddRegion(mCursor.getString(mCursor.getColumnIndex("add_region")));
			user.setAddCity(mCursor.getString(mCursor.getColumnIndex("add_city")));
			user.setAddBrgy(mCursor.getString(mCursor.getColumnIndex("add_brgy")));
			user.setAddLine1(mCursor.getString(mCursor.getColumnIndex("add_line1")));
			user.setAddLine2(mCursor.getString(mCursor.getColumnIndex("add_line2")));
			user.setZipCode(mCursor.getString(mCursor.getColumnIndex("zipCode")));
			user.setSss(mCursor.getString(mCursor.getColumnIndex("sss")));
			user.setPagibig(mCursor.getString(mCursor.getColumnIndex("pagibig")));
			user.setPhilhealth(mCursor.getString(mCursor.getColumnIndex("philhealth")));
			user.setTin(mCursor.getString(mCursor.getColumnIndex("tin")));
			user.setGender(mCursor.getInt(mCursor.getColumnIndex("gender")));
			user.setCivilstatus(mCursor.getString(mCursor.getColumnIndex("civilstatus")));
			user.setEmergency_person(mCursor.getString(mCursor.getColumnIndex("emergency_person")));
			user.setEmergency_number(mCursor.getString(mCursor.getColumnIndex("emergency_number")));
			user.setLandline(mCursor.getString(mCursor.getColumnIndex("landline")));
			user.setMobile(mCursor.getString(mCursor.getColumnIndex("mobile")));

			user.setSuffix(mCursor.getString(mCursor.getColumnIndex("suffix")));
			user.setMaiden_name(mCursor.getString(mCursor.getColumnIndex("maiden_name")));
			user.setOfc_number(mCursor.getString(mCursor.getColumnIndex("ofc_number")));
			user.setBen_firstName(mCursor.getString(mCursor.getColumnIndex("ben_firstName")));
			user.setBen_middleName(mCursor.getString(mCursor.getColumnIndex("ben_middleName")));
			user.setBen_lastName(mCursor.getString(mCursor.getColumnIndex("ben_lastName")));
			user.setBen_suffix(mCursor.getString(mCursor.getColumnIndex("ben_suffix")));
			user.setBen_birthday(mCursor.getLong(mCursor.getColumnIndex("birthday")));
			user.setBen_gender(mCursor.getInt(mCursor.getColumnIndex("ben_gender")));
			user.setBen_civilstatus(mCursor.getString(mCursor.getColumnIndex("ben_civilstatus")));
			user.setBen_maiden_name(mCursor.getString(mCursor.getColumnIndex("ben_maiden_name")));
			user.setBen_disabled(mCursor.getInt(mCursor.getColumnIndex("ben_disabled")));

			user.setPh_pen(mCursor.getString(mCursor.getColumnIndex("ph_pen")));
			user.setPh_employer_name(mCursor.getString(mCursor.getColumnIndex("ph_employer_name")));
			user.setPh_employer_address(mCursor.getString(mCursor.getColumnIndex("ph_employer_address")));
			user.setPh_date_hired(mCursor.getLong(mCursor.getColumnIndex("ph_date_hired")));
			user.setPh_profession(mCursor.getString(mCursor.getColumnIndex("ph_profession")));
			user.setPh_country_based(mCursor.getString(mCursor.getColumnIndex("ph_country_based")));
			user.setPh_foreign_address(mCursor.getString(mCursor.getColumnIndex("ph_foreign_address")));
			user.setPh_duration_from(mCursor.getLong(mCursor.getColumnIndex("ph_duration_from")));
			user.setPh_duration_to(mCursor.getLong(mCursor.getColumnIndex("ph_duration_to")));
			user.setPh_member_type(mCursor.getString(mCursor.getColumnIndex("ph_member_type")));
			user.setPh_family_income(mCursor.getString(mCursor.getColumnIndex("ph_family_income")));
			user.setPh_doc1(mCursor.getString(mCursor.getColumnIndex("ph_doc1")));
			user.setPh_doc2(mCursor.getString(mCursor.getColumnIndex("ph_doc2")));
			user.setPh_doc3(mCursor.getString(mCursor.getColumnIndex("ph_doc3")));
			user.setPh_doc4(mCursor.getString(mCursor.getColumnIndex("ph_doc4")));

			user.setSs_pension_type(mCursor.getString(mCursor.getColumnIndex("ss_pension_type")));
			user.setSs_pension_account_number(mCursor.getString(mCursor.getColumnIndex("ss_pension_account_number")));
			user.setSs_membership_type(mCursor.getString(mCursor.getColumnIndex("ss_membership_type")));
			user.setSs_employer_id(mCursor.getString(mCursor.getColumnIndex("ss_employer_id")));
			user.setSs_receipt_number(mCursor.getString(mCursor.getColumnIndex("ss_receipt_number")));
			user.setSs_doc1(mCursor.getString(mCursor.getColumnIndex("ss_doc1")));
			user.setSs_doc2(mCursor.getString(mCursor.getColumnIndex("ss_doc2")));
			user.setSs_doc3(mCursor.getString(mCursor.getColumnIndex("ss_doc3")));
			user.setSs_doc4(mCursor.getString(mCursor.getColumnIndex("ss_doc4")));

			user.setPi_membership_type(mCursor.getString(mCursor.getColumnIndex("pi_membership_type")));
			user.setPi_employer_business_name(mCursor.getString(mCursor.getColumnIndex("pi_employer_business_name")));
			user.setPi_employee_number(mCursor.getString(mCursor.getColumnIndex("pi_employee_number")));
			user.setPi_date_start(mCursor.getLong(mCursor.getColumnIndex("pi_date_start")));
			user.setPi_profession(mCursor.getString(mCursor.getColumnIndex("pi_profession")));
			user.setPi_employment_status(mCursor.getString(mCursor.getColumnIndex("pi_employement_status")));
			user.setPi_address(mCursor.getString(mCursor.getColumnIndex("pi_address")));
			user.setPi_basic(mCursor.getString(mCursor.getColumnIndex("pi_basic")));
			user.setPi_allowance(mCursor.getString(mCursor.getColumnIndex("pi_allowance")));
			user.setPi_total(mCursor.getString(mCursor.getColumnIndex("pi_total")));
			user.setPi_doc1(mCursor.getString(mCursor.getColumnIndex("pi_doc1")));
			user.setPi_doc2(mCursor.getString(mCursor.getColumnIndex("pi_doc2")));
			user.setPi_doc3(mCursor.getString(mCursor.getColumnIndex("pi_doc3")));
			user.setPi_doc4(mCursor.getString(mCursor.getColumnIndex("pi_doc4")));

			user.setEdited_by(mCursor.getString(mCursor.getColumnIndex("edited_by")));
			user.setCreated(mCursor.getLong(mCursor.getColumnIndex("created")));
			user.setUpdated(mCursor.getLong(mCursor.getColumnIndex("updated")));
			user.setVersion(mCursor.getInt(mCursor.getColumnIndex("version")));

			return user;
		}

		public List<User> fetchRecordsUnsynced() throws Exception {
			List<User> userList = new ArrayList<User>();
			try {
				User user = new User();
				Cursor mCursor = mDb.rawQuery("SELECT *  FROM " + TABLE_NAME + " where  ispushed != 1", null);
				mCursor.moveToFirst();
				while (mCursor.isAfterLast() == false) {
					user = processCursorForFetch(mCursor);

					userList.add(user);
					mCursor.moveToNext();
				}
				mCursor.close();
			} catch (Exception e) {
			}

			return userList;
		}

		public List<String> fetchAllIds() throws Exception {
			List<String> ids = new ArrayList<String>();
			// initialize ids variable
			ids.add("'{00000000-0000-0000-0000-000000000000}'");
			Cursor mCursor = mDb.rawQuery("SELECT id FROM " + TABLE_NAME + " ", null);
			mCursor.moveToFirst();
			while (mCursor.isAfterLast() == false) {
				ids.add("'" + String.valueOf(mCursor.getString(mCursor.getColumnIndex("id"))) + "'");
				mCursor.moveToNext();
			}
			mCursor.close();
			return ids;
		}

		public static boolean VerifyUserByName(String userName) throws Exception {

			Cursor mCursor = mDb.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE LOWER(email) = '" + userName.toLowerCase(Locale.getDefault()) + "'", null);
			mCursor.moveToFirst();
			while (mCursor.isAfterLast() == false) {
				return true;
			}
			mCursor.close();

			return false;
		}

		public static boolean fetchUserPassword(String username) throws Exception {
			boolean result = false;
			Cursor mCursor = mDb.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE LOWER(email) = '" + username.toLowerCase(Locale.getDefault()) + "'", null);
			mCursor.moveToFirst();
			if (mCursor.getCount() != 0) {
				while (mCursor.isAfterLast() == false) {
					User u = applicationObj.getCurrentUser();
					u.setId(mCursor.getString(mCursor.getColumnIndex("id")));
					u.setEmail(mCursor.getString(mCursor.getColumnIndex("email")));
					u.setPassword_hash(mCursor.getString(mCursor.getColumnIndex("password")));
					u.setFirstName(mCursor.getString(mCursor.getColumnIndex("firstName")));
					u.setLastName(mCursor.getString(mCursor.getColumnIndex("lastName")));

					result = true;
					mCursor.moveToNext();
				}
			} else {
				result = false;
			}
			mCursor.close();

			return result;
		}

		public boolean deleteRecord(String id) throws Exception {

			try {
				mDb.execSQL("DELETE FROM " + TABLE_NAME + " WHERE id = '" + id + "'");
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}

	}

}
