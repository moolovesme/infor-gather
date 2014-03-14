package com.dabeshackers.infor.gather;


import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;

import us.feras.ecogallery.EcoGallery;
import us.feras.ecogallery.EcoGalleryAdapterView;
import us.feras.ecogallery.EcoGalleryAdapterView.OnItemClickListener;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dabeshackers.infor.gather.R;
import com.dabeshackers.infor.gather.application.AppMain;
import com.dabeshackers.infor.gather.entities.Media;
import com.dabeshackers.infor.gather.entities.Offer;
import com.dabeshackers.infor.gather.entities.User;
import com.dabeshackers.infor.gather.helpers.BitmapHelper;
import com.dabeshackers.infor.gather.helpers.FileOperationsHelper;
import com.dabeshackers.infor.gather.helpers.GUIDHelper;
import com.dabeshackers.infor.gather.helpers.LocationHelper;
import com.dabeshackers.infor.gather.helpers.MediaCaptureHelper;
import com.dabeshackers.infor.gather.http.LokalWebService;
import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.util.Joiner;
import com.kpbird.chipsedittextlibrary.ChipsAdapter;
import com.kpbird.chipsedittextlibrary.ChipsItem;
import com.kpbird.chipsedittextlibrary.ChipsMultiAutoCompleteTextview;

public class NewOfferActivity extends SherlockActivity {

	EcoGallery ecoGallery;
	GridView gridView;
	LinearLayout gridViewContainer;
	Double lng = null;
	Double lat = null;

	private LatLng currentLatLng;
	private String currentLocationText;

	private static final int LOCATION_PICKER_REQUEST_CODE = 10;
	public static final int NEW_OFFER_REQUEST_CODE = NewOfferActivity.class.getSimpleName().hashCode();

	private Uri fileUri;

	private EditText title, price, description, bizurl, yturl, fb_url, gplus_url, twtrurl, landline, mobile;
	private TextView merchant, locationtext;
	private CheckBox publishonsave;

	ChipsMultiAutoCompleteTextview ch;
	ChipsAdapter chipsAdapter;

	private Offer currentOffer;
	private List<Media> images;

	private AppMain appMain;

