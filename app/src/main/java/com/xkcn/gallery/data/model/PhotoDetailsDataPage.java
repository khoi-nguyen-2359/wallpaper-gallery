package com.xkcn.gallery.data.model;

import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.xkcn.gallery.imageloader.PhotoFileManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by khoinguyen on 6/18/16.
 */
public class PhotoDetailsDataPage extends DataPage<PhotoDetails> {
	@Inject
	PhotoFileManager photoFileManager;
	private List<PhotoDisplayInfo> allDisplayInfos = new ArrayList<>();

	public PhotoDetailsDataPage() {
		super(null, 0);
	}

	@Override
	public void append(DataPage<PhotoDetails> nextPage) {
		super.append(nextPage);

		appendDisplayInfo(nextPage);
	}

	private void appendDisplayInfo(DataPage<PhotoDetails> nextPage) {
		if (nextPage == null) {
			return;
		}

		List<PhotoDetails> photos = nextPage.getData();
		if (photos == null || photos.isEmpty()) {
			return;
		}

		for (PhotoDetails photoDetails : photos) {
			PhotoDisplayInfo displayInfo = new PhotoDisplayInfo();
			displayInfo.setPhotoId(photoDetails.getIdentifierAsString());
			displayInfo.setDescription(photoDetails.getPermalinkMeta());
			displayInfo.setLowResUrl(photoDetails.getLowResUrl());
			displayInfo.setHighResUrl(photoDetails.getHighResUrl());
			displayInfo.setLocalFile(photoFileManager.getPhotoFile(photoDetails));

			allDisplayInfos.add(displayInfo);
		}
	}

	@Override
	public void reset() {
		super.reset();

		allDisplayInfos.clear();
	}

	@Override
	public void prepend(DataPage<PhotoDetails> prevPage) {
		super.prepend(prevPage);

		prependDisplayInfo(prevPage);
	}

	private void prependDisplayInfo(DataPage<PhotoDetails> prevPage) {
		if (prevPage == null) {
			return;
		}

		List<PhotoDetails> photos = prevPage.getData();
		if (photos == null || photos.isEmpty()) {
			return;
		}

		final int len = photos.size();
		for (int i = len - 1; i >= 0; --i) {
			PhotoDisplayInfo displayInfo = new PhotoDisplayInfo();
			PhotoDetails photoDetails = photos.get(i);
			displayInfo.setPhotoId(photoDetails.getIdentifierAsString());
			displayInfo.setDescription(photoDetails.getPermalinkMeta());
			displayInfo.setLowResUrl(photoDetails.getLowResUrl());
			displayInfo.setHighResUrl(photoDetails.getHighResUrl());
			displayInfo.setLocalFile(photoFileManager.getPhotoFile(photoDetails));

			allDisplayInfos.add(0, displayInfo);
		}
	}

	public List<PhotoDisplayInfo> getAllDisplayInfos() {
		return allDisplayInfos;
	}
}
