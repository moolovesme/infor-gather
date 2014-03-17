package com.dabeshackers.infor.gather.entities;

public class Question {

	private String id;
	private String event_id;

	private String details;
	private String asked_by;
	private long time_asked;

	private String edited_by;
	private long created;
	private long updated;
	private int version;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEvent_id() {
		return event_id;
	}

	public void setEvent_id(String event_id) {
		this.event_id = event_id;
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

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getAsked_by() {
		return asked_by;
	}

	public void setAsked_by(String asked_by) {
		this.asked_by = asked_by;
	}

	public long getTime_asked() {
		return time_asked;
	}

	public void setTime_asked(long time_asked) {
		this.time_asked = time_asked;
	}

}
