package com.xkcn.gallery.view;

import com.xkcn.gallery.data.model.DataPage;
import com.xkcn.gallery.data.model.PhotoDetails;

import java.io.File;

/**
 * Created by khoinguyen on 12/13/15.
 */
public interface MainView extends ScreenView {
  void startActionUpdate();

  void onPagingLoaded();

  void showWallpaperChooser(File photoFile);

  void enablePaging();

  void updateDownloadProgress(PhotoDetails photoDetails, Float progress);

  void showDownloadComplete(PhotoDetails photoDetails);

  void showDownloadError(PhotoDetails photoDetails, String message);
}
