package com.xkcn.gallery.view.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.khoinguyen.apptemplate.eventbus.IEventBus;
import com.khoinguyen.apptemplate.listing.item.BaseViewHolder;
import com.khoinguyen.apptemplate.listing.item.ListingItem;
import com.khoinguyen.apptemplate.listing.adapter.PartitionedListingAdapter;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoGalleryPhotoSelect;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoListingItemClick;
import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.photoviewerkit.impl.event.OnShrinkTransitionEnd;
import com.khoinguyen.photoviewerkit.impl.view.PhotoActionView;
import com.khoinguyen.photoviewerkit.impl.view.PhotoGalleryView;
import com.khoinguyen.photoviewerkit.impl.view.PhotoListingView;
import com.khoinguyen.photoviewerkit.impl.view.PhotoOverlayView;
import com.khoinguyen.photoviewerkit.impl.view.PhotoViewerKitWidget;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoViewerKitWidget;
import com.khoinguyen.photoviewerkit.util.BottomLoadingIndicatorAdapter;
import com.khoinguyen.ui.UiUtils;
import com.khoinguyen.util.log.L;
import com.xkcn.gallery.R;
import com.xkcn.gallery.adapter.PhotoActionAdapter;
import com.xkcn.gallery.data.PhotoDownloadNotificationsInfo;
import com.xkcn.gallery.data.model.PhotoCategory;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.event.PhotoCrawlingFinishedEvent;
import com.xkcn.gallery.presenter.MainViewPresenter;
import com.xkcn.gallery.presenter.PhotoListingViewPresenter;
import com.xkcn.gallery.service.UpdateService;
import com.xkcn.gallery.view.dialog.PhotoDownloadProgressDialog;
import com.xkcn.gallery.view.interfaces.MainView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class MainActivity extends BaseActivity
    implements NavigationView.OnNavigationItemSelectedListener, MainView {
  @Bind(R.id.main_coordinator_layout)
  CoordinatorLayout mainCoordinatorLayout;
  @Bind(R.id.nav_view)
  NavigationView viewNavigation;
  @Bind(R.id.app_bar)
  AppBarLayout appBarLayout;
  @Bind(R.id.toolbar)
  Toolbar toolbar;
  @Bind(R.id.toolbar_container)
  FrameLayout toolbarContainerLayout;
  @Bind(R.id.photokit_widget)
  PhotoViewerKitWidget photoKitWidget;
  @Bind(R.id.photokit_photo_listing)
  PhotoListingView photoListingView;
  @Bind(R.id.photokit_photo_gallery)
  PhotoGalleryView photoGalleryView;
  @Bind(R.id.photokit_photo_overlay)
  PhotoOverlayView photoOverlayView;

  protected MainViewPresenter mainViewPresenter;
  protected PhotoDownloadProgressDialog progressDialog;

  L log = L.get(this);

  protected PhotoListingViewPresenter photoListingPresenter;

  protected IEventBus photoViewerKitEventBus;
  private PhotoListingAdapter photoListingAdapter;
  private PhotoGalleryAdapter photoGalleryAdapter;

  private PhotoDownloadNotificationsInfo downloadNotificationsInfo = new PhotoDownloadNotificationsInfo();
  private NotificationCompat.Builder downloadNotificationBuilder;

  private PhotoActionAdapter photoActionAdapter;

  private IPhotoViewerKitWidget.PagingListener listingPagingListener = new IPhotoViewerKitWidget.PagingListener() {
    @Override
    public void onPagingNext(IPhotoViewerKitWidget widget) {
      photoListingPresenter.loadNextPhotoPage();
    }
  };

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
    PhotoDetails photoDetails = photoListingPresenter.findPhoto(photo.getPhotoId());
    if (photoDetails == null) {
      return;
    }

    mainViewPresenter.downloadPhoto(photoDetails);
  }

  @Override
  public void showSharingChooser(PhotoDetails photoDetails) {
    Intent i = new Intent();
    i.setAction(Intent.ACTION_SEND);
    i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFileManager.getPhotoFile(photoDetails)));
    i.setType("image/*");

    startActivity(Intent.createChooser(i, getResources().getString(R.string.send_to)));
  }

  private void sharePhoto(PhotoDisplayInfo photo) {
    PhotoDetails photoDetails = photoListingPresenter.findPhoto(photo.getPhotoId());
    if (photoDetails == null) {
      return;
    }

    mainViewPresenter.sharePhoto(photoDetails);
  }

  private void setWallpaper(PhotoDisplayInfo photo) {
    PhotoDetails photoDetails = photoListingPresenter.findPhoto(photo.getPhotoId());
    if (photoDetails == null) {
      return;
    }

    mainViewPresenter.loadWallpaperSetting(photoDetails);
  }

  private class PhotoListingAdapter extends BottomLoadingIndicatorAdapter {
    public static final int TYPE_PHOTO = 2;

    @Override
    protected List<ListingItem> createDataSet() {
      List<ListingItem> listingItems = new ArrayList<>();
      List<PhotoDisplayInfo> allPhotoDisplayInfos = photoListingPresenter.getAllPages().getAllDisplayInfos();
      for (PhotoDisplayInfo displayInfo : allPhotoDisplayInfos) {
        listingItems.add(new ListingItem(displayInfo, getListingItemType(TYPE_PHOTO)));
      }

      return listingItems;
    }
  };

  private class PhotoGalleryAdapter extends PartitionedListingAdapter<BaseViewHolder> {
    public static final int TYPE_PHOTO = 1;

    @Override
    protected List<ListingItem> createDataSet() {
      List<ListingItem> listingItems = new ArrayList<>();
      List<PhotoDisplayInfo> allPhotoDisplayInfos = photoListingPresenter.getAllPages().getAllDisplayInfos();
      for (PhotoDisplayInfo displayInfo : allPhotoDisplayInfos) {
        ListingItem photoListingItem = new ListingItem(displayInfo, getListingItemType(TYPE_PHOTO));
        listingItems.add(photoListingItem);
      }

      return listingItems;
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initTemplateViews();
    initData();
    initViews();
    analyticsCollection.trackListingScreenView();
    photoListingPresenter.loadPhotoPage(0, PhotoCategory.LATEST);
  }

  @Override
  protected void onStart() {
    super.onStart();
    mainViewPresenter.checkToCrawlPhoto();
    EventBus.getDefault().register(this);
    photoViewerKitEventBus.register(photoKitEventListener);
  }

  @Override
  protected void onStop() {
    EventBus.getDefault().unregister(this);
    photoViewerKitEventBus.unregister(photoKitEventListener);
    super.onStop();
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else if (!photoKitWidget.handleBackPress()) {
      super.onBackPressed();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    trackListingLength();
  }

  private void trackListingLength() {
    analyticsCollection.trackListingEndScroll(photoListingPresenter.getCurrentListingType().getName(), photoListingPresenter.getAllPages().getNextStart() - 1);
  }

  private void initViews() {
    photoListingView.setPhotoAdapter(photoListingAdapter);
    photoGalleryView.setPhotoAdapter(photoGalleryAdapter);
    photoOverlayView.setPhotoActionAdapter(photoActionAdapter);
    photoActionAdapter.updateDataSet();
    photoActionAdapter.notifyDataSetChanged();
    photoOverlayView.setPhotoActionEventListener(photoActionListener);
    photoViewerKitEventBus = photoKitWidget.getEventBus();

    photoKitWidget.setPagingListener(listingPagingListener);
  }

  private void initData() {
    mainViewPresenter = new MainViewPresenter(this);
    getApplicationComponent().inject(mainViewPresenter);

    photoListingPresenter = new PhotoListingViewPresenter(getApplicationComponent());
    photoListingPresenter.setView(this);

    photoListingAdapter = new PhotoListingAdapter();
    photoListingAdapter.registerListingItemType(photoListingView.new PhotoItemType(PhotoListingAdapter.TYPE_PHOTO));

    photoGalleryAdapter = new PhotoGalleryAdapter();
    photoGalleryAdapter.registerListingItemType(new PhotoGalleryView.PhotoItemType(PhotoGalleryAdapter.TYPE_PHOTO));

    photoActionAdapter = new PhotoActionAdapter();
    photoActionAdapter.registerListingItemType(new PhotoActionAdapter.ShareItemType());
    photoActionAdapter.registerListingItemType(new PhotoActionAdapter.SetWallpaperItemType());
    photoActionAdapter.registerListingItemType(new PhotoActionAdapter.DownloadItemType(photoFileManager));
  }

  protected void initTemplateViews() {
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    toolbar.setTitle(getString(R.string.app_name));
    setSupportActionBar(toolbar);

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    viewNavigation.setNavigationItemSelectedListener(this);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_hotest) {
      trackListingLength();
      photoListingPresenter.loadPhotoPage(0, PhotoCategory.HOSTEST);
    } else if (id == R.id.nav_latest) {
      trackListingLength();
      photoListingPresenter.loadPhotoPage(0, PhotoCategory.LATEST);
    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  @Override
  public void startActionUpdate() {
    UpdateService.startActionUpdate(this);
  }

  @Override
  public void onPagingLoaded() {
    photoListingAdapter.updateDataSet();
    photoListingAdapter.notifyDataSetChanged();
    photoGalleryAdapter.updateDataSet();
    photoGalleryAdapter.notifyDataSetChanged();
  }

  @Override
  public void enablePaging() {
    photoKitWidget.enablePaging();
  }

  @Override
  public void updateDownloadProgress(PhotoDetails photoDetails, Float progress) {
    NotificationManager notificationManager = getNotificationManager();
    NotificationCompat.Builder notificationBuilder = getDownloadNotificationBuilder()
        .setContentTitle(getString(R.string.download_notification_title, photoDetails.getIdentifierAsString()))
        .setContentText(getString(R.string.download_notification_downloading_message))
        .setOngoing(true)
        .setProgress(100, (int) (progress * 100), false);

    notificationManager.notify(downloadNotificationsInfo.getId(photoDetails), notificationBuilder.build());
  }

  @Override
  public void showDownloadComplete(PhotoDetails photoDetails) {
    NotificationManager notificationMan = getNotificationManager();

    Intent resultIntent = new Intent(Intent.ACTION_VIEW);
    resultIntent.setDataAndType(Uri.fromFile(photoFileManager.getPhotoFile(photoDetails)), "image/*");
    PendingIntent intentOpenExternal = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Builder notifBuilder = getDownloadNotificationBuilder()
        .setContentTitle(getString(R.string.download_notification_title, photoDetails.getIdentifierAsString()))
        .setContentText(getString(R.string.download_notification_completed))
        .setOngoing(false)
        .setContentIntent(intentOpenExternal)
        .setProgress(0, 0, false);

    notificationMan.notify(downloadNotificationsInfo.getId(photoDetails), notifBuilder.build());
  }

  private NotificationCompat.Builder getDownloadNotificationBuilder() {
    if (downloadNotificationBuilder == null) {
      downloadNotificationBuilder = new NotificationCompat.Builder(this)
          .setSmallIcon(R.drawable.ic_launcher);
    }

    return downloadNotificationBuilder;
  }

  @Override
  public void showDownloadError(PhotoDetails photoDetails, String message) {
    NotificationManager notificationMan = getNotificationManager();
    NotificationCompat.Builder notifBuilder = getDownloadNotificationBuilder()
        .setContentTitle(getString(R.string.download_notification_title, photoDetails.getIdentifierAsString()))
        .setContentText(getString(R.string.download_notification_error, message))
        .setOngoing(false)
        .setProgress(0, 0, false);

    notificationMan.notify(downloadNotificationsInfo.getId(photoDetails), notifBuilder.build());
  }

  /***
   * event bus
   ***/

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEventMainThread(PhotoCrawlingFinishedEvent event) {
    photoListingPresenter.loadPhotoPage(0, PhotoCategory.LATEST);
  }

  /***
   * end - event bus
   ***/

  @Override
  public void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
  }

  @Override
  public void updateProgressLoading(int progress) {
    if (progressDialog == null || !progressDialog.isShowing()) {
      return;
    }

    progressDialog.setProgress(progress);
  }

  @Override
  public void showProgressLoading(int messageResId) {
    progressDialog = new PhotoDownloadProgressDialog(this);
    progressDialog.setMessage(getString(messageResId));
    progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
      @Override
      public void onDismiss(DialogInterface dialog) {
        mainViewPresenter.cancelBlockingTask();
      }
    });
    progressDialog.show();
  }

  @Override
  public void hideLoading() {
    progressDialog.setOnDismissListener(null);
    UiUtils.dismissDlg(progressDialog);
  }

  @Override
  public void showWallpaperChooser(File photoFile) {
    Uri uriImg = Uri.fromFile(photoFile);
    Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    intent.setDataAndType(uriImg, "image/*");
    intent.putExtra("mimeType", "image/*");

    startActivity(Intent.createChooser(intent, getString(R.string.photo_actions_set_wp_chooser)));
  }

  protected Object photoKitEventListener = new Object() {
    @com.khoinguyen.apptemplate.eventbus.Subscribe
    public void handleOnPhotoListingItemClick(OnPhotoListingItemClick event) {
      appBarLayout.setExpanded(false, false);
      analyticsCollection.trackGalleryScreenView();
    }

    @com.khoinguyen.apptemplate.eventbus.Subscribe
    public void handleOnPhotoGalleryPhotoSelect(OnPhotoGalleryPhotoSelect event) {
      photoActionAdapter.updateCurrentActivePhoto(photoListingPresenter.findPhoto(event.getPhotoDisplayInfo().getPhotoId()));
      photoActionAdapter.notifyDataSetChanged();
    }

    @com.khoinguyen.apptemplate.eventbus.Subscribe
    public void handleOnShrinkTransitionEnd(OnShrinkTransitionEnd event) {
      analyticsCollection.trackListingScreenView();
    }
  };
}
