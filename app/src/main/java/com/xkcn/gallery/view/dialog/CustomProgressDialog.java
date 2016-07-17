/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xkcn.gallery.view.dialog;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.khoinguyen.util.log.L;
import com.xkcn.gallery.R;

public class CustomProgressDialog extends Dialog {

  private ProgressBar mProgress;

  private int mMax;
  private int mProgressVal;
  private boolean mIndeterminate;

  private boolean mHasStarted;
  private TextView tvMessage;
  private ImageButton btCancel;
  private String message;

  private OnCancelClickListener cancelListener;

  public CustomProgressDialog(Context context) {
    super(context, R.style.HeightAdjustableDialog);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Context mContext = getContext();
    setContentView(R.layout.dialog_custom_progress);
    mProgress = (ProgressBar) findViewById(R.id.progress);
    tvMessage = (TextView) findViewById(R.id.tv_message);
    btCancel = (ImageButton) findViewById(R.id.bt_cancel);

    if (mMax > 0) {
      setMax(mMax);
    }
    if (mProgressVal > 0) {
      setProgress(mProgressVal);
    }

    if (message != null) {
      tvMessage.setText(message);
    } else {
      tvMessage.setVisibility(View.GONE);
    }

    btCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (cancelListener != null) {
          cancelListener.onCancelClick(CustomProgressDialog.this);
        }
      }
    });

    setIndeterminate(mIndeterminate);
    super.onCreate(savedInstanceState);
  }

  public void setOnCancelClickListener(OnCancelClickListener listener) {
    cancelListener = listener;
  }

  @Override
  public void onStart() {
    super.onStart();
    mHasStarted = true;
  }

  @Override
  protected void onStop() {
    super.onStop();
    mHasStarted = false;
  }

  public void setProgress(int value) {
    if (mHasStarted) {
      ObjectAnimator progressAnim = ObjectAnimator.ofInt(mProgress, "progress", value);
      progressAnim.setInterpolator(new AccelerateDecelerateInterpolator());
      progressAnim.setDuration(200);
      progressAnim.start();
    } else {
      mProgressVal = value;
    }
  }

  public void setMax(int max) {
    if (mProgress != null) {
      mProgress.setMax(max);
    } else {
      mMax = max;
    }
  }

  public void setIndeterminate(boolean indeterminate) {
    if (mProgress != null) {
      mProgress.setIndeterminate(indeterminate);
    } else {
      mIndeterminate = indeterminate;
    }
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public interface OnCancelClickListener {
    void onCancelClick(CustomProgressDialog dialog);
  }
}
