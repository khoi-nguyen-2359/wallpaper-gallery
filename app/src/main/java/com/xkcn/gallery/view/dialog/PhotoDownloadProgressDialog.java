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
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xkcn.gallery.R;

public class PhotoDownloadProgressDialog extends Dialog {

	private ProgressBar mProgress;

	private int mMax = 100;
	private int mProgressVal;
	private boolean mIndeterminate = false;

	private boolean mHasStarted;
	private TextView tvMessage;
	private String message;

	public PhotoDownloadProgressDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.dialog_custom_progress);
		mProgress = (ProgressBar) findViewById(R.id.progress);
		tvMessage = (TextView) findViewById(R.id.tv_message);

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

		setIndeterminate(mIndeterminate);
		super.onCreate(savedInstanceState);
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
}
