package com.dabeshackers.infor.gather.helpers;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class LocationHelper {

	private final static String TAG = LocationHelper.class.getSimpleName();

	private static final int TWO_MINUTES = 1000 * 60 * 2;

	public static Location isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return location;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return location;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return currentBestLocation;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return location;
		} else if (isNewer && !isLessAccurate) {
			return location;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return location;
		}
		return currentBestLocation;
	}

	/** Checks whether two providers are the same */
	private static boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	public static Location getLastKnownLocation(Context context) {
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = lm.getProviders(true);

		/*
		 * Loop over the array backwards, and if you get an accurate location,
		 * then break out the loop
		 */
		Location l = null;

		for (int i = providers.size() - 1; i >= 0; i--) {
			l = lm.getLastKnownLocation(providers.get(i));
			if (l != null)
				break;
		}

		return l;
	}

	public static LatLng getLastKnownLocationLatLng(Context context) {
		Location l = getLastKnownLocation(context);

		if (l != null) {
			return new LatLng(l.getLatitude(), l.getLongitude());
		} else {
			return null;
		}

	}

	private static final double DEFAULT_LAT = 14.5965865;
	private static final double DEFAULT_LNG = 120.9797033;

	public static LatLng getDefaultLocationLatLng(Context context) {
		return new LatLng(DEFAULT_LAT, DEFAULT_LNG);
	}

	public final static String LOCATION_UNAVAILABLE = "Location Unavailable";

	public static String getReadableAddressFromLatLng(Context context, LatLng latlng, boolean includeCountry) {
		Location loc = new Location("");
		loc.setLatitude(latlng.latitude);
		loc.setLongitude(latlng.longitude);

		return getReadableAddressFromLocation(context, loc, includeCountry);
	}

	public static String getReadableAddressFromLocation(Context context, Location location, boolean includeCountry) {
		DecimalFormat df = new DecimalFormat("#,###.0000");
		String defaultLocationString = df.format(location.getLatitude()) + " : " + df.format(location.getLongitude());
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		// Create a list to contain the result address
		List<Address> addresses = null;
		try {
			if (location != null) {
				addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			}

		} catch (IOException e1) {
			Log.e(TAG, "IO Exception in getFromLocation()");
			e1.printStackTrace();
			return defaultLocationString;
		} catch (IllegalArgumentException e2) {
			// Error message to post in the log
			String errorString = "Illegal arguments " + Double.toString(location.getLatitude()) + " , " + Double.toString(location.getLongitude()) + " passed to address service";
			Log.e(TAG, errorString);
			e2.printStackTrace();
			// return errorString;
			return (LOCATION_UNAVAILABLE);
		}

		// If the reverse geocode returned an address
		if (addresses != null && addresses.size() > 0) {
			// Get the first address
			Address address = addresses.get(0);
			/*
			 * Format the first line of address (if available), city, and
			 * country name.
			 */
			String addressText = String.format("%s, %s %s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getLocality(), ((!includeCountry) ? "" : ", " + address.getCountryName()));
			// Return the text
			return addressText;
		} else {
			return Double.toString(location.getLatitude()) + " , " + Double.toString(location.getLongitude());
		}
	}

	public static List<LatLng> makeCircle(LatLng centre, double radius) {
		List<LatLng> points = new ArrayList<LatLng>();

		double EARTH_RADIUS = 6378100.0;
		// Convert to radians.
		double lat = centre.latitude * Math.PI / 180.0;
		double lng = centre.longitude * Math.PI / 180.0;

		for (double t = 0; t <= Math.PI * 2; t += 0.3) {
			// y
			double latPoint = lat + (radius / EARTH_RADIUS) * Math.sin(t);
			// x
			double lonPoint = lng + (radius / EARTH_RADIUS) * Math.cos(t) / Math.cos(lat);

			// saving the location on circle as a LatLng point
			LatLng point = new LatLng(latPoint * 180.0 / Math.PI, lonPoint * 180.0 / Math.PI);

			// here mMap is my GoogleMap object
			// mMap.addMarker(new MarkerOptions().position(point));

			// now here note that same point(lat/lng) is used for marker as well
			// as saved in the ArrayList
			points.add(point);

		}

		return points;
	}

	//	public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	//	LocationClient mLocationClient;
	//	LocationRequest mLocationRequest;
	//	boolean mUpdatesRequested;
	//	private Activity activity;
	//	private Context context;
	//	ConnectionCallbacks connectionCallback = new ConnectionCallbacks() {
	//
	//		@Override
	//		public void onConnected(Bundle arg0) {
	//			if (mUpdatesRequested) {
	//				startPeriodicUpdates();
	//			}
	//		}
	//
	//		@Override
	//		public void onDisconnected() {
	//		}
	//
	//	};
	//
	//	LocationListener locationListener = new LocationListener() {
	//
	//		@Override
	//		public void onLocationChanged(Location arg0) {
	//
	//		}
	//
	//	};
	//
	//	private void startPeriodicUpdates() {
	//
	//		mLocationClient.requestLocationUpdates(mLocationRequest, locationListener);
	//	}
	//
	//	OnConnectionFailedListener onConnectionFailedListener = new OnConnectionFailedListener() {
	//
	//		@Override
	//		public void onConnectionFailed(ConnectionResult connectionResult) {
	//			/*
	//			 * Google Play services can resolve some errors it detects. If the
	//			 * error has a resolution, try sending an Intent to start a Google
	//			 * Play services activity that can resolve error.
	//			 */
	//			if (connectionResult.hasResolution()) {
	//				try {
	//
	//					// Start an Activity that tries to resolve the error
	//					connectionResult.startResolutionForResult(activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
	//
	//					/*
	//					 * Thrown if Google Play services canceled the original
	//					 * PendingIntent
	//					 */
	//
	//				} catch (IntentSender.SendIntentException e) {
	//
	//					// Log the error
	//					e.printStackTrace();
	//				}
	//			} else {
	//
	//				// If no resolution is available, display a dialog to the user with the error.
	//				showErrorDialog(connectionResult.getErrorCode());
	//			}
	//		}
	//
	//	};
	//
	//	public void setupLocationClient(Context context) {
	//		this.activity = (Activity) context;
	//		this.context = context;
	//		mLocationRequest = LocationRequest.create();
	//		mLocationRequest.setInterval(5000); //5 Seconds
	//		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	//		mLocationRequest.setFastestInterval(1000); //1 second
	//		mUpdatesRequested = true;
	//
	//		mLocationClient = new LocationClient(context, connectionCallback, onConnectionFailedListener);
	//		mLocationClient.connect();
	//	}
	//
	//	public LatLng getLatestLocation() {
	//
	//		LatLng latLng = getDefaultLocationLatLng(context);
	//
	//		if (isServicesConnected(context)) {
	//			// Get the current location
	//			Location currentLocation = mLocationClient.getLastLocation();
	//			if (currentLocation != null) {
	//				latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
	//			}
	//		}
	//
	//		return latLng;
	//	}
	//
	//	public static boolean isServicesConnected(Context context) {
	//		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
	//		if (ConnectionResult.SUCCESS == resultCode) {
	//			Log.d(TAG, "Connected");
	//			return true;
	//		} else {
	//			return false;
	//		}
	//	}
	//
	//	private void showErrorDialog(int errorCode) {
	//
	//		// Get the error dialog from Google Play services
	//		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
	//
	//		// If Google Play services can provide an error dialog
	//		if (errorDialog != null) {
	//
	//			errorDialog.show();
	//		}
	//	}
	//
	//	/**
	//	 * Define a DialogFragment to display the error dialog generated in
	//	 * showErrorDialog.
	//	 */
	//	public static class ErrorDialogFragment extends DialogFragment {
	//
	//		// Global field to contain the error dialog
	//		private Dialog mDialog;
	//
	//		/**
	//		 * Default constructor. Sets the dialog field to null
	//		 */
	//		public ErrorDialogFragment() {
	//			super();
	//			mDialog = null;
	//		}
	//
	//		/**
	//		 * Set the dialog to display
	//		 * 
	//		 * @param dialog
	//		 *            An error dialog
	//		 */
	//		public void setDialog(Dialog dialog) {
	//			mDialog = dialog;
	//		}
	//
	//		/*
	//		 * This method must return a Dialog to the DialogFragment.
	//		 */
	//		@Override
	//		public Dialog onCreateDialog(Bundle savedInstanceState) {
	//			return mDialog;
	//		}
	//	}
	//
	//	public LocationHelper(Context context) {
	//		setupLocationClient(context);
	//	}

	Timer timer1;
	LocationManager lm;
	LocationResult locationResult;
	boolean gps_enabled = false;
	boolean network_enabled = false;

	public boolean getLocation(Context context, LocationResult result) {
		//I use LocationResult callback class to pass location value from MyLocation to user code.
		locationResult = result;
		if (lm == null)
			lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		//exceptions will be thrown if provider is not permitted.
		try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		try {
			network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		//don't start listeners if no provider is enabled
		if (!gps_enabled && !network_enabled)
			return false;

		if (gps_enabled)
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
		if (network_enabled)
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
		timer1 = new Timer();
		timer1.schedule(new GetLastLocation(), 20000);
		return true;
	}

	LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer1.cancel();
			locationResult.gotLocation(new LatLng(location.getLatitude(), location.getLongitude()));
			lm.removeUpdates(this);
			lm.removeUpdates(locationListenerNetwork);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer1.cancel();

			locationResult.gotLocation(new LatLng(location.getLatitude(), location.getLongitude()));
			lm.removeUpdates(this);
			lm.removeUpdates(locationListenerGps);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	class GetLastLocation extends TimerTask {
		@Override
		public void run() {
			lm.removeUpdates(locationListenerGps);
			lm.removeUpdates(locationListenerNetwork);

			Location net_loc = null, gps_loc = null;
			LatLng net, gps;
			if (gps_enabled) {
				gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}

			if (network_enabled) {
				net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}

			//if there are both values use the latest one
			if (gps_loc != null && net_loc != null) {
				if (gps_loc.getTime() > net_loc.getTime()) {
					gps = new LatLng(gps_loc.getLatitude(), gps_loc.getLongitude());
					locationResult.gotLocation(gps);
				} else {
					net = new LatLng(net_loc.getLatitude(), net_loc.getLongitude());
					locationResult.gotLocation(net);
				}

				return;
			}

			if (gps_loc != null) {
				gps = new LatLng(gps_loc.getLatitude(), gps_loc.getLongitude());
				locationResult.gotLocation(gps);
				return;
			}
			if (net_loc != null) {
				net = new LatLng(net_loc.getLatitude(), net_loc.getLongitude());
				locationResult.gotLocation(net);
				return;
			}
			locationResult.gotLocation(null);
		}
	}

	public static abstract class LocationResult {
		public abstract void gotLocation(LatLng latLng);
	}

}
