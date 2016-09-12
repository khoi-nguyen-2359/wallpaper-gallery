package com.xkcn.gallery.model.mapper;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class ModelMapper<S, D> {

	public D map(S source) {
		if (source == null) {
			return null;
		}

		return doMapping(source);
	}

	protected abstract D doMapping(@NonNull S source);

	public List<D> map(List<S> sourceList) {
		List<D> desList = null;
		if (sourceList != null) {
			desList = new ArrayList<>();
			for (S source : sourceList) {
				D des = map(source);
				if (des != null) {
					desList.add(des);
				}
			}
		}
		return desList;
	}
}
