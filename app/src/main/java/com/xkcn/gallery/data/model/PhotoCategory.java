package com.xkcn.gallery.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by khoinguyen on 4/29/16.
 */
public class PhotoCategory implements Parcelable {
  String category;

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(category);
  }

  public static final Parcelable.Creator<PhotoCategory> CREATOR
      = new Parcelable.Creator<PhotoCategory>() {
    public PhotoCategory createFromParcel(Parcel in) {
      return new PhotoCategory(in);
    }

    public PhotoCategory[] newArray(int size) {
      return new PhotoCategory[size];
    }
  };

  private PhotoCategory(Parcel in) {
    category = in.readString();
  }
}