	ProgressDialog pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_offer_add);

		appMain = (AppMain) getApplication();

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		ecoGallery = (EcoGallery) findViewById(R.id.images);
		//		ecoGallery.setAdapter(new ImageAdapter(this));

		gridView = (GridView) findViewById(R.id.gridView);
		gridViewContainer = (LinearLayout) findViewById(R.id.gridViewContainer);
		gridViewContainer.setVisibility(View.GONE);

		if (getIntent().getExtras().containsKey("lng")) {
			lng = getIntent().getExtras().getDouble("lng");
		}

		if (getIntent().getExtras().containsKey("lat")) {
			lat = getIntent().getExtras().getDouble("lat");
		}

		if (getIntent().getExtras().containsKey("loc_text")) {
			currentLocationText = getIntent().getExtras().getString("loc_text");
		}

		if (getIntent().getExtras().containsKey("item")) {
			currentOffer = (Offer) getIntent().getExtras().getSerializable("item");
		}

		currentLatLng = new LatLng(lat, lng);

		title = (EditText) findViewById(R.id.title);
		price = (EditText) findViewById(R.id.price);
		description = (EditText) findViewById(R.id.description);
		bizurl = (EditText) findViewById(R.id.bizurl);
		yturl = (EditText) findViewById(R.id.yturl);
		fb_url = (EditText) findViewById(R.id.fb_url);
		gplus_url = (EditText) findViewById(R.id.gplus_url);
		twtrurl = (EditText) findViewById(R.id.twtrurl);
		landline = (EditText) findViewById(R.id.landline);
		mobile = (EditText) findViewById(R.id.mobile);
		merchant = (TextView) findViewById(R.id.merchant);
		locationtext = (TextView) findViewById(R.id.locationtext);
		publishonsave = (CheckBox) findViewById(R.id.publishonsave);

		bizurl.setOnFocusChangeListener(urlFocusListener);
		yturl.setOnFocusChangeListener(youtubeUrlFocusListener);
		fb_url.setOnFocusChangeListener(urlFocusListener);
		gplus_url.setOnFocusChangeListener(urlFocusListener);
		twtrurl.setOnFocusChangeListener(urlFocusListener);

		User currentUser = appMain.getCurrentUser();
		merchant.setText("By: " + currentUser.getTradeName());
		bizurl.setText(currentUser.getBusinessURL());
		fb_url.setText(currentUser.getFacebookURL());
		twtrurl.setText(currentUser.getTwitterURL());
		gplus_url.setText(currentUser.getGplusURL());

		landline.setText(currentUser.getLandline());
		mobile.setText(currentUser.getMobile());

		if (currentLocationText != null && !currentLocationText.equals(LocationHelper.LOCATION_UNAVAILABLE)) {
			locationtext.setText("near " + currentLocationText == null ? "Anywhere" : currentLocationText);
		} else {
			locationtext.setText("Location Unavailable");
		}

		//setup tags
		final ArrayList<ChipsItem> defaultCategories = new ArrayList<ChipsItem>();
		ChipsItem chip = new ChipsItem();
		chip.setTitle("");
		defaultCategories.add(chip);

		chipsAdapter = new ChipsAdapter(NewOfferActivity.this, defaultCategories);

		ch = (ChipsMultiAutoCompleteTextview) findViewById(R.id.tags);
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<String> webTags = LokalWebService.Tags.fetchDistinctRecords(NewOfferActivity.this);
				final ArrayList<ChipsItem> presetCategories = new ArrayList<ChipsItem>();

				for (String tag : webTags) {
					ChipsItem chip = new ChipsItem();
					chip.setTitle(tag);
					presetCategories.add(chip);
				}

				//APPLY TO TAGS FIELD
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						chipsAdapter = new ChipsAdapter(NewOfferActivity.this, presetCategories);
						ch.setAdapter(chipsAdapter);
					}

				});

			}

		}).start();

		//populate fields if editing...
		if (currentOffer != null) {
			populateFieldsForEditing();
		}

	}

	private void populateFieldsForEditing() {

		DecimalFormat df = new DecimalFormat("###0.00");

		title.setText(currentOffer.getTitle());
		price.setText(df.format(currentOffer.getPrice_discounted()));
		description.setText(currentOffer.getDescription());
		merchant.setText(currentOffer.getMerchant());
		locationtext.setText(currentOffer.getLoc_text());

		bizurl.setText(currentOffer.getBiz_url());
		yturl.setText(currentOffer.getYoutube_url());
		fb_url.setText(currentOffer.getFacebook_url());
		gplus_url.setText(currentOffer.getGplus_url());
		twtrurl.setText(currentOffer.getTwitter_url());
		mobile.setText(currentOffer.getMobile());
		landline.setText(currentOffer.getLandline());

		if (currentOffer.getTagsList() != null && currentOffer.getTagsList().size() > 0) {
			String tags = Joiner.on(',').join(currentOffer.getTagsList());
			ch.setText(tags);
			ch.setChips();
		}
		images = currentOffer.getImagesList();
		populatePhotos();

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

	OnFocusChangeListener youtubeUrlFocusListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			EditText text = (EditText) v;
			if (!hasFocus) {

				if (text.getText().toString().length() > 0) {
					//if user only puts video id
					if (!text.getText().toString().startsWith("https://www.youtube.com/watch?v=")) {
						text.setText("https://www.youtube.com/watch?v=" + text.getText());
					}

					//If user did not put anything
					if (text.getText().toString().trim().equals("https://www.youtube.com/watch?v=")) {
						text.setText("");
					}
				}

			} else {
				if (text.getText().toString().length() == 0) {
					text.setText("https://www.youtube.com/watch?v=" + text.getText());
				}
			}
		}
	};

	boolean hasValidationErrors = false;
	final StringBuilder errMsg = new StringBuilder();

	private boolean validate() {
		hasValidationErrors = false;
		errMsg.delete(0, errMsg.length());
		errMsg.append("Errors in the following fields were encountered. Please correct them then try again.\n\n");

		if (title.getText().toString() == null || title.getText().toString().length() == 0) {
			hasValidationErrors = true;
			errMsg.append("- Title should not be left blank.").append("\n");
		}

		if (price.getText().toString() == null || price.getText().toString().length() == 0) {
			hasValidationErrors = true;
			errMsg.append("- Price should not be left blank.").append("\n");
		}

		if (description.getText().toString() == null || description.getText().toString().length() == 0) {
			hasValidationErrors = true;
			errMsg.append("- Description should not be left blank.").append("\n");
		}

		if (images == null || images.size() == 0) {
			hasValidationErrors = true;
			errMsg.append("- At least 1 (one) photo is required.").append("\n");
		}

		if (hasValidationErrors) {
			return false;
		}

		return true;
	}

	private void save() {
		pd = new ProgressDialog(NewOfferActivity.this);
		pd.setIndeterminate(true);
		pd.setTitle("Saving to Cloud.");
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
							AlertDialog.Builder alert = new AlertDialog.Builder(NewOfferActivity.this);
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

				} else {

					if (currentOffer == null) {
						currentOffer = new Offer();
						currentOffer.setContext(NewOfferActivity.this);
						currentOffer.setId(GUIDHelper.createGUID());
						currentOffer.setStatus(Offer.OFFER_STATUS_PUBLISHED);
						currentOffer.setVersion(1);
						currentOffer.setCreated(Calendar.getInstance().getTimeInMillis());
					} else {
						currentOffer.setVersion(currentOffer.getVersion() + 1);
					}

					currentOffer.setTitle(title.getText().toString());
					currentOffer.setPrice_discounted(Double.parseDouble(price.getText().toString()));
					currentOffer.setDescription(description.getText().toString());
					currentOffer.setBiz_url(bizurl.getText().toString());
					currentOffer.setYoutube_url(yturl.getText().toString());
					currentOffer.setFacebook_url(fb_url.getText().toString());
					currentOffer.setTwitter_url(twtrurl.getText().toString());
					currentOffer.setGplus_url(gplus_url.getText().toString());
					currentOffer.setLandline(landline.getText().toString());
					currentOffer.setMobile(mobile.getText().toString());
					currentOffer.setLoc_text(locationtext.getText().toString());
					currentOffer.setLoc_lat(currentLatLng.latitude);
					currentOffer.setLoc_lng(currentLatLng.longitude);

					if (publishonsave.isChecked()) {
						currentOffer.setStatus(Offer.OFFER_STATUS_PUBLISHED);
					} else {
						currentOffer.setStatus(Offer.OFFER_STATUS_DRAFT);
					}

					String[] chips = ch.getText().toString().trim().split(",");
					List<String> tags = new ArrayList<String>(Arrays.asList(chips));

					if (images != null) {
						currentOffer.setImagesList(images);
					}

					if (tags != null) {
						currentOffer.setTagsList(tags);
					}

					User currentUser = appMain.getCurrentUser();

					currentOffer.setMerchant(currentUser.getTradeName());
					currentOffer.setEdited_by(currentUser.getId());
					currentOffer.setUpdated(Calendar.getInstance().getTimeInMillis());

					if (LokalWebService.Offers.pushRecordToBackEnd(NewOfferActivity.this, currentOffer)) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								pd.dismiss();
								AlertDialog.Builder alert = new AlertDialog.Builder(NewOfferActivity.this);
								alert.setTitle("Successful");
								alert.setMessage("Offer successfully saved!");
								alert.setCancelable(false);
								alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										//Clear temp folder
										for (Media media : images) {
											File f = new File(media.getLocalFilePath());
											if (f.exists()) {
												if (!f.delete()) {
													f.deleteOnExit();
												}
											}
										}

										setResult(RESULT_OK);
										finish();
									}
								});
								alert.setIcon(android.R.drawable.ic_dialog_alert);
								alert.show();
							}
						});

					} else {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								pd.dismiss();

								AlertDialog.Builder alert = new AlertDialog.Builder(NewOfferActivity.this);
								alert.setTitle("Unable to save changes.");
								alert.setMessage("Unable to save to cloud at this time. Please try again.");
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
		// if the result is capturing Image
		if (requestCode == MediaCaptureHelper.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// successfully captured the image
				// display it in image view
				if (images == null) {
					images = new ArrayList<Media>();
				}

				File f = new File(fileUri.getPath());

				Media media = new Media(NewOfferActivity.this);
				media.setId(GUIDHelper.createGUID());
				media.setType(Media.MEDIA_TYPE_BITMAP);
				media.setLocalFilePath(fileUri.getPath());
				media.setName(f.getName());
				media.setStatus(Media.MEDIA_STATUS_NEW);

				User currentUser = appMain.getCurrentUser();

				media.setEdited_by(currentUser.getId());
				media.setCreated(Calendar.getInstance().getTimeInMillis());
				media.setUpdated(Calendar.getInstance().getTimeInMillis());
				media.setVersion(1);

				images.add(media);

				BitmapHelper.resize(fileUri.getPath(), BitmapHelper.SIZE_300);

				// Populate GridView
				populatePhotos();

			} else if (resultCode == RESULT_CANCELED) {
				// user) cancelled Image capture
				// Toast.makeText(getApplicationContext(),"User cancelled image capture",
				// Toast.LENGTH_SHORT).show();
			} else {
				// failed to capture image
				Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
			}
		} else if (requestCode == MediaCaptureHelper.CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// video successfully recorded
				// preview the recorded video
				// previewVideo();
			} else if (resultCode == RESULT_CANCELED) {
				// user cancelled recording
				Toast.makeText(getApplicationContext(), "User cancelled video recording", Toast.LENGTH_SHORT).show();
			} else {
				// failed to record video
				Toast.makeText(getApplicationContext(), "Sorry! Failed to record video", Toast.LENGTH_SHORT).show();
			}
		} else if (requestCode == MediaCaptureHelper.GALLERY_PICK_IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// successfully captured the image
				// display it in image view
				if (images == null) {
					images = new ArrayList<Media>();
				}

				File selectedImage = new File(FileOperationsHelper.getRealPathFromURI(NewOfferActivity.this, data.getData()));
				File f = new File(fileUri.getPath());

				try {
					FileUtils.copyFile(selectedImage, f);
				} catch (IOException e) {
					e.printStackTrace();
				}

				Media media = new Media(NewOfferActivity.this);
				media.setId(GUIDHelper.createGUID());
				media.setType(Media.MEDIA_TYPE_BITMAP);
				media.setLocalFilePath(fileUri.getPath());
				media.setName(f.getName());
				media.setStatus(Media.MEDIA_STATUS_NEW);

				User currentUser = appMain.getCurrentUser();

				media.setEdited_by(currentUser.getId());
				media.setCreated(Calendar.getInstance().getTimeInMillis());
				media.setUpdated(Calendar.getInstance().getTimeInMillis());
				media.setVersion(1);

				images.add(media);

				BitmapHelper.resize(fileUri.getPath(), BitmapHelper.SIZE_300);

				// Populate GridView
				populatePhotos();

			} else if (resultCode == RESULT_CANCELED) {
				// user) cancelled Image capture
				// Toast.makeText(getApplicationContext(),"User cancelled image capture",
				// Toast.LENGTH_SHORT).show();
			} else {
				// failed to capture image
				Toast.makeText(getApplicationContext(), "Sorry! Failed to pick image", Toast.LENGTH_SHORT).show();
			}
		} else if (requestCode == LOCATION_PICKER_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				double lat = (Double) data.getExtras().get(LocationPicker.EXTRAS_RESULT_LATITUDE);
				double lng = (Double) data.getExtras().get(LocationPicker.EXTRAS_RESULT_LONGITUDE);
				currentLatLng = new LatLng(lat, lng);

				Location loc = new Location("");
				loc.setLatitude(lat);
				loc.setLongitude(lng);
				currentLocationText = LocationHelper.getReadableAddressFromLocation(NewOfferActivity.this, loc, false);

				TextView locationtext = (TextView) findViewById(R.id.locationtext);
				locationtext.setText(currentLocationText == null ? "Anywhere" : currentLocationText);

			} else if (resultCode == RESULT_CANCELED) {
				// user cancelled
			} else {
				// failed
			}
		}
	}

	private void populatePhotos() {
		TextView imageslabel = (TextView) findViewById(R.id.imageslabel);
		imageslabel.setText("Photos (" + images.size() + ")");
		if (images.size() > 0) {

			ecoGallery.setAdapter(new ImageAdapter(NewOfferActivity.this));
			ecoGallery.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(EcoGalleryAdapterView<?> parent, View view, final int position, long id) {
					final File f = new File(images.get(position).getLocalFilePath());

					final PopupMenu popup = new PopupMenu(NewOfferActivity.this, view);
					popup.getMenuInflater().inflate(R.menu.image_options, popup.getMenu());
					popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

						@Override
						public boolean onMenuItemClick(android.view.MenuItem menu) {
							switch (menu.getItemId()) {

							case R.id.menu_view:
								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_VIEW);
								intent.setDataAndType(Uri.parse("file://" + f.getAbsolutePath()), "image/*");
								startActivity(intent);

								break;

							case R.id.menu_remove:
								images.remove(position);
								populatePhotos();

								break;

							default:
								break;
							}

							return true;
						}

					});

					popup.show();
				}
			});
			ecoGallery.setSelection(images.size() - 1);
			gridViewContainer.setVisibility(View.VISIBLE);

		} else {
			gridViewContainer.setVisibility(View.GONE);
		}

	}

	public class ImageAdapter extends BaseAdapter {
		Context context;

		public ImageAdapter(Context context) {
			this.context = context;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			ImageView imageView;
			imageView = new ImageView(NewOfferActivity.this);

			final String imageString = images.get(position).getLocalFilePath();
			Bitmap bitmap = BitmapHelper.decodeSampledBitmapFromFile(imageString, 200, 200);
			imageView.setImageBitmap(bitmap);
			imageView.setBackgroundColor(Color.RED);
			//				imageView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 200));
			imageView.setLayoutParams(new EcoGallery.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));

			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

			return imageView;

		}

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Get the options menu view from menu.xml in menu folder
		getSupportMenuInflater().inflate(R.menu.activity_offer_add, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
			this.finish();
			return true;

			// ***NOTE: Events for search is handled on onCreateOptionsMenu()

		case R.id.menu_location:

			Intent intent = new Intent(NewOfferActivity.this, LocationPicker.class);
			intent.putExtra("lat", currentLatLng.latitude);
			intent.putExtra("lng", currentLatLng.longitude);
			startActivityForResult(intent, LOCATION_PICKER_REQUEST_CODE);

			break;

		case R.id.menu_camera:

			fileUri = MediaCaptureHelper.captureImage(NewOfferActivity.this, fileUri);

			break;

		case R.id.menu_gallery:

			fileUri = MediaCaptureHelper.pickPhoto(NewOfferActivity.this, fileUri);

			break;

		case R.id.menu_proceed:
			save();

			break;

		}
		return true;
	}

	private boolean doubleBackToExitPressedOnce = false;

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			setResult(RESULT_CANCELED);
			NewOfferActivity.super.onBackPressed();
			return;
		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please click BACK again to cancel", Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {

			public void run() {
				doubleBackToExitPressedOnce = false;

			}
		}, 2000);

	}
}
