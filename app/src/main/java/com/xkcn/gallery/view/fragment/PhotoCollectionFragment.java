package com.xkcn.gallery.view.fragment;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.khoinguyen.apptemplate.eventbus.IEventBus;
import com.khoinguyen.apptemplate.listing.adapter.PartitionedListingAdapter;
import com.khoinguyen.apptemplate.listing.item.BaseViewHolder;
import com.khoinguyen.apptemplate.listing.item.ListingItem;
import com.khoinguyen.apptemplate.listing.item.ListingItemType;
import com.khoinguyen.apptemplate.listing.item.RecyclerListingViewHolder;
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
import com.khoinguyen.recyclerview.SimpleDividerItemDec;
import com.xkcn.gallery.R;
import com.xkcn.gallery.adapter.PhotoActionAdapter;
import com.xkcn.gallery.analytics.AnalyticsCollection;
import com.xkcn.gallery.data.cloud.model.PhotoCollection;
import com.xkcn.gallery.data.local.model.PhotoDetails;
import com.xkcn.gallery.databinding.FragmentPhotoCollectionBinding;
import com.xkcn.gallery.model.DataPage;
import com.xkcn.gallery.model.PhotoDownloadNotificationsInfo;
import com.xkcn.gallery.viewmodel.PhotoCollectionViewModel;
import com.xkcn.gallery.usecase.PhotoListingUsecase;
import com.xkcn.gallery.view.dialog.PhotoDownloadProgressDialog;

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

public class PhotoCollectionFragment extends BaseFragment {
	private static final String ARG_PHOTO_COLLECTION = "ARG_PHOTO_COLLECTION";
	private FragmentPhotoCollectionBinding binding;

	public static PhotoCollectionFragment instantiate(PhotoCollection photoCollection) {
		PhotoCollectionFragment f = new PhotoCollectionFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_PHOTO_COLLECTION, photoCollection);
		f.setArguments(args);

