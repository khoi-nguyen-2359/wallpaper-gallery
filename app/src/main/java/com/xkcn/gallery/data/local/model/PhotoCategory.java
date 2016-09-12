package com.xkcn.gallery.data.local.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by khoinguyen on 4/29/16.
 */
public class PhotoCategory implements Parcelable {
	public final static PhotoCategory LATEST;
	public final static PhotoCategory HOSTEST;
	public static final Parcelable.Creator<PhotoCategory> CREATOR
		= new Parcelable.Creator<PhotoCategory>() {
		public PhotoCategory createFromParcel(Parcel in) {
			return new PhotoCategory(in);
		}

		public PhotoCategory[] newArray(int size) {
			return new PhotoCategory[size];
		}
	};

	static {
		LATEST = new PhotoCategory(1, "LATEST");
		HOSTEST = new PhotoCategory(2, "HOTEST");
	}

	private int id;
	private String name;

	public PhotoCategory(int id, String name) {
		this.id = id;
		this.name = name;
	}

	private PhotoCategory(Parcel in) {
		id = in.readInt();
		name = in.readString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
	}

	public int getId() {
		return id;
	}
}
