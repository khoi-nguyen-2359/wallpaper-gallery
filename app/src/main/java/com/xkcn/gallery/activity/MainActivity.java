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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.khoinguyen.apptemplate.listing.BaseItemCreator;
import com.khoinguyen.apptemplate.listing.ItemPart;
import com.khoinguyen.apptemplate.listing.adapter.PartitionedListingAdapter;
import com.khoinguyen.apptemplate.eventbus.LightEventBus;
import com.khoinguyen.apptemplate.listing.util.RecyclerListingViewHolder;
import com.khoinguyen.photoviewerkit.event.OnPhotoListingItemClick;
import com.khoinguyen.photoviewerkit.data.PhotoDisplayInfo;
import com.khoinguyen.photoviewerkit.view.impl.PhotoGalleryView;
import com.khoinguyen.photoviewerkit.view.impl.PhotoListingView;
import com.khoinguyen.photoviewerkit.view.impl.PhotoViewerKitWidget;
import com.khoinguyen.ui.UiUtils;
import com.khoinguyen.util.log.L;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xkcn.gallery.R;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.event.PhotoCrawlingFinishedEvent;
import com.xkcn.gallery.event.SetWallpaperClicked;
import com.xkcn.gallery.presenter.MainViewPresenter;
import com.xkcn.gallery.presenter.PhotoListingViewPresenter;
import com.xkcn.gallery.service.UpdateService;
import com.xkcn.gallery.util.AndroidUtils;
import com.xkcn.gallery.view.AppViewType;
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

  @Bind(R.id.photokit_pager_photo_gallery)
  PhotoGalleryView photoGalleryView;

  protected MainViewPresenter presenter;
  private SystemBarTintManager.SystemBarConfig kitkatSystemBarConfig;
  protected Dialog proDlg;

  L log = L.get(this);

  protected PhotoListingViewPresenter photoListingPresenter;

  private List<PhotoDetails> allPhotos;

  protected LightEventBus photoViewerKitEventBus;

  private PartitionedListingAdapter<RecyclerListingViewHolder> photoListingAdapter = new PartitionedListingAdapter<RecyclerListingViewHolder>() {
    @Override
    protected List<ItemPart> createDataSet() {
      List<ItemPart> parts = new ArrayList<>();
      parts.add(new ItemPart("Caption", AppViewType.MainView_PhotoListing_CaptionItem.ordinal()));
      for (PhotoDetails photoDetails : allPhotos) {
        PhotoDisplayInfo photoDisplayInfo = new PhotoDisplayInfo();
        photoDisplayInfo.setPhotoId(photoDetails.getIdentifier()+"");
        photoDisplayInfo.setLowResUrl(photoDetails.getLowResUrl());
        photoDisplayInfo.setHighResUrl(photoDetails.getHighResUrl());

        ItemPart photoPart = new ItemPart(photoDisplayInfo, AppViewType.MainView_PhotoListing_PhotoItem.ordinal());
        parts.add(photoPart);
      }

      return parts;
    }
  };

  private PartitionedListingAdapter photoGalleryAdapter = new PartitionedListingAdapter() {
    @Override
    protected List<ItemPart> createDataSet() {
      List<ItemPart> parts = new ArrayList<>();
      for (PhotoDetails photoDetails : allPhotos) {
        PhotoDisplayInfo photoDisplayInfo = new PhotoDisplayInfo();
        photoDisplayInfo.setPhotoId(String.valueOf(photoDetails.getIdentifier()));
        photoDisplayInfo.setHighResUrl(photoDetails.getHighResUrl());
        photoDisplayInfo.setLowResUrl(photoDetails.getLowResUrl());

        ItemPart photoPart = new ItemPart(photoDisplayInfo, AppViewType.MainView_PhotoGallery_PhotoItem.ordinal());
        parts.add(photoPart);
      }
      return parts;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initData();
    initTemplateViews();
    initViews();
    photoListingPresenter.loadPhotoListPage(1, PhotoListingViewPresenter.TYPE_LATEST);
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
    photoListingView.setListingAdapter(photoListingAdapter);
    photoGalleryView.setListingAdapter(photoGalleryAdapter);
    photoViewerKitEventBus = photoKitWidget.getEventBus();
  }

  private void initData() {
    allPhotos = new ArrayList<>();

    presenter = new MainViewPresenter(photoDownloader, this, preferenceRepository);
    photoListingPresenter = new PhotoListingViewPresenter(photoListingUsecase, preferencesUsecase);
    photoListingPresenter.setView(this);

    SystemBarTintManager kitkatTintManager = new SystemBarTintManager(this);
    kitkatSystemBarConfig = kitkatTintManager.getConfig();

    PhotoListingView.PhotoListingViewCreator listingPhotoItemCreator = new PhotoListingView.PhotoListingViewCreator(AppViewType.MainView_PhotoListing_PhotoItem.ordinal());
    BaseItemCreator listingCaptionItemCreator = new ListingCaptionItemCreator(AppViewType.MainView_PhotoListing_CaptionItem.ordinal());
    photoListingAdapter.registerViewCreator(listingPhotoItemCreator);
    photoListingAdapter.registerViewCreator(listingCaptionItemCreator);

    BaseItemCreator photoGalleryItemCreator = new PhotoGalleryView.PhotoGalleryItemViewCreator(AppViewType.MainView_PhotoGallery_PhotoItem.ordinal());
    photoGalleryAdapter.registerViewCreator(photoGalleryItemCreator);
  }

  public static class ListingCaptionItemCreator extends BaseItemCreator<RecyclerListingViewHolder> {
    public ListingCaptionItemCreator(int viewType) {
      super(viewType);
    }

    @Override
    public View createView(ViewGroup container) {
      return new TextView(container.getContext());
    }

    @Override
    public RecyclerListingViewHolder createViewHolder(View view) {
      return new PhotoListingCaptionViewHolder(view);
    }
  }

  private static class PhotoListingCaptionViewHolder extends RecyclerListingViewHolder<String> {
    TextView tvCaption;

    public PhotoListingCaptionViewHolder(View view) {
      super(view);

      tvCaption = (TextView) view;
    }

    @Override
    public void bind(String s) {
      tvCaption.setText(s);
    }
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
        ((View) photoListingView).setPadding(0, 0, 0, insets.getSystemWindowInsetBottom());

        return insets;
      }
    });

    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
      toolbarContainerLayout.setPadding(0, kitkatSystemBarConfig.getPixelInsetTop(false), 0, 0);
      ((View) photoListingView).setPadding(0, 0, 0, kitkatSystemBarConfig.getPixelInsetBottom());
    }
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_hotest) {
      photoListingPresenter.loadPhotoListPage(1, PhotoListingViewPresenter.TYPE_HOTEST);
    } else if (id == R.id.nav_latest) {
      photoListingPresenter.loadPhotoListPage(1, PhotoListingViewPresenter.TYPE_LATEST);
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
  public void appendPhotoData(int page, List<PhotoDetails> photos) {
    log.d("page=%d, photos=%d", page, photos != null ? photos.size() : 0);
    if (page == 1) {
      allPhotos.clear();
    }

    allPhotos.addAll(photos);
    photoListingAdapter.notifyDataSetChanged();
    photoGalleryAdapter.notifyDataSetChanged();
  }

  /***
   * event bus
   ***/

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEventMainThread(PhotoCrawlingFinishedEvent event) {
    photoListingPresenter.loadPhotoListPage(1, PhotoListingViewPresenter.TYPE_LATEST);
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
    public void handleOnPhotoListingItemClick(OnPhotoListingItemClick event) {
      appBarLayout.setExpanded(false, false);
    }
  };
}
