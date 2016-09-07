package com.khoinguyen.photoviewerkit.interfaces;

import android.graphics.Matrix;

import com.khoinguyen.apptemplate.listing.pageable.IPageableListingView;

/**
 * Created by khoinguyen on 5/13/16.
 */
public interface IPhotoGalleryView<D> extends IPhotoViewerKitComponent<D>, IPageableListingView {
	void translate(float x, float y);

	void setCurrentPhoto(String photoId);

	void setCurrentPhoto(int itemIndex);

	void show();

	void zoomPrimaryItem(Matrix transformMatrix);

	void hide();
}
