package com.xkcn.gallery.view.interfaces;

import com.xkcn.gallery.data.model.PhotoDetails;

/**
 * Created by khoinguyen on 12/13/15.
 */
public interface MainView extends ScreenView {
	void startActionUpdate();

	void onPagingLoaded();

	void showWallpaperChooser(PhotoDetails photoDetails);

	void enablePaging();

	void updateDownloadProgress(PhotoDetails photoDetails, Float progress);

	void showDownloadComplete(PhotoDetails photoDetails);

	void showDownloadError(PhotoDetails photoDetails, String message);

	void showSharingChooser(PhotoDetails photoDetails);
}
