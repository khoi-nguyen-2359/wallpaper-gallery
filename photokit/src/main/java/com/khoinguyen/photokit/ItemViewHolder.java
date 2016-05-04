package com.khoinguyen.photokit;

import android.view.View;

/**
 * Created by khoinguyen on 4/29/16.
 */
public abstract class ItemViewHolder<DATA> {
    protected View itemView;
    protected int itemIndex;

    public ItemViewHolder(View itemView) {
        this.itemView = itemView;
    }

    public void bindItemData(int itemIndex, DATA data) {
        this.itemIndex = itemIndex;
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public View getItemView() {
        return this.itemView;
    }
}
