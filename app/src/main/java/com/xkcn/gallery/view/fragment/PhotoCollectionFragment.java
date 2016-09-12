package com.xkcn.gallery.view.fragment;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.khoinguyen.apptemplate.eventbus.IEventBus;
import com.khoinguyen.apptemplate.listing.adapter.PartitionedListingAdapter;
import com.khoinguyen.apptemplate.listing.item.BaseViewHolder;
import com.khoinguyen.apptemplate.listing.item.ListingItem;
import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoGalleryPhotoSelect;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoListingItemClick;
import com.khoinguyen.photoviewerkit.impl.event.OnShrinkTransitionEnd;
import com.khoinguyen.photoviewerkit.impl.view.PhotoActionView;
import com.khoinguyen.photoviewerkit.impl.view.PhotoGalleryView;
import com.khoinguyen.photoviewerkit.impl.view.PhotoListingView;
import com.khoinguyen.photoviewerkit.impl.view.PhotoOverlayView;
import com.khoinguyen.photoviewerkit.impl.view.PhotoViewerKitWidget;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoViewerKitWidget;
import com.khoinguyen.photoviewerkit.util.BottomLoadingIndicatorAdapter;
import com.khoinguyen.ui.UiUtils;
import com.xkcn.gallery.R;
import com.xkcn.gallery.adapter.PhotoActionAdapter;
import com.xkcn.gallery.data.local.model.PhotoDetails;
import com.xkcn.gallery.di.module.SystemServiceModule;
import com.xkcn.gallery.model.PhotoDownloadNotificationsInfo;
import com.xkcn.gallery.presenter.PhotoCollectionPresenter;
import com.xkcn.gallery.presenter.PhotoListingViewPresenter;
import com.xkcn.gallery.view.dialog.PhotoDownloadProgressDialog;
import com.xkcn.gallery.view.interfaces.PhotoCollectionView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by khoinguyen on 9/12/16.
 */

public class PhotoCollectionFragment extends BaseFragment implements PhotoCollectionView {
	private static final String ARG_COMMAND = "ARG_COMMAND";

	public static PhotoCollectionFragment instantiate(String command) {
		PhotoCollectionFragment f = new PhotoCollectionFragment();
		Bundle args = new Bundle();
		args.putString(ARG_COMMAND, command);
		f.setArguments(args);

		return f;
	}

	@Inject
	NotificationManager notificationManager;

	@BindView(R.id.toolbar)
	Toolbar toolbar;
	@BindView(R.id.photokit_widget)
	PhotoViewerKitWidget photoKitWidget;
	@BindView(R.id.photokit_photo_listing)
	PhotoListingView photoListingView;
	@BindView(R.id.photokit_photo_gallery)
	PhotoGalleryView photoGalleryView;
	@BindView(R.id.photokit_photo_overlay)
	PhotoOverlayView photoOverlayView;
	@BindView(R.id.app_bar)
	AppBarLayout appBarLayout;

	protected PhotoDownloadProgressDialog progressDialog;

	private NotificationCompat.Builder downloadNotificationBuilder;
	protected PhotoDownloadNotificationsInfo downloadNotificationsInfo = new PhotoDownloadNotificationsInfo();

	private PhotoListingViewPresenter photoListingPresenter;
	private PhotoCollectionPresenter photoCollectionPresenter;

	private IEventBus photoViewerKitEventBus;
	private Unbinder unbinder;

