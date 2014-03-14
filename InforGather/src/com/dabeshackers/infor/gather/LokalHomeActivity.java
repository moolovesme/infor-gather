package com.dabeshackers.infor.gather;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.SocialAuthAdapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.dabeshackers.infor.gather.R;
import com.dabeshackers.infor.gather.application.AppMain;
import com.dabeshackers.infor.gather.application.ApplicationUtils;
import com.dabeshackers.infor.gather.entities.Offer;
import com.dabeshackers.infor.gather.entities.User;
import com.dabeshackers.infor.gather.helpers.BitmapHelper;
import com.dabeshackers.infor.gather.helpers.FileOperationsHelper;
import com.dabeshackers.infor.gather.helpers.HashHelper;
import com.dabeshackers.infor.gather.helpers.LocationHelper;
import com.dabeshackers.infor.gather.helpers.LocationHelper.LocationResult;
import com.dabeshackers.infor.gather.http.LokalWebService;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class LokalHomeActivity extends SherlockListActivity {

	private static final String TAG = LokalHomeActivity.class.getSimpleName();
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private int mActivatedPosition = ListView.INVALID_POSITION;
	private AppMain appMain;

	private static final String TITLE_BUYER_OFFERS_BROWSE = "Newest Offers";
	private static final String TITLE_MERCHANT_OFFERS_PUBLISHED = "Your Offers";

	public static final String OPERATION_MODE = "mode";
	public static final int OPERATION_MODE_BUYER = 0;
	public static final int OPERATION_MODE_MERCHANT = 1;
	private static int CURRENT_OPERATION_MODE;

	public static final int MERCHANT_OFFER_TYPE_PUBLISHED = 0;
	public static final int MERCHANT_OFFER_TYPE_DRAFTS = 1;
	private static int MERCHANT_OFFER_TYPE;

	private int dbCurrentRow = 0;
	private static final int DB_ROW_LIMIT = 5;

	private User currentUser;
	private LatLng currentLatLng;
	private String currentLocationText;

	private boolean isSearchEnabled;

	private List<Offer> items = new ArrayList<Offer>();
	private List<Offer> filtered_items = new ArrayList<Offer>();

	SocialAuthAdapter socialAdapter;

	private MyCustomAdapter adapter;
	EditText filterText;
	ImageButton clearBtn;

	private boolean isLoading;
	private boolean isLoadingPrevious;

	private RelativeLayout top_layout;

	ProgressDialog pd;
	GridView gridView;
	Button search;

	boolean isUpdateProfileRequired;
	//	boolean loadingMore;

	// Drawer Fields
	private DrawerLayout mDrawerLayout;
	private ScrollView mDrawerContainer;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	int[] icon;

	// End Drawer Fields

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		appMain = (AppMain) getApplication();
		currentUser = appMain.getCurrentUser();

		if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(OPERATION_MODE)) {
			CURRENT_OPERATION_MODE = getIntent().getExtras().getInt(OPERATION_MODE);
		}

		switch (CURRENT_OPERATION_MODE) {
		case OPERATION_MODE_MERCHANT:
			MERCHANT_OFFER_TYPE = MERCHANT_OFFER_TYPE_PUBLISHED;
			setTitle(TITLE_MERCHANT_OFFERS_PUBLISHED);
			setContentView(R.layout.app_main_merchant);
			break;
		case OPERATION_MODE_BUYER:
			setTitle(TITLE_BUYER_OFFERS_BROWSE);
			setContentView(R.layout.app_main);
			break;
		default:
			setContentView(R.layout.app_main);
			break;
		}

		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				// Show Progress Dialog
				pd = new ProgressDialog(LokalHomeActivity.this);
				pd.setIndeterminate(true);
				pd.setMessage("Waiting for location...");
				pd.setTitle("Please wait.");
				pd.setCancelable(false);
				pd.show();
			}

		});

		setUpDrawer(savedInstanceState);

		// top_layout = (RelativeLayout) findViewById(R.id.top_layout);
		// if (isNotFirstTime()) {
		// top_layout.setVisibility(View.INVISIBLE);
		// }

		View footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listfooterview, null, false);
		final ImageView animImageView = (ImageView) footerView.findViewById(R.id.loading);
		animImageView.setBackgroundResource(R.drawable.loader);
		animImageView.post(new Runnable() {
			@Override
			public void run() {
				AnimationDrawable frameAnimation = (AnimationDrawable) animImageView.getBackground();
				frameAnimation.start();
			}
		});
		getListView().addFooterView(footerView);

		//Add bottom scrolling
		getListView().setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				//what is the bottom item that is visible
				int lastInScreen = firstVisibleItem + visibleItemCount;
				//is the bottom item visible & not loading more already ? Load more !
				if ((lastInScreen == totalItemCount) && !isLoadingPrevious) {
					// Perform sync then refresh
					new Thread(new Runnable() {

						public void run() {
							isLoadingPrevious = true;

							appMain.requestSync();
							fillData(FILLDATA_GET_NEXT_SET);

							//Inform UI thread of the changes and update UI as needed
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									adapter.notifyDataSetChanged();
									//									loadingMore = false;
									isLoadingPrevious = false;
								}

							});
						}

					}).start();
				}
			}
		});

		//Pull to refresh enablement
		((PullToRefreshListView) getListView()).setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (isLoading) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							((PullToRefreshListView) getListView()).onRefreshComplete();
						}
					});
					return;

				}
				//Do work to refresh the list here.
				//Peform retrieval from DB
				setProgressBarIndeterminateVisibility(Boolean.TRUE);
				isLoading = true;

				new Thread(new Runnable() {

					public void run() {
						dbCurrentRow = 0; //reset row counter
						appMain.requestSync();
						fillData(FILLDATA_REQUERY);

						//Inform UI thread of the changes and update UI as needed
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								adapter.notifyDataSetChanged();
								((PullToRefreshListView) getListView()).onRefreshComplete();
							}

						});
					}

				}).start();
			}
		});

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		mDrawerToggle.syncState();
	}

	private void setUpDrawer(Bundle savedInstanceState) {
		switch (CURRENT_OPERATION_MODE) {
		case OPERATION_MODE_MERCHANT:
			mDrawerTitle = getString(R.string.lbl_drawer_merchant);
			break;

		case OPERATION_MODE_BUYER:
			mDrawerTitle = getString(R.string.lbl_drawer_buyer);
			break;

		default:
			break;
		}

		mTitle = getTitle();
		mDrawerContainer = (ScrollView) findViewById(R.id.left_drawer_cont);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				// TODO Auto-generated method stub
				getSupportActionBar().setTitle(mTitle);
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				// TODO Auto-generated method stub
				// Set the title on the action when drawer open
				getSupportActionBar().setTitle(mDrawerTitle);
				super.onDrawerOpened(drawerView);
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Set Drawer Identity
		final Profile p = appMain.getLoggedInProfile();
		final TextView fullname = (TextView) mDrawerLayout.findViewById(R.id.fullname);
		final TextView email = (TextView) mDrawerLayout.findViewById(R.id.email);
		final ImageView profilepic = (ImageView) mDrawerLayout.findViewById(R.id.profilepic);
		final ImageButton perspective = (ImageButton) mDrawerLayout.findViewById(R.id.perspective);

		perspective.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mDrawerLayout.closeDrawer(mDrawerContainer);

				SharedPreferences.Editor editor = ApplicationUtils.getSharedPreferencesEditor(LokalHomeActivity.this);
				Intent intent;
				switch (CURRENT_OPERATION_MODE) {
				case OPERATION_MODE_MERCHANT:
					editor.putInt(ApplicationUtils.PREFS_LAST_OPERATION_MODE_USED, OPERATION_MODE_BUYER);
					editor.commit();

					intent = new Intent(getApplicationContext(), LokalHomeActivity.class);
					intent.putExtra(LokalHomeActivity.OPERATION_MODE, OPERATION_MODE_BUYER);
					startActivity(intent);
					finish();

					break;

				case OPERATION_MODE_BUYER:
					editor.putInt(ApplicationUtils.PREFS_LAST_OPERATION_MODE_USED, OPERATION_MODE_MERCHANT);
					editor.commit();

					intent = new Intent(getApplicationContext(), LokalHomeActivity.class);
					intent.putExtra(LokalHomeActivity.OPERATION_MODE, OPERATION_MODE_MERCHANT);
					startActivity(intent);
					finish();

					break;

				default:
					break;
				}
			}
		});
		fullname.setText(p.getFirstName().toUpperCase(Locale.ENGLISH) + " " + p.getLastName().toUpperCase(Locale.ENGLISH));
		fullname.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				isUpdateProfileRequired = false;
				Intent intent = new Intent(LokalHomeActivity.this, EditUserActivity.class);
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
				Intent intent = new Intent(LokalHomeActivity.this, EditUserActivity.class);
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
				Intent intent = new Intent(LokalHomeActivity.this, EditUserActivity.class);
				intent.putExtra("lat", currentLatLng.latitude);
				intent.putExtra("lng", currentLatLng.longitude);
				intent.putExtra("loc_text", currentLocationText);
				startActivityForResult(intent, EditUserActivity.REQUESTCODE);
			}
		});

		final File f = new File(ApplicationUtils.Paths.getLocalAppImagesFolder(LokalHomeActivity.this), HashHelper.md5(p.getEmail()) + ApplicationUtils.IMAGE_EXTENSION);

		if (f.exists()) {
			// Load cached copy
			profilepic.setImageBitmap(BitmapHelper.decodeSampledBitmapFromFile(f.getPath(), 100, 100));

			// Attempt to refresh from cloud
			retrieveProfilePic(p, profilepic, f);
		} else {
			// Attempt to refresh from cloud
			retrieveProfilePic(p, profilepic, f);
		}

		switch (CURRENT_OPERATION_MODE) {
		case OPERATION_MODE_BUYER:
			setupBuyerButtonListeners();
			break;

		case OPERATION_MODE_MERCHANT:
			setupMerchantButtonListeners();
			break;

		default:
			break;
		}

		// Set current location
		LocationHelper myLocation = new LocationHelper();
		//		updateProgressDialog("Waiting for location...");
		myLocation.getLocation(this, new LocationResult() {

			@Override
			public void gotLocation(LatLng latLng) {
				if (latLng != null) {
					currentLatLng = latLng;
				} else {
					currentLatLng = LocationHelper.getDefaultLocationLatLng(LokalHomeActivity.this);
				}
				currentLocationText = LocationHelper.getReadableAddressFromLatLng(LokalHomeActivity.this, currentLatLng, false);
				final Button location = (Button) mDrawerLayout.findViewById(R.id.btnLocation);
				if (location != null) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							location.setText(currentLocationText == null ? "Anywhere" : currentLocationText);
						}
					});
				}

				// Determine if first time user or not -- letting user modify and pushing it to host if so.
				// currentUser = appMain.getCurrentUser(); this is set on the earlier part of this method
				new Thread(new Runnable() {

					@Override
					public void run() {
						if (LokalWebService.Users.isUserExists(LokalHomeActivity.this, currentUser.getId())) {
							currentUser = LokalWebService.Users.getUser(LokalHomeActivity.this, currentUser);

						} else {
							isUpdateProfileRequired = true;
							Intent intent = new Intent(LokalHomeActivity.this, EditUserActivity.class);
							intent.putExtra("lat", currentLatLng.latitude);
							intent.putExtra("lng", currentLatLng.longitude);
							intent.putExtra("loc_text", currentLocationText);
							startActivityForResult(intent, EditUserActivity.REQUESTCODE);
						}

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// mDrawerLayout.closeDrawer(mDrawerContainer);
								//Update drawer values
								updateProfileName();

								//Finally, start getting some items
								if (items.isEmpty() && !isLoading) {
									Log.i(TAG, "Retrieving records");
									setProgressBarIndeterminateVisibility(Boolean.TRUE);
									isLoading = true;
									appMain.requestSync();
									fillData(FILLDATA_REQUERY);

								}

								pd.dismiss();

							}
						});

					}

				}).start();

			}
		});

		mDrawerLayout.openDrawer(mDrawerContainer);

	}

	private void updateProfileName() {

		String profileName = null;

		switch (CURRENT_OPERATION_MODE) {
		case OPERATION_MODE_MERCHANT:
			profileName = (currentUser.getTradeName() == null ? "" : currentUser.getTradeName()).toUpperCase(Locale.ENGLISH);
			break;

		case OPERATION_MODE_BUYER:
			profileName = (currentUser.getFirstName() == null ? "" : currentUser.getFirstName() + " " + currentUser.getLastName() == null ? "" : currentUser.getLastName()).toUpperCase(Locale.ENGLISH);
			break;

		default:
			break;
		}

		final TextView fullname = (TextView) mDrawerLayout.findViewById(R.id.fullname);
		fullname.setText(profileName);
	}

	private void setupMerchantButtonListeners() {
		final Button promote = (Button) mDrawerLayout.findViewById(R.id.btnPromote);
		promote.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawerLayout.closeDrawer(mDrawerContainer);
				// New Offer process here
				Intent intent = new Intent(LokalHomeActivity.this, NewOfferActivity.class);
				intent.putExtra("lat", currentLatLng.latitude);
				intent.putExtra("lng", currentLatLng.longitude);
				intent.putExtra("loc_text", currentLocationText);
				startActivityForResult(intent, NewOfferActivity.NEW_OFFER_REQUEST_CODE);
			}
		});

		final Button published = (Button) mDrawerLayout.findViewById(R.id.published_offers);
		published.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				publishedButtonClicked();
			}
		});

		final Button drafts = (Button) mDrawerLayout.findViewById(R.id.draft_offers);
		drafts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				draftsButtonClicked();
			}
		});
	}

	protected void draftsButtonClicked() {
		dbCurrentRow = 0; //reset row counter
		mDrawerLayout.closeDrawer(mDrawerContainer);
		MERCHANT_OFFER_TYPE = MERCHANT_OFFER_TYPE_DRAFTS;
		setProgressBarIndeterminateVisibility(Boolean.TRUE);
		isLoading = true;

		// Perform sync then refresh
		new Thread(new Runnable() {

			public void run() {
				appMain.requestSync();
				fillData(FILLDATA_REQUERY);
			}

		}).start();
	}

	protected void publishedButtonClicked() {
		dbCurrentRow = 0; //reset row counter
		mDrawerLayout.closeDrawer(mDrawerContainer);
		MERCHANT_OFFER_TYPE = MERCHANT_OFFER_TYPE_PUBLISHED;
		setProgressBarIndeterminateVisibility(Boolean.TRUE);
		isLoading = true;

		// Perform sync then refresh
		new Thread(new Runnable() {

			public void run() {
				appMain.requestSync();
				fillData(FILLDATA_REQUERY);
			}

		}).start();
	}

	private void setupBuyerButtonListeners() {
		// Set Drawer listeners
		final Button location = (Button) mDrawerLayout.findViewById(R.id.btnLocation);
		final Button search = (Button) mDrawerLayout.findViewById(R.id.btnSearch);
		final Button browse = (Button) mDrawerLayout.findViewById(R.id.btnViewOffers);

		location.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				locationButtonClicked();
			}
		});

		browse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				browseButtonClicked();
			}
		});

		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchButtonClicked();
			}
		});
		// End Drawer Listeners
	}

	protected void searchButtonClicked() {
		dbCurrentRow = 0; //reset row counter
		mDrawerLayout.closeDrawer(mDrawerContainer);
		fillData(FILLDATA_REQUERY);
	}

	protected void browseButtonClicked() {
		dbCurrentRow = 0; //reset row counter
		mDrawerLayout.closeDrawer(mDrawerContainer);
		fillData(FILLDATA_REQUERY);
	}

	protected void locationButtonClicked() {
		// Show location picker here
		Intent intent = new Intent(LokalHomeActivity.this, LocationPicker.class);
		intent.putExtra("lat", currentLatLng.latitude);
		intent.putExtra("lng", currentLatLng.longitude);
		startActivityForResult(intent, LocationPicker.REQUESTCODE);
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
						//Use old, probably outdated image if existing.
						if (!f.exists()) {
							BitmapHelper.createDefaultImage(getResources(), "no_image", f.getAbsolutePath());
						}
					}

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							profilepic.setImageBitmap(BitmapHelper.decodeSampledBitmapFromFile(f.getPath(), 100, 100));
						}

					});

				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}

		}).start();
	}

	private static final int FILLDATA_REQUERY = 0;
	private static final int FILLDATA_GET_NEXT_SET = 1;
	private static final int FILLDATA_USE_FILTERED_ITEMS = 2;
	private static final int FILLDATA_USE_UNFILTERED_ITEMS = 3;

	private void fillData(int fillType) {
		try {
			switch (fillType) {
			case FILLDATA_GET_NEXT_SET:

				List<Offer> newOffers = new ArrayList<Offer>();

				// Webservice calls are done here
				switch (CURRENT_OPERATION_MODE) {
				case OPERATION_MODE_MERCHANT:

					switch (MERCHANT_OFFER_TYPE) {
					case MERCHANT_OFFER_TYPE_PUBLISHED:
						newOffers = LokalWebService.Offers.fetchRecordsByUserId(LokalHomeActivity.this, currentUser.getId(), false, dbCurrentRow);
						break;
					case MERCHANT_OFFER_TYPE_DRAFTS:
						newOffers = LokalWebService.Offers.fetchRecordsByUserId(LokalHomeActivity.this, currentUser.getId(), true, dbCurrentRow);
						break;
					default:
						break;
					}

					break;
				case OPERATION_MODE_BUYER:
					newOffers = LokalWebService.Offers.fetchRecordsWithinRange(LokalHomeActivity.this, currentLatLng, dbCurrentRow);
					break;
				default:
					break;
				}

				if (newOffers != null && newOffers.size() > 0) {
					items.addAll(newOffers);
					dbCurrentRow += DB_ROW_LIMIT;
				}

				// Load adapter to list and Disable loading animation in actionbar
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setProgressBarIndeterminateVisibility(Boolean.FALSE);
						isLoading = false;
					}
				});

				break;

			case FILLDATA_REQUERY:
				setProgressBarIndeterminateVisibility(Boolean.TRUE);

				new Thread(new Runnable() {

					@Override
					public void run() {
						// Webservice calls are done here
						switch (CURRENT_OPERATION_MODE) {
						case OPERATION_MODE_MERCHANT:

							switch (MERCHANT_OFFER_TYPE) {
							case MERCHANT_OFFER_TYPE_PUBLISHED:
								items = LokalWebService.Offers.fetchRecordsByUserId(LokalHomeActivity.this, currentUser.getId(), false, dbCurrentRow);
								dbCurrentRow += DB_ROW_LIMIT;
								break;
							case MERCHANT_OFFER_TYPE_DRAFTS:
								//								setViewTitle(TITLE_MERCHANT_OFFERS_DRAFTS);
								items = LokalWebService.Offers.fetchRecordsByUserId(LokalHomeActivity.this, currentUser.getId(), true, dbCurrentRow);
								dbCurrentRow += DB_ROW_LIMIT;
								break;
							default:
								break;
							}

							break;
						case OPERATION_MODE_BUYER:
							items = LokalWebService.Offers.fetchRecordsWithinRange(LokalHomeActivity.this, currentLatLng, dbCurrentRow);
							dbCurrentRow += DB_ROW_LIMIT;
							break;
						default:
							break;
						}

						if (items == null || items.size() == 0) {
							// No items retrieved due to some reason. hence, we use cached data
							try {
								retrieveCache();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						adapter = new MyCustomAdapter(getApplicationContext(), R.layout.activity_activities_list_row, items);

						// Load adapter to list and Disable loading animation in actionbar
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								setListAdapter(adapter);
								setProgressBarIndeterminateVisibility(Boolean.FALSE);
								isLoading = false;
							}
						});
					}

				}).start();

				break;

			case FILLDATA_USE_UNFILTERED_ITEMS:

				adapter = new MyCustomAdapter(getApplicationContext(), R.layout.activity_activities_list_row, items);
				setListAdapter(adapter);
				setProgressBarIndeterminateVisibility(Boolean.FALSE);

				break;

			case FILLDATA_USE_FILTERED_ITEMS:

				adapter = new MyCustomAdapter(getApplicationContext(), R.layout.activity_activities_list_row, filtered_items);
				setListAdapter(adapter);
				setProgressBarIndeterminateVisibility(Boolean.FALSE);

				break;

			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mActivatedPosition != ListView.INVALID_POSITION) {
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}

		createCache();

	}

	public void setActivateOnItemClick(boolean activateOnItemClick) {
		getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
	}

	public void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	public class MyCustomAdapter extends ArrayAdapter<Offer> {

		List<Offer> objects;

		public MyCustomAdapter(Context context, int textViewResourceId, List<Offer> objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = getLayoutInflater();

			final Offer item = objects.get(position);

			convertView = inflater.inflate(R.layout.activity_offers_list_row, parent, false);

			final Button overflow = (Button) convertView.findViewById(R.id.overflow);
			final ImageView media = (ImageView) convertView.findViewById(R.id.media);
			final TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
			final TextView txtPrice = (TextView) convertView.findViewById(R.id.price);
			final TextView txtMerchant = (TextView) convertView.findViewById(R.id.merchant);
			final TextView txtContent = (TextView) convertView.findViewById(R.id.content);
			final TextView txtAddress = (TextView) convertView.findViewById(R.id.address);
			final TextView txtExpiration = (TextView) convertView.findViewById(R.id.expiration);

			txtTitle.setText(item.getTitle());
			txtPrice.setText("Php" + item.getFormattedprice_discounted());
			txtMerchant.setText("By: " + item.getMerchant());
			txtContent.setText(item.getDescription());
			txtAddress.setText(item.getLoc_text());

			if (CURRENT_OPERATION_MODE == OPERATION_MODE_MERCHANT && MERCHANT_OFFER_TYPE == MERCHANT_OFFER_TYPE_DRAFTS) {
				txtExpiration.setTextColor(getResources().getColor(R.color.facebook_blue));
				txtExpiration.setText("- DRAFT -");
			} else {
				txtExpiration.setText("Expires on: " + new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH).format(item.getCreated() + (1000 * 60 * 60 * 24 * 3)));
			}

			// Load image
			if (item.getImagesList() != null && item.getImagesList().size() > 0) {

				Bitmap b = item.getCurrentImageFromList();
				media.setImageBitmap(b);

				final int delaySecondsMin = 10;
				final int delaySecondsMax = 20;

				final Runnable timedAnimation = new Runnable() {

					@Override
					public void run() {
						final Animation animationFadeIn = AnimationUtils.loadAnimation(LokalHomeActivity.this, R.anim.fade_in);
						final Animation animationFadeOut = AnimationUtils.loadAnimation(LokalHomeActivity.this, R.anim.fade_out);

						AnimationListener animListener = new AnimationListener() {
							Bitmap bitmap;

							@Override
							public void onAnimationStart(Animation animation) {
								bitmap = item.getNextImageFromList(); // Was set here so that when the list is scrolled. we still have the latest image to display
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
							}

							@Override
							public void onAnimationEnd(Animation animation) {
								media.setImageBitmap(bitmap);
								media.startAnimation(animationFadeIn);
							}
						};
						animationFadeOut.setAnimationListener(animListener);
						media.startAnimation(animationFadeOut);

						media.postDelayed(this, (new Random().nextInt((delaySecondsMax - delaySecondsMin) + 1) + delaySecondsMin) * 1000);
					}
				};

				//Enable image animation for 2 or more images
				if (item.getImagesList().size() > 1) {
					media.postDelayed(timedAnimation, (new Random().nextInt((delaySecondsMax - delaySecondsMin) + 1) + delaySecondsMin) * 1000);
				}
			} else {
				media.setVisibility(View.GONE);
			}
			// End Load image

			// Click handler
			convertView.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					Intent intent = new Intent(LokalHomeActivity.this, ViewOfferActivity.class);
					intent.putExtra("item", item);
					startActivity(intent);
				}
			});

			final PopupMenu popup = new PopupMenu(LokalHomeActivity.this, overflow);
			//set overflow options
			if (CURRENT_OPERATION_MODE == OPERATION_MODE_MERCHANT) {
				overflow.setVisibility(View.VISIBLE);
				switch (MERCHANT_OFFER_TYPE) {

				case MERCHANT_OFFER_TYPE_PUBLISHED:
					popup.getMenuInflater().inflate(R.menu.activity_offer_list_merch_published, popup.getMenu());
					break;

				case MERCHANT_OFFER_TYPE_DRAFTS:
					popup.getMenuInflater().inflate(R.menu.activity_offer_list_merch_draft, popup.getMenu());
					break;

				default:
					break;
				}
			}
			popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(android.view.MenuItem menu) {
					switch (menu.getItemId()) {

					case R.id.menu_edit:
						editOffer(item);

						break;

					case R.id.menu_unpublish:
						//						ToastHelper.toast(LokalHomeActivity.this, "Unpublishing Offer...", Toast.LENGTH_SHORT);
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									item.createSnapShot();
									Offer snapShot = item.getSnapShot();
									snapShot.setStatus(Offer.OFFER_STATUS_DRAFT);
									snapShot.setUpdated(Calendar.getInstance().getTimeInMillis());
									snapShot.setVersion(snapShot.getVersion());
									if (LokalWebService.Offers.updateStatus(LokalHomeActivity.this, snapShot)) {
										item.setStatus(Offer.OFFER_STATUS_DRAFT);
										item.setUpdated(snapShot.getUpdated());
										item.setVersion(snapShot.getVersion());

										runOnUiThread(new Runnable() {

											@Override
											public void run() {
												AlertDialog.Builder alert = new AlertDialog.Builder(LokalHomeActivity.this);
												alert.setTitle("Successful");
												alert.setCancelable(false);
												alert.setMessage("The offer has been successfully moved to drafts.");
												alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {
														items.remove(item);
														adapter.notifyDataSetChanged();
														publishedButtonClicked();
													}
												});
												alert.setIcon(android.R.drawable.ic_dialog_info);
												alert.show();
											}

										});
									} else {
										runOnUiThread(new Runnable() {

											@Override
											public void run() {
												AlertDialog.Builder alert = new AlertDialog.Builder(LokalHomeActivity.this);
												alert.setTitle("Failed");
												alert.setMessage("Operation failed. Please try again later.");
												alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {

													}
												});
												alert.setIcon(android.R.drawable.ic_dialog_alert);
												alert.show();
											}

										});
									}
								} catch (CloneNotSupportedException e) {
									e.printStackTrace();
								}

							}
						}).start();
						break;

					case R.id.menu_publish:
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									item.createSnapShot();
									Offer snapShot = item.getSnapShot();
									snapShot.setStatus(Offer.OFFER_STATUS_PUBLISHED);
									snapShot.setUpdated(Calendar.getInstance().getTimeInMillis());
									snapShot.setVersion(snapShot.getVersion());
									if (LokalWebService.Offers.updateStatus(LokalHomeActivity.this, snapShot)) {
										item.setStatus(Offer.OFFER_STATUS_PUBLISHED);
										item.setUpdated(snapShot.getUpdated());
										item.setVersion(snapShot.getVersion());

										runOnUiThread(new Runnable() {

											@Override
											public void run() {
												AlertDialog.Builder alert = new AlertDialog.Builder(LokalHomeActivity.this);
												alert.setTitle("Successful");
												alert.setCancelable(false);
												alert.setMessage("The offer has been published successfully.");
												alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {
														items.remove(item);
														adapter.notifyDataSetChanged();
														draftsButtonClicked();
													}
												});
												alert.setIcon(android.R.drawable.ic_dialog_info);
												alert.show();
											}

										});
									} else {
										runOnUiThread(new Runnable() {

											@Override
											public void run() {
												AlertDialog.Builder alert = new AlertDialog.Builder(LokalHomeActivity.this);
												alert.setTitle("Failed");
												alert.setMessage("Operation failed. Please try again later.");
												alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {

													}
												});
												alert.setIcon(android.R.drawable.ic_dialog_alert);
												alert.show();
											}

										});
									}
								} catch (CloneNotSupportedException e) {
									e.printStackTrace();
								}

							}
						}).start();

						break;

					case R.id.menu_share:
						boolean withImage = false;
						File myFile = null;

						if (item.getImagesList() != null && item.getImagesList().size() > 0) {
							withImage = true;
							myFile = new File(item.getImagesList().get(0).getLocalFilePath());
						}

						final StringBuilder msg = new StringBuilder();
						msg.append("Hey, I've come across this offer. Check it out!\n");
						msg.append(item.getTitle() + " by " + item.getMerchant() + "\n");
						msg.append(item.getDescription() + "\n");
						msg.append("located at " + item.getLoc_text() + "\n");
						msg.append("for only " + item.getFormattedprice_discounted() + "\n");
						msg.append("visit " + item.getBiz_url() + " for more info.");

						final Intent intent = new Intent(Intent.ACTION_SEND);
						if (withImage) {
							//						intent.setType(type);
							intent.setType("image/jpeg");
							intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
							intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(myFile));
						} else {
							intent.setType("text/plain");
						}

						ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
						ClipData clip = ClipData.newPlainText("label", msg.toString());
						clipboard.setPrimaryClip(clip);

						intent.putExtra(Intent.EXTRA_TEXT, msg.toString());
						intent.putExtra(Intent.EXTRA_SUBJECT, "A cool offer via lokal.ph");
						intent.putExtra(Intent.EXTRA_TITLE, msg.toString());

						try {
							AlertDialog.Builder alert = new AlertDialog.Builder(LokalHomeActivity.this);
							alert.setTitle("Warning");
							alert.setMessage("Sharing to Facebook does not allow us to put anything on the caption field. Thus, we copied the share message to your clipboard. You can long press the caption field and select PASTE to share this offer to your friends and family.\n\nOther services are not affected by this limitation.\n\nFor more information, visit:\nhttps://developers.facebook.com/policy/");
							alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									startActivity(Intent.createChooser(intent, "Share using"));
								}
							});
							alert.setIcon(android.R.drawable.ic_dialog_alert);
							alert.show();

						} catch (android.content.ActivityNotFoundException ex) {
							// (handle error)
						}
						break;

					default:
						break;
					}

					return true;
				}

			});

			overflow.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popup.show();
				}
			});

			return convertView;
		}
	}

	private void editOffer(Offer item) {
		Intent intent = new Intent(LokalHomeActivity.this, NewOfferActivity.class);
		intent.putExtra("item", item);
		intent.putExtra("lat", currentLatLng.latitude);
		intent.putExtra("lng", currentLatLng.longitude);
		intent.putExtra("loc_text", currentLocationText);
		startActivityForResult(intent, NewOfferActivity.NEW_OFFER_REQUEST_CODE);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	EditText editsearch;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Get the options menu view from menu.xml in menu folder
		getSupportMenuInflater().inflate(R.menu.activity_lokal_home, menu);

		// Locate the EditText in menu.xml
		editsearch = (EditText) menu.findItem(R.id.menu_search_item).getActionView();

		// Capture Text in EditText
		editsearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				String text = s.toString().toLowerCase(Locale.getDefault());

				if (isSearchEnabled) {
					setProgressBarIndeterminateVisibility(Boolean.TRUE);
					filtered_items.clear();
					for (Offer offer : items) {
						if (offer.toString().toLowerCase(Locale.ENGLISH).contains(text)) {
							filtered_items.add(offer);
						}
					}

					fillData(FILLDATA_USE_FILTERED_ITEMS);
				} else {
					fillData(FILLDATA_USE_UNFILTERED_ITEMS);
				}

			}

		});

		// Show the search menu item in menu.xml
		MenuItem menuSearch = menu.findItem(R.id.menu_search_item);
		menuSearch.setOnActionExpandListener(new OnActionExpandListener() {

			// Menu Action Collapse
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				// Empty EditText to remove text filtering
				isSearchEnabled = false;
				editsearch.setText("");
				editsearch.clearFocus();

				return true;
			}

			// Menu Action Expand
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				isSearchEnabled = true;
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

		switch (item.getItemId()) {

		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(mDrawerContainer)) {
				mDrawerLayout.closeDrawer(mDrawerContainer);
			} else {
				mDrawerLayout.openDrawer(mDrawerContainer);
			}
			return true;

			// ***NOTE: Events for search is handled on onCreateOptionsMenu()

		case R.id.menu_help:
			Intent ActivityIntent = new Intent(this, AboutActivity.class);
			startActivity(ActivityIntent);
			return true;
		}
		return true;
	}

	@SuppressWarnings("unused")
	private void logOut() {
		appMain.logOut(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EditUserActivity.REQUESTCODE) {
			if (resultCode == RESULT_OK) {
				currentUser = appMain.getCurrentUser();

			} else if (resultCode == RESULT_CANCELED) {
				if (isUpdateProfileRequired) {
					finish();
				}

			} else {
				if (isUpdateProfileRequired) {
					finish();
				}

			}
		} else if (requestCode == NewOfferActivity.NEW_OFFER_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

				setProgressBarIndeterminateVisibility(Boolean.TRUE);
				dbCurrentRow = 0;
				isLoading = true;
				items.clear();
				adapter.notifyDataSetChanged();

				// Perform sync then refresh
				new Thread(new Runnable() {

					public void run() {
						appMain.requestSync();
						fillData(FILLDATA_REQUERY);
					}

				}).start();
			}
		} else if (requestCode == LocationPicker.REQUESTCODE) {
			if (resultCode == RESULT_OK) {
				double lat = (Double) data.getExtras().get(LocationPicker.EXTRAS_RESULT_LATITUDE);
				double lng = (Double) data.getExtras().get(LocationPicker.EXTRAS_RESULT_LONGITUDE);
				currentLatLng = new LatLng(lat, lng);

				currentLocationText = LocationHelper.getReadableAddressFromLatLng(LokalHomeActivity.this, currentLatLng, false);

				Button location = (Button) findViewById(R.id.btnLocation);
				location.setText(currentLocationText == null ? "Anywhere" : currentLocationText);

			} else if (resultCode == RESULT_CANCELED) {
				// user cancelled 
			} else {
				// failed 
			}
		}

	}

	@SuppressWarnings("unused")
	private boolean isNotFirstTime() {

		SharedPreferences preferences = ApplicationUtils.getSharedPreferences(LokalHomeActivity.this);// getPreferences(MODE_PRIVATE);
		// Set to false to always show guide overlay
		boolean ranBefore = preferences.getBoolean("RanBefore", false);
		if (!ranBefore) {

			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("RanBefore", true);
			editor.commit();
			top_layout.setVisibility(View.VISIBLE);
			top_layout.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					top_layout.setVisibility(View.INVISIBLE);
					return false;
				}

			});

		}
		return ranBefore;

	}

	private boolean doubleBackToExitPressedOnce = false;

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			setResult(RESULT_CANCELED);
			LokalHomeActivity.super.onBackPressed();
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

	private final String CACHED_OFFERS = "offercache.dat";

	public void retrieveCache() throws IOException {
		Context context = LokalHomeActivity.this;
		String data = FileOperationsHelper.readFromFile(LokalHomeActivity.this, CACHED_OFFERS);
		Gson gsonDetail = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		InputStream stream = new ByteArrayInputStream(data.getBytes("UTF-8"));
		JsonReader readerDetail = new JsonReader(new InputStreamReader(stream));

		readerDetail.beginArray();
		while (readerDetail.hasNext()) {
			Offer item = gsonDetail.fromJson(readerDetail, Offer.class);

			item.setContext(context);
			item.processImagesList();
			items.add(item);
		}
		readerDetail.endArray();
		readerDetail.close();

	}

	public void createCache() {
		Gson gson = new Gson();
		String json = gson.toJson(items);
		FileOperationsHelper.writeToFile(LokalHomeActivity.this, json, CACHED_OFFERS);

	}

	// ListView click listener in the navigation drawer
	//		@SuppressWarnings("unused")
	//		private class DrawerItemClickListener implements ListView.OnItemClickListener {
	//			@Override
	//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	//				selectItem(position);
	//			}
	//		}
	//
	//		private void selectItem(int position) {
	//
	//			// //use position parameter to do your thing
	//			// switch (position) {
	//			// case 0: //Settings
	//			//
	//			// break;
	//			// case 1: //About
	//			// Intent ActivityIntent = new Intent(this, AboutActivity.class);
	//			// startActivity(ActivityIntent);
	//			//
	//			// break;
	//			// case 2: //Log out
	//			//
	//			// logOut();
	//			// break;
	//			// }
	//			//
	//			// //setItemChecked
	//			// //mDrawerList.setItemChecked(position, true);
	//			//
	//			// // Get the title followed by the position
	//			// //setTitle(title[position]);
	//			//
	//			// // Close drawer
	//			// // mDrawerLayout.closeDrawer(mDrawerList); //use this if drawer only
	//			// contains list
	//			mDrawerLayout.closeDrawer(mDrawerContainer);
	//
	//		}

}