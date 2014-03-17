package com.dabeshackers.infor.gather.entities;

import java.io.Serializable;
import java.util.Calendar;

public class Person implements Serializable, Cloneable {

	private static final long serialVersionUID = -8373657576287083885L;

	private String id;
	private String firstName;
	private String middleName;
	private String lastName;

	private long birthday;

	private String image;
	private String addRegion;
	private String addCity;
	private String addBrgy;
	private String add_line1;
	private String add_line2;
	private String zipCode;

	private double add_lat;
	private double add_lng;

	private String edited_by;
	private long created;
	private long updated;
	private int version;

	private Person snapShot;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public long getBirthday() {
		return birthday;
	}

	public void setBirthday(long birthday) {
		this.birthday = birthday;
	}

	public String getAddRegion() {
		return addRegion;
	}

	public void setAddRegion(String addRegion) {
		this.addRegion = addRegion;
	}

	public String getAddCity() {
		return addCity;
	}

	public void setAddCity(String addCity) {
		this.addCity = addCity;
	}

	public String getAddBrgy() {
		return addBrgy;
	}

	public void setAddBrgy(String addBrgy) {
		this.addBrgy = addBrgy;
	}

	public String getAddLine1() {
		return add_line1;
	}

	public void setAddLine1(String addLine1) {
		this.add_line1 = addLine1;
	}

	public String getAddLine2() {
		return add_line2;
	}

	public void setAddLine2(String addLine2) {
		this.add_line2 = addLine2;
	}

	public String getEdited_by() {
		return edited_by;
	}

	public void setEdited_by(String edited_by) {
		this.edited_by = edited_by;
	}

	public long getCreated() {
		if (created == 0) {
			created = Calendar.getInstance().getTimeInMillis();
		}
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public long getUpdated() {
		if (updated == 0) {
			updated = Calendar.getInstance().getTimeInMillis();
		}
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public Person getSnapShot() {
		return snapShot;
	}

	public void createSnapShot() throws CloneNotSupportedException {
		this.snapShot = (Person) clone();
	}

	public double getAdd_lat() {
		return add_lat;
	}

	public void setAdd_lat(double add_lat) {
		this.add_lat = add_lat;
	}

	public double getAdd_lng() {
		return add_lng;
	}

	public void setAdd_lng(double add_lng) {
		this.add_lng = add_lng;
	}
}
