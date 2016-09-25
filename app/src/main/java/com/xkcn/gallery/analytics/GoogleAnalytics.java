package com.xkcn.gallery.analytics;

import android.content.Context;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.xkcn.gallery.BuildConfig;
import com.xkcn.gallery.data.local.model.PhotoDetails;

/**
 * Created by khoinguyen on 7/21/16.
 */

public class GoogleAnalytics implements IAnalytics {
	private static final int DISPATCH_PERIOD = BuildConfig.GA_DISPATCH_PERIOD;
	private static final String TRACKER_ID = "UA-81048549-1";

	private static final String CAT_PHOTO_ACTION = "Photo Action";
	private static final String CAT_LISTING = "Listing";

	private static final String ACTION_SET_WALLPAPER = "Set Wallpaper";
	private static final String ACTION_DOWNLOAD = "Download";
	private static final String ACTION_SHARE = "Share";
	private static final String ACTION_LOAD = "Load";

	private static final String LABEL_END = "End";

	private static final int DIMEN_PHOTO_COLLECTION = 1;
	private static final int DIMEN_PHOTO_ID = 2;
	private static final int DIMEN_PHOTO_INDEX = 3;
	private static final int DIMEN_PHOTO_TITLE = 4;
	private static final int DIMEN_SCREEN_TITLE = 5;
	private static final int DIMEN_PHOTO_LINK = 6;

	private static final String VAL_SCREEN_TITLE_GALLERY_PHOTO = "Gallery Photo Screen";
	private static final String VAL_SCREEN_TITLE_LISTING = "Listing Screen";


	private Tracker tracker;

	public GoogleAnalytics(Context context) {
		com.google.android.gms.analytics.GoogleAnalytics analytics = com.google.android.gms.analytics.GoogleAnalytics.getInstance(context);
		analytics.setLocalDispatchPeriod(DISPATCH_PERIOD);

		tracker = analytics.newTracker(TRACKER_ID);
		tracker.enableAutoActivityTracking(false);
		tracker.enableExceptionReporting(true);
		tracker.setAnonymizeIp(false);
	}

	private static HitBuilders.EventBuilder buildPhotoDimensions(HitBuilders.EventBuilder builder, PhotoDetails photoDetails) {
		if (builder != null && photoDetails != null) {
			builder.setCustomDimension(DIMEN_PHOTO_ID, photoDetails.getIdentifierAsString())
				.setCustomDimension(DIMEN_PHOTO_TITLE, photoDetails.getPermalinkMetaAsText())
				.setCustomDimension(DIMEN_PHOTO_LINK, photoDetails.getDefaultDownloadUrl())
			;
		}

		return builder;
	}

	/**
	 * Because of this f**king <b>protected</b> {@link com.google.android.gms.analytics.HitBuilders.HitBuilder}.
	 *
	 * @param builder
	 * @param photoDetails
	 * @return
	 */
	private static HitBuilders.ScreenViewBuilder buildPhotoDimensions(HitBuilders.ScreenViewBuilder builder, PhotoDetails photoDetails) {
		if (builder != null && photoDetails != null) {
			builder.setCustomDimension(DIMEN_PHOTO_ID, photoDetails.getIdentifierAsString())
				.setCustomDimension(DIMEN_PHOTO_TITLE, photoDetails.getPermalinkMetaAsText())
				.setCustomDimension(DIMEN_PHOTO_LINK, photoDetails.getDefaultDownloadUrl())
			;
		}

		return builder;
	}

	@Override
	public void trackListingLastItem(String collectionName, int lastPhotoIndex) {
		tracker.send(new HitBuilders.EventBuilder()
			.setCategory(CAT_LISTING)
			.setAction(ACTION_LOAD)
			.setLabel(LABEL_END)
			.setCustomDimension(DIMEN_PHOTO_COLLECTION, collectionName)
			.setCustomDimension(DIMEN_PHOTO_INDEX, formatPhotoIndex(lastPhotoIndex))
			.build()
		);
	}

	private String formatPhotoIndex(int lastPhotoIndex) {
		return String.format("%010d", lastPhotoIndex);
	}

	@Override
	public void trackGalleryPhotoScreenView(PhotoDetails photoDetails) {
		trackScreenView(VAL_SCREEN_TITLE_GALLERY_PHOTO, buildPhotoDimensions(new HitBuilders.ScreenViewBuilder(), photoDetails));
	}

	private void trackScreenView(String screenName, HitBuilders.ScreenViewBuilder builder) {
		tracker.setScreenName(screenName);
		tracker.send(builder.build());
	}

	private void trackScreenView(String screenName) {
		trackScreenView(screenName, new HitBuilders.ScreenViewBuilder());
	}

	@Override
	public void trackListingScreenView() {
		trackScreenView(VAL_SCREEN_TITLE_LISTING);
	}

	@Override
	public void trackShareGalleryPhoto(PhotoDetails photoDetails) {
		tracker.send(buildPhotoDimensions(new HitBuilders.EventBuilder(), photoDetails)
			.setCategory(CAT_PHOTO_ACTION)
			.setAction(ACTION_SHARE)
			.setCustomDimension(DIMEN_SCREEN_TITLE, VAL_SCREEN_TITLE_GALLERY_PHOTO)
			.build()
		);
	}

	@Override
	public void trackSetWallpaperGalleryPhoto(PhotoDetails photoDetails) {
		tracker.send(buildPhotoDimensions(new HitBuilders.EventBuilder(), photoDetails)
			.setCategory(CAT_PHOTO_ACTION)
			.setAction(ACTION_SET_WALLPAPER)
			.setCustomDimension(DIMEN_SCREEN_TITLE, VAL_SCREEN_TITLE_GALLERY_PHOTO)
			.build()
		);
	}

	@Override
	public void trackDownloadGalleryPhoto(PhotoDetails photoDetails) {
		tracker.send(buildPhotoDimensions(new HitBuilders.EventBuilder(), photoDetails)
			.setCategory(CAT_PHOTO_ACTION)
			.setAction(ACTION_DOWNLOAD)
			.setCustomDimension(DIMEN_SCREEN_TITLE, VAL_SCREEN_TITLE_GALLERY_PHOTO)
			.build()
		);
	}
}
