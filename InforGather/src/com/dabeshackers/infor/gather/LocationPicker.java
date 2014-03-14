package com.dabeshackers.infor.gather;


import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.dabeshackers.infor.gather.R;
import com.dabeshackers.infor.gather.helpers.LocationHelper;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationPicker extends SherlockFragmentActivity {

	public static final int REQUESTCODE = 123;

	public static String EXTRAS_RESULT_LONGITUDE = "EXTRAS_RESULT_LONGITUDE";
	public static String EXTRAS_RESULT_LATITUDE = "EXTRAS_RESULT_LATITUDE";

	private GoogleMap mMap;
	UiSettings mUiSettings;

	// MapOverlay mapOvlay;
	MapView mapView;
	TextView tvLocation, tvLocAddress;

	double selLng;
	double selLat;

	Double lng = null;
	Double lat = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locationpicker);

		setTitle("Pick a location");

		// Getting reference to tv_location available in activity_main.xml
		tvLocation = (TextView) findViewById(R.id.tv_location);
		tvLocAddress = (TextView) findViewById(R.id.tv_locationaddress);

		if (getIntent().getExtras().containsKey("lng")) {
			lng = getIntent().getExtras().getDouble("lng");
		}

		if (getIntent().getExtras().containsKey("lat")) {
			lat = getIntent().getExtras().getDouble("lat");
		}

		try {
			setUpMapIfNeeded();
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}
		Location location = new Location("Location");
		location.setLongitude(lng);
		location.setLatitude(lat);
		moveMapToMyLocation(location);

		if (mMap != null) {
			mMap.setOnMapClickListener(new OnMapClickListener() {

				public void onMapClick(LatLng latlng) {

					mMap.clear();
					Location location = new Location("Location");

					selLng = latlng.longitude;
					selLat = latlng.latitude;
					location.setLongitude(latlng.longitude);
					location.setLatitude(latlng.latitude);
					moveMapToMyLocation(location);

					// Setting Latitude and Longitude in TextView
					DecimalFormat df = new DecimalFormat("#,###.0000");
					tvLocation.setText("Latitude:" + df.format(latlng.latitude) + ", " + "Longitude:" + df.format(latlng.longitude));

					setReadableAddress();

				}
			});
		}
	}

	private void setReadableAddress() {
		final LatLng latLng = new LatLng(selLat, selLng);
		new Thread(new Runnable() {

			@Override
			public void run() {
				final String result = LocationHelper.getReadableAddressFromLatLng(LocationPicker.this, latLng, false);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (result == null || result.equals(LocationHelper.LOCATION_UNAVAILABLE)) {
							tvLocAddress.setText("");
							tvLocAddress.setVisibility(View.GONE);
						} else {
							tvLocAddress.setText(result);
							tvLocAddress.setVisibility(View.VISIBLE);
						}
					}

				});

			}

		}).start();

	}

	EditText editsearch;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_locationpicker, menu);

		// Locate the EditText in menu.xml
		editsearch = (EditText) menu.findItem(R.id.menu_search_item).getActionView();
		editsearch.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
		// Capture Text in EditText
		editsearch.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				final String value = v.getText().toString().toLowerCase(Locale.getDefault());

				new Thread(new Runnable() {

					@Override
					public void run() {

						// Creating an instance of Geocoder class
						Geocoder geocoder = new Geocoder(LocationPicker.this);

						try {
							// Getting a maximum of 3 Address that matches the
							// input text
							final List<Address> addresses = geocoder.getFromLocationName(value, 3);

							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									if (addresses == null || addresses.size() == 0) {
										Toast.makeText(LocationPicker.this, "No Location found", Toast.LENGTH_SHORT).show();
									}

									// Clears all the existing markers on the
									// map
									mMap.clear();

									// Adding Markers on Google Map for each
									// matching address
									for (int i = 0; i < addresses.size(); i++) {

										Address address = (Address) addresses.get(i);

										// Creating an instance of GeoPoint, to
										// display in Google Map
										LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

										String addressText = String.format("%s, %s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getCountryName());

										MarkerOptions markerOptions = new MarkerOptions();
										markerOptions.position(latLng);
										markerOptions.title(addressText);

										mMap.addMarker(markerOptions);

										// Locate the first location
										if (i == 0) {
											mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

											selLng = latLng.longitude;
											selLat = latLng.latitude;

											// Setting Latitude and Longitude in
											// TextView
											DecimalFormat df = new DecimalFormat("#,###.0000");
											tvLocation.setText("Latitude:" + df.format(latLng.latitude) + ", " + "Longitude:" + df.format(latLng.longitude));

											setReadableAddress();
										}

									}
								}

							});
						} catch (IOException e) {
							e.printStackTrace();
						}

					}

				}).start();

				return false;
			}
		});

		// Show the search menu item in menu.xml
		MenuItem menuSearch = menu.findItem(R.id.menu_search_item);
		menuSearch.setOnActionExpandListener(new OnActionExpandListener() {

			// Menu Action Collapse
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				// Empty EditText to remove text filtering
				editsearch.setText("");
				editsearch.clearFocus();

				return true;
			}

			// Menu Action Expand
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				// Focus on EditText
				editsearch.requestFocus();

				// Force the keyboard to show on EditText focus
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

				return true;
			}
		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {

		case R.id.menu_cur_loc:

			Location location = new Location("Location");
			location.setLongitude(lng);
			location.setLatitude(lat);
			moveMapToMyLocation(location);

			return true;

		case R.id.menu_ok:

			Intent data = new Intent();
			data.putExtra(EXTRAS_RESULT_LONGITUDE, selLng);
			data.putExtra(EXTRAS_RESULT_LATITUDE, selLat);

			Log.i("LocationPickerActivity", "Result Location: resultLat: " + selLat + " , resultLng: " + selLng);

			setResult(RESULT_OK, data);
			LocationPicker.this.finish();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Sets up the map if it is possible to do so (i.e., the Google Play
	 * services APK is correctly installed) and the map has not already been
	 * instantiated.. This will ensure that we only ever call
	 * {@link #setUpMap()} once when {@link #mMap} is not null.
	 * <p>
	 * If it isn't installed {@link SupportMapFragment} (and
	 * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt
	 * for the user to install/update the Google Play services APK on their
	 * device.
	 * <p>
	 * A user can return to this FragmentActivity after following the prompt and
	 * correctly installing/updating/enabling the Google Play services. Since
	 * the FragmentActivity may not have been completely destroyed during this
	 * process (it is likely that it would only be stopped or paused),
	 * {@link #onCreate(Bundle)} may not be called again so we should call this
	 * method in {@link #onResume()} to guarantee that it will be called.
	 * 
	 * @throws GooglePlayServicesNotAvailableException
	 */
	private void setUpMapIfNeeded() throws GooglePlayServicesNotAvailableException {
		Bundle extras = getIntent().getExtras();
		Location location = null;
		if (extras != null) {
			double lng = extras.getDouble("lng");
			double lat = extras.getDouble("lat");
			location = new Location("Location");
			location.setLongitude(lng);
			location.setLatitude(lat);
		}

		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {

			MapsInitializer.initialize(LocationPicker.this);

			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap(location);
			}

		}
	}

	/**
	 * This is where we can add markers or lines, add listeners or move the
	 * camera. In this case, we just add a marker near Africa.
	 * <p>
	 * This should only be called once and when we are sure that {@link #mMap}
	 * is not null.
	 */
	private void setUpMap(Location location) {

		mMap.setMyLocationEnabled(true);
		mUiSettings = mMap.getUiSettings();
		moveMapToMyLocation(location);

	}

	Location loc;

	private void moveMapToMyLocation(Location location) {
		if (location == null) {
			LocationManager locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Criteria crit = new Criteria();
			loc = locMan.getLastKnownLocation(locMan.getBestProvider(crit, false));
		} else {
			loc = location;

			MarkerOptions markerOptions = new MarkerOptions();
			// markerOptions.title(name);
			markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
			markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
			// markerOptions.snippet(vicinity);

			Marker marker = mMap.addMarker(markerOptions);
			marker.showInfoWindow();

		}

		CameraPosition camPos = new CameraPosition.Builder().zoom(15f).target(new LatLng(loc.getLatitude(), loc.getLongitude())).build();

		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));

		selLng = loc.getLongitude();
		selLat = loc.getLatitude();

		// Setting Latitude and Longitude in TextView
		DecimalFormat df = new DecimalFormat("#,###.0000");
		tvLocation.setText("Latitude:" + df.format(selLng) + ", " + "Longitude:" + df.format(selLat));

		setReadableAddress();

	}

}