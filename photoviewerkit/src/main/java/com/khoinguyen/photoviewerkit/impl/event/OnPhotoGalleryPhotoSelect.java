package com.khoinguyen.photoviewerkit.impl.event;

import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;

/**
 * Created by khoinguyen on 4/26/16.
 */
public class OnPhotoGalleryPhotoSelect {
	private final int itemIndex;
	private final PhotoDisplayInfo photoDisplayInfo;

	public OnPhotoGalleryPhotoSelect(int itemIndex, PhotoDisplayInfo photoDisplayInfo) {
		this.itemIndex = itemIndex;
		this.photoDisplayInfo = photoDisplayInfo;
	}

	public int getItemIndex() {
		return itemIndex;
	}

	public PhotoDisplayInfo getPhotoDisplayInfo() {
		return photoDisplayInfo;
	}
}
