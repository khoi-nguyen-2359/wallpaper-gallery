package com.khoinguyen.apptemplate.listing.adapter;

import android.database.Observable;

/**
 * Created by khoinguyen on 5/18/16.
 */
public class DataObservable extends Observable<DataObserver> {
	public void notifyChanged() {
		for (int i = mObservers.size() - 1; i >= 0; i--) {
			mObservers.get(i).onChanged();
		}
	}
}