		return f;
	}

	@Inject
	NotificationManager notificationManager;
	@Inject
	PhotoListingUsecase photoListingUsecase;
	@Inject
	AnalyticsCollection analytics;

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

	protected PhotoDownloadProgressDialog photoLoadingProgressDialog;

	private NotificationCompat.Builder downloadNotificationBuilder;
	protected PhotoDownloadNotificationsInfo downloadNotificationsInfo = new PhotoDownloadNotificationsInfo();

	private PhotoCollectionViewModel photoCollectionViewModel;

	private IEventBus photoViewerKitEventBus;
	private Unbinder unbinder;

	private PhotoListingAdapter photoListingAdapter;
	private PhotoGalleryAdapter photoGalleryAdapter;
	private PhotoActionAdapter photoActionAdapter;

	private PhotoCollection photoCollection;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		readArgs();
		getApplicationComponent().inject(this);
	}

	private void readArgs() {
		Bundle args = getArguments();
		if (args == null) {
			return;
		}

		photoCollection = (PhotoCollection) args.getSerializable(ARG_PHOTO_COLLECTION);
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
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setupDataBinding();
		photoCollectionViewModel.loadFirstPhotoPage(photoCollection);
	}

	private void setupDataBinding() {
		binding = DataBindingUtil.bind(getView());
		photoCollectionViewModel = new PhotoCollectionViewModel(photoFileManager, photoListingUsecase, analytics);
		photoCollectionViewModel.obsLoadPhotoProgress.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				updatePhotoLoadingProgressDialog((int) photoCollectionViewModel.obsLoadPhotoProgress.get());
			}
		});
		photoCollectionViewModel.obsLoadWallpaperResult.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				showWallpaperChooser(photoCollectionViewModel.obsLoadWallpaperResult.get());
			}
		});
		photoCollectionViewModel.obsLastestPhotoPage.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				photoListingAdapter.removeProgressIndicator();
				appendPhotoAdapters(photoCollectionViewModel.obsLastestPhotoPage.get());
				enableWidgetPaging(photoCollectionViewModel.obsLastestPhotoPage.get());
			}
		});
		photoCollectionViewModel.obsDownloadError.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				showDownloadError(photoCollectionViewModel.obsDownloadError.get());
			}
		});
		photoCollectionViewModel.obsDownloadPhotoResult.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				showDownloadComplete(photoCollectionViewModel.obsDownloadPhotoResult.get());
			}
		});
		photoCollectionViewModel.obsDownloadPhotoProgress.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				updateDownloadProgress(photoCollectionViewModel.obsDownloadPhotoProgress.get());
			}
		});
		photoCollectionViewModel.obsDownloadPhotoResult.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				showDownloadComplete(photoCollectionViewModel.obsDownloadPhotoResult.get());
			}
		});
		photoCollectionViewModel.obsSharePhotoResult.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				showSharingChooser(photoCollectionViewModel.obsSharePhotoResult.get());
			}
		});
		binding.setPhotoCollection(photoCollectionViewModel);
	}

	private void enableWidgetPaging(DataPage<PhotoDisplayInfo> latestPhotoPage) {
		if (latestPhotoPage != null && !latestPhotoPage.isDataEmpty()) {
			photoKitWidget.enablePaging();
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		photoViewerKitEventBus.register(photoKitEventListener);
	}

	@Override
	public void onStop() {
		super.onStop();

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

		photoCollectionViewModel.trackListingLastItem(photoCollection);
	}

	private void setupViews() {
		toolbar.setTitle(getString(R.string.app_name));

		photoViewerKitEventBus = photoKitWidget.getEventBus();

		photoKitWidget.setPagingListener(listingPagingListener);

		photoListingAdapter = new PhotoListingAdapter();
		photoListingAdapter.registerListingItemType(photoListingView.new PhotoItemType(R.id.item_type_listing_photo));
		photoListingAdapter.registerListingItemType(new ProgressIndicatorType());

		photoGalleryAdapter = new PhotoGalleryAdapter();
		photoGalleryAdapter.registerListingItemType(new PhotoGalleryView.PhotoItemType(R.id.item_type_listing_photo));

		photoActionAdapter = new PhotoActionAdapter();

		photoListingView.setPhotoAdapter(photoListingAdapter);
		photoGalleryView.setPhotoAdapter(photoGalleryAdapter);
		photoOverlayView.setPhotoActionAdapter(photoActionAdapter);
		photoOverlayView.setPhotoActionEventListener(photoActionListener);

		Resources resources = getResources();
		final int nLayoutCol = 4;   //resources.getInteger(R.integer.photo_page_col);
		GridLayoutManager rcvLayoutMan = new GridLayoutManager(getContext(), nLayoutCol, GridLayoutManager.VERTICAL, false);
		rcvLayoutMan.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				return photoListingAdapter.isProgressIndicatorItem(position) ? rcvLayoutMan.getSpanCount() : 1;
			}
		});

		photoListingView.setLayoutManager(rcvLayoutMan);
		photoListingView.addItemDecoration(new SimpleDividerItemDec(null, StaggeredGridLayoutManager.VERTICAL, resources.getDimensionPixelSize(com.khoinguyen.photoviewerkit.R.dimen.photo_list_pager_item_offset)));
	}

	private PhotoActionView.PhotoActionEventListener photoActionListener = (actionId, photo) -> {
		switch (actionId) {
			case PhotoActionAdapter.TYPE_SHARE: {
				photoCollectionViewModel.sharePhoto(photo);
				break;
			}

			case PhotoActionAdapter.TYPE_SET_WALLPAPER: {
				photoCollectionViewModel.loadWallpaper(photo);
				break;
			}

			case PhotoActionAdapter.TYPE_DOWNLOAD: {
				photoCollectionViewModel.downloadPhoto(photo);
				break;
			}
		}
	};

	private IPhotoViewerKitWidget.PagingListener listingPagingListener = widget -> {
		photoCollectionViewModel.loadNextPhotoPage(photoCollection);
		photoListingAdapter.appendProgressIndicator();
		photoListingAdapter.notifyDataSetChanged();
	};

	private void appendPhotoAdapters(DataPage<PhotoDisplayInfo> latestPhotoPage) {
		if (latestPhotoPage.getStart() == 0) {
			photoListingAdapter.clear();
			photoGalleryAdapter.clear();
		}

		photoListingAdapter.append(latestPhotoPage.getData());
		photoGalleryAdapter.append(latestPhotoPage.getData());

		photoListingAdapter.notifyDataSetChanged();
		photoGalleryAdapter.notifyDataSetChanged();
	}

	private void showWallpaperChooser(PhotoDisplayInfo photoDetails) {
		File photoFile = photoDetails.getLocalFile();
		if (!photoFile.exists()) {
			showToast(getString(R.string.app_alert_please_retry));
			return;
		}

		Uri uriImg = Uri.fromFile(photoFile);
		Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setDataAndType(uriImg, "image/*");
		intent.putExtra("mimeType", "image/*");

		startActivity(Intent.createChooser(intent, getString(R.string.photo_actions_set_wp_chooser)));
	}

	public void updateDownloadProgress(PhotoCollectionViewModel.DownloadPhotoProgress progress) {
		NotificationCompat.Builder notificationBuilder = getDownloadNotificationBuilder()
			.setContentTitle(getString(R.string.download_notification_title, progress.photoDisplayInfo.getPhotoId()))
			.setContentText(getString(R.string.download_notification_downloading_message))
			.setOngoing(true)
			.setProgress(100, (int) (progress.progress * 100), false);

		notificationManager.notify(downloadNotificationsInfo.getId(progress.photoDisplayInfo), notificationBuilder.build());
	}

	private NotificationCompat.Builder getDownloadNotificationBuilder() {
		if (downloadNotificationBuilder == null) {
			downloadNotificationBuilder = new NotificationCompat.Builder(getContext())
				.setSmallIcon(R.drawable.ic_launcher);
		}

		return downloadNotificationBuilder;
	}

	public void showDownloadComplete(PhotoDisplayInfo photoDisplayInfo) {
		Intent resultIntent = new Intent(Intent.ACTION_VIEW);
		resultIntent.setDataAndType(photoDisplayInfo.getLocalFileUri(), "image/*");
		PendingIntent intentOpenExternal = PendingIntent.getActivity(getContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder notifBuilder = getDownloadNotificationBuilder()
			.setContentTitle(getString(R.string.download_notification_title, photoDisplayInfo.getPhotoId()))
			.setContentText(getString(R.string.download_notification_completed))
			.setOngoing(false)
			.setContentIntent(intentOpenExternal)
			.setProgress(0, 0, false);

		notificationManager.notify(downloadNotificationsInfo.getId(photoDisplayInfo), notifBuilder.build());
	}

	public void showDownloadError(PhotoCollectionViewModel.DownloadPhotoError error) {
		NotificationCompat.Builder notifBuilder = getDownloadNotificationBuilder()
			.setContentTitle(getString(R.string.download_notification_title, error.photoDisplayInfo.getPhotoId()))
			.setContentText(getString(R.string.download_notification_error, error.throwable.getMessage()))
			.setOngoing(false)
			.setProgress(0, 0, false);

		notificationManager.notify(downloadNotificationsInfo.getId(error.photoDisplayInfo), notifBuilder.build());
	}

	public void showSharingChooser(PhotoDisplayInfo photoDisplayInfo) {
		Intent i = new Intent();
		i.setAction(Intent.ACTION_SEND);
		i.putExtra(Intent.EXTRA_STREAM, photoDisplayInfo.getLocalFileUri());
		i.setType("image/*");

		startActivity(Intent.createChooser(i, getResources().getString(R.string.send_to)));
	}

	public void showToast(String message) {
		Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
	}

	private void updatePhotoLoadingProgressDialog(int progress) {
		if (photoLoadingProgressDialog == null || !photoLoadingProgressDialog.isShowing()) {
			photoLoadingProgressDialog = new PhotoDownloadProgressDialog(getContext());
			photoLoadingProgressDialog.setMessage(getString(R.string.photo_gallery_downloading_message));
			photoLoadingProgressDialog.setOnDismissListener(dialog -> photoCollectionViewModel.cancelPhotoLoadingTask());
			photoLoadingProgressDialog.show();
		}

		if (photoLoadingProgressDialog != null && photoLoadingProgressDialog.isShowing()) {
			photoLoadingProgressDialog.setProgress(progress);
			if (progress == 100) {
				photoLoadingProgressDialog.dismiss();
			}
		}
	}

	protected Object photoKitEventListener = new Object() {
		@com.khoinguyen.apptemplate.eventbus.Subscribe
		public void handleOnPhotoListingItemClick(OnPhotoListingItemClick event) {
			appBarLayout.setExpanded(false, false);
		}

		@com.khoinguyen.apptemplate.eventbus.Subscribe
		public void handleOnGalleryPhotoPageSelect(OnPhotoGalleryPhotoSelect event) {
			PhotoDetails photoDetails = photoCollectionViewModel.findPhoto(event.getPhotoDisplayInfo());
			analyticsCollection.trackGalleryPhotoScreenView(photoDetails);
		}

		@com.khoinguyen.apptemplate.eventbus.Subscribe
		public void handleOnShrinkTransitionEnd(OnShrinkTransitionEnd event) {
			analyticsCollection.trackListingScreenView();
		}
	};

	private class PhotoListingAdapter extends PartitionedListingAdapter<RecyclerListingViewHolder> {
		@Override
		protected List<ListingItem> createDataSet() {
			return new ArrayList<>();
		}

		public void append(List<PhotoDisplayInfo> lastPagePhotos) {
			if (lastPagePhotos == null) {
				return;
			}

			for (PhotoDisplayInfo displayInfo : lastPagePhotos) {
				dataSet.add(new ListingItem(displayInfo, R.id.item_type_listing_photo));
			}
		}

		void clear() {
			updateDataSet();
		}

		ListingItem getLastItem() {
			if (dataSet.isEmpty()) {
				return null;
			}

			return dataSet.get(dataSet.size() - 1);
		}

		void appendProgressIndicator() {
			ListingItem lastItem = getLastItem();
			if (lastItem != null && lastItem.getViewType() != R.id.item_type_listing_progress_indicator) {
				dataSet.add(new ListingItem(null, R.id.item_type_listing_progress_indicator));
			}
		}

		void removeProgressIndicator() {
			ListingItem lastItem = getLastItem();
			if (lastItem != null && lastItem.getViewType() == R.id.item_type_listing_progress_indicator) {
				dataSet.remove(lastItem);
			}
		}

		public boolean isProgressIndicatorItem(int position) {
			return getViewType(position) == R.id.item_type_listing_progress_indicator;
		}
	}

	private class PhotoGalleryAdapter extends PartitionedListingAdapter<BaseViewHolder> {
		@Override
		protected List<ListingItem> createDataSet() {
			return new ArrayList<>();
		}

		public void append(List<PhotoDisplayInfo> lastPagePhotos) {
			if (lastPagePhotos == null) {
				return;
			}

			for (PhotoDisplayInfo displayInfo : lastPagePhotos) {
				dataSet.add(new ListingItem(displayInfo, R.id.item_type_listing_photo));
			}
		}

		public void clear() {
			updateDataSet();
		}
	}

	private class ProgressIndicatorType extends ListingItemType<RecyclerListingViewHolder> {

		public ProgressIndicatorType() {
			super(R.id.item_type_listing_progress_indicator);
		}

		@Override
		public View createView(ViewGroup container) {
			return getLayoutInflater(container.getContext()).inflate(com.khoinguyen.photoviewerkit.R.layout.photokit_photo_listing_loading_indicator, container, false);
		}

		@Override
		public RecyclerListingViewHolder createViewHolder(View view) {
			return new RecyclerListingViewHolder(view);
		}
	}
}
