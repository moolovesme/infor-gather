package com.dabeshackers.infor.gather.entities;

public class Attendee extends Person {
	private static final long serialVersionUID = -7279060574909410623L;

	private String email;
	private boolean didConfirm;
	private boolean didAttend;
	private long timeAttend;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean didConfirm() {
		return didConfirm;
	}

	public void setDidConfirm(boolean didConfirm) {
		this.didConfirm = didConfirm;
	}

	public boolean didAttend() {
		return didAttend;
	}

	public void setDidAttend(boolean didAttend) {
		this.didAttend = didAttend;
	}

	public long getTimeAttend() {
		return timeAttend;
	}

	public void setTimeAttend(long timeAttend) {
		this.timeAttend = timeAttend;
	}

}
