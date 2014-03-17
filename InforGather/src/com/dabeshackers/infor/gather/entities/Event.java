package com.dabeshackers.infor.gather.entities;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.builder.ToStringBuilder;

import android.content.Context;
import android.graphics.Bitmap;

import com.dabeshackers.infor.gather.helpers.BitmapHelper;

public class Event implements Serializable, Cloneable {

	private static final long serialVersionUID = -3661260023918041604L;

	public int currentImageIndex;

	private transient Context context;

	private String id;

	private String title;
	private String subtitle;
	private String description;
	private String organizer;
	private String eventmaster;

	private long datefrom;
	private long dateto;

	private String transcript;
	private String tag;
	private String status;

	private String loc_text;
	private double loc_lng;
	private double loc_lat;

	private List<String> tagsList;
	private List<Media> imagesList;
	private transient List<Bitmap> imagesBitmapList;
	private List<Attendee> attendees;
	private List<Schedule> programme;
	private List<Question> questions;

	private String ref_url;
	private String youtube_url;
	private String facebook_url;
	private String gplus_url;
	private String twitter_url;

	private String edited_by;
	private long created;
	private long updated;
	private int version;

	private Event snapShot;

	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public Event getSnapShot() {
		return snapShot;
	}

	public void createSnapShot() throws CloneNotSupportedException {
		this.snapShot = (Event) clone();
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

	public List<String> getTagsList() {
		return tagsList;
	}

	public void setTagsList(List<String> tagsList) {
		this.tagsList = tagsList;
	}

	public String getOrganizer() {
		return organizer;
	}

	public void setOrganizer(String organizer) {
		this.organizer = organizer;
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

	public String getRef_url() {
		return ref_url;
	}

	public void setRef_url(String ref_url) {
		this.ref_url = ref_url;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getEventmaster() {
		return eventmaster;
	}

	public void setEventmaster(String eventmaster) {
		this.eventmaster = eventmaster;
	}

	public long getDatefrom() {
		return datefrom;
	}

	public void setDatefrom(long datefrom) {
		this.datefrom = datefrom;
	}

	public long getDateto() {
		return dateto;
	}

	public void setDateto(long dateto) {
		this.dateto = dateto;
	}

	public String getTranscript() {
		return transcript;
	}

	public void setTranscript(String transcript) {
		this.transcript = transcript;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public List<Attendee> getAttendees() {
		return attendees;
	}

	public void setAttendees(List<Attendee> attendees) {
		this.attendees = attendees;
	}

	public List<Schedule> getProgramme() {
		return programme;
	}

	public void setProgramme(List<Schedule> programme) {
		this.programme = programme;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

}
