package com.dabeshackers.infor.gather.entities;


import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.dabeshackers.infor.gather.helpers.BitmapHelper;

import android.content.Context;
import android.graphics.Bitmap;

public class Offer implements Serializable, Cloneable {

	private static final long serialVersionUID = -3661260023918041604L;

	public static final int OFFER_REGISTRATION_TYPE_FREE = 0;
	public static final int OFFER_REGISTRATION_TYPE_PAID = 1;

	public static final String OFFER_STATUS_DRAFT = "DRAFT";
	public static final String OFFER_STATUS_PUBLISHED = "PUBLISHED";
	public static final String OFFER_STATUS_INACTIVE = "INACTIVE";

	public int currentImageIndex;

	private transient Context context;

	private String id;

	private String title;
	private String merchant;
	private String description;
	private int registration_type;

	private double price_discounted;

	private String loc_text;
	private double loc_lng;
	private double loc_lat;
	private double distance;

	private List<String> tagsList;
	private List<Media> imagesList;
	private transient List<Bitmap> imagesBitmapList;

	private String biz_url;
	private String youtube_url;
	private String facebook_url;
	private String gplus_url;
	private String twitter_url;
	private String landline;
	private String mobile;

	private String status;

	private String edited_by;
	private long created;
	private long updated;
	private int version;

	private Offer snapShot;

	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public Offer getSnapShot() {
		return snapShot;
	}

	public void createSnapShot() throws CloneNotSupportedException {
		this.snapShot = (Offer) clone();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLoc_text() {
		return loc_text;
	}

	public void setLoc_text(String loc_text) {
		this.loc_text = loc_text;
	}

	public double getLoc_lng() {
		return loc_lng;
	}

	public void setLoc_lng(double loc_lng) {
		this.loc_lng = loc_lng;
	}

	public double getLoc_lat() {
		return loc_lat;
	}

	public void setLoc_lat(double loc_lat) {
		this.loc_lat = loc_lat;
	}

	public String getEdited_by() {
		return edited_by;
	}

	public void setEdited_by(String edited_by) {
		this.edited_by = edited_by;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public long getUpdated() {
		return updated;
	}

	public void setUpdated(long updated) {
		this.updated = updated;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getRegistration_type() {
		return registration_type;
	}

	public void setRegistration_type(int registration_type) {
		this.registration_type = registration_type;
	}

	public double getPrice_discounted() {
		return price_discounted;
	}

	public String getFormattedprice_discounted() {
		DecimalFormat df = new DecimalFormat("#,##0.00");
		return df.format(price_discounted);
	}

	public void setPrice_discounted(double price_discounted) {
		this.price_discounted = price_discounted;
	}

	public List<String> getTagsList() {
		return tagsList;
	}

	public void setTagsList(List<String> tagsList) {
		this.tagsList = tagsList;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public List<Media> getImagesList() {
		return imagesList;
	}

	public void setImagesList(List<Media> imagesList) {
		this.imagesList = imagesList;

		if (imagesBitmapList != null) {
			imagesBitmapList.clear();
		} else {
			imagesBitmapList = new ArrayList<Bitmap>();
		}

		for (Media media : imagesList) {
			String s = media.getLocalFilePath();
			if (new File(s).exists()) {
				imagesBitmapList.add(BitmapHelper.decodeSampledBitmapFromFile(s, 200, 200));
			}
		}
	}

	public void processImagesList() {
		if (imagesList == null) {
			return;
		} else {
			imagesBitmapList = new ArrayList<Bitmap>();

			for (Media media : imagesList) {
				String s = media.getLocalFilePath();
				if (new File(s).exists()) {
					imagesBitmapList.add(BitmapHelper.decodeSampledBitmapFromFile(s, 200, 200));
				}
			}
		}
	}

	public Bitmap getCurrentImageFromList() {
		if (imagesList != null && imagesList.size() > 0) {

			return imagesBitmapList.get(currentImageIndex);

		} else {
			return null;
		}

	}

	public Bitmap getNextImageFromList() {
		if (imagesList != null && imagesList.size() > 0) {
			currentImageIndex++;
			if (currentImageIndex > imagesList.size() - 1) {
				currentImageIndex = 0;
			}

			return imagesBitmapList.get(currentImageIndex);

		} else {
			return null;
		}

	}

	public Bitmap getPrevImageFromList() {
		if (imagesList != null && imagesList.size() > 0) {
			currentImageIndex--;
			if (currentImageIndex < 0) {
				currentImageIndex = imagesList.size() - 1;
			}

			return imagesBitmapList.get(currentImageIndex);

		} else {
			return null;
		}

	}

	public Bitmap getRandomizedImageFromList() {
		if (imagesList != null && imagesList.size() > 0) {
			int index = 0;
			while (index == currentImageIndex) {
				index = new Random().nextInt(((imagesList.size() - 1) - 0) + 1) + 0;
			}
			currentImageIndex = index;
			return imagesBitmapList.get(index);

		} else {
			return null;
		}

	}

	public String getBiz_url() {
		return biz_url;
	}

	public void setBiz_url(String biz_url) {
		this.biz_url = biz_url;
	}

	public String getYoutube_url() {
		return youtube_url;
	}

	public void setYoutube_url(String youtube_url) {
		this.youtube_url = youtube_url;
	}

	public String getFacebook_url() {
		return facebook_url;
	}

	public void setFacebook_url(String facebook_url) {
		this.facebook_url = facebook_url;
	}

	public String getGplus_url() {
		return gplus_url;
	}

	public void setGplus_url(String gplus_url) {
		this.gplus_url = gplus_url;
	}

	public String getTwitter_url() {
		return twitter_url;
	}

	public void setTwitter_url(String twitter_url) {
		this.twitter_url = twitter_url;
	}

	public String getLandline() {
		return landline;
	}

	public void setLandline(String landline) {
		this.landline = landline;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
