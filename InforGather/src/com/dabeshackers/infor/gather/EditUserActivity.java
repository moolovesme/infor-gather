package com.dabeshackers.infor.gather;


import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dabeshackers.infor.gather.R;
import com.dabeshackers.infor.gather.application.AppMain;
import com.dabeshackers.infor.gather.entities.User;
import com.dabeshackers.infor.gather.helpers.DateHelper;
import com.dabeshackers.infor.gather.helpers.LocationHelper;
import com.dabeshackers.infor.gather.http.ApplicationWebService;
import com.google.android.gms.maps.model.LatLng;

public class EditUserActivity extends SherlockFragmentActivity {

	//	private static final String TAG = EditUserActivity.class.getSimpleName();

	public static final int REQUESTCODE = EditUserActivity.class.getSimpleName().hashCode();

	Double lng = null;
	Double lat = null;

	private LatLng currentLatLng;
	private String currentLocationText;

	private static long currentBirthday;
	private static User currentUser;

	private EditText email, tradeName, firstName, lastName, zipcode, website, fb_url, gp_url, twtr_url, landline, mobile;
	private static Button birthday;

	private Button address;

	boolean hasValidationErrors = false;
	final StringBuilder errMsg = new StringBuilder();

	protected static final int DIALOG_LOCATION_PICKER = 0;
	protected static final int DIALOG_BIRTHDATE_PICKER = 1;

	ProgressDialog pd;

