package com.xkcn.gallery.data.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by khoinguyen on 12/22/14.
 */
public class PhotoDetails implements Parcelable {
  String photo100;
  String photo250;
  String photo500;
  String photoHigh;
  long identifier;
  String permalink;
  String permalinkMeta;
  String notesUrl;
  int heightHighRes;
  int widthHighRes;
  String title;
  String tags;
  int notes;
  int status;

  public PhotoDetails() {
  }

  public String getDefaultDownloadUrl() {
    return photoHigh;
  }

  public String getPhoto100() {
    return photo100;
  }

  public void setPhoto100(String photo100) {
    this.photo100 = photo100;
  }

  public String getPhoto250() {
    return photo250;
  }

  public void setPhoto250(String photo250) {
    this.photo250 = photo250;
  }

  public String getPhoto500() {
    return photo500;
  }

  public void setPhoto500(String photo500) {
    this.photo500 = photo500;
  }

  public String getPhotoHigh() {
    return photoHigh;
  }

  public void setPhotoHigh(String photoHigh) {
    this.photoHigh = photoHigh;
  }

  public long getIdentifier() {
    return identifier;
  }

  public String getIdentifierAsString() {
    return String.valueOf(identifier);
  }

  public void setIdentifier(long identifier) {
    this.identifier = identifier;
  }

  public String getPermalink() {
    return permalink;
  }

  public void setPermalink(String permalink) {
    this.permalink = permalink;
  }

  public String getNotesUrl() {
    return notesUrl;
  }

  public void setNotesUrl(String notesUrl) {
    this.notesUrl = notesUrl;
  }

  public int getHeightHighRes() {
    return heightHighRes;
  }

  public void setHeightHighRes(int heightHighRes) {
    this.heightHighRes = heightHighRes;
  }

  public int getWidthHighRes() {
    return widthHighRes;
  }

  public void setWidthHighRes(int widthHighRes) {
    this.widthHighRes = widthHighRes;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }

  public String getPermalinkMeta() {
    return permalinkMeta;
  }

  public void setPermalinkMeta(String permalinkMeta) {
    this.permalinkMeta = permalinkMeta;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(photo100);
    dest.writeString(photo250);
    dest.writeString(photo500);
    dest.writeString(photoHigh);
    dest.writeLong(identifier);
    dest.writeString(permalink);
    dest.writeString(permalinkMeta);
    dest.writeString(notesUrl);
    dest.writeInt(heightHighRes);
    dest.writeInt(widthHighRes);
    dest.writeString(title);
    dest.writeString(tags);
    dest.writeInt(notes);
    dest.writeInt(status);
  }

  public static final Parcelable.Creator<PhotoDetails> CREATOR
      = new Parcelable.Creator<PhotoDetails>() {
    public PhotoDetails createFromParcel(Parcel in) {
      return new PhotoDetails(in);
    }

    public PhotoDetails[] newArray(int size) {
      return new PhotoDetails[size];
    }
  };

  private PhotoDetails(Parcel in) {
    photo100 = in.readString();
    photo250 = in.readString();
    photo500 = in.readString();
    photoHigh = in.readString();
    identifier = in.readLong();
    permalink = in.readString();
    permalinkMeta = in.readString();
    notesUrl = in.readString();
    heightHighRes = in.readInt();
    widthHighRes = in.readInt();
    title = in.readString();
    tags = in.readString();
    notes = in.readInt();
    status = in.readInt();
  }

  public int getNotes() {
    return notes;
  }

  public void setNotes(int notes) {
    this.notes = notes;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getStatus() {
    return status;
  }

  public String getLowResUrl() {
    return getPhoto250();
  }

  public String getHighResUrl() {
    return getPhoto500();
  }

  public Uri getLowResUri() {
    return Uri.parse(getLowResUrl());
  }

  public Uri getHighResUri() {
    return Uri.parse(getHighResUrl());
  }
}
