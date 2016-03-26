package com.xkcn.gallery.view;

import com.xkcn.gallery.data.model.PhotoDetails;

import java.util.HashMap;
import java.util.List;

/**
 * Created by khoinguyen on 12/14/15.
 */
public interface PhotoSinglePagerView extends PhotoListingView {
    void setupPagerAdapter(List<PhotoDetails> photoDetailses);
    PhotoPagerLoadingTracker getPhotoPagerLoadingTracker();

    class PhotoPagerLoadingTracker {
        public static final int STATUS_UNSTARTED = 0;
        public static final int STATUS_STARTED_LOADING = 1;
        public static final int STATUS_LOADED = 2;

        private HashMap<Long, Integer> mapPhotoLoadedStatus;
        private Long currentPagePhotoIdentifier;

        public void setup(List<PhotoDetails> allPhotoDetails, int currentPagePosition) {
            if (mapPhotoLoadedStatus == null) {
                mapPhotoLoadedStatus = new HashMap<>();
            }

            mapPhotoLoadedStatus.clear();
            if (allPhotoDetails != null && !allPhotoDetails.isEmpty() && currentPagePosition >= 0 && currentPagePosition < allPhotoDetails.size()) {
                this.currentPagePhotoIdentifier = allPhotoDetails.get(currentPagePosition).getIdentifier();

                for (PhotoDetails photo : allPhotoDetails) {
                    mapPhotoLoadedStatus.put(photo.getIdentifier(), STATUS_UNSTARTED);
                }
            }
        }

        public int getPhotoStatus(Long identifier) {
            return mapPhotoLoadedStatus.get(identifier);
        }

        public Long getCurrentPagePhotoIdentifier() {
            return currentPagePhotoIdentifier;
        }

        public int getCurrentPhotoStatus() {
            return mapPhotoLoadedStatus.get(currentPagePhotoIdentifier);
        }

        public boolean isCurrentPhoto(long identifier) {
            return currentPagePhotoIdentifier == identifier;
        }

        public void changeCurrentPhotoPage(long identifier) {
            currentPagePhotoIdentifier = identifier;
        }

        public void setPhotoStatus(long identifier, int status) {
            mapPhotoLoadedStatus.put(identifier, status);
        }
    }
}
