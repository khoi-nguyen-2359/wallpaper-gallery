package com.xkcn.gallery.event;

/**
 * Created by khoinguyen on 12/15/15.
 */
public class OnPhotoListItemClicked {
    private int clickedPosition;

    public OnPhotoListItemClicked(int clickedPosition) {
        this.clickedPosition = clickedPosition;
    }

    public int getClickedPosition() {
        return clickedPosition;
    }
}
