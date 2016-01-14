package com.xkcn.crawler.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xkcn.crawler.R;
import com.xkcn.crawler.service.UpdateService;

/**
 * Created by khoinguyen on 1/14/16.
 */
public class DebugDialog extends DialogFragment {
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
        return root;
    }
}