	private PhotoListingAdapter photoListingAdapter;
	private PhotoGalleryAdapter photoGalleryAdapter;
	private PhotoActionAdapter photoActionAdapter;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupDi();
		setupData();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_photo_collection, container, false);
		unbinder = ButterKnife.bind(this, rootView);
		setupViews();
		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();

		EventBus.getDefault().register(this);
		photoViewerKitEventBus.register(photoKitEventListener);
	}

	@Override
	public void onStop() {
		super.onStop();

		EventBus.getDefault().unregister(this);
		photoViewerKitEventBus.unregister(photoKitEventListener);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		photoListingPresenter.trackListingLastItem();
	}

	private void setupDi() {
		getApplicationComponent().systemServiceComponent(new SystemServiceModule(getContext())).inject(this);
	}

	private void setupData() {
		photoCollectionPresenter = new PhotoCollectionPresenter(photoFileManager, localConfigManager, schedulerBackground, remoteConfigManager);
		photoCollectionPresenter.setView(this);

		photoListingPresenter = new PhotoListingViewPresenter(getApplicationComponent());
		photoListingPresenter.setView(this);

		photoListingAdapter = new PhotoListingAdapter();
		photoListingAdapter.registerListingItemType(photoListingView.new PhotoItemType(PhotoListingAdapter.TYPE_PHOTO));

		photoGalleryAdapter = new PhotoGalleryAdapter();
		photoGalleryAdapter.registerListingItemType(new PhotoGalleryView.PhotoItemType(PhotoGalleryAdapter.TYPE_PHOTO));

		photoActionAdapter = new PhotoActionAdapter();
	}

	private void setupViews() {
		toolbar.setTitle(getString(R.string.app_name));

		photoViewerKitEventBus = photoKitWidget.getEventBus();

		photoKitWidget.setPagingListener(listingPagingListener);

		photoListingView.setPhotoAdapter(photoListingAdapter);
		photoGalleryView.setPhotoAdapter(photoGalleryAdapter);
		photoOverlayView.setPhotoActionAdapter(photoActionAdapter);
		photoOverlayView.setPhotoActionEventListener(photoActionListener);
	}

	private PhotoActionView.PhotoActionEventListener photoActionListener = new PhotoActionView.PhotoActionEventListener() {
		@Override
		public void onPhotoActionSelect(int actionId, PhotoDisplayInfo photo) {
			switch (actionId) {
				case PhotoActionAdapter.TYPE_SHARE: {
					sharePhoto(photo);
					break;
				}

				case PhotoActionAdapter.TYPE_SET_WALLPAPER: {
					setWallpaper(photo);
					break;
				}

				case PhotoActionAdapter.TYPE_DOWNLOAD: {
					downloadPhoto(photo);
					break;
				}
			}
		}
	};

	private void downloadPhoto(PhotoDisplayInfo photo) {
		PhotoDetails photoDetails = photoListingPresenter.findPhoto(photo);
		if (photoDetails == null) {
			return;
		}

		photoCollectionPresenter.downloadPhoto(photoDetails);
	}

	private void sharePhoto(PhotoDisplayInfo photo) {
		PhotoDetails photoDetails = photoListingPresenter.findPhoto(photo);
		if (photoDetails == null) {
			return;
		}

		photoCollectionPresenter.sharePhoto(photoDetails);
	}

	private void setWallpaper(PhotoDisplayInfo photo) {
		PhotoDetails photoDetails = photoListingPresenter.findPhoto(photo);
		if (photoDetails == null) {
			return;
		}

		photoCollectionPresenter.loadWallpaperSetting(photoDetails);
	}

	private IPhotoViewerKitWidget.PagingListener listingPagingListener = new IPhotoViewerKitWidget.PagingListener() {
		@Override
		public void onPagingNext(IPhotoViewerKitWidget widget) {
			photoListingPresenter.loadNextPhotoPage();
		}
	};

	@Override
	public void onPagingLoaded() {
		photoListingAdapter.updateDataSet();
		photoListingAdapter.notifyDataSetChanged();
		photoGalleryAdapter.updateDataSet();
		photoGalleryAdapter.notifyDataSetChanged();
	}

	@Override
	public void showWallpaperChooser(PhotoDetails photoDetails) {
		File photoFile = photoFileManager.getPhotoFile(photoDetails);
		if (!photoFile.exists()) {
			showToast(getString(R.string.general_alert_please_retry));
			return;
		}

		Uri uriImg = Uri.fromFile(photoFile);
		Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setDataAndType(uriImg, "image/*");
		intent.putExtra("mimeType", "image/*");

		startActivity(Intent.createChooser(intent, getString(R.string.photo_actions_set_wp_chooser)));

		analyticsCollection.trackSetWallpaperGalleryPhoto(photoDetails);
	}

	@Override
	public void enablePaging() {
		photoKitWidget.enablePaging();
	}

	@Override
	public void updateDownloadProgress(PhotoDetails photoDetails, Float progress) {
		NotificationCompat.Builder notificationBuilder = getDownloadNotificationBuilder()
			.setContentTitle(getString(R.string.download_notification_title, photoDetails.getIdentifierAsString()))
			.setContentText(getString(R.string.download_notification_downloading_message))
			.setOngoing(true)
			.setProgress(100, (int) (progress * 100), false);

		notificationManager.notify(downloadNotificationsInfo.getId(photoDetails), notificationBuilder.build());
	}

	private NotificationCompat.Builder getDownloadNotificationBuilder() {
		if (downloadNotificationBuilder == null) {
			downloadNotificationBuilder = new NotificationCompat.Builder(getContext())
				.setSmallIcon(R.drawable.ic_launcher);
		}

		return downloadNotificationBuilder;
	}

	@Override
	public void showDownloadComplete(PhotoDetails photoDetails) {
		Intent resultIntent = new Intent(Intent.ACTION_VIEW);
		resultIntent.setDataAndType(Uri.fromFile(photoFileManager.getPhotoFile(photoDetails)), "image/*");
		PendingIntent intentOpenExternal = PendingIntent.getActivity(getContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder notifBuilder = getDownloadNotificationBuilder()
			.setContentTitle(getString(R.string.download_notification_title, photoDetails.getIdentifierAsString()))
			.setContentText(getString(R.string.download_notification_completed))
			.setOngoing(false)
			.setContentIntent(intentOpenExternal)
			.setProgress(0, 0, false);

		notificationManager.notify(downloadNotificationsInfo.getId(photoDetails), notifBuilder.build());

		analyticsCollection.trackDownloadGalleryPhoto(photoDetails);
	}

	@Override
	public void showDownloadError(PhotoDetails photoDetails, String message) {
		NotificationCompat.Builder notifBuilder = getDownloadNotificationBuilder()
			.setContentTitle(getString(R.string.download_notification_title, photoDetails.getIdentifierAsString()))
			.setContentText(getString(R.string.download_notification_error, message))
			.setOngoing(false)
			.setProgress(0, 0, false);

		notificationManager.notify(downloadNotificationsInfo.getId(photoDetails), notifBuilder.build());
	}

	@Override
	public void showSharingChooser(PhotoDetails photoDetails) {
		Intent i = new Intent();
		i.setAction(Intent.ACTION_SEND);
		i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFileManager.getPhotoFile(photoDetails)));
		i.setType("image/*");

		startActivity(Intent.createChooser(i, getResources().getString(R.string.send_to)));

		analyticsCollection.trackShareGalleryPhoto(photoDetails);
	}

	@Override
	public void showToast(String message) {
		Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void showProgressLoading(int messageResId) {
		progressDialog = new PhotoDownloadProgressDialog(getContext());
		progressDialog.setMessage(getString(messageResId));
		progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				photoCollectionPresenter.cancelBlockingTask();
			}
		});
		progressDialog.show();
	}

	@Override
	public void updateProgressLoading(int progress) {
		if (progressDialog == null || !progressDialog.isShowing()) {
			return;
		}

		progressDialog.setProgress(progress);
	}

	@Override
	public void hideLoading() {
		progressDialog.setOnDismissListener(null);
		UiUtils.dismissDlg(progressDialog);
	}

	protected Object photoKitEventListener = new Object() {
		@com.khoinguyen.apptemplate.eventbus.Subscribe
		public void handleOnPhotoListingItemClick(OnPhotoListingItemClick event) {
			appBarLayout.setExpanded(false, false);
		}

		@com.khoinguyen.apptemplate.eventbus.Subscribe
		public void handleOnGalleryPhotoPageSelect(OnPhotoGalleryPhotoSelect event) {
			PhotoDetails photoDetails = photoListingPresenter.findPhoto(event.getPhotoDisplayInfo());
			analyticsCollection.trackGalleryPhotoScreenView(photoDetails);
		}

		@com.khoinguyen.apptemplate.eventbus.Subscribe
		public void handleOnShrinkTransitionEnd(OnShrinkTransitionEnd event) {
			analyticsCollection.trackListingScreenView();
		}
	};

	private class PhotoListingAdapter extends BottomLoadingIndicatorAdapter {
		public static final int TYPE_PHOTO = 2;

		@Override
		protected List<ListingItem> createDataSet() {
			List<ListingItem> listingItems = new ArrayList<>();
			List<PhotoDisplayInfo> allPhotoDisplayInfos = photoListingPresenter.getAllPages().getAllDisplayInfos();
			for (PhotoDisplayInfo displayInfo : allPhotoDisplayInfos) {
				listingItems.add(new ListingItem(displayInfo, TYPE_PHOTO));
			}

			return listingItems;
		}
	}

	private class PhotoGalleryAdapter extends PartitionedListingAdapter<BaseViewHolder> {
		public static final int TYPE_PHOTO = 1;

		@Override
		protected List<ListingItem> createDataSet() {
			List<ListingItem> listingItems = new ArrayList<>();
			List<PhotoDisplayInfo> allPhotoDisplayInfos = photoListingPresenter.getAllPages().getAllDisplayInfos();
			for (PhotoDisplayInfo displayInfo : allPhotoDisplayInfos) {
				ListingItem photoListingItem = new ListingItem(displayInfo, TYPE_PHOTO);
				listingItems.add(photoListingItem);
			}

			return listingItems;
		}
	}
}
