package com.xkcn.gallery.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by khoinguyen on 3/2/15.
 */
public class PhotoTag implements Parcelable {
  String tag;
  int mark;
  int status;

  public PhotoTag() {

  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public int getMark() {
    return mark;
  }

  public void setMark(int mark) {
    this.mark = mark;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(tag);
    dest.writeInt(mark);
    dest.writeInt(status);
  }

  public static final Parcelable.Creator<PhotoTag> CREATOR
      = new Parcelable.Creator<PhotoTag>() {
    public PhotoTag createFromParcel(Parcel in) {
      return new PhotoTag(in);
    }

    public PhotoTag[] newArray(int size) {
      return new PhotoTag[size];
    }
  };

  private PhotoTag(Parcel in) {
    tag = in.readString();
    mark = in.readInt();
    status = in.readInt();
  }
}
