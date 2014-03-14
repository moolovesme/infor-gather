package com.dabeshackers.infor.gather.entities;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Chronicle implements Serializable, Cloneable{

	private static final long serialVersionUID = 5142856349857391415L;
	
	private String id;
	private String body;
	private String attachment;
	private String loc_text;
	private double loc_lng;
	private double loc_lat;
	
	private List<Media> mediaList;
	
	
	private String edited_by;
	private long created;
	private long  updated;
	private int  version;

	private User snapShot;

	
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	public User getSnapShot() {
		return snapShot;
	}
	public void createSnapShot() throws CloneNotSupportedException {
		this.snapShot = (User)clone();
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
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getAttachment() {
		return attachment;
	}
	public void setAttachment(String attachment) {
		this.attachment = attachment;
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
	public List<Media> getMediaList() {
		return mediaList;
	}
	public void setMediaList(List<Media> mediaList) {
		this.mediaList = mediaList;
	}
	
}
