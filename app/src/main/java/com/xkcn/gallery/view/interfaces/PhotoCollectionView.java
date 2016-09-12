package com.xkcn.gallery.view.interfaces;

import com.xkcn.gallery.data.local.model.PhotoDetails;

/**
 * Created by khoinguyen on 12/13/15.
 */
public interface PhotoCollectionView {
	void onPagingLoaded();

	void showWallpaperChooser(PhotoDetails photoDetails);

	void enablePaging();

	void updateDownloadProgress(PhotoDetails photoDetails, Float progress);

	void showDownloadComplete(PhotoDetails photoDetails);

	void showDownloadError(PhotoDetails photoDetails, String message);

	void showSharingChooser(PhotoDetails photoDetails);

	void showToast(String message);

	void showProgressLoading(int messageResId);

	void updateProgressLoading(int progress);

	void hideLoading();
}
