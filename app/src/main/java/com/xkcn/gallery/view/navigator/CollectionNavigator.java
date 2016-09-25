package com.xkcn.gallery.view.navigator;

import com.khoinguyen.util.log.L;
import com.xkcn.gallery.view.activity.BaseActivity;

/**
 * Created by khoinguyen on 9/11/16.
 */

public class CollectionNavigator implements Navigator {

	@Override
	public void navigate(BaseActivity baseActivity) {
		L.get().d("collection navigator");
	}
}
