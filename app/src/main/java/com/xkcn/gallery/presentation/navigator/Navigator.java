package com.xkcn.gallery.presentation.navigator;

import android.support.v4.app.FragmentActivity;

import java.io.Serializable;

/**
 * Created by khoinguyen on 9/11/16.
 */
public interface Navigator extends Serializable {
	void navigate(FragmentActivity mainActivity);
}
