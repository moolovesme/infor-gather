package com.dabeshackers.infor.gather;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.brickred.socialauth.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.MenuItem;
import com.dabeshackers.infor.gather.application.AppMain;
import com.dabeshackers.infor.gather.application.ApplicationUtils;
import com.dabeshackers.infor.gather.entities.User;
import com.dabeshackers.infor.gather.helpers.BitmapHelper;
import com.dabeshackers.infor.gather.helpers.HashHelper;
import com.dabeshackers.infor.gather.helpers.ToastHelper;
import com.google.android.gms.maps.model.LatLng;

public class MainContainerActivity extends SherlockFragmentActivity {

	private static final String TAG = MainContainerActivity.class.getSimpleName();
	private AppMain appMain;
	private User currentUser;
	private LatLng currentLatLng;
	private String currentLocationText;

	private RelativeLayout top_layout;

	ProgressDialog pd;

	// Drawer Fields
	private DrawerLayout mDrawerLayout;
	private FrameLayout mDrawerContainer;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	int[] icon;
	public boolean isUpdateProfileRequired = false;
	private String[] mDrawerItems;
	private ListView mDrawerList;

	// End Drawer Fields

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		appMain = (AppMain) getApplication();
		currentUser = appMain.getCurrentUser();

		setContentView(R.layout.app_main);

		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				// Show Progress Dialog
				pd = new ProgressDialog(MainContainerActivity.this);
				pd.setIndeterminate(true);
				pd.setMessage("Waiting for location...");
				pd.setTitle("Please wait.");
				pd.setCancelable(false);
				//				pd.show();
			}

		});

		setUpDrawer(savedInstanceState);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		mDrawerToggle.syncState();
	}

	private void setUpDrawer(Bundle savedInstanceState) {

		//		mDrawerTitle = getString(R.string.lbl_drawer_merchant);
		mTitle = mDrawerTitle = getTitle() + " Quick Access";
		mDrawerContainer = (FrameLayout) findViewById(R.id.left_drawer_cont);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mDrawerItems = getResources().getStringArray(R.array.drawer_items);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.menulist);
		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerItems));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Set Drawer Identity
		final Profile p = appMain.getLoggedInProfile();
		final TextView fullname = (TextView) mDrawerLayout.findViewById(R.id.fullname);
		final TextView email = (TextView) mDrawerLayout.findViewById(R.id.email);
		final ImageView profilepic = (ImageView) mDrawerLayout.findViewById(R.id.profilepic);

		fullname.setText(p.getFirstName().toUpperCase(Locale.getDefault()) + " " + p.getLastName().toUpperCase(Locale.getDefault()));
		fullname.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				isUpdateProfileRequired = false;
				Intent intent = new Intent(MainContainerActivity.this, EditUserActivity.class);
				intent.putExtra("lat", currentLatLng.latitude);
				intent.putExtra("lng", currentLatLng.longitude);
				intent.putExtra("loc_text", currentLocationText);
				startActivityForResult(intent, EditUserActivity.REQUESTCODE);
			}
		});
		email.setText(p.getEmail());
		email.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				isUpdateProfileRequired = false;
				Intent intent = new Intent(MainContainerActivity.this, EditUserActivity.class);
				intent.putExtra("lat", currentLatLng.latitude);
				intent.putExtra("lng", currentLatLng.longitude);
				intent.putExtra("loc_text", currentLocationText);
				startActivityForResult(intent, EditUserActivity.REQUESTCODE);
			}
		});

		profilepic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				isUpdateProfileRequired = false;
				Intent intent = new Intent(MainContainerActivity.this, EditUserActivity.class);
				intent.putExtra("lat", currentLatLng.latitude);
				intent.putExtra("lng", currentLatLng.longitude);
				intent.putExtra("loc_text", currentLocationText);
				startActivityForResult(intent, EditUserActivity.REQUESTCODE);
			}
		});

		final File f = new File(ApplicationUtils.Paths.getLocalAppImagesFolder(MainContainerActivity.this), HashHelper.md5(p.getEmail()) + ApplicationUtils.IMAGE_EXTENSION);

		if (f.exists()) {
			// Load cached copy
			profilepic.setImageBitmap(BitmapHelper.decodeSampledBitmapFromFile(f.getPath(), 100, 100));

			// Attempt to refresh from cloud
			retrieveProfilePic(p, profilepic, f);
		} else {
			// Attempt to refresh from cloud
			retrieveProfilePic(p, profilepic, f);
		}

		if (savedInstanceState == null) {
			selectItem(0);
		}

		//		mDrawerLayout.openDrawer(mDrawerContainer);

	}

	private void retrieveProfilePic(final Profile p, final ImageView profilepic, final File f) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				URL url;
				try {
					if (p.getProfileImageURL() == null) {
						return;
					}
					url = new URL(p.getProfileImageURL());
					try {
						org.apache.commons.io.FileUtils.copyURLToFile(url, f);
					} catch (IOException e) {
						BitmapHelper.createDefaultImage(getResources(), "no_image", f.getAbsolutePath());
					}

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							profilepic.setImageBitmap(BitmapHelper.decodeSampledBitmapFromFile(f.getPath(), 100, 100));
						}

					});

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}).start();
	}

	// ListView click listener in the navigation drawer
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {

		FragmentManager fragmentManager = getSupportFragmentManager();
		SherlockListFragment listfragment = null;
		SherlockFragment fragment = null;
		String title = null;

		//use position parameter to do your thing
		switch (position) {
		case 0: //Events
			listfragment = new EventsFragment();
			title = EventsFragment.TITLE;
			fragmentManager.beginTransaction().replace(R.id.content_frame, listfragment).commit();
			break;

		case 1: //Your Events
			listfragment = new EventsFragment();
			title = EventsFragment.TITLE;
			fragmentManager.beginTransaction().replace(R.id.content_frame, listfragment).commit();
			break;

		case 2: //Subscribed Events
			listfragment = new EventsFragment();
			title = EventsFragment.TITLE;
			fragmentManager.beginTransaction().replace(R.id.content_frame, listfragment).commit();
			break;

		case 3: //Alerts
			fragment = new InventoryFragment();
			title = InventoryFragment.TITLE;
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
			break;

		case 4: //Preferences
			fragment = new PosFragment();
			title = PosFragment.TITLE;
			break;

		case 5: //Help
			ToastHelper.toast(MainContainerActivity.this, "Help", Toast.LENGTH_SHORT);
			break;

		case 6: //Logout
			ToastHelper.toast(MainContainerActivity.this, "Exit", Toast.LENGTH_SHORT);
			break;

		}

		if (fragment != null) {
			// Insert the fragment by replacing any existing fragment

		}

		//setItemChecked
		mDrawerList.setItemChecked(position, true);

		if (title != null && title.length() > 0) {
			mTitle = title;
			getSupportActionBar().setTitle(mTitle);
		}

		// Close drawer
		// mDrawerLayout.closeDrawer(mDrawerList); //use this if drawer only contains list
		mDrawerLayout.closeDrawer(mDrawerContainer);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(mDrawerContainer)) {
				mDrawerLayout.closeDrawer(mDrawerContainer);
			} else {
				mDrawerLayout.openDrawer(mDrawerContainer);
			}
			return true;
		}
		return false; //Return false so that the fragments' onOptionsItemSelected() handler inside this activity will be called
	}

	//	private boolean isNotFirstTime() {
	//
	//		SharedPreferences preferences = ApplicationUtils.getSharedPreferences(MainContainerActivity.this);// getPreferences(MODE_PRIVATE);
	//		// Set to false to always show guide overlay
	//		boolean ranBefore = preferences.getBoolean("RanBefore", false);
	//		if (!ranBefore) {
	//
	//			SharedPreferences.Editor editor = preferences.edit();
	//			editor.putBoolean("RanBefore", true);
	//			editor.commit();
	//			top_layout.setVisibility(View.VISIBLE);
	//			top_layout.setOnTouchListener(new View.OnTouchListener() {
	//
	//				@Override
	//				public boolean onTouch(View v, MotionEvent event) {
	//					top_layout.setVisibility(View.INVISIBLE);
	//					return false;
	//				}
	//
	//			});
	//
	//		}
	//		return ranBefore;
	//
	//	}

	private boolean doubleBackToExitPressedOnce = false;

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			setResult(RESULT_CANCELED);
			MainContainerActivity.super.onBackPressed();
			return;
		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {

			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000);

	}

}