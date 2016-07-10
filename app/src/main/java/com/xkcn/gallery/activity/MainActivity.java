package com.xkcn.gallery.activity;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.khoinguyen.apptemplate.eventbus.IEventBus;
import com.khoinguyen.apptemplate.listing.item.BaseViewHolder;
import com.khoinguyen.apptemplate.listing.item.ListingItem;
import com.khoinguyen.apptemplate.listing.adapter.PartitionedListingAdapter;
import com.khoinguyen.apptemplate.listing.item.RecyclerListingViewHolder;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoListingItemActivate;
import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.photoviewerkit.impl.view.PhotoActionView;
import com.khoinguyen.photoviewerkit.impl.view.PhotoGalleryView;
import com.khoinguyen.photoviewerkit.impl.view.PhotoListingView;
import com.khoinguyen.photoviewerkit.impl.view.PhotoOverlayView;
import com.khoinguyen.photoviewerkit.impl.view.PhotoViewerKitWidget;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoViewerKitWidget;
import com.khoinguyen.ui.UiUtils;
import com.khoinguyen.util.log.L;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xkcn.gallery.R;
import com.xkcn.gallery.adapter.PhotoActionAdapter;
import com.xkcn.gallery.data.PhotoDownloadNotificationsInfo;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.event.PhotoCrawlingFinishedEvent;
import com.xkcn.gallery.presenter.MainViewPresenter;
import com.xkcn.gallery.presenter.PhotoListingViewPresenter;
import com.xkcn.gallery.service.UpdateService;
import com.xkcn.gallery.util.AndroidUtils;
import com.xkcn.gallery.view.MainView;

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
  private SystemBarTintManager.SystemBarConfig kitkatSystemBarConfig;
  protected Dialog proDlg;

  L log = L.get(this);

  protected PhotoListingViewPresenter photoListingPresenter;

  protected IEventBus photoViewerKitEventBus;
  private PhotoListingAdapter photoListingAdapter;
  private PhotoGalleryAdapter photoGalleryAdapter;

  private PhotoDownloadNotificationsInfo downloadNotificationsInfo = new PhotoDownloadNotificationsInfo();
  private NotificationCompat.Builder downloadNotificationBuilder;

  private IPhotoViewerKitWidget.PagingListener listingPagingListener = new IPhotoViewerKitWidget.PagingListener() {
    @Override
    public void onPagingNext(IPhotoViewerKitWidget widget) {
      photoListingPresenter.loadNextPhotoPage();
    }
  };

  private PhotoActionView.PhotoActionEventListener photoActionListener = new PhotoActionView.PhotoActionEventListener() {
    @Override
    public void onPhotoActionSelect(int actionId, PhotoDisplayInfo photo) {
      L.get().d("onPhotoActionSelect %d %s", actionId, photo.getPhotoId());
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

  private void sharePhoto(PhotoDisplayInfo photo) {
    PhotoDetails photoDetails = photoListingPresenter.findPhoto(photo.getPhotoId());
    if (photoDetails == null) {
      return;
    }

    String sendText = null;
    Resources resources = getResources();
    if (TextUtils.isEmpty(photoDetails.getPermalinkMeta())) {
      sendText = resources.getString(R.string.send_to_trailing_text, photoDetails.getPermalink());
    } else {
      Spanned spanned = Html.fromHtml(photoDetails.getPermalinkMeta());
      sendText = resources.getString(R.string.send_to_trailing_text, spanned.toString() + " " + photoDetails.getPermalink());
    }

    Intent i = new Intent();
    i.setAction(Intent.ACTION_SEND);
    i.putExtra(Intent.EXTRA_TEXT, sendText);
    i.setType("text/plain");

    startActivity(Intent.createChooser(i, resources.getString(R.string.send_to)));
  }

  private void setWallpaper(PhotoDisplayInfo photo) {
    PhotoDetails photoDetails = photoListingPresenter.findPhoto(photo.getPhotoId());
    if (photoDetails == null) {
      return;
    }

    mainViewPresenter.loadWallpaperSetting(photoDetails);
  }

  private PhotoActionAdapter photoActionAdapter;

  private class PhotoListingAdapter extends PartitionedListingAdapter<RecyclerListingViewHolder> {
    public static final int TYPE_PHOTO = 2;

    @Override
    protected List<ListingItem> createDataSet() {
      List<ListingItem> listingItems = new ArrayList<>();
      for (PhotoDetails photoDetails : photoListingPresenter.getAllPhotos()) {
        PhotoDisplayInfo photoDisplayInfo = PhotoDisplayInfo.create(photoDetails.getIdentifierAsString(), photoDetails.getHighResUrl(), photoDetails.getLowResUrl(), 0);
        photoDisplayInfo.setDescription(photoDetails.getPermalinkMeta());
        listingItems.add(new ListingItem(photoDisplayInfo, getListingItemType(TYPE_PHOTO)));
      }

      return listingItems;
    }
  };

  private class PhotoGalleryAdapter extends PartitionedListingAdapter<BaseViewHolder> {
    public static final int TYPE_PHOTO = 1;

    @Override
    protected List<ListingItem> createDataSet() {
      List<ListingItem> listingItems = new ArrayList<>();
      for (PhotoDetails photoDetails : photoListingPresenter.getAllPhotos()) {
        PhotoDisplayInfo photoDisplayInfo = PhotoDisplayInfo.create(photoDetails.getIdentifierAsString(), photoDetails.getHighResUrl(), photoDetails.getLowResUrl(), 0);
        photoDisplayInfo.setDescription(photoDetails.getPermalinkMeta());
        ListingItem photoListingItem = new ListingItem(photoDisplayInfo, getListingItemType(TYPE_PHOTO));
        listingItems.add(photoListingItem);
      }

      return listingItems;
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initData();
    initTemplateViews();
    initViews();
    photoListingPresenter.loadPhotoPage(0, PhotoListingViewPresenter.TYPE_LATEST);
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

    photoListingPresenter = new PhotoListingViewPresenter();
    photoListingPresenter.setView(this);
    getApplicationComponent().inject(photoListingPresenter);

    SystemBarTintManager kitkatTintManager = new SystemBarTintManager(this);
    kitkatSystemBarConfig = kitkatTintManager.getConfig();

    photoListingAdapter = new PhotoListingAdapter();
    photoListingAdapter.registerListingItemType(new PhotoListingView.PhotoItemType(PhotoListingAdapter.TYPE_PHOTO));

    photoGalleryAdapter = new PhotoGalleryAdapter();
    photoGalleryAdapter.registerListingItemType(new PhotoGalleryView.PhotoItemType(PhotoGalleryAdapter.TYPE_PHOTO));

    photoActionAdapter = new PhotoActionAdapter();
    photoActionAdapter.registerListingItemType(new PhotoActionAdapter.ShareItemType());
    photoActionAdapter.registerListingItemType(new PhotoActionAdapter.SetWallpaperItemType());
    photoActionAdapter.registerListingItemType(new PhotoActionAdapter.DownloadItemType());
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

    applyWindowInsets();
  }

  private void applyWindowInsets() {
    ViewCompat.setOnApplyWindowInsetsListener(toolbarContainerLayout, new OnApplyWindowInsetsListener() {
      @Override
      public WindowInsetsCompat onApplyWindowInsets(View v, final WindowInsetsCompat insets) {
        toolbarContainerLayout.setPadding(0, insets.getSystemWindowInsetTop(), 0, 0);
        photoListingView.setPadding(0, 0, 0, insets.getSystemWindowInsetBottom());

        return insets;
      }
    });

    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
      toolbarContainerLayout.setPadding(0, kitkatSystemBarConfig.getPixelInsetTop(false), 0, 0);
      photoListingView.setPadding(0, 0, 0, kitkatSystemBarConfig.getPixelInsetBottom());
    }
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_hotest) {
      photoListingPresenter.loadPhotoPage(0, PhotoListingViewPresenter.TYPE_HOTEST);
    } else if (id == R.id.nav_latest) {
      photoListingPresenter.loadPhotoPage(0, PhotoListingViewPresenter.TYPE_LATEST);
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
    resultIntent.setDataAndType(Uri.fromFile(photoFileManager.getDownloadFile(photoDetails)), "image/*");
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
    photoListingPresenter.loadPhotoPage(0, PhotoListingViewPresenter.TYPE_LATEST);
  }

  /***
   * end - event bus
   ***/

  @Override
  public void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
  }

  @Override
  public void showLoading() {
    proDlg = ProgressDialog.show(this, null, getString(R.string.msg_wait_a_moment), true);
  }

  @Override
  public void hideLoading() {
    UiUtils.dismissDlg(proDlg);
  }

  @Override
  public void showWallpaperChooser(File photoFile) {
    Uri uri = Uri.fromFile(photoFile);
    AndroidUtils.startSetWallpaperChooser(this, uri);
  }

  protected Object photoKitEventListener = new Object() {
    @com.khoinguyen.apptemplate.eventbus.Subscribe
    public void handleOnPhotoListingItemClick(OnPhotoListingItemActivate event) {
      appBarLayout.setExpanded(false, false);
    }
  };
}
