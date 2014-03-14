package com.dabeshackers.infor.gather.entities;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class User implements Serializable, Cloneable{

	private static final long serialVersionUID = 5142856349857391415L;
	
	public static final int VERIFIED_NO = 0;
	public static final int VERIFIED_YES = 1;

	public static final String SINGLE = "Single";
	public static final String MARRIED = "Married";
	public static final String WIDOW = "Widow";
	public static final String WIDOWER = "Widower";

	public static final int USER_GENDER_MALE = 0;
	public static final int USER_GENDER_FEMALE = 1;

	public static final int USER_BENEFICIARY_NOT_DISABLED = 0;
	public static final int USER_BENEFICIARY_DISABLED = 1;

	public static final String USER_GENDER_MALE_STR = "Male";
	public static final String USER_GENDER_FEMALE_STR = "Female";


	public static final int USER_TYPE_NONE = 0;
	public static final int USER_TYPE_AGENT= 1;
	public static final int USER_TYPE_OUTLET = 2;
	public static final int USER_TYPE_ENCODER = 3;
	public static final int USER_TYPE_MANAGER = 4;
	public static final int USER_TYPE_ADMIN = 99;

	public static List<String> professions;

	private String	id;
	private String	owner_id;

	private String	email;
	private String	password;
	private String	password_hash;
	private boolean	password_changed;
	private String	firstName;
	private String	middleName;
	private String	lastName;
	private long	birthday;

	private int		type;
	private String	image;
	private String	addRegion;
	private String	addCity;
	private String	addBrgy;
	private String	add_line1;
	private String	add_line2;
	private String	zipCode;

	private String	tradeName;
	private double	add_lat;
	private double	add_lng;
	private String	businessURL;
	private String	facebookURL;
	private String	gplusURL;
	private String	twitterURL;
	private String	landline;
	private String	mobile;
	private int	verified;

	private String	edited_by;
	private long	created;
	private long 	updated;
	private int		version;

	private User	snapShot;

	//UNUSED FIELDS
	private String	termination_image;
	private String	user_id;
	private String	sss;
	private String	pagibig;
	private String	philhealth;
	private String	tin;
	private int		gender; //0 - Male : 1 - Female
	private String	civilstatus;
	private String	emergency_person;
	private String	emergency_number;
	

	private String	suffix;
	private String	maiden_name;
	private String	ofc_number;
	private String	ben_firstName;
	private String	ben_middleName;
	private String	ben_lastName;
	private String	ben_suffix;
	private long	ben_birthday;
	private int		ben_gender; //0 - Male : 1 - Female
	private String	ben_civilstatus;
	private String	ben_maiden_name;
	private int		ben_disabled;


	public static final String USER_PROFESSION_OTHER = "Other";

	//PHILHEALTH FIELDS
	public static final String PHILHEALTH_MEMBER_CATEGORY_GOVT_HH_PRIVATE = "Government / Household / Private";
	public static final String PHILHEALTH_MEMBER_CATEGORY_OFW = "OFW";
	public static final String PHILHEALTH_MEMBER_CATEGORY_INDIVIDUALLY_PAYING_MEMBER = "Individually Paying Member";
	public static final String PHILHEALTH_MEMBER_CATEGORY_LIFETIME = "Lifetime";

	public static final String PHILHEALTH_MEMBER_TYPE_OTHERS = "Others";
	public static final String PHILHEALTH_MEMBER_TYPE_SELF_EMP_NON_PRO = "Self Employed Non Professional";
	public static final String PHILHEALTH_MEMBER_TYPE_SELF_EMP_PRO = "Self Employed Professional";

	public static final String PHILHEALTH_FAMILY_INCOME_25K_BELOW = "Below 25,000";
	public static final String PHILHEALTH_FAMILY_INCOME_25K_ABOVE = "Above 25,000";

	public static final String APPLICATION_STATUS_PENDING = "Pending Application";

	private String	ph_member_category;
	private String	ph_pen;
	private String	ph_employer_name;
	private String	ph_employer_address;
	private long	ph_date_hired;
	private String	ph_profession;
	private String	ph_country_based;
	private String	ph_foreign_address;
	private long	ph_duration_from;
	private long	ph_duration_to;
	private String	ph_member_type;
	private String	ph_family_income;
	private String	ph_doc1;
	private String	ph_doc2;
	private String	ph_doc3;
	private String	ph_doc4;


	//SSS FIELDS
	public static final String SSS_PENSION_TYPE_PENSIONER = "SSS Pensioner";
	public static final String SSS_PENSION_TYPE_NON_PENSIONER = "Non-SSS Pensioner";

	public static final String SSS_MEMBER_TYPE_EMPLOYED = "Employed";
	public static final String SSS_MEMBER_TYPE_SELF_EMPLOYED = "Self Employed";
	public static final String SSS_MEMBER_TYPE_HOUSEHOLD_HELPER = "Household Helper";
	public static final String SSS_MEMBER_TYPE_VOLUNTARY_MEMBER = "Voluntary Member";
	public static final String SSS_MEMBER_TYPE_NON_WORKING_SPOUSE = "Non Working Spouse";
	public static final String SSS_MEMBER_TYPE_NON_OFW = "Overseas Contract Worker";

	private String	ss_pension_type;
	private String	ss_pension_account_number;
	private String	ss_membership_type;
	private String	ss_employer_id;
	private String	ss_receipt_number;
	private String	ss_doc1;
	private String	ss_doc2;
	private String	ss_doc3;
	private String	ss_doc4;

	//PAGIBIG FIELDS
	public static final String PI_MEMBER_TYPE_EMPLOYED_PRIVATE = "Employed - Private";
	public static final String PI_MEMBER_TYPE_EMPLOYED_GOVT = "Employed - Government";
	public static final String PI_MEMBER_TYPE_EMPLOYED_OFW = "Employed - OFW";
	public static final String PI_MEMBER_TYPE_EMPLOYED_PRIVATE_HOUSEOLD = "Employed - Household";
	public static final String PI_MEMBER_TYPE_EMPLOYED_SELF_EMPLOYED = "Self Employed";
	public static final String PI_MEMBER_TYPE_NOT_EMPLOYED = "Not Yet Employed";

	public static final String PI_EMPLOYEMENT_STATUS_PERMANENT = "Permanent";
	public static final String PI_EMPLOYEMENT_STATUS_CONTRACTUAL = "Contractual";
	public static final String PI_EMPLOYEMENT_STATUS_TEMPORARY = "Temporary / Part-time";
	public static final String PI_EMPLOYEMENT_STATUS_CASUAL = "Casual";
	public static final String PI_EMPLOYEMENT_STATUS_PROJECT_BASED = "Project Based";

	private String	pi_membership_type;
	private String	pi_employer_business_name;
	private String	pi_employee_number;
	private long	pi_date_start;
	private String	pi_profession;
	private String	pi_employment_status;
	private String	pi_address;
	private String	pi_basic;
	private String	pi_allowance;
	private String	pi_total;
	private String	pi_doc1;
	private String	pi_doc2;
	private String	pi_doc3;
	private String	pi_doc4;



	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
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
		if(created == 0){
			created = Calendar.getInstance().getTimeInMillis();
		}
		return created;
	}
	public void setCreated(long created) {
		this.created = created;
	}
	public long getUpdated() {
		if(updated == 0){
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
	public String getPassword_hash() {
		if(password_hash==null){
			password_hash = "";
		}
		return password_hash;
	}
	public void setPassword_hash(String password_hash) {
		this.password_hash = password_hash;
	}

	public String hashPassword(){
		return md5(password);
	}

	public static String md5(String s) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(s.getBytes());
			byte[] a = digest.digest();
			int len = a.length;
			StringBuilder sb = new StringBuilder(len << 1);
			for (int i = 0; i < len; i++) {
				sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
				sb.append(Character.forDigit(a[i] & 0x0f, 16));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
		return "";
	}
	public String getSss() {
		if(sss == null){
			sss = "";
		}

		return sss;
	}
	public void setSss(String sss) {
		this.sss = sss;
	}
	public String getPagibig() {
		if(pagibig == null){
			pagibig = "";
		}

		return pagibig;
	}
	public void setPagibig(String pagibig) {
		this.pagibig = pagibig;
	}
	public String getPhilhealth() {
		if(philhealth == null){
			philhealth = "";
		}

		return philhealth;
	}
	public void setPhilhealth(String philhealth) {
		this.philhealth = philhealth;
	}
	public String getTin() {
		if(tin == null){
			tin = "";
		}
		return tin;
	}
	public void setTin(String tin) {
		this.tin = tin;
	}
	public String getLandline() {
		if(landline == null){
			landline = "";
		}
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
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public String getCivilstatus() {
		return civilstatus;
	}
	public static List<String> getAvailableCivilstatuses() {
		List<String> statuses = new ArrayList<String>();
		statuses.add(SINGLE);
		statuses.add(MARRIED);
		statuses.add(WIDOW);
		statuses.add(WIDOWER);

		return statuses;
	}
	public void setCivilstatus(String civilstatus) {
		this.civilstatus = civilstatus;
	}
	public String getEmergency_person() {
		return emergency_person;
	}
	public void setEmergency_person(String emergency_person) {
		this.emergency_person = emergency_person;
	}
	public String getEmergency_number() {
		return emergency_number;
	}
	public void setEmergency_number(String emergency_number) {
		this.emergency_number = emergency_number;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public boolean isPassword_changed() {
		return password_changed;
	}
	public void setPassword_changed(boolean password_changed) {
		this.password_changed = password_changed;
	}

	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	public User getSnapShot() {
		return snapShot;
	}
	public void createSnapShot() throws CloneNotSupportedException {
		this.snapShot = (User)clone();
	}
	public String getSuffix() {
		if(suffix == null || suffix.equals("null")){
			suffix = "";
		}
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public String getMaiden_name() {
		return maiden_name;
	}
	public void setMaiden_name(String maiden_name) {
		this.maiden_name = maiden_name;
	}
	public String getOfc_number() {
		if(ofc_number == null){
			ofc_number = "";
		}
		return ofc_number;
	}
	public void setOfc_number(String ofc_number) {
		this.ofc_number = ofc_number;
	}
	public String getBen_firstName() {
		return ben_firstName;
	}
	public void setBen_firstName(String ben_firstName) {
		this.ben_firstName = ben_firstName;
	}
	public String getBen_middleName() {
		return ben_middleName;
	}
	public void setBen_middleName(String ben_middleName) {
		this.ben_middleName = ben_middleName;
	}
	public String getBen_lastName() {
		return ben_lastName;
	}
	public void setBen_lastName(String ben_lastName) {
		this.ben_lastName = ben_lastName;
	}
	public String getBen_suffix() {
		return ben_suffix;
	}
	public void setBen_suffix(String ben_suffix) {
		this.ben_suffix = ben_suffix;
	}
	public long getBen_birthday() {
		return ben_birthday;
	}
	public void setBen_birthday(long ben_birthday) {
		this.ben_birthday = ben_birthday;
	}
	public int getBen_gender() {
		return ben_gender;
	}
	public void setBen_gender(int ben_gender) {
		this.ben_gender = ben_gender;
	}
	public String getBen_civilstatus() {
		return ben_civilstatus;
	}
	public void setBen_civilstatus(String ben_civilstatus) {
		this.ben_civilstatus = ben_civilstatus;
	}
	public String getBen_maiden_name() {
		return ben_maiden_name;
	}
	public void setBen_maiden_name(String ben_maiden_name) {
		this.ben_maiden_name = ben_maiden_name;
	}
	public int getBen_disabled() {
		return ben_disabled;
	}
	public void setBen_disabled(int ben_disabled) {
		this.ben_disabled = ben_disabled;
	}


	public String getPh_pen() {
		return ph_pen;
	}
	public void setPh_pen(String ph_pen) {
		this.ph_pen = ph_pen;
	}
	public String getPh_employer_name() {
		return ph_employer_name;
	}
	public void setPh_employer_name(String ph_employer_name) {
		this.ph_employer_name = ph_employer_name;
	}
	public String getPh_employer_address() {
		return ph_employer_address;
	}
	public void setPh_employer_address(String ph_employer_address) {
		this.ph_employer_address = ph_employer_address;
	}
	public long getPh_date_hired() {
		return ph_date_hired;
	}
	public void setPh_date_hired(long ph_date_hired) {
		this.ph_date_hired = ph_date_hired;
	}
	public String getPh_profession() {
		return ph_profession;
	}
	public void setPh_profession(String ph_profession) {
		this.ph_profession = ph_profession;
	}
	public String getPh_country_based() {
		return ph_country_based;
	}
	public void setPh_country_based(String ph_country_based) {
		this.ph_country_based = ph_country_based;
	}
	public String getPh_foreign_address() {
		return ph_foreign_address;
	}
	public void setPh_foreign_address(String ph_foreign_address) {
		this.ph_foreign_address = ph_foreign_address;
	}
	public long getPh_duration_from() {
		return ph_duration_from;
	}
	public void setPh_duration_from(long ph_duration_from) {
		this.ph_duration_from = ph_duration_from;
	}
	public long getPh_duration_to() {
		return ph_duration_to;
	}
	public void setPh_duration_to(long ph_duration_to) {
		this.ph_duration_to = ph_duration_to;
	}
	public String getPh_member_type() {
		return ph_member_type;
	}
	public void setPh_member_type(String ph_member_type) {
		this.ph_member_type = ph_member_type;
	}
	public String getPh_family_income() {
		return ph_family_income;
	}
	public void setPh_family_income(String ph_family_income) {
		this.ph_family_income = ph_family_income;
	}

	public static List<String> getAvailableProfessions() {
		List<String> professions = new ArrayList<String>();
		professions.add("ACCOUNTANT");
		professions.add("AGRICULTURIST");
		professions.add("ARCHITECT");
		professions.add("ARTIST");
		professions.add("BUSINESSMAN/BUSIN ESS OWNER");
		professions.add("CONSULTANT");
		professions.add("CRIMINOLOGIST");
		professions.add("CUSTOMS BROKER");
		professions.add("DENTIST");
		professions.add("DIETICIAN");
		professions.add("ENGINEER-AERONAUTICAL");
		professions.add("ENGINEER-AGRICULTURAL");
		professions.add("ENGINEER-CHEMICAL");
		professions.add("ENGINEER-CIVIL");
		professions.add("ENGINEER-ELECTRICAL");
		professions.add("ENGINEER-ELECTRICAL COMMUNICATION");
		professions.add("ENGINEER-GEODETIC");
		professions.add("ENGINEER-MARINE");
		professions.add("ENGINEER-MECHANICAL");
		professions.add("ENGINEER-METALLURGICAL");
		professions.add("ENGINEER-MINING");
		professions.add("ENGINEER-SANITARY");
		professions.add("ENVIRONMENTAL PLANNER");
		professions.add("FISHERIES TECHNOLOGIST");
		professions.add("FORESTER");
		professions.add("GEOLOGIST");
		professions.add("GUIDANCE COUNSELOR");
		professions.add("INDUSTRIAL ENGINEER");
		professions.add("INTERIOR DESIGNER");
		professions.add("LAN DSCAPE ARCHITECI'");
		professions.add("LAW PRACTITION ER");
		professions.add("LIBRARIAN");
		professions.add("MARINE DECK OFFICER");
		professions.add("MARINE ENGINEER OFFICER");
		professions.add("MASTER PLUMBER");
		professions.add("MEDIA-ACTOR/ACFRESS");
		professions.add("MEDIA-DIRECTOR");
		professions.add("MEDIA-NEWS CORRESPONDENT");
		professions.add("MEDIA-SCRIPTWRITER");
		professions.add("MEDICAL DOCTOR");
		professions.add("MEDICAL TECH NOLOGIST");
		professions.add("MIDWIFE");
		professions.add("NAVAL ARCHITECT");
		professions.add("NURSE");
		professions.add("NUTRITIONIST");
		professions.add("OPTOMETRIST");
		professions.add("OTHER");
		professions.add("PHARMACIST");
		professions.add("PHYSICAL AND OCCUPATIONAL THERAPIST");
		professions.add("PROFESSIONAL ATHLETE");
		professions.add("PROFESSIONAL COACH");
		professions.add("PROFESSIONAL REFEREE");
		professions.add("PROFESSIONAL TEACHER");
		professions.add("PROFESSIONAL TRAINOR");
		professions.add("RADIOLOCIST AND X-RAY TECHNICIAN");
		professions.add("SOCIAL WORKER");
		professions.add("SUGAR TECHNOLOCIST");
		professions.add("VETERINARIAN");

		return professions;
	}

	public static List<String> getAvailablePhilhealthMembershipTypes() {
		List<String> types = new ArrayList<String>();
		types.add(PHILHEALTH_MEMBER_TYPE_OTHERS);
		types.add(PHILHEALTH_MEMBER_TYPE_SELF_EMP_NON_PRO);
		types.add(PHILHEALTH_MEMBER_TYPE_SELF_EMP_PRO);


		return types;
	}

	public static List<String> getAvailablePhilhealthFamilyIncome() {
		List<String> types = new ArrayList<String>();
		types.add(PHILHEALTH_FAMILY_INCOME_25K_ABOVE);
		types.add(PHILHEALTH_FAMILY_INCOME_25K_BELOW);

		return types;
	}
	public String getSs_pension_type() {
		return ss_pension_type;
	}
	public void setSs_pension_type(String ss_pension_type) {
		this.ss_pension_type = ss_pension_type;
	}
	public String getSs_pension_account_number() {
		return ss_pension_account_number;
	}
	public void setSs_pension_account_number(String ss_pension_account_number) {
		this.ss_pension_account_number = ss_pension_account_number;
	}
	public String getSs_membership_type() {
		return ss_membership_type;
	}
	public void setSs_membership_type(String ss_membership_type) {
		this.ss_membership_type = ss_membership_type;
	}
	public String getSs_employer_id() {
		return ss_employer_id;
	}
	public void setSs_employer_id(String ss_employer_id) {
		this.ss_employer_id = ss_employer_id;
	}
	public String getSs_receipt_number() {
		return ss_receipt_number;
	}
	public void setSs_receipt_number(String ss_receipt_number) {
		this.ss_receipt_number = ss_receipt_number;
	}


	public static List<String> getAvailableSssPensionTypes() {
		List<String> types = new ArrayList<String>();
		types.add(SSS_PENSION_TYPE_PENSIONER);
		types.add(SSS_PENSION_TYPE_NON_PENSIONER);

		return types;
	}

	public static List<String> getAvailableSssMemberTypes() {
		List<String> types = new ArrayList<String>();
		types.add(SSS_MEMBER_TYPE_EMPLOYED);
		types.add(SSS_MEMBER_TYPE_SELF_EMPLOYED);
		types.add(SSS_MEMBER_TYPE_HOUSEHOLD_HELPER);
		types.add(SSS_MEMBER_TYPE_VOLUNTARY_MEMBER);
		types.add(SSS_MEMBER_TYPE_NON_WORKING_SPOUSE);
		types.add(SSS_MEMBER_TYPE_NON_OFW);

		return types;
	}
	public void setSnapShot(User snapShot) {
		this.snapShot = snapShot;
	}

	public static List<String> getAvailablePagibigMemberTypes() {
		List<String> types = new ArrayList<String>();
		types.add(PI_MEMBER_TYPE_EMPLOYED_PRIVATE);
		types.add(PI_MEMBER_TYPE_EMPLOYED_GOVT);
		types.add(PI_MEMBER_TYPE_EMPLOYED_OFW);
		types.add(PI_MEMBER_TYPE_EMPLOYED_PRIVATE_HOUSEOLD);
		types.add(PI_MEMBER_TYPE_EMPLOYED_SELF_EMPLOYED);
		types.add(PI_MEMBER_TYPE_NOT_EMPLOYED);


		return types;
	}

	public static List<String> getAvailablePagibigEmploymentTypes() {
		List<String> types = new ArrayList<String>();
		types.add(PI_EMPLOYEMENT_STATUS_PERMANENT);
		types.add(PI_EMPLOYEMENT_STATUS_CONTRACTUAL);
		types.add(PI_EMPLOYEMENT_STATUS_TEMPORARY);
		types.add(PI_EMPLOYEMENT_STATUS_CASUAL);
		types.add(PI_EMPLOYEMENT_STATUS_PROJECT_BASED);


		return types;
	}
	public String getPi_employer_business_name() {
		return pi_employer_business_name;
	}
	public void setPi_employer_business_name(String pi_employer_business_name) {
		this.pi_employer_business_name = pi_employer_business_name;
	}
	public long getPi_date_start() {
		return pi_date_start;
	}
	public void setPi_date_start(long pi_date_start) {
		this.pi_date_start = pi_date_start;
	}
	public String getPi_profession() {
		return pi_profession;
	}
	public void setPi_profession(String pi_profession) {
		this.pi_profession = pi_profession;
	}
	public String getPi_employment_status() {
		return pi_employment_status;
	}
	public void setPi_employment_status(String pi_employment_status) {
		this.pi_employment_status = pi_employment_status;
	}
	public String getPi_address() {
		return pi_address;
	}
	public void setPi_address(String pi_address) {
		this.pi_address = pi_address;
	}
	public String getPi_basic() {
		return pi_basic;
	}
	public void setPi_basic(String pi_basic) {
		this.pi_basic = pi_basic;
	}
	public String getPi_allowance() {
		return pi_allowance;
	}
	public void setPi_allowance(String pi_allowance) {
		this.pi_allowance = pi_allowance;
	}
	public String getPi_total() {
		return pi_total;
	}
	public void setPi_total(String pi_total) {
		this.pi_total = pi_total;
	}
	public String getPi_membership_type() {
		return pi_membership_type;
	}
	public void setPi_membership_type(String pi_membership_type) {
		this.pi_membership_type = pi_membership_type;
	}
	public String getPi_employee_number() {
		return pi_employee_number;
	}
	public void setPi_employee_number(String pi_employee_number) {
		this.pi_employee_number = pi_employee_number;
	}
	public String getPh_member_category() {
		return ph_member_category;
	}
	public void setPh_member_category(String ph_member_category) {
		this.ph_member_category = ph_member_category;
	}
	public String getPh_doc1() {
		return ph_doc1;
	}
	public void setPh_doc1(String ph_doc1) {
		this.ph_doc1 = ph_doc1;
	}
	public String getPh_doc2() {
		return ph_doc2;
	}
	public void setPh_doc2(String ph_doc2) {
		this.ph_doc2 = ph_doc2;
	}
	public String getPh_doc3() {
		return ph_doc3;
	}
	public void setPh_doc3(String ph_doc3) {
		this.ph_doc3 = ph_doc3;
	}
	public String getSs_doc1() {
		return ss_doc1;
	}
	public void setSs_doc1(String ss_doc1) {
		this.ss_doc1 = ss_doc1;
	}
	public String getSs_doc2() {
		return ss_doc2;
	}
	public void setSs_doc2(String ss_doc2) {
		this.ss_doc2 = ss_doc2;
	}
	public String getSs_doc3() {
		return ss_doc3;
	}
	public void setSs_doc3(String ss_doc3) {
		this.ss_doc3 = ss_doc3;
	}
	public String getPi_doc1() {
		return pi_doc1;
	}
	public void setPi_doc1(String pi_doc1) {
		this.pi_doc1 = pi_doc1;
	}
	public String getPi_doc2() {
		return pi_doc2;
	}
	public void setPi_doc2(String pi_doc2) {
		this.pi_doc2 = pi_doc2;
	}
	public String getPi_doc3() {
		return pi_doc3;
	}
	public void setPi_doc3(String pi_doc3) {
		this.pi_doc3 = pi_doc3;
	}
	public String getPh_doc4() {
		return ph_doc4;
	}
	public void setPh_doc4(String ph_doc4) {
		this.ph_doc4 = ph_doc4;
	}
	public String getSs_doc4() {
		return ss_doc4;
	}
	public void setSs_doc4(String ss_doc4) {
		this.ss_doc4 = ss_doc4;
	}
	public String getPi_doc4() {
		return pi_doc4;
	}
	public void setPi_doc4(String pi_doc4) {
		this.pi_doc4 = pi_doc4;
	}
	public String getTermination_image() {
		return termination_image;
	}
	public void setTermination_image(String termination_image) {
		this.termination_image = termination_image;
	}
	public String getTradeName() {
		return tradeName;
	}
	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
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
	public String getBusinessURL() {
		return businessURL;
	}
	public void setBusinessURL(String businessURL) {
		this.businessURL = businessURL;
	}
	public int getVerified() {
		return verified;
	}
	public void setVerified(int verified) {
		this.verified = verified;
	}
	public String getOwner_id() {
		return owner_id;
	}
	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}
	public String getFacebookURL() {
		return facebookURL;
	}
	public void setFacebookURL(String facebookURL) {
		this.facebookURL = facebookURL;
	}
	public String getGplusURL() {
		return gplusURL;
	}
	public void setGplusURL(String gplusURL) {
		this.gplusURL = gplusURL;
	}
	public String getTwitterURL() {
		return twitterURL;
	}
	public void setTwitterURL(String twitterURL) {
		this.twitterURL = twitterURL;
	}

}
