package com.dabeshackers.infor.gather.helpers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class PlacesHelper {
	public static ArrayList<Place> findPlaces(double latitude, double longitude, String placeSpacification, String apiKey) {
		try {
			
			String urlString = makeUrl(latitude, longitude, placeSpacification, apiKey);
			System.out.println(urlString);


			String json = getJSON(urlString);

			System.out.println(json);
			JSONObject object = new JSONObject(json);
			JSONArray array = object.getJSONArray("results");

			ArrayList<Place> arrayList = new ArrayList<Place>();
			for (int i = 0; i < array.length(); i++) {
				try {
					Place place = Place.jsonToPlace((JSONObject) array.get(i));
					Log.v("Places Services ", "" + place);
					arrayList.add(place);
				} catch (Exception e) {
				}
			}
			return arrayList;
		} catch (JSONException ex) {
			ex.printStackTrace();
			//			Logger.getLogger(PlacesService.class.getName()).log(Level.SEVERE,
			//					null, ex);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	// https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=apikey
	private static String makeUrl(double latitude, double longitude, String place, String apiKey) throws UnsupportedEncodingException {
		StringBuilder urlString = new StringBuilder(
				"https://maps.googleapis.com/maps/api/place/search/json?");

		if (place.equals("")) {
			urlString.append("&location=");
			urlString.append(Double.toString(latitude));
			urlString.append(",");
			urlString.append(Double.toString(longitude));
//			urlString.append("&radius=10000");
			urlString.append("&rankby=distance");
			// urlString.append("&types="+place);
			urlString.append("&sensor=false&key=" + apiKey);
		} else {
			urlString.append("&location=");
			urlString.append(Double.toString(latitude));
			urlString.append(",");
			urlString.append(Double.toString(longitude));
//			urlString.append("&radius=10000");
			urlString.append("&rankby=distance");
			urlString.append("&keyword=" + URLEncoder.encode(place,"UTF-8"));
			urlString.append("&sensor=false&key=" + apiKey);
		}
		return urlString.toString();
	}

	protected static String getJSON(String url) {
		return getUrlContents(url);
	}

	private static String getUrlContents(String theUrl) {
		StringBuilder content = new StringBuilder();
		try {
			URL url = new URL(theUrl);
			URLConnection urlConnection = url.openConnection();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(urlConnection.getInputStream()), 8);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				content.append(line + "\n");
			}
			bufferedReader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return content.toString();
	}

	public static class Place {
		private String id;
		private String icon;
		private String name;
		private String vicinity;
		
		public Place(String id, String icon, String name, String vicinity, Double latitude, Double longitude) {
			super();
			this.id = id;
			this.icon = icon;
			this.name = name;
			this.vicinity = vicinity;
			this.latitude = latitude;
			this.longitude = longitude;
		}
		
		public Place() {
			super();
			this.id = null;
			this.icon = null;
			this.name = null;
			this.vicinity = null;
			this.latitude = 0d;
			this.longitude = 0d;
		}

		private Double latitude;
		private Double longitude;

		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getIcon() {
			return icon;
		}
		public void setIcon(String icon) {
			this.icon = icon;
		}
		public Double getLatitude() {
			return latitude;
		}
		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}
		public Double getLongitude() {
			return longitude;
		}
		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getVicinity() {
			return vicinity;
		}
		public void setVicinity(String vicinity) {
			this.vicinity = vicinity;
		}

		static Place jsonToPlace(JSONObject pointOfreference) {
			try {
				Place result = new Place();
				JSONObject geometry = (JSONObject) pointOfreference.get("geometry");
				JSONObject location = (JSONObject) geometry.get("location");
				result.setLatitude((Double) location.get("lat"));
				result.setLongitude((Double) location.get("lng"));
				result.setIcon(pointOfreference.getString("icon"));
				result.setName(pointOfreference.getString("name"));
				result.setVicinity(pointOfreference.getString("vicinity"));
				result.setId(pointOfreference.getString("id"));
				return result;
			} catch (JSONException ex) {
				Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
			}
			return null;
		}

		@Override
		public String toString() {
			return "Place{" + "id=" + id + ", icon=" + icon + ", name=" + name + ", latitude=" + latitude + ", longitude=" + longitude + '}';
		}

	}
}

