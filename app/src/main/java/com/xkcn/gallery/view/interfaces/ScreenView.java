package com.xkcn.gallery.view.interfaces;

/**
 * Created by khoinguyen on 12/14/15.
 */
public interface ScreenView {
	void showToast(String message);

	void showProgressLoading(int messageResId);

	/**
	 * @param progress [0 100]
	 */
	void updateProgressLoading(int progress);

	void hideLoading();
}
