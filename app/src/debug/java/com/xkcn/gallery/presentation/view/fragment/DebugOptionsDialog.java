package com.xkcn.gallery.presentation.view.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.khoinguyen.util.log.L;
import com.xkcn.gallery.R;
import com.xkcn.gallery.event.RefreshPhotoListingPager;
import com.xkcn.gallery.imageloader.PhotoFileManager;
import com.xkcn.gallery.service.UpdateService;
import com.xkcn.gallery.presentation.view.activity.MainActivityImpl;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by khoinguyen on 1/14/16.
 */
public class DebugOptionsDialog extends DialogFragment {
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.dialog_debug, container, false);
		root.findViewById(R.id.bt_force_crawl).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				UpdateService.startActionUpdate(getContext());
			}
		});

		root.findViewById(R.id.bt_refresh_photo_listing_pager).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				EventBus.getDefault().post(new RefreshPhotoListingPager());
			}
		});

		root.findViewById(R.id.bt_clear_fresco_cache).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ImagePipeline imgPipeline = Fresco.getImagePipeline();
				imgPipeline.clearCaches();
			}
		});

		root.findViewById(R.id.bt_delete_downloaded_photos).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Activity activity = getActivity();
				if (activity instanceof MainActivityImpl) {
					PhotoFileManager photoFileManager = ((MainActivityImpl) activity).getPhotoFileManager();
					File[] files = photoFileManager.getPhotoDir().listFiles();
					if (files.length > 0) {
						for (File f : files) {
							L.get().d("delete %s, %s", f.getName(), f.delete());
						}
					}
				}
			}
		});

		return root;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setTitle("Debug Options");

		return dialog;
	}
}
