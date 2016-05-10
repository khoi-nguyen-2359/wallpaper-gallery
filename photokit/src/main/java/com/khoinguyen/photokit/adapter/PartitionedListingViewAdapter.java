package com.khoinguyen.photokit.adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khoinguyen on 5/7/16.
 */
public abstract class PartitionedListingViewAdapter extends RecycledListingViewAdapter {
    protected final List<PartDefinition> dataSet = new ArrayList<>();

    public abstract List<PartDefinition> updateDataSet();

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object getViewType(int itemIndex) {
        PartDefinition part = dataSet.get(itemIndex);
        return part.getViewType();
    }

    @Override
    public Object getData(int itemIndex) {
        return dataSet.get(itemIndex).getData();
    }
}
