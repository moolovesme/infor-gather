package com.dabeshackers.infor.gather;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.feras.ecogallery.EcoGallery;
import us.feras.ecogallery.EcoGalleryAdapterView;
import us.feras.ecogallery.EcoGalleryAdapterView.OnItemClickListener;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.dabeshackers.infor.gather.R;
import com.dabeshackers.infor.gather.entities.Media;
import com.dabeshackers.infor.gather.entities.Offer;
import com.dabeshackers.infor.gather.helpers.BitmapHelper;
import com.dabeshackers.infor.gather.helpers.LocationHelper;
import com.dabeshackers.infor.gather.helpers.ToastHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class ViewOfferActivity extends YouTubeFailureRecoveryActivity implements OnTabChangeListener, OnPageChangeListener {

	TextView title;
	TextView description;
	TextView expiration;
	TextView merchant;
	TextView price;

	TextView biz_url;
	TextView fb_url;
	TextView gplus_url;
	TextView twtr_url;
	TextView landline;
	TextView mobile;

	ImageView img, share, navigate;
	EcoGallery ecoGallery;

	private Offer item;

	private TabHost host;
	private ViewPager pager;

	BitmapDrawable[] layers;
	TransitionDrawable transDraw;

	List<Media> images;
	ImageAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_offer_view);

		if (getIntent().hasExtra("item")) {
			item = (Offer) getIntent().getExtras().getSerializable("item");
			item.setContext(ViewOfferActivity.this);
			item.processImagesList();

			title = (TextView) findViewById(R.id.title);
			merchant = (TextView) findViewById(R.id.merchant);
			description = (TextView) findViewById(R.id.description);
			price = (TextView) findViewById(R.id.price);
			expiration = (TextView) findViewById(R.id.expiration);
			img = (ImageView) findViewById(R.id.img);

			biz_url = (TextView) findViewById(R.id.biz_url);
			fb_url = (TextView) findViewById(R.id.fb_url);
			gplus_url = (TextView) findViewById(R.id.gplus_url);
			twtr_url = (TextView) findViewById(R.id.twtr_url);
			landline = (TextView) findViewById(R.id.landline);
			mobile = (TextView) findViewById(R.id.mobile);

			title.setText(item.getTitle());
			merchant.setText("By: " + item.getMerchant());
			description.setText(item.getDescription());
			price.setText("Php" + item.getFormattedprice_discounted());
			expiration.setText("Valid until: " + new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH).format(item.getCreated() + (1000 * 60 * 60 * 24 * 3)));

			biz_url.setText(item.getBiz_url());
			fb_url.setText(item.getFacebook_url());
			gplus_url.setText(item.getGplus_url());
			twtr_url.setText(item.getTwitter_url());
			landline.setText(item.getLandline());
			mobile.setText(item.getMobile());

			landline.setAutoLinkMask(Linkify.ALL);
			mobile.setAutoLinkMask(Linkify.ALL);

			//			Linkify.addLinks(landline, Linkify.ALL);
			//			Linkify.addLinks(mobile, Linkify.ALL);

			setupTabs();

			// Initialize YouTube Player
			YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
			youTubeView.initialize(YouTubeDeveloperKey.DEVELOPER_KEY, this);

			ecoGallery = (EcoGallery) findViewById(R.id.images);
			ecoGallery.setAnimationDuration(3000);
			if (item.getImagesList() != null && item.getImagesList().size() > 0) {
				images = item.getImagesList();
				adapter = new ImageAdapter(ViewOfferActivity.this);
				ecoGallery.setAdapter(adapter);
				ecoGallery.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(EcoGalleryAdapterView<?> parent, View view, int position, long id) {
						File f = new File(images.get(position).getLocalFilePath());
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_VIEW);
						intent.setDataAndType(Uri.parse("file://" + f.getAbsolutePath()), "image/*");
						startActivity(intent);
					}
				});
				Runnable timedRunnable = new Runnable() {

					@Override
					public void run() {
						//						ecoGallery.setSelection(item.get, true);
						final int selection = (ecoGallery.getSelectedItemPosition() + 1) > adapter.getCount() - 1 ? 0 : ecoGallery.getSelectedItemPosition() + 1;
						ecoGallery.post(new Runnable() {

							@Override
							public void run() {

								//								ecoGallery.setSelection(selection, false);
								ecoGallery.setSelection(selection, true);
							}

						});

						ecoGallery.postDelayed(this, 5000);
					}

				};
				ecoGallery.postDelayed(timedRunnable, 5000);

			} else {
				ecoGallery.setVisibility(View.GONE);
			}
			// End Load image

			share = (ImageView) findViewById(R.id.share);
			share.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					boolean withImage = false;
					File myFile = null;

					if (item.getImagesList() != null && item.getImagesList().size() > 0) {
						withImage = true;
						myFile = new File(images.get(0).getLocalFilePath());
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
						AlertDialog.Builder alert = new AlertDialog.Builder(ViewOfferActivity.this);
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
				}
			});

			navigate = (ImageView) findViewById(R.id.navigate);
			navigate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					LatLng currentLoc = LocationHelper.getLastKnownLocationLatLng(ViewOfferActivity.this);
					final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + "saddr=" + currentLoc.latitude + "," + currentLoc.longitude + "&daddr=" + item.getLoc_lat() + "," + item.getLoc_lng()));
					intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

					try {
						startActivity(intent);
					} catch (android.content.ActivityNotFoundException ex) {
						ToastHelper.toast(ViewOfferActivity.this, "No application could handle your navigation request. Please install Google Maps and try again.", Toast.LENGTH_LONG);
					}

				}
			});

		}

	}

	private void setupTabs() {
		host = (TabHost) findViewById(android.R.id.tabhost);
		pager = (ViewPager) findViewById(R.id.pager);

		host.setup();
		TabSpec spec = host.newTabSpec("tab1");
		spec.setContent(R.id.tab1);
		spec.setIndicator("Details");
		host.addTab(spec);

		spec = host.newTabSpec("tab2");
		spec.setContent(R.id.tab2);
		spec.setIndicator("Youtube");
		host.addTab(spec);

		spec = host.newTabSpec("tab3");
		spec.setContent(R.id.tab3);
		spec.setIndicator("Web");
		host.addTab(spec);

		// pager.setAdapter(new MyPagerAdapter(ViewOfferActivity.this));
		pager.setOnPageChangeListener(this);
		host.setOnTabChangedListener(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onTabChanged(String tabId) {
		int pageNumber = 0;
		if (tabId.equals("tab1")) {
			pageNumber = 0;
		} else if (tabId.equals("tab2")) {
			pageNumber = 1;
		} else if (tabId.equals("tab3")) {
			pageNumber = 2;
		} else {
			pageNumber = 3;
		}
		pager.setCurrentItem(pageNumber);
	}

	@Override
	public void onPageSelected(int pageNumber) {
		host.setCurrentTab(pageNumber);
	}

	public class MyPagerAdapter extends PagerAdapter {
		private Context ctx;

		public MyPagerAdapter(Context ctx) {
			this.ctx = ctx;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			TextView tView = new TextView(ctx);
			position++;
			tView.setText("Page number: " + position);
			tView.setTextColor(Color.RED);
			tView.setTextSize(20);
			container.addView(tView);
			return tView;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return (view == object);
		}
	}

	private final String YOUTUBE_DEFAULT_ID = "wKJ9KzGQq0w";

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {

			if (item.getYoutube_url() == null || item.getYoutube_url().length() == 0) {
				player.cueVideo(YOUTUBE_DEFAULT_ID);
			} else {
				// extract ID from URL
				String url = item.getYoutube_url();
				Pattern p = Pattern.compile("http.*\\?v=([a-zA-Z0-9_\\-]+)(?:&.)*");
				Matcher m = p.matcher(url);

				if (m.matches()) {
					url = m.group(1);
				}

				player.cueVideo(url);
			}
		}
	}

	@Override
	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}

	public class ImageAdapter extends BaseAdapter {
		Context context;

		public ImageAdapter(Context context) {
			this.context = context;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			ImageView imageView;
			imageView = new ImageView(ViewOfferActivity.this);

			final String imageString = images.get(position).getLocalFilePath();
			Bitmap bitmap = BitmapHelper.decodeSampledBitmapFromFile(imageString, 200, 200);
			imageView.setImageBitmap(bitmap);
			imageView.setBackgroundColor(Color.RED);
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

}
