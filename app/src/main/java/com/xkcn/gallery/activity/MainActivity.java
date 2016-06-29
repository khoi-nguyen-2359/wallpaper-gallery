package com.xkcn.gallery.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.support.v7.widget.Toolbar;
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
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.event.PhotoCrawlingFinishedEvent;
import com.xkcn.gallery.event.SetWallpaperClicked;
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

  protected MainViewPresenter presenter;
  private SystemBarTintManager.SystemBarConfig kitkatSystemBarConfig;
  protected Dialog proDlg;

  L log = L.get(this);

  protected PhotoListingViewPresenter photoListingPresenter;

  protected IEventBus photoViewerKitEventBus;
  private PhotoListingAdapter photoListingAdapter;
  private PhotoGalleryAdapter photoGalleryAdapter;

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
    }
  };

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
  };

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
    presenter.checkToCrawlPhoto();
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
    presenter = new MainViewPresenter(photoDownloader, this, preferenceRepository);
    photoListingPresenter = new PhotoListingViewPresenter(photoListingUsecase, preferencesUsecase);
    photoListingPresenter.setView(this);

    SystemBarTintManager kitkatTintManager = new SystemBarTintManager(this);
    kitkatSystemBarConfig = kitkatTintManager.getConfig();

    photoListingAdapter = new PhotoListingAdapter();
    photoListingAdapter.registerListingItemType(new PhotoListingView.PhotoItemType(PhotoListingAdapter.TYPE_PHOTO));

    photoGalleryAdapter = new PhotoGalleryAdapter();
    photoGalleryAdapter.registerListingItemType(new PhotoGalleryView.PhotoItemType(PhotoGalleryAdapter.TYPE_PHOTO));

    photoActionAdapter = new PhotoActionAdapter();
    photoActionAdapter.registerListingItemType(new PhotoActionAdapter.ShareItemType(PhotoActionAdapter.TYPE_SHARE));
    photoActionAdapter.registerListingItemType(new PhotoActionAdapter.SetWallpaperItemType(PhotoActionAdapter.TYPE_SET_WALLPAPER));
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

  /***
   * event bus
   ***/

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEventMainThread(PhotoCrawlingFinishedEvent event) {
    photoListingPresenter.loadPhotoPage(0, PhotoListingViewPresenter.TYPE_LATEST);
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEventMainThread(SetWallpaperClicked event) {
    PhotoDetails photoDetails = event.getPhoto();
    presenter.loadWallpaperSetting(photoDetails);
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