	boolean allowChangeTradeName;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_user_edit);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		AppMain appMain = (AppMain) getApplication();
		currentUser = appMain.getCurrentUser();

		int backOff = 100;
		Location loc = null;
		while (backOff < 100 || loc == null) {
			loc = LocationHelper.getLastKnownLocation(EditUserActivity.this);
			backOff++;
		}

		currentLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
		currentLocationText = LocationHelper.getReadableAddressFromLocation(EditUserActivity.this, loc, false);

		email = (EditText) findViewById(R.id.email);
		tradeName = (EditText) findViewById(R.id.tradename);
		firstName = (EditText) findViewById(R.id.firstname);
		lastName = (EditText) findViewById(R.id.lastname);
		zipcode = (EditText) findViewById(R.id.zipcode);
		website = (EditText) findViewById(R.id.website);
		fb_url = (EditText) findViewById(R.id.fb_url);
		gp_url = (EditText) findViewById(R.id.gp_url);
		twtr_url = (EditText) findViewById(R.id.twtr_url);
		landline = (EditText) findViewById(R.id.landline);
		mobile = (EditText) findViewById(R.id.mobile);

		website.setOnFocusChangeListener(urlFocusListener);
		fb_url.setOnFocusChangeListener(urlFocusListener);
		gp_url.setOnFocusChangeListener(urlFocusListener);
		twtr_url.setOnFocusChangeListener(urlFocusListener);

		address = (Button) findViewById(R.id.address);
		if (currentLocationText == null || !currentLocationText.equals(LocationHelper.LOCATION_UNAVAILABLE)) {
			address.setText(currentLocationText == null ? "Not yet set" : currentLocationText);
		} else {
			address.setText("Location Unavailable");
		}
		address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(EditUserActivity.this, LocationPicker.class);
				intent.putExtra("lat", currentLatLng.latitude);
				intent.putExtra("lng", currentLatLng.longitude);
				startActivityForResult(intent, LocationPicker.REQUESTCODE);
			}
		});

		birthday = (Button) findViewById(R.id.birthday);
		birthday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showBirthdatePickerDialog();
			}
		});

		// Populate
		if (currentUser.getTradeName() == null || currentUser.getTradeName().length() == 0) {
			allowChangeTradeName = true;
			tradeName.setFocusable(true);
		} else {
			allowChangeTradeName = false;
			tradeName.setFocusable(false);
		}

		email.setFocusable(false);
		email.setText(currentUser.getId());
		tradeName.setText(currentUser.getTradeName());
		firstName.setText(currentUser.getFirstName());
		lastName.setText(currentUser.getLastName());
		zipcode.setText(currentUser.getZipCode());
		website.setText(currentUser.getBusinessURL());
		fb_url.setText(currentUser.getFacebookURL());
		gp_url.setText(currentUser.getGplusURL());
		twtr_url.setText(currentUser.getTwitterURL());
		landline.setText(currentUser.getLandline());
		mobile.setText(currentUser.getMobile());

		currentBirthday = ((currentUser.getTradeName() == null) ? Calendar.getInstance().getTimeInMillis() : currentUser.getBirthday());
		birthday.setText(DateHelper.toMMDDYYY(currentBirthday, "/"));

		currentLatLng = new LatLng(currentUser.getAdd_lat(), currentUser.getAdd_lng());
		currentLocationText = currentUser.getAddLine1();// LocationHelper.getReadableAddressFromLatLng(EditUserActivity.this,currentLatLng,
		// false);

	}

	OnFocusChangeListener urlFocusListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			EditText text = (EditText) v;
			if (!hasFocus) {
				if ((!text.getText().toString().startsWith("http://") && (!text.getText().toString().startsWith("https://"))) && text.getText().toString().length() > 0) {
					text.setText("http://" + text.getText());
				}
			}
		}
	};

	private boolean validate() {
		errMsg.delete(0, errMsg.length());
		errMsg.append("Errors in the following fields were encountered. Please correct them then try again.\n\n");

		if (tradeName.getText().toString() == null || tradeName.getText().toString().length() == 0) {
			hasValidationErrors = true;
			errMsg.append("- Trade Name should not be left blank.").append("\n");
		}

		if (hasValidationErrors) {
			return false;

		}

		return true;
	}

	private void save() {

		// REMINDER- Run on separate thread
		pd = new ProgressDialog(EditUserActivity.this);
		pd.setIndeterminate(true);
		pd.setTitle("Saving Changes.");
		pd.setMessage("Please wait.");
		pd.setCancelable(false);
		pd.show();

		new Thread(new Runnable() {

			@Override
			public void run() {

				validate();
				if (hasValidationErrors) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();
							AlertDialog.Builder alert = new AlertDialog.Builder(EditUserActivity.this);
							alert.setTitle("Unable to proceed.");
							alert.setMessage(errMsg);
							alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {

								}
							});
							alert.setIcon(android.R.drawable.ic_dialog_alert);
							alert.show();
						}

					});

				} else if (allowChangeTradeName && !ApplicationWebService.Users.isTradeNameUnique(EditUserActivity.this, tradeName.getText().toString())) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();

							AlertDialog.Builder alert = new AlertDialog.Builder(EditUserActivity.this);
							alert.setTitle("Unable to save changes.");
							alert.setMessage("Trade Name is already taken. Please change it and try again.");
							alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {

								}
							});
							alert.setIcon(android.R.drawable.ic_dialog_alert);
							alert.show();
						}

					});

				} else {
					// Update entity
					currentUser.setTradeName(tradeName.getText().toString());
					currentUser.setFirstName(firstName.getText().toString());
					currentUser.setLastName(lastName.getText().toString());
					currentUser.setZipCode(zipcode.getText().toString());
					currentUser.setBusinessURL(website.getText().toString());
					currentUser.setFacebookURL(fb_url.getText().toString());
					currentUser.setGplusURL(gp_url.getText().toString());
					currentUser.setTwitterURL(twtr_url.getText().toString());
					currentUser.setLandline(landline.getText().toString());
					currentUser.setMobile(mobile.getText().toString());

					currentUser.setBirthday(currentBirthday);
					currentUser.setAddLine1(currentLocationText);

					currentUser.setAdd_lat(currentLatLng.latitude);
					currentUser.setAdd_lat(currentLatLng.longitude);

					currentUser.setUpdated(Calendar.getInstance().getTimeInMillis());

					if (ApplicationWebService.Users.pushRecordToBackEnd(EditUserActivity.this, currentUser)) {
						setResult(RESULT_OK);
						finish();
					} else {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								pd.dismiss();

								AlertDialog.Builder alert = new AlertDialog.Builder(EditUserActivity.this);
								alert.setTitle("Unable to save changes.");
								alert.setMessage("An unexpected error has been encountered. Please try again.");
								alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {

									}
								});
								alert.setIcon(android.R.drawable.ic_dialog_alert);
								alert.show();
							}

						});
					}

				}
			}

		}).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LocationPicker.REQUESTCODE) {
			if (resultCode == RESULT_OK) {
				double lat = (Double) data.getExtras().get(LocationPicker.EXTRAS_RESULT_LATITUDE);
				double lng = (Double) data.getExtras().get(LocationPicker.EXTRAS_RESULT_LONGITUDE);
				currentLatLng = new LatLng(lat, lng);

				currentLocationText = LocationHelper.getReadableAddressFromLatLng(EditUserActivity.this, currentLatLng, false);

				TextView address = (TextView) findViewById(R.id.address);
				address.setText(currentLocationText == null ? "Anywhere" : currentLocationText);

			} else if (resultCode == RESULT_CANCELED) {
				// user cancelled
			} else {
				// failed
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Get the options menu view from menu.xml in menu folder
		getSupportMenuInflater().inflate(R.menu.activity_user_edit, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
			setResult(RESULT_CANCELED);
			this.finish();
			return true;

		case R.id.menu_proceed:
			save();

			break;

		}
		return true;
	}

	public void showBirthdatePickerDialog() {
		DialogFragment newFragment = new BirthdatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "birthdatePicker");
	}

	public static class BirthdatePickerFragment extends DialogFragment implements OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of TimePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			Calendar c = Calendar.getInstance();
			c.set(year, monthOfYear, dayOfMonth);
			currentBirthday = c.getTimeInMillis();
			birthday.setText(DateHelper.toMMDDYYY(currentBirthday, "/"));
		}

	}

	@Override
	public void onBackPressed() {

		setResult(RESULT_CANCELED);
		EditUserActivity.super.onBackPressed();

	}

}
