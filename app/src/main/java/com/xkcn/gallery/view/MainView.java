package com.xkcn.gallery.view;

import com.xkcn.gallery.data.model.PhotoDetails;

import java.io.File;
import java.util.List;

/**
 * Created by khoinguyen on 12/13/15.
 */
public interface MainView extends ScreenView {
  void startActionUpdate();

  void appendPhotoData(int page, List<PhotoDetails> photos);

  void showWallpaperChooser(File photoFile);
}
