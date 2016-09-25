package com.xkcn.gallery.view.navigator;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.khoinguyen.util.log.L;
import com.xkcn.gallery.R;
import com.xkcn.gallery.data.cloud.model.PhotoCollection;
import com.xkcn.gallery.view.fragment.PhotoCollectionFragment;

/**
 * Created by khoinguyen on 9/11/16.
 */

public class CollectionNavigator implements Navigator {
	private PhotoCollection photoCollection;

	@Override
	public void navigate(FragmentActivity activity) {
		L.get().d("collection navigator");

		FragmentManager fragMan = activity.getSupportFragmentManager();
		fragMan
			.beginTransaction()
			.replace(R.id.fragment_container, PhotoCollectionFragment.instantiate(photoCollection))
			.commit();
	}

	public PhotoCollection getPhotoCollection() {
		return photoCollection;
	}

	public void setPhotoCollection(PhotoCollection photoCollection) {
		this.photoCollection = photoCollection;
	}
}
