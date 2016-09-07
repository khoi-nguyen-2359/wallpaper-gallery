package com.khoinguyen.photoviewerkit.util;

import android.text.TextUtils;

import com.khoinguyen.apptemplate.listing.adapter.IListingAdapter;
import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;

/**
 * Created by khoinguyen on 5/9/16.
 */
public class AdapterPhotoFinder {
	public static final int NO_POSITION = -1;
	private IListingAdapter adapter;

	public AdapterPhotoFinder(IListingAdapter adapter) {
		this.adapter = adapter;
	}

	public PhotoDisplayInfo getPhoto(int itemIndex) {
		Object data = adapter.getData(itemIndex);
		if (data == null || !(data instanceof PhotoDisplayInfo)) {
			return null;
		}

		return (PhotoDisplayInfo) data;
	}

	/**
	 * Search index of item which has backed photo id matches the given id.<br/>
	 *
	 * @param photoId id to search
	 * @return item index or -1 if not found
	 */
	public int indexOf(String photoId) {
		if (TextUtils.isEmpty(photoId)) {
			return NO_POSITION;
		}

		final int itemCount = adapter.getCount();
		for (int i = 0; i < itemCount; ++i) {
			PhotoDisplayInfo displayInfo = getPhoto(i);
			if (displayInfo == null) {
				continue;
			}

			if (photoId.equals(displayInfo.getPhotoId())) {
				return i;
			}
		}

		return NO_POSITION;
	}

	public IListingAdapter getAdapter() {
		return adapter;
	}

	/**
	 * Find the last photo item's index
	 *
	 * @return
	 */
	public int lastPhotoIndex() {
		final int itemCount = adapter.getCount();
		for (int i = itemCount; i >= 0; --i) {
			PhotoDisplayInfo photo = getPhoto(i);
			if (photo == null) {
				continue;
			}

			return i;
		}

		return NO_POSITION;
	}
}
