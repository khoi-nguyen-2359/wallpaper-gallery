package com.xkcn.gallery.view.navigator;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.khoinguyen.util.log.L;
import com.xkcn.gallery.R;
import com.xkcn.gallery.view.fragment.PhotoCollectionFragment;

/**
 * Created by khoinguyen on 9/11/16.
 */

public class CollectionNavigator implements Navigator {
	private String data;

	public CollectionNavigator(String data) {
		this.data = data;
	}

	@Override
	public void navigate(FragmentActivity activity) {
		L.get().d("collection navigator");

		FragmentManager fragMan = activity.getSupportFragmentManager();
		fragMan
			.beginTransaction()
			.replace(R.id.fragment_container, PhotoCollectionFragment.instantiate(data))
			.commit();
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
