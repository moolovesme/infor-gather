package com.dabeshackers.infor.gather.entities;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.dabeshackers.infor.gather.application.ApplicationUtils;
import com.dabeshackers.infor.gather.helpers.Base64Helper;

import android.content.Context;

public class Media implements Serializable, Cloneable {
	@SuppressWarnings("unused")
	private static final String TAG = Media.class.getSimpleName();
	private static final long serialVersionUID = 5142856349857391415L;

	public static final int MEDIA_STATUS_NEW = 0;
	public static final int MEDIA_STATUS_EDITED = 1;
	public static final int MEDIA_STATUS_DELETED = 2;

	public static final String MEDIA_TYPE_BITMAP = "Bitmap";
	public static final String MEDIA_TYPE_AMR = "Audio";
	public static final String MEDIA_TYPE_VIDEO = "Video";
	public static final String MEDIA_TYPE_DOCUMENT = "Document";

	public static final String MEDIA_EXT_JPEG = ".jpg";
	public static final String MEDIA_EXT_PNG = ".png";
	public static final String MEDIA_EXT_MP4 = ".mp4";
	public static final String MEDIA_EXT_AVI = ".avi";
	public static final String MEDIA_EXT_3GP = ".3gp";
	public static final String MEDIA_EXT_AMR = ".amr";
	public static final String MEDIA_EXT_WAV = ".wav";

	transient private Context context;

	private String id;
	private String owner_id;
	transient private byte[] content;
	transient private String content_string;
	private String type;
	private String name;
	private String localFilePath;
	private int status;

	private String edited_by;
	private long created;
	private long updated;
	private int version;

	private Media snapShot;

	public Media(Context context) {
		this.context = context;
	}

	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public Media getSnapShot() {
		return snapShot;
	}

	public void createSnapShot() throws CloneNotSupportedException {
		this.snapShot = (Media) clone();
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

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent_string() {
		return content_string;
	}

	public void setContent_string(String content_string) {
		this.content_string = content_string;
	}

	public void convertContentStringToBytes() throws IOException {
		// Log.i(TAG, "String to be converted:" + content_string);
		// setContent(new Base64().decode(content_string.getBytes()));
		setContent(Base64Helper.decode(content_string));

	}

	public String getLocalFilePath() {
		if (localFilePath == null || localFilePath.length() == 0) {
			localFilePath = new File(ApplicationUtils.Paths.getLocalAppImagesFolder(context), getOwner_id() + "/" + getName()).getAbsolutePath();
		}
		return localFilePath;
	}

	public void setLocalFilePath(String localFilePath) {
		this.localFilePath = localFilePath;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

}
