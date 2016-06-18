package com.xkcn.gallery.data.model;

import java.util.List;

/**
 * Created by khoinguyen on 6/18/16.
 */
public class PhotoDetailsPage {
  private List<PhotoDetails> photos;
  private int page;

  public List<PhotoDetails> getPhotos() {
    return photos;
  }

  public void setPhotos(List<PhotoDetails> photos) {
    this.photos = photos;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }
}
