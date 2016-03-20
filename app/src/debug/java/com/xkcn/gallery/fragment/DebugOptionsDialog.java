package com.xkcn.gallery.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xkcn.gallery.R;
import com.xkcn.gallery.event.RefreshPhotoListingPager;
import com.xkcn.gallery.service.UpdateService;

import de.greenrobot.event.EventBus;

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
