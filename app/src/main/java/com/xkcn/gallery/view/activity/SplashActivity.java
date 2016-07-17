package com.xkcn.gallery.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xkcn.gallery.presenter.SplashViewPresenter;
import com.xkcn.gallery.view.interfaces.SplashView;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class SplashActivity extends BaseActivity implements SplashView {
  private SplashViewPresenter presenter;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    startActivity(new Intent(this, MainActivityImpl.class));
    finish();
  }
}
