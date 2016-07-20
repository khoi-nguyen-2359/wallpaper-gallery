package com.xkcn.gallery.view.activity;

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.xkcn.gallery.BaseApp;
import com.xkcn.gallery.analytics.AnalyticsCollection;
import com.xkcn.gallery.data.repo.PhotoDetailsRepository;
import com.xkcn.gallery.data.repo.PhotoTagRepository;
import com.xkcn.gallery.data.repo.PreferenceRepository;
import com.xkcn.gallery.di.component.ApplicationComponent;
import com.xkcn.gallery.imageloader.PhotoFileManager;
import com.xkcn.gallery.usecase.PhotoListingUsecase;
import com.xkcn.gallery.usecase.PreferencesUsecase;

import javax.inject.Inject;

/**
 * Created by khoinguyen on 2/1/16.
 */
public abstract class BaseActivity extends AppCompatActivity {
  @Inject
  PhotoDetailsRepository photoDetailsRepository;
  @Inject
  PreferenceRepository preferenceRepository;
  @Inject
  PhotoTagRepository photoTagRepository;
  @Inject
  PhotoFileManager photoFileManager;
  @Inject
  PhotoListingUsecase photoListingUsecase;
  @Inject
  PreferencesUsecase preferencesUsecase;

  @Inject
  AnalyticsCollection analyticsCollection;

  private NotificationManager notificationManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getApplicationComponent().inject(this);
  }

  protected ApplicationComponent getApplicationComponent() {
    return ((BaseApp) getApplication()).getApplicationComponent();
  }

  public NotificationManager getNotificationManager() {
    if (notificationManager == null) {
      notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    return notificationManager;
  }
}
