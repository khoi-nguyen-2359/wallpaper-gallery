package com.xkcn.gallery.view.navigator;

import com.xkcn.gallery.view.activity.BaseActivity;

import java.io.Serializable;

/**
 * Created by khoinguyen on 9/11/16.
 */
public interface Navigator extends Serializable {
	void navigate(BaseActivity mainActivity);
}
